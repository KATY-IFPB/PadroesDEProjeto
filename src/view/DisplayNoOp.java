package view;

/**
 * Display "no-op" que substitui o Display real quando o usuário deseja
 * desabilitar a geração de imagem
 */
public class DisplayNoOp extends Display {


    public DisplayNoOp(int numeroDisplay, int simuladorId) {
        super(numeroDisplay, simuladorId);
    }

    @Override
    public void gerarImagem() {
        //  não gera arquivo de saída
    }
}

