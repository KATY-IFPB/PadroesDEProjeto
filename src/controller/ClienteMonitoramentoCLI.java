package controller;

import model.Alerta;
import model.ContaAgua;
import model.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente CLI para operação do Painel de Monitoramento de Hidrômetros.
 * Demonstra a utilização completa da FachadaMonitoramento.
 */
public class ClienteMonitoramentoCLI {

    private static final FachadaMonitoramento fachada = FachadaMonitoramento.getInstancia();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        exibirBanner();

        boolean executando = true;
        while (executando) {
            try {
                exibirMenu();
                int opcao = lerInteiro("\nEscolha uma opcao: ");

                switch (opcao) {
                    case 1 -> menuUsuarios();
                    case 2 -> menuContas();
                    case 3 -> menuHidrometros();
                    case 4 -> menuMonitoramento();
                    case 5 -> menuAlertas();
                    case 6 -> exibirEstatisticas();
                    case 0 -> {
                        fachada.encerrarSistema();
                        executando = false;
                    }
                    default -> System.out.println("Opcao invalida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }

        System.out.println("\nAte logo!\n");
        scanner.close();
    }

    private static void exibirBanner() {
        System.out.println("\n=======================================================");
        System.out.println("                                                       ");
        System.out.println("    PAINEL DE MONITORAMENTO DE HIDROMETROS - CAGEPA   ");
        System.out.println("              Sistema de Gestao de Consumo             ");
        System.out.println("                                                       ");
        System.out.println("=======================================================\n");
    }

    private static void exibirMenu() {
        System.out.println("\n==================== MENU PRINCIPAL ===================");
        System.out.println("  1 - Gestao de Usuarios                               ");
        System.out.println("  2 - Gestao de Contas de Agua                         ");
        System.out.println("  3 - Gestao de Hidrometros (SHA)                      ");
        System.out.println("  4 - Monitoramento de Consumo                         ");
        System.out.println("  5 - Sistema de Alertas                               ");
        System.out.println("  6 - Exibir Estatisticas                              ");
        System.out.println("  0 - Sair                                             ");
        System.out.println("=======================================================");
    }

    // ==================== MENU USUARIOS ====================

    private static void menuUsuarios() {
        System.out.println("\n=============== GESTAO DE USUARIOS ================");
        System.out.println("  1 - Criar Usuario                                ");
        System.out.println("  2 - Buscar Usuario                               ");
        System.out.println("  3 - Atualizar Usuario                            ");
        System.out.println("  4 - Remover Usuario                              ");
        System.out.println("  5 - Listar Todos os Usuarios                     ");
        System.out.println("  0 - Voltar                                       ");
        System.out.println("===================================================");

        int opcao = lerInteiro("\nEscolha: ");

        switch (opcao) {
            case 1 -> criarUsuario();
            case 2 -> buscarUsuario();
            case 3 -> atualizarUsuario();
            case 4 -> removerUsuario();
            case 5 -> listarUsuarios();
            case 0 -> {}
            default -> System.out.println("Opcao invalida!");
        }
    }

    private static void criarUsuario() {
        System.out.println("\nCRIAR NOVO USUARIO");
        String cpf = lerString("CPF: ");
        String nome = lerString("Nome: ");
        String email = lerString("Email: ");
        String telefone = lerString("Telefone: ");
        String endereco = lerString("Endereco: ");

        try {
            Usuario usuario = fachada.criarUsuario(cpf, nome, email, telefone, endereco);
            System.out.println("Usuario criado: " + usuario);
        } catch (Exception e) {
            System.err.println("Erro ao criar usuario: " + e.getMessage());
        }
    }

    private static void buscarUsuario() {
        String cpf = lerString("\nCPF do usuario: ");
        Usuario usuario = fachada.buscarUsuario(cpf);

        if (usuario != null) {
            System.out.println("\nUsuario encontrado:");
            System.out.println(usuario);

            // Mostra contas associadas
            List<ContaAgua> contas = fachada.listarContasPorUsuario(cpf);
            if (!contas.isEmpty()) {
                System.out.println("\nContas associadas:");
                contas.forEach(System.out::println);
            }
        } else {
            System.out.println("Usuario nao encontrado.");
        }
    }

    private static void atualizarUsuario() {
        String cpf = lerString("\nCPF do usuario: ");
        Usuario usuario = fachada.buscarUsuario(cpf);

        if (usuario == null) {
            System.out.println("Usuario nao encontrado.");
            return;
        }

        System.out.println("Dados atuais: " + usuario);
        System.out.println("(deixe em branco para manter o valor atual)");

        String nome = lerStringOpcional("Novo nome: ");
        String email = lerStringOpcional("Novo email: ");
        String telefone = lerStringOpcional("Novo telefone: ");
        String endereco = lerStringOpcional("Novo endereco: ");

        try {
            fachada.atualizarUsuario(cpf, nome, email, telefone, endereco);
            System.out.println("Usuario atualizado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void removerUsuario() {
        String cpf = lerString("\nCPF do usuario: ");

        if (confirmar("Confirma remocao? (s/n): ")) {
            try {
                if (fachada.removerUsuario(cpf)) {
                    System.out.println("Usuario removido com sucesso!");
                } else {
                    System.out.println("Usuario nao encontrado.");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = fachada.listarUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("\nNenhum usuario cadastrado.");
        } else {
            System.out.println("\nUSUARIOS CADASTRADOS (" + usuarios.size() + "):");
            System.out.println("-".repeat(80));
            usuarios.forEach(System.out::println);
        }
    }

    // ==================== MENU CONTAS ====================

    private static void menuContas() {
        System.out.println("\n=============== GESTAO DE CONTAS ==================");
        System.out.println("  1 - Criar Conta                                  ");
        System.out.println("  2 - Buscar Conta                                 ");
        System.out.println("  3 - Remover Conta                                ");
        System.out.println("  4 - Vincular Hidrometro a Conta                  ");
        System.out.println("  5 - Desvincular Hidrometro da Conta              ");
        System.out.println("  6 - Configurar Limite de Consumo                 ");
        System.out.println("  7 - Listar Todas as Contas                       ");
        System.out.println("  0 - Voltar                                       ");
        System.out.println("===================================================");

        int opcao = lerInteiro("\nEscolha: ");

        switch (opcao) {
            case 1 -> criarConta();
            case 2 -> buscarConta();
            case 3 -> removerConta();
            case 4 -> vincularSHA();
            case 5 -> desvincularSHA();
            case 6 -> configurarLimite();
            case 7 -> listarContas();
            case 0 -> {}
            default -> System.out.println("Opcao invalida!");
        }
    }

    private static void criarConta() {
        System.out.println("\nCRIAR NOVA CONTA");
        String numeroConta = lerString("Numero da conta: ");
        String cpfUsuario = lerString("CPF do usuario: ");

        try {
            ContaAgua conta = fachada.criarConta(numeroConta, cpfUsuario);
            System.out.println("Conta criada: " + conta);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void buscarConta() {
        String numeroConta = lerString("\nNumero da conta: ");
        ContaAgua conta = fachada.buscarConta(numeroConta);

        if (conta != null) {
            System.out.println("\nConta encontrada:");
            System.out.println(conta);

            double consumo = fachada.obterConsumoAtualConta(numeroConta);
            System.out.printf("Consumo atual: %.2f m3%n", consumo);

            if (conta.getLimiteConsumo() > 0) {
                double percentual = (consumo / conta.getLimiteConsumo()) * 100;
                System.out.printf("Utilizacao: %.1f%% do limite%n", percentual);
            }
        } else {
            System.out.println("Conta nao encontrada.");
        }
    }

    private static void removerConta() {
        String numeroConta = lerString("\nNumero da conta: ");

        if (confirmar("Confirma remocao? (s/n): ")) {
            if (fachada.removerConta(numeroConta)) {
                System.out.println("Conta removida com sucesso!");
            } else {
                System.out.println("Conta nao encontrada.");
            }
        }
    }

    private static void vincularSHA() {
        String numeroConta = lerString("\nNumero da conta: ");
        int idSHA = lerInteiro("ID do hidrometro (SHA): ");

        try {
            fachada.vincularSHAConta(numeroConta, idSHA);
            System.out.println("Hidrometro vinculado a conta!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void desvincularSHA() {
        String numeroConta = lerString("\nNumero da conta: ");
        int idSHA = lerInteiro("ID do hidrometro (SHA): ");

        try {
            fachada.desvincularSHAConta(numeroConta, idSHA);
            System.out.println("Hidrometro desvinculado da conta!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void configurarLimite() {
        String numeroConta = lerString("\nNumero da conta: ");
        double limite = lerDouble("Limite de consumo (m3): ");

        try {
            fachada.configurarLimiteConsumo(numeroConta, limite);
            System.out.println("Limite configurado!");

            boolean alertaEmail = confirmar("Habilitar alerta por email? (s/n): ");
            fachada.habilitarAlertaEmail(numeroConta, alertaEmail);

            boolean alertaCon = confirmar("Habilitar alerta para concessionaria? (s/n): ");
            fachada.habilitarAlertaConcessionaria(numeroConta, alertaCon);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void listarContas() {
        List<ContaAgua> contas = fachada.listarContas();

        if (contas.isEmpty()) {
            System.out.println("\nNenhuma conta cadastrada.");
        } else {
            System.out.println("\nCONTAS CADASTRADAS (" + contas.size() + "):");
            System.out.println("-".repeat(80));
            for (ContaAgua conta : contas) {
                System.out.println(conta);
                double consumo = fachada.obterConsumoAtualConta(conta.getNumeroConta());
                System.out.printf("   Consumo: %.2f m3%n", consumo);
            }
        }
    }

    // ==================== MENU HIDROMETROS ====================

    private static void menuHidrometros() {
        System.out.println("\n============= GESTAO DE HIDROMETROS ===============");
        System.out.println("  1 - Criar SHA (padrao)                           ");
        System.out.println("  2 - Criar SHA (de arquivo)                       ");
        System.out.println("  3 - Criar SHA (customizado)                      ");
        System.out.println("  4 - Finalizar SHA                                ");
        System.out.println("  5 - Modificar Vazao                              ");
        System.out.println("  6 - Habilitar/Desabilitar Geracao de Imagens    ");
        System.out.println("  7 - Configurar Intervalo de Simulacao            ");
        System.out.println("  8 - Listar SHAs Ativos                           ");
        System.out.println("  0 - Voltar                                       ");
        System.out.println("===================================================");

        int opcao = lerInteiro("\nEscolha: ");

        switch (opcao) {
            case 1 -> criarSHAPadrao();
            case 2 -> criarSHAArquivo();
            case 3 -> criarSHACustom();
            case 4 -> finalizarSHA();
            case 5 -> modificarVazao();
            case 6 -> habilitarImagem();
            case 7 -> configurarIntervalo();
            case 8 -> listarSHAs();
            case 0 -> {}
            default -> System.out.println("Opcao invalida!");
        }
    }

    private static void criarSHAPadrao() {
        try {
            int id = fachada.criarSHA();
            System.out.println("SHA criado com ID: " + id);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void criarSHAArquivo() {
        int fileId = lerInteiro("\nID do arquivo (1-5): ");
        try {
            int id = fachada.criarSHAComArquivo(fileId);
            System.out.println("SHA criado com ID: " + id);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void criarSHACustom() {
        System.out.println("\nCRIAR SHA CUSTOMIZADO");
        int regulagem = lerInteiro("Regulagem da torneira (0-100%): ");
        double larguraEntrada = lerDouble("Largura entrada (mm): ");
        double larguraSaida = lerDouble("Largura saida (mm): ");
        double velocidade = lerDouble("Velocidade da agua (m3/s): ");

        try {
            int id = fachada.criarSHACustomizado(regulagem, larguraEntrada, larguraSaida, velocidade);
            System.out.println("SHA criado com ID: " + id);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void finalizarSHA() {
        int id = lerInteiro("\nID do SHA: ");
        fachada.finalizarSHA(id);
    }

    private static void modificarVazao() {
        int id = lerInteiro("\nID do SHA: ");
        double velocidade = lerDouble("Nova velocidade (m3/s): ");
        fachada.modificarVazaoSHA(id, velocidade);
    }

    private static void habilitarImagem() {
        int id = lerInteiro("\nID do SHA: ");
        boolean habilitar = confirmar("Habilitar geracao de imagens? (s/n): ");
        fachada.habilitarGeracaoImagemSHA(id, habilitar);
    }

    private static void configurarIntervalo() {
        int intervalo = lerInteiro("\nIntervalo em milissegundos: ");
        fachada.configurarSimuladorSHA(intervalo);
    }

    private static void listarSHAs() {
        System.out.println(fachada.listarSHAs());
    }

    // ==================== MENU MONITORAMENTO ====================

    private static void menuMonitoramento() {
        System.out.println("\n=========== MONITORAMENTO DE CONSUMO ==============");
        System.out.println("  1 - Iniciar Monitoramento                        ");
        System.out.println("  2 - Parar Monitoramento                          ");
        System.out.println("  3 - Ver Consumo de Conta                         ");
        System.out.println("  4 - Ver Consumo de SHA                           ");
        System.out.println("  0 - Voltar                                       ");
        System.out.println("===================================================");

        int opcao = lerInteiro("\nEscolha: ");

        switch (opcao) {
            case 1 -> iniciarMonitoramento();
            case 2 -> pararMonitoramento();
            case 3 -> verConsumoConta();
            case 4 -> verConsumoSHA();
            case 0 -> {}
            default -> System.out.println("Opcao invalida!");
        }
    }

    private static void iniciarMonitoramento() {
        String numeroConta = lerString("\nNumero da conta: ");
        int intervalo = lerInteiro("Intervalo de verificacao (segundos): ");

        try {
            fachada.iniciarMonitoramento(numeroConta, intervalo);
            System.out.println("Monitoramento iniciado!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void pararMonitoramento() {
        String numeroConta = lerString("\nNumero da conta: ");
        fachada.pararMonitoramento(numeroConta);
    }

    private static void verConsumoConta() {
        String numeroConta = lerString("\nNumero da conta: ");
        try {
            double consumo = fachada.obterConsumoAtualConta(numeroConta);
            System.out.printf("Consumo atual: %.2f m3%n", consumo);

            ContaAgua conta = fachada.buscarConta(numeroConta);
            if (conta != null && conta.getLimiteConsumo() > 0) {
                double percentual = (consumo / conta.getLimiteConsumo()) * 100;
                System.out.printf("Limite: %.2f m3 (%.1f%% utilizado)%n",
                    conta.getLimiteConsumo(), percentual);
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void verConsumoSHA() {
        int id = lerInteiro("\nID do SHA: ");
        double consumo = fachada.obterConsumoSHA(id);
        System.out.printf("Consumo do SHA %d: %.2f m3%n", id, consumo);
    }

    // ==================== MENU ALERTAS ====================

    private static void menuAlertas() {
        System.out.println("\n=============== SISTEMA DE ALERTAS ================");
        System.out.println("  1 - Listar Alertas Nao Lidos                     ");
        System.out.println("  2 - Listar Todos os Alertas                      ");
        System.out.println("  3 - Listar Alertas de Usuario                    ");
        System.out.println("  4 - Listar Alertas de Conta                      ");
        System.out.println("  5 - Marcar Alerta como Lido                      ");
        System.out.println("  0 - Voltar                                       ");
        System.out.println("===================================================");

        int opcao = lerInteiro("\nEscolha: ");

        switch (opcao) {
            case 1 -> listarAlertasNaoLidos();
            case 2 -> listarTodosAlertas();
            case 3 -> listarAlertasUsuario();
            case 4 -> listarAlertasConta();
            case 5 -> marcarAlertaLido();
            case 0 -> {}
            default -> System.out.println("Opcao invalida!");
        }
    }

    private static void listarAlertasNaoLidos() {
        List<Alerta> alertas = fachada.listarAlertasNaoLidos();
        exibirAlertas(alertas, "NAO LIDOS");
    }

    private static void listarTodosAlertas() {
        List<Alerta> alertas = fachada.listarTodosAlertas();
        exibirAlertas(alertas, "TODOS");
    }

    private static void listarAlertasUsuario() {
        String cpf = lerString("\nCPF do usuario: ");
        List<Alerta> alertas = fachada.listarAlertasUsuario(cpf);
        exibirAlertas(alertas, "DO USUARIO " + cpf);
    }

    private static void listarAlertasConta() {
        String numeroConta = lerString("\nNumero da conta: ");
        List<Alerta> alertas = fachada.listarAlertasConta(numeroConta);
        exibirAlertas(alertas, "DA CONTA " + numeroConta);
    }

    private static void exibirAlertas(List<Alerta> alertas, String titulo) {
        if (alertas.isEmpty()) {
            System.out.println("\nNenhum alerta " + titulo.toLowerCase() + ".");
        } else {
            System.out.println("\nALERTAS " + titulo + " (" + alertas.size() + "):");
            System.out.println("-".repeat(90));
            alertas.forEach(System.out::println);
        }
    }

    private static void marcarAlertaLido() {
        int id = lerInteiro("\nID do alerta: ");
        fachada.marcarAlertaComoLido(id);
        System.out.println("Alerta marcado como lido!");
    }

    // ==================== ESTATISTICAS ====================

    private static void exibirEstatisticas() {
        System.out.println(fachada.obterEstatisticas());
    }

    // ==================== UTILIDADES ====================

    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            System.out.print("Valor invalido. " + mensagem);
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpa buffer
        return valor;
    }

    private static double lerDouble(String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextDouble()) {
            System.out.print("Valor invalido. " + mensagem);
            scanner.next();
        }
        double valor = scanner.nextDouble();
        scanner.nextLine(); // Limpa buffer
        return valor;
    }

    private static String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private static String lerStringOpcional(String mensagem) {
        System.out.print(mensagem);
        String valor = scanner.nextLine().trim();
        return valor.isEmpty() ? null : valor;
    }

    private static boolean confirmar(String mensagem) {
        System.out.print(mensagem);
        String resposta = scanner.nextLine().trim().toLowerCase();
        return resposta.equals("s") || resposta.equals("sim") || resposta.equals("y") || resposta.equals("yes");
    }
}

