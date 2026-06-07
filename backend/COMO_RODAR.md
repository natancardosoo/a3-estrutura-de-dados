# Como rodar o servidor web

## Pré-requisitos
- Java instalado (o mesmo que já roda o projeto .jar)

## Passo a passo

1. Abra o terminal (cmd) na pasta raiz do projeto
2. Execute:

```
backend\rodar.bat
```

3. Aguarde a mensagem:
   ```
   Servidor rodando em http://localhost:8080
   ```

4. Abra o arquivo `frontend/index.html` no navegador

5. Faça login:
   - **admin / admin** → tela de administrador
   - **solicitante / solicitante** → tela de solicitante

## Para parar o servidor
Pressione `Ctrl+C` no terminal.

## Observações
- Os dados continuam sendo salvos nos mesmos arquivos `data/*.txt`
- O servidor e o app Java original usam os mesmos dados
- Para rodar em outra porta, edite a constante `PORT` em `ChamadoServer.java`
  e atualize a constante `API` em `frontend/js/api.js`
