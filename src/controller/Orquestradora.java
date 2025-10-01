package controller;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import model.Hidrometro;
import view.Messages;

/**
 * Orquestradora multithread para gerenciar até 5 simuladores de hidrômetro concorrentes.
 *
 * Esta classe mantém o código original "puro" e adiciona capacidade multithread,
 * permitindo que múltiplos simuladores executem independentemente com comportamentos
 * de entrada, medição e saída completamente diferentes.
 */
public class Orquestradora {

    /** Número máximo de simuladores concorrentes */
    private static final int MAX_SIMULADORES = 5;

    /** Mapa thread-safe para armazenar os simuladores ativos */
    private static final Map<Integer, Hidrometro> simuladores = new ConcurrentHashMap<>();

    /** Mapa para armazenar as threads dos simuladores */
    private static final Map<Integer, Thread> threads = new ConcurrentHashMap<>();

    /** Contador para IDs únicos dos simuladores */
    private static final AtomicInteger contadorId = new AtomicInteger(1);

    // Constantes de comando do menu (expandido para multithread)
    private static final int CRIAR_SIMULADOR = 0;
    private static final int SET_LARGURA_CANO_ENTRADA = 1;
    private static final int SET_LARGURA_CANO_SAIDA = 2;
    private static final int SET_REGULAGEM_DA_TORNEIRA = 3;
    private static final int SET_VELOCIDADE_DA_AGUA = 4;
    private static final int VER_MEDICAO_DO_HIDROMETRO = 5;
    private static final int LISTAR_SIMULADORES = 6;
    private static final int PARAR_SIMULADOR = 7;
    private static final int SAIR = 9;

    /**
     * Método principal que inicia a aplicação multithread.
     */
    public static void main(String[] args) {
        System.out.println("=== SIMULADOR MULTITHREAD DE HIDRÔMETROS ===");
        System.out.println("Capacidade: até " + MAX_SIMULADORES + " simuladores concorrentes");
        exibirMenu();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            int comando = sc.nextInt();

            switch (comando) {
                case CRIAR_SIMULADOR:
                    criarSimulador(sc);
                    break;

                case SET_LARGURA_CANO_ENTRADA:
                    modificarLarguraCanoEntrada(sc);
                    break;

                case SET_LARGURA_CANO_SAIDA:
                    modificarLarguraCanoSaida(sc);
                    break;

                case SET_REGULAGEM_DA_TORNEIRA:
                    modificarRegulagemTorneira(sc);
                    break;

                case SET_VELOCIDADE_DA_AGUA:
                    modificarVelocidadeAgua(sc);
                    break;

                case VER_MEDICAO_DO_HIDROMETRO:
                    verMedicao(sc);
                    break;

                case LISTAR_SIMULADORES:
                    listarSimuladores();
                    break;

                case PARAR_SIMULADOR:
                    pararSimulador(sc);
                    break;

                case SAIR:
                    encerrarTodos();
                    System.out.println("Sistema encerrado.");
                    return;

                default:
                    System.out.println("Comando inválido!");
                    break;
            }

            exibirMenu();
        }

        sc.close();
    }

    /**
     * Exibe o menu de comandos disponíveis.
     */
    private static void exibirMenu() {
        System.out.println("\n=== MENU DE COMANDOS ===");
        System.out.println("0 - Criar novo simulador");
        System.out.println("1 - Modificar largura do cano de entrada");
        System.out.println("2 - Modificar largura do cano de saída");
        System.out.println("3 - Modificar regulagem da torneira");
        System.out.println("4 - Modificar velocidade da água");
        System.out.println("5 - Ver medição do hidrômetro");
        System.out.println("6 - Listar simuladores ativos");
        System.out.println("7 - Parar simulador");
        System.out.println("9 - Sair");
        System.out.print("Digite o comando: ");
    }

    /**
     * Cria um novo simulador com configurações do arquivo ou personalizadas.
     */
    private static void criarSimulador(Scanner sc) {
        if (simuladores.size() >= MAX_SIMULADORES) {
            System.out.println("Limite máximo de simuladores atingido (" + MAX_SIMULADORES + ").");
            return;
        }

        int id = contadorId.getAndIncrement();
        if (id > MAX_SIMULADORES) {
            contadorId.decrementAndGet();
            System.out.println("Todos os IDs de simuladores foram utilizados.");
            return;
        }

        System.out.println("Escolha o tipo de configuração:");
        System.out.println("1 - Usar arquivo de configuração padrão (configuracao.txt)");
        System.out.println("2 - Usar arquivo específico (configuracao" + id + ".txt)");
        System.out.println("3 - Configuração personalizada");
        System.out.print("Opção: ");

        int opcao = sc.nextInt();

        try {
            Hidrometro hidrometro;

            if (opcao == 1) {
                hidrometro = criarSimuladorPadrao(id);
            } else if (opcao == 2) {
                hidrometro = criarSimuladorEspecifico(id);
            } else {
                hidrometro = criarSimuladorPersonalizado(sc, id);
            }

            Thread thread = new Thread(hidrometro, "Hidrometro-" + id);
            thread.start();

            simuladores.put(id, hidrometro);
            threads.put(id, thread);

            System.out.println("Simulador " + id + " criado e iniciado com sucesso!");
            if (opcao == 2) {
                System.out.println("Usando configurações do arquivo: configuracao" + id + ".txt");
            }
            System.out.println("Arquivo de saída: saida/leitura_do_hidrometro_" + id + ".jpg");

        } catch (Exception e) {
            System.out.println("Erro ao criar simulador: " + e.getMessage());
            contadorId.decrementAndGet(); // Reverte o contador em caso de erro
        }
    }

    /**
     * Cria simulador com configurações do arquivo padrão.
     */
    private static Hidrometro criarSimuladorPadrao(int id) throws Exception {
        return carregarConfiguracaoDeArquivo(id, "/configuracao.txt");
    }

    /**
     * Cria simulador com configurações de arquivo específico.
     */
    private static Hidrometro criarSimuladorEspecifico(int id) throws Exception {
        return carregarConfiguracaoDeArquivo(id, "/configuracao" + id + ".txt");
    }

    /**
     * Carrega configuração de um arquivo específico.
     */
    private static Hidrometro carregarConfiguracaoDeArquivo(int id, String nomeArquivo) throws Exception {
        // Tenta carregar como recurso primeiro, depois como arquivo local
        InputStream inputStream = Orquestradora.class.getResourceAsStream(nomeArquivo);

        if (inputStream == null) {
            // Se não encontrar como recurso, tenta carregar do diretório src/
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

    /**
     * Cria simulador com configurações personalizadas.
     */
    private static Hidrometro criarSimuladorPersonalizado(Scanner sc, int id) {
        System.out.print("Largura do cano de entrada (mm): ");
        double larguraCanoEntrada = sc.nextDouble();

        System.out.print("Largura do cano de saída (mm): ");
        double larguraCanoSaida = sc.nextDouble();

        System.out.print("Regulagem da torneira (0-100): ");
        int regulagemDaTorneira = sc.nextInt();

        System.out.print("Velocidade da água (m³/s): ");
        double velocidadeDaAgua = sc.nextDouble();

        return new Hidrometro(id, regulagemDaTorneira, larguraCanoEntrada,
                            larguraCanoSaida, velocidadeDaAgua);
    }

    /**
     * Modifica a largura do cano de entrada de um simulador específico.
     */
    private static void modificarLarguraCanoEntrada(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        System.out.print("Nova largura do cano de entrada (mm): ");
        double novoValor = sc.nextDouble();

        hidrometro.setLarguraCanoEntrada(novoValor);
        System.out.println("Largura do cano de entrada do simulador " + id + " atualizada!");
    }

    /**
     * Modifica a largura do cano de saída de um simulador específico.
     */
    private static void modificarLarguraCanoSaida(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        System.out.print("Nova largura do cano de saída (mm): ");
        double novoValor = sc.nextDouble();

        hidrometro.setLarguraCanoSaida(novoValor);
        System.out.println("Largura do cano de saída do simulador " + id + " atualizada!");
    }

    /**
     * Modifica a regulagem da torneira de um simulador específico.
     */
    private static void modificarRegulagemTorneira(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        System.out.print("Nova regulagem da torneira (0-100): ");
        int novoValor = sc.nextInt();

        hidrometro.setTorneiraRegulagem(novoValor);
        System.out.println("Regulagem da torneira do simulador " + id + " atualizada!");
    }

    /**
     * Modifica a velocidade da água de um simulador específico.
     */
    private static void modificarVelocidadeAgua(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        System.out.print("Nova velocidade da água (m³/s): ");
        double novoValor = sc.nextDouble();

        hidrometro.setVelocidadeAguaEntrada(novoValor);
        System.out.println("Velocidade da água do simulador " + id + " atualizada!");
    }

    /**
     * Exibe a medição atual de um simulador específico.
     */
    private static void verMedicao(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        int volume = (int) hidrometro.getVolumeAcumulado();
        System.out.println("Simulador " + id + " - Volume medido: " + volume + " m³");
    }

    /**
     * Lista todos os simuladores ativos.
     */
    private static void listarSimuladores() {
        if (simuladores.isEmpty()) {
            System.out.println("Nenhum simulador ativo.");
            return;
        }

        System.out.println("\n=== SIMULADORES ATIVOS ===");
        simuladores.forEach((id, hidrometro) -> {
            System.out.printf("ID: %d | Volume: %d m³ | Regulagem: %d%% | Status: %s%n",
                id,
                (int) hidrometro.getVolumeAcumulado(),
                hidrometro.getTorneiraRegulagem(),
                threads.get(id).isAlive() ? "Ativo" : "Parado"
            );
        });
    }

    /**
     * Para um simulador específico.
     */
    private static void pararSimulador(Scanner sc) {
        int id = selecionarSimulador(sc);
        if (id == -1) return;

        Hidrometro hidrometro = simuladores.get(id);
        Thread thread = threads.get(id);

        hidrometro.parar();
        thread.interrupt();

        simuladores.remove(id);
        threads.remove(id);

        System.out.println("Simulador " + id + " parado e removido.");
    }

    /**
     * Seleciona um simulador ativo pelo ID.
     */
    private static int selecionarSimulador(Scanner sc) {
        if (simuladores.isEmpty()) {
            System.out.println("Nenhum simulador ativo.");
            return -1;
        }

        listarSimuladores();
        System.out.print("Digite o ID do simulador: ");
        int id = sc.nextInt();

        if (!simuladores.containsKey(id)) {
            System.out.println("Simulador com ID " + id + " não encontrado.");
            return -1;
        }

        return id;
    }

    /**
     * Encerra todos os simuladores ativos.
     */
    private static void encerrarTodos() {
        System.out.println("Encerrando todos os simuladores...");

        simuladores.values().forEach(Hidrometro::parar);
        threads.values().forEach(Thread::interrupt);

        simuladores.clear();
        threads.clear();

        System.out.println("Todos os simuladores foram encerrados.");
    }
}
