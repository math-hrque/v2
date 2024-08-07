package br.com.lol.lol.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.lol.lol.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(Long numeroPedido);
    Optional<List<Pedido>> findByClienteIdCliente(Long idCliente);
    Optional<List<Pedido>> findByClienteUsuarioIdUsuario(Long idUsuario);
    Optional<List<Pedido>> findByDataPagamentoBetween(OffsetDateTime dataDe, OffsetDateTime dataAte);
    @Query("SELECT COALESCE(MAX(p.numeroPedido), 0) FROM Pedido p")
    Optional<Long> findMaxNumeroPedido();
}
