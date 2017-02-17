package com.aaronrama.codeposter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aaron on 15/02/17.
 */
public class FileUtility {

    public static String[] loadCodeFromFile(String path) throws IOException {
        String fileContents = readFile(path);
        String cleanedFile = fileContents.trim().replaceAll("\\s*\\n+\\s*", " ").replaceAll("\\s", " ");
        return cleanedFile.split("");
    }

    public static String[][] loadImagePixelsFromFile(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        image = resizeImage(image);

        // Following is taken from: http://stackoverflow.com/a/9470843/1675156
        // Has been modified to suit my needs.
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        String[][] result = new String[height][width];
        final  int pixelLength = hasAlphaChannel ? 4 : 3;
        for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            if (hasAlphaChannel) {
                int alpha = (pixels[pixel]) & 0xff;
                int blue = (pixels[pixel + 1]) & 0xff;
                int green = (pixels[pixel + 2]) & 0xff;
                int red = (pixels[pixel + 3]) & 0xff;

                result[row][col] = ColourUtility.rgbaToHex(red, green, blue, alpha);
            } else {
                int blue = (pixels[pixel]) & 0xff;
                int green = (pixels[pixel + 1]) & 0xff;
                int red = (pixels[pixel + 2]) & 0xff;

                result[row][col] = ColourUtility.rgbToHex(red, green, blue);
            }

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return result;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private static BufferedImage resizeImage(BufferedImage inputImage) {
        int scaledWidth = (int) Math.round(inputImage.getWidth() * 1.667);
        int scaledHeight = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }
}
