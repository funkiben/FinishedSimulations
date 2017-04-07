package isolation_method;

import java.awt.Color;
import java.awt.Graphics;

import lab.component.LabComponent;
import lab.component.data.Graph;
import lab.component.data.GraphDataSet;
import lab.component.data.GraphUtil;
import lab.component.swing.Label;
import lab.util.HorizontalGraduation;
import lab.util.SigFig;
import lab.util.VerticalGraduation;

public class ReactionOrderGraph extends LabComponent {

	private final Graph graph;
	private final GraphDataSet data, lineOfBestFit;
	private final Label slopeLabel, interceptLabel;

	public ReactionOrderGraph(String title, String yLabel, HorizontalGraduation hg, VerticalGraduation vg) {
		super(260, 280);

		hg.setShowLabels(false);
		vg.setRemovePointZero(false);

		graph = new Graph(200, 200, title, "t (s)", yLabel, hg, vg);

		graph.setOffsetX(60);
		graph.setYLabelOffset(15);

		data = new GraphDataSet("data", false, false);
		lineOfBestFit = new GraphDataSet("lobf", true, false, Color.blue);

		graph.addDataSet(data, lineOfBestFit);

		slopeLabel = new Label(200, 20, "Slope: ");
		interceptLabel = new Label(200, 20, "Intercept: ");

		slopeLabel.setOffsetY(210);

		graph.addChild(slopeLabel, interceptLabel);

		addChild(graph);

	}

	public Graph getGraph() {
		return graph;
	}

	public GraphDataSet getData() {
		return data;
	}

	public GraphDataSet getLineOfBestFit() {
		return lineOfBestFit;
	}

	public Label getSlopeLabel() {
		return slopeLabel;
	}

	public Label getInterceptLabel() {
		return interceptLabel;
	}

	public void clear() {
		slopeLabel.setText("Slope: ");
		interceptLabel.setText("Intercept: ");

		data.clearPoints();
		lineOfBestFit.clearPoints();
	}
	
	public void plotLineOfBestFit() {
		double[] lobf = GraphUtil.getLineOfBestFit(data.getPoints());
		
		slopeLabel.setText("Slope: " + SigFig.sigfigalize(lobf[0], 4));
		interceptLabel.setText("Intercept: " + SigFig.sigfigalize(lobf[1], 4));
		
		double end = graph.gethGraduation().getEnd();
		
		lineOfBestFit.addPoint(0, lobf[1]);
		lineOfBestFit.addPoint(end, end * lobf[0] + lobf[1]);
	}
	
	@Override
	public void draw(int x, int y, int width, int height, Graphics g) { }

}
