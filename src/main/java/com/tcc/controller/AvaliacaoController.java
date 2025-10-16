package com.tcc.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tcc.model.Avaliacao;
import com.tcc.model.Postagem;
import com.tcc.model.Usuario;
import com.tcc.repository.AvaliacaoRepository;
import com.tcc.repository.PostagemRepository;
import com.tcc.repository.UsuarioRepository;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{idPostagem}")
    public ResponseEntity<Avaliacao> avaliar(@PathVariable Long idPostagem,
                                             @RequestBody Avaliacao avaliacao,
                                             Authentication authentication) {

        String usuarioLogado = authentication.getName();
        Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogado);
        Optional<Postagem> postagem = postagemRepository.findById(idPostagem);

        if (usuario.isEmpty() || postagem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Avaliacao> existente = avaliacaoRepository.findByUsuarioIdAndPostagemId(
                usuario.get().getId(), idPostagem);

        Avaliacao novaAvaliacao;
        if (existente.isPresent()) {
            novaAvaliacao = existente.get();
            novaAvaliacao.setNota(avaliacao.getNota());
        } else {
            novaAvaliacao = new Avaliacao();
            novaAvaliacao.setUsuario(usuario.get());
            novaAvaliacao.setPostagem(postagem.get());
            novaAvaliacao.setNota(avaliacao.getNota());
        }

        return ResponseEntity.ok(avaliacaoRepository.save(novaAvaliacao));
    }

    @GetMapping("/media/{idPostagem}")
    public ResponseEntity<Double> media(@PathVariable Long idPostagem) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByPostagemId(idPostagem);

        if (avaliacoes.isEmpty()) return ResponseEntity.ok(0.0);

        double media = avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(media);
    }

    @GetMapping("/minha/{idPostagem}")
    public ResponseEntity<Avaliacao> minhaAvaliacao(@PathVariable Long idPostagem,
                                                    Authentication authentication) {
        String usuarioLogado = authentication.getName();
        Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogado);

        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return avaliacaoRepository.findByUsuarioIdAndPostagemId(usuario.get().getId(), idPostagem)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
