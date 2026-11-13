package codjam.editor;

import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.imageio.ImageIO;

public final class GeradorTema {

    public static final String VERSAO = "1";

    private static final String[] PASTAS_DE_IMAGENS = {"Legal3/images", "Legal3/Painters", "images"};
    private static final String[] IDIOMAS = {"br", "eng", "esp"};

    private GeradorTema() {
    }

    public static void main(String[] argumentos) throws IOException {
        if (argumentos.length == 1 && argumentos[0].equals("--versao")) {
            System.out.println(VERSAO);
            return;
        }
        if (argumentos.length < 3) {
            System.err.println("Uso: GeradorTema <repositorio> <destino> <logo.png>");
            System.exit(1);
        }
        final Path repositorio = Paths.get(argumentos[0]);
        final Path destino = Paths.get(argumentos[1]);
        BufferedImage logo = ImageIO.read(new File(argumentos[2]));

        final int[] total = {0};
        for (String pasta : PASTAS_DE_IMAGENS) {
            Path raiz = repositorio.resolve(pasta);
            if (!Files.isDirectory(raiz)) {
                continue;
            }
            Files.walkFileTree(raiz, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path arquivo, BasicFileAttributes atributos) throws IOException {
                    if (recolorir(repositorio, arquivo, destino)) {
                        total[0]++;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        gravarIconesEAbertura(logo, destino);
        Files.write(destino.resolve(".versao"), VERSAO.getBytes("UTF-8"));
        System.out.println("Tema gerado: " + total[0] + " imagens recoloridas, mais a abertura e os icones.");
    }

    private static boolean recolorir(Path repositorio, Path arquivo, Path destino) throws IOException {
        String nome = arquivo.getFileName().toString().toLowerCase();
        String formato = nome.endsWith(".png") ? "png" : nome.endsWith(".gif") ? "gif"
                : nome.endsWith(".jpg") || nome.endsWith(".jpeg") ? "jpg" : null;
        if (formato == null) {
            return false;
        }
        BufferedImage origem = ImageIO.read(arquivo.toFile());
        if (origem == null) {
            return false;
        }
        BufferedImage saida = recolorir(origem);
        if (formato.equals("jpg")) {
            saida = semAlfa(saida);
        }
        Path alvo = destino.resolve(repositorio.relativize(arquivo).toString());
        Files.createDirectories(alvo.getParent());
        ImageIO.write(saida, formato, alvo.toFile());
        return true;
    }

    static BufferedImage recolorir(BufferedImage origem) {
        int largura = origem.getWidth();
        int altura = origem.getHeight();
        int[] pixels = origem.getRGB(0, 0, largura, altura, null, 0, largura);
        for (int i = 0; i < pixels.length; i++) {
            if ((pixels[i] >>> 24) != 0) {
                pixels[i] = MapaDeCores.mapear(pixels[i]);
            }
        }
        BufferedImage saida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        saida.setRGB(0, 0, largura, altura, pixels, 0, largura);
        return saida;
    }

    private static BufferedImage semAlfa(BufferedImage imagem) {
        BufferedImage saida = new BufferedImage(imagem.getWidth(), imagem.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = saida.createGraphics();
        g.setColor(Tema.FUNDO);
        g.fillRect(0, 0, imagem.getWidth(), imagem.getHeight());
        g.drawImage(imagem, 0, 0, null);
        g.dispose();
        return saida;
    }

    private static void gravarIconesEAbertura(BufferedImage logo, Path destino) throws IOException {
        Path principal = destino.resolve("Legal3/images/Main");
        Files.createDirectories(principal);
        ImageIO.write(redimensionar(logo, 64), "png", principal.resolve("pnca1.png").toFile());
        ImageIO.write(redimensionar(logo, 32), "png", principal.resolve("pnca2.png").toFile());
        ImageIO.write(redimensionar(logo, 16), "png", principal.resolve("pnca3.png").toFile());

        double[] brilho = {0.0, 0.5, 1.0};
        for (String idioma : IDIOMAS) {
            Path pasta = destino.resolve("Legal3/images/General").resolve(idioma);
            Files.createDirectories(pasta);
            for (int i = 0; i < brilho.length; i++) {
                ImageIO.write(abertura(logo, brilho[i]), "png", pasta.resolve("Splash" + (i + 1) + ".png").toFile());
            }
        }
    }

    private static BufferedImage abertura(BufferedImage logo, double brilho) {
        int largura = 697;
        int altura = 492;
        BufferedImage cartao = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cartao.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RoundRectangle2D forma = new RoundRectangle2D.Double(28, 26, 640, 440, 24, 24);
        g.setPaint(new GradientPaint(0, 26, Tema.FUNDO, 0, 466, Tema.mistura(Tema.FUNDO, Tema.DESTAQUE, 0.22)));
        g.fill(forma);
        g.setPaint(new RadialGradientPaint(348, 230, 240, new float[] {0f, 1f},
                new Color[] {new Color(0x90, 0x49, 0xE3, (int) (110 * brilho)), new Color(0x90, 0x49, 0xE3, 0)}));
        g.fill(forma);
        g.setPaint(new GradientPaint(28, 26, Tema.mistura(Tema.BORDA, Tema.DESTAQUE, brilho), 668, 466,
                Tema.mistura(Tema.FUNDO, Tema.BORDA, 0.8)));
        g.setStroke(new BasicStroke(4f));
        g.draw(forma);
        int lado = 270;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.35 + 0.65 * brilho)));
        g.drawImage(redimensionar(logo, lado), (largura - lado) / 2, 56, null);
        g.dispose();
        return cartao;
    }

    private static BufferedImage redimensionar(BufferedImage origem, int lado) {
        BufferedImage saida = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(origem, 0, 0, lado, lado, null);
        g.dispose();
        return saida;
    }
}
