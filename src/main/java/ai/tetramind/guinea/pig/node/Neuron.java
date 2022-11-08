package ai.tetramind.guinea.pig.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Neuron extends Node {
    private final double[] weights;
    private double bias;

    public Neuron(int dimension) {

        if (dimension <= 0) throw new IllegalStateException();

        weights = new double[dimension];
        Arrays.fill(weights, DEFAULT_VALUE);
        bias = DEFAULT_VALUE;
    }

    public void changeBias(double value) {
        bias = value;
    }

    public void compute(@NotNull double[] inputs) {

        if (weights.length < inputs.length) throw new IllegalStateException();

        var sum = bias;
        for (var i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }

        value = (1.0 / (1.0 + Math.pow(Math.E, -sum)));
    }
}
