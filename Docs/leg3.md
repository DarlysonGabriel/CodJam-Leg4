# A linguagem LEGAL (leg3) e como o programa funciona

Este documento explica como a linguagem do Legal funciona hoje e como o programa transforma o que o aluno escreve em ordens para o robô. Ele foi escrito a partir da leitura do código decompilado (`PNCA`, `Montador` e as classes do IDE em `Legal3`), **sem executar o programa nem usar o robô**.

Como a confiança varia, cada afirmação importante vem marcada:

- **Visto:** li no código ou nos textos do programa.
- **Deduzido:** conclusão a partir do código, ainda sem teste.
- **A confirmar:** não consegui verificar.

## 1. Visão geral

O Legal tem três partes que trabalham em sequência:

1. **O IDE (`Legal3`):** a janela onde o aluno escreve o programa, em abas, e que também conversa com o robô.
2. **O compilador (`PNCA` + `Montador`):** lê o texto, confere se está certo e gera as instruções do robô.
3. **O robô módulo:** recebe as instruções por porta serial (USB) e as executa com o firmware dele.

```
texto do aluno (.leg3)
      │  abas: variáveis, Aprenda, principal, eventos 1 a 8
      ▼
IDE (CompilerThread) ── chama o compilador aba por aba
      ▼
Compiler: lê palavra por palavra e escolhe um método por comando
      ▼
Montador: monta as instruções do robô (texto curto, uma por linha)
      ▼
IDE: confere os sensores conectados e envia pela serial
      ▼
firmware do robô executa
```

## 2. O arquivo `.leg3`

**Visto:**

- O arquivo é texto na codificação `latin1`. O cabeçalho é `LEGAL V05`. O código também sabe ler os cabeçalhos `V03` e `V04`.
- Bibliotecas usam a extensão `.bib3` e o cabeçalho `LEGAL LIB V05`.
- O arquivo é dividido em seções marcadas por etiquetas:

| Etiqueta | O que guarda |
| --- | --- |
| `<P>` | O programa principal |
| `<E1>` a `<E8>` | O código de cada um dos 8 eventos |
| `<BibVar>` | A aba de variáveis e definições |
| `<BibSub>` | A aba das sub-rotinas (`Aprenda`) |
| `<Fim>` | Fim do arquivo |

Uma correção: na Etapa 1 eu tinha deduzido que `<BibVar>` e `<BibSub>` eram "variáveis e sub-rotinas importadas". A ordem de compilação do IDE mostra que são, na verdade, as abas de variáveis e de sub-rotinas do próprio programa. O prefixo "Bib" sugere que essas duas abas também podem ser guardadas como biblioteca (`.bib3`) e reaproveitadas com `inclua` (**deduzido**).

**Compatibilidade já existente (visto):** o IDE recusa arquivos do Legal 2011 e do Legal 2012, com uma mensagem dizendo que não são compatíveis com o Legal 3. Ou seja, o Legal já quebrou compatibilidade com versões antigas uma vez.

## 3. Como o programa é escrito

### 3.1 Estrutura geral (visto)

- O programa principal fica entre `Por favor` e `Obrigado` (ou `Obrigada`).
- **Blocos** usam colchetes `[` e `]`, não indentação. O compilador conta os blocos abertos, e um `[` ou `]` esquecido gera erro.
- **Comentários:** uma linha que começa com `#` é comentário. Chaves `{ ... }` também são tratadas como comentário pelo leitor.
- **Maiúsculas e minúsculas não importam:** o compilador converte as palavras para maiúsculas antes de comparar.
- **Os três idiomas:** existem tabelas de palavras em português, inglês e espanhol. O idioma escolhido decide qual tabela vale.

### 3.2 Exemplo real, explicado

Programa do arquivo de exemplo, com o que cada linha faz:

```
Por favor
	# Instalar motores em ME e MD
	# Instalar os sensores de faixa em S2 e S6
	# Instalar sensor de contato ou proximidade em S1
	# Instalar o sensor de cor em S4
	Siga faixa escura até obstáculo lento
	ré 1s
	giro direita 1,5s
	Frente até obstáculo
	giro direita 1,5s
	Siga faixa escura até cor vermelha lento
Obrigado
```

- As quatro linhas `# Instalar ...` são **comentários gerados pelo IDE** (**deduzido**): quando o compilador percebe quais sensores o programa usa, o IDE escreve esses lembretes de montagem.
- `Siga faixa escura até obstáculo lento`: o robô segue a faixa escura, devagar, até o sensor de contato ou proximidade acusar um obstáculo.
- `ré 1s`: anda para trás por 1 segundo.
- `giro direita 1,5s`: gira para a direita por 1,5 segundo. O número usa vírgula.
- `Frente até obstáculo`: anda para a frente até encontrar um obstáculo.
- `Siga faixa escura até cor vermelha lento`: segue a faixa até o sensor de cor ver vermelho.

### 3.3 Os comandos

| Comando | Para que serve | Certeza |
| --- | --- | --- |
| `frente`, `ré` | Anda para a frente ou para trás, por um tempo (`ré 1s`) ou até uma condição (`frente até obstáculo`) | Visto |
| `giro direita` / `giro esquerda` | Gira no lugar, por um tempo ou até uma condição | Visto |
| `curva` | Faz o robô virar em curva | Visto (forma exata a confirmar) |
| `pare` | Para os motores | Visto |
| `siga luz`, `siga faixa` | Segue uma luz ou uma faixa (clara, escura ou de uma cor), com `até <condição>` e velocidade `lento`, `normal` ou `rápido` | Visto |
| `fuja` ... `luz` | Foge da luz (o erro 26 cobra a palavra `luz`) | Visto |
| `motor me` / `motor md` | Controla um motor com sentido (`frente` ou `ré`) e velocidade. Sem velocidade, o padrão é 70 | Visto |
| `motores` | Palavra existente na tabela; uso exato a confirmar | A confirmar |
| `servo1` a `servo4` | Move um servo para um ângulo de 0 a 180, escrito com `grau` ou `graus` (`grau` para 0 e 1, `graus` para 2 ou mais). Depois de mover, o robô espera 0,7 s | Visto |
| `acenda` / `apague` | Acende e apaga luzes (`l1` a `l6`) | Visto (forma exata a confirmar) |
| `ligue` / `desligue` | Liga e desliga LEDs, luzes e o sensor de distância (`ligue sensor distância`) | Visto em parte |
| `som alarme`, `som aviso`, `som explosão` | Toca um efeito sonoro. Não aceita tempo depois | Visto |
| `nota` | Toca uma nota por um tempo. Existem notas de 4 oitavas, com `b` para bemol (`réb`, `solb`...) | Visto |
| `mudo` | Silencia o som | Visto |
| `espere` | Espera por um tempo (`espere 2s`) ou até uma condição (`espere até ...`) | Visto |
| `repita sempre [ ... ]` | Repete o bloco para sempre | Visto |
| `repita N vezes [ ... ]` | Repete o bloco N vezes, de 1 a 200 | Visto |
| `se <condição> então [ ... ] senão [ ... ]` | Decide entre dois caminhos | Visto (o bloco do `senão` é deduzido) |
| `enquanto <condição> [ ... ]` | Repete enquanto a condição valer | Visto |
| `e`, `ou` | Combinam condições | Visto |
| `entre` | Condição de intervalo: `variável entre inteiro e inteiro` | Visto |
| `aleatório` | Sorteia um valor (`de` ... `até`) | Deduzido |
| `habilite evento`, `desabilite evento` | Liga e desliga um dos 8 eventos, que reagem a sensores. Aceita `invertido`, `verdadeiro` e `falso` | Visto (forma completa a confirmar) |
| `ative`, `intervalo`, `coletar`, `fim da coleta` | Coleta de dados dos sensores | Deduzido |
| `envie`, `receba`, `recebeu` | Palavras de comunicação; não li o tratamento | A confirmar |
| `inclua` | Importa uma biblioteca `.bib3`. O compilador ignora a linha, e o IDE faz a inclusão antes | Visto |

### 3.4 Variáveis, definições e sub-rotinas (visto)

- **`variável nome`** declara uma variável. Só vale na aba de variáveis. O nome não pode ser uma palavra reservada nem um nome já usado.
- **`defina nome texto`** guarda o resto da linha como uma substituição de texto (**deduzido**: funciona como um apelido).
- **`Aprenda nome [ ... ]`** declara uma sub-rotina. Só vale na aba de sub-rotinas. Para chamar, basta escrever o nome dela no programa.
- Cada aba aceita só o que é dela: `Aprenda` na aba de variáveis e `variável` na aba de sub-rotinas dão erro.

### 3.5 Sensores, portas e valores (visto)

- **Portas de sensor:** `s1` a `s8`. **Motores:** `ME` e `MD`. **Servos:** `servo1` a `servo4`. **Luzes:** `l1` a `l6`. **Saídas:** `saída1` a `saída8`. **PWM:** `p1` a `p4`.
- **Sensores citados no código:** luz, som, faixa, cor, distância, contato e proximidade (`obstáculo`), temperatura, ruído, resistência, voltímetro.
- **Cores:** azul, vermelha, amarela, verde, branca, preta, indeterminada, e `clara` e `escura` para faixas.
- **Unidades:** `cm`, `V`, `Ohm`, `C`, `dB`, `lx`, `s`, `min`, com os multiplicadores `k` (mil) e `m` (milhão) (**deduzido** o uso dos multiplicadores).

### 3.6 Limites (visto)

| Item | Limite |
| --- | --- |
| Inteiros | -32768 a 32767 |
| `repita N vezes` | 1 a 200 |
| Tempo | 0,1 a 120 |
| Ângulo de servo | 0 a 180 |
| Sensor de ruído | 40 a 90 |
| Leitura analógica de eventos | 0 a 1023 |
| Memória do programa no robô | 4000 (acima disso, erro 56) |

## 4. Como o compilador funciona por dentro

**Visto:**

1. **Ordem das abas.** O IDE compila as inclusões, depois a aba de variáveis, a de sub-rotinas, o programa principal e, por fim, os eventos de 1 a 8.
2. **Leitura.** O `TextScanner` lê o texto linha a linha. A função `lexico` devolve a próxima palavra e uma lista com o tipo dela (palavra da linguagem, nome, número inteiro, número com unidade, operador, `[`, `]`...).
3. **Escolha do comando.** `waitCommand` olha o tipo da primeira palavra e chama o método do comando: `comFrente`, `comSe`, `comEnquanto`, `instRepita`, `comportSeguir` e assim por diante.
4. **Geração.** Cada método confere o resto do comando e já pede ao `Montador` a instrução correspondente. Não existe uma árvore sintática: o texto vira instrução direto, numa passada só.
5. **Erros.** Qualquer problema chama `setErrorCode` com um número. O IDE usa o número para buscar a mensagem na tabela do idioma e, em alguns erros, acrescenta um complemento (porta, evento, texto que faltou).
6. **Sensores usados.** Quando um comando precisa de um sensor, o compilador avisa o IDE (`generateCommandSensorEvent`). É assim que o IDE sabe quais sensores o programa exige.

## 5. O que o robô recebe

**Visto:**

- O `Montador` gera instruções curtas em texto, cada uma terminada por quebra de linha. Exemplos: `CP` (pare), `CL` (siga faixa preta), `C.` (fim do programa), `C!` (fim de um evento).
- O código dos eventos vem separado por marcadores `M1`, `M2` até `M8`.
- O limite de 4000 vale para esse código.
- Os comandos `repita`, as sub-rotinas (`cGoSub`, retorno de sub-rotina) e as esperas viram instruções do `Montador`.

**Antes de enviar (visto):** o IDE pede ao robô a lista de sensores conectados e compara com os que o programa usa. Se faltar algum, mostra "Conecte o sensor de cor na porta S4" (ou semelhante). Só depois envia pela serial. Existe também um **simulador** que recebe o mesmo código, mas não suporta servos, LEDs, sensores de resistência, ruído, temperatura nem voltímetros.

## 6. O que o IDE oferece além de programar

**Visto, pelos textos da interface:**

- Modo **Programar**, modo **Controlar** e modo **Coletar Dados**.
- **Conexão em rede com o computador do professor**, com escolha do nome da equipe, para enviar o programa do aluno.
- Interface em português, inglês e espanhol.
- Aviso quando o módulo conectado é um módulo de controle do Legal 2011.

## 7. Pontos de atenção

- **`ré` tem dois sentidos:** é o sentido de movimento e também uma nota musical. O compilador resolve pelo contexto. `até` também aparece duas vezes na tabela de palavras.
- **Os números dos tokens** (106 para variável, 108 para inteiro, 120 e 121 para os colchetes) não têm nome no código. Só os ids de 1 a 68 correspondem à posição na lista de palavras.
- **O compilador é uma classe só**, com cerca de 6.400 linhas. Isso explica a dificuldade de manter projetos complexos que o CodJam descreve.
- **Várias mensagens de erro** nunca são disparadas pelo compilador; vêm do IDE ou nem são usadas (ver as issues dos erros 17 e 108).
- **O firmware é fixo:** qualquer coisa nova no leg4 precisa virar as mesmas instruções.

## 8. O que ainda falta confirmar

1. A forma exata de `curva`, `motores`, `acenda`/`apague`, `ligue`/`desligue` e dos comandos de evento (`habilite evento`).
2. Como funcionam `envie`, `receba`, `recebeu` e os comandos de coleta de dados.
3. O bloco do `senão` e as regras completas de atribuição e de expressões.
4. Como o IDE gera os comentários `# Instalar ...`.
5. A tabela completa de instruções que o `Montador` gera e o formato do envio na serial.

A melhor forma de fechar essas lacunas é compilar os arquivos `.leg3` reais e comparar o código gerado antes e depois de qualquer mudança. Isso também serve de teste de compatibilidade para o leg4.

## 9. Como isso se liga ao leg4

- A **`Ação`** do CodJam corresponde ao `Aprenda` de hoje, que já é chamado por nome e fica em aba própria.
- A concorrência que existe hoje são os **8 eventos**. A "execução paralela ou alternada" do leg4 precisa se apoiar neles, porque o firmware não muda.
- O **problema das portas** pode usar a detecção de sensores que o IDE já faz no envio.
- Os **limites** (inteiros de 16 bits, memória de 4000) definem quais tipos e funções do Python cabem no leg4.
- O **aviso de tecnologia legada** pode seguir o modelo das mensagens que o IDE já mostra para arquivos do Legal 2011 e 2012.