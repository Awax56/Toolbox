/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 LE SAUCE Julien
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

package org.jls.toolbox.widget;

import javax.swing.JInternalFrame;

import org.jls.toolbox.widget.ui.DesktopPane;

/**
 * Fenêtre permettant d'afficher une chaîne de texte en XML.
 * 
 * @author LE SAUCE Julien
 * @date 15 mai 2015
 */
public class XMLMessageFrame extends InternalFrame {

    private static final long serialVersionUID = 3020342855466613427L;

    private final XMLViewer xmlViewer;

    /**
     * Permet d'instancier une fenêtre d'affichage.
     * 
     * @param id
     *            Identifiant unique de la fenêtre permettant sa gestion par le
     *            {@link DesktopPane}.
     * @param title
     *            Titre de la fenêtre.
     */
    public XMLMessageFrame(final String id, final String title) {
        super(id, title, true, true, true, false);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        this.xmlViewer = new XMLViewer("");
        setContentPane(this.xmlViewer);
    }

    /**
     * Permet de mettre à jour le message affiché par la fenêtre.
     * 
     * @param msg
     *            Message à afficher.
     */
    public void updateMessage (String msg) {
        this.xmlViewer.setText(msg);
    }
}
