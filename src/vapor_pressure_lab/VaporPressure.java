package vapor_pressure_lab;

import java.awt.Color;
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

	// constant values that are used throughout the simulation
	private static final double[] K = { 0.0693, 0.077, 0.086625, 0.1155, 1.197833E-6, 2.341675E-6, 4.4528E-6,
			1.5284E-5 };
	private static final double[] WATER_VP = { 2.333, 4.234, 7.367, 19.993 };
	private static final double[] TETEN_CONSTANT = { 6.122, 17.62, 243.12 };
	private static final double[] MOLARITY = { 55.4082, 55.2661, 55.0728, 54.5686 };
	private static final int CHANGE_IN_TIME = 1;
	private static final int SIG_FIGS = 5;
	private static final Color[] COLOR = { Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN };

	// windows
	private final LabFrame vaporPressureTimeTableFrame, vaporPressureTemperatureGraphFrame, tankFrame, equipmentFrame, instructionsFrame;

	// create dynamic variables for calculations
	private int time;
	private double[] vaporPressure = new double[4];
	private boolean running = false;
	private GraphDataSet[] molaritySet = new GraphDataSet[4];
	private GraphDataSet[] vaporPressureSet = new GraphDataSet[4];
	private GraphDataSet vaporPressureTemperature;
	private int k;

	// components that are used in multiple methods
	private final Button play;
	private final DataTable<Double> vaporPressureMolarityTable, vaporPressureTimeTable;
	private final DoubleField inputTemperature;
	private final Graph molarityGraph, vaporPressureGraph, vaporPressureTemperatureGraph;
	private final Label outputVaporPressure;
	private final MenuComponent menu;
	private final WaterTank[] waterTank = new WaterTank[4];

	// start simulation
	public static void main(String args[]) {
		new VaporPressure("Equilibrium Vapor Pressure Simulation", 800, 650);
	}

	// create windows and components
	public VaporPressure(String name, int width, int height) {
		super(name, width, height);
		getRoot().setLayout(LabComponent.FREE_FORM);

		// create play button
		play = new Button(100, 25, "Play") {
			@Override
			public void doSomething() {
				// set the simulation running, or pause it
				if (running)
					running = false;
				else
					running = true;
			}
		};
		// set the position of the button on the screen
		play.setOffset(30, 500);

		// create step button
		Button step = new Button(100, 25, "Step") {
			@Override
			public void doSomething() {
				// step the simulation forward if it is paused
				if (!running)
					stepSimulation();
			}
		};
		step.setOffset(130, 500);

		// create reset button
		Button reset = new Button(100, 25, "Reset") {
			@Override
			public void doSomething() {
				// call the method to reset the simulation
				resetSimulation();
			}
		};
		reset.setOffset(230, 500);

		// create molarity vs time graph and the graduations for the axes
		HorizontalGraduation timeGraduation = new HorizontalGraduation(0, 100, 20, 10);
		VerticalGraduation molarityGraduation = new VerticalGraduation(54, 55.6, .2, .1);
		molarityGraph = new Graph(275, 400, "Molarity vs Time", "Time (s)", "Molarity H2O (mol/L)", timeGraduation,
				molarityGraduation);
		molarityGraph.setOffset(60, 50);
		// position the graduation texts and labels so the graph is easy to read
		molarityGraph.setYLabelOffset(32);
		molarityGraduation.setTextOffset(-32);

		// create vapor pressure vs time graph
		vaporPressureGraph = new Graph(275, 400, "Vapor Pressure vs Time", "Time (s)", "Vapor Pressure (kPa)",
				timeGraduation, new VerticalGraduation(0, 25, 5, 2.5));
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
		vaporPressureMolarityTable.setRow(1, MOLARITY);
		for (int i = 0; i < vaporPressureMolarityTable.getColumnNumber(); i++)
			vaporPressureMolarityTable.setColumnColor(i, COLOR[i]);

		// create tank frame
		tankFrame = new LabFrame("Tanks", 250, 720, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {

			}
		};
		// the tank frame will close, but not terminate the whole application
		tankFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		tankFrame.getRoot().setLayout(LabComponent.FREE_FORM);

		// create tanks and labels for the temperatures
		Label[] tankTemp = { new Label(30, 20, "20C"), new Label(30, 20, "30C"), new Label(30, 20, "40C"),
				new Label(30, 20, "60C") };
		for (int i = 0; i < waterTank.length; i++) {
			// create each water tank, space them out, and add them to the frame
			waterTank[i] = new WaterTank(175, 175, 30, 14, .5, 2);
			waterTank[i].setOffset(60, 180 * i);
			tankTemp[i].setOffset(15, 180 * i + (175 / 2));
			tankFrame.addComponent(waterTank[i], tankTemp[i]);
		}
		tankFrame.start(30);

		// create the showtank button
		Button showTank = new Button(90, 25, "Show Tank") {
			@Override
			public void doSomething() {
				// display the tank frame
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

		// create an image of the flask and add it to the frame
		equipmentFrame.addComponent(new ImageComponent(400, 300, "/vapor_pressure_lab/flask.gif"));
		equipmentFrame.start(0);

		// create button to display the equipment frame
		Button showEquipment = new Button(125, 25, "Show Equipment") {
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

		// create vapor pressure vs temperature graph
		vaporPressureTemperatureGraph = new Graph(420, 500, "Vapor Pressure vs Temperature", "Temperature (C)",
				"Vapor Pressure (kPa)", new HorizontalGraduation(0, 105, 20, 10),
				new VerticalGraduation(0, 105, 10, 5));
		vaporPressureTemperatureGraph.setYLabelOffset(70);

		// create button, textfield, and label
		Label temperatureLabel = new Label(100, 25, "Temperature");
		temperatureLabel.setOffset(20, 565);
		inputTemperature = new DoubleField(85, 0, 100, 5);
		inputTemperature.setOffset(20, 585);
		Button plot = new Button(100, 25, "Plot") {
			@Override
			public void doSomething() {
				// plot a point on the graph if the textfield has a value
				if (inputTemperature.hasInput()) {
					// calculate point
					double temperature = inputTemperature.getValue();
					double vp = TETEN_CONSTANT[0]
							* Math.exp((TETEN_CONSTANT[1] * temperature / (TETEN_CONSTANT[2] + temperature))) / 10;
					if (temperature > 99.3352) {
						vp = 101.325;
						temperature = 100;
					}
					// plot point and display vapor pressure
					vaporPressureTemperature.addPoint(temperature, vp);
					outputVaporPressure.setText("Vapor Pressure: " + SigFig.sigfigalize(vp, 5) + " kPa");
				}
			}
		};
		plot.setOffset(230, 575);
		outputVaporPressure = new Label(300, 25, "Vapor Pressure: ");
		outputVaporPressure.setOffset(340, 575);
		// add components to the vapor pressure temperature graph frame
		vaporPressureTemperatureGraphFrame.addComponent(vaporPressureTemperatureGraph, inputTemperature, plot,
				outputVaporPressure, temperatureLabel);
		vaporPressureTemperatureGraphFrame.start(30);

		// create show vapor pressure vs temperature frame button
		Button showPressureGraph = new Button(205, 25, "Show Pressure vs Temperature") {
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

		// create vapor pressure time table
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
		for (int i = 1; i < vaporPressureTimeTable.getColumnNumber(); i++)
			vaporPressureTimeTable.setColumnColor(i, COLOR[i - 1]);
		// add the table to the frame
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

		// create instructions text
		ScrollLabel instructions = new ScrollLabel(500, 350, "/vapor_pressure_lab/instructions.txt");
		instructions.setHoriztonalScrollBarPolicy(ScrollLabel.HORIZONTAL_SCROLLBAR_NEVER);
		instructions.setFontSize(13);
		// add it to the frame
		instructionsFrame.addComponent(instructions);
		instructionsFrame.start(0);

		// create file menu
		menu = new MenuComponent(getRoot().getWidth(), 25);

		// create and add control menu
		menu.addMenu("Control");
		menu.addMenuItem("Play", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (running)
					running = false;
				else
					running = true;
			}
		});
		menu.addMenuItem("Step", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!running)
					stepSimulation();
			}
		});
		menu.addMenuItem("Reset", "Control", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetSimulation();
			}
		});

		// create and add view menu
		menu.addMenu("View");
		menu.addRadioButtonMenuItem("Show Vapor Pressure vs Time Table", "View", true, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vaporPressureTimeTableFrame.isVisible())
					vaporPressureTimeTableFrame.dispose();
				else
					vaporPressureTimeTableFrame.setVisible(true);
			}
		});
		menu.addRadioButtonMenuItem("Show Tank", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tankFrame.isVisible())
					tankFrame.dispose();
				else
					tankFrame.setVisible(true);
			}
		});
		menu.addRadioButtonMenuItem("Show Equipment", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (equipmentFrame.isVisible())
					equipmentFrame.dispose();
				else
					equipmentFrame.setVisible(true);
			}
		});
		menu.addRadioButtonMenuItem("Show Pressure vs Temperature", "View", false, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (vaporPressureTemperatureGraphFrame.isVisible())
					vaporPressureTemperatureGraphFrame.dispose();
				else
					vaporPressureTemperatureGraphFrame.setVisible(true);
			}
		});

		// create help menu
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
		for (int i = 0; i < molaritySet.length; i++) {
			molaritySet[i].setColor(COLOR[i]);
			vaporPressureSet[i].setColor(COLOR[i]);
		}
		vaporPressureGraph.addDataSet(vaporPressureSet);
		vaporPressureTemperature = new GraphDataSet("", false, false);
		vaporPressureTemperatureGraph.addDataSet(vaporPressureTemperature);

		// create main frame and set up simulation
		addComponent(molarityGraph, vaporPressureGraph, play, step, reset, vaporPressureMolarityTable, showTank,
				showEquipment, showPressureGraph, menu);
		start(30);
		resetSimulation();
	}

	// resets all simulation values
	private void resetSimulation() {
		// pause simulation
		running = false;
		// clear graph points
		for (GraphDataSet set : molaritySet)
			set.clearPoints();
		for (GraphDataSet set : vaporPressureSet)
			set.clearPoints();
		vaporPressureTemperature.clearPoints();
		// reset time
		time = 0;
		// clear data table and reset ot
		vaporPressureTimeTable.setAll(null);
		vaporPressureTimeTable.setCell(0, 0, (double) time);
		for (int i = 0; i < vaporPressure.length; i++) {
			// reset vapor pressures
			vaporPressure[i] = 0;
			vaporPressureTimeTable.setCell(i + 1, 0, vaporPressure[i]);
		}
		// display initial values
		vaporPressureMolarityTable.setRow(0, vaporPressure);
		// reset the water tanks
		for (int i = 0; i < waterTank.length; i++) {
			k = waterTank[i].getLiquidParticleSystem().getActiveParticles();
			for (int j = 0; j < (30 - k); j++) {
				waterTank[i].getLiquidParticleSystem().spawnParticle();
				waterTank[i].getGasParticleSystem().removeParticle();
			}
		}
	}

	// advance simulation by one data point
	private void stepSimulation() {
		// stop simulation after 100 seconds
		if (time >= 100)
			running = false;
		else {
			// calculate values
			time += CHANGE_IN_TIME;
			for (int i = 0; i < vaporPressure.length; i++) {
				vaporPressure[i] = WATER_VP[i] - WATER_VP[i] * Math.exp(-K[i] * time);
			}
			// display values in table
			if (time % 10 == 0) {
				vaporPressureTimeTable.setCell(0, time / 10, (double) time);
				for (int i = 0; i < vaporPressure.length; i++)
					vaporPressureTimeTable.setCell(i + 1, time / 10, vaporPressure[i]);
			}
			// spawn gas particles in water tanks
			for (int i = 1; i < 4; i++)
				if (time % (100 / (i * 2)) == 0)
					spawnGasParticle(i - 1);
			if (time % (100 / 14) == 0)
				spawnGasParticle(3);
			// set cells and plot points
			for (int i = 0; i < molaritySet.length; i++) {
				molaritySet[i].addPoint(time, MOLARITY[i]);
				vaporPressureSet[i].addPoint(time, vaporPressure[i]);
				vaporPressureMolarityTable.setCell(i, 0, vaporPressure[i]);
				vaporPressureMolarityTable.setCell(i, 1, MOLARITY[i]);
			}
		}
	}

	// Create gas particles and remove liquid ones
	private void spawnGasParticle(int i) {
		waterTank[i].getGasParticleSystem().spawnParticle();
		waterTank[i].getLiquidParticleSystem().removeParticle();
	}

	// set text for play button and make sure windows correspond to radio
	// buttons
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
