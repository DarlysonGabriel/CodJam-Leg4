#!/usr/bin/env bash
# Roda a base de testes do compilador do Legal (sem interface e sem robô).
# Uso: bash tests/rodar-testes.sh [pasta-do-repositório]
# Sem argumento, procura o repositório na pasta do pacote ou na pasta de cima.
# Precisa de um JDK (javac e java) instalado. Testado com o JDK 8.
set -euo pipefail

AQUI="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PACOTE="$(cd "$AQUI/.." && pwd)"
SRC="$PACOTE/src-reconstruido"

if [ -n "${1:-}" ]; then
  REPO="$(cd "$1" && pwd)"
elif [ -d "$PACOTE/PNCA" ] && [ -d "$PACOTE/Montador" ]; then
  REPO="$PACOTE"
elif [ -d "$PACOTE/../PNCA" ] && [ -d "$PACOTE/../Montador" ]; then
  REPO="$(cd "$PACOTE/.." && pwd)"
else
  echo "Erro: não achei a raiz do repositório (a pasta com PNCA/ e Montador/)." >&2
  echo "Informe o caminho: bash tests/rodar-testes.sh /caminho/do/repositorio" >&2
  exit 1
fi

command -v javac >/dev/null 2>&1 || { echo "Erro: não encontrei o javac. Instale um JDK (no Arch: sudo pacman -S jdk8-openjdk)." >&2; exit 1; }
[ -d "$REPO/PNCA" ] && [ -d "$REPO/Montador" ] || { echo "Erro: $REPO não parece ser a raiz do repositório (faltam PNCA/ e Montador/)." >&2; exit 1; }
[ -d "$SRC/PNCA" ] || { echo "Erro: falta a pasta src-reconstruido/ em $PACOTE." >&2; exit 1; }
echo "Repositório: $REPO"

VERSAO_JAVAC="$(javac -version 2>&1 | grep -m1 '^javac ' || true)"
MAJOR="$(printf '%s' "$VERSAO_JAVAC" | sed -E 's/^javac (1\.)?([0-9]+).*/\2/')"
case "$MAJOR" in ''|*[!0-9]*) MAJOR=0 ;; esac
if [ "$MAJOR" = "8" ]; then FLAGS="-source 1.6 -target 1.6"; else FLAGS="--release 8"; fi

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/reconstruido" "$TMP/teste"

echo "Compilando o código reconstruído (${VERSAO_JAVAC:-javac})..."
javac -encoding UTF-8 $FLAGS -nowarn -cp "$REPO" -d "$TMP/reconstruido" \
  "$SRC"/PNCA/*.java "$SRC"/Montador/*.java 2>&1 | grep -v "^Note:\|^warning\|^[0-9]* warning" || true
echo "Compilando o programa de teste..."
javac -encoding UTF-8 $FLAGS -nowarn -cp "$REPO" -d "$TMP/teste" "$AQUI/Harness.java" 2>&1 | grep -v "^Note:\|^warning\|^[0-9]* warning" || true

CASOS="$AQUI/casos.txt"
ESPERADO="$AQUI/saida_esperada.txt"
total="$(grep -c '^#### ' "$CASOS")"
falhas=0

echo
echo "Teste 1: classes compiladas do repositório (as que o programa usa)"
java -cp "$TMP/teste:$REPO" Harness "$CASOS" > "$TMP/saida_repo.txt" 2>/dev/null
if diff -q "$TMP/saida_repo.txt" "$ESPERADO" >/dev/null; then
  echo "  OK: $total casos idênticos ao esperado."
else
  echo "  FALHOU. Diferenças (até 20 linhas):"; diff "$TMP/saida_repo.txt" "$ESPERADO" | head -20 || true; falhas=$((falhas + 1))
fi

echo "Teste 2: código reconstruído (src-reconstruido/)"
java -cp "$TMP/teste:$TMP/reconstruido:$REPO" Harness "$CASOS" > "$TMP/saida_rec.txt" 2>/dev/null
if diff -q "$TMP/saida_rec.txt" "$ESPERADO" >/dev/null; then
  echo "  OK: $total casos idênticos ao esperado."
else
  echo "  FALHOU. Diferenças (até 20 linhas):"; diff "$TMP/saida_rec.txt" "$ESPERADO" | head -20 || true; falhas=$((falhas + 1))
fi

echo
if [ "$falhas" = 0 ]; then echo "Todos os testes passaram."; else echo "$falhas teste(s) falharam."; exit 1; fi
