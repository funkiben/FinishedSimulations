package vapor_pressure_equilibrium;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import lab.LabFrame;
import lab.util.Vector2;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;
import lab.component.container.Bulb;
import lab.component.data.GraphDataSet;
import lab.component.data.Graph;
import lab.component.swing.input.Dropdown;
import lab.component.swing.input.slider.LabeledDoubleSlider;
import lab.component.swing.Label;
import lab.substance.Substance;

public class VPEquilibrium extends LabFrame {

	private final Graph graph;
	private static final long serialVersionUID = 1L;
	private final Bulb flask1, flask2;
	private final LabeledDoubleSlider tempSlider;
	private final VerticalGraduation vGrad;
	private final HorizontalGraduation hGrad;
	private ArrayList<Substance> substances;
	private Dropdown<String> dropdown;
	private Dropdown<String> dropdown2;
	private Label f1Label;
	private Label f2Label;
	private double tempo;
	private double pressure1;
	private double pressure2;
	private double saturation = .3;

	public static void main(String[] args) {
		new VPEquilibrium("Vapor Pressure Equilibrium Lab", 1000, 650);

	}

	public VPEquilibrium(String name, int width, int height) {
		super(name, width, height);

		// PV=nRT
		// P=nRT/V

		vGrad = new VerticalGraduation(0, 2600, 50, 200);
		
		hGrad = new HorizontalGraduation(0, 100, 10, 5);

		Substance benzene = new Substance("Benzene", 0.045, true, Color.gray);
		Substance water = new Substance("Water", 0.050, true, Color.blue);
		Substance methanol = new Substance("Methanol", 0.040, true, new Color(0, 51, 0));
		Substance ethanol = new Substance("Ethanol", 0.035, true, new Color(130, 130, 255));
		Substance aceticAcid = new Substance("Acetic Acid", 0.030, true, new Color(20, 200, 255));

		substances = new ArrayList<Substance>();
		substances.add(benzene);
		substances.add(water);
		substances.add(methanol);
		substances.add(ethanol);
		substances.add(aceticAcid);

		graph = new Graph(600, 200, "Partial Pressures of Gases", "Temperature (Celcius)", "Pressure (Torr)", hGrad,
				vGrad);
		vGrad.setTextOffset(-30);
		flask1 = new Bulb(150, 150);
		flask2 = new Bulb(150, 150);
		tempSlider = new LabeledDoubleSlider(200, 0.0, 100.0, 1.0, 4, 1) {
			@Override
			public void update() {
				tempSlider.getLabel().setText(SigFig.sigfigalize(getValue(), tempSlider.getSigFigs()) + "\u00b0 K ");
			}
		};
		tempSlider.getLabel().setWidth(tempSlider.getLabel().getWidth()+70);
		tempSlider.setOffsetY(25);
		dropdown = new Dropdown<String>(200, 50) {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}

		};
		dropdown2 = new Dropdown<String>(200, 50) {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		};
		f1Label = new Label(150, 150, "Flask 1");
		f2Label = new Label(150, 150, "Flask 2");

		for (int i = 0; i < substances.size(); i++) {
			dropdown.getJComponent().addItem(substances.get(i).getName());

			dropdown2.getJComponent().addItem(substances.get(i).getName());

			substances.get(i).setSet(new GraphDataSet(substances.get(i).getName(), true, true));

			substances.get(i).getSet().setColor(substances.get(i).getColor());

			graph.addDataSet(substances.get(i).getSet());

		}

		substances.get(0).setBoilingConditions(30.77, 353.25, 1334.13);
		substances.get(1).setBoilingConditions(44, 373.15, 760.0);
		substances.get(2).setBoilingConditions(38.278, 337.85, 2489.45);
		substances.get(3).setBoilingConditions(38.56, 351.52, 1649.9);
		substances.get(4).setBoilingConditions(23.7, 391.25, 526.72);

		start(62);

		addComponent(flask1);
		addComponent(f1Label);
		addComponent(dropdown);
		addComponent(dropdown2);
		addComponent(f2Label);
		addComponent(flask2);
		addComponent(tempSlider);
		addComponent(graph);
		
		tempSlider.setOffsetX(50);
		dropdown.setOffsetY(200);
		dropdown.setOffsetX(-200);
		dropdown2.setOffsetY(200);
		dropdown2.setOffsetX(50);
		f2Label.setOffsetX(-200);
		graph.setOffsetY(50);
		graph.setOffsetX(200);

	}

	@Override
	public void update() {
		changeAction();
		tempSlider.getLabel().setText(tempSlider.getValue() + "C");
		for (int i = 0; i < substances.size(); i++) {
			if (substances.get(i).getName() == dropdown.getJComponent().getSelectedItem()) {
				substances.get(i).getSet().addPoint(new Vector2(tempo, pressure1));
				f1Label.setText("<html><font color=\"red\">Flask 1</font> <br>Substance: " + "<font color=\"rgb("
						+ substances.get(i).getColor().getRed() + "," + substances.get(i).getColor().getGreen() + ","
						+ substances.get(i).getColor().getBlue() + ")\">" + substances.get(i).getName()
						+ "</font><br>Pressure: " + (Math.floor(pressure1 * 100) / 100) + "mmHg</html>");
				flask1.setColor(calculateFlaskColor(substances.get(i), pressure1));
			}
			if (substances.get(i).getName() == dropdown2.getJComponent().getSelectedItem()) {
				substances.get(i).getSet().addPoint(new Vector2(tempo, pressure2));
				f2Label.setText("<html><font color=\"blue\">Flask 2</font> <br>Substance: " + "<font color=\"rgb("
						+ substances.get(i).getColor().getRed() + "," + substances.get(i).getColor().getGreen() + ","
						+ substances.get(i).getColor().getBlue() + ")\">" + substances.get(i).getName()
						+ "</font><br>Pressure: " + (Math.floor(pressure2 * 100) / 100) + "mmHg</html>");
				flask2.setColor(calculateFlaskColor(substances.get(i), pressure2));
			}
		}

		if (Math.abs(tempo - tempSlider.getValue()) > 1) {
			if (tempo - tempSlider.getValue() < -1) {
				tempo++;
			} else if (tempo - tempSlider.getValue() > 1) {
				tempo--;
			}
		} else {
			tempo = tempSlider.getValue();
		}
	
	}
	

	private void changeAction() {

		for (int i = 0; i < substances.size(); i++) {
			if (substances.get(i).getName().equals(dropdown.getJComponent().getSelectedItem())
					|| substances.get(i).getName().equals(dropdown2.getJComponent().getSelectedItem())) {

				graph.removeDataSet(substances.get(i).getName());

				graph.addDataSet(substances.get(i).getSet());
				if (substances.get(i).getName().equals(dropdown.getJComponent().getSelectedItem())) {

					pressure1 = findPressure(373.15, substances.get(i).getBoilPressure(),
							substances.get(i).getHeatOfVaporization(), 273.15 + tempo);

				}
				if (substances.get(i).getName().equals(dropdown2.getJComponent().getSelectedItem())) {

					pressure2 = findPressure(373.15, substances.get(i).getBoilPressure(),
							substances.get(i).getHeatOfVaporization(), 273.15 + tempo);

				}
			} else {
				graph.removeDataSet(substances.get(i).getName());
			}
		}

	}

	private Color calculateFlaskColor(Substance s, double currentPressure) {
		double pressureRatio = saturation * currentPressure
				/ findPressure(373.15, s.getBoilPressure(), s.getHeatOfVaporization(), 373.15);
		double redComponent = (255 - ((255 - s.getColor().getRed()) * pressureRatio));
		double greenComponent = (255 - ((255 - s.getColor().getGreen()) * pressureRatio));
		double blueComponent = (255 - ((255 - s.getColor().getBlue()) * pressureRatio));
		return new Color(redComponent >= 255 ? 255 : (int) (redComponent + .5),
				greenComponent >= 255 ? 255 : (int) (greenComponent + .5),
				blueComponent >= 255 ? 255 : (int) (blueComponent + .5));
	}

	private double findPressure(double boilPoint, double boilPressure, double hVaporization, double newTemp) {
		return ((boilPressure) * (Math.exp(((1000 * hVaporization) / 8.314) * ((1 / boilPoint) - (1 / newTemp)))));
	}

}
