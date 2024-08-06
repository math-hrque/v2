package br.com.lol.lol.rest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import br.com.lol.lol.enums.TipoSituacao;
import br.com.lol.lol.model.Cliente;
import br.com.lol.lol.model.Pedido;
import br.com.lol.lol.model.Situacao;
import br.com.lol.lol.repository.ClienteRepository;
import br.com.lol.lol.repository.PedidoRepository;
import br.com.lol.lol.repository.SituacaoRepository;

@CrossOrigin
@RestController
@RequestMapping("/pedido")
public class PedidoREST {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SituacaoRepository situacaoRepository;

    @PostMapping("/cadastrar")
    public ResponseEntity<PedidoDTO> cadastrar(@RequestBody PedidoDTO pedidoDTO) {
        if (validaDadosCadastrarPedido(pedidoDTO)) {
            Optional<Cliente> cliente = clienteRepository.findById(pedidoDTO.getIdCliente());
            if (cliente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                if (pedidoDTO.getOrcamento().isAprovado()) {
                    Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(TipoSituacao.EM_ABERTO);
                    if (situacao.isPresent()) {
                        Pedido pedido = new Pedido();
                        pedido.cadastrar(pedidoDTO, situacao.get());
                        Optional<Long> ultimoNumeroPedido = pedidoRepository.findMaxNumeroPedido();
                        if (ultimoNumeroPedido.isPresent()) {
                            pedido.setNumeroPedido(ultimoNumeroPedido.get() + 1L);
                        } else {
                            pedido.setNumeroPedido(10000L);
                        }
                        Pedido pedidoSalva = pedidoRepository.save(pedido);
                        PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else if (!pedidoDTO.getOrcamento().isAprovado()) {
                    Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(TipoSituacao.REJEITADO);
                    if (situacao.isPresent()) {
                        Pedido pedido = new Pedido();
                        pedido.cadastrar(pedidoDTO, situacao.get());
                        Optional<Long> ultimoNumeroPedido = pedidoRepository.findMaxNumeroPedido();
                        if (ultimoNumeroPedido.isPresent()) {
                            pedido.setNumeroPedido(ultimoNumeroPedido.get() + 1L);
                        } else {
                            pedido.setNumeroPedido(10000L);
                        }
                        Pedido pedidoSalva = pedidoRepository.save(pedido);
                        PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public boolean validaDadosCadastrarPedido(PedidoDTO pedidoDTO) {
        boolean numeroPedidoValido = pedidoDTO.getNumeroPedido() == 0;
        boolean idClienteValido = pedidoDTO.getIdCliente() != 0;
        boolean listaPedidoRoupasValida = !pedidoDTO.getListaPedidoRoupas().isEmpty();
        return numeroPedidoValido && idClienteValido && listaPedidoRoupasValida;
    }

    @PutMapping("/atualizarPorCliente/{numeroPedido}")
    public ResponseEntity<PedidoDTO> atualizarPorCliente(@PathVariable("numeroPedido") Long numeroPedido, @RequestBody PedidoDTO pedidoDTO) {
        if (validaDadosAtualizarPedidoPorCliente(pedidoDTO)) {
            Optional<Pedido> pedidoBD = pedidoRepository.findByNumeroPedido(numeroPedido);
            if (pedidoBD.isPresent()) {
                switch (pedidoDTO.getSituacao()) {
                    case CANCELADO:
                        if (pedidoBD.get().getSituacao().getTipoSituacao().equals(TipoSituacao.EM_ABERTO)) {
                            Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(pedidoDTO.getSituacao());
                            if (situacao.isPresent()) {
                                pedidoBD.get().setSituacao(situacao.get());
                                Pedido pedidoSalva = pedidoRepository.save(pedidoBD.get());
                                PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                            } else {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    case PAGO:
                        if (pedidoBD.get().getSituacao().getTipoSituacao().equals(TipoSituacao.AGUARDANDO_PAGAMENTO)) {
                            Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(pedidoDTO.getSituacao());
                            if (situacao.isPresent()) {
                                pedidoBD.get().pagar(situacao.get());
                                Pedido pedidoSalva = pedidoRepository.save(pedidoBD.get());
                                PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                            } else {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    default:
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public boolean validaDadosAtualizarPedidoPorCliente(PedidoDTO pedidoDTO) {
        boolean numeroPedidoValido = pedidoDTO.getNumeroPedido() != 0;
        boolean situacaoValida = !pedidoDTO.getSituacao().toString().isEmpty();
        return numeroPedidoValido && situacaoValida;
    }

    @PutMapping("/atualizarPorFuncionario/{numeroPedido}")
    public ResponseEntity<PedidoDTO> atualizarPorFuncionario(@PathVariable("numeroPedido") Long numeroPedido, @RequestBody PedidoDTO pedidoDTO) {
        if (validaDadosAtualizarPedidoPorFuncionario(pedidoDTO)) {
            Optional<Pedido> pedidoBD = pedidoRepository.findByNumeroPedido(numeroPedido);
            if (pedidoBD.isPresent()) {
                switch (pedidoDTO.getSituacao()) {
                    case RECOLHIDO:
                        if (pedidoBD.get().getSituacao().getTipoSituacao().equals(TipoSituacao.EM_ABERTO)) {
                            Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(pedidoDTO.getSituacao());
                            if (situacao.isPresent()) {
                                pedidoBD.get().setSituacao(situacao.get());
                                Pedido pedidoSalva = pedidoRepository.save(pedidoBD.get());
                                PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    case AGUARDANDO_PAGAMENTO:
                        if (pedidoBD.get().getSituacao().getTipoSituacao().equals(TipoSituacao.RECOLHIDO)) {
                            Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(pedidoDTO.getSituacao());
                            if (situacao.isPresent()) {
                                pedidoBD.get().setSituacao(situacao.get());
                                Pedido pedidoSalva = pedidoRepository.save(pedidoBD.get());
                                PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    case FINALIZADO:
                        if (pedidoBD.get().getSituacao().getTipoSituacao().equals(TipoSituacao.PAGO)) {
                            Optional<Situacao> situacao = situacaoRepository.findByTipoSituacao(pedidoDTO.getSituacao());
                            if (situacao.isPresent()) {
                                pedidoBD.get().setSituacao(situacao.get());
                                Pedido pedidoSalva = pedidoRepository.save(pedidoBD.get());
                                PedidoDTO pedidoDTOSalva = new PedidoDTO(pedidoSalva);
                                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTOSalva);
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                    default:
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public boolean validaDadosAtualizarPedidoPorFuncionario(PedidoDTO pedidoDTO) {
        boolean numeroPedidoValido = pedidoDTO.getNumeroPedido() != 0;
        boolean situacaoValida = !pedidoDTO.getSituacao().toString().isEmpty();
        return numeroPedidoValido && situacaoValida;
    }

    @GetMapping("/consultar/{numeroPedido}")
    public ResponseEntity<PedidoDTO> consultar(@PathVariable("numeroPedido") Long numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido).map(pedidoBD -> {
            PedidoDTO pedidoDTO = new PedidoDTO(pedidoBD);
            return ResponseEntity.ok(pedidoDTO);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PedidoDTO>> listar() {
        List<Pedido> listaPedidoBD = pedidoRepository.findAll();
        if (listaPedidoBD.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<PedidoDTO> listaPedidoDTO = listaPedidoBD.stream().map(pedido -> {
                PedidoDTO pedidoDTO = new PedidoDTO(pedido);
                return pedidoDTO;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(listaPedidoDTO);
        }
    }

    @GetMapping("/listarPorCliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> listarPorCliente(@PathVariable("idCliente") Long idCliente) {
        Optional<List<Pedido>> listaPedidoBD = pedidoRepository.findByClienteIdCliente(idCliente);
        if (listaPedidoBD.get().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<PedidoDTO> listaPedidoDTO = listaPedidoBD.get().stream().map(pedido -> {
                PedidoDTO pedidoDTO = new PedidoDTO(pedido);
                return pedidoDTO;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(listaPedidoDTO);
        }
    }
}
