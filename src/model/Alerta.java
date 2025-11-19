package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Representa um alerta gerado pelo sistema de monitoramento.
 */
public class Alerta {
    @SuppressWarnings("unused")
    public enum TipoAlerta {
        LIMITE_EXCEDIDO,
        CONSUMO_ANORMAL,
        SISTEMA
    }

    private final int id;
    private final TipoAlerta tipo;
    private final String numeroConta;
    private final String cpfUsuario;
    private final double volumeAtual;
    private final double volumeLimite;
    private final LocalDateTime timestamp;
    private boolean lido;

    private static int proximoId = 1;

    public Alerta(TipoAlerta tipo, String numeroConta, String cpfUsuario, double volumeAtual, double volumeLimite) {
        this.id = proximoId++;
        this.tipo = tipo;
        this.numeroConta = numeroConta;
        this.cpfUsuario = cpfUsuario;
        this.volumeAtual = volumeAtual;
        this.volumeLimite = volumeLimite;
        this.timestamp = LocalDateTime.now();
        this.lido = false;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public TipoAlerta getTipo() {
        return tipo;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    @SuppressWarnings("unused")
    public double getVolumeAtual() {
        return volumeAtual;
    }

    @SuppressWarnings("unused")
    public double getVolumeLimite() {
        return volumeLimite;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public boolean naoLido() {
        return !lido;
    }

    public void marcarComoLido() {
        this.lido = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alerta alerta = (Alerta) o;
        return id == alerta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[Alerta #%d] %s | Conta: %s | CPF: %s | Volume: %.2f/%.2f m³ | %s %s",
                id, tipo, numeroConta, cpfUsuario, volumeAtual, volumeLimite,
                timestamp.format(formatter), lido ? "[LIDO]" : "[NÃO LIDO]");
    }
}

