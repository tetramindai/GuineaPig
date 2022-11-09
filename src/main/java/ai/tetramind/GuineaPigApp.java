package ai.tetramind;

import ai.tetramind.guinea.pig.GuineaPig;
import ai.tetramind.guinea.pig.Incubator;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class GuineaPigApp {

    private static final Random RANDOM = new SecureRandom();

    private final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors() * 10;

    private GuineaPigApp() {
    }

    private static Map<double[][], double[]> buildDataSet() {

        var result = new HashMap<double[][], double[]>();


        for (var i = 0; i < 1000; i++) {

            var sequence = new double[10][1];
            for (var s = 0; s < sequence.length; s++) {
                sequence[s][0] = RANDOM.nextDouble();
            }

            var solution = false;

            for (var j = 0; j < sequence.length - 2; j++) {

                var a = sequence[j][0];
                var b = sequence[j + 1][0];
                var c = sequence[j + 2][0];

                if (a < b && b < c) {
                    solution = true;
                    break;
                }
            }

            result.put(sequence, new double[]{solution ? 1.0 : -1.0});
        }

        return result;
    }

    public static void main(String[] args) {

        System.out.println("GuineaPig Started");

        var dataSet = buildDataSet();

        var incubator = new Incubator(dataSet, new GuineaPig(1, 1));

        for (var i = 0; i < AVAILABLE_PROCESSORS; i++) {
            incubator.createWorker();
        }
    }
}
