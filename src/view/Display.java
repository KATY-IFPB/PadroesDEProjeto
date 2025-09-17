package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Classe responsável por exibir e gerar imagens que simulam
 * a leitura de um hidrômetro. 
 * 
 * Esta classe carrega uma imagem base (hidrômetro), desenha
 * por cima dela o valor do display formatado e salva a saída
 * em um novo arquivo.
 * 
 * @author 
 */
public class Display {
	
	/** Valor que será exibido no display (leitura do hidrômetro). */
	private int numeroDisplay;

	/**
	 * Construtor da classe Display.
	 *
	 * @param numeroDisplay valor inicial a ser exibido no display
	 */
	public Display(int numeroDisplay) {
		super();
		this.numeroDisplay = numeroDisplay;
	}
	
	/**
	 * Retorna o valor atual do display.
	 *
	 * @return o número exibido no display
	 */
	public int getNumeroDisplay() {
		return numeroDisplay;
	}

	/**
	 * Define um novo valor para o display.
	 *
	 * @param numeroDisplay novo valor a ser exibido
	 */
	public void setNumeroDisplay(int numeroDisplay) {
		this.numeroDisplay = numeroDisplay;
	}
	
	/**
	 * Gera uma nova imagem JPG contendo o número do display
	 * desenhado sobre a imagem base do hidrômetro.
	 * <p>
	 * O método realiza as seguintes etapas:
	 * <ol>
	 *   <li>Lê a imagem base localizada em "resources/hidrometro.jpg".</li>
	 *   <li>Configura fonte e cor para o texto.</li>
	 *   <li>Formata o número do display para 8 dígitos, adicionando zeros à esquerda.</li>
	 *   <li>Insere espaços entre os dígitos para simular melhor a leitura.</li>
	 *   <li>Desenha o texto na posição (290, 180) da imagem.</li>
	 *   <li>Salva o resultado em "saida/leitura_do_hidrometro.jpg".</li>
	 * </ol>
	 *
	 * Em caso de erro, a exceção será exibida no console.
	 */
	public void gerarImagem() {
		try {
            // 1. Ler a imagem JPG
            BufferedImage imagem = ImageIO.read(new File("resources/hidrometro.jpg"));

            // 2. Obter o contexto gráfico para desenhar
            Graphics2D g2d = imagem.createGraphics();

            // 3. Configurar estilo do texto
            g2d.setFont(new Font("Arial", Font.BOLD, 23));
            g2d.setColor(Color.RED);
            
            // 4. Formatar o valor do display (8 dígitos + espaçamento)
            String aux = String.format("%08d", numeroDisplay);
            aux = aux.replaceAll(".", "$0  ");
            
            // 5. Escrever o texto na imagem
            g2d.drawString(aux, 290, 180);

            // Finalizar edição
            g2d.dispose();

            // 6. Salvar a imagem editada em JPG
            ImageIO.write(imagem, "jpg", new File("saida/leitura_do_hidrometro.jpg"));

            System.out.println("Imagem editada com sucesso! " + numeroDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
