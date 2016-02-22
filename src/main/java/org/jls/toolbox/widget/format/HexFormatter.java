/*#
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
 #*/

package org.jls.toolbox.widget.format;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.text.DefaultFormatter;

/**
 * Permet de créer un formatter hexadécimal, c'est-à-dire un formatter
 * interdisant l'utilisation des caractères autres que [0-9a-fA-F]. Il permet
 * également de fixer une limite minimale et maximale au nombre tapé. Ce
 * formatter peut être utilisé dans les {@link JFormattedTextField} ou dans les
 * {@link JSpinner} par exemple.
 * 
 * @author LE SAUCE Julien
 * @date Feb 24, 2015
 */
public class HexFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 5757171622125713231L;

	private final Long minimum;
	private final Long maximum;

	/**
	 * Permet d'instancier un formatter et de spécifier les valeurs limites du
	 * modèle.
	 * 
	 * @param min
	 *            Valeur minimale pouvant être affichée.
	 * @param max
	 *            Valeur maximale pouvant être affichée.
	 */
	public HexFormatter (Long min, Long max) {
		this(min, max, true, true, true);
	}

	/**
	 * Permet d'instancier un formatter et de lui spécifier ses différents
	 * paramètres.
	 * 
	 * @param min
	 *            Valeur minimale pouvant être affichée.
	 * @param max
	 *            Valeur maximale pouvant être affichée.
	 * @param allowInvalid
	 *            Voir {@link DefaultFormatter#setAllowsInvalid(boolean)}.
	 * @param commitsOnValidEdit
	 *            Voir {@link DefaultFormatter#setCommitsOnValidEdit(boolean)}.
	 * @param overwriteMode
	 *            Voir {@link DefaultFormatter#setOverwriteMode(boolean)}.
	 */
	public HexFormatter (Long min, Long max, boolean allowInvalid, boolean commitsOnValidEdit, boolean overwriteMode) {
		super();
		this.maximum = max;
		this.minimum = min;
		setAllowsInvalid(allowInvalid);
		setCommitsOnValidEdit(commitsOnValidEdit);
		setOverwriteMode(overwriteMode);
	}

	@Override
	public Object stringToValue (String text) throws ParseException {
		Long val = null;
		// Si la chaîne est vide
		if (text.isEmpty()) {
			// On renvoie la valeur minimale si elle existe, sinon 0
			if (this.minimum != null) {
				val = this.minimum;
			} else {
				val = 0L;
			}
		} else {
			// On essaye de parser la chaîne de texte en hexadécimal
			try {
				val = Long.parseLong(text, 16);
			} catch (@SuppressWarnings("unused") NumberFormatException e) {
				throw new ParseException("Incorrect hexadecimal string", 0);
			}
			// Si la valeur est supérieure au maximum
			if (this.maximum != null) {
				// On sature la valeur et on lance une exception
				if (val > this.maximum) {
					val = this.maximum;
					throw new ParseException("Value must be smaller than maximum", 0);
				}
			}
			// Si la valeur est inférieure au minimum
			if (this.minimum != null) {
				// On sature la valeur et on lance une exception
				if (val < this.minimum) {
					val = this.minimum;
					throw new ParseException("Value must be higher than minimum", 0);
				}
			}
		}
		return Long.toHexString(val);
	}

	@Override
	public String valueToString (Object value) throws ParseException {
		// Si la valeur reçue est une chaîne
		if (value instanceof String) {
			String text = (String) value;
			Long val = Long.parseLong(text, 16);
			// S'il y a un maximum et que la valeur est supérieure à celui-ci
			if (this.maximum != null) {
				if (val > this.maximum) {
					// On sature la valeur
					val = this.maximum;
				}
			}
			// S'il y a un minimum et que la valeur est inférieure à celui-ci
			if (this.minimum != null) {
				if (val < this.minimum) {
					// On sature la valeur
					val = this.minimum;
				}
			}
			return Long.toHexString(val).toUpperCase();
		}
		// Sinon si la valeur reçue n'est pas une chaîne
		else {
			// Si c'est un Long
			if (value instanceof Long) {
				long val = ((Long) value).longValue();
				// S'il y a un maximum et que la valeur est supérieure à
				// celui-ci
				if (this.maximum != null) {
					if (val > this.maximum) {
						// On sature la valeur
						val = this.maximum;
					}
				}
				// S'il y a un minimum et que la valeur est inférieure à
				// celui-ci
				if (this.minimum != null) {
					if (val < this.minimum) {
						// On sature la valeur
						val = this.minimum;
					}
				}
				return Long.toHexString(val).toUpperCase();
			}
		}
		throw new ParseException("Invalid input.", 0);
	}
}