package multi_tenant.pos.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import multi_tenant.pos.dto.AuthenticationRequestDTO;
import multi_tenant.pos.dto.RegisterUserDTO;
import multi_tenant.pos.dto.UserDTO;
import multi_tenant.pos.dto.UserTokenDTO;
import multi_tenant.pos.handler.DuplicateResourceException;
import multi_tenant.pos.handler.ResourceNotFoundException;
import multi_tenant.pos.handler.UnauthorizedException;
import multi_tenant.pos.mapper.UserMapper;
import multi_tenant.pos.model.Store;
import multi_tenant.pos.model.User;
import multi_tenant.pos.repository.StoreRepository;
import multi_tenant.pos.repository.UserRepository;
import multi_tenant.pos.util.JwtUtil;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, 
                       @Lazy AuthenticationManager authenticationManager, 
                       JwtUtil jwtUtil, 
                       @Lazy PasswordEncoder passwordEncoder,
                       StoreRepository storeRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.storeRepository = storeRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserTokenDTO autenticarYGenerarToken(AuthenticationRequestDTO requestDTO) {
        try {
            // Spring Security se encarga de validar username y password
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.getUsername(),
                            requestDTO.getPassword()
                    )
            );

            // Obtenemos el usuario autenticado
            User usuario = (User) auth.getPrincipal();

            // Mapeamos entidad → DTO (rol enum → String)
            UserTokenDTO userTokenDTO = userMapper.toUserTokenDTO(usuario);

            // Generamos el JWT y lo asignamos al DTO
            String token = jwtUtil.generateToken(userTokenDTO);
            userTokenDTO.setToken(token);

            return userTokenDTO;

        } catch (BadCredentialsException ex) {
            // Usuario o contraseña incorrectos
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return usuario;
    }

    @Transactional
    public UserDTO registrarUsuario(RegisterUserDTO registroDTO) {
        /* User currentUser = getCurrentUser();

        if (currentUser.getRol() != Rol.ADMIN) {
            throw new UnauthorizedException("No tienes permisos para crear usuarios.");
        } */

        Store store = storeRepository.findById(registroDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        if (userRepository.existsByUsername(registroDTO.getUsername())) {
            throw new DuplicateResourceException("El nombre de usuario ya está en uso.");
        }

        User user = userMapper.fromDTO(registroDTO);
        user.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        user.setStore(store);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    // Devuelve la entidad completa
    public User getCurrentUser() {
        return fetchAuthenticatedUser();
    }

    // Devuelve un DTO para exponer al front-end
    public UserDTO obtenerUsuarioAutenticado() {
        return userMapper.toDTO(fetchAuthenticatedUser());
    }

    // Metodo privado para la busqueda del usuario autenticado

    private User fetchAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("No hay un usuario autenticado");
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

















    
    /* public UserDTO obtenerUsuarioAutenticado() {
        // Tomamos solo el username del SecurityContext
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // devuelve el username incluso si es "anonymousUser"

        if (username == null || username.equals("anonymousUser")) {
            throw new UnauthorizedException("No hay un usuario autenticado");
        }

        // Buscar usuario en DB
        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return userMapper.toDTO(usuario);
    } */

    /*     public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new UnauthorizedException("No hay un usuario autenticado");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    } */

}
