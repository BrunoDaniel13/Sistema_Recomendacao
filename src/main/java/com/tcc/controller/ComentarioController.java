package com.tcc.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tcc.model.Comentario;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostagemRepository postagemRepository;

    // 🔹 Criar um novo comentário
    @PostMapping("/{idPostagem}")
    public ResponseEntity<?> criarComentario(
            @PathVariable Long idPostagem,
            @Valid @RequestBody Comentario comentario,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        var usuario = usuarioRepository.findByUsuario(principal.getName())
                .orElse(null);
        if (usuario == null)
            return ResponseEntity.status(403).body("Usuário não encontrado");

        var postagem = postagemRepository.findById(idPostagem)
                .orElse(null);
        if (postagem == null)
            return ResponseEntity.status(404).body("Postagem não encontrada");

        comentario.setUsuario(usuario);
        comentario.setPostagem(postagem);
        comentario.setDataHora(LocalDateTime.now());

        return ResponseEntity.status(201).body(comentarioRepository.save(comentario));
    }

    // 🔹 Listar comentários por postagem
    @GetMapping("/postagem/{idPostagem}")
    public ResponseEntity<List<Comentario>> listarPorPostagem(@PathVariable Long idPostagem) {
        List<Comentario> comentarios = comentarioRepository.findAllByPostagemId(idPostagem);
        return ResponseEntity.ok(comentarios);
    }

    // 🔹 Atualizar comentário (apenas dono pode editar)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarComentario(
            @PathVariable Long id,
            @Valid @RequestBody Comentario comentarioAtualizado,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        Optional<Comentario> comentarioExistente = comentarioRepository.findById(id);
        if (comentarioExistente.isEmpty())
            return ResponseEntity.status(404).body("Comentário não encontrado");

        Comentario comentario = comentarioExistente.get();

        // ✅ Só o dono pode editar
        if (!comentario.getUsuario().getUsuario().equals(principal.getName())) {
            return ResponseEntity.status(403).body("Você não tem permissão para editar este comentário");
        }

        comentario.setTexto(comentarioAtualizado.getTexto());
        comentario.setDataHora(LocalDateTime.now());

        return ResponseEntity.ok(comentarioRepository.save(comentario));
    }

    // 🔹 Deletar comentário (apenas dono pode deletar)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarComentario(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        Optional<Comentario> comentarioOpt = comentarioRepository.findById(id);
        if (comentarioOpt.isEmpty())
            return ResponseEntity.status(404).body("Comentário não encontrado");

        Comentario comentario = comentarioOpt.get();

        // ✅ Só o dono pode deletar
        if (!comentario.getUsuario().getUsuario().equals(principal.getName())) {
            return ResponseEntity.status(403).body("Você não tem permissão para excluir este comentário");
        }

        comentarioRepository.delete(comentario);
        return ResponseEntity.noContent().build();
    }
}
