package br.com.lol.lol.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lol.lol.dto.FuncionarioDTO;
import br.com.lol.lol.enums.TipoPermissao;
import br.com.lol.lol.model.Funcionario;
import br.com.lol.lol.model.Permissao;
import br.com.lol.lol.repository.FuncionarioRepository;
import br.com.lol.lol.repository.PermissaoRepository;

@Service
public class FuncionarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;
    
    public ResponseEntity<FuncionarioDTO> cadastrar(@RequestBody Funcionario funcionario) {
        if (validaDadosCadastrarFuncionario(funcionario)) {
            funcionario.getUsuario().setEmail(funcionario.getUsuario().getEmail().toLowerCase());
            Optional<Funcionario> funcionarioBD = funcionarioRepository.findByUsuarioEmail(funcionario.getUsuario().getEmail());
            if (funcionarioBD.isPresent()) {
                FuncionarioDTO funcionarioDTOExistente = new FuncionarioDTO(funcionarioBD.get());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(funcionarioDTOExistente);
            } else {
                Optional<Permissao> permissao = permissaoRepository.findByTipoPermissao(funcionario.getUsuario().getPermissao().getTipoPermissao());
                if (permissao.isPresent()) {
                    funcionario.getUsuario().setPermissao(permissao.get());
                    funcionario.getUsuario().setSenha(passwordEncoder.encode(funcionario.getUsuario().getSenha()));
                    Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
                    FuncionarioDTO funcionarioDTOSalvo = new FuncionarioDTO(funcionarioSalvo);
                    return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioDTOSalvo);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } 
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable("idFuncionario") Long idFuncionario, @RequestBody Funcionario funcionario) {
        if (validaDadosAtualizarFuncionario(funcionario)) {
            Optional<Funcionario> funcionarioBD = funcionarioRepository.findById(idFuncionario);
            if (funcionarioBD.isPresent()) {
                funcionario.getUsuario().setEmail(funcionario.getUsuario().getEmail().toLowerCase());
                Optional<Funcionario> funcionarioExistente = funcionarioRepository.findByUsuarioEmail(funcionario.getUsuario().getEmail());
                if (funcionarioExistente.isPresent() && !funcionarioExistente.get().getUsuario().getIdUsuario().equals(funcionarioBD.get().getUsuario().getIdUsuario())) {
                    FuncionarioDTO outroFuncionarioDTOExistente = new FuncionarioDTO(funcionarioExistente.get());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(outroFuncionarioDTOExistente);
                } else {
                    funcionario.setIdFuncionario(funcionarioBD.get().getIdFuncionario());
                    funcionario.getUsuario().setIdUsuario(funcionarioBD.get().getUsuario().getIdUsuario());
                    funcionario.getUsuario().setPermissao(funcionarioBD.get().getUsuario().getPermissao());
                    if (funcionario.getUsuario().getSenha() == null || funcionario.getUsuario().getSenha().isEmpty()) {
                        funcionario.getUsuario().setSenha(funcionarioBD.get().getUsuario().getSenha());
                    } else {
                        funcionario.getUsuario().setSenha(passwordEncoder.encode(funcionario.getUsuario().getSenha()));
                    }
                    Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
                    FuncionarioDTO funcionarioDTOSalvo = new FuncionarioDTO(funcionarioSalvo);
                    return ResponseEntity.ok(funcionarioDTOSalvo);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<FuncionarioDTO> remover(@PathVariable("idFuncionario") Long idFuncionario) {
        return funcionarioRepository.findById(idFuncionario).map(funcionarioBD -> {
            funcionarioRepository.delete(funcionarioBD);
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO(funcionarioBD);
            return ResponseEntity.ok(funcionarioDTO);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<FuncionarioDTO> consultar(@PathVariable("idUsuario") Long idUsuario) {
        return funcionarioRepository.findByUsuarioIdUsuario(idUsuario).map(funcionarioBD -> {
            FuncionarioDTO funcionarioDTO = new FuncionarioDTO(funcionarioBD);
            return ResponseEntity.ok(funcionarioDTO);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<List<FuncionarioDTO>> listar() {
        List<Funcionario> listaFuncionarioBD = funcionarioRepository.findAll();
        if (listaFuncionarioBD.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<FuncionarioDTO> listaFuncionarioDTO = listaFuncionarioBD.stream().map(funcionario -> {
                FuncionarioDTO funcionarioDTO = new FuncionarioDTO(funcionario);
                return funcionarioDTO;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(listaFuncionarioDTO);
        }
    }

    public boolean validaDadosCadastrarFuncionario(Funcionario funcionario) {
        boolean idUsuarioValido = funcionario.getUsuario().getIdUsuario() == 0;
        boolean emailValido = !funcionario.getUsuario().getEmail().isEmpty();
        boolean senhaValida = !funcionario.getUsuario().getSenha().isEmpty();
        boolean nomeValido = !funcionario.getUsuario().getNome().isEmpty();
        boolean permissaoValida = funcionario.getUsuario().getPermissao().getTipoPermissao().equals(TipoPermissao.FUNCIONARIO);
        return idUsuarioValido && emailValido && senhaValida && nomeValido && permissaoValida;
    }

    public boolean validaDadosAtualizarFuncionario(Funcionario funcionario) {
        boolean idUsuarioValido = funcionario.getUsuario().getIdUsuario() != 0;
        boolean emailValido = !funcionario.getUsuario().getEmail().isEmpty();
        boolean nomeValido = !funcionario.getUsuario().getNome().isEmpty();
        boolean permissaoValida = funcionario.getUsuario().getPermissao().getTipoPermissao().equals(TipoPermissao.FUNCIONARIO);
        return idUsuarioValido && emailValido && nomeValido && permissaoValida;
    }
    
}
