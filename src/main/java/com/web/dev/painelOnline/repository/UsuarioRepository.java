package com.web.dev.painelOnline.repository;

import com.web.dev.painelOnline.entities.Usuario;
import com.web.dev.painelOnline.Enum.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);

    List<Usuario> findByAtivo(Boolean ativo);

    List<Usuario> findByTipoUsuarioAndAtivo(TipoUsuario tipoUsuario, Boolean ativo);
}