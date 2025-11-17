#!/bin/bash

# Script para rodar testes antes de fazer build do Docker
# Garante que apenas código testado seja deployado

set -e  # Para em caso de erro

echo "🧪 Executando testes..."
echo "======================================"

# Roda os testes
./mvnw test

# Captura o resultado
TEST_RESULT=$?

echo ""
echo "======================================"

if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ Todos os testes passaram!"
    echo ""
    echo "🐳 Iniciando build do Docker..."
    echo "======================================"
    
    # Build e start dos containers
    docker-compose up --build
else
    echo "❌ Testes falharam! Build cancelado."
    echo "Corrija os erros antes de fazer o deploy."
    exit 1
fi
