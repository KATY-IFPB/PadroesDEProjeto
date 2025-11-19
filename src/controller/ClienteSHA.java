package controller;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Cliente CLI que utiliza a `FachadaSHA` para controlar os simuladores sem
 * alterar o funcionamento original do projeto.
 */
public class ClienteSHA {

    public static void main(String[] args) {
        controller.FachadaSHA fachada = controller.FachadaSHA.getInstancia();
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);

        while (true) {
            System.out.println("\n=== Cliente SHA (CLI) ===");
            System.out.println("1 - Configurar intervalo de simulação");
            System.out.println("2 - Criar simulador (padrão)");
            System.out.println("3 - Criar simulador (arquivo específico)");
            System.out.println("4 - Criar simulador (personalizado)");
            System.out.println("5 - Finalizar simulador");
            System.out.println("6 - Modificar vazão (velocidade)");
            System.out.println("7 - Habilitar/Desabilitar geração de imagem");
            System.out.println("8 - Listar simuladores");
            System.out.println("9 - Sair");
            System.out.print("Escolha: ");

            int opc;
            try {
                opc = sc.nextInt();
            } catch (InputMismatchException ime) {
                System.out.println("Entrada inválida.");
                sc.nextLine();
                continue;
            }

            try {
                switch (opc) {
                    case 1:
                        System.out.print("Intervalo em ms: ");
                        int intervalo = sc.nextInt();
                        fachada.configSimuladorSHA(intervalo);
                        break;

                    case 2:
                        int id = fachada.criaSHA();
                        System.out.println("Simulador criado com id: " + id);
                        break;

                    case 3:
                        System.out.print("Número do arquivo (1-5): ");
                        int fileId = sc.nextInt();
                        int id2 = fachada.criaSHAFromFile(fileId);
                        System.out.println("Simulador criado com id: " + id2);
                        break;

                    case 4:
                        System.out.print("Regulagem da torneira (0-100): ");
                        int regulagem = sc.nextInt();
                        System.out.print("Largura cano entrada (m): ");
                        double le = sc.nextDouble();
                        System.out.print("Largura cano saída (m): ");
                        double ls = sc.nextDouble();
                        System.out.print("Velocidade da água (m/s): ");
                        double vel = sc.nextDouble();
                        int id3 = fachada.criaSHACustom(regulagem, le, ls, vel);
                        System.out.println("Simulador criado com id: " + id3);
                        break;

                    case 5:
                        System.out.print("ID do simulador a finalizar: ");
                        int idf = sc.nextInt();
                        fachada.finalizaSHA(idf);
                        break;

                    case 6:
                        System.out.print("ID do simulador: ");
                        int idm = sc.nextInt();
                        System.out.print("Nova velocidade (m/s): ");
                        double nv = sc.nextDouble();
                        fachada.modificaVazaoSHA(idm, nv);
                        break;

                    case 7:
                        System.out.print("ID do simulador: ");
                        int idi = sc.nextInt();
                        System.out.print("Habilitar geração de imagem? (true/false): ");
                        boolean hab = sc.nextBoolean();
                        fachada.habilitaGeracaoImagemSHA(idi, hab);
                        break;

                    case 8:
                        System.out.println(fachada.listarSimuladores());
                        break;

                    case 9:
                        System.out.println("Encerrando cliente CLI...");
                        sc.close();
                        return;

                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Entrada inválida para o parâmetro.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}
