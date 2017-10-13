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

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

/**
 * Classe permettant d'étendre un {@link StyledDocument} utilisable dans un
 * {@link JTextArea}.
 * 
 * @author LE SAUCE Julien
 * @date Jul 9, 2015
 */
public class Document extends DefaultStyledDocument {

    private static final long serialVersionUID = 8868561777832804162L;

    private final Integer maxLength;

    /**
     * Permet d'instancier {@link UnknownError} {@link DefaultStyledDocument}.
     */
    public Document() {
        this(null);
    }

    /**
     * Permet d'instancier {@link UnknownError} {@link DefaultStyledDocument}.
     * 
     * @param maxLength
     *            Permet de spécifier la taille maximale du buffer en nombre de
     *            caractères ( <code>null</code> permet de spécifier une taille
     *            illimitée).
     */
    public Document(final Integer maxLength) {
        super();
        this.maxLength = maxLength;
    }

    @Override
    public void insertString (int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
        if (this.maxLength != null && getLength() > this.maxLength) {
            remove(0, str.length());
        }
    }
}
