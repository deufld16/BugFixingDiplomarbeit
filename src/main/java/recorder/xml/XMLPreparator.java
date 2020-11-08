package recorder.xml;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import recorder.beans.Article;
import recorder.beans.Command;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import recorder.guiOperations.GUIOperations;

/**
 *
 * @author KROBATHLukas
 */
public class XMLPreparator {

    /**
     * Method that converts all "Command" objects in XML elements
     *
     * @param document
     * @param command
     * @return
     */
    public static Element parse(Document document, Command command) {
        Element target = document.createElement(command.getCommandName());
        for (String attributeName : command.getAttributes().keySet()) {
            if (command.getAttributes().get(attributeName) instanceof Article) {
                Article art = (Article) command.getAttributes().get(attributeName);
                target.setAttribute("article", art.getXmlArticleName());
                target.setAttribute("def", art.getArticleName());
                for (Article tmp_art : GUIOperations.getAtm().getAllArtikels()) {
                    if (tmp_art.getArticleName().equalsIgnoreCase(art.getArticleName())) {
                        if (String.format("%.2f", tmp_art.getPreis()).equalsIgnoreCase("0,00")) {
                            target.setAttribute("price", (int)(art.getPreis() * 100) + "");
                        }
                    }
                }
                if(art.getCategory().equalsIgnoreCase("Preis EAN")){
                    target.setAttribute("price", (int)(art.getPreis() * 100) + "");
                }
            } else if (command.getAttributes().get(attributeName) instanceof Integer) {
                target.setAttribute(attributeName, String.format("%d", (int) command.getAttributes().get(attributeName)));
            } else if (command.getAttributes().get(attributeName) instanceof Double) {
                target.setAttribute(attributeName, String.format("%.2f", (double) command.getAttributes().get(attributeName)).replaceAll(",", "."));
            } else if (command.getAttributes().get(attributeName) instanceof Boolean) {
                target.setAttribute(attributeName, (boolean) command.getAttributes().get(attributeName) ? "true" : "false");
            } else {
                target.setAttribute(attributeName, (String) command.getAttributes().get(attributeName));
            }
        }
        if(command.isIsTextNode()){
            target.setTextContent(command.getTextNodeContent());
        }
        return target;
    }
}
