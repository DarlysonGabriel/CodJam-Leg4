# leg4 – Registro da Etapa 6: base de testes, código reconstruído e correções

## 1. Resumo

- **Base de testes criada** (`tests/`): roda o compilador do Legal sem interface e sem robô sobre 108 casos e compara com o resultado do compilador **original**.
- **Código reconstruído** (`src-reconstruido/`): o `PNCA` e o `Montador` decompilados, com 2 reparos manuais, recompilam para Java 6 e dão resultado idêntico ao original nos 108 casos.
- **Os três patches de mensagens não mudam o comportamento do compilador:** com eles aplicados, os 108 casos continuam idênticos.
- **Duas mensagens minhas estavam erradas** e foram corrigidas no patch da Etapa 6: erros 16 e 109.
- **O documento da linguagem foi corrigido** (segunda versão), porque os testes mostraram erros na primeira.

## 2. Correções do patch da Etapa 6

Os testes mostraram que os eventos são identificados pelo **sensor da porta** (`habilite evento s1`), e não por número. Isso invalidou dois textos que eu tinha escrito.

| Erro | Antes (Etapas 3 e 5) | Depois |
|---|---|---|
| 16 | Faltou dizer qual evento ativar. Escreva o número do evento, de 1 a 8. | Faltou dizer qual evento ativar. Escreva o sensor do evento, de s1 a s8. |
| 109 | Faltou escrever "habilite evento" ou "desabilite evento" para o evento [número] | Faltou escrever "habilite evento" ou "desabilite evento" para o evento do sensor S[número] |

## 3. O que os testes revelaram na linguagem

- `motor` aceita velocidade em porcentagem (`Motor me frente 50%`), não `lento` ou `rápido`.
- `ligue` e `desligue` controlam **saídas** (`Ligue saída1`), não luzes. Luzes são `acenda` e `apague` (`l1` a `l6`).
- `espere até` aceita cor (`Espere até cor vermelha`), mas não `obstáculo`.
- `se` e `enquanto` aceitam comparações com sensores (`se s1 > 5 então [ ... ]`); `se obstáculo` não.
- `repita 0 vezes` é recusado, embora a mensagem do erro 33 diga "entre 0 e 200".
- O evento N é o do sensor SN.

## 4. Propostas ainda não aplicadas (precisam da sua aprovação)

| Erro | Situação | Proposta |
|---|---|---|
| 33 | A mensagem diz "entre 0 e 200", mas o compilador recusa 0 | O número de repetições deve estar entre 1 e 200. |
| 74 | "Qual evento devo desligar?" aparece em `desabilite evento 1` | Faltou dizer qual evento desativar. Escreva o sensor do evento, de s1 a s8. |

## 5. Como aplicar e testar

1. `bash patch/aplicar-patches.sh` aplica os três patches em ordem, numa branch nova (já pula os que estiverem aplicados).
2. `bash tests/rodar-testes.sh` confere que o comportamento do compilador continua o mesmo.

## 6. Limites

- Os testes não cobrem as abas de variáveis e sub-rotinas, `inclua`, a coleta de dados nem a verificação do erro 109 do IDE.
- Só existe um programa `.leg3` real; os demais casos foram escritos a partir do código.
- Nada foi testado no IDE nem com o robô, e os testes só rodaram com o JDK 8.
