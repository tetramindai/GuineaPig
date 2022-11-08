package ai.tetramind;

import ai.tetramind.guinea.pig.Incubator;

import java.util.HashMap;

public final class GuineaPigApp {
    private final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private GuineaPigApp() {
    }

    public static void main(String[] args) {

        System.out.println("GuineaPig Started");

        var dataSet = new HashMap<double[][], double[]>();
        dataSet.put(new double[][]{{0.5}, {0.6}, {0.8}}, new double[]{-0.5});
        dataSet.put(new double[][]{{0.9}, {0.6}, {0.8}}, new double[]{-0.5});
        dataSet.put(new double[][]{{0.5}, {0.5}, {0.8}}, new double[]{-0.5});

        var incubator = new Incubator(dataSet);

        for (var i = 0; i < AVAILABLE_PROCESSORS; i++) {
            incubator.createWorker();
        }
    }
}
