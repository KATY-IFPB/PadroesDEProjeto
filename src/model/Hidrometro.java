package model;

import view.Display;

/**
 * Representa um Hidrometro que monitora e registra o volume de água
 * que passa por um sistema hidráulico.
 * 
 * A classe implementa {@link Runnable}, permitindo que seja executada
 * em uma thread separada para atualizar continuamente o volume acumulado.
 */
public class Hidrometro implements Runnable {

    /** Volume máximo que o hidrometro pode registrar antes de zerar (em m³) */
    private double volumeMaximo = 99999999;

    /** Volume total acumulado registrado pelo hidrometro (em m³) */
    private double volumeAcumulado;

    /** Largura do cano de entrada (em metros) */
    private double larguraCanoEntrada;

    /** Largura do cano de saída (em metros) */
    private double larguraCanoSaida;

    /** Regulação da torneira em percentual (0 a 100) */
    private int torneiraRegulagem;

    /** Velocidade da água na entrada (em m/s) */
    private double velocidadeAguaEntrada;

     private Display display; // Futuro uso para exibição visual

    /**
     * Construtor do Hidrometro.
     *
     * @param torneiraRegulagem percentual de abertura da torneira
     * @param larguraCanoEntrada largura do cano de entrada (m)
     * @param larguraCanoSaida largura do cano de saída (m)
     * @param velocidadeAguaEntrada velocidade da água na entrada (m/s)
     */
    public Hidrometro(int torneiraRegulagem, double larguraCanoEntrada, double larguraCanoSaida, double velocidadeAguaEntrada) {
        this.volumeAcumulado = 0.0;
        this.larguraCanoEntrada = larguraCanoEntrada;
        this.larguraCanoSaida = larguraCanoSaida;
        this.torneiraRegulagem = torneiraRegulagem;
        this.velocidadeAguaEntrada = velocidadeAguaEntrada;
        display = new Display((int)volumeAcumulado);
    }

    /**
     * Calcula a área da seção transversal do cano de entrada.
     * 
     * @return área em m²
     */
    private double calcularArea() {
        return Math.PI * Math.pow(larguraCanoEntrada / 2.0, 2);
    }

    /**
     * Calcula a vazão instantânea de água no hidrometro.
     * 
     * Considera a área do cano, a velocidade da água e a regulagem da torneira.
     * 
     * @return vazão em m³/s
     */
    private double calcularVazao() {
        double porcentagem = torneiraRegulagem * 0.01;
        return calcularArea() * velocidadeAguaEntrada * porcentagem;
    }

    /**
     * Atualiza o volume acumulado de água após um intervalo de tempo.
     * 
     * @param tempoSegundos tempo decorrido em segundos
     */
    public void registrarConsumo(double tempoSegundos) {
        double vazao = calcularVazao(); // m³/s
        double volume = vazao * tempoSegundos; // m³
        this.volumeAcumulado += volume;

        // Se ultrapassar o volume máximo, zera o excesso (comportamento cíclico)
        while (volumeAcumulado > volumeMaximo) {
            volumeAcumulado -= volumeMaximo;
        }
        display.setNumeroDisplay((int)volumeAcumulado);
        display.gerarImagem();
    }

    // ================= Getters e Setters =================

    public double getVolumeMaximo() {
        return volumeMaximo;
    }

    public void setVolumeMaximo(double volumeMaximo) {
        this.volumeMaximo = volumeMaximo;
    }

    public double getVolumeAcumulado() {
        return volumeAcumulado;
    }

    public void setVolumeAcumulado(double volumeAcumulado) {
        this.volumeAcumulado = volumeAcumulado;
    }

    public double getLarguraCanoEntrada() {
        return larguraCanoEntrada;
    }

    public void setLarguraCanoEntrada(double larguraCanoEntrada) {
        this.larguraCanoEntrada = larguraCanoEntrada;
    }

    public double getLarguraCanoSaida() {
        return larguraCanoSaida;
    }

    public void setLarguraCanoSaida(double larguraCanoSaida) {
        this.larguraCanoSaida = larguraCanoSaida;
    }

    public int getTorneiraRegulagem() {
        return torneiraRegulagem;
    }

    public void setTorneiraRegulagem(int torneiraRegulagem) {
        this.torneiraRegulagem = torneiraRegulagem;
    }

    public double getVelocidadeAguaEntrada() {
        return velocidadeAguaEntrada;
    }

    public void setVelocidadeAguaEntrada(double velocidadeAguaEntrada) {
        this.velocidadeAguaEntrada = velocidadeAguaEntrada;
    }

    // ================= Runnable =================

    /**
     * Executa o hidrometro em uma thread separada.
     * 
     * Atualiza o volume acumulado a cada segundo de forma contínua.
     */
    @Override
    public void run() {
        while (true) {
            registrarConsumo(1.0); // atualiza a cada segundo

            try {
                Thread.sleep(1000); // pausa de 1 segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
