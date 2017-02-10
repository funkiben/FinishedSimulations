package simulation;

import lab.LabFrame;
import lab.component.HorizontalGraduation;
import lab.component.VerticalGraduation;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.swing.input.Button;
import lab.component.swing.input.CheckBox;

//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame {
	private static final long serialVersionUID = 1L;
	// initial values from simulation
	private int temperature = 0;
	private int dtemperature = 1;
	private double volume = 1;
	private double R = 8.314;
	private int time = 0;
	private int dtime = 1;

	private Button play;
	private Button step;
	private Button reset;
	private Graph molarity;
	private Graph vaporPressure;
	private Graph pressure;
	private CheckBox showTank;
	private CheckBox showEquipment;
	private CheckBox showPressureGraph;
	private DataTable<Double> vaporPressureMolarity;
	private DataTable<Double> vaporPressureTime;

	public static void main(String args[]) {
		new VaporPressure("Vapor Pressure Lab", 930, 800);
	}

	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		getRoot().setLayout(0);
		play = new Button(100, 25, "Play") {
			@Override
			public void doSomething() {

			}
		};
		play.setOffset(60, 500);
		step = new Button(100, 25, "Step") {
			@Override
			public void doSomething() {

			}
		};
		step.setOffset(160, 500);
		reset = new Button(100, 25, "Reset") {
			@Override
			public void doSomething() {

			}
		};
		reset.setOffset(260, 500);
		HorizontalGraduation timeGraduation = new HorizontalGraduation(0, 100, 20, 10);
		VerticalGraduation molarityGraduation = new VerticalGraduation(54, 55.8, .2, .1);
		VerticalGraduation vaporPressureGraduation = new VerticalGraduation(0, 25, 5, 2.5);
		molarity = new Graph(300, 400, "Molarity vs Time 20C, 30C, 40C, 60C", "Molarity H2O (mol/L)", "Time (s)",
				molarityGraduation, timeGraduation);
		molarity.setOffset(60, 50);
		molarityGraduation.setTextOffset(-32);
		vaporPressure = new Graph(300, 400, "Vapor Pressure vs Time 20C, 30C, 40C, 60C", "Vapor Pressure", "Time (s)",
				vaporPressureGraduation, timeGraduation);
		vaporPressure.setOffset(500, 50);
		addComponent(molarity, vaporPressure, play, step, reset);
		start(30);
	}

	@Override
	public void update() {

	}

}