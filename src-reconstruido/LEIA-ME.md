# Código reconstruído do compilador (PNCA e Montador)

**Isto não é o código-fonte original do Legal.** O original não está disponível. Estes arquivos foram gerados por decompilação das classes `PNCA/*.class` e `Montador/*.class` deste repositório (commit `9544a18`) com o decompilador CFR 0.152.

## Reparos manuais

O decompilador deixou dois trechos que não são Java válido, e eles foram refeitos à mão em `PNCA/Compiler.java` (o diff está em `reparos-manuais.diff`):

1. `identifyNumber`: o método tinha saltos (`goto`) sem equivalente em Java. Foi reescrito com a mesma lógica.
2. `scanAndGenerateAllSubs`: um `break` inalcançável foi removido.

## Verificação

Compilado com o JDK 8 em modo Java 6, o código gera 18 classes, o mesmo número do original, e produz **resultado idêntico ao compilador original em 108 casos de teste** (ver `tests/`). Isso mostra fidelidade nos casos testados, não prova equivalência total.

## Cuidados

- Os nomes das variáveis locais foram perdidos na decompilação (`c2`, `bl` etc.).
- Não edite estes arquivos achando que são o código original; qualquer mudança deve ser acompanhada de `tests/rodar-testes.sh`.
- Os comentários de licença e crédito originais não estão preservados (ver a issue sobre licenças e créditos).
