CREATE TABLE pedido (
	id_pedido INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	numero_pedido INTEGER NOT NULL UNIQUE,
	data_pedido TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
	data_pagamento TIMESTAMPTZ,
	id_cliente INTEGER NOT NULL,
	id_situacao INTEGER NOT NULL,
	id_orcamento INTEGER NOT NULL,
	CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
	CONSTRAINT fk_situacao FOREIGN KEY (id_situacao) REFERENCES situacao(id_situacao),
	CONSTRAINT fk_orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamento(id_orcamento)
);