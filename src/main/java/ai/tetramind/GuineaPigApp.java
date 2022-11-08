package ai.tetramind;

import ai.tetramind.guinea.pig.GuineaPig;

import java.security.SecureRandom;

public final class GuineaPigApp {

    private GuineaPigApp() {
    }

    public static void main(String[] args) {

        var random = new SecureRandom();

        var ai = new GuineaPig(1, 1);

        var cycle = 0;
        var success = 0;

        while (true) {

            cycle++;

            var values = new double[2][1];

            var ordered = true;
            Double lastValue = null;

            for (var v : values) {

                v[0] = random.nextDouble();

                if (lastValue != null && lastValue > v[0]) {
                    ordered = false;
                }

                lastValue = v[0];
            }

            var result = ai.evaluate(values);


            if (ordered == result[0] > 0.0) {
                success++;
            } else {
                ai.randomMutation();
            }

            if (cycle % 100 == 0) {
                System.out.println(Math.round(success * 100.0 / cycle) + "%");
            }
        }
    }
}
