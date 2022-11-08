package ai.tetramind.guinea.pig.node;

public final class Input extends Node {

    public Input() {
        super();
    }

    public void write(double value) {
        this.value = value;
    }

    protected double load() {
        return value;
    }
}
