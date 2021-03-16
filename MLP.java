import java.io.IOException;
import java.util.Arrays;
import DataHandlers.Extractor;
import DataHandlers.Writer;

/**
 * The MLP Class defines neurons with weights, biases, and outputs
 * using 2D and 3D arrays. All MLPs will contain a single
 * input layer, hidden layer and output layer, and hence will all have 3 layers
 */
public class MLP{
    /**
     * Define 2D and 3D array of type double, to prevent rounding errors
     * of floating points by using greater precision. Index are as follows:
     * neuronOutput[layer][neuron], bias[layer, neuron] and finally
     * neuronWeight[layer][neuron][prevNeuron]
     */
    private double[][] outputs;
    private double[][] biases;
    private double[][][] weights;

    // define data, error, and deltaJ arrays
    private double[][] data;
    public double[] errors;
    private double[][] validationData;
    private double[] validationErrors;
    private double learningParam;

    // define number of neurons for each layer
    public final int INPUT_SIZE;
    public final int HIDDEN_SIZE;
    public final int OUTPUT_SIZE;

    // define writer and extractor for excel sheet
    private static Extractor extractor;
    private static Writer writer;

    public MLP(int INPUT_SIZE, int HIDDEN_SIZE, int OUTPUT_SIZE, double learningParameter, double[][] inputs, double[][] validationData){
        // set learning parameter
        this.learningParam = learningParameter;

        // define number of neurons in each layer
        this.INPUT_SIZE = INPUT_SIZE;
        this.HIDDEN_SIZE = HIDDEN_SIZE;
        this.OUTPUT_SIZE = OUTPUT_SIZE;

        // initialise number of layers
        this.outputs = new double[3][];
        this.biases = new double[2][];
        this.weights = new double[2][][];

        // initialise number of outputs for each layer
        this.outputs[0] = new double[INPUT_SIZE];
        this.outputs[1] = new double[HIDDEN_SIZE];
        this.outputs[2] = new double[OUTPUT_SIZE];

        // initialise biases for hidden and output layers
        this.biases[0] = extractor.getWeightsBiases(2, 1, HIDDEN_SIZE);
        this.biases[1] = extractor.getWeightsBiases(3, 1, OUTPUT_SIZE);

        // initialise weights between layers
        this.weights[0] = new double[HIDDEN_SIZE][INPUT_SIZE];
        this.weights[1] = new double[OUTPUT_SIZE][HIDDEN_SIZE];
        for(int i=0; i<HIDDEN_SIZE; i++)
        {
            int row = (8*i)+1;
            weights[0][i] = extractor.getWeightsBiases(0, row, row+7);
        }
        weights[1][0] = extractor.getWeightsBiases(1, 1, HIDDEN_SIZE);

        // define data for MLP object
        this.data = inputs;
        this.validationData = validationData;
    }

    private double activation(double sum){
        return 1/(1+Math.exp(-sum));
    }

    private double forwardPass(double[] predictands){
        // equate outputs of input nodes to given predictands
        this.outputs[0] = predictands;
        for(int i=0; i<biases.length; i++)
        for(int j=0; j<biases[i].length; j++)
        {
            // get bias at given node
            double bias = biases[i][j];
            double sum = 0;

            // iterate over all weights and outputs leading to bias node, sum then apply activation function
            for(int k=0; k<weights[i][j].length; k++){
                double weight = weights[i][j][k];
                double prevOutput = outputs[i][k];
                sum += weight*prevOutput;
            }
            outputs[i+1][j] = activation(sum+bias);
        }
        return outputs[outputs.length-1][0];
    }

    private void updateWeightsBiases(double[][] deltaJs){
        // iterate over weights and biases
        for(int layer=0; layer<2; layer++)
        for(int neuron=0; neuron<weights[layer].length; neuron++){
            // get deltaJ at the given neuron
            double deltaJ = deltaJs[layer][neuron];

            // get and update bias at neuron
            double bias = biases[layer][neuron];
            biases[layer][neuron] = bias + this.learningParam*deltaJ* outputs[layer+1][neuron];

            for(int prevNeuron=0; prevNeuron<weights[layer][neuron].length; prevNeuron++){
                // get and update weight moving out of neuron
                double weight = weights[layer][neuron][prevNeuron];
                weights[layer][neuron][prevNeuron] = weight + this.learningParam*deltaJ*outputs[layer][prevNeuron];
            }
        }
    }

    private void backPropagate(double actual, double prediction) {
        // initialise deltaJ
        double[][] deltaJs = new double[2][];
        deltaJs[0] = new double[HIDDEN_SIZE];
        deltaJs[1] = new double[1];

        // set deltaJ for output nodes, using derivative of sigmoid function
        double derivative = prediction*(1-prediction);
        deltaJs[1][0] = (actual-prediction)*derivative;

        // set deltaJ for hidden nodes
        for(int i=0; i<HIDDEN_SIZE; i++){
            double output = outputs[1][i];
            derivative = output*(1-output);
            deltaJs[0][i] = weights[1][0][i]*deltaJs[1][0]*derivative;
        }
        this.updateWeightsBiases(deltaJs);
    }

    public void runNetwork(int epochs){
        // initialise training and validation errors
        this.errors = new double[epochs];
        this.validationErrors = new double[epochs];

        // iterate over epochs
        for(int i=0; i<epochs; i++){
            double meanSquaredError = 0;
            double validationError = 0;

            // iterate over training and validation data, computing errors
            for(int row=0; row<data.length; row++){
                // extract predictands and predictors
                double[] predictands = Arrays.copyOfRange(this.data[row], 0, 8);
                double actual = data[row][8];

                // run forward pass, backpropagation algorithm to adjust weights
                double prediction = forwardPass(predictands);
                this.backPropagate(actual, prediction);

                // compute squared error, sum to previous errors
                meanSquaredError += (actual - prediction)*(actual - prediction);

                // necessary check to avoid out of bounds error (validation and testing sets are different size)
                if(row<validationData.length){
                    // extract predictands and predictors
                    double[] valPredictands = Arrays.copyOfRange(validationData[row], 0, 8);
                    double valActual = validationData[row][8];

                    // run forward pass, compute squared error for validation, sum to previous errors
                    double valPrediction = forwardPass(valPredictands);
                    validationError += (valActual - valPrediction)*(valActual - valPrediction);
                }
            }
            this.errors[i] = meanSquaredError/data.length;
            this.validationErrors[i] = validationError/validationData.length;
        }
    }

    public static void main(String[] args) throws IOException {
        // instantiate extractor, writer and get training and validation data
        extractor = new Extractor();
        writer = new Writer(extractor);
        double[][] inputs = extractor.getData(1, 352);
        double[][] validationData = extractor.getData(353, 470);

        // iterate over number of hidden nodes, run 1 million epochs for each and write errors into Excel
        for(int i=4; i<=16; i++){
            MLP neuralNetwork = new MLP(8, i, 1, 0.1, inputs, validationData);
            neuralNetwork.runNetwork(1000000);

            // write errors
            int index = (i-4)*2;
            writer.writeError(neuralNetwork.errors, index);
            writer.writeError(neuralNetwork.validationErrors, index+1);
        }
    }
}