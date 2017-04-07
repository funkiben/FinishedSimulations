package LeChatelierConcentration;

import java.awt.Color;

import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.container.Bulb;
import lab.component.container.ContentState;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.slider.LabeledDoubleSlider;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

public class LeChatelierConcentration extends LabFrame {

	private static final long serialVersionUID = 2402023506629915960L;

	private Bulb bulb;
	
	private LabeledDoubleSlider cSlider;
	private LabeledDoubleSlider h2oSlider;
	private LabeledDoubleSlider coSlider;
	private LabeledDoubleSlider h2Slider;

	private VerticalGraduation vGrad;
	private HorizontalGraduation hGrad;

	private Label cLabel;
	private Label h2oLabel;
	private Label coLabel;
	private Label h2Label;

	private double cMolesEquilibrium;
	private double h2oMolesEquilibrium;
	private double coMolesEquilibrium;
	private double h2MolesEquilibrium;
	private double globalTime;
	
	private GraphDataSet cDataSet;
	private GraphDataSet h2oDataSet;
	private GraphDataSet coDataSet;
	private GraphDataSet h2DataSet;
	
	private Button resetButton;
	
	private int pointIndex = 0;
	
	private final double EQUILIBRIUM_CONSTANT = 27.5625;
	private final double TIME_INCREMENT = .5;
	
	private Graph graph;

	public static void main(String[] args) {
		new LeChatelierConcentration("LeChatelier", 1920, 1000);
	}

	public LeChatelierConcentration(String name, int width, int height) {
		super(name, width, height);

		globalTime = 0;
		bulb = new Bulb(250, 250);
		addComponent(bulb);

		EmptyComponent sliderHolder = new EmptyComponent(350, 400);

		cSlider = new LabeledDoubleSlider(250, 0.01, 1, .00009, 5, 0) {
			@Override
			public void update() {
				cSlider.getLabel().setText(SigFig.sigfigalize(getValue(), cSlider.getSigFigs()) + " Moles C");
			}
		};
		cSlider.getLabel().setWidth(cSlider.getLabel().getWidth() + 100);
		sliderHolder.addChild(cSlider);

		h2oSlider = new LabeledDoubleSlider(250, 0.01, 1, .00009, 5, 0) {
			@Override
			public void update() {
				h2oSlider.getLabel().setText(SigFig.sigfigalize(getValue(), h2oSlider.getSigFigs()) + " Moles H2O");
			}
		};
		h2oSlider.getLabel().setWidth(h2oSlider.getLabel().getWidth() + 100);
		h2oSlider.setOffsetY(10);
		sliderHolder.addChild(h2oSlider);

		coSlider = new LabeledDoubleSlider(250, 0.01, 1, .00009, 5, 0) {
			@Override
			public void update() {
				coSlider.getLabel().setText(SigFig.sigfigalize(getValue(), coSlider.getSigFigs()) + " Moles CO");
			}
		};
		coSlider.getLabel().setWidth(coSlider.getLabel().getWidth() + 100);
		coSlider.setOffsetY(10);
		sliderHolder.addChild(coSlider);

		h2Slider = new LabeledDoubleSlider(250, 0.01, 1, .00009, 5, 0) {
			@Override
			public void update() {
				h2Slider.getLabel().setText(SigFig.sigfigalize(getValue(), h2Slider.getSigFigs()) + " Moles H2");
			}
		};
		h2Slider.getLabel().setWidth(h2Slider.getLabel().getWidth() + 100);
		h2Slider.setOffsetY(10);
		sliderHolder.addChild(h2Slider);

		sliderHolder.setOffsetY(300);
		sliderHolder.setOffsetX(-250);

		addComponent(sliderHolder);

		hGrad = new HorizontalGraduation(0, 1, 250, 50);
		vGrad = new VerticalGraduation(0, 2.5, .5, .25);

		graph = new Graph(1000, 500, "Moles of Substances", "time (s)", "moles", hGrad, vGrad);
		graph.setOffsetX(100);
		vGrad.setTextOffset(-40);
		addComponent(graph);

		cSlider.setValue(1.0);
		coSlider.setValue(1.0);
		h2Slider.setValue(1.0);
		h2oSlider.setValue(1.0);

		cMolesEquilibrium = cSlider.getValue();
		h2oMolesEquilibrium = h2oSlider.getValue();
		coMolesEquilibrium = coSlider.getValue();
		h2MolesEquilibrium = h2Slider.getValue();

		EmptyComponent labelHolder = new EmptyComponent(800, 200);
		Label titleLabel = new Label(800, 100, "Measured Amounts of Material in the Glass Bulb");
		titleLabel.setFontSize(14f);
		labelHolder.addChild(titleLabel);
		cLabel = new Label(200, 100, "C: " + cMolesEquilibrium + " moles");
		cLabel.setOffsetY(-25);
		labelHolder.addChild(cLabel);
		h2oLabel = new Label(200, 100, "H2O: " + h2oMolesEquilibrium + " moles");
		h2oLabel.setOffsetY(-25);
		labelHolder.addChild(h2oLabel);
		coLabel = new Label(200, 100, "CO: " + coMolesEquilibrium + " moles");
		coLabel.setOffsetX(-400);
		labelHolder.addChild(coLabel);
		h2Label = new Label(200, 100, "H2: " + h2MolesEquilibrium + " moles");
		labelHolder.addChild(h2Label);

		labelHolder.setOffsetX(-1450);
		labelHolder.setOffsetY(450);
		addComponent(labelHolder);

		cDataSet = new GraphDataSet("              Moles C", true, true);
		h2oDataSet = new GraphDataSet("Moles H2O", true, true);
		coDataSet = new GraphDataSet("           Moles CO", true, true);
		h2DataSet = new GraphDataSet("Moles H2", true, true);

		cDataSet.addPoint(0, 0);
		h2oDataSet.addPoint(0, 0);
		coDataSet.addPoint(0, 0);
		h2DataSet.addPoint(0, 0);

		graph.gethGraduation().setLineIntervals(TIME_INCREMENT * 250);
		graph.gethGraduation().setSubLineIntervals(TIME_INCREMENT * 50);

		graph.addDataSet(cDataSet, h2oDataSet, coDataSet, h2DataSet);

		resetButton = new Button(400, 100, "Reset Simulation") {

			@Override
			public void doSomething() {
				
				globalTime = 0;
				pointIndex = 0;
				
				cDataSet.clearPoints();
				h2oDataSet.clearPoints();
				coDataSet.clearPoints();
				h2DataSet.clearPoints();
				
				cDataSet.addPoint(0, 0);
				h2oDataSet.addPoint(0, 0);
				coDataSet.addPoint(0, 0);
				h2DataSet.addPoint(0, 0);
				
				cSlider.setValue(1.0);
				coSlider.setValue(1.0);
				h2Slider.setValue(1.0);
				h2oSlider.setValue(1.0);
			}
		};

		resetButton.setOffset(-800, 600);
		
		graph.gethGraduation().setEnd(1);
		
		addComponent(resetButton);

		start(60);

	}

	public double calculatePressure(double currentMoles) {
		double R = 8.314;
		double temperature = 1000;
		double volume = 10;
		return (currentMoles * R * temperature) / volume;
	}

	private static double lerp(double v1, double v2, float f) {
		return ((v2 - v1) * f + v1);
	}

	private static double[] findZeros(double a, double b, double c) {
		double s = Math.sqrt((b * b) - 4 * a * c);

		double[] zeros = new double[2];
		zeros[0] = (-b + s) / (2 * a);
		zeros[1] = (-b - s) / (2 * a);

		return zeros;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

		double zero = findZeros(EQUILIBRIUM_CONSTANT - 1,
				(EQUILIBRIUM_CONSTANT * h2oSlider.getValue()) + (EQUILIBRIUM_CONSTANT * cSlider.getValue())
						+ coSlider.getValue() + h2Slider.getValue(),
				(EQUILIBRIUM_CONSTANT * cSlider.getValue() * h2oSlider.getValue())
						- (coSlider.getValue() * h2Slider.getValue()))[0];

		cMolesEquilibrium = cSlider.getValue() + zero;
		h2oMolesEquilibrium = h2oSlider.getValue() + zero;
		coMolesEquilibrium = coSlider.getValue() - zero;
		h2MolesEquilibrium = h2Slider.getValue() - zero;

		cLabel.setText("C: " + (float) cMolesEquilibrium + " moles");
		h2oLabel.setText("H2O: " + (float) h2oMolesEquilibrium + " moles");
		coLabel.setText("CO: " + (float) coMolesEquilibrium + " moles");
		h2Label.setText("H2: " + (float) h2MolesEquilibrium + " moles");

		pointIndex++;
		globalTime += TIME_INCREMENT;

		cDataSet.addPoint(globalTime, lerp(cDataSet.getPoints().get(pointIndex - 1).getY(), cMolesEquilibrium, .04f));
		h2oDataSet.addPoint(globalTime,
				lerp(h2oDataSet.getPoints().get(pointIndex - 1).getY(), h2oMolesEquilibrium, .04f));
		coDataSet.addPoint(globalTime,
				lerp(coDataSet.getPoints().get(pointIndex - 1).getY(), coMolesEquilibrium, .04f));
		h2DataSet.addPoint(globalTime,
				lerp(h2DataSet.getPoints().get(pointIndex - 1).getY(), h2MolesEquilibrium, .04f));

		graph.gethGraduation().setEnd((int) ((globalTime/ graph.gethGraduation().getSubLineIntervals()) + 1)
				* graph.gethGraduation().getSubLineIntervals());

		bulb.setContentState(ContentState.SOLID);
		bulb.setContentColor(Color.black);;
		bulb.setValue(50*((cDataSet.getPoints().get(cDataSet.size()-1).getY())));
		

	}

}
