package vapor_pressure_lab;

import lab.LabFrame;
import lab.component.ImageComponent;
import lab.component.LabComponent;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.swing.input.Button;
import lab.component.swing.input.CheckBox;
import lab.util.HorizontalGraduation;
import lab.util.VerticalGraduation;

//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame {
	private static final long serialVersionUID = 1L;
	private LabFrame pressureTimeTableFrame;
	private LabFrame pressureTimeGraphFrame;
	private LabFrame tankFrame;
	private LabFrame equipmentFrame;
	// initial values from simulation
	private int temperature = 0;
	private int dtemperature = 1;
	private double volume = 1;
	private double R = 8.314;
	private int time = 0;
	private int dtime = 1;
	private double[] vaporPressure = new double[4];
	private double[] molarity = new double[4];

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
		play = new Button(100, 25, "Play") {
			@Override
			public void doSomething() {

			}
		};
		play.setOffset(30, 500);
		step = new Button(100, 25, "Step") {
			@Override
			public void doSomething() {

			}
		};
		step.setOffset(130, 500);
		reset = new Button(100, 25, "Reset") {
			@Override
			public void doSomething() {

			}
		};
		reset.setOffset(230, 500);
		HorizontalGraduation timeGraduation = new HorizontalGraduation(0, 100, 20, 10);
		VerticalGraduation molarityGraduation = new VerticalGraduation(54, 55.6, .2, .1);
		VerticalGraduation vaporPressureGraduation = new VerticalGraduation(0, 25, 5, 2.5);
		molarityGraph = new Graph(275, 400, "Molarity vs Time 20C, 30C, 40C, 60C", "Molarity H2O (mol/L)", "Time (s)",
				molarityGraduation, timeGraduation);
		molarityGraph.setOffset(60, 50);
		molarityGraduation.setTextOffset(-32);
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time 20C, 30C, 40C, 60C", "Vapor Pressure",
				"Time (s)", vaporPressureGraduation, timeGraduation);
		vaporPressureGraph.setOffset(450, 50);
		vaporPressureMolarityTable = new DataTable<Double>(700, 75, 2, 4, DataTable.ROW_TITLES_ONLY);
		vaporPressureMolarityTable.setOffset(30, 550);
		for (int i = 0; i < vaporPressure.length; i++) {
			vaporPressure[i] = 0;
		}
		molarity[0] = 55.409;
		molarity[1] = 55.268;
		molarity[2] = 55.076;
		molarity[3] = 54.576;
		vaporPressureMolarityTable.setRowTitle(0, "Vapor Pressure");
		vaporPressureMolarityTable.setRowTitle(1, "Molarity H2O");
		vaporPressureMolarityTable.setRow(0, vaporPressure);
		vaporPressureMolarityTable.setRow(1, molarity);
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
		addComponent(molarityGraph, vaporPressureGraph, play, step, reset, vaporPressureMolarityTable, showTank,
				showEquipment, showPressureGraph);
		start(30);
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
	}

	@Override
	public void update() {

	}

}