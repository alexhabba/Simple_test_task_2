import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Generator {

    private static List<String> listXml         = new ArrayList<>();
    private static List<String> listSourceData  = new ArrayList<>();
    private static Document document            = new Document();

//    public static void main(String[] args){
//
//        parseXmlFile("copy.xml");
//        initListSourceData("source-data.tsv");
//        initDocumentColumns(document);
//        initDocumentPageWidthAndHeight(document);
//        writeFile("test.tsv", getDocString());
//
//    }

    public static void main(String[] args){

        parseXmlFile(args[0]);
        initListSourceData(args[1]);
        initDocumentColumns(document);
        initDocumentPageWidthAndHeight(document);
        writeFile(args[2], getDocString());

    }

    public static String getDocString(){
        StringBuilder string = new StringBuilder();
        StringBuilder string1 = new StringBuilder();
        heading(string);
        listSourceData.forEach(x -> {
            String[] str = x.split("\\t");
            String temp = getLine(str[0], str[1], str[2]);
            string.append(temp);
            if (string.toString().split("\n").length > document.getHeightPage()){
                string.delete(string.length() - 2 - temp.length() - document.getWidthPage(), string.length() - 1);
                string.append("~\n");
                string1.append(string);
                string.delete(0, string.length());
                heading(string);
                string.append(temp);
            }
            string.append(delimiter());
        });

        if (string.length() > 3)
            string1.append(string).delete(string1.length() - 1 - document.getWidthPage(), string1.length() - 1);

        string1.deleteCharAt(string1.length() - 2);
        while (string1.charAt(string1.length() - 1) == '\n')
            string1.deleteCharAt(string1.length() - 1);

        return string1.toString();
    }

    public static void writeFile(String nameFile, String str){
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(nameFile), "UTF-16")){
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void heading(StringBuilder string){
        string.append(getLine(document.getColumn1(), document.getColumn2(), document.getColumn3()));
        string.append(delimiter());
    }

    public static String getLine(String value1, String value2, String value3){

        StringBuilder string = new StringBuilder();

        while (value1 != null || value2 != null || value3 != null) {
            if (value1 != null)
                value1 = test(value1, document.getColumn1Size(), string);
            else
                value1 = test("", document.getColumn1Size(), string);
            if (value2 != null)
                value2 = test(value2, document.getColumn2Size(), string);
            else
                value2 = test("", document.getColumn2Size(), string);
            if (value3 != null)
                value3 = test(value3, document.getColumn3Size(), string);
            else
                value3 = test("", document.getColumn3Size(), string);
            string.append("|\n");

        }
        return string.toString();
    }

    public static String test(String value, int size, StringBuilder string) {

        value = value.trim();
        if (value.length() <= size) {
            getOneLine2(size - value.length(), string, value);
            return null;
        }
        else {
//            String str = value.substring(0, size);
            String str = value.substring(0, checkWordBorder(value, size));
            getOneLine2(size - str.length(), string, str);
            return value.replace(str, "");
        }

    }

    public static int checkWordBorder(String str, int size){
        String[] strArr = str.split("[^\\wа-яА-Я]");
        String string = "";
        for (int i = 0; i < strArr.length; i++) {
            string = (string + strArr[i]).length() <= size || (string + strArr[i] + " ").length() <= size ?
                    string + strArr[i] : string.equals("") ? strArr[i] + "" : string;
            if (string.length() != 0)
            if (string.length() < size)
                string += " ";
        }

        int count = string.length();
        if (string.substring(0, 1).matches("[\\w]") && count == size &&
                string.charAt(string.length() - 1) == ' ' && str.charAt(size - 1) != '/')
            count--;
        if (string.length() > size)
            return size;
        return count;
    }

    public static String delimiter(){
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < document.getWidthPage(); i++) {
            string.append("-");
        }
        string.append("\n");
        return string.toString();
    }

    public static void getOneLine2(int size, StringBuilder string, String str){

        string.append("| ").append(str);
        for (int i = 0; i <= size; i++) {
            string.append(" ");
        }
    }

    public static void initListSourceData(String nameFile){
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(nameFile));
            listSourceData = Arrays.asList(new String(bytes, StandardCharsets.UTF_16).split("\\n"));
            listSourceData = listSourceData.stream().map(x-> x = x.replaceAll("\\r", "")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseXmlFile(String nameFile){
        try{
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(nameFile));

            while (parser.hasNext()) {
                int event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT)
                    listXml.add(parser.getLocalName());
                if (event == XMLStreamConstants.CHARACTERS && !parser.getText().replaceAll("\\s+", "").equals(""))
                    listXml.add(parser.getText());
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void initDocumentPageWidthAndHeight(Document document){
        for (int i = 0; i < listXml.size(); i++) {
            if (listXml.get(i).equals("page")) {
                i++;
                while (listXml.size() > i && (listXml.get(i).equals("width") || listXml.get(i).equals("height"))) {
                    if (listXml.get(i).equals("width")) {
                        i++;
                        document.setWidthPage(Integer.parseInt(listXml.get(i++)));
                    } else if (listXml.get(i).equals("height")) {
                        i++;
                        document.setHeightPage(Integer.parseInt(listXml.get(i++)));
                    }
                }
            }
        }
    }

    public static void initDocumentColumns(Document document){
        List<String> list = getColumns();
        document.setColumn1(list.get(0).split("\\s")[0]);
        document.setColumn2(list.get(1).split("\\s")[0]);
        document.setColumn3(list.get(2).split("\\s")[0]);
        document.setColumn1Size(Integer.parseInt(list.get(0).split("\\s")[1]));
        document.setColumn2Size(Integer.parseInt(list.get(1).split("\\s")[1]));
        document.setColumn3Size(Integer.parseInt(list.get(2).split("\\s")[1]));
    }

    public static List<String> getColumns(){

        List<String> temp = new ArrayList<>();
        for (int i = 0; i < listXml.size(); i++) {

            while (listXml.size() > i && listXml.get(i).equals("column")) {
                String title = "";
                String width = "";
                i++;
                while (listXml.size() > i && (listXml.get(i).equals("title") || listXml.get(i).equals("width"))) {
                    if (listXml.get(i).equals("title")) {
                        i++;
                        title = listXml.get(i++);
                    } else if (listXml.get(i).equals("width")) {
                        i++;
                        width = listXml.get(i++);
                    }
                }
                temp.add(title + " " + width);
            }
        }
        return temp;
    }


}
