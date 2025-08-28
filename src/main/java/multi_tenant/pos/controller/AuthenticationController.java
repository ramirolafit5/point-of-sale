package multi_tenant.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import multi_tenant.pos.dto.AuthenticationRequestDTO;
import multi_tenant.pos.dto.RegisterUserDTO;
import multi_tenant.pos.dto.UserDTO;
import multi_tenant.pos.dto.UserTokenDTO;
import multi_tenant.pos.service.UserService;

/**
 * Controlador para la gestión de autenticación y registro de usuarios.
 */
@RestController
@RequestMapping("/api/autenticacion")
public class AuthenticationController {
    
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UserDTO> registrarUsuario(@Valid @RequestBody RegisterUserDTO registroDTO) {
        UserDTO userDto = userService.registrarUsuario(registroDTO);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenDTO> autenticarUsuario(@Valid @RequestBody AuthenticationRequestDTO autenticacionDTO) {
        UserTokenDTO respuesta = userService.autenticarYGenerarToken(autenticacionDTO);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> obtenerUsuarioActual() {
        UserDTO usuarioActual = userService.obtenerUsuarioAutenticado();
        return ResponseEntity.ok(usuarioActual);
    }

}
