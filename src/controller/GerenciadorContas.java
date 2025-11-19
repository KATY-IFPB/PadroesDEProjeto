package controller;

import model.ContaAgua;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerenciador responsável pelo CRUD de contas de água.
 */
public class GerenciadorContas {
    private final Map<String, ContaAgua> contas = new ConcurrentHashMap<>();
    private final Map<Integer, String> shaParaConta = new ConcurrentHashMap<>();

    public ContaAgua criar(String numeroConta, String cpfUsuario) {
        if (contas.containsKey(numeroConta)) {
            throw new IllegalArgumentException("Conta " + numeroConta + " já existe");
        }
        ContaAgua conta = new ContaAgua(numeroConta, cpfUsuario);
        contas.put(numeroConta, conta);
        return conta;
    }

    public ContaAgua buscar(String numeroConta) {
        return contas.get(numeroConta);
    }

    public ContaAgua buscarPorSHA(int idSHA) {
        String numeroConta = shaParaConta.get(idSHA);
        return numeroConta != null ? contas.get(numeroConta) : null;
    }

    public boolean remover(String numeroConta) {
        ContaAgua conta = contas.remove(numeroConta);
        if (conta != null) {
            // Remove os mapeamentos SHA -> Conta
            for (Integer idSHA : conta.getIdsHidrometros()) {
                shaParaConta.remove(idSHA);
            }
            return true;
        }
        return false;
    }

    public List<ContaAgua> listar() {
        return new ArrayList<>(contas.values());
    }

    public List<ContaAgua> listarPorUsuario(String cpfUsuario) {
        List<ContaAgua> resultado = new ArrayList<>();
        for (ContaAgua conta : contas.values()) {
            if (conta.getCpfUsuario().equals(cpfUsuario)) {
                resultado.add(conta);
            }
        }
        return resultado;
    }

    public void vincularSHA(String numeroConta, int idSHA) {
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }
        // Verifica se o SHA já está vinculado a outra conta
        String contaExistente = shaParaConta.get(idSHA);
        if (contaExistente != null && !contaExistente.equals(numeroConta)) {
            throw new IllegalArgumentException("SHA " + idSHA + " já está vinculado à conta " + contaExistente);
        }
        conta.adicionarHidrometro(idSHA);
        shaParaConta.put(idSHA, numeroConta);
    }

    public void desvincularSHA(String numeroConta, int idSHA) {
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }
        conta.removerHidrometro(idSHA);
        shaParaConta.remove(idSHA);
    }

    public void configurarLimite(String numeroConta, double limite) {
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }
        conta.setLimiteConsumo(limite);
    }

    public void configurarAlertaEmail(String numeroConta, boolean habilitar) {
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }
        conta.setAlertaEmailHabilitado(habilitar);
    }

    public void configurarAlertaConcessionaria(String numeroConta, boolean habilitar) {
        ContaAgua conta = contas.get(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }
        conta.setAlertaConcessionariaHabilitado(habilitar);
    }

    @SuppressWarnings("unused")
    public boolean existe(String numeroConta) {
        return contas.containsKey(numeroConta);
    }

    public int total() {
        return contas.size();
    }
}

