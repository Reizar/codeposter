package com.aaronrama.codeposter;

/**
 * Created by aaron on 15/02/17.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class PosterMaker {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-code", description = "Path to input code file or directory.", required = true)
    private String codeFilePath;

    @Parameter(names = "-comment-characters", description = "Comment characters to look for to strip from code")
    private String commentCharacters = "";

    @Parameter(names = "-code-file-extension", description = "Only files with this extension will be loaded when providing an input directory")
    private String codeFileExtension = "";

    @Parameter(names = "-image", description = "Required - Path to input image.", required = true)
    private String imageFilePath;

    @Parameter(names = "-width", description = "Final width of the poster, Default value is 3150.")
    private int posterWidth = 3150;

    @Parameter(names = "-height", description = "Final width of the poster, Default value is 4050.")
    private int posterHeight = 4050;

    @Parameter(names = "-output-file", description = "Path / Filename for the output file. Default is ./poster.png")
    private String outputFilePath = "./poster.png";

    @Parameter(names = "-output-format", description = "File format of outputted poster. Options are: PNG, JPG, SVG. Default is PNG")
    private String outputFileFormat = "PNG";

    private Poster poster;

    public static void main(String[] args) throws IOException {
        PosterMaker maker = new PosterMaker();
        new JCommander(maker, args);

        maker.createPoster();
    }

    private void createPoster() throws IOException {
        this.poster = new Poster();
        poster.ratio = 0.6f;
        poster.width = posterWidth;
        poster.height = posterHeight;

        File file = new File(codeFilePath);
        if (file.exists()) {
          poster.codeCharacters = file.isFile() ? FileUtility.loadCodeFromFile(codeFilePath, commentCharacters) :
                                                  FileUtility.loadCodeFromDirectory(codeFilePath, commentCharacters, codeFileExtension);
        }

        poster.pixels = FileUtility.loadImagePixelsFromFile(imageFilePath);

        // Inefficiently pad the code characters list so that there is guaranteed to be enough
        // characters to cover each pixel.
        while ((poster.pixels.length * poster.pixels[0].length) > poster.codeCharacters.length) {

            String[] newChars = new String[poster.codeCharacters.length * 2];

            System.arraycopy(poster.codeCharacters, 0, newChars, 0, poster.codeCharacters.length);
            System.arraycopy(poster.codeCharacters, 0, newChars, poster.codeCharacters.length, poster.codeCharacters.length);

            poster.codeCharacters = newChars;
        }

        manuallyDrawImage();
    }

    private void manuallyDrawImage() {
        int imageWidth = poster.pixels[0].length;
        int imageHeight = poster.pixels.length;

        BufferedImage bi = new BufferedImage(posterWidth, posterHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D context = bi.createGraphics();

        float widthScale = (posterWidth / imageWidth);
        float heightScale = posterHeight / imageHeight;
        Font font = new Font("Source Code Pro", Font.BOLD, (int)heightScale);
        context.setFont(font);

        for (int y = 0; y < poster.pixels.length; y++) {
            Color[] row = poster.pixels[y];

            for (int x = 0; x < row.length; x++) {
                String character = poster.codeCharacters[y * imageWidth + x];
                Color color = row[x];

                float newX = x * widthScale;
                float newY = y * heightScale;

                context.setColor(color);
                context.drawString(character, newX, newY);
            }
        }

        try {
            ImageIO.write(bi, "PNG", new File("test2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
