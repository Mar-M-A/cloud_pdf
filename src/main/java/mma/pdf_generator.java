package mma;

//package pdf_form_filler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class pdf_generator {

    public final static String PDF_PATH = "src/assets/DEMANDA JV - SIN POSTULACIÓN.pdf";

    public static String loadTemplate() throws IOException{
        File file = new File(PDF_PATH);
        PDDocument doc = Loader.loadPDF(file);

        PDFTextStripper stripper = new PDFTextStripper();
        return  stripper.getText(doc);
    }

    public static String removeSpecialCharacters(String test) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < test.length(); i++) {
            if (WinAnsiEncoding.INSTANCE.contains(test.charAt(i))) {
                b.append(test.charAt(i));
            }
        }
        return b.toString();
    }


    public static String fillTemplate(String template, Dictionary<String, String> placeholders) {
        for (Enumeration<String> keys = placeholders.keys(); keys.hasMoreElements(); ) {
            String key = keys.nextElement();
            template = template.replace("{" + key + "}", placeholders.get(key));
        }
        return template;
    }

    public static void createPdf(String content, String outputFilePath) throws IOException {
        PDDocument document = new PDDocument();

        PDPage page = new PDPage(PDRectangle.A4);

        document.addPage(page);

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Define a text content stream using the selected font, moving the cursor and
        // drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont(font, 12);
        String[] textList = content.split("\n");
        int y = 800;
        contentStream.newLineAtOffset(25, 800);
        for(int i =0; i < textList.length;i++ ){

            //contentStream.newLineAtOffset(100, y);
            contentStream.showText(removeSpecialCharacters(textList[i]));
            contentStream.newLineAtOffset(0, -15);

            
            y -= 15;

            if(y <= 0){
                
                PDPage page2 = new PDPage(PDRectangle.A4);
                document.addPage(page2);
                contentStream.endText();
                contentStream.close();
                contentStream = new PDPageContentStream(document, page2);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(25, 800);
                y = 800;
                //contentStream.newLineAtOffset(25, 800);
            }
        }
        
        contentStream.endText();

        // Make sure that the content stream is closed:
        contentStream.close();

        document.save(outputFilePath);// File path);
        document.close();
    }
}