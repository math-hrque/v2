package br.com.lol.lol.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.lol.lol.enums.TipoPermissao;
import br.com.lol.lol.model.Permissao;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long> {
    Optional<Permissao> findByTipoPermissao(TipoPermissao tipoPermissao);
}
