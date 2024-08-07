package br.com.lol.lol.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lol.lol.dto.PedidoDTO;
import br.com.lol.lol.service.PedidoService;

@CrossOrigin
@RestController
@RequestMapping("/pedido")
public class PedidoREST {

    @Autowired
    PedidoService pedidoService;

    @PostMapping("/cadastrar")
    public ResponseEntity<PedidoDTO> cadastrar(@RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.cadastrar(pedidoDTO);
    }

    @PutMapping("/atualizarPorCliente/{numeroPedido}")
    public ResponseEntity<PedidoDTO> atualizarPorCliente(@PathVariable("numeroPedido") Long numeroPedido, @RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.atualizarPorCliente(numeroPedido, pedidoDTO);
    }

    @PutMapping("/atualizarPorFuncionario/{numeroPedido}")
    public ResponseEntity<PedidoDTO> atualizarPorFuncionario(@PathVariable("numeroPedido") Long numeroPedido, @RequestBody PedidoDTO pedidoDTO) {
        return pedidoService.atualizarPorFuncionario(numeroPedido, pedidoDTO);
    }

    @GetMapping("/consultar/{numeroPedido}")
    public ResponseEntity<PedidoDTO> consultar(@PathVariable("numeroPedido") Long numeroPedido) {
        return pedidoService.consultar(numeroPedido);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PedidoDTO>> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/listarPorCliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> listarPorCliente(@PathVariable("idCliente") Long idCliente) {
        return pedidoService.listarPorCliente(idCliente);
    }

}
