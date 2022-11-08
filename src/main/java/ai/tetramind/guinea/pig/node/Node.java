package ai.tetramind.guinea.pig.node;

public abstract class Node {

    public static final double DEFAULT_VALUE = 0.0;

    protected double value;

    public Node() {
        value = DEFAULT_VALUE;
    }
}
