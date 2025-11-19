package controller;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import model.Hidrometro;

/**
 * Fachada Singleton (Façade + Singleton) para o SHA multithread.
 * Esta classe NÃO altera o comportamento original do sistema; ela apenas
 * encapsula e reutiliza a mesma lógica que já existe em {@link Orquestradora},
 * expondo uma API simples para clientes (CLI, GUI, etc.).
 */
public class SHAFacade {

    // ------------------------- SINGLETON -------------------------

    private static SHAFacade instancia;

    /**
     * Construtor privado para garantir uma única instância.
     */
    private SHAFacade() {
        // Nenhuma inicialização pesada aqui para não mudar o comportamento;
        // a lógica é criada sob demanda nos métodos da fachada.
    }

    /**
     * Ponto de acesso global (thread-safe) à instância única da fachada.
     */
    public static synchronized SHAFacade getInstancia() {
        if (instancia == null) {
            instancia = new SHAFacade();
        }
        return instancia;
    }

    // ------------------------- ESTADO INTERNO -------------------------

    /** Número máximo de simuladores concorrentes (mesmo valor da Orquestradora). */
    private static final int MAX_SIMULADORES = 5;

    /** Mapa thread-safe para armazenar os simuladores ativos, indexados por ID. */
    private final Map<Integer, Hidrometro> simuladores = new ConcurrentHashMap<>();

    /** Mapa para armazenar as threads associadas a cada simulador. */
    private final Map<Integer, Thread> threads = new ConcurrentHashMap<>();

    /** Contador de IDs de simulador. */
    private final AtomicInteger contadorId = new AtomicInteger(1);

    /** Intervalo de tempo de simulação em milissegundos (caso necessário). */
    private volatile int intervaloSimulacaoMs = 1000; // valor padrão neutro

    // ------------------------- MÉTODOS FAÇADE -------------------------

    /**
     * 1) Define parâmetros globais de configuração do simulador SHA.
     * No momento, apenas o intervalo de tempo é armazenado. Este valor pode
     * ser utilizado pelos clientes para controlar o tempo entre leituras,
     * sem alterar o comportamento original das threads do Hidrometro.
     */
    public synchronized void configSimuladorSHA(int intervaloTempoMs) {
        if (intervaloTempoMs <= 0) {
            throw new IllegalArgumentException("Intervalo deve ser positivo");
        }
        this.intervaloSimulacaoMs = intervaloTempoMs;
    }

    /**
     * 2) Cria uma nova instância de SHA utilizando a mesma lógica da Orquestradora.
     *
     * @param usarConfiguracaoArquivo true para usar arquivos configuracao.txt/1..5,
     *                                false para usar valores personalizados.
     * @param idArquivoConfiguracao   se usar configuracaoX.txt, informar o X (1..5);
     *                                se 0, usa configuracao.txt padrão.
     * @param larguraEntrada          usado somente quando usarConfiguracaoArquivo for false
     * @param larguraSaida            usado somente quando usarConfiguracaoArquivo for false
     * @param regulagemTorneira       usado somente quando usarConfiguracaoArquivo for false
     * @param velocidadeAgua          usado somente quando usarConfiguracaoArquivo for false
     * @return id interno do simulador criado
     */
    public synchronized int criaSHA(boolean usarConfiguracaoArquivo,
                                    int idArquivoConfiguracao,
                                    double larguraEntrada,
                                    double larguraSaida,
                                    int regulagemTorneira,
                                    double velocidadeAgua) throws Exception {
        if (simuladores.size() >= MAX_SIMULADORES) {
            throw new IllegalStateException("Limite máximo de simuladores atingido (" + MAX_SIMULADORES + ")");
        }

        int id = contadorId.getAndIncrement();
        if (id > MAX_SIMULADORES) {
            contadorId.decrementAndGet();
            throw new IllegalStateException("Todos os IDs de simuladores foram utilizados.");
        }

        Hidrometro hidrometro;

        if (usarConfiguracaoArquivo) {
            // Se idArquivoConfiguracao == 0, usa configuracao.txt
            // Senão, usa configuracaoX.txt (idArquivoConfiguracao)
            String nomeArquivo;
            if (idArquivoConfiguracao <= 0) {
                nomeArquivo = "/configuracao.txt";
            } else {
                nomeArquivo = "/configuracao" + idArquivoConfiguracao + ".txt";
            }
            hidrometro = carregarConfiguracaoDeArquivo(id, nomeArquivo);
        } else {
            // Configuração personalizada, mesma lógica da Orquestradora
            hidrometro = new Hidrometro(id, regulagemTorneira, larguraEntrada,
                    larguraSaida, velocidadeAgua);
        }

        Thread thread = new Thread(hidrometro, "Hidrometro-" + id);
        thread.start();

        simuladores.put(id, hidrometro);
        threads.put(id, thread);

        return id;
    }

    /**
     * 3) Finaliza uma instância específica do SHA.
     */
    public synchronized void finalizaSHA(int idSimulador) {
        Hidrometro hidrometro = simuladores.get(idSimulador);
        Thread thread = threads.get(idSimulador);

        if (hidrometro == null || thread == null) {
            throw new IllegalArgumentException("Simulador com ID " + idSimulador + " não encontrado.");
        }

        hidrometro.parar();
        thread.interrupt();

        simuladores.remove(idSimulador);
        threads.remove(idSimulador);
    }

    /**
     * 4) Altera o valor da vazão (velocidade da água na entrada) de um SHA.
     */
    public void modificaVazaoSHA(int idSimulador, double novaVazao) {
        Hidrometro hidrometro = simuladores.get(idSimulador);
        if (hidrometro == null) {
            throw new IllegalArgumentException("Simulador com ID " + idSimulador + " não encontrado.");
        }
        hidrometro.setVelocidadeAguaEntrada(novaVazao);
    }

    /**
     * 5) Habilita ou desabilita a geração de imagens de um SHA.
     * Como o código original não possui um flag explícito para isso, esta
     * implementação assume que o Hidrometro tem (ou pode ter) um método
     * setGerarImagem(boolean). Caso contrário, este método pode ficar como
     * no-op ou apenas registrar a intenção.
     */
    public void habilitaGeracaoImagemSHA(int idSimulador, boolean habilitar) {
        Hidrometro hidrometro = simuladores.get(idSimulador);
        if (hidrometro == null) {
            throw new IllegalArgumentException("Simulador com ID " + idSimulador + " não encontrado.");
        }

        try {
            // Se existir um método setGerarImagem(boolean), usamos via reflexão
            hidrometro.getClass()
                    .getMethod("setGerarImagem", boolean.class)
                    .invoke(hidrometro, habilitar);
        } catch (NoSuchMethodException e) {
            // Se o método não existir, apenas ignoramos para não alterar o código original
        } catch (Exception e) {
            throw new RuntimeException("Erro ao alterar geração de imagem para o simulador " + idSimulador, e);
        }
    }

    // ------------------------- MÉTODOS AUXILIARES -------------------------

    /**
     * Carrega configuração de um arquivo específico, reutilizando a lógica da Orquestradora.
     */
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
            scanner.nextLine(); // descrição
            double larguraCanoEntrada = Double.parseDouble(scanner.nextLine());
            scanner.nextLine();
            double larguraCanoSaida = Double.parseDouble(scanner.nextLine());
            scanner.nextLine();
            int regulagemDaTorneira = Integer.parseInt(scanner.nextLine());
            scanner.nextLine();
            double velocidadeDaAgua = Double.parseDouble(scanner.nextLine());

            return new Hidrometro(id, regulagemDaTorneira, larguraCanoEntrada,
                    larguraCanoSaida, velocidadeDaAgua);
        }
    }

    // Métodos auxiliares opcionais para um cliente consultar estado -----------------

    public int getIntervaloSimulacaoMs() {
        return intervaloSimulacaoMs;
    }

    public Map<Integer, Hidrometro> getSimuladoresAtivos() {
        return simuladores;
    }
}

