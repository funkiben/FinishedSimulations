package simulation;
import java.awt.Color;

import lab.LabFrame;
import lab.SigFig;
import lab.component.EmptyComponent;
import lab.component.HorizontalGraduation;
import lab.component.VerticalGraduation;
import lab.component.container.Bulb;
import lab.component.container.ContentState;
import lab.component.data.GraphDataSet;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.LabeledDoubleSlider;
import lab.component.swing.input.DoubleSlider;
import lab.component.data.Graph;

public class HIEquilibrium extends LabFrame {

	private static final long serialVersionUID = 1L;



	public static void main(String[] args) {
		new HIEquilibrium();
		
	}

	private static final int SIGFIGS = 3;
	
	private final Bulb bulb;
	
	private final Graph graph;
	private final GraphDataSet H2DataSet;
	private final GraphDataSet I2DataSet;
	private final GraphDataSet HIDataSet;
	
	// Kc = (HI)^2 / (H2)(I2)
	// Kc = (HI + 2x)^2 / (H2 - x)(I2 - x)
	// Kc = (HI - 2s)^2 / (H2 + x)(I2 + x)
	private final double Kc = 56;

	private final float rate = 0.1f;
	
	private double H2Concentration = 5.0;
	private double I2Concentration = 2.0;
	private double HIConcentration = 10.0;

	private double time = 0;

	
	
	private final LabeledDoubleSlider H2Slider = new LabeledDoubleSlider(170, 20, 0.0f, 10.0f, 1.0f, 3, DoubleSlider.HORIZONTAL);
	private final LabeledDoubleSlider I2Slider = new LabeledDoubleSlider(170, 20, 0.0f, 10.0f, 1.0f, 3, DoubleSlider.HORIZONTAL);
	private final LabeledDoubleSlider HISlider = new LabeledDoubleSlider(170, 20, 0.0f, 10.0f, 1.0f, 3, DoubleSlider.HORIZONTAL);
	
	private final Button H2SetButton = new Button(60, 30, "Set") {
		@Override
		public void doSomething() {
			H2Concentration = H2Slider.getValue();
		}
	};
	
	private final Button I2SetButton = new Button(60, 30, "Set") {
		@Override
		public void doSomething() {
			I2Concentration = I2Slider.getValue();
		}
	};
	
	private final Button HISetButton = new Button(60, 30, "Set") {
		@Override
		public void doSomething() {
			HIConcentration = HISlider.getValue();
		}
	};
	
	
	public HIEquilibrium() {
		super("H2 + I2 -> 2HI", 820, 630);

		bulb = new Bulb(300, 300);
		bulb.setContentState(ContentState.GAS);
		bulb.setContentColor(new Color(115, 37, 119));
		bulb.setColor(new Color(250, 250, 250));
		bulb.setOffsetY(100);
		bulb.setLabelOffsetX(100);
		bulb.setLabelOffsetY(150);
		bulb.setLabelSize(15);
		
		VerticalGraduation vg = new VerticalGraduation(0, 1, 2, 0.5);
		HorizontalGraduation hg = new HorizontalGraduation(0, 60, 60, 15);

		graph = new Graph(400, 400, "Concentrations", "time (s)", "molarity (M)", vg, hg);

		graph.setDrawYLines(false);
		
		graph.setOffsetX(70);
		graph.setOffsetY(50);

		I2DataSet = new GraphDataSet("I2", true, true);
		I2DataSet.setColor(new Color(255, 0, 127));
		H2DataSet = new GraphDataSet("H2", true, true);
		H2DataSet.setColor(Color.red);
		HIDataSet = new GraphDataSet("HI", true, true);
		HIDataSet.setColor(Color.black);

		graph.addDataSet(I2DataSet);
		graph.addDataSet(H2DataSet);
		graph.addDataSet(HIDataSet);

		addComponent(graph, bulb);
		
		addComponent(new EmptyComponent(900, 20));
	
		H2Slider.setOffsetY(20);
		I2Slider.setOffsetY(20);
		HISlider.setOffsetY(20);
		
		H2Slider.getLabel().setOffsetY(-5);
		I2Slider.getLabel().setOffsetY(-5);
		HISlider.getLabel().setOffsetY(-5);
		
		H2SetButton.setOffsetX(55);
		I2SetButton.setOffsetX(55);
		HISetButton.setOffsetX(55);
		
		
		addComponent(new EmptyComponent(80, 0), new Label(20, 30, "H2"), H2Slider, H2SetButton, new EmptyComponent(400, 0));
		addComponent(new EmptyComponent(80, 0), new Label(20, 30, "I2"), I2Slider, I2SetButton, new EmptyComponent(400, 0));
		addComponent(new EmptyComponent(80, 0), new Label(20, 30, "HI"), HISlider, HISetButton);
		
		start(30);
		
	}


	@Override
	public void update() {
		
		time += 0.1;

		I2DataSet.addPoint(time, I2Concentration);
		H2DataSet.addPoint(time, H2Concentration);
		HIDataSet.addPoint(time, HIConcentration);

		double offset;
		double Qc = Qc();

		if (Qc > Kc) { // NEED MORE REACTANT, SHIFT BACKWARD

			// Kc = (HI - 2x)^2 / (H2 + x)(I2 + x)
			// Kc = (HI - 2x)(HI - 2x) / (H2 + x)(I2 + x)

			// Kc = (4x^2 - (4*HI)x + (HI*HI)) / (x^2 + (I2+H2)x + (H2*I2))

			double a1, b1, c1, a2, b2, c2;

			// cross multiply and foil reactants
			a1 = Kc;
			b1 = Kc * (I2Concentration + H2Concentration);
			c1 = Kc * H2Concentration * I2Concentration;

			// foil products
			a2 = 4;
			b2 = -4 * HIConcentration;
			c2 = HIConcentration * HIConcentration;

			double[] zeros = findZeros(a1 - a2, b1 - b2, c1 - c2);
			
			offset = HIConcentration - 2 * zeros[0] > 0 ? zeros[0] : zeros[1];

			
			H2Concentration = lerp(H2Concentration, H2Concentration + offset, rate);
			I2Concentration = lerp(I2Concentration, I2Concentration + offset, rate);
			HIConcentration = lerp(HIConcentration, HIConcentration - 2 * offset, rate);


		} else if (Qc < Kc) { // NEED MORE PRODUCT, SHIFT FORWARD
			
			// Kc = (HI + 2x)^2 / (H2 - x)(I2 - x)
			// Kc = (HI + 2x)(HI + 2X) / (H2 - x)(I2 - x)
			
			// Kc = (4x^2 + (4*HI)x + (HI*HI)) / (x^2 - (H2+I2)x + (HI*I2))
			
			
			double a1, b1, c1, a2, b2, c2;

			// cross multiply and foil reactants
			a1 = Kc;
			b1 = Kc * -(I2Concentration + H2Concentration);
			c1 = Kc * H2Concentration * I2Concentration;

			// foil products
			a2 = 4;
			b2 = 4 * HIConcentration;
			c2 = HIConcentration * HIConcentration;

			double[] zeros = findZeros(a1 - a2, b1 - b2, c1 - c2);

			
			offset = H2Concentration - zeros[0] > 0 && I2Concentration - zeros[0] > 0 ? zeros[0] : zeros[1];
			
			H2Concentration = lerp(H2Concentration, H2Concentration - offset, rate);
			I2Concentration = lerp(I2Concentration, I2Concentration - offset, rate);
			HIConcentration = lerp(HIConcentration, HIConcentration + 2 * offset, rate);

			
		}
		
		
		graph.getvGraduation().setEnd(Math.max(graph.getvGraduation().getEnd(), getMinYAxisEnd()));
		//graph.getvGraduation().setEnd(lerp(graph.getvGraduation().getEnd(), getMinYAxisEnd(), 0.2f));
		
		
		if ((int) time % 60 == 59) {
			graph.gethGraduation().setStart((int) time + 1);
			graph.gethGraduation().setEnd((int) time + 61);

			graph.getvGraduation().setEnd(getMinYAxisEnd());
		}
		
		
		double m = graph.getMaxYSubTicks(30);
		
		graph.getvGraduation().setSubLineIntervals(m);
		graph.getvGraduation().setLineIntervals(m < 1 ? 1 : 2);
		
		
		bulb.setLabel("H2: " + SigFig.sigfigalize(H2Concentration, SIGFIGS) + "M\n"
					+ "I2: " + SigFig.sigfigalize(I2Concentration, SIGFIGS) + "M\n"
					+ "HI: " + SigFig.sigfigalize(HIConcentration, SIGFIGS) + "M");
		
		bulb.setValue(I2Concentration / 10 * 255);

	}
	
	private int getMinYAxisEnd() {
		int e = (int) Math.ceil(Math.max(HIConcentration, Math.max(H2Concentration, I2Concentration)));
		return e % 2 == 1 ? e + 1 : e;
	}
	
	

	private double Qc() {
		return (HIConcentration * HIConcentration) / (H2Concentration * I2Concentration);
	}

	private static double lerp(double v1, double v2, float f) {
		return (v2 - v1) * f + v1;
	}

	private static double[] findZeros(double a, double b, double c) {
		double s = Math.sqrt((b * b) - 4 * a * c);

		double[] zeros = new double[2];
		zeros[0] = (-b + s) / (2 * a);
		zeros[1] = (-b - s) / (2 * a);

		return zeros;
	}

}
