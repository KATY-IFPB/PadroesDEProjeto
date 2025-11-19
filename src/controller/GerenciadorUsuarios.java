package controller;

import model.Usuario;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerenciador responsável pelo CRUD de usuários.
 */
public class GerenciadorUsuarios {
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    public Usuario criar(String cpf, String nome, String email, String telefone, String endereco) {
        if (usuarios.containsKey(cpf)) {
            throw new IllegalArgumentException("Usuário com CPF " + cpf + " já existe");
        }
        Usuario usuario = new Usuario(cpf, nome, email, telefone, endereco);
        usuarios.put(cpf, usuario);
        return usuario;
    }

    public Usuario buscar(String cpf) {
        return usuarios.get(cpf);
    }

    public Usuario atualizar(String cpf, String nome, String email, String telefone, String endereco) {
        Usuario usuario = usuarios.get(cpf);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário com CPF " + cpf + " não encontrado");
        }
        if (nome != null) usuario.setNome(nome);
        if (email != null) usuario.setEmail(email);
        if (telefone != null) usuario.setTelefone(telefone);
        if (endereco != null) usuario.setEndereco(endereco);
        return usuario;
    }

    public boolean remover(String cpf) {
        return usuarios.remove(cpf) != null;
    }

    public List<Usuario> listar() {
        return new ArrayList<>(usuarios.values());
    }

    public boolean existe(String cpf) {
        return usuarios.containsKey(cpf);
    }

    public int total() {
        return usuarios.size();
    }
}

