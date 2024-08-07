package br.com.lol.lol.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="cliente")
@NoArgsConstructor
@AllArgsConstructor
public class Cliente implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_cliente")
    @Setter @Getter
    private Long idCliente;

    @Column(name="cpf", nullable = false, unique = true)
    @Setter @Getter
    private String cpf;

    @Column(name="telefone", nullable = false)
    @Setter @Getter
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario", nullable = false)
    @Setter @Getter
    private Usuario usuario;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="id_endereco", nullable = false)
    @Setter @Getter
    private Endereco endereco;

    public Cliente(Long idCliente) {
        this.idCliente = idCliente;
    }
}
