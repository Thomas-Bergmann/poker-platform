package de.hatoka.poker.bot.strategy.neural;

import java.util.NoSuchElementException;

enum NeuralResult
{
    FOLD(0, 1, 0, -1), CALL(1, 0, 1, 0), RAISE(2, -1, 0, 1);

    private final int index;
    private final double[] outputs;

    private NeuralResult(int index, double a, double b, double c)
    {
        this.index = index;
        this.outputs = new double[] { a, b, c };
    }

    public int getIndex()
    {
        return index;
    }

    public double[] getOutputs()
    {
        return outputs;
    }

    private static NeuralResult valueViaIndex(int index)
    {
        for (NeuralResult aResult : NeuralResult.values())
        {
            if (aResult.getIndex() == index) return aResult;
        }
        throw new NoSuchElementException("Result doesn't exist: '" + index + "'");
    }

    public static NeuralResult mapToResult(double[] output)
    {
        double max = 0;
        for (double o : output)
        {
            if (o > max)
            {
                max = o;
            }
        }
        for (int i = 0; i < output.length; i++)
        {
            if (output[i] == max)
            {
                return NeuralResult.valueViaIndex(i);
            }
        }
        throw new NoSuchElementException("Result doesn't exist: '" + max + "'");
    }

}
