package com.tcc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tcc.model.Comentario;
import com.tcc.model.Postagem;
import com.tcc.model.Usuario;
import com.tcc.repository.ComentarioRepository;
import com.tcc.repository.PostagemRepository;
import com.tcc.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comentarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Comentario>> getAll() {
        return ResponseEntity.ok(comentarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getById(@PathVariable Long id) {
        return comentarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/postagem/{idPostagem}")
    public ResponseEntity<List<Comentario>> getByPostagem(@PathVariable Long idPostagem) {
        return ResponseEntity.ok(comentarioRepository.findAllByPostagem_Id(idPostagem));
    }

    // ✅ Novo endpoint: cria comentário vinculado à postagem e ao usuário logado
    @PostMapping("/{idPostagem}")
    public ResponseEntity<?> post(
            @PathVariable Long idPostagem,
            @Valid @RequestBody Comentario comentario,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {

        return postagemRepository.findById(idPostagem)
                .map(postagem -> {
                    Usuario usuario = usuarioRepository.findById(usuarioAutenticado.getId()).orElse(null);
                    if (usuario == null)
                        return ResponseEntity.status(403).body("Usuário não encontrado.");

                    comentario.setUsuario(usuario);
                    comentario.setPostagem(postagem);

                    Comentario salvo = comentarioRepository.save(comentario);
                    return ResponseEntity.status(201).body(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Comentario> put(@Valid @RequestBody Comentario comentario) {
        if (comentarioRepository.existsById(comentario.getId()))
            return ResponseEntity.ok(comentarioRepository.save(comentario));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (comentarioRepository.existsById(id)) {
            comentarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
