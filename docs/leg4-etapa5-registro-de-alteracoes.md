# leg4 – Registro de alterações (Etapa 5: erros numerados em português)

Este documento diz exatamente o que mudou, o que não mudou, como foi verificado e como aplicar. Nada foi gravado no seu repositório: tudo foi feito numa cópia, e você aplica os patches quando quiser.

## 1. Resumo

- **25 mensagens de erro em português** foram reescritas, conforme você aprovou na Etapa 4. Os erros **9** e **17** ficaram como estão (a proposta era "sem mudança").
- **Só um arquivo compilado mudou nesta etapa:** `PNCA/ErrorMessages.class`. A lógica do compilador não foi tocada.
- **Nenhum efeito colateral:** diferente da Etapa 3, desta vez nenhum texto era compartilhado com o inglês ou o espanhol, então as tabelas desses idiomas ficaram idênticas.
- Somando a Etapa 3, já são **52 mensagens** em português reescritas (27 + 25) e 2 erros de digitação do IDE corrigidos.

## 2. As 25 mensagens (antes e depois)

Nas mensagens que recebem complemento do programa, o texto termina exatamente onde o complemento entra, indicado entre colchetes.

| Erro | Antes | Depois |
|---|---|---|
| 47 | Você esqueceu de escrever o termo da atribuição. | Esse valor não pode ser usado nesta atribuição. Escreva um número, uma variável, verdadeiro, falso ou um sensor. |
| 91 | Sensor de cor esperado em S | Conecte o sensor de cor na porta S [número] |
| 92 | Sensor de luz esperado em S | Conecte o sensor de luz na porta S [número] |
| 93 | Sensor de faixa esperado em S | Conecte o sensor de faixa na porta S [número] |
| 94 | Você esqueceu de conectar um motor em M | Conecte o motor na porta M [E ou D] |
| 95 | Você esqueceu de conectar o servo | Conecte o servo [número] |
| 96 | Você esqueceu de indicar o sentido do motor | Faltou dizer o sentido do motor. Escreva "frente" ou "ré". |
| 97 | Você esqueceu de conectar um sensor digital em S | Conecte um sensor digital na porta S [número] |
| 98 | Você esqueceu de conectar um dispositivo externo em S | Conecte o dispositivo externo na porta S [número] |
| 102 | O sensor de ruído só aceita valores entre 40 e 90 | O sensor de ruído só aceita valores entre 40 e 90. |
| 105 | Já tem um sensor conectado em S | Duas partes do programa usam a mesma porta para sensores diferentes: S [número] (conflito com a aba e a linha) |
| 106 | Você só pode enviar um inteiro ou uma variável | Você só pode enviar um número inteiro ou uma variável. |
| 107 | Você precisa escrever o nome de uma variável aqui | Você precisa escrever o nome de uma variável aqui. |
| 108 | Você esqueceu de conectar um sensor em S | Conecte um sensor na porta S [número] |
| 109 | Você não habilitou ou desabilitou o Evento | Faltou escrever "habilite evento" ou "desabilite evento" para o evento [número] |
| 117 | A expressão 'entre' tem a forma: <Variável> entre <Inteiro> e <Inteiro> | A expressão "entre" é escrita assim: variável entre inteiro e inteiro. Exemplo: x entre 1 e 10. |
| 133 | Você esqueceu de conectar um voltímetro em S | Conecte o voltímetro na porta S [número] |
| 134 | Sensor de resistência esperado em S | Conecte o sensor de resistência na porta S [número] |
| 135 | Sensor de temperatura esperado em S | Conecte o sensor de temperatura na porta S [número] |
| 136 | Sensor de ruído esperado em S | Conecte o sensor de ruído na porta S [número] |
| 137 | Sensor de distância esperado em S | Conecte o sensor de distância na porta S [número] |
| 139 | Protótipo de sensor analógico esperado em S | Conecte o sensor analógico (protótipo) na porta S [número] |
| 143 | As saídas válidas de PWM são de PWM 1 até PWM 4 | As saídas de PWM válidas vão de PWM 1 até PWM 4. |
| 154 | Faltou a palavra 'graus'. | Depois de um número igual ou maior que 2, escreva "graus". |
| 155 | Faltou a palavra 'grau'. | Depois de 0 ou 1, escreva "grau" (no singular). |

As mensagens 95 e 109 terminam com um espaço de propósito, porque o programa cola o número logo depois.

## 3. O que não foi alterado

- A lógica do compilador, do montador e do IDE.
- Os erros 9 e 17 (sem mudança, conforme a proposta).
- Inglês e espanhol, que continuam com as inconsistências já listadas (por exemplo "between 1 and 180" no inglês e "Erro Fatal." em português na tabela espanhola).
- Qualquer outro arquivo do repositório.

## 4. Como foi verificado

- As 25 mensagens ficaram exatamente como aprovadas e as outras 131 do português continuam iguais.
- As tabelas de inglês e espanhol ficaram idênticas ao estado anterior, e o restante do arquivo (o código que monta as tabelas) não teve nenhum byte alterado.
- Uma leitura independente com o decompilador confirmou o resultado.
- Testei a aplicação dos patches num repositório temporário: o patch acumulado aplica sem conflito na versão original, e a sequência "patch da Etapa 3 + patch da Etapa 5" gera exatamente os mesmos 5 arquivos finais.

## 5. O que não foi testado

Eu não consigo abrir o Legal nem usar o robô, e não executei o programa. Os pontos que mais merecem teste são os que dependem de como o programa completa o texto:

1. **Erros de sensor (91 a 98, 133 a 137, 139):** só aparecem ao enviar o programa ao robô, com o sensor ausente ou na porta errada. Confira que o final fica "…na porta S4" (ou "M" seguido de E ou D no motor).
2. **105:** use a mesma porta para dois sensores diferentes em abas diferentes e confira a frase completa com a aba e a linha.
3. **109:** o texto foi proposto sem ler o contexto completo do disparo. Confira se faz sentido para a situação real.
4. **47:** provoque o erro com um valor inválido e também com a unidade `bat`, e veja se o texto serve aos dois casos.
5. **117 e 96:** confira se o exemplo "x entre 1 e 10" e as palavras "frente" e "ré" são aceitos pelo compilador.
6. **154 e 155:** teste um servo com 1 grau, 2 graus e 0 grau.

## 6. Como aplicar

Escolha **uma** das formas, de acordo com o que você já fez no repositório:

- **Se ainda não aplicou a Etapa 3:** use `leg4-mensagens-pt-acumulado.patch` (Etapas 3 e 5 juntas) com `git apply`, ou copie as 5 `.class` do `leg4-mensagens-pt-acumulado-classes.zip` por cima das originais, mantendo as pastas.
- **Se já aplicou a Etapa 3:** use só `leg4-mensagens-pt-etapa5.patch`.

Faça uma cópia de segurança antes. Para desfazer, `git checkout -- .` volta ao estado anterior.

## 7. Observações

- Na cópia de trabalho do meu ambiente havia um commit local de "Mentora", com exatamente o conteúdo da Etapa 3 (conferido arquivo por arquivo). Usei esse estado como base. Também existem na pasta de saída arquivos que eu não criei (`issues-leg4.md` e `publicar-leg4.sh`). Eu não li nem executei nenhum dos dois.
- As issues dos 27 erros numerados continuam descrevendo o texto como pendente. Depois que você testar e aplicar, elas podem ser fechadas.
