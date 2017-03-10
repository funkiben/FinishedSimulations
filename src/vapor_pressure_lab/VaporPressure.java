package vapor_pressure_lab;

import java.text.DecimalFormat;

import lab.LabFrame;
import lab.component.ImageComponent;
import lab.component.LabComponent;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.swing.input.Button;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;

//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame {
	private static final long serialVersionUID = 1L;
	private LabFrame pressureTimeTableFrame;
	private LabFrame pressureTimeGraphFrame;
	private LabFrame tankFrame;
	private LabFrame equipmentFrame;
	private LabFrame instructionsFrame;
	// initial values from simulation
	private double temperature;
	private double dtemperature = 1;
	private double volume = 1;
	private double R = 8.314;
	private double pressure;
	private int time;
	private int dtime = 1;
	private double[] vaporPressure = new double[4];
	private double[] molarity = new double[4];
	// 20 30 40 60 120 130 140 160
	private double[] k = new double[8];
	private boolean running = false;
	private DecimalFormat round = new DecimalFormat("#.####");
	private GraphDataSet molarity20Set;
	private GraphDataSet molarity30Set;
	private GraphDataSet molarity40Set;
	private GraphDataSet molarity60Set;
	private GraphDataSet vaporPressure20Set;
	private GraphDataSet vaporPressure30Set;
	private GraphDataSet vaporPressure40Set;
	private GraphDataSet vaporPressure60Set;

	private Button play;
	private Button step;
	private Button reset;
	private Graph molarityGraph;
	private Graph vaporPressureGraph;
	private Graph pressureGraph;
	private Button showTank;
	private Button showEquipment;
	private Button showPressureGraph;
	private DataTable<Double> vaporPressureMolarityTable;
	private DataTable<Double> vaporPressureTimeTable;
	private ImageComponent equipment;

	public static void main(String args[]) {
		new VaporPressure("Vapor Pressure Lab", 800, 650);
	}

	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		getRoot().setLayout(LabComponent.FREE_FORM);

		k[0] = 0.0693;
		k[1] = 0.077;
		k[2] = 0.086625;
		k[3] = 0.1155;
		k[4] = 1.197833E-6;
		k[5] = 2.341675E-6;
		k[6] = 4.4528E-6;
		k[7] = 1.5284E-5;

		play = new Button(100, 25, "Play") {
			@Override
			public void doSomething() {
				if (running) {
					running = false;
				} else {
					running = true;
				}
			}
		};
		play.setOffset(30, 500);
		step = new Button(100, 25, "Step") {
			@Override
			public void doSomething() {
				if (!running) {
					stepSimulation();
				}
			}
		};
		step.setOffset(130, 500);
		reset = new Button(100, 25, "Reset") {
			@Override
			public void doSomething() {
				resetSimulation();
			}
		};
		reset.setOffset(230, 500);
		HorizontalGraduation timeGraduation = new HorizontalGraduation(0, 100, 20, 10);
		VerticalGraduation molarityGraduation = new VerticalGraduation(54, 55.6, .2, .1);
		VerticalGraduation vaporPressureGraduation = new VerticalGraduation(0, 25, 5, 2.5);
		molarityGraph = new Graph(275, 400, "Molarity vs Time 20C, 30C, 40C, 60C", "Time (s)", "Molarity H2O",
				molarityGraduation, timeGraduation);
		molarityGraph.setOffset(60, 50);
		molarityGraduation.setTextOffset(-32);
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time 20C, 30C, 40C, 60C", "Time (s)",
				"Vapor Pressure", vaporPressureGraduation, timeGraduation);
		vaporPressureGraph.setOffset(450, 50);
		vaporPressureMolarityTable = new DataTable<Double>(700, 75, 2, 4, DataTable.ROW_TITLES_ONLY);
		vaporPressureMolarityTable.setOffset(30, 550);
		vaporPressureMolarityTable.setRowTitle(0, "Vapor Pressure");
		vaporPressureMolarityTable.setRowTitle(1, "Molarity H2O");
		tankFrame = new LabFrame("Tank", 400, 300, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		tankFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		tankFrame.setVisible(false);
		tankFrame.start(30);
		showTank = new Button(90, 25, "Show Tank") {
			@Override
			public void doSomething() {
				tankFrame.setVisible(true);
			}
		};
		showTank.setOffset(345, 500);

		equipmentFrame = new LabFrame("Equipment", 400, 300, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		equipmentFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		equipmentFrame.setVisible(false);
		equipment = new ImageComponent(400, 300, "/vapor_pressure_lab/flask.gif");
		equipmentFrame.addComponent(equipment);
		equipmentFrame.start(0);
		showEquipment = new Button(125, 25, "Show Equipment") {
			@Override
			public void doSomething() {
				equipmentFrame.setVisible(true);
			}
		};
		showEquipment.setOffset(435, 500);
		pressureTimeGraphFrame = new LabFrame("Vapor Pressure vs. Time Graph", 500, 600, false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		pressureTimeGraphFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pressureTimeGraphFrame.setVisible(false);

		pressureTimeGraphFrame.start(30);
		showPressureGraph = new Button(205, 25, "Show Pressure vs. Time Graph") {
			@Override
			public void doSomething() {
				pressureTimeGraphFrame.setVisible(true);
			}
		};
		showPressureGraph.setOffset(560, 500);
		pressureTimeTableFrame = new LabFrame("Vapor Pressure vs. Time Table", 600, 375, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		pressureTimeTableFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		vaporPressureTimeTable = new DataTable<Double>(600, 375, 11, 5, DataTable.COLUMN_TITLES_ONLY);
		vaporPressureTimeTable.setColumnTitle(0, "Time (s)");
		vaporPressureTimeTable.setColumnTitle(1, "VP (kPa) @ 20C");
		vaporPressureTimeTable.setColumnTitle(2, "VP (kPa) @ 30C");
		vaporPressureTimeTable.setColumnTitle(3, "VP (kPa) @ 40C");
		vaporPressureTimeTable.setColumnTitle(4, "VP (kPa) @ 60C");
		pressureTimeTableFrame.addComponent(vaporPressureTimeTable);
		pressureTimeTableFrame.start(30);
		instructionsFrame = new LabFrame("Instructions", 500, 350, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		instructionsFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addComponent(molarityGraph, vaporPressureGraph, play, step, reset, vaporPressureMolarityTable, showTank,
				showEquipment, showPressureGraph);
		start(30);
		resetSimulation();
	}

	private void resetSimulation() {
		running = false;
		molarity20Set = new GraphDataSet("20C", true, true);
		molarity30Set = new GraphDataSet("30C", true, true);
		molarity40Set = new GraphDataSet("40C", true, true);
		molarity60Set = new GraphDataSet("60C", true, true);

		vaporPressure20Set = new GraphDataSet("20C", true, true);
		vaporPressure30Set = new GraphDataSet("30C", true, true);
		vaporPressure40Set = new GraphDataSet("40C", true, true);
		vaporPressure60Set = new GraphDataSet("60C", true, true);
		for (int i = 0; i < vaporPressure.length; i++) {
			vaporPressure[i] = 0;
		}
		molarity[0] = 55.409;
		molarity[1] = 55.268;
		molarity[2] = 55.076;
		molarity[3] = 54.576;
		time = 0;
		temperature = 0;
		vaporPressureMolarityTable.setRow(0, vaporPressure);
		vaporPressureMolarityTable.setRow(1, molarity);
		vaporPressureTimeTable.setAll(null);
		vaporPressureTimeTable.setCell(0, 0, time);
		vaporPressureTimeTable.setCell(1, 0, vaporPressure[0]);
		vaporPressureTimeTable.setCell(2, 0, vaporPressure[1]);
		vaporPressureTimeTable.setCell(3, 0, vaporPressure[2]);
		vaporPressureTimeTable.setCell(4, 0, vaporPressure[3]);
		molarityGraph.removeDataSet(molarity20Set.getName());
		molarityGraph.removeDataSet(molarity30Set.getName());
		molarityGraph.removeDataSet(molarity40Set.getName());
		molarityGraph.removeDataSet(molarity60Set.getName());
		vaporPressureGraph.removeDataSet(vaporPressure20Set.getName());
		vaporPressureGraph.removeDataSet(vaporPressure30Set.getName());
		vaporPressureGraph.removeDataSet(vaporPressure40Set.getName());
		vaporPressureGraph.removeDataSet(vaporPressure60Set.getName());

	}

	private void stepSimulation() {
		if (time > 101.0) {
			running = false;
		} else {
			time += dtime;
			vaporPressure[0] = (2.333 - vaporPressure[0] * Math.exp(-k[0] * time));
			molarity[0] = (molarity[0] * Math.exp(-k[4] * time));
			if (time > 101.0) {
				running = false;
			}
			if (time > 37.0) {
				molarity[0] = 55.4082;
			}
			vaporPressure[1] = (4.234 - vaporPressure[1] * Math.exp(-k[1] * time));
			molarity[1] = (molarity[1] * Math.exp(-k[5] * time));
			if (time > 20.0) {
				molarity[1] = 55.2661;
			}
			vaporPressure[2] = (7.367 - vaporPressure[2] * Math.exp(-k[2] * time));
			molarity[2] = (molarity[2] * Math.exp(-k[6] * time));
			if (time > 18.0) {
				molarity[2] = 55.0728;
			}
			vaporPressure[3] = (19.993 - vaporPressure[3] * Math.exp(-k[3] * time));
			molarity[3] = (molarity[3] * Math.exp(-k[7] * time));
			if (time > 6.0) {
				molarity[3] = 54.5686;
			}

			for (int i = 0; i < 4; i++) {
				vaporPressureMolarityTable.setCell(i, 0, Double.parseDouble(round.format(vaporPressure[i])));
				vaporPressureMolarityTable.setCell(i, 1, Double.parseDouble(round.format(molarity[i])));
			}
			if (time % 10 == 0) {
				vaporPressureTimeTable.setCell(0, time / 10, time);
				vaporPressureTimeTable.setCell(1, time / 10, Double.parseDouble(round.format(vaporPressure[0])));
				vaporPressureTimeTable.setCell(2, time / 10, Double.parseDouble(round.format(vaporPressure[1])));
				vaporPressureTimeTable.setCell(3, time / 10, Double.parseDouble(round.format(vaporPressure[2])));
				vaporPressureTimeTable.setCell(4, time / 10, Double.parseDouble(round.format(vaporPressure[3])));
			}

			molarity20Set.addPoint(time, molarity[0]);
			molarity30Set.addPoint(time, molarity[1]);
			molarity40Set.addPoint(time, molarity[2]);
			molarity60Set.addPoint(time, molarity[3]);
			molarityGraph.addDataSet(molarity20Set);
			molarityGraph.addDataSet(molarity30Set);
			molarityGraph.addDataSet(molarity40Set);
			molarityGraph.addDataSet(molarity60Set);

			vaporPressure20Set.addPoint(time, vaporPressure[0]);
			vaporPressure30Set.addPoint(time, vaporPressure[1]);
			vaporPressure40Set.addPoint(time, vaporPressure[2]);
			vaporPressure60Set.addPoint(time, vaporPressure[3]);
			vaporPressureGraph.addDataSet(vaporPressure20Set);
			vaporPressureGraph.addDataSet(vaporPressure30Set);
			vaporPressureGraph.addDataSet(vaporPressure40Set);
			vaporPressureGraph.addDataSet(vaporPressure60Set);
		}

		// pressure = 6.112 * Math.exp((17.62 * temperature / (243.12 +
		// temperature)) / 10.0);
		// if(temperature > 99.3352){
		// pressure = 101.325;
		// temperature = 100.0;
		// }

	}

	@Override
	public void update() {
		if (running) {
			play.setText("Pause");
			stepSimulation();
		} else {
			play.setText("Play");
		}
	}

}
