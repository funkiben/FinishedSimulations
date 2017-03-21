package HI_equilibrium;

import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.container.Bulb;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.sensor.PressureGauge;
import lab.component.sensor.Thermometer;
import lab.component.swing.input.Button;
import lab.component.swing.input.Dropdown;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;

public class HIEquilibrium extends LabFrame{
	
	public static void main(String[] args) {
		new HIEquilibrium();
	}
	
	
	private static final HIReactionState state298K = new HIReactionState(
			10, // volume
			298, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			2.45 - 0.1825, // final hi
			0.1825, // final h2
			0.1825, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state500K = new HIReactionState(
			10, // volume
			500, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			2.45 - 0.3637, // final hi
			0.3637, // final h2
			0.3637, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state700K = new HIReactionState(
			10, // volume
			700, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			2.45 - 0.4765, // final hi
			0.4765, // final h2
			0.4765, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state1000K = new HIReactionState(
			10, // volume
			1000, // temperature
			0.0, // initial hi
			2.45, // initial h2
			2.45, // initial i2
			2.45 - 0.5773, // final hi
			0.5773, // final h2
			0.5773, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	
	
	
	private static final HIReactionState state5L = new HIReactionState(
			5, // volume
			700, // temperature
			0.0, // initial hi
			4.9, // initial h2
			4.9, // initial i2
			4.9 - 0.2383, // final hi
			0.2383, // final h2
			0.2383, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	private static final HIReactionState state20L = new HIReactionState(
			20, // volume
			700, // temperature
			0.0, // initial hi
			4.9, // initial h2
			4.9, // initial i2
			4.9 - 0.9531, // final hi
			0.9531, // final h2
			0.9531, // final i2
			false, // hi tube
			true, // h2 tube
			true // i2 tube
	);
	
	private static final HIReactionState stateHIReactant = new HIReactionState(
			10, // volume
			700, // temperature
			3.5, // initial hi
			0.0, // initial h2
			0.0, // initial i2
			2.819, // final hi
			3.5 - 2.819, // final h2
			3.5 - 2.819, // final i2
			true, // hi tube
			false, // h2 tube
			false // i2 tube
	);
	

	private static final long serialVersionUID = 7321300459079955237L;
	
	/*
	private Bulb bulbH2;
	private Bulb bulbI2;
	private Bulb bulbHI;
	private Bulb bulbReaction;
	
	private Graph pressureTime;
	
	private PressureGauge pressureI2;
	private PressureGauge pressureH2;
	
	private Dropdown<String> tempDropdown;
	
	private Button setTemperature;
	
	private GraphDataSet h2Set;
	private GraphDataSet i2Set;
	
	private Thermometer thermometer;
	
	private double reactionTemperature;
	private double H2Concentration = 2.45;
	private double I2Concentration = 2.45;
	private double HIConcentration = 0;
	private double Kp;
	
	private double reactionTime = 0;
	
	
	*/
	
	private final ReactionApparatus reactionApparatus = new ReactionApparatus();
	
	private final Thermometer thermometer = new Thermometer(100);
	
	private Dropdown<HIReactionState> temperatureSelector;
	private Dropdown<HIReactionState> volumeSelector;
	private Dropdown<HIReactionState> reactantSelector;
	
	private Graph pressureVsTime;
	
	private GraphDataSet H2I2Set;
	private GraphDataSet HISet;
	
	private HIReactionState currentState;
	
	public HIEquilibrium() {
		super("Gauge Test Lab", 1400, 900);
		
		reactionApparatus.setOffset(10, 10);
		
		addComponent(reactionApparatus);
		
		temperatureSelector = new Dropdown<HIReactionState>(100, 20, state298K, state500K, state700K, state1000K);
		volumeSelector = new Dropdown<HIReactionState>(100, 20, state5L, state700K, state20L);
		reactantSelector = new Dropdown<HIReactionState>(100, 20, state298K, stateHIReactant);
		
		pressureVsTime = new Graph(350, 350, "Pressure vs Time", "Time (s)", "Pressure (atm)", new VerticalGraduation(0,2.45,.5,.1), new HorizontalGraduation(0,10,1,.5f));
		
		H2I2Set = new GraphDataSet("H2 & I2",true,true);
		HISet = new GraphDataSet("HI",true,true);
		
		pressureVsTime.addDataSet(H2I2Set);
		pressureVsTime.addDataSet(HISet);
		
		/*
		getRoot().setScaleChildren(true);
		
		Kp = 617.5;
		
		EmptyComponent bulbContainer = new EmptyComponent(450,300);
		
		bulbH2 = new Bulb(100,100);
		bulbContainer.addChild(bulbH2);
		
		bulbHI = new Bulb(100,100);
		bulbHI.setOffset(-100,150);
		bulbContainer.addChild(bulbHI);
		
		bulbI2 = new Bulb(100,100);
		bulbI2.setOffset(-100,275);
		bulbContainer.addChild(bulbI2);
		
		bulbReaction = new Bulb(250,250);
		bulbContainer.addChild(bulbReaction);
		
		pressureH2 = new PressureGauge(175,175,"Pressure H2","atm",5);
		pressureH2.setOffset(425,-175);
		pressureH2.setFont(pressureH2.getFont().deriveFont(12f));
		pressureH2.setValue(2.45f);
		bulbContainer.addChild(pressureH2);
		
		pressureI2 = new PressureGauge(175,175,"Pressure I2","atm",5);
		pressureI2.setOffset(425,25);
		pressureI2.setFont(pressureI2.getFont().deriveFont(12f));
		pressureI2.setValue(2.45f);
		bulbContainer.addChild(pressureI2);
		
		addComponent(bulbContainer);
		
		pressureTime = new Graph(350, 350, "Pressure vs Time", "Time (s)", "Pressure (atm)", new VerticalGraduation(0,2.45,.5,.1), new HorizontalGraduation(0,10,1,.5f));
		h2Set = new GraphDataSet("H2",true,true);
		i2Set = new GraphDataSet("   & I2",true,true);
		pressureTime.addDataSet(h2Set);
		pressureTime.addDataSet(i2Set);
		pressureTime.setOffsetX(250);
		addComponent(pressureTime);
		
		tempDropdown = new Dropdown<String>(200,50,"298K","500K","700K","1000K") {
			public void onSelectItem(String item) {
				try {
					setTemperature.setEnabled(true);
				} catch(NullPointerException e) {
					System.out.println("Null Pointer Exception Avoided");
				}
			}
		};
		
		tempDropdown.setOffset(700,25);
		tempDropdown.setValue("298K");
		
		thermometer = new Thermometer(350);
		thermometer.setGraduation(new VerticalGraduation(198, 1200, 100, 10));
		thermometer.getGraduation().setSuffix(" K");
		thermometer.setValue(298);
		thermometer.setOffsetY(-375);
		
		reactionTemperature = Double.parseDouble((tempDropdown.getValue() + "").replaceAll("K", ""));
		addComponent(tempDropdown);
		
		setTemperature = new Button(200,50,"Set Temperature") {
			@Override
			public void doSomething() {
				reactionTemperature = Double.parseDouble((tempDropdown.getValue() + "").replaceAll("K", ""));
				switch((int)reactionTemperature + "") {
				case "298":
					Kp = 617.5;
				break;
				case "500":
					Kp = 131.6;
				break;
				case "700":
					Kp = 68.6;
				break;
				case "1000":
					Kp = 42.1;
				break;
				}
				this.setEnabled(false);
			}
		};
		setTemperature.setEnabled(false);
		setTemperature.setOffsetY(25);
		addComponent(setTemperature);
		

		H2PressureReader.getTitleLabel().setFontSize(10);
		H2PressureReader.getGaugeLabel().setFontSize(11);
		
		I2PressureReader.getTitleLabel().setFontSize(10);
		I2PressureReader.getGaugeLabel().setFontSize(11);
		
		HIPressureReader.getTitleLabel().setFontSize(10);
		HIPressureReader.getGaugeLabel().setFontSize(11);
		
		Tube tube;
		
		tube = Tube.straight(3, 80, 70, 110);
		tube.setZOrder(-1);
		H2PressureReader.addChild(tube);
		
		tube = Tube.straight(3, 70, 110, 110);
		tube.setZOrder(-1);
		I2PressureReader.addChild(tube);
		
		tube = Tube.straight(3, 75, 90, 100);
		tube.setZOrder(-1);
		HIPressureReader.addChild(tube);
		
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
		*/
		
		start(30);
	}
	
	private static double lerp(double v1, double v2, float f) {
		return ((v2 - v1) * f + v1);
	}
	
	/*
	private double Qp() {
		return (HIConcentration * HIConcentration) / (H2Concentration * I2Concentration);
	}
	*/
	
	@Override
	public void update() {
		/*
		reactionTime = reactionTime + .015;
		pressureI2.setValue(I2Concentration);
		pressureH2.setValue(H2Concentration);
		
		float rate = 0.0089f;
		
		thermometer.setValue(lerp(thermometer.getValue(),reactionTemperature,rate));
		
		h2Set.addPoint(reactionTime, pressureH2.getValue());
		i2Set.addPoint(reactionTime, pressureI2.getValue());
	
		
		double offset;
		double Qc = Qp();

		if (Qc > Kp) { // NEED MORE REACTANT, SHIFT BACKWARD

			// Kc = (HI - 2x)^2 / (H2 + x)(I2 + x)
			// Kc = (HI - 2x)(HI - 2x) / (H2 + x)(I2 + x)

			// Kc = (4x^2 - (4*HI)x + (HI*HI)) / (x^2 + (I2+H2)x + (H2*I2))

			double a1, b1, c1, a2, b2, c2;

			// cross multiply and foil reactants
			a1 = Kp;
			b1 = Kp * (I2Concentration + H2Concentration);
			c1 = Kp * H2Concentration * I2Concentration;

			// foil products
			a2 = 4;
			b2 = -4 * HIConcentration;
			c2 = HIConcentration * HIConcentration;

			double[] zeros = findZeros(a1 - a2, b1 - b2, c1 - c2);
			
			offset = HIConcentration - 2 * zeros[0] > 0 ? zeros[0] : zeros[1];

			
			H2Concentration = lerp(H2Concentration, H2Concentration + offset, rate);
			I2Concentration = lerp(I2Concentration, I2Concentration + offset, rate);
			HIConcentration = lerp(HIConcentration, HIConcentration - 2 * offset, rate);


		} else if (Qc < Kp) { // NEED MORE PRODUCT, SHIFT FORWARD
			
			// Kc = (HI + 2x)^2 / (H2 - x)(I2 - x)
			// Kc = (HI + 2x)(HI + 2X) / (H2 - x)(I2 - x)
			
			// Kc = (4x^2 + (4*HI)x + (HI*HI)) / (x^2 - (H2+I2)x + (HI*I2))
			
			
			double a1, b1, c1, a2, b2, c2;

			// cross multiply and foil reactants
			a1 = Kp;
			b1 = Kp * -(I2Concentration + H2Concentration);
			c1 = Kp * H2Concentration * I2Concentration;

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
		*/
		
	}
	
	/*
	private static double[] findZeros(double a, double b, double c) {
		double s = Math.sqrt((b * b) - 4 * a * c);

		double[] zeros = new double[2];
		zeros[0] = (-b + s) / (2 * a);
		zeros[1] = (-b - s) / (2 * a);

		return zeros;
	}
	*/
	

}
