package blood.bear.neuro.view;

/**
 * Created by Blood Bear on 21-Sep-17.
 */

public final class Triangle {
    public final Neuron neuron_1, neuron_2, neuron_3;
    public boolean active = true;
    public int alpha = 255;
    public boolean increment=true;

    public Triangle(Neuron neuron_1, Neuron neuron_2, Neuron neuron_3) {
        this.neuron_1 = neuron_1;
        this.neuron_2 = neuron_2;
        this.neuron_3 = neuron_3;
    }
}
