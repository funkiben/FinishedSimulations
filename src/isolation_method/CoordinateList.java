package isolation_method;

import lab.component.EmptyComponent;
import lab.component.LabComponent;
import lab.component.swing.Label;
import lab.component.swing.input.field.DoubleField;
import lab.component.swing.input.list.MutableList;
import lab.util.SigFig;
import lab.util.Vector2;

public class CoordinateList extends MutableList<Vector2> {

	private final DoubleField xField, yField;
	private final String toString;
	
	public CoordinateList(int width, int height, String xLabel, String yLabel, String toString) {
		super(width, height);
		
		this.toString = toString;
		
		xField = new DoubleField(width / 2, -9999, 9999, -1);
		yField = new DoubleField(width / 2, -9999, 9999, -1);
		
		xField.setText("");
		yField.setText("");
		
		xField.getJComponent().addKeyListener(new EntryFieldKeyListener());
		yField.getJComponent().addKeyListener(new EntryFieldKeyListener());
		
		Label xFieldLabel = new Label(0, 20, xLabel);
		Label yFieldLabel = new Label(0, 20, yLabel);
		
		xFieldLabel.setWidth(Math.max(xFieldLabel.getTextWidth(), yFieldLabel.getTextWidth()) + 5);
		yFieldLabel.setWidth(xFieldLabel.getWidth());
		
		addChild(xFieldLabel, xField, yFieldLabel, yField, new EmptyComponent(width - xField.getWidth() - yFieldLabel.getWidth(), 0));
		
		LabComponent button = getChild(1);
		removeChild(1);
		
		addChild(button);
		
	}
	
	

	@Override
	public Vector2 getEntry() {
		if (xField.hasInput() && yField.hasInput()) {
			return new Vector2(xField.getValue(), yField.getValue()) {
				@Override
				public String toString() {
					return toString.replace("%x%", SigFig.sigfigalize(getX(), 4, 5)).replace("%y%", SigFig.sigfigalize(getY(), 4, 5));
				}
			};
		}
		
		return null;
	}

	@Override
	public void clearEntry() {
		xField.setText("");
		yField.setText("");
	}

	@Override
	public boolean entryHasFocus() {
		return yField.hasFocus();
	}

}
