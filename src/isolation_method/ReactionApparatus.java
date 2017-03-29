package isolation_method;

import java.awt.Color;
import java.awt.Graphics;

import lab.component.LabComponent;
import lab.component.Tube;
import lab.component.container.Tank;
import lab.component.geo.Rectangle;
import lab.component.Piston;
import lab.util.VerticalGraduation;

public class ReactionApparatus extends LabComponent {

	private final Piston O2Piston;
	private final Piston NOPiston;
	private final Tank tank;
	
	public ReactionApparatus() {
		super(170, 400);
		
		setLayout(LabComponent.FREE_FORM);
		
		O2Piston = new Piston(50, 240);
		O2Piston.setGraduation(new VerticalGraduation(0, 10, 1, 1));
		O2Piston.getGraduation().setSuffix("mL");
		O2Piston.setOffset(10, 10);
		O2Piston.setGasColor(null);
		O2Piston.setCanDrag(false);
		O2Piston.setValue(10);
		
		NOPiston = new Piston(50, 240);
		NOPiston.setGraduation(new VerticalGraduation(0, 10, 1, 1));
		NOPiston.getGraduation().setSuffix("mL");
		NOPiston.setOffset(100, 10);
		NOPiston.setGasColor(null);
		NOPiston.setCanDrag(false);
		NOPiston.setValue(10);
		
		Tube tube;
		
		tube = Tube.straight(O2Piston.getWidth() / 2 - 5, O2Piston.getHeight(), 0, 40);
		tube.setZOrder(-1);
		tube.addChild(Tube.angle90(0, 60, -90, 20));
		
		O2Piston.addChild(tube);
		
		tube = Tube.straight(NOPiston.getWidth() / 2 - 5, NOPiston.getHeight(), 0, 40);
		tube.setZOrder(-1);
		tube.addChild(Tube.angle90(10, 30, 180, 20));
		
		NOPiston.addChild(tube);
		
		
		tank = new Tank(66, 80);
		tank.setOffset(47, 270);
		
		Rectangle spectrometerLaser = new Rectangle(0, 60, 66, 5);
		spectrometerLaser.setFillColor(Color.red);
		spectrometerLaser.setStroke(false);
		
		tank.addChild(spectrometerLaser);
		
		
		addChild(O2Piston, NOPiston, tank);
		
	}
	
	public Piston getO2Piston() {
		return O2Piston;
	}
	
	public Piston getNOPiston() {
		return NOPiston;	
	}
	
	public Tank getTank() {
		return tank;
	}

	@Override
	public void draw(int x, int y, int width, int height, Graphics g) {
		
	}

}
