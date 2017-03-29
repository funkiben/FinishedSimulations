package temperature_equilibrium;


import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import lab.component.data.GraphDataSet;
import lab.component.data.Graph;


import lab.LabFrame;
import lab.component.container.Bulb;
import lab.component.container.ContentState;
import lab.component.swing.input.Button;
import lab.component.swing.input.field.TextField;
import lab.component.swing.input.slider.DoubleSlider;
import lab.component.swing.Label;
import lab.substance.Substance;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.Vector2;
import lab.util.VerticalGraduation;

public class TemperatureEquilibrium extends LabFrame implements KeyListener {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new TemperatureEquilibrium("Temperature Equilibrium Lab", 1100, 500);
		
	}
	
	private final Button reset;
	private final Graph s;
	private final Bulb b;
	private final Label l;
	private final Button button;

	private ArrayList<Substance> substanceArray = new ArrayList<Substance>();
	
	private Substance carbon = new Substance("C", 12, true, Color.black);
	private Substance water = new Substance("H2O", 8, true, Color.BLUE);
	private Substance monoxide = new Substance("CO", 0, false, Color.gray);
	private Substance hydrogen = new Substance("H2", 0, false, Color.red);
	
	
	private final double standardMultiplier = .0035;
	private double temperature;
	private double globalDifference;
	private double tempValue = 0;
	
	private double frame;

	private boolean simulationRunning = false;
	private boolean keyPressed = false;



	private DoubleSlider doubleSlider;
	private TextField tempField;

	private VerticalGraduation vg = new VerticalGraduation(0, 10, 2, 1);
	private HorizontalGraduation hg = new HorizontalGraduation(0, 500, 100, 50);

	public TemperatureEquilibrium(String name, int width, int height) {
		
		super(name, width, height);
		
		substanceArray.add(carbon);
		substanceArray.add(water);
		substanceArray.add(hydrogen);
		substanceArray.add(monoxide);
		
		reset = new Button(this.getFontMetrics(getFont()).stringWidth("Reset Simulation") + 10, 50, "Reset Simulation") {

			@Override
			public void doSomething() {
				
				for(int i = 0; i < substanceArray.size(); i++) {
					substanceArray.get(i).getSet().setShowName(false);
				}
				
				substanceArray.get(0).setAmount(12);
				substanceArray.get(1).setAmount(8);
				substanceArray.get(2).setAmount(0);
				substanceArray.get(3).setAmount(0);
				frame = 0;
				for(int i = 0; i < substanceArray.size(); i++) {
					substanceArray.get(i).getSet().clearPoints();
					substanceArray.get(i).getConnectedLabel().setText(substanceArray.get(i).getUnits() + " " + substanceArray.get(i).getName() + ": " + substanceArray.get(i).getAmount());
				}
				s.gethGraduation().setEnd(500);
				s.gethGraduation().setStart(0);
				temperature = 0;
				tempValue = 0;
				globalDifference = 0;
				doubleSlider.setValue(((2750-700)/2)+700);
				tempField.setText(doubleSlider.getValue() + "");
				
			}
		};
		
		frame = 0;
		
		l = new Label(this.getFontMetrics(getFont()).stringWidth("Temperature: _____K") + 10, 30, "Temperature: K");
		l.setOffsetY(80);
		l.setOffsetX(-190);
		
		b = new Bulb(200,200);
		
		b.setContentColor(Color.black);
		b.setContentState(ContentState.SOLID);
		b.setValue(carbon.getAmount()*2);
		b.setColor(Color.white);
		
		
		
		button = new Button(this.getFontMetrics(getFont()).stringWidth("Start Simulation") + 10, 50, "Start simulation") {
			@Override
			public void doSomething() {
				simulationRunning = !simulationRunning;
				if (!simulationRunning) {
					this.getJComponent().setText("Start simulation");
				} else {
					this.getJComponent().setText("Pause simulation");
					for(int i = 0; i < substanceArray.size(); i++) {
						substanceArray.get(i).getSet().setShowName(true);
					}
				}
			}
		};
		button.setOffsetY(25);
		button.setOffsetX(25);
		tempField = new TextField(100,"");
		tempField.setOffsetY(25);
		doubleSlider = new DoubleSlider(200, 700, 2750, 0.01f, 0){
			@Override
			public void onChange() {}
		};
		
		doubleSlider.setOffsetX(-215);
		doubleSlider.setOffsetY(100);
		
		s = new Graph(400, 400, "Moles of reactants", "s", "Moles", hg, vg);
		s.setOffsetX(150);

		

		for(int i = 0; i < substanceArray.size(); i++) {
			
			substanceArray.get(i).setUnits("Moles");
			substanceArray.get(i).setConnectedLabel(new Label(this.getFontMetrics(getFont()).stringWidth(substanceArray.get(i).getUnits() + " " + substanceArray.get(i).getName() + ": " + "________"), 50,substanceArray.get(i).getUnits() + " " + substanceArray.get(i).getName() + ": " + substanceArray.get(i).getAmount()));
			substanceArray.get(i).setSet(new GraphDataSet(substanceArray.get(i).getUnits() + " " + substanceArray.get(i).getName(), true, true));
			substanceArray.get(i).getSet().setColor(substanceArray.get(i).getColor());
			s.addDataSet(substanceArray.get(i).getSet());
		}
		
		tempField.getJComponent().addKeyListener(this);

		addComponent(b);
		
		addComponent(s);
		
		addComponent(button);
		addComponent(tempField);
		addComponent(doubleSlider);
		addComponent(l);
		
		for(int i = 0; i < substanceArray.size(); i++) {
			addComponent(substanceArray.get(i).getConnectedLabel());
		}
		
		addComponent(reset);
		
		substanceArray.get(2).getSet().setShowName(false);
		substanceArray.get(3).getSet().setName(substanceArray.get(3).getUnits() + " " + substanceArray.get(3).getName() + " & " + substanceArray.get(2).getName());
		substanceArray.get(0).getConnectedLabel().setOffsetY(200);
		substanceArray.get(0).getConnectedLabel().setOffsetX(-135);
		substanceArray.get(1).getConnectedLabel().setOffsetY(200);
		substanceArray.get(2).getConnectedLabel().setOffsetY(250);
		substanceArray.get(2).getConnectedLabel().setOffsetX(-240);
		substanceArray.get(3).getConnectedLabel().setOffsetY(250);
		for(int i = 0; i < substanceArray.size(); i++) {
			substanceArray.get(i).getSet().setShowName(false);
		}
		
		// ln(K2/K1)=-(deltaH/R)(1/T2-1/T1)
		start(60);
	}

	private static double lerp(double v1, double v2, float f) {
		return ((v2 - v1) * f + v1);
	}

	boolean areNegative;
	int howManyNegative;
	double prevTime;
	@Override
	public void update() {
		
		howManyNegative = 0;
		areNegative = false;
		
		if (tempField.getJComponent().hasFocus()) {
			
			if (keyPressed) {
				try {
					doubleSlider.setValue(tempField.getValue());
				} catch(Exception e) {
					
				}
				temperature = doubleSlider.getValue();
				doubleSlider.getJComponent().requestFocus();
				keyPressed = false;
			}
			
		} else if (doubleSlider.getJComponent().hasFocus()) {
			
			
			tempField.setText(doubleSlider.getValue() + "");
			temperature = doubleSlider.getValue();
			
			
		} else {
			
			temperature = doubleSlider.getValue();
			tempField.setText(doubleSlider.getValue() + "");
			
		}
		
		l.setText("Temperature: " + (int)(temperature+ 0.5) + "K");
		
		if (simulationRunning) {
			frame++;
			
			double globalDifferenceTarget = (temperature - tempValue) * standardMultiplier;
			globalDifference = lerp(globalDifference, globalDifferenceTarget, 0.03f);

			
			
			for(int k = 0; k < substanceArray.size(); k++) {
				if(substanceArray.get(k).getAmount()<0) {
					howManyNegative++;
					areNegative = true;
				}
			}
			
			for(int i = 0; i < substanceArray.size(); i++) {
				
				
				if(!areNegative) {
					
					b.setValue(carbon.getAmount() > 0 ? ((carbon.getAmount())*4) : 0);
					if(substanceArray.get(i).isReactant()) {
						
						substanceArray.get(i).setAmount(substanceArray.get(i).getAmount()-globalDifference);
						
					} else {
						
						substanceArray.get(i).setAmount(substanceArray.get(i).getAmount()+globalDifference);
						
					}
					
					substanceArray.get(i).getSet().addPoint(new Vector2(frame, substanceArray.get(i).getAmount() <=0 ? 0 : substanceArray.get(i).getAmount()));
					substanceArray.get(i).getConnectedLabel().setText(substanceArray.get(i).getUnits() + " " + substanceArray.get(i).getName() + ": " + (substanceArray.get(i).getAmount()<=0 ? 0 : SigFig.sigfigalize(substanceArray.get(i).getAmount(),5)));
				
				} else {
					
					if(substanceArray.get(i).isReactant()) {
						
						substanceArray.get(i).setAmount(substanceArray.get(i).getAmount()-globalDifference);
						
					} else {
						
						substanceArray.get(i).setAmount(substanceArray.get(i).getAmount()+globalDifference);
						
					}
					
					substanceArray.get(i).getSet().addPoint(new Vector2(frame, substanceArray.get(i).getSet().getPoints().get(substanceArray.get(i).getSet().getPoints().size()-1).getY()));
				
				}
			}
			
			if (frame == s.gethGraduation().getEnd()) {
				s.gethGraduation().setEnd(2 * s.gethGraduation().getEnd() - s.gethGraduation().getStart());
				s.gethGraduation().setStart((int) frame);
			}

			tempValue = temperature;
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			keyPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			keyPressed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}