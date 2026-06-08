@echo off
echo Compilando o servidor...

set ROOT=%~dp0..
set BACKEND=%ROOT%\backend
set OUT=%ROOT%\out\server

if not exist "%OUT%" mkdir "%OUT%"

javac -d "%OUT%" ^
  "%BACKEND%\br\com\faculdade\chamados\model\Chamado.java" ^
  "%BACKEND%\br\com\faculdade\chamados\model\Usuario.java" ^
  "%BACKEND%\br\com\faculdade\chamados\model\Historico.java" ^
  "%BACKEND%\br\com\faculdade\chamados\estrutura\ArvoreChamados.java" ^
  "%BACKEND%\br\com\faculdade\chamados\estrutura\FilaChamados.java" ^
  "%BACKEND%\br\com\faculdade\chamados\estrutura\ListaLigadaChamados.java" ^
  "%BACKEND%\br\com\faculdade\chamados\estrutura\PilhaHistorico.java" ^
  "%BACKEND%\br\com\faculdade\chamados\service\ArquivoService.java" ^
  "%BACKEND%\br\com\faculdade\chamados\service\ChamadoService.java" ^
  "%BACKEND%\br\com\faculdade\chamados\server\ChamadoServer.java" ^
  "%BACKEND%\br\com\faculdade\chamados\server\LoginHandler.java" ^
  "%BACKEND%\br\com\faculdade\chamados\server\ChamadosHandler.java" ^
  "%BACKEND%\br\com\faculdade\chamados\server\HistoricoHandler.java" ^
  "%BACKEND%\br\com\faculdade\chamados\server\AplicativosHandler.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERRO na compilacao! Verifique as mensagens acima.
    pause
    exit /b 1
)

echo Compilacao concluida!
echo.
echo Iniciando servidor em http://localhost:8080
echo Pressione Ctrl+C para parar.
echo.

java -cp "%OUT%" br.com.faculdade.chamados.server.ChamadoServer

pause
