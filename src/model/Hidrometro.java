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

    /** ID único do simulador (1-5) */
    private final int simuladorId;

    /** Volume máximo que o hidrometro pode registrar antes de zerar (em m³) */
    private double volumeMaximo = 99999999;

    /** Volume total acumulado registrado pelo hidrometro (em m³) */
    private volatile double volumeAcumulado;

    /** Largura do cano de entrada (em metros) */
    private volatile double larguraCanoEntrada;

    /** Largura do cano de saída (em metros) */
    private volatile double larguraCanoSaida;

    /** Regulação da torneira em percentual (0 a 100) */
    private volatile int torneiraRegulagem;

    /** Velocidade da água na entrada (em m/s) */
    private volatile double velocidadeAguaEntrada;

    private Display display; // Para exibição visual

    /** Flag para controlar execução da thread */
    private volatile boolean executando = true;

    /**
     * Construtor original do Hidrometro (compatibilidade).
     */
    public Hidrometro(int torneiraRegulagem, double larguraCanoEntrada, double larguraCanoSaida, double velocidadeAguaEntrada) {
        this(1, torneiraRegulagem, larguraCanoEntrada, larguraCanoSaida, velocidadeAguaEntrada);
    }

    /**
     * Construtor do Hidrometro com ID de simulador.
     *
     * @param simuladorId ID único do simulador (1-5)
     * @param torneiraRegulagem percentual de abertura da torneira
     * @param larguraCanoEntrada largura do cano de entrada (m)
     * @param larguraCanoSaida largura do cano de saída (m)
     * @param velocidadeAguaEntrada velocidade da água na entrada (m/s)
     */
    public Hidrometro(int simuladorId, int torneiraRegulagem, double larguraCanoEntrada, double larguraCanoSaida, double velocidadeAguaEntrada) {
        this.simuladorId = simuladorId;
        this.volumeAcumulado = 0.0;
        this.larguraCanoEntrada = larguraCanoEntrada;
        this.larguraCanoSaida = larguraCanoSaida;
        this.torneiraRegulagem = torneiraRegulagem;
        this.velocidadeAguaEntrada = velocidadeAguaEntrada;
        display = new Display((int)volumeAcumulado, simuladorId);
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
    public synchronized void registrarConsumo(double tempoSegundos) {
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

    // ================= Getters e Setters (thread-safe) =================

    public synchronized double getVolumeMaximo() {
        return volumeMaximo;
    }

    public synchronized void setVolumeMaximo(double volumeMaximo) {
        this.volumeMaximo = volumeMaximo;
    }

    public synchronized double getVolumeAcumulado() {
        return volumeAcumulado;
    }

    public synchronized void setVolumeAcumulado(double volumeAcumulado) {
        this.volumeAcumulado = volumeAcumulado;
    }

    public synchronized double getLarguraCanoEntrada() {
        return larguraCanoEntrada;
    }

    public synchronized void setLarguraCanoEntrada(double larguraCanoEntrada) {
        this.larguraCanoEntrada = larguraCanoEntrada;
    }

    public synchronized double getLarguraCanoSaida() {
        return larguraCanoSaida;
    }

    public synchronized void setLarguraCanoSaida(double larguraCanoSaida) {
        this.larguraCanoSaida = larguraCanoSaida;
    }

    public synchronized int getTorneiraRegulagem() {
        return torneiraRegulagem;
    }

    public synchronized void setTorneiraRegulagem(int torneiraRegulagem) {
        this.torneiraRegulagem = torneiraRegulagem;
    }

    public synchronized double getVelocidadeAguaEntrada() {
        return velocidadeAguaEntrada;
    }

    public synchronized void setVelocidadeAguaEntrada(double velocidadeAguaEntrada) {
        this.velocidadeAguaEntrada = velocidadeAguaEntrada;
    }

    public int getSimuladorId() {
        return simuladorId;
    }

    public void parar() {
        executando = false;
    }

    // ================= Runnable =================

    /**
     * Executa o hidrometro em uma thread separada.
     *
     * Atualiza o volume acumulado a cada segundo de forma contínua.
     */
    @Override
    public void run() {
        while (executando) {
            registrarConsumo(1.0); // atualiza a cada segundo

            try {
                Thread.sleep(1000); // pausa de 1 segundo
            } catch (InterruptedException e) {
                executando = false;
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
