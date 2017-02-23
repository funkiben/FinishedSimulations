package simulation;

import lab.LabFrame;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.swing.input.Button;
import lab.component.swing.input.CheckBox;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;


//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame{
	private static final long serialVersionUID = 1L;
	//initial values from simulation
	int temperature = 0;
	int dtemperature = 1;
	double volume = 1;
	double R = 8.314;
	int time = 0;
	int dtime = 1;
	

	Graph molarity;
	Graph vaporPressure;
	Graph pressure;
	CheckBox showTank;
	CheckBox showEquipment;
	CheckBox showPressureGraph;
	DataTable<Double> vaporPressureMolarity;
	DataTable<Double> vaporPressureTime;
	
	public static void main(String args[]){
		new VaporPressure("Vapor Pressure Lab", 700, 800);
	}
	

	Button play;
	Button step;
	Button reset;

	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		play = new Button(100, 25, "Play"){
			@Override
			public void doSomething() {
				
			}
		};
		step = new Button(100, 25, "Step"){
			@Override
			public void doSomething() {
				
			}
		};
		reset = new Button(100, 25, "Reset"){
			@Override
			public void doSomething() {
				
			}
		};
		HorizontalGraduation horizontalGraduation = new HorizontalGraduation(0, 100, 20, 10);
		VerticalGraduation verticalGraduation = new VerticalGraduation(54, 55.8, .2, .1);
		molarity = new Graph(750, 500, "Molarity vs Time 20C, 30C, 40C, 60C", "Molarity H2O (mol/L)", "Time (s)", verticalGraduation, horizontalGraduation);
		addComponent(molarity, play, step, reset);
		start(30);
	}

	@Override
	public void update() {

		
	}

}
