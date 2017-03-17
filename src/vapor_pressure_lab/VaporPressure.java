package vapor_pressure_lab;

import java.text.DecimalFormat;

import lab.LabFrame;
import lab.component.ImageComponent;
import lab.component.LabComponent;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.swing.Label;
import lab.component.swing.ScrollLabel;
import lab.component.swing.input.Button;
import lab.component.swing.input.TextField;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;

//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame {
	private static final long serialVersionUID = 1L;
	private LabFrame pressureTimeTableFrame;
	private LabFrame vaporPressureTemperatureGraphFrame;
	private LabFrame tankFrame;
	private LabFrame equipmentFrame;
	private LabFrame instructionsFrame;
	// initial values from simulation
	private double temperature;
	private int time;
	private int dtime = 1;
	private double[] vaporPressure = new double[4];
	private double[] molarity = new double[4];
	// 20 30 40 60 120 130 140 160
	private double[] k = { 0.0693, 0.077, 0.086625, 0.1155, 1.197833E-6, 2.341675E-6, 4.4528E-6, 1.5284E-5 };
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
	private Graph vaporPressureTemperatureGraph;
	private Button showTank;
	private Button showEquipment;
	private Button showPressureGraph;
	private Button plot;
	private TextField inputTemperature;
	private Label outputVaporPressure;
	private DataTable<Double> vaporPressureMolarityTable;
	private DataTable<Double> vaporPressureTimeTable;
	private ImageComponent equipment;
	private ScrollLabel instructions;

	public static void main(String args[]) {
		new VaporPressure("Vapor Pressure Lab", 800, 650);
	}

	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		getRoot().setLayout(LabComponent.FREE_FORM);

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
		molarityGraph = new Graph(275, 400, "Molarity vs Time", "Time (s)", "Molarity H2O (mol/L)", molarityGraduation,
				timeGraduation);
		molarityGraph.setOffset(60, 50);
		molarityGraph.setYLabelOffset(32);
		molarityGraduation.setTextOffset(-32);
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time", "Time (s)", "Vapor Pressure (kPa)",
				vaporPressureGraduation, timeGraduation);
		vaporPressureGraph.setOffset(450, 50);
		vaporPressureGraph.setYLabelOffset(32);
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
		vaporPressureTemperatureGraphFrame = new LabFrame("Vapor Pressure vs Temperature Graph", 500, 600, false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		vaporPressureTemperatureGraphFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		vaporPressureTemperatureGraphFrame.setVisible(false);
		HorizontalGraduation temperatureGraduation = new HorizontalGraduation(0, 100, 20, 10);
		vaporPressureTemperatureGraph = new Graph(400, 400, "Vapor Pressure vs Temperature", "Vapor Pressure (kPa)",
				"Temperature (C)", vaporPressureGraduation, temperatureGraduation);

		vaporPressureTemperatureGraphFrame.addComponent(vaporPressureTemperatureGraph);
		vaporPressureTemperatureGraphFrame.start(30);
		showPressureGraph = new Button(205, 25, "Show Pressure vs Temperature") {
			@Override
			public void doSomething() {
				vaporPressureTemperatureGraphFrame.setVisible(true);
			}
		};
		showPressureGraph.setOffset(560, 500);
		pressureTimeTableFrame = new LabFrame("Vapor Pressure vs Time Table", 600, 375, true) {
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
		instructionsFrame.setResizable(false);
		instructions = new ScrollLabel(500, 350, "/vapor_pressure_lab/instructions.txt");
		instructions.setHoriztonalScrollBarPolicy(ScrollLabel.HORIZONTAL_SCROLLBAR_NEVER);
		instructions.setFontSize(13);
		instructionsFrame.addComponent(instructions);
		instructionsFrame.start(0);

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
		molarity[0] = 55.4082;
		molarity[1] = 55.2661;
		molarity[2] = 55.0728;
		molarity[3] = 54.5686;
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
		molarityGraph.addDataSet(molarity20Set);
		molarityGraph.addDataSet(molarity30Set);
		molarityGraph.addDataSet(molarity40Set);
		molarityGraph.addDataSet(molarity60Set);
		vaporPressureGraph.addDataSet(vaporPressure20Set);
		vaporPressureGraph.addDataSet(vaporPressure30Set);
		vaporPressureGraph.addDataSet(vaporPressure40Set);
		vaporPressureGraph.addDataSet(vaporPressure60Set);
	}

	private void stepSimulation() {
		if (time > 100 || temperature > 101) {
			running = false;
		} else {
			time += dtime;
			vaporPressure[0] = (2.333 - 2.333 * Math.exp(-k[0] * time));
			vaporPressure[1] = (4.234 - 4.234 * Math.exp(-k[1] * time));
			vaporPressure[2] = (7.367 - 7.367 * Math.exp(-k[2] * time));
			vaporPressure[3] = (19.993 - 19.993 * Math.exp(-k[3] * time));
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
			vaporPressure20Set.addPoint(time, vaporPressure[0]);
			vaporPressure30Set.addPoint(time, vaporPressure[1]);
			vaporPressure40Set.addPoint(time, vaporPressure[2]);
			vaporPressure60Set.addPoint(time, vaporPressure[3]);
		}

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
