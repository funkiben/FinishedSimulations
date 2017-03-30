package vapor_pressure_lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import lab.LabFrame;
import lab.component.ImageComponent;
import lab.component.LabComponent;
import lab.component.data.DataTable;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.swing.Label;
import lab.component.swing.ScrollLabel;
import lab.component.swing.input.Button;
import lab.component.swing.input.MenuComponent;
import lab.component.swing.input.field.DoubleField;
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

	// create variables for calculation
	private int time;
	private double[] vaporPressure = new double[4];
	private double[] molarity = new double[4];
	private boolean running = false;
	private GraphDataSet[] molaritySet = new GraphDataSet[4];
	private GraphDataSet[] vaporPressureSet = new GraphDataSet[4];
	private GraphDataSet vaporPressureTemperature;
	private int k;

	// components
	private final Button play;
	private final Button step;
	private final Button reset;
	private final Button showTank;
	private final Button showEquipment;
	private final Button showPressureGraph;
	private final Button plot;
	private final DataTable<Double> vaporPressureMolarityTable;
	private final DataTable<Double> vaporPressureTimeTable;
	private final DoubleField inputTemperature;
	private final Graph molarityGraph;
	private final Graph vaporPressureGraph;
	private final Graph vaporPressureTemperatureGraph;
	private final HorizontalGraduation timeGraduation;
	private final HorizontalGraduation temperatureGraduation;
	private final ImageComponent equipment;
	private final Label outputVaporPressure;
	private final Label temperatureLabel;
	private final Label[] tankTemp = { new Label(30, 20, "20C"), new Label(30, 20, "30C"), new Label(30, 20, "40C"),
			new Label(30, 20, "60C") };
	private final MenuComponent menu;
	private final ScrollLabel instructions;
	private final VerticalGraduation molarityGraduation;
	private VerticalGraduation vaporPressureGraduation;
	private final WaterTank[] waterTank = new WaterTank[4];

	// start simulation
	public static void main(String args[]) {
		new VaporPressure("Vapor Pressure Lab", 800, 650);
	}

	// create windows and components
	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		getRoot().setLayout(LabComponent.FREE_FORM);

		// play button
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

		// step button
		step = new Button(100, 25, "Step") {
			@Override
			public void doSomething() {
				if (!running) {
					stepSimulation();
				}
			}
		};
		step.setOffset(130, 500);

		// reset button
		reset = new Button(100, 25, "Reset") {
			@Override
			public void doSomething() {
				resetSimulation();
			}
		};
		reset.setOffset(230, 500);

		// create molarity vs time graph
		timeGraduation = new HorizontalGraduation(0, 100, 20, 10);
		molarityGraduation = new VerticalGraduation(54, 55.6, .2, .1);
		molarityGraph = new Graph(275, 400, "Molarity vs Time", "Time (s)", "Molarity H2O (mol/L)", timeGraduation,
				molarityGraduation);
		molarityGraph.setOffset(60, 50);
		molarityGraph.setYLabelOffset(32);
		molarityGraduation.setTextOffset(-32);

		// create vapor pressure vs time graph
		vaporPressureGraduation = new VerticalGraduation(0, 25, 5, 2.5);
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time", "Time (s)", "Vapor Pressure (kPa)",
				timeGraduation, vaporPressureGraduation);
		vaporPressureGraph.setOffset(450, 50);
		vaporPressureGraph.setYLabelOffset(32);

		// create vapor pressure molarity table
		vaporPressureMolarityTable = new DataTable<Double>(700, 75, 2, 4, DataTable.ROW_TITLES_ONLY) {
			@Override
			public String getString(Double value) {
				return SigFig.sigfigalize(value, SIG_FIGS);
			}
		};
		vaporPressureMolarityTable.setOffset(30, 550);
		vaporPressureMolarityTable.setRowTitle(0, "Vapor Pressure");
		vaporPressureMolarityTable.setRowTitle(1, "Molarity H2O");

		// create tank frame
		tankFrame = new LabFrame("Tanks", 250, 720, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		tankFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		tankFrame.getRoot().setLayout(LabComponent.FREE_FORM);
		for (int i = 0; i < waterTank.length; i++) {
			waterTank[i] = new WaterTank(175, 175, 30, 14, .5, 2);
			waterTank[i].setOffset(60, 180 * i);
			tankTemp[i].setOffset(15, 180 * i + (175 / 2));
			tankFrame.addComponent(waterTank[i], tankTemp[i]);
		}
		tankFrame.start(30);
		showTank = new Button(90, 25, "Show Tank") {
			@Override
			public void doSomething() {
				tankFrame.setVisible(true);
			}
		};
		showTank.setOffset(345, 500);

		// create equipment frame
		equipmentFrame = new LabFrame("Equipment", 400, 300, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		equipmentFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

		// create vapor pressure vs temperature frame
		vaporPressureTemperatureGraphFrame = new LabFrame("Vapor Pressure vs Temperature Graph", 550, 630, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		vaporPressureTemperatureGraphFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		vaporPressureTemperatureGraphFrame.getRoot().setLayout(LabComponent.FREE_FORM);
		vaporPressureGraduation = new VerticalGraduation(0, 105, 10, 5);
		temperatureGraduation = new HorizontalGraduation(0, 100, 20, 10);
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

		// create vapor pressure time table frame
		vaporPressureTimeTableFrame = new LabFrame("Vapor Pressure vs Time Table", 600, 375, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		vaporPressureTimeTableFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		vaporPressureTimeTable = new DataTable<Double>(600, 375, 11, 5, DataTable.COLUMN_TITLES_ONLY) {
			@Override
			public String getString(Double value) {
				return SigFig.sigfigalize(value, SIG_FIGS);
			}
		};
		vaporPressureTimeTable.setColumnTitle(0, "Time (s)");
		vaporPressureTimeTable.setColumnTitle(1, "VP (kPa) @ 20C");
		vaporPressureTimeTable.setColumnTitle(2, "VP (kPa) @ 30C");
		vaporPressureTimeTable.setColumnTitle(3, "VP (kPa) @ 40C");
		vaporPressureTimeTable.setColumnTitle(4, "VP (kPa) @ 60C");
		vaporPressureTimeTableFrame.addComponent(vaporPressureTimeTable);
		vaporPressureTimeTableFrame.start(30);

		// create instructions frame
		instructionsFrame = new LabFrame("Instructions", 500, 350, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		instructionsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		instructionsFrame.setResizable(false);
		instructions = new ScrollLabel(500, 350, "/vapor_pressure_lab/instructions.txt");
		instructions.setHoriztonalScrollBarPolicy(ScrollLabel.HORIZONTAL_SCROLLBAR_NEVER);
		instructions.setFontSize(13);
		instructionsFrame.addComponent(instructions);
		instructionsFrame.start(0);

		// menu
		menu = new MenuComponent(getRoot().getWidth(), 25);
		menu.addMenu("Control");
		menu.addMenuItem("Play", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (running) {
					running = false;
				} else {
					running = true;
				}
			}
		});
		menu.addMenuItem("Step", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!running) {
					stepSimulation();
				}
			}
		});
		menu.addMenuItem("Reset", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetSimulation();
			}
		});
		menu.addMenu("View");
		menu.addRadioButtonMenuItem("Show Vapor Pressure vs Time Table", "View", true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vaporPressureTimeTableFrame.isVisible()) {
					vaporPressureTimeTableFrame.dispose();
				} else {
					vaporPressureTimeTableFrame.setVisible(true);
				}
			}
		});
		menu.addRadioButtonMenuItem("Show Tank", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tankFrame.isVisible()) {
					tankFrame.dispose();
				} else {
					tankFrame.setVisible(true);
				}
			}
		});
		menu.addRadioButtonMenuItem("Show Equipment", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (equipmentFrame.isVisible()) {
					equipmentFrame.dispose();
				} else {
					equipmentFrame.setVisible(true);
				}
			}
		});
		menu.addRadioButtonMenuItem("Show Pressure vs Temperature", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vaporPressureTemperatureGraphFrame.isVisible()) {
					vaporPressureTemperatureGraphFrame.dispose();
				} else {
					vaporPressureTemperatureGraphFrame.setVisible(true);
				}
			}
		});
		menu.addMenu("Help");
		menu.addMenuItem("Instructions", "Help", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instructionsFrame.setVisible(true);
			}
		});

		// create and add data sets for graphs
		molaritySet[0] = new GraphDataSet("20C", true, true);
		molaritySet[1] = new GraphDataSet("30C", true, true);
		molaritySet[2] = new GraphDataSet("40C", true, true);
		molaritySet[3] = new GraphDataSet("60C", true, true);
		molarityGraph.addDataSet(molaritySet);
		vaporPressureSet[0] = new GraphDataSet("20C", true, true);
		vaporPressureSet[1] = new GraphDataSet("30C", true, true);
		vaporPressureSet[2] = new GraphDataSet("40C", true, true);
		vaporPressureSet[3] = new GraphDataSet("60C", true, true);
		vaporPressureGraph.addDataSet(vaporPressureSet);
		vaporPressureTemperature = new GraphDataSet("", false, false);
		vaporPressureTemperatureGraph.addDataSet(vaporPressureTemperature);

		// create main frame and set up simulation
		addComponent(molarityGraph, vaporPressureGraph, play, step, reset, vaporPressureMolarityTable, showTank,
				showEquipment, showPressureGraph, menu);
		start(30);
		resetSimulation();
	}

	// plot vapor pressure value for a given temperature value
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

	// resets all simulation values
	private void resetSimulation() {
		running = false;
		for (GraphDataSet set : molaritySet) {
			set.clearPoints();
		}
		for (GraphDataSet set : vaporPressureSet) {
			set.clearPoints();
		}
		vaporPressureTemperature.clearPoints();
		time = 0;
		vaporPressureTimeTable.setAll(null);
		vaporPressureTimeTable.setCell(0, 0, (double) time);
		for (int i = 0; i < vaporPressure.length; i++) {
			vaporPressure[i] = 0;
			vaporPressureTimeTable.setCell(i + 1, 0, vaporPressure[i]);
		}
		molarity[0] = 55.4082;
		molarity[1] = 55.2661;
		molarity[2] = 55.0728;
		molarity[3] = 54.5686;
		vaporPressureMolarityTable.setRow(0, vaporPressure);
		vaporPressureMolarityTable.setRow(1, molarity);
		for(int i = 0; i < waterTank.length; i++){
			k = waterTank[i].getLiquidParticleSystem().getActiveParticles();
			for(int j = 0; j < (30 - k); j++){
				waterTank[i].getLiquidParticleSystem().spawnParticle();
				waterTank[i].getGasParticleSystem().removeParticle();
			}
		}
	}

	// advance simulation by one data point
	private void stepSimulation() {
		if (time >= 100) {
			running = false;
		} else {
			time += CHANGE_IN_TIME;
			vaporPressure[0] = (2.333 - 2.333 * Math.exp(-K[0] * time));
			vaporPressure[1] = (4.234 - 4.234 * Math.exp(-K[1] * time));
			vaporPressure[2] = (7.367 - 7.367 * Math.exp(-K[2] * time));
			vaporPressure[3] = (19.993 - 19.993 * Math.exp(-K[3] * time));
			if (time % 10 == 0) {
				vaporPressureTimeTable.setCell(0, time / 10, (double) time);
				for (int i = 0; i < vaporPressure.length; i++) {
					vaporPressureTimeTable.setCell(i + 1, time / 10, vaporPressure[i]);
				}
			}
			for (int i = 1; i < 4; i++) {
				if (time % (100 / (i * 2)) == 0) {
					spawnGasParticle(i - 1);
				}
			}
			if (time % (100 / 14) == 0) {
				spawnGasParticle(3);
			}
			for (int i = 0; i < molaritySet.length; i++) {
				molaritySet[i].addPoint(time, molarity[i]);
				vaporPressureSet[i].addPoint(time, vaporPressure[i]);
				vaporPressureMolarityTable.setCell(i, 0, vaporPressure[i]);
				vaporPressureMolarityTable.setCell(i, 1, molarity[i]);
			}
		}
	}

	//Create gas particles and remove liquid ones
	private void spawnGasParticle(int... i) {
		for (int j : i) {
			waterTank[j].getGasParticleSystem().spawnParticle();
			waterTank[j].getLiquidParticleSystem().removeParticle();
		}
	}

	// set text for play button
	@Override
	public void update() {
		menu.setRadioButtonSelected("Show Vapor Pressure vs Time Table", vaporPressureTimeTableFrame.isVisible());
		menu.setRadioButtonSelected("Show Tank", tankFrame.isVisible());
		menu.setRadioButtonSelected("Show Equipment", equipmentFrame.isVisible());
		menu.setRadioButtonSelected("Show Pressure vs Temperature", vaporPressureTemperatureGraphFrame.isVisible());
		if (running) {
			play.setText("Pause");
			menu.setMenuItemText("Play", "Pause");
			stepSimulation();
		} else {
			play.setText("Play");
			menu.setMenuItemText("Play", "Play");
		}
	}

}
