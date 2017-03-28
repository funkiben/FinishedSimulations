package isolation_method;

import java.awt.Color;

import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.geo.Rectangle;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.field.DoubleField;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;

public class IsolationMethod extends LabFrame {

	private static final long serialVersionUID = -6825206580344099210L;

	public static void main(String[] args) {
		new IsolationMethod();
	}
	
	private final ReactionApparatus reactionApparatus;
	private final Button start, stop, reset;
	private final DoubleField O2Amount, NOAmount;
	
	private final Graph zeroOrderGraph, firstOrderGraph, secondOrderGraph;
	private final GraphDataSet zeroOrderData, firstOrderData, secondOrderData;
	
	public IsolationMethod() {
		super("Isolation Method", 1000, 650);
		
		reactionApparatus = new ReactionApparatus();
		reactionApparatus.setOffset(15, 30);
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
		
		
		HorizontalGraduation hg;
		VerticalGraduation vg;
		
		
		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);

		hg.setShowLabels(false);
		
		zeroOrderGraph = new Graph(200, 200, "Zero Order", "t (s)", "C", hg, vg);
		zeroOrderGraph.setOffsetX(50);
		addComponent(zeroOrderGraph);
		
		zeroOrderData = new GraphDataSet("NO2", false, false);
		zeroOrderGraph.addDataSet(zeroOrderData);
		
		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);
		
		hg.setShowLabels(false);
		
		firstOrderGraph  = new Graph(200, 200, "First Order", "t (s)", "ln(Cf-C)", hg, vg);
		firstOrderGraph.setOffsetX(50);
		addComponent(firstOrderGraph);
		
		firstOrderData = new GraphDataSet("NO2", false, false);
		firstOrderGraph.addDataSet(firstOrderData);
		
		hg = new HorizontalGraduation(0, 1, 1, 0.1);
		vg = new VerticalGraduation(0, 1, 1, 0.1);
		
		hg.setShowLabels(false);
		
		secondOrderGraph  = new Graph(200, 200, "Second Order", "t (s)", "1/(Cf-C)", hg, vg);
		secondOrderGraph.setOffsetX(50);
		addComponent(secondOrderGraph);
		
		secondOrderData = new GraphDataSet("NO2", false, false);
		secondOrderGraph.addDataSet(secondOrderData);
		
		
		start(30);
		
	}

	@Override
	public void update() {
		
	}

	
	

	

}
