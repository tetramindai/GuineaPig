package ai.tetramind;

import ai.tetramind.guinea.pig.GuineaPig;

import java.security.SecureRandom;

public final class GuineaPigApp {

    private GuineaPigApp() {
    }

    public static void main(String[] args) {

        var random = new SecureRandom();

        var ai = new GuineaPig(1, 1);

        while (true) {

            var values = new double[10][1];

            var ordered = true;
            Double lastValue = null;

            for (var v : values) {

                v[0] = random.nextDouble();

                if (lastValue != null && lastValue > v[0]) {
                    ordered = false;
                }

                lastValue = v[0];
            }

            var result = ai.evaluate(values)[0] > 0.0;

            if (ordered && result) {
                System.out.println("Good !");
            } else {
                ai.randomMutation();
                System.out.println("Mutation !");
            }
        }
    }
}
