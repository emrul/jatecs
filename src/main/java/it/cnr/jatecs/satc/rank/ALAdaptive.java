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

package it.cnr.jatecs.satc.rank;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntDoubleHashMap;
import it.cnr.jatecs.activelearning.Adaptive;
import it.cnr.jatecs.classification.ClassificationScoreDB;
import it.cnr.jatecs.classification.interfaces.IClassifierRuntimeCustomizer;
import it.cnr.jatecs.classification.interfaces.ILearner;
import it.cnr.jatecs.indexes.DB.interfaces.IClassificationDB;
import it.cnr.jatecs.indexes.DB.interfaces.IIndex;
import it.cnr.jatecs.satc.interfaces.IStaticRank;

public class ALAdaptive extends Adaptive implements IStaticRank {

    public ALAdaptive(ClassificationScoreDB confidenceUnlabelled, IIndex trainingSet,
                      IClassificationDB classificationUnlabelled, IIndex testSet,
                      double[] weightsList, ILearner learner,
                      IClassifierRuntimeCustomizer classifierCustomizer) {

        super(confidenceUnlabelled, trainingSet, classificationUnlabelled,
                testSet, trainingSet, testSet.getDocumentDB()
                        .getDocumentsCount(), weightsList, learner,
                classifierCustomizer);
    }

    @Override
    public TIntDoubleHashMap getMacroTable() {
        return rankingMap;
    }

    @Override
    public TIntDoubleHashMap getMicroTable() {
        return getMacroTable();
    }

    @Override
    public TIntArrayList getMacroRank() {
        return getFirstMacro(-1);
    }

    @Override
    public TIntArrayList getMicroRank() {
        return getFirstMicro(-1);
    }

}
