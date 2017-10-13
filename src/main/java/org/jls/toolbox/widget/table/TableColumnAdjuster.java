/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 LE SAUCE Julien
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.jls.toolbox.widget.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Classe permettant d'adapter la largeur des colonnes d'une {@link Table} à
 * leur contenu. Plusieurs propriétés permettent de calculer la manière dont la
 * largeur des colonnes est calculée. Une autre propriété permet de spécifier
 * quelles colonnes doivent être ajustées dynamiquement ou non.
 * 
 * @author LE SAUCE Julien
 * @date 26 mai 2015
 */
public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

    private final Table table;
    private final Map<TableColumn, Integer> columnSizes;

    private boolean isColumnHeaderIncluded;
    private boolean isColumnDataIncluded;
    private boolean isOnlyAdjustLarger;
    private boolean isDynamicAdjustment;
    private int spacing;
    private int minimumWidth;

    /**
     * Permet d'instancier un adjuster.
     * 
     * @param table
     *            Table dont on souhaite ajuster la largeur des colonnes.
     */
    public TableColumnAdjuster(Table table) {
        this(table, 6);
    }

    /**
     * Permet d'instancier un adjuster à partir d'une table et de l'espacement entre
     * colonnes.
     * 
     * @param table
     *            Table dont on souhaite ajuster la largeur des colonnes.
     * @param spacing
     *            Espacement entre les colonnes.
     */
    public TableColumnAdjuster(Table table, int spacing) {
        this.table = table;
        this.columnSizes = new HashMap<>();
        setColumnHeaderIncluded(true);
        setColumnDataIncluded(true);
        setOnlyAdjustLarger(false);
        setDynamicAdjustment(false);
        this.spacing = spacing;
        this.minimumWidth = 0;
        installActions();
    }

    /**
     * Permet d'ajuster la largeur des colonnes de la table à leur contenu.
     */
    public void adjustColumns () {
        TableColumnModel columnModel = this.table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            adjustColumn(i);
        }
        this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
    }

    /**
     * Permet d'ajuster la largeur d'une colonne en particulier à son contenu.
     * 
     * @param column
     *            Indice de la colonne dans la table.
     */
    public void adjustColumn (final int column) {
        if (column < 0) {
            throw new ArrayIndexOutOfBoundsException(column);
        }
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnDataWidth(column);
        int width = Math.max(columnHeaderWidth, columnDataWidth);
        width = Math.max(this.minimumWidth, width);

        updateTableColumn(column, width);
        this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
    }

    /**
     * Permet de restaurer la largeur des colonnes de la table à leur largeur
     * précédente.
     */
    public void restoreColumns () {
        TableColumnModel tcm = this.table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    /**
     * Permet de déterminer la largeur de la colonne d'entête spécifiée de la table.
     * 
     * @param column
     *            Indice de la colonne dans la table.
     * @return Largeur de la colonne d'entête.
     */
    private int getColumnHeaderWidth (int column) {
        if (!this.isColumnHeaderIncluded) {
            return 0;
        }

        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = this.table.getTableHeader().getDefaultRenderer();
        }

        Component c = renderer.getTableCellRendererComponent(this.table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Permet de calculer la meilleure largeur pour la colonne en se basant sur la
     * cellule la plus large de la colonne.
     * 
     * @param column
     *            Indice de la colonne dans la table.
     * @return Largeur la mieux adaptée pour la colonne.
     */
    private int getColumnDataWidth (int column) {
        if (!this.isColumnDataIncluded) {
            return 0;
        }
        int preferredWidth = 0;
        int maxWidth = this.table.getColumnModel().getColumn(column).getMaxWidth();
        for (int row = 0; row < this.table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
            // Si on dépasse la largeur maximale, inutile de continuer
            if (preferredWidth >= maxWidth) {
                break;
            }
        }
        return preferredWidth;
    }

    /**
     * Permet de récupérer la largeur occupée par le contenu d'une cellule en
     * particulier.
     * 
     * @param row
     *            Indice de la ligne dans la table.
     * @param column
     *            Indice de la colonne dans la table.
     * @return Largeur adaptée pour la cellule spécifiée.
     */
    private int getCellDataWidth (int row, int column) {
        // On utilise le renderer de la cellule pour calculer la largeur
        // préférée
        TableCellRenderer cellRenderer = this.table.getCellRenderer(row, column);
        Component c = this.table.prepareRenderer(cellRenderer, row, column);
        int width = c.getPreferredSize().width + this.table.getIntercellSpacing().width;
        return width;
    }

    /**
     * Permet de mettre à jour la largeur d'une colonne.
     * 
     * @param column
     *            Indice de la colonne dans la table.
     * @param width
     *            Largeur de la colonne.
     */
    private void updateTableColumn (int column, int width) {
        final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        int w = width + this.spacing;
        // Permet de ne pas rogner la colonne
        if (this.isOnlyAdjustLarger) {
            w = Math.max(w, tableColumn.getPreferredWidth());
        }
        this.columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));
        this.table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(w);
    }

    /**
     * Permet de restaurer la largeur d'une colonne à sa largeur précédente.
     * 
     * @param column
     *            Indice de la colonne dans la tble.
     */
    protected void restoreColumn (int column) {
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        Integer width = this.columnSizes.get(tableColumn);

        if (width != null) {
            this.table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width.intValue());
        }
    }

    protected Table getTable () {
        return table;
    }

    protected boolean isOnlyAdjustLarger () {
        return isOnlyAdjustLarger;
    }

    protected boolean isDynamicAdjustment () {
        return isDynamicAdjustment;
    }

    /**
     * Indique s'il faut inclure la taille des entêtes dans le calcul des largeurs
     * des colonnes.
     * 
     * @param isColumnHeaderIncluded
     *            Argument.
     */
    public void setColumnHeaderIncluded (boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    /**
     * Indique s'il faut tenir compte des données contenues dans les cellules dans
     * le calcul des largeurs des colonnes.
     * 
     * @param isColumnDataIncluded
     *            Argument.
     */
    public void setColumnDataIncluded (boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    /**
     * Indique si lors de l'ajustement des colonnes on peut diminuer la largeur des
     * colonnes par rapport à leur état actuel.
     * 
     * @param isOnlyAdjustLarger
     *            Argument.
     */
    public void setOnlyAdjustLarger (boolean isOnlyAdjustLarger) {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    /**
     * Indique si lors d'un changement dans le modèle de données, la largeur des
     * colonnes doit être recalculée.
     * 
     * @param isDynamicAdjustment
     *            Argument.
     */
    public void setDynamicAdjustment (boolean isDynamicAdjustment) {
        if (this.isDynamicAdjustment != isDynamicAdjustment) {
            // On ajoute ou on retire le listener
            if (isDynamicAdjustment) {
                this.table.addPropertyChangeListener(this);
                this.table.getModel().addTableModelListener(this);
            } else {
                this.table.removePropertyChangeListener(this);
                this.table.getModel().removeTableModelListener(this);
            }
        }
        this.isDynamicAdjustment = isDynamicAdjustment;
    }

    @Override
    public void propertyChange (PropertyChangeEvent e) {
        // Lorsqu'un changement est opéré dans le modèle de données, on met
        // à jour les listeners et les largeurs des colonnes
        if ("model".equals(e.getPropertyName())) {
            TableModel model = (TableModel) e.getOldValue();
            model.removeTableModelListener(this);
            model = (TableModel) e.getNewValue();
            model.addTableModelListener(this);
            adjustColumns();
        }
    }

    @Override
    public void tableChanged (TableModelEvent e) {
        if (!this.isColumnDataIncluded) {
            return;
        }
        // Lorsqu'une cellule a été mise à jour
        if (e.getType() == TableModelEvent.UPDATE) {
            int column = this.table.convertColumnIndexToView(e.getColumn());

            if (column < 0) {
                return;
            }

            // Si on ne peut qu'agrandir les cellules
            if (this.isOnlyAdjustLarger) {
                int row = e.getFirstRow();
                TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

                if (tableColumn.getResizable()) {
                    int width = getCellDataWidth(row, column);
                    updateTableColumn(column, width);
                }
            }
            // Sinon on ajuste toutes les colonnes
            else {
                adjustColumn(column);
            }
        }
        // Sinon c'est que la modification affecte plus d'une colonne
        else {
            adjustColumns();
        }
    }

    public int getSpacing () {
        return this.spacing;
    }

    public void setSpacing (int spacing) {
        this.spacing = spacing;
    }

    public int getMinimumWidth () {
        return this.minimumWidth;
    }

    public void setMinimumWidth (int minimumWidth) {
        this.minimumWidth = minimumWidth;
    }

    /**
     * Installation des Actions pour permettre à l'utilisateur le contrôle de
     * certaines fonctionnalités.
     */
    private void installActions () {
        installColumnAction(true, true, "adjustColumn", "control ADD");
        installColumnAction(false, true, "adjustColumns", "control shift ADD");
        installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
        installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");

        installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
        installToggleAction(false, true, "toggleLarger", "control DIVIDE");
    }

    /**
     * Mise à jour de l'entrée et de la map des actions avec un nouveau
     * ColumnAction.
     *
     * @param isSelectedColumn
     *            .
     * @param isAdjust
     *            .
     * @param key
     *            .
     * @param keyStroke
     *            .
     */
    private void installColumnAction (boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
        Action action = new ColumnAction(isSelectedColumn, isAdjust);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        this.table.getInputMap().put(ks, key);
        this.table.getActionMap().put(key, action);
    }

    /*
     * Update the input and action maps with new ToggleAction
     */
    private void installToggleAction (boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
        Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    /**
     * Action to adjust or restore the width of a single column or all columns.
     * 
     * @author LE SAUCE Julien
     * @date 26 mai 2015
     */
    private class ColumnAction extends AbstractAction {

        private static final long serialVersionUID = -9140872372345566351L;

        private final boolean isSelectedColumn;
        private final boolean isAdjust;

        public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        @Override
        public void actionPerformed (ActionEvent e) {
            // Handle selected column(s) width change actions

            if (this.isSelectedColumn) {
                int[] columns = getTable().getSelectedColumns();

                for (int i = 0; i < columns.length; i++) {
                    if (this.isAdjust)
                        adjustColumn(columns[i]);
                    else
                        restoreColumn(columns[i]);
                }
            } else {
                if (this.isAdjust)
                    adjustColumns();
                else
                    restoreColumns();
            }
        }
    }

    /**
     * Toggle properties of the TableColumnAdjuster so the user can customize the
     * functionality to their preferences
     * 
     * @author LE SAUCE Julien
     * @date 26 mai 2015
     */
    private class ToggleAction extends AbstractAction {

        private static final long serialVersionUID = 1936147391564067381L;

        private final boolean isToggleDynamic;
        private final boolean isToggleLarger;

        public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        @Override
        public void actionPerformed (ActionEvent e) {
            if (isToggleDynamic) {
                setDynamicAdjustment(!isDynamicAdjustment());
                return;
            }

            if (isToggleLarger) {
                setOnlyAdjustLarger(!isOnlyAdjustLarger());
                return;
            }
        }
    }
}
