package isolation_method;

import java.awt.Color;

import lab.util.animation.DoubleLinearAnimation;
import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.geo.Rectangle;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.field.DoubleField;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

public class IsolationMethod extends LabFrame {

	private static final long serialVersionUID = -6825206580344099210L;

	public static void main(String[] args) {
		new IsolationMethod();
	}

	private final double NOConcentration = 4.09E-4;
	private final double O2Concentration = 0.0409;

	private final ReactionApparatus reactionApparatus;
	private final Button start, stop, reset;
	private final DoubleField O2Amount, NOAmount;

	private final Graph zeroOrderGraph, firstOrderGraph, secondOrderGraph;
	private final GraphDataSet zeroOrderData, firstOrderData, secondOrderData;

	private final Label volumeLabel;

	private int time = 0;
	private double NO2Moles = 0;

	public IsolationMethod() {
		super("Isolation Method", 1000, 650);

		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(15, 30);
		addComponent(reactionApparatus);

		Rectangle controlArea = new Rectangle(190, 140);
		controlArea.setFill(false);
		controlArea.setStrokeColor(Color.LIGHT_GRAY);

		Label O2MLabel, NOMLabel;

		O2MLabel = new Label(190, 20, "O2 Molarity: " + O2Concentration + "mol/L");
		O2MLabel.setOffsetX(5);

		NOMLabel = new Label(190, 20, "NO Molarity: " + SigFig.sigfigalize(NOConcentration, 3) + "mol/L");
		NOMLabel.setOffsetX(5);

		volumeLabel = new Label(190, 20, "Total Volume: 2mL");
		volumeLabel.setOffsetX(5);

		controlArea.addChild(O2MLabel, NOMLabel, volumeLabel);

		start = new Button(55, 20, "Start") {
			@Override
			public void doSomething() {
				start();
			}
		};
		start.setOffset(5, 5);

		stop = new Button(55, 20, "Stop") {
			@Override
			public void doSomething() {
				stop();
			}
		};
		stop.setOffset(5, 5);

		reset = new Button(55, 20, "Reset") {
			@Override
			public void doSomething() {
				reset();
			}
		};
		reset.setOffset(5, 5);

		O2Amount = new DoubleField(100, 1.0, 10.0, 3);
		NOAmount = new DoubleField(100, 1.0, 10.0, 3);

		controlArea.addChild(new EmptyComponent(5, 0), new Label(60, 20, "O2 (mL): "), O2Amount,
				new EmptyComponent(10000, 0), new EmptyComponent(5, 0), new Label(60, 20, "NO (mL): "), NOAmount,
				new EmptyComponent(1000, 5), start, stop, reset);

		controlArea.setOffset(-10, 380);

		reactionApparatus.addChild(controlArea);

		HorizontalGraduation hg;
		VerticalGraduation vg;

		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);

		hg.setShowLabels(false);

		zeroOrderGraph = new Graph(200, 200, "[NO2] vs. t", "t (s)", "[NO2] mol/L", hg, vg);
		zeroOrderGraph.setOffsetX(60);
		zeroOrderGraph.setYLabelOffset(15);
		vg.setTextOffset(-26);
		vg.setRemovePointZero(false);
		addComponent(zeroOrderGraph);

		zeroOrderData = new GraphDataSet("NO2", false, false);
		zeroOrderGraph.addDataSet(zeroOrderData);

		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);

		hg.setShowLabels(false);

		firstOrderGraph = new Graph(200, 200, "ln[NO2] vs. t", "t (s)", "ln([NO2]) mol/L", hg, vg);
		firstOrderGraph.setOffsetX(60);
		firstOrderGraph.setYLabelOffset(20);
		vg.setTextOffset(-26);
		vg.setRemovePointZero(false);
		addComponent(firstOrderGraph);

		firstOrderData = new GraphDataSet("NO2", false, false);
		firstOrderGraph.addDataSet(firstOrderData);

		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);

		hg.setShowLabels(false);

		secondOrderGraph = new Graph(200, 200, "1/[NO2] vs. t", "t (s)", "1/[NO2] mol/L", hg, vg);
		secondOrderGraph.setOffsetX(60);
		secondOrderGraph.setYLabelOffset(15);
		vg.setTextOffset(-26);
		vg.setRemovePointZero(false);
		addComponent(secondOrderGraph);

		secondOrderData = new GraphDataSet("NO2", false, false);
		secondOrderGraph.addDataSet(secondOrderData);

		start(30);

	}

	private void reset() {
		time = 0;
		NO2Moles = 0;
		
		reactionApparatus.getO2Piston().setValue(10);
		reactionApparatus.getNOPiston().setValue(10);
	}

	private void start() {

		getAnimator().addAnimation("NOPiston", new DoubleLinearAnimation(10 - NOAmount.getValue(), 0.1) {
			@Override
			public Double getValue() {
				return reactionApparatus.getNOPiston().getValue();
			}

			public void setValue(Double v) {
				reactionApparatus.getNOPiston().setValue(v);
			}
		});

		getAnimator().addAnimation("O2Piston", new DoubleLinearAnimation(10 - O2Amount.getValue(), 0.1) {
			@Override
			public Double getValue() {
				return reactionApparatus.getO2Piston().getValue();
			}

			public void setValue(Double v) {
				reactionApparatus.getO2Piston().setValue(v);
			}
		});

	}

	private void stop() {
		reactionApparatus.getO2Piston().setValue(10);
		reactionApparatus.getNOPiston().setValue(10);
	}

	private void step() {
		time++;

	}

	@Override
	public void update() {

		if (O2Amount.hasInput() && NOAmount.hasInput()) {
			volumeLabel.setText("Total Volume: " + SigFig.sigfigalize(NOAmount.getValue() + O2Amount.getValue(), 3) + "mL");
		}

	}

}
