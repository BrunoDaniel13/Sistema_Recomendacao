package com.tcc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tcc.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findAllByPostagem_Id(Long postagemId);
}