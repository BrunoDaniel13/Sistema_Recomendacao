package com.tcc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioIdAndPostagemId(Long usuarioId, Long postagemId);
    List<Avaliacao> findByPostagemId(Long postagemId);
}
