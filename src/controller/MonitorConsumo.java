package controller;

import model.Alerta.TipoAlerta;
import model.ContaAgua;
import model.Hidrometro;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Monitor que verifica periodicamente o consumo das contas e dispara alertas.
 */
public class MonitorConsumo {
    private final FachadaSHA fachadaSHA;
    private final GerenciadorContas gerenciadorContas;
    private final SistemaAlertas sistemaAlertas;

    private final Map<String, Thread> monitoresAtivos = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> runningFlags = new ConcurrentHashMap<>();
    private final Map<String, Boolean> alertasJaDisparados = new ConcurrentHashMap<>();

    public MonitorConsumo(FachadaSHA fachadaSHA, GerenciadorContas gerenciadorContas,
                          SistemaAlertas sistemaAlertas) {
        this.fachadaSHA = fachadaSHA;
        this.gerenciadorContas = gerenciadorContas;
        this.sistemaAlertas = sistemaAlertas;
    }

    public void iniciarMonitoramento(String numeroConta, int intervaloSegundos) {
        if (monitoresAtivos.containsKey(numeroConta)) {
            System.out.println("Monitoramento já está ativo para a conta " + numeroConta);
            return;
        }

        ContaAgua conta = gerenciadorContas.buscar(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }

        AtomicBoolean running = new AtomicBoolean(true);
        runningFlags.put(numeroConta, running);
        alertasJaDisparados.put(numeroConta, false);

        Thread monitor = new Thread(() -> {
            System.out.println("🔍 Monitor iniciado para conta " + numeroConta +
                             " (intervalo: " + intervaloSegundos + "s)");

            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    verificarConsumo(numeroConta);
                    //noinspection BusyWait
                    Thread.sleep(intervaloSegundos * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Erro no monitor da conta " + numeroConta + ": " + e.getMessage());
                }
            }

            System.out.println("🔍 Monitor finalizado para conta " + numeroConta);
        }, "Monitor-" + numeroConta);

        monitoresAtivos.put(numeroConta, monitor);
        monitor.start();
    }

    public void pararMonitoramento(String numeroConta) {
        AtomicBoolean running = runningFlags.get(numeroConta);
        Thread monitor = monitoresAtivos.get(numeroConta);

        if (running == null || monitor == null) {
            System.out.println("Nenhum monitoramento ativo para a conta " + numeroConta);
            return;
        }

        running.set(false);
        monitor.interrupt();

        monitoresAtivos.remove(numeroConta);
        runningFlags.remove(numeroConta);
        alertasJaDisparados.remove(numeroConta);

        System.out.println("Monitoramento parado para a conta " + numeroConta);
    }

    public void pararTodosMonitoramentos() {
        for (String numeroConta : monitoresAtivos.keySet()) {
            pararMonitoramento(numeroConta);
        }
    }

    private void verificarConsumo(String numeroConta) {
        ContaAgua conta = gerenciadorContas.buscar(numeroConta);
        if (conta == null) return;

        double consumoTotal = obterConsumoTotal(conta);
        double limite = conta.getLimiteConsumo();

        // Se há limite configurado e foi excedido
        if (limite > 0 && consumoTotal > limite) {
            Boolean jaDisparado = alertasJaDisparados.get(numeroConta);
            if (jaDisparado == null || !jaDisparado) {
                // Dispara alerta apenas uma vez
                sistemaAlertas.dispararAlerta(
                    TipoAlerta.LIMITE_EXCEDIDO,
                    numeroConta,
                    conta.getCpfUsuario(),
                    consumoTotal,
                    limite
                );
                alertasJaDisparados.put(numeroConta, true);
            }
        } else if (consumoTotal <= limite * 0.9) {
            // Reset do flag se o consumo voltar a ficar abaixo de 90% do limite
            alertasJaDisparados.put(numeroConta, false);
        }
    }

    private double obterConsumoTotal(ContaAgua conta) {
        double total = 0.0;
        for (Integer idSHA : conta.getIdsHidrometros()) {
            Hidrometro h = fachadaSHA.obterHidrometro(idSHA);
            if (h != null) {
                total += h.getVolumeAcumulado();
            }
        }
        return total;
    }

    public boolean estaMonitorando(String numeroConta) {
        return monitoresAtivos.containsKey(numeroConta);
    }

    public int totalMonitoresAtivos() {
        return monitoresAtivos.size();
    }
}

