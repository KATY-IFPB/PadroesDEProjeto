<<<<<<< HEAD
package controller;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import model.Hidrometro;

/**
 * Cliente CLI que utiliza exclusivamente a SHAFacade para operar o SHA.
 * Esta classe demonstra que um cliente qualquer consegue operar todas as
 * funcionalidades do SHA apenas chamando os métodos da fachada, sem conhecer
 * detalhes internos de threads, configuração ou modelo.
 */
public class ClienteCLI {

    public static void main(String[] args) {
        SHAFacade fachada = SHAFacade.getInstancia();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CLIENTE CLI - FACHADA SHA ===");

        boolean executando = true;
        while (executando) {
            exibirMenu();
            String opcao = sc.next();

            try {
                switch (opcao) {
                    case "1":
                        configurarSistema(sc, fachada);
                        break;
                    case "2":
                        criarInstanciaSHA(sc, fachada);
                        break;
                    case "3":
                        modificarVazao(sc, fachada);
                        break;
                    case "4":
                        controlarImagem(sc, fachada);
                        break;
                    case "5":
                        finalizarInstancia(sc, fachada);
                        break;
                    case "6":
                        listarInstancias(fachada);
                        break;
                    case "0":
                        executando = false;
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Tente novamente.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        sc.close();
        System.out.println("CLI encerrada.");
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("--- MENU FACHADA SHA ---");
        System.out.println("1 - Configurar Sistema (intervalo simulação)");
        System.out.println("2 - Criar Instância SHA");
        System.out.println("3 - Modificar Vazão de uma Instância");
        System.out.println("4 - Habilitar/Desabilitar Geração de Imagem");
        System.out.println("5 - Finalizar Instância");
        System.out.println("6 - Listar Instâncias Ativas");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private static void configurarSistema(Scanner sc, SHAFacade fachada) {
        System.out.print("Intervalo de simulação em ms: ");
        int intervalo = sc.nextInt();
        fachada.configSimuladorSHA(intervalo);
        System.out.println("Intervalo configurado para " + intervalo + " ms");
    }

    private static void criarInstanciaSHA(Scanner sc, SHAFacade fachada) throws Exception {
        System.out.println("Tipo de configuração:");
        System.out.println("1 - Arquivo padrão (configuracao.txt)");
        System.out.println("2 - Arquivo específico (configuracaoX.txt)");
        System.out.println("3 - Configuração personalizada");
        System.out.print("Opção: ");
        int tipo = sc.nextInt();

        int idGerado;

        switch (tipo) {
            case 1:
                idGerado = fachada.criaSHA(true, 0, 0, 0, 0, 0);
                System.out.println("Instância criada com ID: " + idGerado + " (configuração padrão)");
                break;
            case 2:
                System.out.print("ID do arquivo (1-5): ");
                int idArq = sc.nextInt();
                idGerado = fachada.criaSHA(true, idArq, 0, 0, 0, 0);
                System.out.println("Instância criada com ID: " + idGerado + " (configuracao" + idArq + ".txt)");
                break;
            case 3:
                System.out.print("Largura cano entrada (mm): ");
                double largE = sc.nextDouble();
                System.out.print("Largura cano saída (mm): ");
                double largS = sc.nextDouble();
                System.out.print("Regulagem torneira (0-100): ");
                int reg = sc.nextInt();
                System.out.print("Velocidade água (m³/s): ");
                double vel = sc.nextDouble();
                idGerado = fachada.criaSHA(false, 0, largE, largS, reg, vel);
                System.out.println("Instância criada com ID: " + idGerado + " (personalizada)");
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private static void modificarVazao(Scanner sc, SHAFacade fachada) {
        System.out.print("ID da instância: ");
        int id = sc.nextInt();
        System.out.print("Nova vazão (m³/s): ");
        double nova = sc.nextDouble();
        fachada.modificaVazaoSHA(id, nova);
        System.out.println("Vazão da instância " + id + " atualizada.");
    }

    private static void controlarImagem(Scanner sc, SHAFacade fachada) {
        System.out.print("ID da instância: ");
        int id = sc.nextInt();
        System.out.print("Habilitar imagens? (true/false): ");
        boolean hab = sc.nextBoolean();
        fachada.habilitaGeracaoImagemSHA(id, hab);
        System.out.println("Imagem para instância " + id + " configurada para: " + hab);
    }

    private static void finalizarInstancia(Scanner sc, SHAFacade fachada) {
        System.out.print("ID da instância a finalizar: ");
        int id = sc.nextInt();
        fachada.finalizaSHA(id);
        System.out.println("Instância " + id + " finalizada.");
    }

    private static void listarInstancias(SHAFacade fachada) {
        Map<Integer, Hidrometro> ativos = fachada.getSimuladoresAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhuma instância ativa.");
            return;
        }
        System.out.println("Instâncias ativas:");
        ativos.forEach((id, hidrometro) -> {
            int volume = (int) hidrometro.getVolumeAcumulado();
            System.out.printf("ID: %d | Volume: %d m³ | Regulagem: %d%%%n",
                    id, volume, hidrometro.getTorneiraRegulagem());
        });
    }
}
=======
>>>>>>> origin/dev_Pedro.Cordeiro

