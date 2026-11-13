# Editor CodJam — tema escuro para o IDE Legal

Este pacote aplica o tema escuro roxo da logo CodJam **ao IDE original do Legal** (telas de abertura, menu Programar/Controlar/Coletar Dados, editor de código e diálogos), sem reescrever nenhuma tela. Roda em X11 (Linux com Xorg ou XWayland), usando o Java 8 ou mais novo que a biblioteca de edição do repositório já exige.

## Como abrir

Na raiz do repositório:

```
bash editor/rodar-editor.sh
```

Na primeira vez, ele gera o tema (recolore as imagens do IDE e cria a logo de abertura) e guarda em `~/.codjam/tema-gerado`; nas próximas vezes só reusa o que já gerou. Para forçar gerar de novo, apague essa pasta.

## O que foi feito

- **Logo e ícone:** a logo enviada aparece na tela de carregamento e como ícone de todas as janelas do IDE.
- **Interface original inteira recolorida**, não só o editor: a tela de abertura, o menu Programar/Controlar/Coletar Dados (com os robôs e os cartões), a barra lateral de comandos do modo Programar, os diálogos e o editor de texto. A recoloração troca o brilho de cada pixel por um ponto no gradiente preto → roxo → branco da paleta enviada, preservando transparência, sombras e formas.
- **Sem aba inicial no editor:** ao entrar em Programar, o editor já aparece pronto.
- **Botão direito no editor:** Desfazer, Copiar, Colar, **Robô ▸ Controlar / Coletar dados** (troca de modo pelo próprio menu, sem precisar fechar e escolher no menu principal) e Propriedades.
- **Propriedades:** número da linha, linha atual, quebra de linha, espaços e tabs, tamanho da fonte e do tab. Fica gravado em `~/.codjam/editor.properties` e vale também para os editores dos outros modos (eventos, sub-rotinas).

## Como foi verificado

Testei abrindo o IDE de verdade numa tela X11 (Xvfb), com mouse e teclado simulados: a tela de abertura com a logo, o menu principal, a entrada no modo Programar com a barra lateral original, o menu de contexto com o submenu Robô, a troca para "Coletar Dados" pelo próprio menu (o IDE realmente trocou de tela) e a janela de Propriedades. As capturas estão em `screenshots/`.

## Como funciona (para quem for mexer no código depois)

- `Tema.java` define as três cores-base e aplica no Swing padrão (`UIManager`) para os diálogos.
- `MapaDeCores.java` tem a função de recoloração (luminosidade → ponto no gradiente), usada tanto para gerar os arquivos de imagem quanto, em tempo real, pelas telas que o IDE desenha por código (pasta `Legal3/Painters`).
- `GeradorTema.java` varre `Legal3/images/`, `Legal3/Painters/painter-images/` e `images/` do repositório, recolore cada PNG/GIF/JPG preservando o alfa, e desenha a logo de abertura e os ícones.
- `src-pintores/Legal3/Painters/` é uma cópia **decompilada e ajustada** das classes que desenham o menu principal por código (gradientes, cantos arredondados). A única mudança de comportamento é que a classe `Color` delas agora devolve cores do tema; a lógica de desenho é a mesma.
- `IntegracaoIde.java` observa as janelas do IDE abrirem (sem alterar o `Legal3.Legal` original) para: pôr o ícone, pintar o fundo da janela principal e instalar o menu de contexto em cada editor de código que aparecer.
- `CodJam.java` é o novo ponto de entrada: instala o tema e chama o `Legal3.Legal.main` original.

## Limites

- A fonte do editor continua a monoespaçada padrão do Java; a fonte do IDE original (pedida na imagem do mapa de cores) ainda não foi identificada.
- A recoloração troca **cor por luminosidade**; uma imagem que já fosse roxa ou preta pode mudar de tom sutilmente.
- O código em `src-pintores/` foi obtido por decompilação (não é o fonte original) porque o fonte do Legal não está disponível; qualquer ajuste nele deve ser conferido visualmente.
- Só testei no JDK 8 (bytecode 52, que a `RSyntaxTextArea` do repositório já exige) e numa tela X11 virtual. As bibliotecas nativas de porta serial para Linux (inclusive `x86_64`) já estão no repositório, mas não testei a comunicação com o robô.
