# Sistema de Chamados de TI

Projeto acadêmico desenvolvido em Java para simular o atendimento de chamados de suporte técnico de informática, aplicando estruturas de dados como Fila, Pilha, Lista Ligada e Árvore Binária.

---

## Pré-requisitos

- Java JDK 17 ou superior instalado

---

## Credenciais padrão

| Perfil         | Login        | Senha        |
|----------------|--------------|--------------|
| Administrador  | admin        | admin        |
| Solicitante    | solicitante  | solicitante  |

---

## Opção 1 — Aplicação Desktop (Java Swing)

Interface gráfica nativa em Java, sem necessidade de navegador.

```bash
java -jar ProjetoChamadosTI_atualizado.jar
```

---

## Opção 2 — Interface Web (Frontend + Backend HTTP)

Interface web moderna rodando no navegador, com backend Java embutido.

### Como rodar

**Passo 1 — Iniciar o servidor**

Abra o PowerShell e cole:

```powershell
Start-Process "c:\Users\PICHAU\meu-projeto-a3\a3-estrutura-de-dados\backend\rodar.bat"
```

Ou simplesmente dê **duplo clique** no arquivo `backend/rodar.bat` pelo Explorador de Arquivos do Windows.

> ⚠️ Não rode `rodar.bat` diretamente no PowerShell — use o `Start-Process` acima ou o duplo clique.

**Passo 2 — Aguardar o servidor subir**

Na janela CMD que abriu, espere aparecer:
```
Servidor rodando em http://localhost:8080
```

**Passo 3 — Abrir o frontend**

Abra o arquivo `frontend/index.html` no navegador (duplo clique ou arraste para o Chrome/Edge).

**Passo 4 — Fazer login**

Use `admin / admin` ou `solicitante / solicitante`.

> Para parar o servidor, feche a janela CMD ou pressione `Ctrl+C` nela.
> O servidor usa apenas o JDK padrão (`com.sun.net.httpserver`), sem dependências externas.

---

## Estrutura do projeto

```
a3-estrutura-de-dados/
├── src/                        # Código-fonte Java (Swing + lógica)
│   └── br/com/faculdade/chamados/
│       ├── model/              # Chamado, Usuario, Historico
│       ├── estrutura/          # Fila, Pilha, Lista Ligada, Árvore Binária
│       ├── service/            # ChamadoService, ArquivoService
│       └── ui/                 # Telas Swing (Login, Admin, Solicitante)
├── backend/                    # Servidor HTTP Java (API REST)
│   ├── rodar.bat               # Compila e inicia o servidor
│   └── br/com/faculdade/chamados/server/
│       ├── ChamadoServer.java  # Servidor principal (porta 8080)
│       ├── LoginHandler.java
│       ├── ChamadosHandler.java
│       ├── HistoricoHandler.java
│       └── AplicativosHandler.java
├── frontend/                   # Interface web
│   ├── index.html
│   ├── css/style.css
│   └── js/
│       ├── api.js              # Comunicação com o backend
│       ├── ui.js               # Manipulação de tela
│       └── app.js              # Lógica principal
├── data/                       # Dados persistidos em arquivos .txt
│   ├── chamados.txt
│   ├── usuarios.txt
│   ├── historicos.txt
│   └── aplicativos.txt
└── ProjetoChamadosTI_atualizado.jar
```

---

## Rotas da API (backend web)

| Método | Rota                            | Descrição                    |
|--------|---------------------------------|------------------------------|
| POST   | /api/login                      | Autenticação                 |
| GET    | /api/chamados                   | Lista todos os chamados      |
| GET    | /api/chamados?solicitante=Nome  | Lista por solicitante        |
| GET    | /api/chamados/{id}              | Busca chamado por ID         |
| POST   | /api/chamados                   | Cria novo chamado            |
| PUT    | /api/chamados/{id}/atuar        | Admin assume o chamado       |
| PUT    | /api/chamados/{id}/salvar       | Salva dados de atendimento   |
| PUT    | /api/chamados/{id}/finalizar    | Finaliza o chamado           |
| PUT    | /api/chamados/{id}/cancelar     | Cancela o chamado            |
| GET    | /api/historico/{id}             | Histórico do chamado         |
| GET    | /api/aplicativos                | Lista de aplicativos         |

---

## Estruturas de dados utilizadas

| Estrutura      | Uso no sistema                                                  |
|----------------|------------------------------------------------------------------|
| Fila           | Organiza chamados com status **Aberto** aguardando atendimento  |
| Pilha          | Registra e exibe o histórico do chamado (mais recente primeiro) |
| Lista Ligada   | Armazena todos os chamados em memória                           |
| Árvore Binária | Busca eficiente de chamados por ID                              |
