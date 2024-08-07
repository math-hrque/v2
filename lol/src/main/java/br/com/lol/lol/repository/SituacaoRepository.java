package br.com.lol.lol.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.lol.lol.enums.TipoSituacao;
import br.com.lol.lol.model.Situacao;

@Repository
public interface SituacaoRepository extends JpaRepository<Situacao, Long> {
    Optional<Situacao> findByTipoSituacao(TipoSituacao tipoSituacao);
}
