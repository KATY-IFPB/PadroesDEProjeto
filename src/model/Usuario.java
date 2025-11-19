package model;

import java.util.Objects;

/**
 * Representa um usuário/cliente da concessionária de água.
 */
public class Usuario {
    private final String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;

    public Usuario(String cpf, String nome, String email, String telefone, String endereco) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.cpf = cpf.trim();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    @SuppressWarnings("unused")
    public String getCpf() {
        return cpf;
    }

    @SuppressWarnings("unused")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @SuppressWarnings("unused")
    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return cpf.equals(usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return String.format("Usuario[CPF=%s, Nome=%s, Email=%s, Tel=%s, End=%s]",
                cpf, nome, email, telefone, endereco);
    }
}

