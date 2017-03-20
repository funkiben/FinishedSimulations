package HI_equilibrium;

import java.awt.Color;

import draw.animation.ColorLinearAnimation;
import draw.animation.DoubleLinearAnimation;
import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.LabComponent;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.sensor.PressureGauge;
import lab.component.sensor.Thermometer;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.Dropdown;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

public class HIEquilibrium extends LabFrame{
	
	public static void main(String[] args) {
		new HIEquilibrium();
	}
	
	
	private static final HIReactionState state298K = new HIReactionState("298K",
			10, // volume
			225, // bulb size
			298, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.1825) * 2, // final hi
			0.1825, // final h2
			0.1825, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state500K = new HIReactionState("500K",
			10, // volume
			225, // bulb size
			500, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.3637) * 2, // final hi
			0.3637, // final h2
			0.3637, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state700K = new HIReactionState("700K",
			10, // volume
			225, // bulb size
			700, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.4765) * 2, // final hi
			0.4765, // final h2
			0.4765, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state1000K = new HIReactionState("1000K",
			10, // volume
			225, // bulb size
			1000, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.5773) * 2, // final hi
			0.5773, // final h2
			0.5773, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	
	
	
	private static final HIReactionState state5L = new HIReactionState("5.00L",
			5, // volume
			175, // bulb size
			700, // temperature
			0.0, // initial hi
			4.9, // initial h2
			4.9, // initial i2
			(4.9 - 0.9531) * 2, // final hi
			0.9531, // final h2
			0.9531, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state10L = new HIReactionState("10.00L",
			10, // volume
			225, // bulb size
			700, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.4765) * 2, // final hi
			0.4765, // final h2
			0.4765, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state20L = new HIReactionState("20.00L",
			20, // volume
			275, // bulb size
			700, // temperature
			0.0, // initial hi
			1.225, // initial h2
			1.225, // initial i2
			(1.225 - 0.2383) * 2, // final hi
			0.2383, // final h2
			0.2383, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	
	
	private static final HIReactionState stateH2I2Reactant = new HIReactionState("H2 & I2",
			10, // volume
			225, // bulb size
			700, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			(2.45 - 0.4765) * 2, // final hi
			0.4765, // final h2
			0.4765, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState stateHIReactant = new HIReactionState("HI",
			10, // volume
			225, // bulb size
			700, // temperature
			3.5, // initial hi
			0.0, // initial h2
			0.0, // initial i2
			2.819, // final hi
			(3.5 - 2.819) / 2, // final h2
			(3.5 - 2.819) / 2, // final i2
			true, // hi tube
			false, // h2 tube
			false // i2 tube
	);
	
	

	private static final long serialVersionUID = 7321300459079955237L;
	
	private final ReactionApparatus reactionApparatus;
	private final Thermometer thermometer;
	private final Dropdown<String> variableSelector;
	private final Dropdown<HIReactionState> temperatureSelector, volumeSelector, reactantSelector;
	private final PressureGauge H2PressureReader, I2PressureReader, HIPressureReader;
	private final Label volumeLabel;
	private final Button startButton, stopButton, resetButton;
	private final Graph graph;
	private final GraphDataSet H2I2DataSet, HIDataSet;
	private double H2Pressure, I2Pressure, HIPressure;
	private boolean reactionInProgress = false;
	private double time = 0;
	private HIReactionState currentState = state298K;
	
	public HIEquilibrium() {
		super("Homogeneous Equilibrium: H2 + I2 <-> 2HI", 700, 750);
		
		
		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(10, 10);
		addComponent(reactionApparatus);
		
		
		volumeLabel = new Label(150, 30, "");
		volumeLabel.setOffset(180, 275);
		
		reactionApparatus.addChild(volumeLabel);
		
		
		
		
		
		variableSelector = new Dropdown<String>(150, 25, "Temperature", "Volume", "Reactant") {
			
			@Override
			public void onSelectItem(String s) {
				
				if (s.equals("Temperature")) {
					temperatureSelector.setVisible(true);
					volumeSelector.setVisible(false);
					reactantSelector.setVisible(false);
					
					changeState(temperatureSelector.getValue(), true);
				} else if (s.equals("Volume")) {
					volumeSelector.setVisible(true);
					temperatureSelector.setVisible(false);
					reactantSelector.setVisible(false);
					
					changeState(volumeSelector.getValue(), true);
				} else {
					reactantSelector.setVisible(true);
					volumeSelector.setVisible(false);
					temperatureSelector.setVisible(false);
					
					changeState(reactantSelector.getValue(), true);
				}
				
			}
			
		};
		
		temperatureSelector = new Dropdown<HIReactionState>(100, 25, state298K, state500K, state700K, state1000K) {
			@Override
			public void onSelectItem(HIReactionState s) {
				changeState(s, false);
			}
		};
		
		volumeSelector = new Dropdown<HIReactionState>(100, 25, state5L, state10L, state20L) {
			@Override
			public void onSelectItem(HIReactionState s) {
				changeState(s, true);
			}
		};
		
		reactantSelector = new Dropdown<HIReactionState>(100, 25, stateH2I2Reactant, stateHIReactant) {
			@Override
			public void onSelectItem(HIReactionState s) {
				changeState(s, true);
			}
		};
		
		
		
		
		LabComponent container = new EmptyComponent(450, 25);
		container.setLayout(LabComponent.FREE_FORM);
		container.setOffset(90, 335);
		
		variableSelector.setOffset(60, 0);
		
		temperatureSelector.setOffset(215, 0);
		volumeSelector.setOffset(215, 0);
		reactantSelector.setOffset(215, 0);
		
		container.addChild(new Label(75, 25, "Variable: "), variableSelector, temperatureSelector, volumeSelector, reactantSelector);
		
		
		reactionApparatus.addChild(container);
		
		
		
		
		
		
		
		
		container = new EmptyComponent(300, 50);
		container.setShowBounds(true);
		container.setOffset(90, 370);
		
		startButton = new Button(100, 50, "START") {
			@Override
			public void doSomething() {
				start();
			}
		};
		
		stopButton = new Button(100, 50, "STOP") {
			@Override
			public void doSomething() {
				stop();
			}
		};
		stopButton.setEnabled(false);
		
		resetButton = new Button(100, 50, "RESET") {
			@Override
			public void doSomething() {
				reset();
			}
		};
		resetButton.setEnabled(false);
		
		container.addChild(startButton, stopButton, resetButton);
	
		reactionApparatus.addChild(container);
		
		
		
		
		
		
		
		
		container = new EmptyComponent(150, 340);
		
		H2PressureReader = new PressureGauge(150, 150, "Pressure H2", "atm", 4);
		I2PressureReader = new PressureGauge(150, 150, "Pressure I2", "atm", 4);
		HIPressureReader = new PressureGauge(150, 150, "Pressure HI", "atm", 4);
		
		H2PressureReader.getTitleLabel().setFontSize(10);
		H2PressureReader.getGaugeLabel().setFontSize(12);
		
		H2PressureReader.getTitleLabel().setFontSize(10);
		H2PressureReader.getGaugeLabel().setFontSize(12);
		
		I2PressureReader.getTitleLabel().setFontSize(10);
		I2PressureReader.getGaugeLabel().setFontSize(12);
		
		HIPressureReader.getTitleLabel().setFontSize(10);
		HIPressureReader.getGaugeLabel().setFontSize(12);
		
		
		
		H2PressureReader.setOffset(0, 20);
		I2PressureReader.setOffset(0, 190);
		HIPressureReader.setOffset(0, 95);
		
		container.setLayout(LabComponent.FREE_FORM);
		container.setOffsetX(20);
		container.addChild(H2PressureReader, I2PressureReader, HIPressureReader);
		
		addComponent(container);
		
		
		
		
		
		
		
		thermometer = new Thermometer(400);
		thermometer.setOffset(40, 10);
		thermometer.setGraduation(new VerticalGraduation(200, 1000, 100, 10));
		
		addComponent(thermometer);
		
		
		
		VerticalGraduation vg = new VerticalGraduation(0, 1, 2, 0.5);
		HorizontalGraduation hg = new HorizontalGraduation(0, 60, 60, 15);

		vg.setRemovePointZero(false);
		
		graph = new Graph(600, 250, "Pressures", "time (s)", "pressure (atm)", vg, hg);
		
		graph.setYLabelOffset(30);
		graph.getvGraduation().setTextOffset(-30);
		graph.setOffset(40, 30);
		
		graph.setDrawYLines(false);
		
		addComponent(graph);
		
		H2I2DataSet = new GraphDataSet("H2=I2", true, true);
		H2I2DataSet.setColor(Color.red);
		
		HIDataSet = new GraphDataSet("HI", true, true);
		HIDataSet.setColor(Color.blue);
		
		graph.addDataSet(H2I2DataSet);
		graph.addDataSet(HIDataSet);
		
		
		changeState(currentState, true);
		
		
		start(30);
	}
	
	private double Qc() {
		return (HIPressure * HIPressure) / (H2Pressure * I2Pressure);
	}
	
	private void changeState(HIReactionState state, boolean reset) {
		currentState = state;
		
		thermometer.setValue(state.temperature);
		
		if (reset || HIDataSet.size() == 0) {
			H2Pressure = state.H2PressureInitial;
			I2Pressure = state.I2PressureInitial;
			HIPressure = state.HIPressureInitial;
			
			H2PressureReader.setValue(H2Pressure);
			I2PressureReader.setValue(I2Pressure);
			HIPressureReader.setValue(HIPressure);
			
			H2I2DataSet.clearPoints();
			HIDataSet.clearPoints();
			
			time = 0;
			
			graph.getvGraduation().setEnd(1);
			
			graph.gethGraduation().setStart(0);
			graph.gethGraduation().setEnd(60);
			
			reactionApparatus.getReactionBulb().setValue(0);
			
			reactionApparatus.getI2Bulb().setValue(50);
			reactionApparatus.getI2Tube().setColor(ReactionApparatus.I2_COLOR);
			
			stop();
		}
		
		H2PressureReader.setVisible(state.H2Tube);
		I2PressureReader.setVisible(state.I2Tube);
		HIPressureReader.setVisible(state.HITube);
		
		reactionApparatus.getH2Tube().setVisible(state.H2Tube);
		reactionApparatus.getI2Tube().setVisible(state.I2Tube);
		reactionApparatus.getHITube().setVisible(state.HITube);
		
		reactionApparatus.setReactionBulbSize(state.bulbSize);
		
		volumeLabel.setText("Bulb Volume: " + SigFig.sigfigalize(state.volume, 3) + "L");
		volumeLabel.setOffsetY(165 + state.bulbSize / 2);
		
		
	}
	
	private void start() {
		reactionInProgress = true;
		
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		resetButton.setEnabled(true);
		
		if (currentState.I2Tube) {
			getAnimator().addAnimation("I2 Tube", new ColorLinearAnimation(new Color(230, 230, 230, 255), 5) {
				@Override
				public void setValue(Color color) {
					reactionApparatus.getI2Tube().setColor(color);
				}
				
				@Override
				public Color getValue() {
					return reactionApparatus.getI2Tube().getColor();
				}
			});
			
			getAnimator().addAnimation("I2 Bulb", new DoubleLinearAnimation(0.0, 5) {
				@Override
				public void setValue(Double v) {
					reactionApparatus.getI2Bulb().setValue(v);
				}
				
				@Override
				public Double getValue() {
					return reactionApparatus.getI2Bulb().getValue();
				}
			});
		}
		
	}
	
	private void stop() {
		reactionInProgress = false;
		
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		
		getAnimator().cancelAnimation("I2 Tube");
		getAnimator().cancelAnimation("I2 Bulb");
	}
	
	public void reset() {
		changeState(currentState, true);
		
		resetButton.setEnabled(false);
	}
	
	private static double lerp(double v1, double v2, float f) {
		return ((v2 - v1) * f + v1);
	}

	private int getMinYAxisEnd() {
		int e = (int) Math.ceil(Math.max(HIPressure, Math.max(H2Pressure, I2Pressure)));
		return e;
	}
	
	
	@Override
	public void update() {
		
		if (reactionInProgress) {
			
			//System.out.println(Qc());
			
			time += 0.1;
			
			H2Pressure = lerp(H2Pressure, currentState.H2PressureFinal, 0.03f);
			I2Pressure = lerp(I2Pressure, currentState.I2PressureFinal, 0.03f);
			HIPressure = lerp(HIPressure, currentState.HIPressureFinal, 0.03f);
			
			H2I2DataSet.addPoint(time, I2Pressure);
			HIDataSet.addPoint(time, HIPressure);
		
			
			graph.getvGraduation().setEnd(Math.max(graph.getvGraduation().getEnd(), getMinYAxisEnd()));
			
			if ((int) time % 60 == 59) {
				graph.gethGraduation().setStart((int) time + 1);
				graph.gethGraduation().setEnd((int) time + 61);

				graph.getvGraduation().setEnd(getMinYAxisEnd());
			}
			
			
			double m = graph.getMaxYSubTicks(10);
			
			graph.getvGraduation().setSubLineIntervals(m);
			graph.getvGraduation().setLineIntervals(m < 1 ? 1 : 2);
			
			H2PressureReader.setValue(H2Pressure);
			I2PressureReader.setValue(I2Pressure);
			HIPressureReader.setValue(HIPressure);
			
			reactionApparatus.getReactionBulb().setValue(I2Pressure / 2.0 * 100);
			
		}
		
		
		
	}
	

}
