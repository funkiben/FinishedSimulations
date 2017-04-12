package isolation_method;

import java.awt.Color;

import lab.util.animation.DoubleLinearAnimation;
import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.LabComponent;
import lab.component.geo.Rectangle;
import lab.component.swing.Label;
import lab.component.swing.ScrollLabel;
import lab.component.swing.input.Button;
import lab.component.swing.input.CheckBox;
import lab.component.swing.input.field.DoubleField;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.Vector2;
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
	private final CheckBox zeroOrderCheckBox, firstOrderCheckBox, secondOrderCheckBox;

	private final LineOfBestFitGraph zeroOrderGraph, firstOrderGraph, secondOrderGraph, O2OrderGraph;
	
	private final CoordinateList lnKvslnO2List;
	
	private final Label volumeLabel;
	
	private final Button showInstructionsButton;
	private final LabFrame instructionsFrame;
	
	private double time = 0;
	private double O2Molarity = 0;
	private double NOMolarity = 0;
	private double initialNOMolarity = 0;
	private double totalVolume = 2.0;
	private boolean reactionOccuring = false;
	private boolean stopped = false;
	
	
	public IsolationMethod() {
		super("Isolation Method", 750, 650);
		
		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(15, 30);
		addComponent(reactionApparatus);
		
		
		// create control area for starting and stopping simulation, and changing NO and O2 mL
		Rectangle controlArea = new Rectangle(190, 140);
		controlArea.setFillColor(new Color(245, 245, 245));
		controlArea.setStrokeColor(Color.lightGray);
		
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

		
		
		
		
		
		
		// create NO order graph area
		Rectangle graphArea = new Rectangle(510, 280);
		graphArea.setFillColor(new Color(245, 245, 245));
		graphArea.setStrokeColor(Color.lightGray);
		graphArea.setOffset(35, 5);
		graphArea.setLayout(LabComponent.FREE_FORM);
		
		Rectangle checkBoxes = new Rectangle(120, 80);
		checkBoxes.setFill(false);
		checkBoxes.setStrokeColor(Color.lightGray);
		checkBoxes.setOffset(10, 10);
		
		zeroOrderCheckBox = new CheckBox(110, 25, "[NO] vs. t") {
			@Override
			public void onSelect() {
				zeroOrderCheckBox.setEnabled(false);
				firstOrderCheckBox.setEnabled(true);
				secondOrderCheckBox.setEnabled(true);
				
				firstOrderCheckBox.setSelected(false);
				secondOrderCheckBox.setSelected(false);
				
				zeroOrderGraph.setVisible(true);
				firstOrderGraph.setVisible(false);
				secondOrderGraph.setVisible(false);
			}
		};
		firstOrderCheckBox = new CheckBox(110, 25, "ln([NO]) vs. t") {
			@Override
			public void onSelect() {
				zeroOrderCheckBox.setEnabled(true);
				firstOrderCheckBox.setEnabled(false);
				secondOrderCheckBox.setEnabled(true);

				zeroOrderCheckBox.setSelected(false);
				secondOrderCheckBox.setSelected(false);
				
				zeroOrderGraph.setVisible(false);
				firstOrderGraph.setVisible(true);
				secondOrderGraph.setVisible(false);
			}
		};
		secondOrderCheckBox = new CheckBox(110, 25, "1/[NO] vs. t") {
			@Override
			public void onSelect() {
				zeroOrderCheckBox.setEnabled(true);
				firstOrderCheckBox.setEnabled(true);
				secondOrderCheckBox.setEnabled(false);
				
				zeroOrderCheckBox.setSelected(false);
				firstOrderCheckBox.setSelected(false);
				
				zeroOrderGraph.setVisible(false);
				firstOrderGraph.setVisible(false);
				secondOrderGraph.setVisible(true);
			}
		};
		
		zeroOrderCheckBox.setOffset(2, 2);
		firstOrderCheckBox.setOffsetX(2);
		secondOrderCheckBox.setOffsetX(2);
		
		checkBoxes.addChild(zeroOrderCheckBox, firstOrderCheckBox, secondOrderCheckBox);
		
		graphArea.addChild(checkBoxes);
		
		
		
		
		
		
		
		
		
		HorizontalGraduation hg;
		VerticalGraduation vg;

		
		// create zero order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(0, 200E-6, 50E-6, 25E-6);

		zeroOrderGraph = new LineOfBestFitGraph(280, 230, "[NO] vs. t", "t (s)", "[NO] mol/L", hg, vg);
		zeroOrderGraph.setOffsetX(160);
		zeroOrderGraph.getSlopeLabel().setOffset(-180, 200);
		zeroOrderGraph.getInterceptLabel().setOffset(-280, 220);
		
		vg.setTextOffset(-45);
		
		graphArea.addChild(zeroOrderGraph);

		
		
		
		// create first order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(-12, -8, 2, 1);

		firstOrderGraph = new LineOfBestFitGraph(280, 230, "ln([NO]) vs. t", "t (s)", "ln([NO]) mol/L", hg, vg);
		firstOrderGraph.getGraph().setYLabelOffset(20);
		firstOrderGraph.setOffsetX(160);
		firstOrderGraph.setVisible(false);
		firstOrderGraph.getSlopeLabel().setOffset(-180, 200);
		firstOrderGraph.getInterceptLabel().setOffset(-280, 220);
		
		vg.setTextOffset(-37);

		graphArea.addChild(firstOrderGraph);


		
		
		
		// create second order graph
		hg = new HorizontalGraduation(0, 60E3, 10E3, 5E3);
		vg = new VerticalGraduation(0, 50E3, 50E3, 10E3);
			
		secondOrderGraph = new LineOfBestFitGraph(280, 230, "1/[NO] vs. t", "t (s)", "1/[NO] mol/L", hg, vg);
		secondOrderGraph.setOffsetX(160);
		secondOrderGraph.setVisible(false);
		secondOrderGraph.getSlopeLabel().setOffset(-180, 200);
		secondOrderGraph.getInterceptLabel().setOffset(-280, 220);
		
		vg.setTextOffset(-35);
		vg.setSigfigs(2);
		
		graphArea.addChild(secondOrderGraph);
		
		
		addComponent(graphArea);
		
		
		
		addComponent(new EmptyComponent(300, 0));
		
		Rectangle border = new Rectangle(510, 350);
		border.setFillColor(new Color(245, 245, 245));
		border.setStrokeColor(Color.lightGray);
		border.setOffset(-80, -140);
		
	
		// create list for plotting ln(k) vs ln[O2]
		lnKvslnO2List = new CoordinateList(150, 200, "O2 Molarity", "Pseudo k", "ln([O2])=%x% mol/L, ln(k)=%y%") {
			
			@Override
			public void onAddValue(Vector2 v) {
				v.setX(Math.log(v.getX()));
				v.setY(Math.log(v.getY()));
				
				O2OrderGraph.getData().addPoint(v);
				
				O2OrderGraph.getGraph().gethGraduation().setSubLineIntervals(O2OrderGraph.getGraph().getMaxXSubTicks(5.0));
				O2OrderGraph.getGraph().getvGraduation().setSubLineIntervals(O2OrderGraph.getGraph().getMaxYSubTicks(5.0));
				
				double start = (int) ((v.getX() - 1) / O2OrderGraph.getGraph().gethGraduation().getSubLineIntervals()) * O2OrderGraph.getGraph().gethGraduation().getSubLineIntervals();
				double end = (int) ((v.getY() + 1) / O2OrderGraph.getGraph().getvGraduation().getSubLineIntervals()) * O2OrderGraph.getGraph().getvGraduation().getSubLineIntervals();
				
				O2OrderGraph.getGraph().gethGraduation().setStart(Math.min(start, O2OrderGraph.getGraph().gethGraduation().getStart()));
				O2OrderGraph.getGraph().getvGraduation().setEnd(Math.max(end, O2OrderGraph.getGraph().getvGraduation().getEnd()));
				
				
				O2OrderGraph.plotLineOfBestFit();
				
			}
			
			@Override
			public void onRemoveValue(Vector2 v) {
				O2OrderGraph.getData().removePoint(v);
				O2OrderGraph.plotLineOfBestFit();

			}
			
		};
		
		lnKvslnO2List.setOffset(10, 10);
		
		border.addChild(lnKvslnO2List);
		
		
		// create graph for finding order of O2
		hg = new HorizontalGraduation(-6, 0, 1, 0.5);
		vg = new VerticalGraduation(0, 1, 1, 0.5);
			
		hg.setSigfigs(-1);
		vg.setSigfigs(-1);
		
		O2OrderGraph = new LineOfBestFitGraph(280, 290, "ln(k) vs. ln[O2]", "ln([O2])", "ln(k)", hg, vg);
		O2OrderGraph.getGraph().setYLabelOffset(5);
		O2OrderGraph.getSlopeLabel().setOffset(-180, 260);
		O2OrderGraph.getInterceptLabel().setOffset(-280, 280);
		
		vg.setTextOffset(-33);
		hg.setTextOffset(0);
		hg.setShowLabels(true);
		
		
		border.addChild(O2OrderGraph);
				
		addComponent(border);
		
		zeroOrderCheckBox.setSelected(true);
		
		
		showInstructionsButton = new Button(140, 30, "Show Instructions") {
			@Override
			public void doSomething() {
				instructionsFrame.setVisible(true);
			}
		};
		
		showInstructionsButton.setOffset(20, 550);
		
		reactionApparatus.addChild(showInstructionsButton);
		
		instructionsFrame = new LabFrame("Instructions", 700, 500, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		instructionsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		instructionsFrame.setResizable(false);

		ScrollLabel instructions = new ScrollLabel(700, 500, "/isolation_method/instructions.txt");
		instructions.setHoriztonalScrollBarPolicy(ScrollLabel.HORIZONTAL_SCROLLBAR_NEVER);
		instructions.setFontSize(13);
		
		instructionsFrame.addComponent(instructions);
		instructionsFrame.start(0);
		
		
		
		
		start(30);

	}

	private void start() {
		
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		
		if (stopped) {
			reactionOccuring = true;
			return;
		} else {
			reset();
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
		
		zeroOrderCheckBox.setSelected(true);
		
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
				
				O2Molarity -= 0.5 * NOMolarity;
				
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
			
			startButton.setEnabled(true);
		} else {
			startButton.setEnabled(false);
		}

	}

}
