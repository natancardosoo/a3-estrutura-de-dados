# Sistema de Chamados de TI - Java Swing

Projeto acadêmico em Java para simular atendimento de chamados de suporte técnico.

## Logins iniciais

- Administrador: `admin` / `admin`
- Solicitante: `victor` / `123`

## Como executar pelo terminal

Entre na pasta do projeto e rode:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
jar cfe SistemaChamadosTI.jar br.com.faculdade.chamados.Main -C out .
java -jar SistemaChamadosTI.jar
```

No Windows PowerShell, se o comando `find` não funcionar, compile pelo NetBeans, Eclipse ou IntelliJ.

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
