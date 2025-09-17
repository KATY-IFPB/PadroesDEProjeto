package controller;

import java.io.InputStream;
import java.util.Scanner;

import model.Hidrometro;
import view.Messages;

/**
 * Controlador principal do Hidrometro.
 * 
 * Esta classe gerencia a inicialização e o controle de um objeto Hidrometro,
 * fornecendo um menu interativo via console para manipular atributos do hidrometro
 * como largura do cano, regulagem da torneira, velocidade da água e visualização da medição.
 * 
 * Utiliza a classe {@link Hidrometro} do pacote model para representar o hidrometro real.
 */
public class HidrometroController {

	/** Indica se o hidrometro já foi inicializado */
	private static boolean hidrometroInicializado = false;

	/** Instância do hidrometro controlado */
	private static Hidrometro hidrometro;

	/** Comando atual digitado pelo usuário */
	private static int comando;

	// Constantes de comando do menu
	private static final int SYSTEM_INIT = 0;
	private static final int SET_LARGURA_CANO_ENTRADA = 1;
	private static final int SET_LARGURA_CANO_SAIDA = 2;
	private static final int SET_REGULAGEM_DA_TORNEIRA = 3;
	private static final int SET_VELOCIDADE_DA_AGUA = 4;
	private static final int VER_MEDICAO_DO_HIDROMETRO = 5;

	/**
	 * Método principal que inicia a aplicação.
	 * 
	 * Cria um menu de interação via console para manipular o hidrometro.
	 * Recebe entradas do usuário e executa os comandos correspondentes.
	 * 
	 * Comandos disponíveis:
	 * <ul>
	 *   <li>0 - Inicializar hidrometro</li>
	 *   <li>1 - Definir largura do cano de entrada</li>
	 *   <li>2 - Definir largura do cano de saída</li>
	 *   <li>3 - Definir regulagem da torneira</li>
	 *   <li>4 - Definir velocidade da água</li>
	 *   <li>5 - Ver medição atual do hidrometro</li>
	 * </ul>
	 * 
	 * @param args argumentos da linha de comando (não utilizados)
	 */
	public static void main(String[] args) {
		System.out.println(Messages.getString("HidrometroController.17"));
		Scanner sc = new Scanner(System.in);

		while (sc.hasNext()) {

			comando = sc.nextInt();
			switch (comando) {
			case SYSTEM_INIT: {
				if(!hidrometroInicializado) {
					// Inicializa o hidrometro lendo valores de configuração de um arquivo
					InputStream inputStream = HidrometroController.class.getResourceAsStream(
							Messages.getString("HidrometroController.18"));

					if (inputStream == null) {
						System.out.println(Messages.getString("HidrometroController.0"));
						return;
					}

					try (Scanner scanner = new Scanner(inputStream)) {
						scanner.nextLine();
						@SuppressWarnings("removal")
						double larguraCanoEntrada = new Double(scanner.nextLine());
						scanner.nextLine();
						double larguraCanoSaida = new Double(scanner.nextLine());
						scanner.nextLine();
						int regulagemDaTorneira = new Integer(scanner.nextLine());
						scanner.nextLine();
						double velocidadeDaAgua = new Double(scanner.nextLine());

						hidrometro = new Hidrometro(regulagemDaTorneira, larguraCanoEntrada, larguraCanoSaida,
								velocidadeDaAgua);
						new Thread(hidrometro).start();
						hidrometroInicializado = true;
						System.out.println(Messages.getString("HidrometroController.1"));
					}} else {
						System.out.println(Messages.getString("HidrometroController.19"));
					}
				break;
			}

			case SET_LARGURA_CANO_ENTRADA: {
				// Atualiza a largura do cano de entrada do hidrometro
				if (hidrometroInicializado) {
					System.out.println(Messages.getString("HidrometroController.2"));
					double novoValor = sc.nextDouble();
					hidrometro.setLarguraCanoEntrada(novoValor);
					System.out.println(Messages.getString("HidrometroController.3"));
				} else {
					System.out.println(Messages.getString("HidrometroController.4"));
				}
				break;
			}

			case SET_LARGURA_CANO_SAIDA: {
				// Atualiza a largura do cano de saída do hidrometro
				if (hidrometroInicializado) {
					System.out.println(Messages.getString("HidrometroController.5"));
					double novoValor = sc.nextDouble();
					hidrometro.setLarguraCanoSaida(novoValor);
					System.out.println(Messages.getString("HidrometroController.6"));
				} else {
					System.out.println(Messages.getString("HidrometroController.7"));
				}
				break;
			}

			case SET_REGULAGEM_DA_TORNEIRA: {
				// Atualiza a regulagem da torneira do hidrometro
				if (hidrometroInicializado) {
					System.out.println(Messages.getString("HidrometroController.8"));
					int novoValor = sc.nextInt();
					hidrometro.setTorneiraRegulagem(novoValor);
					System.out.println(Messages.getString("HidrometroController.9"));
				} else {
					System.out.println(Messages.getString("HidrometroController.10"));
				}
				break;
			}

			case SET_VELOCIDADE_DA_AGUA: {
				// Atualiza a velocidade da água que entra no hidrometro
				if (hidrometroInicializado) {
					System.out.println(Messages.getString("HidrometroController.11"));
					double novoValor = sc.nextDouble();
					hidrometro.setVelocidadeAguaEntrada(novoValor);
					System.out.println(Messages.getString("HidrometroController.12"));
				} else {
					System.out.println(Messages.getString("HidrometroController.13"));
				}
				break;
			}

			case VER_MEDICAO_DO_HIDROMETRO: {
				// Exibe o volume acumulado do hidrometro
				if (hidrometroInicializado) {
					System.out.println(Messages.getString("HidrometroController.14") + (int) hidrometro.getVolumeAcumulado());
				} else {
					System.out.println(Messages.getString("HidrometroController.15"));
				}
				break;
			}

			default: {
				System.out.println(Messages.getString("HidrometroController.16"));
				break;
			}
			}

			System.out.println(Messages.getString("HidrometroController.17"));
		}

	}

}
