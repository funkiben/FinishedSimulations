package vapor_pressure_lab;

import java.awt.Color;

import lab.component.container.ContentState;
import lab.component.container.Tank;
import lab.component.fx.ParticleShape;
import lab.component.fx.ParticleSystem;
import lab.component.fx.RandomVector2Generator;
import lab.component.fx.Vector2DistributionType;

public class WaterTank extends Tank {

	// particle systems to contain particles
	private final ParticleSystem liquidParticles;
	private final ParticleSystem gasParticles;

	// constructor
	public WaterTank(int width, int height, int liquidParticleAmount, int gasParticleAmount, double liquidParticleSpeed,
			double gasParticleSpeed) {
		super(width, height);

		// create gas particles system
		gasParticles = new ParticleSystem(width, height / 2, gasParticleAmount);
		// style gas particles
		gasParticles.setShape(ParticleShape.ELLIPSE);
		gasParticles.setColor(Color.red);
		gasParticles.setParticleWidth(4);
		gasParticles.setParticleHeight(4);
		gasParticles.setVelocity(new RandomVector2Generator(-gasParticleSpeed / 2, 0, gasParticleSpeed / 2,
				gasParticleSpeed, Vector2DistributionType.ELLIPSE_BORDER));
		gasParticles.setColorFade(0);
		gasParticles.setLifetime(Integer.MAX_VALUE);
		// configure spawn settings
		gasParticles.setSpawnArea(new RandomVector2Generator(3, (height / 2) - 5, width - 3, (height / 2) - 1,
				Vector2DistributionType.RECTANGLE));
		gasParticles.setParticleSpawnRate(Double.MAX_VALUE);
		// add edges of gas container
		gasParticles.addCollidableEdge(0, 0, width, 0); // top
		gasParticles.addCollidableEdge(0, 0, 0, height / 2); // left
		gasParticles.addCollidableEdge(width, 0, width, height / 2); // right
		gasParticles.addCollidableEdge(0, height / 2, width, height / 2); // bottom
		gasParticles.start();

		// create liquid particle system
		liquidParticles = new ParticleSystem(width, height / 2, liquidParticleAmount);
		// style liquid particles
		liquidParticles.setShape(ParticleShape.ELLIPSE);
		liquidParticles.setColor(Color.blue);
		liquidParticles.setParticleWidth(4);
		liquidParticles.setParticleHeight(4);
		liquidParticles.setVelocity(new RandomVector2Generator(-liquidParticleSpeed / 2, 0, liquidParticleSpeed / 2,
				liquidParticleSpeed, Vector2DistributionType.ELLIPSE_BORDER));
		liquidParticles.setColorFade(0);
		liquidParticles.setLifetime(Integer.MAX_VALUE);
		// configure spawn settings
		liquidParticles.setSpawnArea(
				new RandomVector2Generator(1, 1, width - 1, height / 2 - 1, Vector2DistributionType.RECTANGLE));
		liquidParticles.setParticleSpawnRate(Double.MAX_VALUE);
		// add edges of liquid container
		liquidParticles.addCollidableEdge(0, 0, width, 0); // top
		liquidParticles.addCollidableEdge(0, 0, 0, height / 2); // left
		liquidParticles.addCollidableEdge(width, 0, width, height / 2); // right
		liquidParticles.addCollidableEdge(0, height / 2, width, height / 2); // bottom
		liquidParticles.start();

		// spawn liquid particles immediately
		for (int i = 0; i < liquidParticleAmount; i++)
			liquidParticles.spawnParticle();
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
