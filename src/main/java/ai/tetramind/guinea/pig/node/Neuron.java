package ai.tetramind.guinea.pig.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public final class Neuron extends Node implements Computed {
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

    @Override
    public void compute(double @NotNull [] inputs) {

        var sum = bias;
        for (var i = 0; i < inputs.length && i < weights.length; i++) {
            sum += inputs[i] * weights[i];
        }

        value = (1.0 / (1.0 + Math.pow(Math.E, -sum)));
    }

    public void changeWeight(int index, double value) {

        if (index < 0 || index >= weights.length) throw new IllegalStateException();

        weights[index] = value;
    }

    public double[] getWeights() {
        return weights.clone();
    }

    public double getBias() {
        return bias;
    }

    public double getResult() {
        return value;
    }

    public int getWeightsLength() {
        return weights.length;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Neuron neuron)) return false;

        if (neuron.bias != bias) return false;

        return Arrays.equals(weights, neuron.weights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bias, Arrays.hashCode(weights));
    }
}
