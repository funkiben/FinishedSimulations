package HI_equilibrium;

import java.awt.Color;
import java.awt.Graphics;

import lab.component.LabComponent;
import lab.component.Tube;
import lab.component.container.Bulb;
import lab.component.container.ContentState;

public class ReactionApparatus extends LabComponent {
	
	static final Color I2_COLOR = new Color(157, 0, 163, 127);
	
	private final Bulb reactionBulb, H2Bulb, I2Bulb, HIBulb;
	private final Tube reactionBulbTube, H2Tube, I2Tube, HITube;

	public ReactionApparatus() {
		super(370, 320);
		
		setLayout(LabComponent.FREE_FORM);

		reactionBulb = new Bulb(250, 250);
		H2Bulb = new Bulb(100, 100);
		I2Bulb = new Bulb(100, 100);
		HIBulb = new Bulb(100, 100);
		
		I2Bulb.setContentState(ContentState.GAS);
		I2Bulb.setContentColor(I2_COLOR);
		I2Bulb.setValue(100);
		
		reactionBulb.setContentState(ContentState.GAS);
		reactionBulb.setContentColor(I2_COLOR);
		reactionBulb.setValue(0);
		
		
		H2Bulb.setLabel("H2");
		H2Bulb.setLabelOffset(35, 60);
		H2Bulb.setLabelSize(20);
		
		I2Bulb.setLabel("I2");
		I2Bulb.setLabelOffset(35, 60);
		I2Bulb.setLabelSize(20);
		
		HIBulb.setLabel("HI");
		HIBulb.setLabelOffset(35, 60);
		HIBulb.setLabelSize(20);
		

		reactionBulb.setOffset(120, 40);
		H2Bulb.setOffset(0, 0);
		I2Bulb.setOffset(0, 110);
		HIBulb.setOffset(0, 220);

		addChild(HIBulb, H2Bulb, I2Bulb, reactionBulb);

		Tube.setDiameter(6);

		H2Tube = Tube.straight(95, 70, 300, 110);
		I2Tube = Tube.straight(99, 163, 270, 75);
		HITube = Tube.straight(95, 253, 240, 110);

		I2Tube.setColor(I2_COLOR);
		
		addChild(H2Tube, I2Tube, HITube);
		
		reactionBulbTube = Tube.straight(115, 0, 180, 100);
		reactionBulb.addChild(reactionBulbTube);

	}

	public Bulb getReactionBulb() {
		return reactionBulb;
	}

	public Bulb getH2Bulb() {
		return H2Bulb;
	}

	public Bulb getI2Bulb() {
		return I2Bulb;
	}

	public Bulb getHIBulb() {
		return HIBulb;
	}

	public Tube getH2Tube() {
		return H2Tube;
	}

	public Tube getI2Tube() {
		return I2Tube;
	}

	public Tube getHITube() {
		return HITube;
	}
	
	public void setReactionBulbSize(int size) {
		reactionBulb.setOffset(reactionBulb.getOffsetX() + (reactionBulb.getWidth() - size) / 2, reactionBulb.getOffsetY() + (reactionBulb.getHeight() - size) / 2);
		reactionBulbTube.setOffset((size / 2) + 4, 0);
		reactionBulb.setWidth(size);
		reactionBulb.setHeight(size);
	}

	@Override
	public void draw(int x, int y, int width, int height, Graphics g) {

	}

}
