package br.com.lol.lol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.lol.lol.model.Roupa;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoupaRepository extends JpaRepository<Roupa, Long> {
    Optional<List<Roupa>> findByAtivo(boolean ativo);
}
