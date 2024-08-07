package br.com.lol.lol.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.lol.lol.dto.ClienteDTO;
import br.com.lol.lol.dto.ClienteFielDTO;
import br.com.lol.lol.dto.ReceitaDTO;
import br.com.lol.lol.model.Cliente;
import br.com.lol.lol.model.Pedido;
import br.com.lol.lol.repository.ClienteRepository;
import br.com.lol.lol.repository.PedidoRepository;

@Service
public class RelatorioService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    
    public ResponseEntity<List<ReceitaDTO>> visualizarReceitas(@RequestParam("dataDe") LocalDate dataDe, @RequestParam("dataAte") LocalDate dataAte) {
        if (dataAte.isBefore(dataDe)) {
            return ResponseEntity.badRequest().build();
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            OffsetDateTime dataDeOffset = dataDe.atStartOfDay(zoneId).toOffsetDateTime();
            OffsetDateTime dataAteOffset = dataAte.atTime(LocalTime.MAX).atOffset(ZoneOffset.from(zoneId.getRules().getOffset(LocalDateTime.now())));
            Optional<List<Pedido>> listaPedidoBD = pedidoRepository.findByDataPagamentoBetween(dataDeOffset, dataAteOffset);
            if (listaPedidoBD.isPresent()) {
                Map<LocalDate, Double> receitasMap = listaPedidoBD.get().stream().collect(Collectors.groupingBy(pedido -> 
                    pedido.getDataPagamento().toLocalDate(),Collectors.summingDouble(pedido -> pedido.getOrcamento().getValor())
                ));

                List<LocalDate> listaData = Stream.iterate(dataDe, data -> !data.isAfter(dataAte), data -> data.plusDays(1)).collect(Collectors.toList());
                Map<LocalDate, Double> periodoMap = listaData.stream().collect(Collectors.toMap(data -> data,data -> receitasMap.getOrDefault(data, 0.0)));

                List<ReceitaDTO> listaReceitaDTO = periodoMap.entrySet().stream().map(entry -> {
                    ReceitaDTO receitaDTO = new ReceitaDTO();
                    receitaDTO.setData(entry.getKey());
                    receitaDTO.setReceita(entry.getValue());
                    return receitaDTO;
                }).sorted(Comparator.comparing(ReceitaDTO::getData)).collect(Collectors.toList());
    
                return ResponseEntity.ok(listaReceitaDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
    }

    public ResponseEntity<List<ClienteDTO>> visualizarClientes() {
        List<Cliente> listaClienteBD = clienteRepository.findAll();
        if (listaClienteBD.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<ClienteDTO> listaClienteDTO = listaClienteBD.stream().map(cliente -> {
                ClienteDTO clienteDTO = new ClienteDTO(cliente);
                return clienteDTO;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(listaClienteDTO);
        }
    }

    public ResponseEntity<List<ClienteFielDTO>> visualizarClientesFieis() {
        List<Pedido> listaPedidoBD = pedidoRepository.findAll();
        if (listaPedidoBD.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<Pedido> listaPedidosPagosMap = listaPedidoBD.stream().filter(pedido -> pedido.getDataPagamento() != null).collect(Collectors.toList());
            Map<Cliente, List<Pedido>> clienteListaPedidosPagosMap = listaPedidosPagosMap.stream().collect(Collectors.groupingBy(Pedido::getCliente));
            
            List<ClienteFielDTO> listaClienteFiel = clienteListaPedidosPagosMap.entrySet().stream().map(entry -> {
                Cliente cliente = entry.getKey();
                List<Pedido> pedidos = entry.getValue();
                int quantidadePedidos = pedidos.size();
                Double receita = pedidos.stream().mapToDouble(pedido -> pedido.getOrcamento().getValor()).sum();
                ClienteDTO clienteDTO = new ClienteDTO(cliente);
                ClienteFielDTO clienteFielDTO = new ClienteFielDTO();
                clienteFielDTO.setCliente(clienteDTO);
                clienteFielDTO.setQuantidadePedidos(quantidadePedidos);
                clienteFielDTO.setReceita(receita);
                return clienteFielDTO;
            }).sorted(Comparator.comparingInt(ClienteFielDTO::getQuantidadePedidos).reversed()).limit(3).collect(Collectors.toList());

            return ResponseEntity.ok(listaClienteFiel);
        }
    }

}
