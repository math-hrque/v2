package br.com.lol.lol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import br.com.lol.lol.dto.EnderecoApiDTO;
import br.com.lol.lol.dto.EnderecoDTO;

@Service
public class EnderecoService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CEP_PATTERN = "\\d{8}";

    public ResponseEntity<EnderecoDTO> consultar(@PathVariable("cep") String cep) {
        if (cep.matches(CEP_PATTERN)) {
            try {
                String url = "https://viacep.com.br/ws/" + cep + "/json/";
                EnderecoApiDTO enderecoApiDTO = restTemplate.getForObject(url, EnderecoApiDTO.class);

                if (enderecoApiDTO.getCep() == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                
                EnderecoDTO enderecoDTO = new EnderecoDTO(enderecoApiDTO);
                return new ResponseEntity<>(enderecoDTO, HttpStatus.OK);
    
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}
