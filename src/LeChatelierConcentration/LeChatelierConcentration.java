package LeChatelierConcentration;

import lab.LabFrame;
import lab.component.container.Bulb;
import lab.component.data.Graph;
import lab.component.swing.input.Button;
import lab.component.swing.input.LabeledDoubleSlider;

public class LeChatelierConcentration extends LabFrame{

	private static final long serialVersionUID = 2402023506629915960L;

	private Bulb bulb;
	private LabeledDoubleSlider cSlider;
	private LabeledDoubleSlider h2oSlider;
	private LabeledDoubleSlider coSlider;
	private LabeledDoubleSlider h2Slider;
	
	private Button resetButton;
	
	private Graph graph;
	
	public static void main(String[] args) {
		new LeChatelierConcentration("LeChatelier",800,500);
	}
	
	public LeChatelierConcentration(String name,int width, int height) {
		super(name,width,height);
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
}
