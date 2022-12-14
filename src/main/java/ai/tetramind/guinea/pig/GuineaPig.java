package ai.tetramind.guinea.pig;

import ai.tetramind.guinea.pig.node.Input;
import ai.tetramind.guinea.pig.node.Neuron;
import ai.tetramind.guinea.pig.node.Node;
import ai.tetramind.guinea.pig.node.Output;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public final class GuineaPig implements Genetic, Cloneable {
    private static final Random RANDOM = new SecureRandom();
    private final static int NETWORK_HEIGHT = 100;
    private final static int NETWORK_WEIGHT = (int) (NETWORK_HEIGHT * 1.618033988);
    private final Input[] inputs;
    private final Neuron[][] neurons;
    private final Output[] outputs;

    public GuineaPig(int input, int output) {

        if (input <= 0 || output <= 0) throw new IllegalStateException();

        inputs = new Input[input];
        for (var i = 0; i < inputs.length; i++) {
            inputs[i] = new Input();
        }

        neurons = new Neuron[NETWORK_HEIGHT][NETWORK_WEIGHT];
        for (var l = 0; l < neurons.length; l++) {
            for (var n = 0; n < neurons[l].length; n++) {

                var dimension = 0;

                if (l == 0) {
                    dimension = inputs.length;
                } else if (l + 1 < neurons.length) {
                    dimension = NETWORK_WEIGHT + 1;
                } else {
                    dimension = NETWORK_WEIGHT;
                }

                neurons[l][n] = new Neuron(dimension);
            }
        }

        outputs = new Output[output];
        for (var i = 0; i < outputs.length; i++) {
            outputs[i] = new Output(NETWORK_WEIGHT);
        }
    }

    public @NotNull Input input(int index) {

        if (index < 0 || index >= inputs.length) throw new IllegalStateException();

        return inputs[index];
    }

    public @NotNull Output output(int index) {

        if (index < 0 || index >= outputs.length) throw new IllegalStateException();

        return outputs[index];
    }

    public void compute() {

        var values = new double[NETWORK_WEIGHT];
        var epoch = new double[NETWORK_WEIGHT];

        Arrays.fill(epoch, Node.DEFAULT_VALUE);
        Arrays.fill(values, Node.DEFAULT_VALUE);

        for (var i = 0; i < inputs.length; i++) {
            values[i] = inputs[i].getValue();
        }

        for (var l = 0; l < neurons.length; l++) {

            var layer = neurons[l];
            var nextLayer = (l + 1 < neurons.length) ? neurons[l + 1] : null;

            Arrays.fill(epoch, Node.DEFAULT_VALUE);

            if (nextLayer != null) {
                for (var n = 0; n < nextLayer.length; n++) {
                    epoch[n] = nextLayer[n].getResult();
                }
            }

            computeLayer(values, layer, epoch);

            Arrays.fill(values, Node.DEFAULT_VALUE);

            for (var n = 0; n < layer.length; n++) {
                values[n] = layer[n].getResult();
            }
        }

        for (var output : outputs) {
            output.compute(values);
        }
    }

    private void computeLayer(double @NotNull [] values, @NotNull Neuron[] layer, double @NotNull [] epoch) {

        if (values.length != layer.length) throw new IllegalStateException();
        if (epoch.length != layer.length) throw new IllegalStateException();

        for (var n = 0; n < layer.length; n++) {

            var neuron = layer[n];

            var inputs = new double[layer.length + 1];
            System.arraycopy(values, 0, inputs, 0, layer.length);
            for (var i = layer.length; i < inputs.length; i++) {
                inputs[i] = epoch[n];
            }

            neuron.compute(inputs);
        }
    }

    @Override
    public String toString() {

        var builder = new StringBuilder();

        for (var i = 0; i < inputs.length; i++) {

            builder.append(inputs[i]);

            if (i + 1 < inputs.length) {
                builder.append(' ');
            }
        }

        builder.append(System.lineSeparator());

        for (var layer : neurons) {

            for (var neuron : layer) {

                builder.append(neuron);

                builder.append(' ');
            }

            builder.append(System.lineSeparator());
        }

        for (var output : outputs) {

            builder.append(output);

            builder.append(' ');
        }

        return new String(builder);
    }


    public void forgot() {

        for (var input : inputs) {
            input.reset();
        }

        for (var layer : neurons) {
            for (var neuron : layer) {
                neuron.reset();
            }
        }

        for (var output : outputs) {
            output.reset();
        }
    }

    public double @NotNull [] evaluate(double @NotNull [] @NotNull [] dataset) {

        forgot();

        var result = new double[outputs.length];

        Arrays.fill(result, Node.DEFAULT_VALUE);

        for (var values : dataset) {

            if (values.length != inputs.length) throw new IllegalStateException();

            for (var i = 0; i < values.length; i++) {

                var input = input(i);

                input.write(values[i]);
            }

            compute();
        }

        for (var o = 0; o < outputs.length; o++) {

            var output = output(o);

            result[o] = output.read();
        }

        return result;
    }

    public @NotNull GuineaPig mate(@NotNull GuineaPig guineaPig) {

        if (inputs.length != guineaPig.inputs.length || outputs.length != guineaPig.outputs.length)
            throw new IllegalStateException();

        var child = new GuineaPig(inputs.length, outputs.length);

        System.arraycopy(inputs, 0, child.inputs, 0, inputs.length);

        var middleLayer = neurons.length / 2;

        for (var l = 0; l < middleLayer; l++) {
            System.arraycopy(neurons[l], 0, child.neurons[l], 0, neurons[l].length);
        }

        for (var l = middleLayer; l < guineaPig.neurons.length; l++) {
            System.arraycopy(guineaPig.neurons[l], 0, child.neurons[l], 0, guineaPig.neurons[l].length);
        }

        System.arraycopy(guineaPig.outputs, 0, child.outputs, 0, guineaPig.outputs.length);

        return child;
    }

    public void mutate() {

        var layerIndex = RANDOM.nextInt(neurons.length + 1);

        var randomValue = RANDOM.nextDouble() * (RANDOM.nextBoolean() ? -1.0 : 1.0);

        if (layerIndex >= neurons.length) {

            var outputIndex = RANDOM.nextInt(outputs.length);

            var output = outputs[outputIndex];

            var weightsLength = output.getWeightsLength();

            var weightIndex = RANDOM.nextInt(weightsLength);

            output.changeWeight(weightIndex, randomValue);

        } else {

            var layer = neurons[layerIndex];

            var neuronIndex = RANDOM.nextInt(layer.length);

            var neuron = layer[neuronIndex];

            if (RANDOM.nextBoolean()) {

                neuron.changeBias(randomValue);

            } else {

                var weightsLength = neuron.getWeightsLength();

                var weightIndex = RANDOM.nextInt(weightsLength);

                neuron.changeWeight(weightIndex, randomValue);
            }
        }
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof GuineaPig guineaPig)) return false;

        if (!Arrays.equals(inputs, guineaPig.inputs)) return false;

        if (!Arrays.deepEquals(neurons, guineaPig.neurons)) return false;

        return Arrays.equals(outputs, guineaPig.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(inputs), Arrays.hashCode(outputs), Arrays.deepHashCode(neurons));
    }


    @Override
    public GuineaPig clone() {

        var result = new GuineaPig(inputs.length, outputs.length);

        return result;
    }
}
