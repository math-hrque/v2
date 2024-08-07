package br.com.lol.lol.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lol.lol.dto.EnderecoDTO;
import br.com.lol.lol.service.EnderecoService;

@CrossOrigin
@RestController
@RequestMapping("/endereco")
public class EnderecoREST {

    @Autowired
    EnderecoService enderecoService;

    @GetMapping("/consultar/{cep}")
    public ResponseEntity<EnderecoDTO> consultar(@PathVariable("cep") String cep) {
        return enderecoService.consultar(cep);
    }
    
}
