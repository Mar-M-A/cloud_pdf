package mma;

//package pdf_form_filler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.google.gson.stream.JsonWriter;

import org.apache.pdfbox.Loader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class pdf_form_filler {

    public static final String INIT_SELLER_TEXT = "VENDEDORA:";
    public static final String END_SELLER_TEXT = "De otra parte";

    public static final String INIT_BUYER_TEXT = "COMPRADORA:";
    public static final String END_BUYER_TEXT = "Ambas partes";

    public static Dictionary<String, String> sellerDict;
    public static Dictionary<String, String> buyerDict;
    public static String[] clauses;

    public static final String INIT_CLAUS_TEXT = "ESTIPULACIONES";
    public static final String END_CLAUS_TEXT = "Y para que así conste";

    public static boolean loadPdfData(String url) throws Exception {
        File file = new File(url);
        PDDocument doc = Loader.loadPDF(file);

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);

        int initSeller = text.indexOf(INIT_SELLER_TEXT) + INIT_SELLER_TEXT.length();
        int endSeller = text.indexOf(END_SELLER_TEXT);

        int initBuyer = text.indexOf(INIT_BUYER_TEXT) + INIT_BUYER_TEXT.length();
        int endBuyer = text.indexOf(END_BUYER_TEXT);

        String sellerInfo = text.substring(initSeller, endSeller);
        String buyerInfo = text.substring(initBuyer, endBuyer);

        sellerDict = getInfoMap(sellerInfo, false);
        buyerDict = getInfoMap(buyerInfo, true);

        clauses = getClausesArrayFromText(text);

        // System.out.println(INIT_SELLER_TEXT);
        // System.out.println(sellerDict.toString());
        // System.out.println(INIT_BUYER_TEXT);
        // System.out.println(buyerDict.toString());

        // testerino
        // String[] name = getNameAndSurnamesFromFullnam(sellerDict.get("name"));

        doc.close();

        return true;
    }

    public static void createJSONFile() {
        JsonWriter jsonWriter = null;
        try {
            jsonWriter = new JsonWriter(new FileWriter("src/assets/test.json"));
            jsonWriter.beginObject();
            jsonWriter.name("seller");
            jsonWriter.beginObject();
            jsonWriter.name("name");
            jsonWriter.value(sellerDict.get("seller_name"));
            jsonWriter.name("dni");
            jsonWriter.value(sellerDict.get("seller_DNI"));
            jsonWriter.name("city");
            jsonWriter.value(sellerDict.get("seller_city"));
            jsonWriter.name("street");
            jsonWriter.value(sellerDict.get("seller_street"));
            jsonWriter.name("num");
            jsonWriter.value(sellerDict.get("seller_num"));
            jsonWriter.name("cp");
            jsonWriter.value(sellerDict.get("seller_CP"));
            jsonWriter.endObject();
            jsonWriter.name("buyer");
            jsonWriter.beginObject();
            jsonWriter.name("name");
            jsonWriter.value(buyerDict.get("buyer_name"));
            jsonWriter.name("dni");
            jsonWriter.value(buyerDict.get("buyer_DNI"));
            jsonWriter.name("city");
            jsonWriter.value(buyerDict.get("buyer_city"));
            jsonWriter.name("street");
            jsonWriter.value(buyerDict.get("buyer_street"));
            jsonWriter.name("num");
            jsonWriter.value(buyerDict.get("buyer_num"));
            jsonWriter.name("cp");
            jsonWriter.value(buyerDict.get("buyer_CP"));
            jsonWriter.endObject();

            jsonWriter.name("clauses");
            jsonWriter.beginArray();
            for (int i = 0; i < clauses.length; i++) {
                jsonWriter.beginObject();
                jsonWriter.name("clauses_id");
                jsonWriter.value(i);
                jsonWriter.name("text");
                jsonWriter.value(clauses[i]);
                jsonWriter.endObject();

            }

            jsonWriter.endArray();
            jsonWriter.endObject();
        } catch (IOException e) {

        } finally {
            try {
                jsonWriter.close();
            } catch (IOException e) {

            }
        }
    }

    private static Dictionary<String, String> getInfoMap(String info, boolean isBuyer) {
        Dictionary<String, String> dict = new Hashtable<>();

        String[] pepet = info.split(",");
        /// condicional ternari equivaldira a ->
        /// if(isBuyer){prefix = "buyer"} else { prefix = "seller"}
        String prefix = isBuyer ? "buyer" : "seller";

        dict.put(interpolateStrings(prefix, "name"), getFullName(pepet[0]));
        dict.put(interpolateStrings(prefix, "DNI"), getDni(pepet[1]));
        dict.put(interpolateStrings(prefix, "city"), getCity(pepet[2]));
        dict.put(interpolateStrings(prefix, "street"), getStreet(pepet[3]));
        dict.put(interpolateStrings(prefix, "num"), getNum(pepet[4]));
        dict.put(interpolateStrings(prefix, "CP"), getCP(pepet[5]));

        // recuperar valors del dict --> dict.get("name");

        return dict;
    }

    private static String[] getClausesArrayFromText(String text) {

        int initClause = text.indexOf(INIT_CLAUS_TEXT) + INIT_CLAUS_TEXT.length();
        int endClause = text.indexOf(END_CLAUS_TEXT);

        String clauseInfo = text.substring(initClause, endClause);

        String[] clauses = clauseInfo.split(".-");
        String[] result = new String[clauses.length - 1];

        // comencem per el 1 perque el primer item dek array es PRIMERA
        for (int i = 1; i < clauses.length; i++) {
            int index = clauses[i].lastIndexOf(".") + 1;
            result[i - 1] = clauses[i].substring(0, index).trim();
        }

        return result;
    }

    private static String interpolateStrings(String prefix, String variable) {
        return String.format("%s_%s", prefix, variable);
    }

    private static String getFullName(String sentence) {
        return sentence.replace("D. ", "").replace("Dª. ", "").replace("\n", "");
    }

    private static String[] getNameAndSurnamesFromFullnam(String fullName) {
        return fullName.split(" ");
    }

    private static String getDni(String sentece) {
        return sentece.replace("con N.I.F. nº ", "").replace("\n", "");
    }

    private static String getCity(String sentece) {
        return sentece.replace("y domicilio en ", "").replace("\n", "");
    }

    private static String getStreet(String sentence) {
        return sentence.replace("calle ", "").replace("\n", "");
    }

    private static String getNum(String sentence) {
        return sentence.replace("nº ", "").replace("\n", "");
    }

    private static String getCP(String sentence) {
        return sentence.replace("C.P. ", "").replace("\n", "").trim();
    }

}