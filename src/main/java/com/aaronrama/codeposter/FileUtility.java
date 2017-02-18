package com.aaronrama.codeposter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aaron on 15/02/17.
 */
public class FileUtility {

    public static String[] loadCodeFromFile(String path, String commentChars) throws IOException {
        String fileContents = readFile(path);
        String cleanedFile = cleanCodeFile(fileContents, commentChars);
        return cleanedFile.split("");
    }

    public static String[] loadCodeFromDirectory(String path, String commentChars, String fileExt) throws IOException {
        String condensedInput =  Files.walk(Paths.get(path))
                                      .filter(Files::isRegularFile)
                                      .filter(path1 -> path1.toString().endsWith(fileExt))
                                      .map(path1 -> readFile(path1.toString()))
                                      .map(fileContents -> cleanCodeFile(fileContents, commentChars))
                                      .reduce("", String::concat);

        return condensedInput.split("");
    }

    public static Color[][] loadImagePixelsFromFile(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        image = resizeImage(image);

        // Following is taken from: http://stackoverflow.com/a/9470843/1675156
        // Has been modified to suit my needs.
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        Color[][] result = new Color[height][width];
        final  int pixelLength = hasAlphaChannel ? 4 : 3;
        for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            if (hasAlphaChannel) {
                int alpha = ColourUtility.normalize((pixels[pixel]) & 0xff);
                int blue = ColourUtility.normalize((pixels[pixel + 1]) & 0xff);
                int green = ColourUtility.normalize((pixels[pixel + 2]) & 0xff);
                int red = ColourUtility.normalize((pixels[pixel + 3]) & 0xff);
                result[row][col] = new Color(red, green, blue, alpha);
            } else {
                int blue = ColourUtility.normalize((pixels[pixel]) & 0xff);
                int green = ColourUtility.normalize((pixels[pixel + 1]) & 0xff);
                int red = ColourUtility.normalize((pixels[pixel + 2]) & 0xff);
                result[row][col] = new Color(red, green, blue);
            }

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return result;
    }

    private static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Error reading inputted code file");
            System.exit(0);

            return "";
        }
    }

    private static String cleanCodeFile(String code, String commentChars) {
        if (commentChars.length() > 0) {
            code = code.replaceAll(commentChars + ".*?\n","\n");
        }
        return code.trim()
                   .replaceAll("\\s*\\n+\\s*", " ")
                   .replaceAll("\\s", " ");
    }

    private static BufferedImage resizeImage(BufferedImage inputImage) {
        int scaledWidth = (int) Math.round(inputImage.getWidth() * 1.667);
        int scaledHeight = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }
}
