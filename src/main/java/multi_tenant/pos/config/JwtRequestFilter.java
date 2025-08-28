package multi_tenant.pos.config;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import multi_tenant.pos.model.enums.Rol;
import multi_tenant.pos.util.JwtUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        String jwt = extractJwtFromHeader(header);

        if (jwt != null) {
            try {
                Claims claims = jwtUtil.extractAllClaims(jwt);
                String username = claims.getSubject();
                String rolStr = claims.get("rol", String.class);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Rol rol = Rol.valueOf(rolStr);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol.name());

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(authority)
                            );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                log.warn("Token JWT expirado: {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("Error al procesar JWT: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromHeader(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
