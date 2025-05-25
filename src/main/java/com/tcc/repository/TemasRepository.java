package com.tcc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.tcc.model.Temas;

public interface TemasRepository extends JpaRepository<Temas, Long> {
	
	public List<Temas> findAllByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);

}
