package view;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Display {
	
	
	private int numeroDisplay;

	
	public Display(int numeroDisplay) {
		super();
		
		this.numeroDisplay = numeroDisplay;
	}
	
	public int getNumeroDisplay() {
		return numeroDisplay;
	}
	public void setNumeroDisplay(int numeroDisplay) {
		this.numeroDisplay = numeroDisplay;
	}
	
	public void gerarImagem() {
		try {
            // 1. Ler a imagem JPG
            BufferedImage imagem = ImageIO.read(new File("resources/hidrometro.jpg"));

            // 2. Obter o contexto gráfico para desenhar
            Graphics2D g2d = imagem.createGraphics();

            // 3. Configurar estilo do texto
            g2d.setFont(new Font("Arial", Font.BOLD, 23));
            g2d.setColor(Color.RED);
            
            //formatando a leitura para ser exibida
            String aux= String.format("%08d", numeroDisplay);
            aux= aux.replaceAll(".", "$0  ");
            // 4. Escrever texto na imagem
            g2d.drawString(aux, 290, 180);

            // Finalizar edição
            g2d.dispose();

            // 5. Salvar a imagem editada em JPG
            ImageIO.write(imagem, "jpg", new File("saida/leitura_do_hidrometro.jpg"));

            
            System.out.println("Imagem editada com sucesso! "+ numeroDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
}

