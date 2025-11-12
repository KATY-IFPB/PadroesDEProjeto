package controller;

import model.Alerta;
import model.ContaAgua;
import model.Usuario;
import java.util.List;

/**
 * Fachada Singleton de alto nível para o Painel de Monitoramento de Hidrômetros.
 * Esta fachada coordena todos os subsistemas: gestão de usuários, contas, SHAs,
 * monitoramento de consumo e sistema de alertas.
 */
public class FachadaMonitoramento {

    private static FachadaMonitoramento instancia;

    private final FachadaSHA fachadaSHA;
    private final GerenciadorUsuarios gerenciadorUsuarios;
    private final GerenciadorContas gerenciadorContas;
    private final SistemaAlertas sistemaAlertas;
    private final MonitorConsumo monitorConsumo;

    private FachadaMonitoramento() {
        this.fachadaSHA = FachadaSHA.getInstancia();
        this.gerenciadorUsuarios = new GerenciadorUsuarios();
        this.gerenciadorContas = new GerenciadorContas();
        this.sistemaAlertas = new SistemaAlertas(gerenciadorUsuarios);
        this.monitorConsumo = new MonitorConsumo(fachadaSHA, gerenciadorContas, sistemaAlertas);
    }

    public static synchronized FachadaMonitoramento getInstancia() {
        if (instancia == null) {
            instancia = new FachadaMonitoramento();
        }
        return instancia;
    }

    // ==================== GESTÃO DE USUÁRIOS ====================

    public Usuario criarUsuario(String cpf, String nome, String email, String telefone, String endereco) {
        return gerenciadorUsuarios.criar(cpf, nome, email, telefone, endereco);
    }

    public Usuario buscarUsuario(String cpf) {
        return gerenciadorUsuarios.buscar(cpf);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Usuario atualizarUsuario(String cpf, String nome, String email, String telefone, String endereco) {
        return gerenciadorUsuarios.atualizar(cpf, nome, email, telefone, endereco);
    }

    public boolean removerUsuario(String cpf) {
        // Verifica se há contas vinculadas
        List<ContaAgua> contas = gerenciadorContas.listarPorUsuario(cpf);
        if (!contas.isEmpty()) {
            throw new IllegalStateException("Usuário possui contas vinculadas. Remova as contas primeiro.");
        }
        return gerenciadorUsuarios.remover(cpf);
    }

    public List<Usuario> listarUsuarios() {
        return gerenciadorUsuarios.listar();
    }

    public int totalUsuarios() {
        return gerenciadorUsuarios.total();
    }

    // ==================== GESTÃO DE CONTAS ====================

    public ContaAgua criarConta(String numeroConta, String cpfUsuario) {
        // Valida se o usuário existe
        if (!gerenciadorUsuarios.existe(cpfUsuario)) {
            throw new IllegalArgumentException("Usuário com CPF " + cpfUsuario + " não encontrado");
        }
        return gerenciadorContas.criar(numeroConta, cpfUsuario);
    }

    public ContaAgua buscarConta(String numeroConta) {
        return gerenciadorContas.buscar(numeroConta);
    }

    public boolean removerConta(String numeroConta) {
        // Para o monitoramento se estiver ativo
        if (monitorConsumo.estaMonitorando(numeroConta)) {
            monitorConsumo.pararMonitoramento(numeroConta);
        }
        return gerenciadorContas.remover(numeroConta);
    }

    public List<ContaAgua> listarContas() {
        return gerenciadorContas.listar();
    }

    public List<ContaAgua> listarContasPorUsuario(String cpfUsuario) {
        return gerenciadorContas.listarPorUsuario(cpfUsuario);
    }

    public int totalContas() {
        return gerenciadorContas.total();
    }

    // ==================== GESTÃO DE SHAs (HIDRÔMETROS) ====================

    public int criarSHA() throws Exception {
        return fachadaSHA.criaSHA();
    }

    public int criarSHAComArquivo(int fileId) throws Exception {
        return fachadaSHA.criaSHAFromFile(fileId);
    }

    public int criarSHACustomizado(int torneiraRegulagem, double larguraEntrada,
                                   double larguraSaida, double velocidade) throws Exception {
        return fachadaSHA.criaSHACustom(torneiraRegulagem, larguraEntrada, larguraSaida, velocidade);
    }

    public void finalizarSHA(int idSHA) {
        // Remove das contas que o utilizam
        ContaAgua conta = gerenciadorContas.buscarPorSHA(idSHA);
        if (conta != null) {
            gerenciadorContas.desvincularSHA(conta.getNumeroConta(), idSHA);
        }
        fachadaSHA.finalizaSHA(idSHA);
    }

    public void modificarVazaoSHA(int idSHA, double novaVelocidade) {
        fachadaSHA.modificaVazaoSHA(idSHA, novaVelocidade);
    }

    public void habilitarGeracaoImagemSHA(int idSHA, boolean habilitar) {
        fachadaSHA.habilitaGeracaoImagemSHA(idSHA, habilitar);
    }

    public void configurarSimuladorSHA(int intervaloMillis) {
        fachadaSHA.configSimuladorSHA(intervaloMillis);
    }

    public String listarSHAs() {
        return fachadaSHA.listarSimuladores();
    }

    // ==================== VINCULAÇÃO CONTA ↔ SHA ====================

    public void vincularSHAConta(String numeroConta, int idSHA) {
        // Verifica se o SHA existe
        if (fachadaSHA.obterHidrometro(idSHA) == null) {
            throw new IllegalArgumentException("SHA " + idSHA + " não encontrado");
        }
        gerenciadorContas.vincularSHA(numeroConta, idSHA);
    }

    public void desvincularSHAConta(String numeroConta, int idSHA) {
        gerenciadorContas.desvincularSHA(numeroConta, idSHA);
    }

    // ==================== MONITORAMENTO DE CONSUMO ====================

    public void iniciarMonitoramento(String numeroConta, int intervaloSegundos) {
        monitorConsumo.iniciarMonitoramento(numeroConta, intervaloSegundos);
    }

    public void pararMonitoramento(String numeroConta) {
        monitorConsumo.pararMonitoramento(numeroConta);
    }

    @SuppressWarnings("unused")
    public void pararTodosMonitoramentos() {
        monitorConsumo.pararTodosMonitoramentos();
    }

    public double obterConsumoAtualConta(String numeroConta) {
        ContaAgua conta = gerenciadorContas.buscar(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta " + numeroConta + " não encontrada");
        }

        double total = 0.0;
        for (Integer idSHA : conta.getIdsHidrometros()) {
            total += fachadaSHA.obterVolumeAcumulado(idSHA);
        }
        return total;
    }

    public double obterConsumoSHA(int idSHA) {
        return fachadaSHA.obterVolumeAcumulado(idSHA);
    }

    @SuppressWarnings("unused")
    public boolean estaMonitorando(String numeroConta) {
        return monitorConsumo.estaMonitorando(numeroConta);
    }

    // ==================== CONFIGURAÇÃO DE LIMITES E ALERTAS ====================

    public void configurarLimiteConsumo(String numeroConta, double volumeMaximo) {
        gerenciadorContas.configurarLimite(numeroConta, volumeMaximo);
    }

    public void habilitarAlertaEmail(String numeroConta, boolean habilitar) {
        gerenciadorContas.configurarAlertaEmail(numeroConta, habilitar);
    }

    public void habilitarAlertaConcessionaria(String numeroConta, boolean habilitar) {
        gerenciadorContas.configurarAlertaConcessionaria(numeroConta, habilitar);
    }

    public List<Alerta> listarAlertasUsuario(String cpfUsuario) {
        return sistemaAlertas.listarPorUsuario(cpfUsuario);
    }

    public List<Alerta> listarAlertasConta(String numeroConta) {
        return sistemaAlertas.listarPorConta(numeroConta);
    }

    public List<Alerta> listarTodosAlertas() {
        return sistemaAlertas.listarTodos();
    }

    public List<Alerta> listarAlertasNaoLidos() {
        return sistemaAlertas.listarNaoLidos();
    }

    public void marcarAlertaComoLido(int idAlerta) {
        sistemaAlertas.marcarComoLido(idAlerta);
    }

    public int totalAlertas() {
        return sistemaAlertas.totalAlertas();
    }

    public int totalAlertasNaoLidos() {
        return sistemaAlertas.totalNaoLidos();
    }

    // ==================== ESTATÍSTICAS GERAIS ====================

    public String obterEstatisticas() {
        return "\n╔════════════════════════════════════════╗\n" +
                "║   ESTATÍSTICAS DO SISTEMA              ║\n" +
                "╠════════════════════════════════════════╣\n" +
                String.format("║ Usuários cadastrados: %-16d ║\n", totalUsuarios()) +
                String.format("║ Contas ativas: %-23d ║\n", totalContas()) +
                String.format("║ Monitores ativos: %-20d ║\n", monitorConsumo.totalMonitoresAtivos()) +
                String.format("║ Alertas gerados: %-22d ║\n", totalAlertas()) +
                String.format("║ Alertas não lidos: %-20d ║\n", totalAlertasNaoLidos()) +
                "╚════════════════════════════════════════╝\n";
    }

    // ==================== SHUTDOWN ====================

    public void encerrarSistema() {
        System.out.println("\n🔌 Encerrando sistema de monitoramento...");
        monitorConsumo.pararTodosMonitoramentos();
        // A FachadaSHA gerencia seus próprios simuladores
        System.out.println("✅ Sistema encerrado com sucesso.");
    }
}

