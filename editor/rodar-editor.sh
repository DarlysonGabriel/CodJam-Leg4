#!/usr/bin/env bash
# Compila e abre o editor CodJam.
# Uso: bash editor/rodar-editor.sh [pasta-do-repositório]
# Precisa de um JDK 8 ou mais novo. O repositório inteiro entra no classpath: é de lá que vem a RSyntaxTextArea.
set -euo pipefail

AQUI="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "${1:-$AQUI/..}" && pwd)"

falha() {
  echo "Erro: $*" >&2
  exit 1
}

[ -f "$REPO/org/fife/ui/rsyntaxtextarea/RSyntaxTextArea.class" ] \
  || falha "não achei a RSyntaxTextArea em $REPO. Rode na pasta do repositório (a que tem a pasta org/) ou informe o caminho: bash editor/rodar-editor.sh /caminho/do/repositorio"
command -v javac >/dev/null 2>&1 || falha "não encontrei o javac. Instale um JDK (no Arch: sudo pacman -S jdk8-openjdk)."
command -v java >/dev/null 2>&1 || falha "não encontrei o java."

VERSAO_JAVAC="$(javac -version 2>&1 | grep -m1 '^javac ' || true)"
MAJOR="$(printf '%s' "$VERSAO_JAVAC" | sed -E 's/^javac (1\.)?([0-9]+).*/\2/')"
case "$MAJOR" in ''|*[!0-9]*) MAJOR=0 ;; esac
[ "$MAJOR" -ge 8 ] || falha "é preciso um JDK 8 ou mais novo (encontrado: ${VERSAO_JAVAC:-desconhecido})."
if [ "$MAJOR" = 8 ]; then FLAGS="-source 1.8 -target 1.8"; else FLAGS="--release 8"; fi

SAIDA="$(mktemp -d)"
trap 'rm -rf "$SAIDA"' EXIT

echo "Compilando o editor (${VERSAO_JAVAC})..."
javac -encoding UTF-8 $FLAGS -nowarn -cp "$REPO" -d "$SAIDA" "$AQUI"/src/codjam/editor/*.java 2>&1 \
  | grep -v "^Note:\|^warning\|^[0-9]* warning" || true

java -Dfile.encoding=UTF-8 -Dcodjam.logo="$AQUI/assets/logo-cj.png" -cp "$SAIDA:$REPO" codjam.editor.CodJamEditor
