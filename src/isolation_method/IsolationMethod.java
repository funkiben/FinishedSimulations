package isolation_method;

import java.awt.Color;

import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.LabComponent;
import lab.component.geo.Rectangle;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.DoubleField;

public class IsolationMethod extends LabFrame {

	private static final long serialVersionUID = -6825206580344099210L;

	public static void main(String[] args) {
		new IsolationMethod();
	}
	
	private final ReactionApparatus reactionApparatus;
	private final Button start;
	private final Button stop;
	private final Button reset;
	private final DoubleField O2Amount;
	private final DoubleField NOAmount;
	
	public IsolationMethod() {
		super("Isolation Method", 800, 650);
		
		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(30, 30);
		addComponent(reactionApparatus);
		
		
		Rectangle controlArea = new Rectangle(170, 80);
		controlArea.setFill(false);
		controlArea.setStrokeColor(Color.LIGHT_GRAY);
		
		
		start = new Button(50, 20, "Start") {
			@Override
			public void doSomething() {
				
			}
		};
		start.setOffset(5, 5);
		
		stop = new Button(50, 20, "Stop") {
			@Override
			public void doSomething() {
				
			}
		};
		stop.setOffset(5, 5);
		
		reset = new Button(50, 20, "Reset") {
			@Override
			public void doSomething() {
				
			}
		};
		reset.setOffset(5, 5);
		
		O2Amount = new DoubleField(100, 0.0, 10.0, 3);
		NOAmount = new DoubleField(100, 0.0, 10.0, 3);
		
		controlArea.addChild(new EmptyComponent(5, 0), new Label(60, 20, "O2 (mL): "), O2Amount, new EmptyComponent(10000, 0), new EmptyComponent(5, 0), new Label(60, 20, "NO (mL): "), NOAmount, new EmptyComponent(1000, 5), start, stop, reset);
		
		controlArea.setOffsetY(380);
		
		reactionApparatus.addChild(controlArea);
		
		start(30);
		
	}

	@Override
	public void update() {
		
	}

	
	

	

}
