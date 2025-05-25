package com.tcc.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tcc.model.Temas;
import com.tcc.repository.TemasRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*" )
public class TemasController {
	
	@Autowired
	private TemasRepository temasRepository;
	
	@GetMapping
	public ResponseEntity<List<Temas>> getAll(){
		return ResponseEntity.ok(temasRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Temas> getById(@PathVariable Long id){
		return temasRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@GetMapping("/descricao/{descricao}")
	public ResponseEntity<List<Temas>> getByTitulo(@PathVariable String descricao){
		return ResponseEntity.ok(temasRepository.findAllByDescricaoContainingIgnoreCase(descricao));
	}
	
	@PostMapping
	public ResponseEntity<Temas> post(@Valid @RequestBody Temas tema){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(temasRepository.save(tema));
	}
	
	@PutMapping
	public ResponseEntity<Temas> put(@Valid @RequestBody Temas tema){
		return temasRepository.findById(tema.getId())
				.map(resposta -> ResponseEntity.status(HttpStatus.OK)
						.body(temasRepository.save(tema)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Temas> tema = temasRepository.findById(id);
		
		if(tema.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		temasRepository.deleteById(id);
	}
}
