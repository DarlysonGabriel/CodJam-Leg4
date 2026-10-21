#!/usr/bin/env bash
set -euo pipefail
REPO="DarlysonGabriel/CodJam-Leg4"
gh auth status >/dev/null
for l in erro-numerado precisa-leitura-do-codigo mensagens leg4 interface legado; do gh label create "$l" --repo "$REPO" --force >/dev/null; done
gh issue create --repo "$REPO" --title "Mensagens de erro em português pouco claras (27 mensagens)" --label "mensagens" --body "$(cat <<'EOF_ISSUE'
As mensagens de erro do compilador em português dizem só o que faltou, sem dizer como resolver. A mais disparada, "Essa expressão é inválida." (erro 54), responde por cerca de 15% dos pontos de erro do compilador.

**Proposta (já aprovada e pronta como patch `leg4-mensagens-pt.patch`):**
- Reescrever 27 mensagens no formato "o que faltou + como arrumar", mantendo os limites reais do compilador (por exemplo tempo de 0,1 a 120).
- Corrigir dois erros de digitação do IDE: "entedi" e "Excessão" (esta aparece em 3 classes).

Arquivos alterados: `PNCA/ErrorMessages.class`, `Legal3/LanguageTerms.class`, `Legal3/CompilerThread.class`, `Legal3/LearnThread.class`, `Legal3/LearnThreadConfigColetor.class`.

**Pendente:** testar no IDE provocando alguns erros (tempo de 0,05 s, bloco `[` sem `]`, expressão incompleta) e com o robô.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Inconsistências nas mensagens em inglês e espanhol (adiado)" --label "mensagens" --body "$(cat <<'EOF_ISSUE'
Foco atual é só português, mas ficaram registrados:
- Inglês, erros 82 e 83: estavam em português (após o patch de português passam a mostrar o novo texto em português).
- Inglês, erro 42: diz "between 1 and 180", mas o código aceita de 0 a 180.
- Espanhol, erro 141: "Erro Fatal." está em português.
- Espanhol, erros 4 e 31: limite escrito como 32768 (o correto, como em português e inglês, é -32768 a 32767).
- Espanhol, erro 27: passa a mostrar o novo texto em português por compartilhar o texto com a tabela portuguesa no arquivo compilado.

**Tarefa:** corrigir quando o idioma voltar ao escopo.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Repositório contém o .jar descompactado e não há código-fonte do Legal" --label "leg4" --body "$(cat <<'EOF_ISSUE'
O repositório reflete o conteúdo do `.jar` descompactado (mais de 10 mil arquivos `.class`, cerca de 100 MB). Os únicos `.java` são 7 arquivos de uma biblioteca de terceiros. Não existe o código-fonte do Legal.

**Tarefa:** decidir como trabalhar daqui para a frente (por exemplo manter o código decompilado de `PNCA` e `Montador` em um diretório de fonte, separar as bibliotecas de terceiros e definir a estrutura do repositório).
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Revisar licenças e créditos antes de publicar o leg4" --label "leg4" --body "$(cat <<'EOF_ISSUE'
O repositório inclui bibliotecas de terceiros com licenças próprias (por exemplo GPL v2 no Simbrain e BSD no hamcrest, conforme os arquivos de licença incluídos). O `ChangeLog` cita um autor e um caminho de projeto de 2011.

**Tarefa:** confirmar a titularidade e as licenças aplicáveis e registrar os créditos antes de publicar uma versão do leg4.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Não precisar decorar as portas dos sensores" --label "interface" --body "$(cat <<'EOF_ISSUE'
Hoje o aluno precisa lembrar em qual porta (S1 a S8, ME/MD, etc.) cada sensor fica. O IDE gera comentários `# Instalar ... em S2`, mas isso não resolve o incômodo de lembrar.

**Ideia (do CodJam):** declarar a porta uma vez e usar o nome depois, por exemplo `Sensor_luz = s3`. O mapeamento hoje está concentrado na `TokenList`.

**Tarefa:** definir a sintaxe e a ajuda visual na interface, sem mudar o firmware.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Ação: evolução do Aprenda e semântica de execução" --label "leg4" --body "$(cat <<'EOF_ISSUE'
A sub-rotina atual é `Aprenda <nome> [ ... ]`. O leg4 propõe `Ação`, com `Ação principal` como entrada.

**Em aberto:**
- `andar()` termina antes de `detectar()` começar, ou podem rodar em paralelo? A decisão é pelo estilo Python/GDScript: sequencial por padrão, com execução paralela ou alternada quando fizer sentido.
- Hoje a concorrência são os 8 eventos (`<E1>` a `<E8>`).
- Tudo precisa virar o mesmo conjunto de instruções do `Montador` (o firmware não muda; a memória do robô é limitada a 4000).
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Compatibilidade com arquivos .leg3 e aviso de tecnologia legada" --label "legado" --body "$(cat <<'EOF_ISSUE'
O leg4 deve continuar lendo `.leg3` (cabeçalho `LEGAL V05`, seções `<P>`, `<E1>` a `<E8>`, `<BibVar>`, `<BibSub>`, `<Fim>`, codificação latin1) e mostrar um aviso de que o formato é de uma tecnologia antiga.

**Tarefa:** reunir arquivos `.leg3` reais como casos de teste e definir como e onde o aviso aparece.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Integração entre blocos visuais e código textual" --label "interface" --body "$(cat <<'EOF_ISSUE'
Objetivo: permitir misturar blocos visuais e código em texto, com execução paralela ou alternada, algo parecido com a relação entre Python e GDScript.

**Depende de:** definição da semântica de execução (issue sobre `Ação`) e da fase da interface.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de escrever (erro 9)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de escrever`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "A palavra 'Invertido' deve ser usada junto com o 'Ligue Evento'. (erro 17)" --label "mensagens,erro-numerado,precisa-leitura-do-codigo" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `A palavra 'Invertido' deve ser usada junto com o 'Ligue Evento'.`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** Ainda não foi reescrita porque falta ler o código que dispara este erro para entender o contexto exato.

**Tarefa:** Ler o ponto de disparo (ou descobrir onde o erro é usado) e propor texto no padrão "o que faltou + como arrumar".

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de escrever o termo da atribuição. (erro 47)" --label "mensagens,erro-numerado,precisa-leitura-do-codigo" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de escrever o termo da atribuição.`

**Uso:** 5 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** Ainda não foi reescrita porque falta ler o código que dispara este erro para entender o contexto exato.

**Tarefa:** Ler o ponto de disparo (ou descobrir onde o erro é usado) e propor texto no padrão "o que faltou + como arrumar".

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de cor esperado em S (erro 91)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de cor esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de luz esperado em S (erro 92)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de luz esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de faixa esperado em S (erro 93)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de faixa esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um motor em M (erro 94)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar um motor em M`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar o servo (erro 95)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar o servo`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de indicar o sentido do motor (erro 96)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de indicar o sentido do motor`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um sensor digital em S (erro 97)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar um sensor digital em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um dispositivo externo em S (erro 98)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar um dispositivo externo em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "O sensor de ruído só aceita valores entre 40 e 90 (erro 102)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `O sensor de ruído só aceita valores entre 40 e 90`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A frase está completa; só falta conferir a pontuação e se o texto ajuda a corrigir o erro.

**Tarefa:** Revisar o texto (ponto final e clareza) mantendo os limites e a forma de escrita já usados pelo compilador.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Já tem um sensor conectado em S (erro 105)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Já tem um sensor conectado em S`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você só pode enviar um inteiro ou uma variável (erro 106)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você só pode enviar um inteiro ou uma variável`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A frase está completa; só falta conferir a pontuação e se o texto ajuda a corrigir o erro.

**Tarefa:** Revisar o texto (ponto final e clareza) mantendo os limites e a forma de escrita já usados pelo compilador.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você precisa escrever o nome de uma variável aqui (erro 107)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você precisa escrever o nome de uma variável aqui`

**Uso:** 2 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A frase está completa; só falta conferir a pontuação e se o texto ajuda a corrigir o erro.

**Tarefa:** Revisar o texto (ponto final e clareza) mantendo os limites e a forma de escrita já usados pelo compilador.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um sensor em S (erro 108)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar um sensor em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você não habilitou ou desabilitou o Evento (erro 109)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você não habilitou ou desabilitou o Evento`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "A expressão 'entre' tem a forma: <Variável> entre <Inteiro> e <Inteiro> (erro 117)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `A expressão 'entre' tem a forma: <Variável> entre <Inteiro> e <Inteiro>`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** A frase está completa; só falta conferir a pontuação e se o texto ajuda a corrigir o erro.

**Tarefa:** Revisar o texto (ponto final e clareza) mantendo os limites e a forma de escrita já usados pelo compilador.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um voltímetro em S (erro 133)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de conectar um voltímetro em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de resistência esperado em S (erro 134)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de resistência esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de temperatura esperado em S (erro 135)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de temperatura esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de ruído esperado em S (erro 136)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de ruído esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de distância esperado em S (erro 137)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Sensor de distância esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Protótipo de sensor analógico esperado em S (erro 139)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Protótipo de sensor analógico esperado em S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A mensagem termina no meio da frase e parece receber um complemento (porta, número ou nome) montado pelo programa.

**Tarefa:** Descobrir como o texto final é montado antes de mudar a mensagem, e propor um texto cujo final continue compatível com o complemento.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "As saídas válidas de PWM são de PWM 1 até PWM 4 (erro 143)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `As saídas válidas de PWM são de PWM 1 até PWM 4`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** A frase está completa; só falta conferir a pontuação e se o texto ajuda a corrigir o erro.

**Tarefa:** Revisar o texto (ponto final e clareza) mantendo os limites e a forma de escrita já usados pelo compilador.

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Faltou a palavra 'graus'. (erro 154)" --label "mensagens,erro-numerado,precisa-leitura-do-codigo" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Faltou a palavra 'graus'.`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** Ainda não foi reescrita porque falta ler o código que dispara este erro para entender o contexto exato.

**Tarefa:** Ler o ponto de disparo (ou descobrir onde o erro é usado) e propor texto no padrão "o que faltou + como arrumar".

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Faltou a palavra 'grau'. (erro 155)" --label "mensagens,erro-numerado,precisa-leitura-do-codigo" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Faltou a palavra 'grau'.`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** Ainda não foi reescrita porque falta ler o código que dispara este erro para entender o contexto exato.

**Tarefa:** Ler o ponto de disparo (ou descobrir onde o erro é usado) e propor texto no padrão "o que faltou + como arrumar".

**Critério:** só mensagem em português, sem mudar a lógica do compilador, no padrão "o que faltou + como arrumar".
EOF_ISSUE
)"
