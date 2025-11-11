package com.tcc.model;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_postagens")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String titulo;

    @NotBlank
    @Size(min = 5, max = 1000)
    private String texto;

    @UpdateTimestamp
    private ZonedDateTime data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "postagens" }) // evita loop Tema -> Postagem -> Tema
    private Temas temas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "postagens", "senha" }) // evita loop Usuario -> Postagem -> Usuario
    private Usuario usuario;

    @OneToMany(mappedBy = "postagem", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({ "postagem", "usuario" }) // evita loop nos comentários
    private List<Comentario> comentarios;

    // 🔹 NOVO RELACIONAMENTO — avaliações ligadas à postagem
    @OneToMany(mappedBy = "postagem", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnoreProperties({ "postagem", "usuario" })
    private List<Avaliacao> avaliacoes;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public ZonedDateTime getData() { return data; }
    public void setData(ZonedDateTime data) { this.data = data; }

    public Temas getTemas() { return temas; }
    public void setTemas(Temas temas) { this.temas = temas; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }
}
