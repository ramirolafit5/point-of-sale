package multi_tenant.pos.service;

import org.springframework.context.annotation.Lazy;
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
import multi_tenant.pos.handler.ConflictException;
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

    /**
     * Metodo para autenticar y generar un token
      */
    public UserTokenDTO autenticarYGenerarToken(AuthenticationRequestDTO requestDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.getUsername(),
                            requestDTO.getPassword()
                    )
            );

            // Obtener el usuario autenticado directamente del Authentication
            User usuario = (User) auth.getPrincipal();

            // Construir DTO
            UserTokenDTO userTokenDTO = buildUserTokenDTO(usuario);

            // Generar token JWT
            String token = jwtUtil.generateToken(userTokenDTO);
            userTokenDTO.setToken(token);

            return userTokenDTO;

        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
        }
    }


    private UserTokenDTO buildUserTokenDTO(User usuario) {
        UserTokenDTO dto = new UserTokenDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setRol(usuario.getRol().name()); // Enum convertido a String
        return dto;
    }

    /**
     * Metodo para 
      */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return usuario;
    }

    /**
     * Metodo para registrar un nuevo usuario (movido desde AuthService)
     */
    @Transactional
    public UserDTO registrarUsuario(RegisterUserDTO registroDTO) {
        // Obtener el usuario autenticado actual y sus roles
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        if (authUser == null || !authUser.isAuthenticated()) {
            throw new UnauthorizedException("No estás autenticado para realizar esta acción.");
        }

        Store store = storeRepository.findById(registroDTO.getStoreId())
        .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        if (userRepository.existsByUsername(registroDTO.getUsername())) {
            throw new ConflictException("El nombre de usuario '" + registroDTO.getUsername() + "' ya está en uso.");
        }

        User user = userMapper.fromDTO(registroDTO);          // Convierte DTO → entidad
        user.setPassword(passwordEncoder.encode(registroDTO.getPassword())); // Encode de password
        user.setStore(store);
        User savedUser = userRepository.save(user);   // Guarda en BD

        return userMapper.toDTO(savedUser);           // Convierte entidad → DTO para response
    }

    public UserDTO obtenerUsuarioAutenticado() {
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
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new UnauthorizedException("No hay un usuario autenticado");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
