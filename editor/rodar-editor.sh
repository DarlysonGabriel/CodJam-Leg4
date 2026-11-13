#!/usr/bin/env bash
# Abre o IDE Legal original, com tema escuro roxo (CodJam).
# Uso: bash editor/rodar-editor.sh [pasta-do-repositório]
# Precisa de um JDK 8 ou mais novo. Roda em X11 (Linux, inclusive Arch com Xorg ou XWayland).
set -euo pipefail

AQUI="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "${1:-$AQUI/..}" && pwd)"
CACHE="$HOME/.codjam/tema-gerado"

falha() {
  echo "Erro: $*" >&2
  exit 1
}

[ -f "$REPO/org/fife/ui/rsyntaxtextarea/RSyntaxTextArea.class" ] \
  || falha "não achei a RSyntaxTextArea em $REPO. Rode na pasta do repositório (a que tem a pasta org/) ou informe o caminho: bash editor/rodar-editor.sh /caminho/do/repositorio"
[ -f "$REPO/Legal3/Legal.class" ] || falha "não encontrei Legal3/Legal.class em $REPO — isso não parece ser o repositório do Legal."
command -v javac >/dev/null 2>&1 || falha "não encontrei o javac. Instale um JDK (no Arch: sudo pacman -S jdk8-openjdk)."
command -v java >/dev/null 2>&1 || falha "não encontrei o java."
if [ -z "${DISPLAY:-}" ]; then
  falha "nenhum servidor X11 (variável DISPLAY vazia). No Wayland, rode numa sessão com XWayland ativo."
fi

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
javac -encoding UTF-8 $FLAGS -nowarn -cp "$SAIDA:$REPO" -d "$SAIDA" "$AQUI"/src-pintores/Legal3/Painters/*.java 2>&1 \
  | grep -v "^Note:\|^warning\|^[0-9]* warning" || true

VERSAO_ATUAL="$(java -cp "$SAIDA" codjam.editor.GeradorTema --versao 2>/dev/null || echo 1)"
if [ ! -f "$CACHE/.versao" ] || [ "$(cat "$CACHE/.versao" 2>/dev/null)" != "$VERSAO_ATUAL" ]; then
  echo "Gerando o tema escuro (primeira vez ou tema desatualizado; demora alguns segundos)..."
  rm -rf "$CACHE"
  mkdir -p "$CACHE"
  java -Djava.awt.headless=true -cp "$SAIDA:$REPO" codjam.editor.GeradorTema "$REPO" "$CACHE" "$AQUI/assets/logo-cj.png"
fi

java -Dfile.encoding=UTF-8 -Dcodjam.logo="$AQUI/assets/logo-cj.png" -cp "$CACHE:$SAIDA:$REPO" codjam.editor.CodJam
