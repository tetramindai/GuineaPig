package ai.tetramind;

import ai.tetramind.guinea.pig.GuineaPig;
import ai.tetramind.guinea.pig.Incubator;
import ai.tetramind.guinea.pig.Predator;
import ai.tetramind.guinea.pig.Struggle;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public final class GuineaPigApp {

    private static double[][] loadTrainData() {

        var tmp = new ArrayList<double[]>();

        double lastValue = 0.0;
        long lastTime = 0;

        try (var input = new BufferedReader(new InputStreamReader(Objects.requireNonNull(GuineaPigApp.class.getResourceAsStream("/data.txt"))))) {

            var line = "";

            while ((line = input.readLine()) != null) {

                var cols = line.split(" ");

                var time = Long.parseLong(cols[0]);
                var value = Double.parseDouble(cols[1]);

                if (lastValue != 0.0 && lastTime != 0.0) {
                    var element = new double[2];
                    element[0] = (time - lastTime) * 1000.0 / lastTime;
                    element[1] = (value - lastValue) / lastValue;
                    tmp.add(element);
                }

                lastValue = value;
                lastTime = time;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmp.toArray(new double[0][]);
    }

    private GuineaPigApp() {
    }

    public static void main(String[] args) {


        var incubator = new Incubator(new MyPredator(), new GuineaPig(1, 1));

        incubator.start();

        var solution = incubator.waitSolution();

        try (var output = new ObjectOutputStream(new FileOutputStream("solution.obj"))) {

            output.writeObject(solution);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File saved!");
    }

    private static class MyPredator implements Predator {

        private static final int PAST_LENGTH = 120;

        private static final Random RANDOM = new SecureRandom();

        private final double[][] data;

        private MyPredator() {
            data = loadTrainData();
        }

        @Override
        public Struggle generate() {

            var index = RANDOM.nextInt(data.length - PAST_LENGTH - 1);

            var inputs = new double[PAST_LENGTH][2];
            System.arraycopy(data, index, inputs, 0, inputs.length);

            var output = data[index + 1];

            return new Struggle(inputs, output);
        }
    }
}
