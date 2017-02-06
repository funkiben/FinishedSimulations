package equilibrium;

import lab.LabFrame;
import lab.component.input.ButtonComponent;

//Vapor pressure simulation lab from Ms. Lund Easy Java

public class VaporPressure extends LabFrame{
	//initial values from simulation
	int temperature = 0;
	int dtemperature = 1;
	double volume = 1;
	double R = 8.313;
	int time = 0;
	int dtime = 1;
	
	ButtonComponent play;
	ButtonComponent step;
	ButtonComponent reset;
	public VaporPressure(String name, int width, int height) {
		super("Vapor Pressure Lab", 700, 800);

	}

	@Override
	public void update() {

		
	}

}
