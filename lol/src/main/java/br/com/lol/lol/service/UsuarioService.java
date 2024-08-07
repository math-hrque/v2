package br.com.lol.lol.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.lol.lol.dto.UsuarioRequestDTO;
import br.com.lol.lol.dto.UsuarioResponseDTO;
import br.com.lol.lol.model.Usuario;
import br.com.lol.lol.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        if (validaDadosLogin(usuarioRequestDTO)) {
            usuarioRequestDTO.setEmail(usuarioRequestDTO.getEmail().toLowerCase());
            Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuarioRequestDTO.getEmail());
            if (usuarioBD.isPresent()) {
                if (passwordEncoder.matches(usuarioRequestDTO.getSenha(), usuarioBD.get().getSenha())) {
                    UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(usuarioBD.get());
                    return ResponseEntity.ok(usuarioResponseDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public boolean validaDadosLogin(UsuarioRequestDTO usuarioRequestDTO) {
        boolean emailValido = !usuarioRequestDTO.getEmail().isEmpty();
        boolean senhaValida = !usuarioRequestDTO.getSenha().isEmpty();
        return emailValido && senhaValida;
    }

}
