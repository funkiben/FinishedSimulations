package equilibrium;

import java.awt.Color;
import java.awt.Point;

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
import lab.component.input.ButtonComponent;
import lab.component.input.LabelComponent;
import lab.component.sensor.Manometer;
import lab.component.sensor.Thermometer;

public class GasEquilibrium extends LabFrame {

	private static final long serialVersionUID = 1L;

	
	private final double Kp;
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
	
	public GasEquilibrium(String name, double mass, String substance, String reaction, double Kp, double Kc, double temp) {
		super(name, 660, 725);
		
		this.Kp = Kp;
		this.temp = temp;
		
		manometer = new Manometer(150, 600);
		manometer.setOffsetY(20);
		manometer.setGraduation(new VerticalGraduation(0, 760, 20, 5));
		manometer.setValue(760.0);
		
		EmptyComponent middleContentArea = new EmptyComponent(300, 600);
		
		bulb = new Bulb(300);
		//bulb.setOffsetY(230);
		bulb.setOffsetX(35);
		bulb.setContentColor(new Color(240, 240, 240));
		bulb.setContentState(ContentState.SOLID);
		
		gasParticles = new ParticleSystem(300, 300, 50);
		gasParticles.setLifetime(Integer.MAX_VALUE);
		gasParticles.setParticleSpawnRate(0.0001);
		gasParticles.setSpawnArea(new Vector2(150, 150));
		gasParticles.setColor(Color.black);
		gasParticles.setColorFade(0);
		gasParticles.setShape(Particle.ELLIPSE);
		gasParticles.setParticleWidth(10);
		gasParticles.setParticleHeight(10);
		gasParticles.setParticleWidthChange(0);
		gasParticles.setParticleHeightChange(0);
		gasParticles.setVelocity(new RandomVector2Generator(1));
		gasParticles.start();
		
		
		bulb.addChild(gasParticles);
		
		EmptyComponent infoComponent = new EmptyComponent(270, 100);
		infoComponent.setShowBounds(true);
		infoComponent.setOffsetX(50);
		infoComponent.setOffsetY(30);
		
		LabelComponent massLabel = new LabelComponent(300, 30, "Initial NaHCO3 Mass: " + mass + "g");
		massLabel.setFontSize(20);
		massLabel.setOffsetX(10);
		massLabel.setOffsetY(0);
		
		LabelComponent atmPressureLabel = new LabelComponent(300, 30, "Atmosphere: 1.0 atm");
		atmPressureLabel.setFontSize(20);
		atmPressureLabel.setOffsetX(10);
		atmPressureLabel.setOffsetY(0);
		
		LabelComponent tempLabel = new LabelComponent(300, 30, "Initial Temp: 20C");
		tempLabel.setFontSize(20);
		tempLabel.setOffsetX(10);
		tempLabel.setOffsetY(0);
		
		infoComponent.addChild(massLabel, tempLabel, atmPressureLabel);
		
		middleContentArea.addChild(infoComponent, bulb);
		
		
		thermometer = new Thermometer(500);
		thermometer.setOffsetX(40);
		thermometer.setOffsetY(20);
		thermometer.setGraduation(new VerticalGraduation(0, 1000, 100, 20));
		thermometer.setValue(20);
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
		animateMeasurable(20, thermometer);
		
		bulb.setValue(0.0);
		addSubstanceButton.setEnabled(true);
		evacuateButton.setEnabled(false);
		heatButton.setEnabled(false);
	}
	
	public void addSubstance() {
		bulb.setValue(20.0);
		evacuateButton.setEnabled(true);
		addSubstanceButton.setEnabled(false);
		
	}
	
	public void evacuate() {
		animateMeasurable(0, manometer);
	
		evacuateButton.setEnabled(false);
		heatButton.setEnabled(true);
	}

	public void heat() {
		animateMeasurable(Kp * 760, manometer);
		animateMeasurable(temp, thermometer);
		
		heatButton.setEnabled(false);
		
	}
	
	public void showKp() {
		KpLabel.setVisible(!KpLabel.isVisible());
	}
	
	public void showKc() {
		KcLabel.setVisible(!KcLabel.isVisible());
	}
	
	
	private boolean addedGasParticleBounds = false;
	
	@Override
	public void update() {
		
		if (bulb.getInnerEdges().size() > 0 && !addedGasParticleBounds) {
		
			for (Point[] edge : bulb.getInnerEdges()) {
				gasParticles.addCollidableEdge(edge[0].x - bulb.getLastDrawX(), edge[0].y - bulb.getLastDrawY(), edge[1].x - bulb.getLastDrawX(), edge[1].y  - bulb.getLastDrawY());
			
			}
			addedGasParticleBounds = true;
		}
		
		gasParticles.setWidth(Math.min(gasParticles.getWidth(), gasParticles.getHeight()));
		gasParticles.setHeight(gasParticles.getWidth());
		
		
		
	}

	

}
