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
	
	private Button resetButton;
	
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
		
		EmptyComponent labelHolder = new EmptyComponent(800,200);
		Label titleLabel = new Label(800,100,"Measured Amounts of Material in the Glass Bulb");
		titleLabel.setFontSize(14f);
		labelHolder.addChild(titleLabel);
		cLabel = new Label(200,100,"C: " + cSlider.getValue() + " moles");
		cLabel.setOffsetY(-25);
		labelHolder.addChild(cLabel);
		h2oLabel = new Label(200,100,"H2O: " + h2oSlider.getValue() + " moles");
		h2oLabel.setOffsetY(-25);
		labelHolder.addChild(h2oLabel);
		coLabel = new Label(200,100,"CO: " + coSlider.getValue() + " moles");
		coLabel.setOffsetX(-400);
		labelHolder.addChild(coLabel);
		h2Label = new Label(200,100,"H2: " + h2Slider.getValue() + " moles");
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

	@Override
	public void update() {
		// TODO Auto-generated method stub
		globalTime++;
	}
	
}
