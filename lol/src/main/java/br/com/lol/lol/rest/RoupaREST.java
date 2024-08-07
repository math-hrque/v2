package br.com.lol.lol.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lol.lol.dto.RoupaDTO;
import br.com.lol.lol.service.RoupaService;

@CrossOrigin
@RestController
@RequestMapping("/roupa")
public class RoupaREST {

    @Autowired
    private RoupaService roupaService;

    @PostMapping("/cadastrar")
    public ResponseEntity<RoupaDTO> cadastrar(@RequestBody RoupaDTO roupaDTO) {
        return roupaService.cadastrar(roupaDTO);
    }

    @PutMapping("/atualizar/{idRoupa}")
    public ResponseEntity<RoupaDTO> atualizar(@PathVariable("idRoupa") Long idRoupa, @RequestBody RoupaDTO roupaDTO) {
        return roupaService.atualizar(idRoupa, roupaDTO);
    }

    @DeleteMapping("/inativar/{idRoupa}")
    public ResponseEntity<RoupaDTO> inativar(@PathVariable("idRoupa") Long idRoupa) {
        return roupaService.inativar(idRoupa);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RoupaDTO>> listar() {
        return roupaService.listar();
    }

}
