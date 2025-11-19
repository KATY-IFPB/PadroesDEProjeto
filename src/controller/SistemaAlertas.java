package controller;

import model.Alerta;
import model.Alerta.TipoAlerta;
import model.Usuario;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Sistema responsável por gerenciar e disparar alertas.
 */
public class SistemaAlertas {
    private final List<Alerta> alertas = new CopyOnWriteArrayList<>();
    private final GerenciadorUsuarios gerenciadorUsuarios;

    public SistemaAlertas(GerenciadorUsuarios gerenciadorUsuarios) {
        this.gerenciadorUsuarios = gerenciadorUsuarios;
    }

    public void dispararAlerta(TipoAlerta tipo, String numeroConta, String cpfUsuario,
                                  double volumeAtual, double volumeLimite) {
        Alerta alerta = new Alerta(tipo, numeroConta, cpfUsuario, volumeAtual, volumeLimite);
        alertas.add(alerta);

        // Log do alerta
        System.out.println("\n⚠️  ALERTA GERADO: " + alerta);

        // Simulação de envio (em produção, aqui seria integração com serviços externos)
        simularEnvioAlerta(alerta);
    }

    private void simularEnvioAlerta(Alerta alerta) {
        Usuario usuario = gerenciadorUsuarios.buscar(alerta.getCpfUsuario());
        if (usuario != null && usuario.getEmail() != null) {
            System.out.println("   📧 Email enviado para: " + usuario.getEmail());
        }
        System.out.println("   🏢 Concessionária notificada sobre a conta: " + alerta.getNumeroConta());
    }

    public List<Alerta> listarTodos() {
        return new ArrayList<>(alertas);
    }

    public List<Alerta> listarPorUsuario(String cpfUsuario) {
        List<Alerta> resultado = new ArrayList<>();
        for (Alerta alerta : alertas) {
            if (alerta.getCpfUsuario().equals(cpfUsuario)) {
                resultado.add(alerta);
            }
        }
        return resultado;
    }

    public List<Alerta> listarPorConta(String numeroConta) {
        List<Alerta> resultado = new ArrayList<>();
        for (Alerta alerta : alertas) {
            if (alerta.getNumeroConta().equals(numeroConta)) {
                resultado.add(alerta);
            }
        }
        return resultado;
    }

    public List<Alerta> listarNaoLidos() {
        List<Alerta> resultado = new ArrayList<>();
        for (Alerta alerta : alertas) {
            if (alerta.naoLido()) {
                resultado.add(alerta);
            }
        }
        return resultado;
    }

    public void marcarComoLido(int idAlerta) {
        for (Alerta alerta : alertas) {
            if (alerta.getId() == idAlerta) {
                alerta.marcarComoLido();
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    public void limparAlertas() {
        alertas.clear();
    }

    public int totalAlertas() {
        return alertas.size();
    }

    public int totalNaoLidos() {
        int count = 0;
        for (Alerta alerta : alertas) {
            if (alerta.naoLido()) count++;
        }
        return count;
    }
}

