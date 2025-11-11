package com.tcc.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            System.out.println("\n=== 🔐 JWT AUTH FILTER INICIADO ===");
            System.out.println("📡 URL: " + request.getRequestURI());
            System.out.println("🔑 Authorization Header: " + authHeader);
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7).trim();
                System.out.println("✅ Token extraído (primeiros 30 chars): " + 
                    (token.length() > 30 ? token.substring(0, 30) + "..." : token));
                System.out.println("📏 Comprimento do token: " + token.length());
                
                // 🔥 TENTAR EXTRAIR USERNAME
                try {
                    username = jwtService.extractUsername(token);
                    System.out.println("👤 Username extraído do token: " + username);
                } catch (Exception e) {
                    System.err.println("❌ Erro ao extrair username do token: " + e.getMessage());
                    e.printStackTrace();
                    throw e; // Propaga o erro
                }
            } else {
                System.out.println("⚠️  Header Authorization inválido ou ausente");
                System.out.println("=== 🔐 JWT FILTER FINALIZADO (sem token) ===");
                filterChain.doFilter(request, response);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("🔍 Buscando UserDetails para: " + username);
                
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("✅ UserDetails carregado: " + userDetails.getUsername());
                    
                    // 🔥 VALIDAR TOKEN
                    System.out.println("🔍 Validando token...");
                    boolean isTokenValid = jwtService.validateToken(token, userDetails);
                    System.out.println("✅ Token válido: " + isTokenValid);
                    
                    if (isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("🎉 USUÁRIO AUTENTICADO COM SUCESSO: " + username);
                    } else {
                        System.err.println("❌ Token inválido na validação");
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write("Token inválido");
                        return;
                    }
                } catch (UsernameNotFoundException e) {
                    System.err.println("❌ Usuário não encontrado no banco: " + username);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Usuário não encontrado");
                    return;
                } catch (Exception e) {
                    System.err.println("❌ Erro ao carregar UserDetails: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.getWriter().write("Erro ao carregar usuário");
                    return;
                }
            } else {
                if (username == null) {
                    System.err.println("❌ Username é NULL - não foi possível extrair do token");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("ℹ️ Já existe autenticação no contexto");
                }
            }
            
            System.out.println("=== 🔐 JWT FILTER FINALIZADO COM SUCESSO ===");
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            System.err.println("❌ Token expirado: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token expirado");
            return;
        } catch (MalformedJwtException e) {
            System.err.println("❌ Token malformado: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token inválido");
            return;
        } catch (SignatureException e) {
            System.err.println("❌ Assinatura do token inválida: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token com assinatura inválida");
            return;
        } catch (UnsupportedJwtException e) {
            System.err.println("❌ Token não suportado: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token não suportado");
            return;
        } catch (Exception e) {
            System.err.println("❌ Erro geral no JWT Filter: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Erro na autenticação: " + e.getMessage());
            return;
        }
    }
}