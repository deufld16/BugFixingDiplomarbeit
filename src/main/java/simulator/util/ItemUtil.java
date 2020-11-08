package simulator.util;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import recorder.beans.Article;

public class ItemUtil {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ItemUtil.class);

    private static ItemUtil theInstance;
    private static List<Article> list;

    private ItemUtil() {
        list = loadArtikel();
    }

    public static ItemUtil getTheInstance() {
        if (theInstance == null) {
            theInstance = new ItemUtil();
        }
        return theInstance;
    }

    public synchronized static List<Article> loadArtikel() {
        try {

            List<Article> listOfArticles = new LinkedList<>();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(
                    Paths.get(System.getProperty("user.dir"), "src", "main", "java", "recorder", "res", "itemmapper.xml")
                            .toFile());
            NodeList listNode = doc.getElementsByTagName("item");

            for (int i = 0; i < listNode.getLength(); i++) {
                if (listNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) listNode.item(i);
                    listOfArticles.add(new Article(el.getAttribute("article"),
                            el.getAttribute("d_c_minus").equals("true"), el.getAttribute("d_size").equals("true"),
                            el.getAttribute("ean"), el.getAttribute("name"), el.getAttribute("pfand").equals("true"),
                            el.getAttribute("pfandart"), Double.parseDouble(el.getAttribute("preis")),
                            el.getAttribute("rabatt").equals("true"), Integer.parseInt(el.getAttribute("ust")),
                            el.getAttribute("weight").equals("true"), el.getAttribute("category"),
                            el.getAttribute("mwd").equals("true"), el.getAttribute("serialnr").equals("true")));
                }
            }
            System.out.println("");
            return listOfArticles;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Fehler beim laden der Artikel Daten!");
        }
        return null;
    }

    public synchronized String getEANforArticleName(String strArtikelName)
            throws Exception {

        String strEAN = "";
        for (Article article : list) {
            if (article.getXmlArticleName().equalsIgnoreCase(strArtikelName)) {
                System.out.println(article.getEan() + "Oje");
                return article.getEan();
            }
        }

        if (strEAN.isEmpty()) {
            System.out.println("HILFE");
            throw new Exception("EAN not found for Article: " + strArtikelName);
        }

        return strEAN;
    }

}
