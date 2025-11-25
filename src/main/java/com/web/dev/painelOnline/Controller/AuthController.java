package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.Usuario;
import com.web.dev.painelOnline.services.UsuarioService;
import com.web.dev.painelOnline.services.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String senha = credentials.get("senha");

            if (email == null || senha == null) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("erro", "Email e senha são obrigatórios");
                return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
            }

            Usuario usuario = usuarioService.login(email, senha);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            String token = jwtUtils.generateToken(
                    userDetails,
                    usuario.getId(),
                    usuario.getTipoUsuario().name()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("tipoUsuario", usuario.getTipoUsuario());
            response.put("mensagem", "Login realizado com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }
    }
}
