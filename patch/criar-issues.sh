#!/usr/bin/env bash
# Cria as issues do projeto com o GitHub CLI. Rode UMA vez (senão as issues ficam duplicadas).
set -euo pipefail
REPO="DarlysonGabriel/CodJam-Leg4"
gh auth status >/dev/null
for l in erro-numerado aguardando-teste mensagens leg4 interface legado repositorio compilador documentacao; do gh label create "$l" --repo "$REPO" --force >/dev/null; done
gh issue create --repo "$REPO" --title "Mensagens de erro em português: 27 mensagens e 2 erros de digitação (patch pronto, aguardando teste)" --label "mensagens,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
As mensagens de erro do compilador em português diziam só o que faltou, sem dizer como resolver. A mais disparada, "Essa expressão é inválida." (erro 54), responde por cerca de 15% dos pontos de erro do compilador.

**Pronto como patch** (`leg4-mensagens-pt.patch`, aplicado em ordem pelo `aplicar-patches.sh`):
- 27 mensagens reescritas no formato "o que faltou + como arrumar", mantendo os limites reais do compilador (por exemplo tempo de 0,1 a 120).
- Dois erros de digitação do IDE corrigidos: "entedi" e "Excessão" (esta aparece em 3 classes).

**Falta testar no Legal:** provocar alguns erros (tempo de 0,05 s, bloco `[` sem `]`, expressão incompleta) e conferir as mensagens novas.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Mensagens de erro em inglês e espanhol com inconsistências (adiado)" --label "mensagens" --body "$(cat <<'EOF_ISSUE'
O foco atual é só português, mas ficaram registrados:
- Inglês, erros 82 e 83: estavam em português (o patch de português faz a tabela inglesa mostrar o novo texto em português).
- Inglês, erro 42: diz "between 1 and 180", mas o compilador aceita de 0 a 180.
- Espanhol, erro 141: "Erro Fatal." está em português.
- Espanhol, erros 4 e 31: limite escrito como 32768 (o correto, como em português e inglês, é -32768 a 32767).
- Espanhol, erro 27: passou a mostrar o novo texto em português por compartilhar o texto com a tabela portuguesa no arquivo compilado.

**Tarefa:** corrigir quando o idioma voltar ao escopo.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Textos da interface (IDE) com pequenos defeitos de português" --label "mensagens,interface" --body "$(cat <<'EOF_ISSUE'
Textos de `Legal3/LanguageTerms` (tabela em português) encontrados na leitura:
- Índice 160: "compativel" sem acento (e a frase termina em "Entre em contato conos..." na leitura; conferir o texto completo).
- Índices 65 e 67: "Direita" com letra maiúscula no meio da frase.
- Índice 119: "Eu entendi seu programa!!!" com três exclamações.
- Índice 128: frase sem ponto final ("...tente novamente").
- Índice 122: "Por favor escolha..." sem vírgula depois de "Por favor".

**Tarefa:** revisar o restante da tabela (cerca de 190 itens) e corrigir, no mesmo estilo dos patches de mensagens de erro.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "O repositório é o .jar descompactado, não tem o código-fonte do Legal e tem arquivos soltos" --label "repositorio" --body "$(cat <<'EOF_ISSUE'
O repositório reflete o conteúdo do `.jar` descompactado: mais de 10 mil `.class`, cerca de 100 MB. Os únicos `.java` são 7 arquivos de uma biblioteca de terceiros. Não existe o código-fonte do Legal.

Na raiz há arquivos que não são do projeto: `Readme.txt`, `ChangeLog` e `License.txt` vêm de bibliotecas incluídas, não do Legal. Também aparecem `Test.class`, `Test$1.class`, `checkstyle-all-5.0.jar`, `dictionary.txt` e vários arquivos de configuração de ferramentas (`eclipse.xml`, `idea.xml`...). Não há um README do projeto.

**Tarefa:** definir a estrutura do repositório (por exemplo: código decompilado de `PNCA` e `Montador` como fonte, bibliotecas de terceiros separadas, README do projeto) e o que sai do repositório.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Revisar licenças e créditos antes de publicar o leg4" --label "repositorio,leg4" --body "$(cat <<'EOF_ISSUE'
O repositório inclui bibliotecas de terceiros com licenças próprias (por exemplo GPL v2 no Simbrain e BSD no hamcrest, conforme os arquivos de licença incluídos). O `ChangeLog` cita um autor e um caminho de projeto de 2011.

**Tarefa:** confirmar a titularidade e as licenças aplicáveis e registrar os créditos antes de publicar uma versão do leg4.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Decidir a arquitetura do compilador: evoluir o atual ou criar um front-end novo" --label "compilador,leg4" --body "$(cat <<'EOF_ISSUE'
Como o compilador é hoje (lido no código decompilado):
- `PNCA/Compiler` tem cerca de 6.400 linhas, em uma só classe.
- Faz uma passada só, sem árvore sintática: um `switch` sobre o número do token escolhe um método por comando (`comFrente`, `comSe`...), que lê os próximos tokens e já emite instruções no `Montador`.
- O vocabulário está em tabelas (`TokenList`), nos três idiomas.

**Opções (a decisão é do dono do projeto):**
1. Evoluir o compilador atual: menor mudança, mas mexe numa classe enorme.
2. Criar um front-end novo (léxico, parser e árvore) que reaproveita o `Montador`, e manter o compilador antigo só para ler `.leg3` com aviso de legado.
3. Só melhorar mensagens, vocabulário e portas.

Restrição fixa: o firmware não muda, então tudo precisa virar o mesmo conjunto de instruções do `Montador`.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Códigos numéricos sem nome no compilador (ids de token)" --label "compilador" --body "$(cat <<'EOF_ISSUE'
O compilador compara tokens por número (por exemplo 106 para variável, 108 para inteiro, 109 para valor com unidade, 120 e 121 para `[` e `]`) e só os ids de 1 a 68 correspondem à posição na lista de palavras. Isso torna a leitura do código difícil.

**Tarefa:** dar nomes (constantes) a esses ids e documentar o significado de cada um, sem mudar o comportamento.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Ação: evolução do Aprenda e semântica de execução" --label "leg4" --body "$(cat <<'EOF_ISSUE'
Hoje a sub-rotina é `Aprenda <nome> [ ... ]`, que fica numa aba própria do IDE (`<BibSub>` no arquivo) e é chamada por nome (`cGoSub` no `Montador`). O leg4 propõe `Ação`, com `Ação principal` como entrada.

**Em aberto:**
- `andar()` termina antes de `detectar()` começar, ou podem rodar em paralelo? A direção indicada é o estilo Python/GDScript: sequencial por padrão, com execução paralela ou alternada quando fizer sentido.
- Hoje a concorrência são os 8 eventos (`<E1>` a `<E8>`), habilitados e desabilitados no programa.
- Tudo precisa virar o conjunto de instruções do `Montador` (o firmware não muda; a memória do robô é limitada a 4000).
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Vocabulário com palavras de duplo sentido (por exemplo ré)" --label "leg4,compilador" --body "$(cat <<'EOF_ISSUE'
Na tabela de palavras, `ré` é ao mesmo tempo o sentido de movimento (id 8) e a nota musical ré; `até` aparece duas vezes (ids 15 e 65). O compilador resolve pelo contexto.

**Tarefa:** listar as ambiguidades e decidir, para o leg4, se continuam resolvidas pelo contexto ou se ganham nomes distintos.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Compatibilidade com .leg3, aviso de tecnologia legada e casos de teste" --label "legado,leg4" --body "$(cat <<'EOF_ISSUE'
O leg4 deve continuar lendo `.leg3` (cabeçalho `LEGAL V05`; também há leitura de `V03` e `V04` no código; seções `<P>`, `<E1>` a `<E8>`, `<BibVar>`, `<BibSub>`, `<Fim>`; codificação latin1) e mostrar um aviso de que o formato é de uma tecnologia antiga.

Já existe precedente de quebra de compatibilidade: o IDE recusa arquivos do Legal 2011 e do Legal 2012 com uma mensagem dizendo que não são compatíveis com o Legal 3.

**Tarefas:**
- Reunir arquivos `.leg3` reais como casos de teste (compilar e comparar o código gerado antes e depois de qualquer mudança).
- Definir como e onde aparece o aviso de legado.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Blocos visuais e código textual juntos (execução paralela ou alternada)" --label "interface,leg4" --body "$(cat <<'EOF_ISSUE'
Objetivo: permitir misturar blocos visuais e código em texto, com execução paralela ou alternada, algo parecido com a relação entre Python e GDScript.

**Depende de:** a semântica de execução (issue sobre `Ação`) e da fase da interface.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Não precisar decorar as portas dos sensores (usar a detecção que o IDE já faz)" --label "interface,leg4" --body "$(cat <<'EOF_ISSUE'
Hoje o aluno precisa lembrar em qual porta (S1 a S8, ME/MD, servos) cada sensor fica. O IDE gera comentários `# Instalar ... em S2`, mas isso não resolve o incômodo.

**O que o IDE já faz:** na hora de enviar o programa, ele lê os sensores conectados ao robô e compara com os que o programa usa, mostrando erros como "Sensor de cor esperado em S4". Ou seja, a informação de qual sensor está em qual porta já está disponível.

**Ideias:**
- Declarar a porta uma vez e usar o nome depois (do CodJam: `Sensor_luz = s3`).
- Usar os sensores detectados para sugerir ou preencher a porta certa.
- Mapeamento das portas hoje concentrado na `TokenList`.

**Tarefa:** definir a sintaxe e a ajuda na interface, sem mudar o firmware.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Tipos e limites da linguagem que cabem no firmware" --label "leg4,compilador" --body "$(cat <<'EOF_ISSUE'
Limites atuais lidos no compilador e no montador:
- Inteiros de -32768 a 32767; só aparecem `inteiro` e `real` como tipos.
- `repita` de 1 a 200 vezes; tempos de 0,1 a 120; ângulos de servo de 0 a 180.
- Memória do programa do robô: 4000 (erro 56, "A memória do robô acabou").

O CodJam lista `texto`, `booleano`, `decimal`, `sensor`, `servo` e `motor`.

**Tarefa:** descobrir quais desses tipos e funções do Python cabem no conjunto de instruções do robô e quais só existiriam como açúcar sintático que o compilador traduz.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Mostrar o uso da memória do robô (limite de 4000)" --label "interface,leg4" --body "$(cat <<'EOF_ISSUE'
O `Montador` tem um limite de 4000 para o código enviado ao robô, e o erro 56 só aparece quando já estourou. Projetos maiores (que já levam dias, segundo o CodJam) esbarram nisso.

**Ideia:** mostrar no IDE quanto da memória o programa usa e avisar antes de estourar.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Inventariar os recursos do IDE que o leg4 precisa preservar" --label "interface,leg4" --body "$(cat <<'EOF_ISSUE'
Pelos textos da interface, além de programar o robô o IDE tem: modo Controlar, modo Coletar Dados, um simulador (que não suporta servos, LEDs, sensores de resistência, ruído, temperatura nem voltímetros) e conexão em rede com o computador do professor, com nome de equipe.

**Tarefa:** listar todos os recursos (lendo as classes de `Legal3`) para decidir o que o leg4 mantém, muda ou remove.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Versão do Java, execução em sistemas modernos e plataformas" --label "repositorio,interface" --body "$(cat <<'EOF_ISSUE'
As classes do `PNCA`, `Montador` e `PNCADevicePackage` foram compiladas para Java 6 e as de `Legal3` para Java 7. O repositório traz bibliotecas nativas de porta serial para Windows, Linux (x86, ARM, PowerPC), macOS e FreeBSD (NRJavaSerial), além de DLLs e `.so` antigos em `JarMateyNatives`.

**Tarefas:**
- Testar o `.jar` atual em um Java moderno e anotar o que falha.
- Definir a versão mínima de Java do leg4.
- Confirmar em quais sistemas a porta serial funciona.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Gerar pacotes de release automaticamente (GitHub Actions)" --label "repositorio" --body "$(cat <<'EOF_ISSUE'
Hoje não há processo de build nem de release: o repositório é o `.jar` descompactado.

**Depende de:** a decisão sobre a estrutura do repositório e a origem do código-fonte (issue sobre o `.jar` descompactado).

**Tarefa:** criar um workflow que gere o pacote e publique a release quando uma tag for criada.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Documentar a linguagem leg3 e o funcionamento do compilador" --label "documentacao" --body "$(cat <<'EOF_ISSUE'
Não há documentação do Legal. Uma primeira explicação da linguagem e do funcionamento do compilador foi escrita a partir da leitura do código decompilado.

**Tarefas:** guardar o documento no repositório (por exemplo em `docs/`), validar os exemplos compilando programas reais e marcar o que ainda está só deduzido do código.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Tutoriais de montagem e desafios para os alunos" --label "documentacao,leg4" --body "$(cat <<'EOF_ISSUE'
Do CodJam: as peças dos robôs são finas e frágeis, e não há muito o que mudar na montagem.

**Tarefa:** criar tutoriais ou manuais de algumas montagens para iniciantes e projetos simples, além de desafios mais difíceis para veteranos, com campeonatos internos e dinâmicas que engajem a turma.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Revisar a ortografia e fechar as lacunas do documento CodJam" --label "documentacao" --body "$(cat <<'EOF_ISSUE'
O documento `CodJem(leg4).md` tem vários erros de digitação (por exemplo "sinples", "nessesariamente", "aibda") e o nome do arquivo está como "CodJem". O próprio texto deixa em aberto a semântica de execução e as "Ações do sistema".

**Tarefa:** revisar o texto, padronizar o nome do projeto e transformar as lacunas em issues ou em decisões registradas.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de escrever (erro 9)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `Você esqueceu de escrever`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** sem mudança de texto (decisão da Etapa 4). O IDE acrescenta ao final o trecho que faltou, entre aspas.

**Tarefa:** conferir no Legal como a frase completa aparece.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "A palavra 'Invertido' deve ser usada junto com o 'Ligue Evento'. (erro 17)" --label "mensagens,erro-numerado" --body "$(cat <<'EOF_ISSUE'
**Texto atual:** `A palavra 'Invertido' deve ser usada junto com o 'Ligue Evento'.`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** sem mudança de texto (decisão da Etapa 4). Não encontrei nenhum ponto que dispare este erro no compilador, no montador nem nas classes do IDE que li.

**Tarefa:** confirmar se o erro é usado e, se não for, decidir se o código é removido.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de escrever o termo da atribuição. (erro 47)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de escrever o termo da atribuição.`

**Texto novo:** `Esse valor não pode ser usado nesta atribuição. Escreva um número, uma variável, verdadeiro, falso ou um sensor.`

**Uso:** 5 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Provocar o erro com um valor inválido numa atribuição e também com a unidade `bat`; o texto precisa servir aos dois casos.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de cor esperado em S (erro 91)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de cor esperado em S`

**Texto novo:** `Conecte o sensor de cor na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de luz esperado em S (erro 92)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de luz esperado em S`

**Texto novo:** `Conecte o sensor de luz na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de faixa esperado em S (erro 93)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de faixa esperado em S`

**Texto novo:** `Conecte o sensor de faixa na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um motor em M (erro 94)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar um motor em M`

**Texto novo:** `Conecte o motor na porta M`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta [E ou D, formando ME ou MD].

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar o servo (erro 95)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar o servo`

**Texto novo:** `Conecte o servo `

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta [número do servo].

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de indicar o sentido do motor (erro 96)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de indicar o sentido do motor`

**Texto novo:** `Faltou dizer o sentido do motor. Escreva "frente" ou "ré".`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Escrever o comando de motor sem "frente" ou "ré".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um sensor digital em S (erro 97)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar um sensor digital em S`

**Texto novo:** `Conecte um sensor digital na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um dispositivo externo em S (erro 98)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar um dispositivo externo em S`

**Texto novo:** `Conecte o dispositivo externo na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "O sensor de ruído só aceita valores entre 40 e 90 (erro 102)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `O sensor de ruído só aceita valores entre 40 e 90`

**Texto novo:** `O sensor de ruído só aceita valores entre 40 e 90.`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Já tem um sensor conectado em S (erro 105)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Já tem um sensor conectado em S`

**Texto novo:** `Duas partes do programa usam a mesma porta para sensores diferentes: S`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta [porta] seguido de (conflito com a aba e a linha).

**Como testar:** Usar a mesma porta para dois sensores diferentes em abas diferentes e conferir a frase completa.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você só pode enviar um inteiro ou uma variável (erro 106)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você só pode enviar um inteiro ou uma variável`

**Texto novo:** `Você só pode enviar um número inteiro ou uma variável.`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você precisa escrever o nome de uma variável aqui (erro 107)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você precisa escrever o nome de uma variável aqui`

**Texto novo:** `Você precisa escrever o nome de uma variável aqui.`

**Uso:** 2 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um sensor em S (erro 108)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar um sensor em S`

**Texto novo:** `Conecte um sensor na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você não habilitou ou desabilitou o Evento (erro 109)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você não habilitou ou desabilitou o Evento`

**Texto novo:** `Faltou escrever "habilite evento" ou "desabilite evento" para o evento `

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta [número do evento].

**Como testar:** Conferir o texto no contexto real do disparo (foi proposto sem ler o contexto completo).
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "A expressão 'entre' tem a forma: <Variável> entre <Inteiro> e <Inteiro> (erro 117)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `A expressão 'entre' tem a forma: <Variável> entre <Inteiro> e <Inteiro>`

**Texto novo:** `A expressão "entre" é escrita assim: variável entre inteiro e inteiro. Exemplo: x entre 1 e 10.`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Conferir que o exemplo "x entre 1 e 10" é aceito pelo compilador.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Você esqueceu de conectar um voltímetro em S (erro 133)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Você esqueceu de conectar um voltímetro em S`

**Texto novo:** `Conecte o voltímetro na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de resistência esperado em S (erro 134)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de resistência esperado em S`

**Texto novo:** `Conecte o sensor de resistência na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de temperatura esperado em S (erro 135)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de temperatura esperado em S`

**Texto novo:** `Conecte o sensor de temperatura na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de ruído esperado em S (erro 136)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de ruído esperado em S`

**Texto novo:** `Conecte o sensor de ruído na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Sensor de distância esperado em S (erro 137)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Sensor de distância esperado em S`

**Texto novo:** `Conecte o sensor de distância na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Protótipo de sensor analógico esperado em S (erro 139)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Protótipo de sensor analógico esperado em S`

**Texto novo:** `Conecte o sensor analógico (protótipo) na porta S`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal. Depois do texto o programa acrescenta o número da porta (`S4`, por exemplo); aparece ao enviar o programa ao robô.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "As saídas válidas de PWM são de PWM 1 até PWM 4 (erro 143)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `As saídas válidas de PWM são de PWM 1 até PWM 4`

**Texto novo:** `As saídas de PWM válidas vão de PWM 1 até PWM 4.`

**Uso:** nenhum uso direto no `PNCA/Compiler` (pode ser disparado pelo IDE, pelo `Montador` ou por código que calcula o número do erro).

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Provocar o erro e conferir a mensagem na tela.
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Faltou a palavra 'graus'. (erro 154)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Faltou a palavra 'graus'.`

**Texto novo:** `Depois de um número igual ou maior que 2, escreva "graus".`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Compilar um comando de servo com 2 graus ou mais sem a palavra "graus".
EOF_ISSUE
)"
gh issue create --repo "$REPO" --title "Faltou a palavra 'grau'. (erro 155)" --label "mensagens,erro-numerado,aguardando-teste" --body "$(cat <<'EOF_ISSUE'
**Texto antigo:** `Faltou a palavra 'grau'.`

**Texto novo:** `Depois de 0 ou 1, escreva "grau" (no singular).`

**Uso:** 1 ponto(s) de uso no `PNCA/Compiler`.

**Situação:** texto novo aplicado em `leg4-mensagens-pt-etapa5.patch`, aguardando teste no Legal.

**Como testar:** Compilar um comando de servo com 0 ou 1 grau sem a palavra "grau".
EOF_ISSUE
)"
