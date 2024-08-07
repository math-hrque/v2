package br.com.lol.lol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class EnderecoApiDTO {
    @JsonProperty("cep")
    @Setter @Getter
    private String cep;

    @JsonProperty("uf")
    @Setter @Getter
    private String uf;

    @JsonProperty("localidade")
    @Setter @Getter
    private String cidade;

    @JsonProperty("bairro")
    @Setter @Getter
    private String bairro;

    @JsonProperty("logradouro")
    @Setter @Getter
    private String rua;
}
