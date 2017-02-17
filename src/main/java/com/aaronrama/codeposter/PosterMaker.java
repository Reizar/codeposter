package com.aaronrama.codeposter;

/**
 * Created by aaron on 15/02/17.
 */

import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PosterMaker {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-code", description = "Path to input code file or directory.", required = true)
    private String codeFilePath;

    @Parameter(names = "-comment-characters", description = "Comment characters to look for to strip from code")
    private String commentCharacters = "";

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
        poster.codeCharacters = FileUtility.loadCodeFromFile(codeFilePath);
        poster.pixelHexes = FileUtility.loadImagePixelsFromFile(imageFilePath);

        // Inefficiently pad the code characters list so that there is guaranteed to be enough
        // characters to cover each pixel.
        while ((poster.pixelHexes.length * poster.pixelHexes[0].length) > poster.codeCharacters.length) {

            String[] newChars = new String[poster.codeCharacters.length * 2];

            System.arraycopy(poster.codeCharacters, 0, newChars, 0, poster.codeCharacters.length);
            System.arraycopy(poster.codeCharacters, 0, newChars, poster.codeCharacters.length, poster.codeCharacters.length);

            poster.codeCharacters = newChars;
        }

        mergePixelsWithCode();
        createXMLElements();
    }

    private void mergePixelsWithCode() {
        int imageWidth = poster.pixelHexes[0].length;

        ArrayList<TextElement> mappedRows = new ArrayList<TextElement>();

        for (int y = 0; y < poster.pixelHexes.length; y++) {
            String[] row = poster.pixelHexes[y];

            ArrayList<TextElement> mappedRow = mapRow(y, imageWidth);
            for (TextElement row1 : mappedRow) {
                mappedRows.add(row1);
            }
        }

        poster.textElements = mappedRows;
    }

    private ArrayList<TextElement> mapRow(int y, int imageWidth) {
        String[] row = poster.pixelHexes[y];

        ArrayList<TextElement> mappedRow  = new ArrayList<TextElement>();

        for (int x = 0; x < row.length; x ++) {
            String character = poster.codeCharacters[y * imageWidth + x];
            String hex = row[x];

            if (!mappedRow.isEmpty() && mappedRow.get(mappedRow.size() - 1).hex.equals(hex)) {
                mappedRow.get(mappedRow.size() - 1).text += character;
            } else {
                TextElement mappedNode = new TextElement(character, x * poster.ratio, y, hex);
                mappedRow.add(mappedNode);
            }
        }
        return mappedRow;
    }

    private void createXMLElements() {
        int imageWidth = poster.pixelHexes[0].length;
        int imageHeight = poster.pixelHexes.length;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElementNS("http://www.w3.org/2000/svg", "svg");
            doc.appendChild(root);

            root.setAttribute("viewBox", "0 0 " + (imageWidth * poster.ratio) + " " + imageHeight);
            root.setAttribute("style", "font-family: 'Source Code Pro'; font-size: 1; font-weight: 900;");
            root.setAttribute("width", "" + poster.width);
            root.setAttribute("height", "" + poster.height);
            root.setAttribute("xml:space", "preserve");

            for (TextElement el : poster.textElements) {
                Element textEl = doc.createElement("text");
                textEl.appendChild(doc.createTextNode(el.text));
                textEl.setAttribute("x", "" + el.x);
                textEl.setAttribute("y", "" + el.y);
                textEl.setAttribute("fill", el.hex);
                root.appendChild(textEl);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            if (outputFileFormat.equals("SVG")) {
                transformer.transform(new DOMSource(doc), new StreamResult(new File(outputFilePath)));
            } else {
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
                String svgString = writer.getBuffer().toString();
                InputStream inputStream = new ByteArrayInputStream(svgString.getBytes("UTF-8"));

                ImageTranscoder t = outputFileFormat.equals("PNG") ? new PNGTranscoder() : new JPEGTranscoder();
                TranscoderInput input = new TranscoderInput(inputStream);
                OutputStream ostream = new FileOutputStream(outputFilePath);
                TranscoderOutput output = new TranscoderOutput(ostream);

                t.transcode(input, output);
                ostream.flush();
                ostream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
