package com.web.dev.painelOnline.services;

import com.web.dev.painelOnline.entities.Usuario;
import com.web.dev.painelOnline.Enum.TipoUsuario;
import com.web.dev.painelOnline.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Cadastra novo usuário
    public Usuario cadastrarUsuario(Usuario usuario) {
        validarDadosUsuario(usuario);

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }

        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario(TipoUsuario.USUARIO);
        }

        return usuarioRepository.save(usuario);
    }

    // Login do usuário
    @Transactional(readOnly = true)
    public Usuario login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }

        // Usar passwordEncoder.matches() quando implementar JWT
        if (!usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        return usuario;
    }

    // Atualiza dados do usuário
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado, Long idUsuarioLogado, TipoUsuario tipoUsuarioLogado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();

        // Verifica permissões
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE && !idUsuarioLogado.equals(id)) {
            throw new IllegalArgumentException("Você não tem permissão para editar este usuário");
        }

        // Atualiza campos permitidos
        if (usuarioAtualizado.getNome() != null) {
            usuario.setNome(usuarioAtualizado.getNome());
        }

        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                throw new IllegalArgumentException("Email já cadastrado no sistema");
            }
            usuario.setEmail(usuarioAtualizado.getEmail());
        }

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            // Encriptar senha quando implementar JWT
            usuario.setSenha(usuarioAtualizado.getSenha());
        }

        // Apenas DEMANDANTE pode alterar tipo de usuário
        if (tipoUsuarioLogado == TipoUsuario.DEMANDANTE && usuarioAtualizado.getTipoUsuario() != null) {
            usuario.setTipoUsuario(usuarioAtualizado.getTipoUsuario());
        }

        return usuarioRepository.save(usuario);
    }

    // Exclui usuário (apenas DEMANDANTE)
    public void excluirUsuario(Long id, TipoUsuario tipoUsuarioLogado) {
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE) {
            throw new IllegalArgumentException("Apenas demandantes podem excluir usuários");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        usuarioRepository.deleteById(id);
    }

    // Desativa usuário (apenas DEMANDANTE)
    public Usuario desativarUsuario(Long id, TipoUsuario tipoUsuarioLogado) {
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE) {
            throw new IllegalArgumentException("Apenas demandantes podem desativar usuários");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    // Reativa usuário (apenas DEMANDANTE)
    public Usuario reativarUsuario(Long id, TipoUsuario tipoUsuarioLogado) {
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE) {
            throw new IllegalArgumentException("Apenas demandantes podem reativar usuários");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    // Busca todos os usuários (apenas DEMANDANTE)
    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosUsuarios(TipoUsuario tipoUsuarioLogado) {
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE) {
            throw new IllegalArgumentException("Apenas demandantes podem listar todos os usuários");
        }
        return usuarioRepository.findAll();
    }

    // Busca usuário por ID
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Long id, Long idUsuarioLogado, TipoUsuario tipoUsuarioLogado) {
        if (tipoUsuarioLogado != TipoUsuario.DEMANDANTE && !idUsuarioLogado.equals(id)) {
            throw new IllegalArgumentException("Você não tem permissão para visualizar este usuário");
        }
        return usuarioRepository.findById(id);
    }

    // Busca usuário por email
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Validações
    private void validarDadosUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }
    }
}