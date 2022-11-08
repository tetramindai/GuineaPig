package ai.tetramind;

import ai.tetramind.guinea.pig.GuineaPig;

import java.util.Arrays;

public final class GuineaPigApp {

    private GuineaPigApp() {
    }

    public static void main(String[] args) {

        var ai = new GuineaPig(1, 1);

        var input = ai.input(0);

        input.write(0.5);

        var output = ai.output(0);

        while (true) {

            ai.compute();

            ai.randomMutation();

            System.out.println("Value : " + output.read());
        }
    }
}
