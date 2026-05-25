# Sistema de Chamados de TI - Java Swing

Projeto acadêmico em Java para simular atendimento de chamados de suporte técnico.

## Pré-requisitos
- É necessário possuir o Java SDK instalado no computador.

## Logins iniciais

- Administrador: `admin` / `admin`
- Solicitante: `solicitante` / `solicitante`

## Como executar pelo terminal

Entre na pasta do projeto e rode:

```bash
java -jar SistemaChamadosTI.jar
```

## Arquivos locais

Ao executar, o sistema cria automaticamente a pasta `data` com:

- `usuarios.txt`
- `chamados.txt`
- `historicos.txt`

## Estruturas de dados utilizadas

- Fila: chamados com status Aberto, aguardando atendimento.
- Pilha: histórico/timeline das ações do chamado, do mais recente para o mais antigo.
- Lista Ligada: armazenamento em memória dos chamados.
- Árvore Binária: busca de chamados por ID.
