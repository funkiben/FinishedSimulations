package equilibrium;

import java.awt.Color;

import draw.animation.DoubleLerpAnimation;
import lab.LabFrame;
import lab.Vector2;
import lab.component.EmptyComponent;
import lab.component.VerticalGraduation;
import lab.component.MeasurableComponent;
import lab.component.container.Bulb;
import lab.component.container.ContentState;
import lab.component.fx.Particle;
import lab.component.fx.ParticleSystem;
import lab.component.fx.RandomVector2Generator;
import lab.component.sensor.Manometer;
import lab.component.sensor.Thermometer;
import lab.component.swing.LabelComponent;
import lab.component.swing.input.ButtonComponent;

public class GasEquilibrium extends LabFrame {

	private static final long serialVersionUID = 1L;
	private final double Kp;
	private final double initialTemp;
	private final double temp;
	
	private final Manometer manometer;
	private final Bulb bulb;
	private final Thermometer thermometer;
	private final ParticleSystem gasParticles;
	
	private final ButtonComponent resetButton;
	private final ButtonComponent addSubstanceButton;
	private final ButtonComponent evacuateButton;
	private final ButtonComponent heatButton;
	private final ButtonComponent showKpButton;
	private final ButtonComponent showKcButton;
	private final LabelComponent KcLabel;
	private final LabelComponent KpLabel;
	
	private boolean reactionOccuring = false;
	
	public GasEquilibrium(String name, double mass, String substance, String reaction, double Kp, double Kc, double initialTemp, double temp) {
		super(name, 660, 725);
		
		this.Kp = Kp;
		this.initialTemp = initialTemp;
		this.temp = temp;
		
		manometer = new Manometer(150, 600);
		manometer.setOffsetY(20);
		manometer.setGraduation(new VerticalGraduation(0, 760, 20, 5));
		manometer.setValue(760.0);
		
		EmptyComponent middleContentArea = new EmptyComponent(300, 600);
		middleContentArea.setOffsetX(20);
		
		bulb = new Bulb(300, 300);
		bulb.setOffsetX(35);
		bulb.setContentColor(new Color(240, 240, 240));
		bulb.setContentState(ContentState.SOLID);
		
		gasParticles = new ParticleSystem(300, 300, 50);
		gasParticles.setLifetime(Integer.MAX_VALUE);
		gasParticles.setParticleSpawnRate(Double.MAX_VALUE);
		gasParticles.setSpawnArea(new Vector2(150, 295));
		gasParticles.setColor(Color.black);
		gasParticles.setColorFade(0);
		gasParticles.setShape(Particle.ELLIPSE);
		gasParticles.setParticleWidth(6);
		gasParticles.setParticleHeight(6);
		gasParticles.setParticleWidthChange(0);
		gasParticles.setParticleHeightChange(0);
		gasParticles.setVelocity(new RandomVector2Generator(6));
		gasParticles.setColor(new Color(0, 0, 255));

		for (int i = 1; i < Bulb.POLY1_X.length - 3; i++) {
			gasParticles.addCollidableEdge(Bulb.POLY1_X[i - 1] * bulb.getWidth(), Bulb.POLY1_Y[i - 1] * bulb.getHeight(), Bulb.POLY1_X[i] * bulb.getWidth(), Bulb.POLY1_Y[i] * bulb.getHeight());
			gasParticles.addCollidableEdge(Bulb.POLY2_X[i - 1] * bulb.getWidth(), Bulb.POLY2_Y[i - 1] * bulb.getHeight(), Bulb.POLY2_X[i] * bulb.getWidth(), Bulb.POLY2_Y[i] * bulb.getHeight());
		}
		
		gasParticles.addCollidableEdge(Bulb.POLY2_X[0] * bulb.getWidth(), Bulb.POLY2_Y[0] * bulb.getHeight(), Bulb.POLY1_X[0] * bulb.getWidth(), Bulb.POLY1_Y[0] * bulb.getHeight());
		
		gasParticles.start();
		
		
		bulb.addChild(gasParticles);
		
		EmptyComponent infoComponent = new EmptyComponent(300, 100);
		infoComponent.setShowBounds(true);
		infoComponent.setOffsetX(30);
		infoComponent.setOffsetY(30);
		
		LabelComponent massLabel = new LabelComponent(300, 30, "Initial NaHCO3 Mass: " + mass + "g");
		massLabel.setFontSize(20);
		massLabel.setOffsetX(10);
		massLabel.setOffsetY(0);
		
		LabelComponent atmPressureLabel = new LabelComponent(300, 30, "Atmosphere: 1.0 atm");
		atmPressureLabel.setFontSize(20);
		atmPressureLabel.setOffsetX(10);
		atmPressureLabel.setOffsetY(0);
		
		LabelComponent tempLabel = new LabelComponent(300, 30, "Initial Temp: " + initialTemp + "C");
		tempLabel.setFontSize(20);
		tempLabel.setOffsetX(10);
		tempLabel.setOffsetY(0);
		
		infoComponent.addChild(massLabel, tempLabel, atmPressureLabel);
		
		middleContentArea.addChild(infoComponent, bulb);
		
		
		thermometer = new Thermometer(500);
		thermometer.setOffsetX(80);
		thermometer.setOffsetY(20);
		thermometer.setGraduation(new VerticalGraduation(0, 1000, 100, 20));
		thermometer.setValue(initialTemp);
		thermometer.getGraduation().setSuffix("C");
		
		
		addComponent(manometer, middleContentArea, thermometer);
		
		
		
		
		resetButton = new ButtonComponent(150, 25, "Reset Experiment") {
			@Override
			public void doSomething() {
				resetExperiment();
			}
		};
		
		addSubstanceButton = new ButtonComponent(150, 25, "Add " + substance) {
			@Override
			public void doSomething() {
				addSubstance();
			}
		};
		
		evacuateButton = new ButtonComponent(150, 25, "Evacuate Bulb") {
			@Override
			public void doSomething() {
				evacuate();
			}
		};
		
		heatButton = new ButtonComponent(150, 25, "Heat System") {
			@Override
			public void doSomething() {
				heat();
			}
		};
		
		showKpButton = new ButtonComponent(100, 25, "Show Kp")  {
			@Override
			public void doSomething() {
				showKp();
			}
		};
		
		showKcButton = new ButtonComponent(100, 25, "Show Kc")  {
			@Override
			public void doSomething() {
				showKc();
			}
		};
		
		KpLabel = new LabelComponent(100, 20, "Kp: " + Kp);
		KpLabel.setVisible(false);
		KcLabel = new LabelComponent(100, 20, "Kc: " + Kc);
		KcLabel.setVisible(false);
		
		LabelComponent reactionLabel = new LabelComponent(250, 15, reaction);
		reactionLabel.setOffsetY(0);
		
		resetButton.setOffsetY(5);
		addSubstanceButton.setOffsetY(5);
		addSubstanceButton.setOffsetY(5);
		evacuateButton.setOffsetY(5);
		heatButton.setOffsetY(5);
		showKpButton.setOffsetY(5);
		showKcButton.setOffsetY(5);
		KpLabel.setOffsetY(5);
		KcLabel.setOffsetY(5);
		
		addComponent(new EmptyComponent(250, 1), reactionLabel, resetButton, addSubstanceButton, evacuateButton, heatButton, showKpButton, showKcButton, KpLabel, KcLabel);
		
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
		animateMeasurable(760, manometer); 
		animateMeasurable(initialTemp, thermometer);
		
		bulb.setValue(0.0);
		addSubstanceButton.setEnabled(true);
		evacuateButton.setEnabled(false);
		heatButton.setEnabled(false);
		reactionOccuring = false;
		gasParticles.stop();
		gasParticles.setParticleSpawnRate(Double.MAX_VALUE);
	}
	
	public void addSubstance() {
		bulb.setValue(20.0);
		evacuateButton.setEnabled(true);
		addSubstanceButton.setEnabled(false);
		gasParticles.start();
		gasParticles.spawnParticle();
	}
	
	public void evacuate() {
		animateMeasurable(0, manometer);
		
		gasParticles.stop();
		gasParticles.start();
		gasParticles.spawnParticle();
		
		evacuateButton.setEnabled(false);
		heatButton.setEnabled(true);
	}

	public void heat() {
		//animateMeasurable(Kp * 760, manometer);
		animateMeasurable(temp, thermometer);
		
		heatButton.setEnabled(false);
		
		gasParticles.spawnParticle();
		
		reactionOccuring = true;
		
	}
	
	public void showKp() {
		KpLabel.setVisible(!KpLabel.isVisible());
	}
	
	public void showKc() {
		KcLabel.setVisible(!KcLabel.isVisible());
	}
	
	
	@Override
	public void update() {
		
		if (reactionOccuring) {
			gasParticles.setParticleSpawnRate(105 - thermometer.getValue() / temp * 100.0);
			
			double p = ((double) gasParticles.getActiveParticles() / gasParticles.getTotalParticles()) * Kp * 760.0;
			
			animateMeasurable(p, manometer);
		}
		
		
		
		
	}

	

}
