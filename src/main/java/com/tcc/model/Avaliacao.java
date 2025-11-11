package com.tcc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tb_avaliacoes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private int nota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"postagem", "senha"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postagem_id", nullable = false)
    @JsonIgnoreProperties({"usuario", "temas", "comentarios"})
    private Postagem postagem;

    public Avaliacao() {}

    public Avaliacao(Long id, int nota, Usuario usuario, Postagem postagem) {
        this.id = id;
        this.nota = nota;
        this.usuario = usuario;
        this.postagem = postagem;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Postagem getPostagem() { return postagem; }
    public void setPostagem(Postagem postagem) { this.postagem = postagem; }
}
