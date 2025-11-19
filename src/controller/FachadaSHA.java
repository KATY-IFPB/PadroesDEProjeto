package controller;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import model.Hidrometro;
import view.Display;
import view.DisplayNoOp;

/**
 * Fachada Singleton para controlar o SHA (simulador de hidrômetros) sem alterar
 * o funcionamento original das classes do proprietário. A fachada gerencia até
 * 5 simuladores concorrentes e expõe operações de configuração, criação,
 * finalização, modificação de parâmetros e habilitação de geração de imagens.
 */
public class FachadaSHA {

    private static FachadaSHA instancia;

    private static final int MAX_SIMULADORES = 5;

    private final Map<Integer, Hidrometro> simuladores = new ConcurrentHashMap<>();
    private final Map<Integer, Thread> threads = new ConcurrentHashMap<>();
    private final Map<Integer, AtomicBoolean> runningFlags = new ConcurrentHashMap<>();
    private final Map<Integer, Display> originalDisplays = new ConcurrentHashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(1);

    // Intervalo padrão em milissegundos usado pelo loop de simulação da fachada
    private volatile int defaultIntervalMillis = 1000;

    private FachadaSHA() {
    }

    public static synchronized FachadaSHA getInstancia() {
        if (instancia == null) {
            instancia = new FachadaSHA();
        }
        return instancia;
    }

    /** Configura parâmetros globais do simulador (por enquanto: intervalo de simulação em ms) */
    public void configSimuladorSHA(int intervaloMillis) {
        if (intervaloMillis <= 0) throw new IllegalArgumentException("intervalo deve ser > 0");
        this.defaultIntervalMillis = intervaloMillis;
        System.out.println("Intervalo de simulação ajustado para " + intervaloMillis + " ms");
    }

    /** Cria um simulador usando o arquivo padrão `src/configuracao.txt`. Retorna o id do simulador criado. */
    public int criaSHA() throws Exception {
        return criaSHAFromArquivo("/configuracao.txt");
    }

    /** Cria um simulador usando o arquivo `src/configuracao{fileId}.txt`. */
    public int criaSHAFromFile(int fileId) throws Exception {
        return criaSHAFromArquivo("/configuracao" + fileId + ".txt");
    }

    /** Cria um simulador com parâmetros personalizados. Retorna o id do simulador criado. */
    public int criaSHACustom(int torneiraRegulagem, double larguraEntrada, double larguraSaida, double velocidade) throws Exception {
        int id = allocateId();
        Hidrometro h = new Hidrometro(id, torneiraRegulagem, larguraEntrada, larguraSaida, velocidade);
        startHidrometroWorker(id, h, defaultIntervalMillis);
        System.out.println("Simulador " + id + " criado (personalizado).");
        return id;
    }

    private int criaSHAFromArquivo(String nomeArquivo) throws Exception {
        if (simuladores.size() >= MAX_SIMULADORES) {
            throw new Exception("Limite máximo de simuladores atingido (" + MAX_SIMULADORES + ").");
        }

        int id = allocateId();
        Hidrometro h = carregarConfiguracaoDeArquivo(id, nomeArquivo);
        startHidrometroWorker(id, h, defaultIntervalMillis);
        System.out.println("Simulador " + id + " criado a partir do arquivo: " + nomeArquivo);
        return id;
    }

    private int allocateId() throws Exception {
        int id = nextId.getAndIncrement();
        if (id > MAX_SIMULADORES) {
            nextId.decrementAndGet();
            throw new Exception("Todos os IDs de simuladores foram utilizados.");
        }
        return id;
    }

    private void startHidrometroWorker(int id, Hidrometro h, int intervalMillis) {
        AtomicBoolean running = new AtomicBoolean(true);
        runningFlags.put(id, running);
        simuladores.put(id, h);

        Thread t = new Thread(() -> {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // registra consumo pelo período em segundos
                    h.registrarConsumo(intervalMillis / 1000.0);
                    //noinspection BusyWait
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Throwable ex) {
                    System.out.println("Erro no simulador " + id + ": " + ex.getMessage());
                    break;
                }
            }
        }, "Fachada-Hidrometro-" + id);

        threads.put(id, t);
        t.start();
    }

    /** Finaliza um simulador específico. */
    public void finalizaSHA(int id) {
        Hidrometro h = simuladores.get(id);
        Thread t = threads.get(id);
        AtomicBoolean running = runningFlags.get(id);

        if (h == null || t == null || running == null) {
            System.out.println("Simulador " + id + " não encontrado.");
            return;
        }

        // sinaliza parada
        running.set(false);
        h.parar(); // chama o método original

        // interrompe a thread de trabalho
        t.interrupt();

        // restaura display, se alterado
        restoreOriginalDisplayIfAny(id, h);

        simuladores.remove(id);
        threads.remove(id);
        runningFlags.remove(id);

        System.out.println("Simulador " + id + " finalizado.");
    }

    /** Modifica a vazão (na prática: velocidade da água) de um simulador já criado. */
    public void modificaVazaoSHA(int id, double novaVelocidade) {
        Hidrometro h = simuladores.get(id);
        if (h == null) {
            System.out.println("Simulador " + id + " não encontrado.");
            return;
        }
        h.setVelocidadeAguaEntrada(novaVelocidade);
        System.out.println("Simulador " + id + " - velocidade ajustada para: " + novaVelocidade);
    }

    /** Habilita ou desabilita a geração de imagens de um simulador através da substituição do Display por um NoOp. */
    public void habilitaGeracaoImagemSHA(int id, boolean habilitar) {
        Hidrometro h = simuladores.get(id);
        if (h == null) {
            System.out.println("Simulador " + id + " não encontrado.");
            return;
        }

        try {
            Field f = Hidrometro.class.getDeclaredField("display");
            f.setAccessible(true);
            Display atual = (Display) f.get(h);

            if (!habilitar) {
                // desabilitar: guarda a referência original e coloca NoOp
                if (!originalDisplays.containsKey(id)) {
                    originalDisplays.put(id, atual);
                    DisplayNoOp noop = new DisplayNoOp(0, id);
                    f.set(h, noop);
                    System.out.println("Geração de imagens desabilitada para o simulador " + id);
                } else {
                    System.out.println("Geração de imagens já está desabilitada para o simulador " + id);
                }
            } else {
                // habilitar: restaura original se existir
                if (originalDisplays.containsKey(id)) {
                    Display original = originalDisplays.remove(id);
                    f.set(h, original);
                    System.out.println("Geração de imagens habilitada para o simulador " + id);
                } else {
                    System.out.println("Geração de imagens já estava habilitada para o simulador " + id);
                }
            }

        } catch (NoSuchFieldException nsfe) {
            System.out.println("Não foi possível acessar o display do Hidrometro: " + nsfe.getMessage());
        } catch (IllegalAccessException iae) {
            System.out.println("Erro ao modificar o display do Hidrometro: " + iae.getMessage());
        }
    }

    /** Lista os simuladores ativos (retorna string formatada). */
    public String listarSimuladores() {
        if (simuladores.isEmpty()) return "Nenhum simulador ativo.";

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== SIMULADORES ATIVOS (Fachada) ===\n");
        simuladores.forEach((id, h) -> {
            Thread t = threads.get(id);
            sb.append(String.format("ID: %d | Volume: %d m³ | Regulagem: %d%% | Status: %s\n",
                    id,
                    (int) h.getVolumeAcumulado(),
                    h.getTorneiraRegulagem(),
                    (t != null && t.isAlive()) ? "Ativo" : "Parado"));
        });
        return sb.toString();
    }

    private void restoreOriginalDisplayIfAny(int id, Hidrometro h) {
        if (originalDisplays.containsKey(id)) {
            try {
                Field f = Hidrometro.class.getDeclaredField("display");
                f.setAccessible(true);
                f.set(h, originalDisplays.remove(id));
            } catch (Exception e) {
                // não crítico
            }
        }
    }

    // ==================== helpers para carregar configs (compatível com Orquestradora) ====================
    private Hidrometro carregarConfiguracaoDeArquivo(int id, String nomeArquivo) throws Exception {
        InputStream inputStream = Orquestradora.class.getResourceAsStream(nomeArquivo);

        if (inputStream == null) {
            String caminhoLocal = "src" + nomeArquivo;
            try {
                inputStream = new java.io.FileInputStream(caminhoLocal);
            } catch (java.io.FileNotFoundException e) {
                throw new Exception("Arquivo de configuração não encontrado: " + nomeArquivo + " nem em " + caminhoLocal);
            }
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.nextLine(); // Pula descrição
            double larguraCanoEntrada = Double.parseDouble(scanner.nextLine());
            scanner.nextLine(); // Pula descrição
            double larguraCanoSaida = Double.parseDouble(scanner.nextLine());
            scanner.nextLine(); // Pula descrição
            int regulagemDaTorneira = Integer.parseInt(scanner.nextLine());
            scanner.nextLine(); // Pula descrição
            double velocidadeDaAgua = Double.parseDouble(scanner.nextLine());

            System.out.println("Configuração carregada do arquivo " + nomeArquivo + ":");
            System.out.println("- Largura entrada: " + larguraCanoEntrada + " mm");
            System.out.println("- Largura saída: " + larguraCanoSaida + " mm");
            System.out.println("- Regulagem torneira: " + regulagemDaTorneira + "%");
            System.out.println("- Velocidade água: " + velocidadeDaAgua + " m³/s");

            return new Hidrometro(id, regulagemDaTorneira, larguraCanoEntrada,
                    larguraCanoSaida, velocidadeDaAgua);
        }
    }

    /** Obtém a referência de um hidrômetro para leitura de dados (usado pelo sistema de monitoramento). */
    public Hidrometro obterHidrometro(int id) {
        return simuladores.get(id);
    }

    /** Obtém o volume acumulado de um simulador específico. */
    public double obterVolumeAcumulado(int id) {
        Hidrometro h = simuladores.get(id);
        return h != null ? h.getVolumeAcumulado() : 0.0;
    }

}
