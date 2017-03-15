package NH4Cl_decomposition;

import java.awt.Color;

import draw.animation.DoubleLerpAnimation;
import draw.animation.DoubleLinearAnimation;
import draw.animation.IntegerLinearAnimation;
import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.LabComponent;
import lab.component.MeasurableComponent;
import lab.component.Tube;
import lab.component.container.Bulb;
import lab.component.container.ContentState;
import lab.component.fx.ParticleShape;
import lab.component.fx.ParticleSystem;
import lab.component.fx.RandomVector2Generator;
import lab.component.misc.BunsenBurner;
import lab.component.sensor.PressureGauge;
import lab.component.sensor.Thermometer;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.Dropdown;
import lab.util.SigFig;
import lab.util.Vector2;
import lab.util.VerticalGraduation;

public class NH4ClDecomposition extends LabFrame {

	public static void main(String[] args) {
		
		ReactionCondition[] conditions = {
					new ReactionCondition(298, 2.07E-6, 1, 20.0, 0),
					new ReactionCondition(611, 165.296, 12, 13.0, 100),
					new ReactionCondition(650, 467.43, 25, 7.0, 150)
		};
		
		new NH4ClDecomposition("Heterogeneous Equilibrium: Decomposition of Ammonium Chloride", 25.0, 1.00, conditions);
	}
	
	private static final long serialVersionUID = 1L;
	
	private final ReactionCondition[] reactionConditions;
	
	//private final Manometer manometer;
	private final PressureGauge pressureGauge;
	private final Bulb bulb;
	private final Thermometer thermometer;
	private final ParticleSystem HClParticles;
	private final ParticleSystem NH3Particles;
	private final BunsenBurner burner;
	
	private final Button resetButton;
	private final Button addSubstanceButton;
	private final Button evacuateButton;
	private final Button setTemperatureButton;
	private final Button detailsButton;
	
	private final Dropdown<ReactionCondition> conditionSelector;
	
	private final LabFrame detailsWindow;
	
	private boolean bulbEvacuated = false;
	
	private ReactionCondition currentCondition;
	
	public NH4ClDecomposition(String name, double mass, double volume, ReactionCondition[] reactionConditions) {
		super(name, 660, 650);
		
		this.reactionConditions = reactionConditions;
		currentCondition = reactionConditions[0];
		
		pressureGauge = new PressureGauge(200, 200, "Total Pressure", "kPa", 6, 6);
		pressureGauge.setOffset(10, 115);
		pressureGauge.setValue(101.325);
		
		pressureGauge.addChild(Tube.straight(200, 105, 270, 50));
		
		EmptyComponent middleContentArea = new EmptyComponent(300, 475);
		middleContentArea.setOffset(35, 50);
		
		bulb = new Bulb(300, 300);
		bulb.setOffset(0, 0);
		bulb.setContentColor(new Color(240, 240, 240));
		bulb.setContentState(ContentState.SOLID);
		bulb.setLayout(LabComponent.FREE_FORM);
		
		bulb.addChild(Tube.straight(145, 0, 0, -100));
		
		
		burner = new BunsenBurner(20, 175);
		burner.setOffsetY(1);
		burner.setOffsetX(140);
		burner.getFlame().setVisible(false);
		burner.getFlame().setIntensity(0);

		
		HClParticles = new ParticleSystem(300, 300, reactionConditions[2].gasParticles);
		HClParticles.setLifetime(Integer.MAX_VALUE);
		HClParticles.setParticleSpawnRate(Double.MAX_VALUE);
		HClParticles.setSpawnArea(new Vector2(150, 295));
		HClParticles.setColorFade(0);
		HClParticles.setShape(ParticleShape.ELLIPSE);
		HClParticles.setParticleWidth(6);
		HClParticles.setParticleHeight(6);
		HClParticles.setParticleWidthChange(0);
		HClParticles.setParticleHeightChange(0);
		HClParticles.setVelocity(new RandomVector2Generator(6));
		HClParticles.setColor(new Color(255, 0, 0));

		for (int i = 1; i < Bulb.POLY1_X.length - 3; i++) {
			HClParticles.addCollidableEdge(Bulb.POLY1_X[i - 1] * bulb.getWidth(), Bulb.POLY1_Y[i - 1] * bulb.getHeight(), Bulb.POLY1_X[i] * bulb.getWidth(), Bulb.POLY1_Y[i] * bulb.getHeight());
			HClParticles.addCollidableEdge(Bulb.POLY2_X[i - 1] * bulb.getWidth(), Bulb.POLY2_Y[i - 1] * bulb.getHeight(), Bulb.POLY2_X[i] * bulb.getWidth(), Bulb.POLY2_Y[i] * bulb.getHeight());
		}
		
		HClParticles.addCollidableEdge(Bulb.POLY2_X[0] * bulb.getWidth(), Bulb.POLY2_Y[0] * bulb.getHeight(), Bulb.POLY1_X[0] * bulb.getWidth(), Bulb.POLY1_Y[0] * bulb.getHeight());
		
		bulb.addChild(HClParticles);
		
		
		
		
		NH3Particles = new ParticleSystem(300, 300, reactionConditions[2].gasParticles);
		NH3Particles.setLifetime(Integer.MAX_VALUE);
		NH3Particles.setParticleSpawnRate(Double.MAX_VALUE);
		NH3Particles.setSpawnArea(new Vector2(150, 295));
		NH3Particles.setColorFade(0);
		NH3Particles.setShape(ParticleShape.ELLIPSE);
		NH3Particles.setParticleWidth(6);
		NH3Particles.setParticleHeight(6);
		NH3Particles.setParticleWidthChange(0);
		NH3Particles.setParticleHeightChange(0);
		NH3Particles.setVelocity(new RandomVector2Generator(6));
		NH3Particles.setColor(new Color(0, 0, 255));

		for (int i = 1; i < Bulb.POLY1_X.length - 3; i++) {
			NH3Particles.addCollidableEdge(Bulb.POLY1_X[i - 1] * bulb.getWidth(), Bulb.POLY1_Y[i - 1] * bulb.getHeight(), Bulb.POLY1_X[i] * bulb.getWidth(), Bulb.POLY1_Y[i] * bulb.getHeight());
			NH3Particles.addCollidableEdge(Bulb.POLY2_X[i - 1] * bulb.getWidth(), Bulb.POLY2_Y[i - 1] * bulb.getHeight(), Bulb.POLY2_X[i] * bulb.getWidth(), Bulb.POLY2_Y[i] * bulb.getHeight());
		}
		
		NH3Particles.addCollidableEdge(Bulb.POLY2_X[0] * bulb.getWidth(), Bulb.POLY2_Y[0] * bulb.getHeight(), Bulb.POLY1_X[0] * bulb.getWidth(), Bulb.POLY1_Y[0] * bulb.getHeight());
		
		bulb.addChild(NH3Particles);
		
		
		
		middleContentArea.addChild(bulb, burner);
		
		
		thermometer = new Thermometer(500);
		thermometer.setOffsetX(40);
		thermometer.setOffsetY(20);
		thermometer.setGraduation(new VerticalGraduation(250, 650, 50, 10));
		thermometer.setValue(currentCondition.temperature);
		thermometer.getGraduation().setSuffix("K");
		
		
		addComponent(pressureGauge, middleContentArea, thermometer);
		
		
		
		
		resetButton = new Button(150, 25, "Reset Experiment") {
			@Override
			public void doSomething() {
				resetExperiment();
			}
		};
		
		addSubstanceButton = new Button(150, 25, "Add NH4Cl") {
			@Override
			public void doSomething() {
				addSubstance();
			}
		};
		
		evacuateButton = new Button(150, 25, "Evacuate Bulb") {
			@Override
			public void doSomething() {
				evacuate();
			}
		};
		
		setTemperatureButton = new Button(200, 25, "Set Temperature") {
			@Override
			public void doSomething() {
				changeTemperature();
			}
		};
		
		conditionSelector = new Dropdown<ReactionCondition>(100, 25, reactionConditions) {
			@Override
			public void onSelectItem(ReactionCondition p) {
				setTemperatureButton.setEnabled(p != currentCondition);
				
			}
		};
		
		detailsButton = new Button(150, 25, "Show Details")  {
			@Override
			public void doSomething() {
				detailsWindow.setVisible(true);
			}
		};
		
		
		addSubstanceButton.setOffsetX(10);
		addSubstanceButton.setOffsetX(10);
		evacuateButton.setOffsetX(10);
		
		conditionSelector.setOffsetX(10);
		

		resetButton.setOffset(10, 15);
		detailsButton.setOffset(10, 15);
		
		addComponent( new EmptyComponent(660, 30), addSubstanceButton, evacuateButton, conditionSelector, setTemperatureButton, resetButton, detailsButton);
		
		
		detailsWindow = new LabFrame("Simulation Details", 400, 250, false) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {
				
			}
		};
		
		detailsWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		detailsWindow.setResizable(false);
		
		Label reactionLabel, massLabel, volumeLabel, atmPressureLabel;
		
		reactionLabel = new Label(400, 30, "NH4Cl(s) <=> HCl(g) + NH3(g)");
		reactionLabel.setFontSize(20);
		reactionLabel.setOffset(10, 0);
		
		
		massLabel = new Label(400, 30, "Ammonium Chloride Mass = " + mass + "g");
		massLabel.setFontSize(20);
		massLabel.setOffset(10, 0);
		
		volumeLabel = new Label(400, 30, "Bulb Volume = " + SigFig.sigfigalize(volume, 3) + "L");
		volumeLabel.setFontSize(20);
		volumeLabel.setOffset(10, 0);
		
		atmPressureLabel = new Label(400, 30, "Atmosphere Pressure = 1.0 atm");
		atmPressureLabel.setFontSize(20);
		atmPressureLabel.setOffset(10, 0);
		
		detailsWindow.addComponent(reactionLabel, massLabel, volumeLabel, atmPressureLabel);
		detailsWindow.start(0);
		
		resetExperiment();
		
		start(30);
		
	}
	
	private void animateMeasurable(double value, final MeasurableComponent c) {
		
		getAnimator().addAnimation(c.toString(), new DoubleLerpAnimation(value, 0.05f, 1.0) {
			@Override
			public Double getValue() {
				return c.getValue();
			}
			
			@Override
			public void setValue(Double v) {
				c.setValue(v);
			}
		});
	}
	
	
	public void resetExperiment() {
		currentCondition = reactionConditions[0];
		
		animateMeasurable(101.325, pressureGauge); 
		animateMeasurable(currentCondition.temperature, thermometer);
		
		if (getAnimator().animationExists("removeSolid")) {
			getAnimator().getAnimation("removeSolid").cancel();
		}
		
		bulb.setValue(0.0);
		addSubstanceButton.setEnabled(true);
		evacuateButton.setEnabled(false);
		setTemperatureButton.setEnabled(false);
		conditionSelector.setValue(currentCondition);
		conditionSelector.setEnabled(false);
		NH3Particles.stop();
		HClParticles.stop();
		
		bulbEvacuated = false;
		
		burner.getFlame().setVisible(false);
		burner.getFlame().setIntensity(0);
	}
	
	public void addSubstance() {
		bulb.setValue(20.0);
		evacuateButton.setEnabled(true);
		addSubstanceButton.setEnabled(false);
	}
	
	public void evacuate() {
		animateMeasurable(0, pressureGauge);
		
		evacuateButton.setEnabled(false);
		conditionSelector.setEnabled(true);
		
		bulbEvacuated = true;
		
		NH3Particles.start();
		HClParticles.start();
		
	}

	public void changeTemperature() {
		currentCondition = conditionSelector.getValue();
		animateMeasurable(currentCondition.temperature, thermometer);
		
		setTemperatureButton.setEnabled(false);
		
		if (thermometer.getValue() < currentCondition.temperature) {
			burner.getFlame().setVisible(true);
		}
		
		getAnimator().addAnimation("flame", new IntegerLinearAnimation(currentCondition.flameIntensity, 5) {
			@Override
			public Integer getValue() {
				return burner.getFlame().getIntensity();
			}
				
			@Override
			public void setValue(Integer v) {
				burner.getFlame().setIntensity(v);
				
				if (v == 0) {
					burner.getFlame().setVisible(false);
				}
			}
		});
		
		
		
		getAnimator().addAnimation("removeSolid", new DoubleLinearAnimation(currentCondition.solidAmount, 0.09) {
			@Override
			public Double getValue() {
				return bulb.getValue();
			}
			
			@Override
			public void setValue(Double v) {
				bulb.setValue(v);
			}
		});
		
	}
	
	private int t = 0;
	
	@Override
	public void update() {
		
		if (bulbEvacuated) {
			
			if (t % 5 == 0) {
			
				if (HClParticles.getActiveParticles() < currentCondition.gasParticles) {
					HClParticles.spawnParticle();
				} else if (HClParticles.getActiveParticles() > currentCondition.gasParticles) {
					HClParticles.removeParticle();
				}
				
				if (NH3Particles.getActiveParticles() < currentCondition.gasParticles) {
					NH3Particles.spawnParticle();
				} else if (NH3Particles.getActiveParticles() > currentCondition.gasParticles) {
					NH3Particles.removeParticle();
				}
				
			}
				
			t++;
			
			double p = ((double) NH3Particles.getActiveParticles() / currentCondition.gasParticles) * (currentCondition.pressure);
				
			animateMeasurable(p, pressureGauge);
		}
		
		
	}
	
	
	static class ReactionCondition {
		final double temperature;
		final double pressure;
		final int gasParticles;
		final double solidAmount;
		final int flameIntensity;
		
		ReactionCondition(double temperature, double pressure, int gasParticles, double solidAmount, int flameIntensity) {
			this.temperature = temperature;
			this.pressure = pressure;
			this.gasParticles = gasParticles;
			this.solidAmount = solidAmount;
			this.flameIntensity = flameIntensity;
		}
		
		@Override
		public String toString() {
			return SigFig.sigfigalize(temperature, 3, 5) + "K";
		}
	}

}
