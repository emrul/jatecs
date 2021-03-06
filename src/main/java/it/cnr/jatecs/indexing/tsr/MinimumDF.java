/*
 * This file is part of JaTeCS.
 *
 * JaTeCS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JaTeCS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JaTeCS.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The software has been mainly developed by (in alphabetical order):
 * - Andrea Esuli (andrea.esuli@isti.cnr.it)
 * - Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 * - Alejandro Moreo Fernández (alejandro.moreo@isti.cnr.it)
 * Other past contributors were:
 * - Giacomo Berardi (giacomo.berardi@isti.cnr.it)
 */

package it.cnr.jatecs.indexing.tsr;

import it.cnr.jatecs.indexes.DB.interfaces.IIndex;

public class MinimumDF implements ITsrFunction {

	private int _minDF;
	
	/**
	 * TSR function which removes features which have a DF under a minimum value
	 * @param minDF minimum DF value
	 */
	public MinimumDF(int minDF) {
		super();
		_minDF = minDF;
	}

	/**
	 * @return 1.0 if the feature DF is >= than minDF, 0.0 otherwise
	 */
	public double compute(short catID, int featID, IIndex index) {
		return (index.getFeatureDocumentsCount(featID, catID)<_minDF? 0 : 1);
	}

	@Override
	public double compute(int TP, int FP, int FN, int TN) {
		return ((TP+FP)<_minDF?0:1);
	}

}
