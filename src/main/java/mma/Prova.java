package mma;

//package pdf_form_filler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import java.io.File;
import java.util.*;

public class Prova {

    public static final String INIT_SELLER_TEXT = "VENDEDORA:";
    public static final String END_SELLER_TEXT = "De otra parte";

    public static final String INIT_BUYER_TEXT = "COMPRADORA:";
    public static final String END_BUYER_TEXT = "Ambas partes";

    public static final String INIT_CLAUS_TEXT = "ESTIPULACIONES";
    public static final String END_CLAUS_TEXT = "Y para que así conste";

    public static Dictionary<String, String> sellerDict;
    public static Dictionary<String, String> buyerDict;

    public static void main(String[] args) throws Exception {

        loadPdfData("src/assets/COMPRA-VENDA_PLENA.pdf");

    }

    public static boolean loadPdfData(String url) throws Exception {
        File file = new File(url);
        PDDocument doc = Loader.loadPDF(file);

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        
        String[] clausesArray = getClausesArrayFromText(text);

        for (int i = 0; i < clausesArray.length; i++) {
            System.out.println(clausesArray[i]);
            System.out.println("*********");
        }

        doc.close();

        return true;
    }

    private static String[] getClausesArrayFromText(String text) {

        int initClause = text.indexOf(INIT_CLAUS_TEXT) + INIT_CLAUS_TEXT.length();
        int endClause = text.indexOf(END_CLAUS_TEXT);

        String clauseInfo = text.substring(initClause, endClause);

        String[] clauses = clauseInfo.split(".-");
        String[] result =  new String[clauses.length-1];


        //comencem per el 1 perque el primer item dek array es PRIMERA
        for (int i = 1; i < clauses.length; i++) {
            int index = clauses[i].lastIndexOf(".") + 1;
            result[i-1] = clauses[i].substring(0, index).trim();
        }

        return result;
    }
}
