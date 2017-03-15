package vapor_pressure_lab;

import java.awt.Color;

import lab.LabFrame;
import lab.component.container.ContentState;
import lab.component.container.Tank;
import lab.component.fx.ParticleShape;
import lab.component.fx.ParticleSystem;
import lab.component.fx.RandomVector2Generator;
import lab.component.fx.Vector2DistributionType;

public class WaterTank extends Tank {

	
	public static void main(String[] args) {
		LabFrame lf = new LabFrame("Water Tank Test", 800, 800) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {
				
			}
			
		};
		
		
		tank.setOffset(100, 100);
		
		lf.addComponent(tank);
		
		lf.start(30);
	}
	
	
	
	
	private final ParticleSystem liquidParticles;
	private final ParticleSystem gasParticles;
	
	public WaterTank(int width, int height, int liquidParticleAmount, int gasParticleAmount, double liquidParticleSpeed, double gasParticleSpeed) {
		super(width, height);
		
		gasParticles = new ParticleSystem(width, height / 2, gasParticleAmount);
		
		gasParticles.setShape(ParticleShape.ELLIPSE);
		gasParticles.setColor(Color.red);
		gasParticles.setParticleWidth(4);
		gasParticles.setParticleHeight(4);
		gasParticles.setVelocity(new RandomVector2Generator(-gasParticleSpeed, -gasParticleSpeed, gasParticleSpeed, gasParticleSpeed, Vector2DistributionType.ELLIPSE_BORDER));
		gasParticles.setColorFade(0);
		gasParticles.setLifetime(Integer.MAX_VALUE);
		
		gasParticles.addCollidableEdge(0, 0, width, 0); // top
		gasParticles.addCollidableEdge(0, 0, 0, height / 2); // left
		gasParticles.addCollidableEdge(width, 0, width, height / 2); // right
		gasParticles.addCollidableEdge(0, height / 2, width, height / 2); // bottom
		
		
		liquidParticles = new ParticleSystem(width, height / 2, liquidParticleAmount);
		
		liquidParticles.setShape(ParticleShape.ELLIPSE);
		liquidParticles.setColor(Color.blue);
		liquidParticles.setParticleWidth(4);
		liquidParticles.setParticleHeight(4);
		liquidParticles.setVelocity(new RandomVector2Generator(-liquidParticleSpeed, -liquidParticleSpeed, liquidParticleSpeed, liquidParticleSpeed, Vector2DistributionType.ELLIPSE_BORDER));
		liquidParticles.setColorFade(0);
		liquidParticles.setLifetime(Integer.MAX_VALUE);
		liquidParticles.setSpawnArea(new RandomVector2Generator(3, (height / 2) - 5, width - 3, (height / 2) - 1, Vector2DistributionType.RECTANGLE));
		
		liquidParticles.addCollidableEdge(0, 0, width, 0); // top
		liquidParticles.addCollidableEdge(0, 0, 0, height / 2); // left
		liquidParticles.addCollidableEdge(width, 0, width, height / 2); // right
		liquidParticles.addCollidableEdge(0, height / 2, width, height / 2); // bottom
		
		
		liquidParticles.start();
		gasParticles.start();

		addChild(gasParticles, liquidParticles);
		
		
		setContentState(ContentState.LIQUID);
		setContentColor(new Color(42, 157, 183));
		setValue(50);
		
	}
	
	public ParticleSystem getLiquidParticleSystem() {
		return liquidParticles;
	}
	
	public ParticleSystem getGasParticleSystem() {
		return gasParticles;
	}

}
