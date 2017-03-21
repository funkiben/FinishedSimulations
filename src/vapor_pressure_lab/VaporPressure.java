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
import lab.component.swing.input.DoubleField;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

//Vapor pressure simulation lab from Ms. Lund Easy Java
public class VaporPressure extends LabFrame {
	private static final long serialVersionUID = 1L;

	// constants
	private static final double[] K = { 0.0693, 0.077, 0.086625, 0.1155, 1.197833E-6, 2.341675E-6, 4.4528E-6,
			1.5284E-5 };
	private static final int CHANGE_IN_TIME = 1;
	private static final int SIG_FIGS = 5;

	// windows
	private final LabFrame vaporPressureTimeTableFrame;
	private final LabFrame vaporPressureTemperatureGraphFrame;
	private final LabFrame tankFrame;
	private final LabFrame equipmentFrame;
	private final LabFrame instructionsFrame;

	// initial values from simulation
	private int time;
	private double[] vaporPressure = new double[4];
	private double[] molarity = new double[4];
	private boolean running = false;
	private GraphDataSet molarity20;
	private GraphDataSet molarity30;
	private GraphDataSet molarity40;
	private GraphDataSet molarity60;
	private GraphDataSet vaporPressure20;
	private GraphDataSet vaporPressure30;
	private GraphDataSet vaporPressure40;
	private GraphDataSet vaporPressure60;
	private GraphDataSet vaporPressureTemperature;

	//components
	private final Button play;
	private final Button step;
	private final Button reset;
	private final Button showTank;
	private final Button showEquipment;
	private final Button showPressureGraph;
	private final Button plot;
	private DataTable<String> vaporPressureMolarityTable;
	private DataTable<String> vaporPressureTimeTable;
	private final DoubleField inputTemperature;
	private final Graph molarityGraph;
	private final Graph vaporPressureGraph;
	private final Graph vaporPressureTemperatureGraph;
	private final ImageComponent equipment;
	private final Label outputVaporPressure;
	private final Label temperatureLabel;
	private final ScrollLabel instructions;

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
		molarityGraph = new Graph(275, 400, "Molarity vs Time", "Time (s)", "Molarity H2O (mol/L)", timeGraduation,
				molarityGraduation);
		molarityGraph.setOffset(60, 50);
		molarityGraph.setYLabelOffset(32);
		molarityGraduation.setTextOffset(-32);
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time", "Time (s)", "Vapor Pressure (kPa)",
				timeGraduation, vaporPressureGraduation);
		vaporPressureGraph.setOffset(450, 50);
		vaporPressureGraph.setYLabelOffset(32);
		vaporPressureMolarityTable = new DataTable<String>(700, 75, 2, 4, DataTable.ROW_TITLES_ONLY) {
			@Override
			public String getString(Double value) {
				return SigFig.sigfigalize(value, SIG_FIGS);
			}
		};
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
		vaporPressureTemperatureGraphFrame = new LabFrame("Vapor Pressure vs Temperature Graph", 550, 630, false) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		vaporPressureTemperatureGraphFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		vaporPressureTemperatureGraphFrame.setVisible(false);
		vaporPressureTemperatureGraphFrame.getRoot().setLayout(LabComponent.FREE_FORM);
		vaporPressureGraduation = new VerticalGraduation(0, 105, 10, 5);
		HorizontalGraduation temperatureGraduation = new HorizontalGraduation(0, 100, 20, 10);
		vaporPressureTemperatureGraph = new Graph(420, 500, "Vapor Pressure vs Temperature", "Temperature (C)",
				"Vapor Pressure (kPa)", temperatureGraduation, vaporPressureGraduation);
		vaporPressureTemperatureGraph.setYLabelOffset(70);
		temperatureLabel = new Label(100, 25, "Temperature");
		temperatureLabel.setOffset(20, 565);
		inputTemperature = new DoubleField(85, 0, 100, 5);
		inputTemperature.setOffset(20, 585);
		plot = new Button(100, 25, "Plot") {
			@Override
			public void doSomething() {
				plotTemperature();
			}
		};
		plot.setOffset(230, 575);
		outputVaporPressure = new Label(300, 25, "Vapor Pressure: ");
		outputVaporPressure.setOffset(340, 575);
		vaporPressureTemperatureGraphFrame.addComponent(vaporPressureTemperatureGraph, inputTemperature, plot,
				outputVaporPressure, temperatureLabel);
		vaporPressureTemperatureGraphFrame.start(30);
		showPressureGraph = new Button(205, 25, "Show Pressure vs Temperature") {
			@Override
			public void doSomething() {
				vaporPressureTemperatureGraphFrame.setVisible(true);
			}
		};
		showPressureGraph.setOffset(560, 500);
		vaporPressureTimeTableFrame = new LabFrame("Vapor Pressure vs Time Table", 600, 375, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		vaporPressureTimeTableFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		vaporPressureTimeTable = new DataTable<String>(600, 375, 11, 5, DataTable.COLUMN_TITLES_ONLY);
		vaporPressureTimeTable.setColumnTitle(0, "Time (s)");
		vaporPressureTimeTable.setColumnTitle(1, "VP (kPa) @ 20C");
		vaporPressureTimeTable.setColumnTitle(2, "VP (kPa) @ 30C");
		vaporPressureTimeTable.setColumnTitle(3, "VP (kPa) @ 40C");
		vaporPressureTimeTable.setColumnTitle(4, "VP (kPa) @ 60C");
		vaporPressureTimeTableFrame.addComponent(vaporPressureTimeTable);
		vaporPressureTimeTableFrame.start(30);
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

	private void plotTemperature() {
		if (inputTemperature.hasInput()) {
			double vp;
			double temperature = inputTemperature.getValue();
			vp = 6.122 * Math.exp((17.62 * temperature / (243.12 + temperature))) / 10;
			if (temperature > 99.3352) {
				vp = 101.325;
				temperature = 100;
			}
			vaporPressureTemperature.addPoint(vp, temperature);
			outputVaporPressure.setText("Vapor Pressure: " + SigFig.sigfigalize(vp, 5) + " kPa");
		}
	}

	private void resetSimulation() {
		running = false;
		molarity20 = new GraphDataSet("20C", true, true);
		molarity30 = new GraphDataSet("30C", true, true);
		molarity40 = new GraphDataSet("40C", true, true);
		molarity60 = new GraphDataSet("60C", true, true);
		vaporPressureTemperature = new GraphDataSet("", false, false);
		vaporPressureTemperatureGraph.addDataSet(vaporPressureTemperature);
		vaporPressure20 = new GraphDataSet("20C", true, true);
		vaporPressure30 = new GraphDataSet("30C", true, true);
		vaporPressure40 = new GraphDataSet("40C", true, true);
		vaporPressure60 = new GraphDataSet("60C", true, true);
		for (int i = 0; i < vaporPressure.length; i++) {
			vaporPressure[i] = 0;
		}
		molarity[0] = 55.4082;
		molarity[1] = 55.2661;
		molarity[2] = 55.0728;
		molarity[3] = 54.5686;
		time = 0;
		vaporPressureMolarityTable.setRow(0, vaporPressure);
		vaporPressureMolarityTable.setRow(1, molarity);
		vaporPressureTimeTable.setAll(null);
		vaporPressureTimeTable.setCell(0, 0, time);
		vaporPressureTimeTable.setCell(1, 0, vaporPressure[0]);
		vaporPressureTimeTable.setCell(2, 0, vaporPressure[1]);
		vaporPressureTimeTable.setCell(3, 0, vaporPressure[2]);
		vaporPressureTimeTable.setCell(4, 0, vaporPressure[3]);
		molarityGraph.removeDataSet(molarity20.getName());
		molarityGraph.removeDataSet(molarity30.getName());
		molarityGraph.removeDataSet(molarity40.getName());
		molarityGraph.removeDataSet(molarity60.getName());
		vaporPressureGraph.removeDataSet(vaporPressure20.getName());
		vaporPressureGraph.removeDataSet(vaporPressure30.getName());
		vaporPressureGraph.removeDataSet(vaporPressure40.getName());
		vaporPressureGraph.removeDataSet(vaporPressure60.getName());
		molarityGraph.addDataSet(molarity20);
		molarityGraph.addDataSet(molarity30);
		molarityGraph.addDataSet(molarity40);
		molarityGraph.addDataSet(molarity60);
		vaporPressureGraph.addDataSet(vaporPressure20);
		vaporPressureGraph.addDataSet(vaporPressure30);
		vaporPressureGraph.addDataSet(vaporPressure40);
		vaporPressureGraph.addDataSet(vaporPressure60);
	}

	private void stepSimulation() {
		if (time > 100) {
			running = false;
		} else {
			time += CHANGE_IN_TIME;
			vaporPressure[0] = (2.333 - 2.333 * Math.exp(-K[0] * time));
			vaporPressure[1] = (4.234 - 4.234 * Math.exp(-K[1] * time));
			vaporPressure[2] = (7.367 - 7.367 * Math.exp(-K[2] * time));
			vaporPressure[3] = (19.993 - 19.993 * Math.exp(-K[3] * time));
			for (int i = 0; i < 4; i++) {
				vaporPressureMolarityTable.setCell(i, 0, SigFig.sigfigalize(vaporPressure[i], SIG_FIGS));
				vaporPressureMolarityTable.setCell(i, 1, SigFig.sigfigalize(molarity[i], SIG_FIGS));
			}
			if (time % 10 == 0) {
				vaporPressureTimeTable.setCell(0, time / 10, time);
				vaporPressureTimeTable.setCell(1, time / 10, SigFig.sigfigalize(vaporPressure[0], SIG_FIGS));
				vaporPressureTimeTable.setCell(2, time / 10, SigFig.sigfigalize(vaporPressure[1], SIG_FIGS));
				vaporPressureTimeTable.setCell(3, time / 10, SigFig.sigfigalize(vaporPressure[2], SIG_FIGS));
				vaporPressureTimeTable.setCell(4, time / 10, SigFig.sigfigalize(vaporPressure[3], SIG_FIGS));
			}
			molarity20.addPoint(time, molarity[0]);
			molarity30.addPoint(time, molarity[1]);
			molarity40.addPoint(time, molarity[2]);
			molarity60.addPoint(time, molarity[3]);
			vaporPressure20.addPoint(time, vaporPressure[0]);
			vaporPressure30.addPoint(time, vaporPressure[1]);
			vaporPressure40.addPoint(time, vaporPressure[2]);
			vaporPressure60.addPoint(time, vaporPressure[3]);
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
