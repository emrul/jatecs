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

package apps.classification;

import it.cnr.jatecs.classification.adaboost.*;
import it.cnr.jatecs.classification.mpboost.MPWeakLearnerMultiThread;
import it.cnr.jatecs.classification.validator.SimpleKFoldEvaluator;
import it.cnr.jatecs.evaluation.ClassificationComparer;
import it.cnr.jatecs.evaluation.ContingencyTableDataManager;
import it.cnr.jatecs.evaluation.ContingencyTableSet;
import it.cnr.jatecs.evaluation.util.EvaluationReport;
import it.cnr.jatecs.indexes.DB.interfaces.IIndex;
import it.cnr.jatecs.indexes.DB.troveCompact.TroveClassificationDBType;
import it.cnr.jatecs.indexes.DB.troveCompact.TroveContentDBType;
import it.cnr.jatecs.indexes.DB.troveCompact.TroveReadWriteHelper;
import it.cnr.jatecs.io.FileSystemStorageManager;
import it.cnr.jatecs.utils.IOperationStatusListener;

import java.io.File;
import java.io.FileWriter;

public class KFoldMPBoostMultiThread {

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err
                    .println("Usage: KFoldMPBoostMultiThread <k-value> <threadCount> <iterationCount> <indexDirectory>");
            return;
        }

        int kFold = Integer.parseInt(args[0]);
        int threadCount = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);

        String dataPath = args[3];

        File file = new File(dataPath);

        String indexName = file.getName();
        dataPath = file.getParent();

        FileSystemStorageManager storageManager = new FileSystemStorageManager(
                dataPath, false);
        storageManager.open();
        IIndex training = TroveReadWriteHelper.readIndex(storageManager,
                indexName, TroveContentDBType.Full,
                TroveClassificationDBType.Full);
        storageManager.close();

        // LEARNING
        AdaBoostLearner learner = new AdaBoostLearner();
        AdaBoostLearnerCustomizer customizer = new AdaBoostLearnerCustomizer();
        customizer.setNumIterations(iterations);
        customizer.setWeakLearner(new MPWeakLearnerMultiThread(threadCount));
        customizer.setPerCategoryNormalization(true);
        customizer.setLossFunction(new ExponentialLoss());
        customizer.keepDistributionMatrix(false);
        customizer
                .setInitialDistributionType(InitialDistributionMatrixType.UNIFORM);
        learner.setRuntimeCustomizer(customizer);

        // KFOLD
        AdaBoostClassifierCustomizer classifierCustomizer = new AdaBoostClassifierCustomizer();
        classifierCustomizer.groupHypothesis(true);

        SimpleKFoldEvaluator kFoldEvaluator = new SimpleKFoldEvaluator(learner,
                customizer, classifierCustomizer, true);
        kFoldEvaluator.setKFoldValue(kFold);

        kFoldEvaluator.setPercentageToUse(100);

        IOperationStatusListener status = null;

        ContingencyTableSet tableSet = kFoldEvaluator
                .evaluate(training, status);

        kFoldEvaluator.setEvaluateAllNodes(true);

        // classification dump and second evaluation (includes all nodes)
        storageManager = new FileSystemStorageManager(dataPath, false);
        storageManager.open();
        TroveReadWriteHelper.writeClassification(storageManager,
                kFoldEvaluator.getClassification(), "/kFoldResult_"
                        + file.getName() + "_" + kFold + "_MPBoost-"
                        + iterations + ".class", true);
        storageManager.close();

        ClassificationComparer cc = new ClassificationComparer(
                kFoldEvaluator.getClassification(),
                training.getClassificationDB());
        tableSet = cc.evaluate();

        tableSet.setName(file.getName());
        ContingencyTableDataManager.writeContingencyTableSet(dataPath
                + "/kFoldResult_" + file.getName() + "_" + kFold + "_MPBoost-"
                + iterations, tableSet);

        String report = EvaluationReport.printReport(tableSet,
                training.getCategoryDB());

        FileWriter writer = new FileWriter(dataPath + "/kFoldResult_"
                + file.getName() + "_" + kFold + "_MPBoost-" + iterations
                + ".txt");
        writer.write(report);
        writer.close();

    }
}
