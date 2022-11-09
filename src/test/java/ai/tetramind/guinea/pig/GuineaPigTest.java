package ai.tetramind.guinea.pig;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GuineaPigTest {
    private static final Random RANDOM = new SecureRandom();

    private static Map<double[][], double[]> buildDataSet() {

        var result = new HashMap<double[][], double[]>();

        for (var i = 0; i < 10000; i++) {

            var sequence = new double[10][1];
            for (var s = 0; s < sequence.length; s++) {
                sequence[s][0] = RANDOM.nextDouble();
            }

            var contains = false;

            for (var oldSequence : result.keySet()) {
                if (Arrays.deepEquals(oldSequence, sequence)) {
                    contains = true;
                    break;
                }
            }


            if (!contains) {

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
        }

        return result;
    }

    private class MyPredator implements Predator {

        private final Map<double[][], double[]> dataSet = buildDataSet();

        @Override
        public Struggle generate() {

            Struggle result = null;

            var keys = dataSet.keySet();

            var index = RANDOM.nextInt(keys.size());

            for (var key : keys) {

                index--;

                if (index < 0) {

                    var expected = dataSet.get(key);

                    result = new Struggle(key, expected);
                }
            }

            return result;
        }
    }

    @Test
    void testWorking() {

        var incubator = new Incubator(new MyPredator(), new GuineaPig(1, 1));

        incubator.start();

        var solution = incubator.waitSolution();

        assertNotNull(solution);
    }
}