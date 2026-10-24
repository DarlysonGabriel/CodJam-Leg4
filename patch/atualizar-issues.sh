#!/usr/bin/env bash
# Atualiza as issues do CodJam-Leg4 com os novos resultados e cria as issues novas.
# Precisa do GitHub CLI (gh) com login feito. Rode UMA vez (senão os comentários e issues se repetem).
set -euo pipefail
REPO="DarlysonGabriel/CodJam-Leg4"
gh auth status >/dev/null
gh label create "testes" --repo "$REPO" --color 0e8a16 --force >/dev/null

comentar() {
  local numero="$1" trecho="$2" texto="$3" titulo
  titulo="$(gh issue view "$numero" --repo "$REPO" --json title -q .title)"
  case "$titulo" in
    *"$trecho"*) gh issue comment "$numero" --repo "$REPO" --body "$texto" >/dev/null; echo "Comentado: #$numero" ;;
    *) echo "AVISO: #$numero não parece ser '$trecho' (título: $titulo). Pulei." >&2 ;;
  esac
}

comentar 47 "27 mensagens" "$(cat <<'EOT'
Atualização: os patches agora fazem parte do repositório, em `patch/`:
- `leg4-mensagens-pt.patch` (Etapa 3), `leg4-mensagens-pt-etapa5.patch` (Etapa 5) e `leg4-mensagens-pt-etapa6.patch` (correção dos erros 16 e 109, que eu tinha escrito errado: os eventos são identificados pelo sensor, `habilite evento s1`).
- `bash patch/aplicar-patches.sh` aplica os três em ordem, numa branch nova.
- `bash tests/rodar-testes.sh` confere o comportamento: com os três patches aplicados, os 108 casos de teste dão o mesmo resultado do compilador original.

Continua faltando o teste no Legal e no robô.
EOT
)"
comentar 83 "(erro 109)" "$(cat <<'EOT'
Texto corrigido no patch da Etapa 6, depois de testar o compilador: os eventos são identificados pelo sensor (`habilite evento s1`).

Novo texto: `Faltou escrever "habilite evento" ou "desabilite evento" para o evento do sensor S` seguido do número que o IDE acrescenta.
EOT
)"
comentar 50 "descompactado" "$(cat <<'EOT'
Atualização: foi gerada uma versão reconstruída do `PNCA` e do `Montador` em `src-reconstruido/`, por decompilação (CFR 0.152), com 2 reparos manuais documentados em `reparos-manuais.diff`. Ela recompila para Java 6 com o JDK 8 e dá resultado idêntico ao compilador original em 108 casos (`tests/`). Não é o código-fonte original.

O `Compiler.java` reconstruído ainda tem os nomes de variáveis locais perdidos. A estrutura final do repositório continua em aberto.
EOT
)"
comentar 56 "Compatibilidade" "$(cat <<'EOT'
Atualização: existe uma base de testes em `tests/` com 108 casos e o resultado esperado do compilador original. Só há **um** programa `.leg3` real; os outros casos foram escritos a partir do código. Ainda faltam arquivos `.leg3` reais (de alunos ou professores) para a compatibilidade ser testada de verdade.
EOT
)"
comentar 62 "Java" "$(cat <<'EOT'
Atualização: o código reconstruído recompila para Java 6 (bytecode versão 50) com o JDK 8 e os testes passaram. Isso foi testado **somente com o JDK 8**; JDK mais novo, IDE e porta serial continuam sem teste.
EOT
)"
comentar 64 "Documentar" "$(cat <<'EOT'
Atualização: existe uma segunda versão do documento da linguagem (`docs/leg3-linguagem-e-funcionamento.md`), agora com os comandos **testados** no compilador original. A primeira versão tinha erros em `motor`, `ligue`, `espere`, `se` e `habilite evento`, corrigidos. Pendente: variáveis, sub-rotinas, `inclua`, coleta de dados e comunicação.
EOT
)"

gh issue create --repo "$REPO" --title "Erro 33: a mensagem diz entre 0 e 200, mas o compilador recusa 0" --label "mensagens" --body "$(cat <<'EOT'
Testado no compilador: `repita 1 vezes` e `repita 200 vezes` compilam; `repita 0 vezes` e `repita 201 vezes` dão o erro 33 ("O número de repetições deve estar entre 0 e 200.").

**Proposta (aguardando aprovação):** `O número de repetições deve estar entre 1 e 200.`
EOT
)"
gh issue create --repo "$REPO" --title "Erro 74: a mensagem de desabilite evento usa número e não o sensor" --label "mensagens" --body "$(cat <<'EOT'
Testado: `desabilite evento 1` dá o erro 74 ("Qual evento devo desligar?"). Os eventos são identificados pelo sensor (`desabilite evento s1`).

**Proposta (aguardando aprovação):** `Faltou dizer qual evento desativar. Escreva o sensor do evento, de s1 a s8.`
EOT
)"
gh issue create --repo "$REPO" --title "Cobrir variáveis, sub-rotinas e inclua na base de testes" --label "testes,compilador" --body "$(cat <<'EOT'
A base de testes (`tests/`) só cobre o programa principal e os eventos. Faltam `variável`, `defina`, `Aprenda`, `inclua`, a coleta de dados, a comunicação (`envie`, `receba`) e a verificação do erro 109 do IDE.

**Tarefa:** repetir no programa de teste mais partes da ordem de compilação do `CompilerThread` e acrescentar casos.
EOT
)"
gh issue create --repo "$REPO" --title "Reunir arquivos .leg3 reais para os testes" --label "testes,legado" --body "$(cat <<'EOT'
Hoje só existe um programa `.leg3` real. Os demais casos de teste foram escritos a partir do código.

**Tarefa:** pedir a professores e alunos arquivos `.leg3` (e `.bib3`) legítimos, de preferência variados (eventos, sub-rotinas, variáveis, servos, sensores diferentes), e acrescentá-los a `tests/` com o resultado esperado.
EOT
)"
gh issue create --repo "$REPO" --title "Revisar os reparos manuais do código reconstruído" --label "repositorio,compilador" --body "$(cat <<'EOT'
O decompilador deixou dois trechos inválidos em `src-reconstruido/PNCA/Compiler.java` (`identifyNumber` e um `break` inalcançável em `scanAndGenerateAllSubs`), refeitos à mão (`reparos-manuais.diff`). Os 108 casos de teste são idênticos ao original, mas isso não prova equivalência total.

**Tarefa:** comparar o bytecode dos dois métodos com o original e acrescentar casos de número (inteiros, decimais, vírgula, valores inválidos).
EOT
)"
echo "Pronto."
