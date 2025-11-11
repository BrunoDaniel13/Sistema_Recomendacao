package com.tcc.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_temas")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Temas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 200)
    private String descricao;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "temas", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties({ "temas", "usuario", "comentarios" })
    private List<Postagem> postagens;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Postagem> getPostagens() { return postagens; }
    public void setPostagens(List<Postagem> postagens) { this.postagens = postagens; }
}
