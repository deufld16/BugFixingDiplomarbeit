/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.bl;

import recorder.beans.Article;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Florian
 */
public class ArtikelTableModel extends AbstractTableModel {

    private List<Article> allArtikels = new LinkedList<>();
    private List<Article> filteredArtikels = new LinkedList<>();
    private List<String> allColNames = Arrays.asList("Artikeltext", "EAN", "Preis", "USt.");
    private String filter = "";
    private String artikelGruppe = "Alle Artikel";

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return filteredArtikels.get(rowIndex).getArticleName();
            case 1:
                return filteredArtikels.get(rowIndex).getEan();
            case 2:
                return filteredArtikels.get(rowIndex).getPreis();
            case 3:
                return filteredArtikels.get(rowIndex).getUst();
        }
        return "Fehler";
    }

    /**
     * Method to get the articles with their EANs
     *
     * @param selectedEan
     * @return Liste von Artikeln
     */
    public List<Article> getArticlesForEan(List<String> selectedEan) {
        List<Article> artForEAN = new LinkedList<>();
        for (String ean : selectedEan) {
            for (Article art : allArtikels) {
                if (art.getEan().equalsIgnoreCase(ean)) {
                    artForEAN.add(art);
                }
            }
        }
        return artForEAN;
    }

    /**
     * Method to fill the ComboBox with the article groups
     *
     * @return Liste von Artikelgruppen
     */
    public List<String> fillComboBox() {
        List<String> allArtikelGroups = new LinkedList<>();
        for (Article allArtikel : allArtikels) {
            if (!allArtikelGroups.contains(allArtikel.getCategory())) {
                allArtikelGroups.add(allArtikel.getCategory());
            }
        }
        Collections.sort(allArtikelGroups);
        allArtikelGroups.add(0, "Alle Artikel");

        return allArtikelGroups;
    }

    /**
     * Method to load the articles from itemmapper.xml
     *
     * @throws IOException
     */
    public void loadData() throws IOException {
        try {
            allArtikels = recorder.io.IOLoader.loadArtikel();
            filter();
        } catch (Exception e) {
            
        }
    }

    /**
     * Method to filter the articles according to their article names
     */
    public void filter() {
        filteredArtikels.clear();
        if (filter.equalsIgnoreCase("all")) {
            filteredArtikels = new LinkedList<>(allArtikels);
        } else {
            for (Article allArtikel : allArtikels) {
                if (allArtikel.getArticleName().contains(filter)) {
                    filteredArtikels.add(allArtikel);
                }
            }
        }
        if (!artikelGruppe.equalsIgnoreCase("Alle Artikel")) {
            filteredArtikels = filteredArtikels
                    .stream()
                    .filter(s -> s.getCategory().equalsIgnoreCase(artikelGruppe))
                    .collect(Collectors.toList());
        }

        filteredArtikels.sort(Comparator.comparing(Article::getArticleName));
        this.fireTableDataChanged();
    }

    public List<Article> getAllArtikels() {
        return allArtikels;
    }

    public List<Article> getFilteredArtikels() {
        return filteredArtikels;
    }

    public void setArtikelGruppe(String artikelGruppe) {
        this.artikelGruppe = artikelGruppe;
    }

        @Override
    public int getRowCount() {
        return filteredArtikels.size();
    }

    @Override
    public int getColumnCount() {
        return allColNames.size();
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
