package com.tcc.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tcc.model.Usuario;
import com.tcc.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		System.out.println("=== 🔍 USER DETAILS SERVICE ===");
		System.out.println("👤 Buscando usuário: " + userName);

		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userName);

		if (usuario.isPresent()) {
			System.out.println("✅ Usuário encontrado no banco: " + usuario.get().getNome());
			System.out.println("✅ Username do banco: " + usuario.get().getUsuario());
			System.out.println("=== 🎯 USER DETAILS ENCONTRADO ===");
			return new UserDetailsImpl(usuario.get());
		}
		else {
			System.err.println("❌ USUÁRIO NÃO ENCONTRADO NO BANCO: " + userName);
			System.out.println("=== 🚫 USER DETAILS NÃO ENCONTRADO ===");
			throw new UsernameNotFoundException("Usuário não encontrado!");
		}
			
	}
}