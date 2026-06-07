@echo off
echo Compilando o servidor...

set ROOT=%~dp0..
set SRC=%ROOT%\src
set BACKEND=%ROOT%\backend
set OUT=%ROOT%\out\server

if not exist "%OUT%" mkdir "%OUT%"

javac -d "%OUT%" ^
  "%SRC%\br\com\faculdade\chamados\model\Chamado.java" ^
  "%SRC%\br\com\faculdade\chamados\model\Usuario.java" ^
  "%SRC%\br\com\faculdade\chamados\model\Historico.java" ^
  "%SRC%\br\com\faculdade\chamados\estrutura\ArvoreChamados.java" ^
  "%SRC%\br\com\faculdade\chamados\estrutura\FilaChamados.java" ^
  "%SRC%\br\com\faculdade\chamados\estrutura\ListaLigadaChamados.java" ^
  "%SRC%\br\com\faculdade\chamados\estrutura\PilhaHistorico.java" ^
  "%SRC%\br\com\faculdade\chamados\service\ArquivoService.java" ^
  "%SRC%\br\com\faculdade\chamados\service\ChamadoService.java" ^
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
