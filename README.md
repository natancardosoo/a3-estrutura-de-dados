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

### Como rodar

**Passo 1 — Iniciar o servidor**

Na pasta raíz do projeto, abra o CMD e insira:

```powershell
backend\rodar.bat
```

Ou simplesmente dê **duplo clique** no arquivo `backend/rodar.bat` pelo Explorador de Arquivos do Windows.

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
