package LeChatelierConcentration;

import lab.LabFrame;
import lab.component.EmptyComponent;
import lab.component.container.Bulb;
import lab.component.data.Graph;
import lab.component.swing.Label;
import lab.component.swing.input.Button;
import lab.component.swing.input.slider.LabeledDoubleSlider;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

public class LeChatelierConcentration extends LabFrame{

	private static final long serialVersionUID = 2402023506629915960L;

	private Bulb bulb;
	private LabeledDoubleSlider cSlider;
	private LabeledDoubleSlider h2oSlider;
	private LabeledDoubleSlider coSlider;
	private LabeledDoubleSlider h2Slider;
	
	private VerticalGraduation vGrad;
	private HorizontalGraduation hGrad;
	
	private Label cLabel;
	private Label h2oLabel;
	private Label coLabel;
	private Label h2Label;
	
	private double cMolesEquilibrium;
	private double h2oMolesEquilibrium;
	private double coMolesEquilibrium;
	private double h2MolesEquilibrium;
	
	private final double EQUILIBRIUM_CONSTANT = 27.5625;
	
	private Button resetButton;
	
	private int increasing = 0;
	
	private double moleIncrement = .1;
	private double globalTime;
	
	private Graph graph;
	
	public static void main(String[] args) {
		new LeChatelierConcentration("LeChatelier",1920,1000);
	}
	
	public LeChatelierConcentration(String name,int width, int height) {
		super(name,width,height);
		
		globalTime = 0;
		bulb = new Bulb(250,250);
		addComponent(bulb);
		
		EmptyComponent sliderHolder = new EmptyComponent(350,400);
		
		cSlider = new LabeledDoubleSlider(250,0.01,1,.00009,5, 0) {
			@Override
			public void update() {
				cSlider.getLabel().setText(SigFig.sigfigalize(getValue(), cSlider.getSigFigs()) + " Moles C");
			}
		};
		cSlider.getLabel().setWidth(cSlider.getLabel().getWidth()+100);
		sliderHolder.addChild(cSlider);
		
		h2oSlider = new LabeledDoubleSlider(250,0.01,1,.00009,5, 0) {
			@Override
			public void update() {
				h2oSlider.getLabel().setText(SigFig.sigfigalize(getValue(), h2oSlider.getSigFigs()) + " Moles H2O");
			}
		};
		h2oSlider.getLabel().setWidth(h2oSlider.getLabel().getWidth()+100);
		h2oSlider.setOffsetY(10);
		sliderHolder.addChild(h2oSlider);
		
		coSlider = new LabeledDoubleSlider(250,0.01,1,.00009,5, 0) {
			@Override
			public void update() {
				coSlider.getLabel().setText(SigFig.sigfigalize(getValue(), coSlider.getSigFigs()) + " Moles CO");
			}
		};
		coSlider.getLabel().setWidth(coSlider.getLabel().getWidth()+100);
		coSlider.setOffsetY(10);
		sliderHolder.addChild(coSlider);
		
		h2Slider = new LabeledDoubleSlider(250,0.01,1,.00009,5, 0) {
			@Override
			public void update() {
				h2Slider.getLabel().setText(SigFig.sigfigalize(getValue(), h2Slider.getSigFigs()) + " Moles H2");
			}
		};
		h2Slider.getLabel().setWidth(h2Slider.getLabel().getWidth()+100);
		h2Slider.setOffsetY(10);
		sliderHolder.addChild(h2Slider);
		
		sliderHolder.setOffsetY(300);
		sliderHolder.setOffsetX(-250);
		
		addComponent(sliderHolder);
		
		hGrad = new HorizontalGraduation(0,100,10,5);
		vGrad = new VerticalGraduation(0,.00250,.0005,.00025);
		
		graph = new Graph(250,250,"Moles of Substances","time (s)","moles",hGrad,vGrad);
		graph.setOffsetX(100);
		vGrad.setTextOffset(-40);
		addComponent(graph);
		
		cSlider.setValue(.99);
		coSlider.setValue(.99);
		h2Slider.setValue(.99);
		h2oSlider.setValue(.99);
		
		
		cMolesEquilibrium = cSlider.getValue();
		h2oMolesEquilibrium = h2oSlider.getValue();
		coMolesEquilibrium = coSlider.getValue();
		h2MolesEquilibrium = h2Slider.getValue();
		
		EmptyComponent labelHolder = new EmptyComponent(800,200);
		Label titleLabel = new Label(800,100,"Measured Amounts of Material in the Glass Bulb");
		titleLabel.setFontSize(14f);
		labelHolder.addChild(titleLabel);
		cLabel = new Label(200,100,"C: " + cMolesEquilibrium + " moles");
		cLabel.setOffsetY(-25);
		labelHolder.addChild(cLabel);
		h2oLabel = new Label(200,100,"H2O: " + h2oMolesEquilibrium + " moles");
		h2oLabel.setOffsetY(-25);
		labelHolder.addChild(h2oLabel);
		coLabel = new Label(200,100,"CO: " + coMolesEquilibrium + " moles");
		coLabel.setOffsetX(-400);
		labelHolder.addChild(coLabel);
		h2Label = new Label(200,100,"H2: " + h2MolesEquilibrium + " moles");
		labelHolder.addChild(h2Label);
		
		labelHolder.setOffsetX(-700);
		labelHolder.setOffsetY(450);
		addComponent(labelHolder);
		
		resetButton = new Button(400,100,"Reset Simulation") {

			@Override
			public void doSomething() {
				// TODO Auto-generated method stub
				globalTime = 0;
			}};
		
		
		
		start(60);
		
	}

	public double calculatePressure(double currentMoles) {
		double R = 8.314;
		double temperature = 1000;
		double volume = 10;
		return (currentMoles*R*temperature)/volume;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		globalTime++;
		
		double tempQ;
		tempQ = (calculatePressure(coMolesEquilibrium)*calculatePressure(h2MolesEquilibrium))/(calculatePressure(cMolesEquilibrium)*calculatePressure(h2oMolesEquilibrium));
		
		if ((float)tempQ < (float)EQUILIBRIUM_CONSTANT) {
			if(increasing == -1) {
				moleIncrement/=10;
			}
			increasing = 1;
			coMolesEquilibrium+=moleIncrement;
			h2MolesEquilibrium+=moleIncrement;
			cMolesEquilibrium-=moleIncrement;
			h2oMolesEquilibrium-=moleIncrement;
		} else if((float)tempQ > (float)EQUILIBRIUM_CONSTANT) {
			if(increasing == 1) {
				moleIncrement/=10;
			}
			increasing = -1;
			coMolesEquilibrium-=moleIncrement;
			h2MolesEquilibrium-=moleIncrement;
			cMolesEquilibrium+=moleIncrement;
			h2oMolesEquilibrium+=moleIncrement;
		}
		
		cLabel.setText("C: " + (float)cMolesEquilibrium + " moles");
		h2oLabel.setText("H2O: " + (float)h2oMolesEquilibrium + " moles");
		coLabel.setText("CO: " + (float)coMolesEquilibrium + " moles");
		h2Label.setText("H2: " + (float)h2MolesEquilibrium + " moles");
		
		
		System.out.println((float)tempQ==(float)EQUILIBRIUM_CONSTANT);
	}
	
}
