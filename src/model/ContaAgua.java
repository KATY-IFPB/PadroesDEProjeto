package model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Representa uma conta de água vinculada a um usuário e associada a um ou mais hidrômetros (SHAs).
 */
public class ContaAgua {
    private final String numeroConta;
    private final String cpfUsuario;
    private final Set<Integer> idsHidrometros;
    private double limiteConsumo; // em m³
    private boolean alertaEmailHabilitado;
    private boolean alertaConcessionariaHabilitado;

    public ContaAgua(String numeroConta, String cpfUsuario) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta não pode ser vazio");
        }
        if (cpfUsuario == null || cpfUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do usuário não pode ser vazio");
        }
        this.numeroConta = numeroConta.trim();
        this.cpfUsuario = cpfUsuario.trim();
        this.idsHidrometros = ConcurrentHashMap.newKeySet();
        this.limiteConsumo = 0; // sem limite por padrão
        this.alertaEmailHabilitado = false;
        this.alertaConcessionariaHabilitado = false;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public Set<Integer> getIdsHidrometros() {
        return Collections.unmodifiableSet(idsHidrometros);
    }

    public void adicionarHidrometro(int idSHA) {
        idsHidrometros.add(idSHA);
    }

    public void removerHidrometro(int idSHA) {
        idsHidrometros.remove(idSHA);
    }

    public double getLimiteConsumo() {
        return limiteConsumo;
    }

    public void setLimiteConsumo(double limiteConsumo) {
        this.limiteConsumo = limiteConsumo;
    }

    @SuppressWarnings("unused")
    public boolean isAlertaEmailHabilitado() {
        return alertaEmailHabilitado;
    }

    public void setAlertaEmailHabilitado(boolean alertaEmailHabilitado) {
        this.alertaEmailHabilitado = alertaEmailHabilitado;
    }

    @SuppressWarnings("unused")
    public boolean isAlertaConcessionariaHabilitado() {
        return alertaConcessionariaHabilitado;
    }

    public void setAlertaConcessionariaHabilitado(boolean alertaConcessionariaHabilitado) {
        this.alertaConcessionariaHabilitado = alertaConcessionariaHabilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaAgua contaAgua = (ContaAgua) o;
        return numeroConta.equals(contaAgua.numeroConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroConta);
    }

    @Override
    public String toString() {
        return String.format("ContaAgua[Numero=%s, CPF=%s, Hidrometros=%s, Limite=%.2f m³]",
                numeroConta, cpfUsuario, idsHidrometros, limiteConsumo);
    }
}

