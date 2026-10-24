# Base de testes do compilador do Legal

Roda o compilador do Legal (`PNCA` e `Montador`) **sem interface e sem robô** sobre uma lista de programas e compara o código gerado com um resultado esperado.

## Como rodar

Com um JDK instalado (testado com o JDK 8), a partir da pasta do pacote:

```
bash tests/rodar-testes.sh
```

O script procura sozinho o repositório (a pasta com `PNCA/` e `Montador/`), seja a própria pasta do pacote, seja a pasta de cima. Se o pacote estiver em outro lugar, informe o caminho: `bash tests/rodar-testes.sh /caminho/do/repositorio`.

O script faz dois testes:

1. **Classes do repositório:** as `.class` que o programa realmente usa. Serve para garantir que patches e alterações não mudam o comportamento do compilador.
2. **Código reconstruído:** o código em `src-reconstruido/`, recompilado na hora. Serve para saber se a reconstrução continua fiel.

## O que há aqui

| Arquivo | Para que serve |
|---|---|
| `casos.txt` | 108 casos: o programa real de exemplo, variações de cada comando e programas com erro. Cada caso começa com `#### nome`; as seções `@@E1` a `@@E8` guardam o código dos eventos. |
| `saida_esperada.txt` | Resultado do compilador **original**, sem nenhum patch: erro, linha, memória usada, sensores exigidos e código gerado. |
| `Harness.java` | Programa de teste: repete a ordem de compilação do IDE para o programa principal e os eventos. |
| `rodar-testes.sh` | Compila e roda tudo. |

## Limites

- Não cobre as abas de variáveis e de sub-rotinas (`variável`, `defina`, `Aprenda`) nem `inclua`.
- Não repete a verificação do erro 109 nem a conferência de sensores no envio ao robô.
- O texto das mensagens de erro não entra na comparação: só o número do erro, a linha e o código gerado.
- Caracteres fora do ASCII que aparecem no código gerado (por exemplo `Ë`) são impressos como `\u00cb`, para o resultado não depender da codificação do terminal nem da versão do Java.
- Os casos foram escritos a partir da leitura do código e de um único programa real; um conjunto de arquivos `.leg3` de verdade melhoraria muito a cobertura.

## Como adicionar um caso

Acrescente um bloco ao `casos.txt` e gere o resultado com o compilador original. Só atualize `saida_esperada.txt` quando a mudança de comportamento for intencional.
