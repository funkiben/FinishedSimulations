package isolation_method;

import lab.component.EmptyComponent;
import lab.component.swing.Label;
import lab.component.swing.input.field.DoubleField;
import lab.component.swing.input.list.MutableList;
import lab.util.Vector2;

public class CoordinateList extends MutableList<Vector2> {

	private final DoubleField xField, yField;
	
	public CoordinateList(int width, int height, String xLabel, String yLabel) {
		super(width, height);
		
		xField = new DoubleField((width - 90) / 2, -9999, 9999, -1);
		yField = new DoubleField((width - 90) / 2, -9999, 9999, -1);
		
		xField.setText("");
		yField.setText("");
		
		xField.getJComponent().addKeyListener(new EntryFieldKeyListener());
		yField.getJComponent().addKeyListener(new EntryFieldKeyListener());
		
		addChild(new EmptyComponent(10, 0), new Label(10, 20, xLabel), xField, new Label(10, 20, yLabel), yField);
		
	}
	
	

	@Override
	public Vector2 getEntry() {
		if (xField.hasInput() && yField.hasInput()) {
			return new Vector2(xField.getValue(), yField.getValue());
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
