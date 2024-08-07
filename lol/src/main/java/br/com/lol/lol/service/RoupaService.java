package br.com.lol.lol.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lol.lol.dto.RoupaDTO;
import br.com.lol.lol.model.Roupa;
import br.com.lol.lol.repository.RoupaRepository;

@Service
public class RoupaService {

    @Autowired
    private RoupaRepository roupaRepository;
    
    public ResponseEntity<RoupaDTO> cadastrar(@RequestBody RoupaDTO roupaDTO) {
        if (validaDadosCadastrarRoupa(roupaDTO)) {
            Optional<Roupa> roupaBD = roupaRepository.findById(roupaDTO.getIdRoupa());
            if (roupaBD.isPresent()) {
                RoupaDTO roupaDTOExistente = new RoupaDTO(roupaBD.get());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(roupaDTOExistente);
            } else {
                Roupa roupa = new Roupa();
                roupa.cadastrar(roupaDTO);
                Roupa roupaSalva = roupaRepository.save(roupa);
                RoupaDTO roupaDTOSalva = new RoupaDTO(roupaSalva);
                return ResponseEntity.status(HttpStatus.CREATED).body(roupaDTOSalva);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<RoupaDTO> atualizar(@PathVariable("idRoupa") Long idRoupa, @RequestBody RoupaDTO roupaDTO) {
        return roupaRepository.findById(idRoupa).map(roupaBD -> {
            Roupa roupa = new Roupa();
            roupa.atualizar(idRoupa, roupaDTO);
            Roupa roupaSalva = roupaRepository.save(roupa);
            RoupaDTO roupaDTOSalva = new RoupaDTO(roupaSalva);
            return ResponseEntity.ok(roupaDTOSalva);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<RoupaDTO> inativar(@PathVariable("idRoupa") Long idRoupa) {
        return roupaRepository.findById(idRoupa).map(roupaBD -> {
            roupaBD.inativar();
            Roupa roupaInativada = roupaRepository.save(roupaBD);
            RoupaDTO roupaDTOInativada = new RoupaDTO(roupaInativada);
            return ResponseEntity.ok(roupaDTOInativada);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public boolean validaDadosCadastrarRoupa(RoupaDTO roupaDTO) {
        boolean idRoupaValido = roupaDTO.getIdRoupa() == 0;
        boolean descricaoValida = !roupaDTO.getDescricao().isEmpty();
        return idRoupaValido && descricaoValida;
    }

    public ResponseEntity<List<RoupaDTO>> listar() {
        Optional<List<Roupa>> listaRoupaBD = roupaRepository.findByAtivo(true);
        if (listaRoupaBD.isPresent()) {
            List<RoupaDTO> listaRoupaDTO = listaRoupaBD.get().stream().map(roupa -> new RoupaDTO(roupa)).collect(Collectors.toList());
            return ResponseEntity.ok(listaRoupaDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
