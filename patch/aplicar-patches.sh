#!/usr/bin/env bash
# Aplica, em ordem, os patches das mensagens de erro em português do leg4.
# Uso: dentro do repositório CodJam-Leg4, rode: bash /caminho/aplicar-patches.sh
# Os arquivos .patch devem estar na mesma pasta deste script.
set -euo pipefail

PASTA="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PATCHES=(
  "leg4-mensagens-pt.patch"
  "leg4-mensagens-pt-etapa5.patch"
  "leg4-mensagens-pt-etapa6.patch"
)
COMMITS=(
  "Mensagens de erro em português mais claras e correção de erros de digitação do IDE"
  "Mensagens de erro em português para sensores, motor, graus e atribuição"
  "Corrige as mensagens dos erros 16 e 109: eventos são identificados pelo sensor (s1 a s8)"
)
BRANCH="mensagens-erro-pt"
USAR_BRANCH=1
FAZER_COMMIT=1
GUARDAR=0

uso() {
  cat <<EOT
Aplica os patches das mensagens de erro em português do leg4.

Uso: bash aplicar-patches.sh [opções]

  --branch NOME   nome da branch a criar (padrão: ${BRANCH})
  --sem-branch    aplica na branch atual, sem criar outra
  --sem-commit    aplica as alterações sem fazer commit
  --stash         guarda alterações pendentes (git stash) antes de aplicar
  -h, --ajuda     mostra esta ajuda

Patches já aplicados são detectados e pulados. Se algo falhar depois de o script
começar a mexer no repositório, ele volta ao estado anterior.
Nada é enviado ao GitHub: o push fica com você.
EOT
}

while [ $# -gt 0 ]; do
  case "$1" in
    --branch)
      [ $# -ge 2 ] || { echo "Faltou o nome depois de --branch." >&2; exit 1; }
      BRANCH="$2"; shift 2 ;;
    --sem-branch) USAR_BRANCH=0; shift ;;
    --sem-commit) FAZER_COMMIT=0; shift ;;
    --stash) GUARDAR=1; shift ;;
    -h|--ajuda) uso; exit 0 ;;
    *) echo "Opção desconhecida: $1" >&2; uso >&2; exit 1 ;;
  esac
done

INICIOU=0
CONCLUIDO=0
CRIOU_BRANCH=0
ORIGEM_REF=""
ORIGEM_SHA=""

desfazer() {
  [ "$CONCLUIDO" = 1 ] && return 0
  [ "$INICIOU" = 1 ] || return 0
  echo "Algo deu errado. Voltando o repositório ao estado anterior..." >&2
  git reset --hard "$ORIGEM_SHA" >/dev/null 2>&1 || true
  git checkout -q "$ORIGEM_REF" >/dev/null 2>&1 || true
  if [ "$CRIOU_BRANCH" = 1 ]; then
    git branch -D "$BRANCH" >/dev/null 2>&1 || true
  fi
  echo "Repositório restaurado." >&2
}
trap desfazer EXIT

erro() {
  echo "Erro: $*" >&2
  exit 1
}

command -v git >/dev/null 2>&1 || erro "o git não está instalado."
git rev-parse --is-inside-work-tree >/dev/null 2>&1 \
  || erro "rode o script dentro da pasta do repositório CodJam-Leg4."
cd "$(git rev-parse --show-toplevel)"

for p in "${PATCHES[@]}"; do
  [ -f "$PASTA/$p" ] || erro "não encontrei $p na pasta $PASTA."
done

if [ "$FAZER_COMMIT" = 1 ]; then
  git config user.name >/dev/null && git config user.email >/dev/null \
    || erro "o git não sabe seu nome e e-mail. Configure com: git config user.name \"Seu Nome\" e git config user.email \"voce@exemplo.com\" (ou use --sem-commit)."
fi

if [ -n "$(git status --porcelain --untracked-files=no)" ]; then
  if [ "$GUARDAR" = 1 ]; then
    git stash push -q -m "aplicar-patches: alterações guardadas antes de aplicar"
    echo "Alterações pendentes guardadas com git stash. Para recuperá-las depois: git stash pop"
  else
    echo "Erro: há alterações não commitadas nestes arquivos rastreados:" >&2
    git status --short --untracked-files=no | head -10 | sed 's/^/  /' >&2
    echo "Faça commit, guarde-as com 'git stash' ou rode de novo com --stash." >&2
    exit 1
  fi
fi

ORIGEM_SHA="$(git rev-parse HEAD)"
ORIGEM_REF="$(git symbolic-ref -q --short HEAD || echo "$ORIGEM_SHA")"

if [ "$USAR_BRANCH" = 1 ]; then
  if git show-ref --verify --quiet "refs/heads/$BRANCH"; then
    erro "a branch '$BRANCH' já existe. Troque para ela (git switch $BRANCH) e use --sem-branch, ou escolha outro nome com --branch."
  fi
  INICIOU=1
  git checkout -q -b "$BRANCH"
  CRIOU_BRANCH=1
  echo "Branch criada: $BRANCH"
else
  INICIOU=1
fi

total="${#PATCHES[@]}"

ultimo_aplicado=-1
for ((i = total - 1; i >= 0; i--)); do
  if git apply --reverse --check "$PASTA/${PATCHES[$i]}" >/dev/null 2>&1; then
    ultimo_aplicado=$i
    break
  fi
done

aplicados=0
for i in "${!PATCHES[@]}"; do
  n=$((i + 1))
  p="$PASTA/${PATCHES[$i]}"
  if [ "$i" -le "$ultimo_aplicado" ]; then
    echo "[$n/$total] ${PATCHES[$i]}: já está aplicado, pulando."
    continue
  fi
  if ! git apply --check "$p" >/dev/null 2>&1; then
    erro "${PATCHES[$i]} não aplica neste repositório. Confirme que ele está no commit original (9544a18) ou com os patches anteriores já aplicados."
  fi
  git apply "$p"
  aplicados=$((aplicados + 1))
  if [ "$FAZER_COMMIT" = 1 ]; then
    git add -u
    git commit -q -m "${COMMITS[$i]}"
    echo "[$n/$total] ${PATCHES[$i]}: aplicado e commitado."
  else
    echo "[$n/$total] ${PATCHES[$i]}: aplicado (sem commit)."
  fi
done

CONCLUIDO=1
echo
if [ "$aplicados" = 0 ]; then
  echo "Nada a fazer: todos os patches já estavam aplicados."
else
  echo "Pronto: $aplicados patch(es) aplicado(s)."
fi
if [ "$FAZER_COMMIT" = 1 ] && [ "$USAR_BRANCH" = 1 ] && [ "$aplicados" -gt 0 ]; then
  echo "Para enviar ao GitHub e abrir o pull request:"
  echo "  git push -u origin $BRANCH"
fi
