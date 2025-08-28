package multi_tenant.pos.util;

import org.springframework.stereotype.Component;

import multi_tenant.pos.repository.UserRepository;

@Component
public class AuthUtil {

    private final UserRepository userRepository;

    public AuthUtil (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /* 
     * Obtenemos el usuario autenticado
     */
/*     public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
    } */
}
