@echo off
REM Ejecuta este archivo desde Windows para iniciar el gestor.
cd /d "%~dp0"
echo Iniciando el gestor de peliculas...
mvn clean compile exec:java
pause
