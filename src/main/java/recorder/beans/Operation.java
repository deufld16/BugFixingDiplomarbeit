/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

import recorder.enums.XMLOperations;

/**
 *
 * @author Florian 
 * 
 * Data class to depict "Operation" objects that are displayed in the
 * display list
 */
public class Operation {

    private XMLOperations xmlText;
    private Article article;
    private String additionalText;
    private int additionalValue;
    private Command command = null;
    private boolean isError;
    private int geschenkkarte_preis = -1;
    private boolean needsAutorisation = false;

    public Operation(XMLOperations xmlText) {
        this.xmlText = xmlText;
    }

    public Operation(XMLOperations xmlText, Article art) {
        this.xmlText = xmlText;
        this.article = art;
    }

    public Operation(XMLOperations xmlText, String additionalText) {
        this.xmlText = xmlText;
        this.additionalText = additionalText;
    }

    public Operation(XMLOperations xmlText, String additionalText, int additionalValue) {
        this.xmlText = xmlText;
        this.additionalText = additionalText;
        this.additionalValue = additionalValue;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Article getArticle() {
        return article;
    }

    public boolean isNeedsAutorisation() {
        return needsAutorisation;
    }

    public void setNeedsAutorisation(boolean needsAutorisation) {
        this.needsAutorisation = needsAutorisation;
    }

    public int getGeschenkkarte_preis() {
        return geschenkkarte_preis;
    }

    public void setGeschenkkarte_preis(int geschenkkarte_preis) {
        this.geschenkkarte_preis = geschenkkarte_preis;
    }
    

    public XMLOperations getXmlText() {
        return xmlText;
    }

    public void setXmlText(XMLOperations xmlText) {
        this.xmlText = xmlText;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public int getAdditionalValue() {
        return additionalValue;
    }

    public void setAdditionalValue(int additionalValue) {
        this.additionalValue = additionalValue;
    }

    @Override
    public String toString() {
        if (article != null) {
            return xmlText.getDisplayText() + " " + article.getArticleName();//article.getArticleName();
        } else if (additionalText != null && !additionalText.isEmpty()) {
            return xmlText.getDisplayText() + " " + additionalText;
        } else if (command != null && command.getDisplayText() != null) {
            return command.getDisplayText();
        } else {
            return xmlText.getDisplayText();
        }
    }

}
