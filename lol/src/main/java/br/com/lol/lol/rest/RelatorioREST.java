package br.com.lol.lol.rest;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.lol.lol.dto.ClienteFielDTO;
import br.com.lol.lol.dto.ClienteDTO;
import br.com.lol.lol.dto.ReceitaDTO;
import br.com.lol.lol.service.RelatorioService;

@CrossOrigin
@RestController
@RequestMapping("/relatorio")
public class RelatorioREST {

    @Autowired
    RelatorioService relatorioService;

    @GetMapping("/visualizarReceitas")
    public ResponseEntity<List<ReceitaDTO>> visualizarReceitas(@RequestParam("dataDe") LocalDate dataDe, @RequestParam("dataAte") LocalDate dataAte) {
        return relatorioService.visualizarReceitas(dataDe, dataAte);
    }

    @GetMapping("/visualizarClientes")
    public ResponseEntity<List<ClienteDTO>> visualizarClientes() {
        return relatorioService.visualizarClientes();
    }

    @GetMapping("/visualizarClientesFieis")
    public ResponseEntity<List<ClienteFielDTO>> visualizarClientesFieis() {
        return relatorioService.visualizarClientesFieis();
    }

}
