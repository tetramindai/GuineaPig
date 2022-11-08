package ai.tetramind.guinea.pig;

import ai.tetramind.guinea.pig.node.Input;
import ai.tetramind.guinea.pig.node.Neuron;
import ai.tetramind.guinea.pig.node.Output;
import org.jetbrains.annotations.NotNull;

public final class GuineaPig {
    private final static int NETWORK_HEIGHT = 100;
    private final static int NETWORK_WEIGHT = (int) (NETWORK_HEIGHT * 1.618033988);
    private final Input[] inputs;
    private final Neuron[][] neurons;
    private final Output[] outputs;

    public GuineaPig(int input, int output) {

        inputs = new Input[input];
        for (var i = 0; i < inputs.length; i++) {
            inputs[i] = new Input();
        }

        neurons = new Neuron[NETWORK_HEIGHT][NETWORK_WEIGHT];
        for (var i = 0; i < neurons.length; i++) {
            for (var j = 0; j < neurons[i].length; j++) {
                neurons[i][j] = new Neuron((i + 1 < neurons.length) ? NETWORK_WEIGHT + 1 : NETWORK_WEIGHT);
            }
        }

        outputs = new Output[output];
        for (var i = 0; i < outputs.length; i++) {
            outputs[i] = new Output();
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

    }
}
