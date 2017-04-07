package isolation_method;

import java.awt.Color;

import lab.util.animation.DoubleLinearAnimation;
import lab.LabFrame;
import lab.component.EmptyComponent;
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
	
	private final double NOPistonMolarity = 4.09E-4;
	private final double O2PistonMolarity = 0.0409;
	private final double rateConstant = 201.95;
	private final double error = 1E-3;
	
	
	private final ReactionApparatus reactionApparatus;
	private final Button startButton, stopButton, resetButton;
	private final DoubleField O2AmountField, NOAmountField;

	private final ReactionOrderGraph zeroOrderGraph, firstOrderGraph, secondOrderGraph;
	
	private final Label volumeLabel;
	
	
	private double time = 0;
	private double O2Molarity = 0;
	private double NOMolarity = 0;
	private double initialNOMolarity = 0;
	private double totalVolume = 2.0;
	private boolean reactionOccuring = false;
	private boolean stopped = false;
	
	
	public IsolationMethod() {
		super("Isolation Method", 1000, 650);
		
		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(15, 30);
		addComponent(reactionApparatus);

		
		
		
		
		
		// create control area for starting and stopping simulation, and changing NO and O2 mL
		Rectangle controlArea = new Rectangle(190, 140);
		controlArea.setFill(false);
		controlArea.setStrokeColor(Color.LIGHT_GRAY);

		Label O2MLabel, NOMLabel;

		O2MLabel = new Label(190, 20, "O2 Molarity: " + O2PistonMolarity + "mol/L");
		O2MLabel.setOffsetX(5);

		NOMLabel = new Label(190, 20, "NO Molarity: " + SigFig.sigfigalize(O2PistonMolarity, 3) + "mol/L");
		NOMLabel.setOffsetX(5);

		volumeLabel = new Label(190, 20, "Total Volume: 2mL");
		volumeLabel.setOffsetX(5);

		controlArea.addChild(O2MLabel, NOMLabel, volumeLabel);

		startButton = new Button(55, 20, "Start") {
			@Override
			public void doSomething() {
				start();
			}
		};
		startButton.setOffset(5, 5);

		stopButton = new Button(55, 20, "Stop") {
			@Override
			public void doSomething() {
				stop();
			}
		};
		stopButton.setOffset(5, 5);
		stopButton.setEnabled(false);

		resetButton = new Button(55, 20, "Reset") {
			@Override
			public void doSomething() {
				reset();
			}
		};
		resetButton.setOffset(5, 5);

		O2AmountField = new DoubleField(100, 1.0, 10.0, 3);
		NOAmountField = new DoubleField(100, 1.0, 10.0, 3);

		controlArea.addChild(new EmptyComponent(5, 0), new Label(60, 20, "O2 (mL): "), O2AmountField,
				new EmptyComponent(10000, 0), new EmptyComponent(5, 0), new Label(60, 20, "NO (mL): "), NOAmountField,
				new EmptyComponent(1000, 5), startButton, stopButton, resetButton);

		controlArea.setOffset(-10, 380);

		reactionApparatus.addChild(controlArea);

		
		
		
		
		
		HorizontalGraduation hg;
		VerticalGraduation vg;

		
		// create zero order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(0, 200E-6, 50E-6, 25E-6);

		zeroOrderGraph = new ReactionOrderGraph("[NO] vs. t", "[NO] mol/L", hg, vg);
		
		vg.setTextOffset(-45);
		
		addComponent(zeroOrderGraph);

		
		
		
		// create first order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(-12, -8, 2, 1);

		firstOrderGraph = new ReactionOrderGraph("ln[NO] vs. t", "ln([NO]) mol/L", hg, vg);
		firstOrderGraph.getGraph().setYLabelOffset(20);
		
		vg.setTextOffset(-37);

		addComponent(firstOrderGraph);


		
		
		
		// create second order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(0, 50E3, 50E3, 10E3);
			
		vg.setSigfigs(2);
		
		secondOrderGraph = new ReactionOrderGraph("1/[NO] vs. t","1/[NO] mol/L", hg, vg);

		vg.setTextOffset(-35);

		addComponent(secondOrderGraph);
		
		
		
		start(30);

	}

	private void start() {
		
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		
		if (stopped) {
			reactionOccuring = true;
			return;
		}
		
		
		totalVolume = NOAmountField.getValue() + O2AmountField.getValue();
		
		NOMolarity = NOAmountField.getValue() * NOPistonMolarity / totalVolume;
		O2Molarity = O2AmountField.getValue() * O2PistonMolarity / totalVolume;
		
		initialNOMolarity = NOMolarity;
		
		getAnimator().addAnimation("NOPiston", new DoubleLinearAnimation(10 - NOAmountField.getValue(), 0.1) {
			@Override
			public Double getValue() {
				return reactionApparatus.getNOPiston().getValue();
			}

			public void setValue(Double v) {
				reactionApparatus.getNOPiston().setValue(v);
			}
		});

		getAnimator().addAnimation("O2Piston", new DoubleLinearAnimation(10 - O2AmountField.getValue(), 0.1) {
			@Override
			public Double getValue() {
				return reactionApparatus.getO2Piston().getValue();
			}

			public void setValue(Double v) {
				reactionApparatus.getO2Piston().setValue(v);
			}
		});
		
		getAnimator().addAnimation("ReactionVesselColor", new DoubleLinearAnimation(128.0, 0.5) {
			@Override
			public Double getValue() {
				return reactionApparatus.getTank().getValue();
			}

			public void setValue(Double v) {
				reactionApparatus.getTank().setValue(v);
			}
		});

		reactionOccuring = true;
		
		zeroOrderGraph.getGraph().getvGraduation().setEnd(((int) (NOMolarity / 25E-6) + 1) * 25E-6);
		firstOrderGraph.getGraph().getvGraduation().setStart(-12);
		secondOrderGraph.getGraph().getvGraduation().setEnd(50E3);
	}
	
	private void reset() {
		
		startButton.setEnabled(true);
		
		time = 0;
		O2Molarity = 0;
		NOMolarity = 0;
		
		reactionApparatus.getO2Piston().setValue(10);
		reactionApparatus.getNOPiston().setValue(10);
		
		reactionApparatus.getTank().setValue(0);
		
		zeroOrderGraph.clear();
		firstOrderGraph.clear();
		secondOrderGraph.clear();
		
		
		reactionOccuring = false;
		
		stopped = false;
		
		getAnimator().cancelAll();
	}
	
	private void stop() {
		reactionOccuring = false;
		stopped = true;
		
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		
	}

	private void step() {
		if (reactionOccuring) {
			time += 250;
			
			if (time % 1000 == 0) {
	
				// rate = k[NO]^2[O2]
				
				// calculate actual NO molarity using integrated rate law
				// 1/[NO] = 1/[NOi] + kt;
				NOMolarity = 1.0 / (1.0 / initialNOMolarity + rateConstant * O2Molarity * time);
				
				
				// put in some error that would be a result of using real spectrophotemetry
				NOMolarity += (Math.random() * 2 - 1) * error * (initialNOMolarity - NOMolarity);
				
				// plot the concentration
				zeroOrderGraph.getData().addPoint(time, NOMolarity);
				
				// make sure the graph y-axis can fit the newly plotted numbers
				if (NOMolarity > zeroOrderGraph.getGraph().getvGraduation().getEnd()) {
					zeroOrderGraph.getGraph().getvGraduation().setEnd(((int) (NOMolarity / 25E-6) + 1) * 25E-6);
				}
				
				
				
				// take natural log of NO molarity and plot
				double lnNOMolarity = Math.log(NOMolarity);
				firstOrderGraph.getData().addPoint(time, lnNOMolarity);
				
				// make sure the graph y-axis can fit the newly plotted numbers
				if (lnNOMolarity < firstOrderGraph.getGraph().getvGraduation().getStart()) {
					firstOrderGraph.getGraph().getvGraduation().setStart((int) (lnNOMolarity / 2 - 1) * 2);
				}
				
				
				
				// inverse NO molarity and plot
				double oneOverNOMolarity = 1.0 / NOMolarity;
				secondOrderGraph.getData().addPoint(time, oneOverNOMolarity);
				
				// make sure the graph y-axis can fit the newly plotted numbers
				if (oneOverNOMolarity > secondOrderGraph.getGraph().getvGraduation().getEnd()) {
					secondOrderGraph.getGraph().getvGraduation().setEnd(((int) (oneOverNOMolarity / 50E3) + 1) * 50E3);
				}
				
				
				
				// stop the reaction once time reaches end of graph
				if (time >= zeroOrderGraph.getGraph().gethGraduation().getEnd()) {
					reactionOccuring = false;
					
					
					zeroOrderGraph.plotLineOfBestFit();
					firstOrderGraph.plotLineOfBestFit();
					secondOrderGraph.plotLineOfBestFit();
					
				}
			}
			
		}
		
		
	}
	
	@Override
	public void update() {
		
		step();

		if (O2AmountField.hasInput() && NOAmountField.hasInput()) {
			volumeLabel.setText("Total Volume: " + SigFig.sigfigalize(NOAmountField.getValue() + O2AmountField.getValue(), 3) + "mL");
		} else {
			startButton.setEnabled(false);
		}

	}

}
