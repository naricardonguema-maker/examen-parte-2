#!/usr/bin/env sh
# Ejecuta este archivo desde macOS/Linux para iniciar el gestor.
cd "$(dirname "$0")"
echo "Iniciando el gestor de peliculas..."
mvn clean compile exec:java
