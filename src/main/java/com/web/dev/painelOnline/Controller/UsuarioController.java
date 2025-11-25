package com.web.dev.painelOnline.Controller;

import com.web.dev.painelOnline.entities.Usuario;
import com.web.dev.painelOnline.Enum.TipoUsuario;
import com.web.dev.painelOnline.services.UsuarioService;
import com.web.dev.painelOnline.services.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, Object>> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
            Map<String, Object> response = criarResponseUsuario(novoUsuario);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao cadastrar usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

            Map<String, Object> response = criarResponseUsuario(usuario);
            response.put("token", token);
            response.put("mensagem", "Login realizado com sucesso");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao realizar login: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios(@RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            List<Usuario> usuarios = usuarioService.buscarTodosUsuarios(tipo);

            List<Map<String, Object>> response = new ArrayList<>();

            for (Usuario usuario : usuarios) {
                response.add(criarResponseUsuario(usuario));
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("erro", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap(
                            "erro",
                            "Erro ao listar usuários: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarUsuarioPorId(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long idUsuarioLogado,
            @RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            Optional<Usuario> usuarioOpt = usuarioService.buscarUsuarioPorId(id, idUsuarioLogado, tipo);

            if (usuarioOpt.isPresent()) {
                Map<String, Object> response = criarResponseUsuario(usuarioOpt.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao buscar usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuario,
            @RequestHeader("X-Usuario-Id") Long idUsuarioLogado,
            @RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario, idUsuarioLogado, tipo);

            Map<String, Object> response = criarResponseUsuario(usuarioAtualizado);
            response.put("mensagem", "Usuário atualizado com sucesso");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao atualizar usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> excluirUsuario(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            usuarioService.excluirUsuario(id, tipo);

            Map<String, Object> sucesso = new HashMap<>();
            sucesso.put("mensagem", "Usuário excluído com sucesso");
            return new ResponseEntity<>(sucesso, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao excluir usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Map<String, Object>> desativarUsuario(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            Usuario usuario = usuarioService.desativarUsuario(id, tipo);

            Map<String, Object> response = criarResponseUsuario(usuario);
            response.put("mensagem", "Usuário desativado com sucesso");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao desativar usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Map<String, Object>> reativarUsuario(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Tipo") String tipoUsuario) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario);
            Usuario usuario = usuarioService.reativarUsuario(id, tipo);

            Map<String, Object> response = criarResponseUsuario(usuario);
            response.put("mensagem", "Usuário reativado com sucesso");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao reativar usuário: " + e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> criarResponseUsuario(Usuario usuario) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", usuario.getId());
        response.put("nome", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("tipoUsuario", usuario.getTipoUsuario());
        response.put("ativo", usuario.getAtivo());
        response.put("dataCriacao", usuario.getDataCriacao());
        response.put("dataAtualizacao", usuario.getDataAtualizacao());
        return response;
    }
}
