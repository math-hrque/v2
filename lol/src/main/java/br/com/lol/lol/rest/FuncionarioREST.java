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

import br.com.lol.lol.dto.FuncionarioDTO;
import br.com.lol.lol.model.Funcionario;
import br.com.lol.lol.service.FuncionarioService;

@CrossOrigin
@RestController
@RequestMapping("/funcionario")
public class FuncionarioREST {

    @Autowired
    private FuncionarioService funcionarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<FuncionarioDTO> cadastrar(@RequestBody Funcionario funcionario) {
        return funcionarioService.cadastrar(funcionario);
    }

    @PutMapping("/atualizar/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable("idFuncionario") Long idFuncionario, @RequestBody Funcionario funcionario) {
        return funcionarioService.atualizar(idFuncionario, funcionario);
    }

    @DeleteMapping("/remover/{idFuncionario}")
    public ResponseEntity<FuncionarioDTO> remover(@PathVariable("idFuncionario") Long idFuncionario) {
        return funcionarioService.remover(idFuncionario);
    }

    @GetMapping("/consultar/{idUsuario}")
    public ResponseEntity<FuncionarioDTO> consultar(@PathVariable("idUsuario") Long idUsuario) {
        return funcionarioService.consultar(idUsuario);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<FuncionarioDTO>> listar() {
        return funcionarioService.listar();
    }
    
}
