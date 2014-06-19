package pt.iscte.meti.healthmonitor.draw;

import java.util.List;

import pt.iscte.meti.healthmonitor.R;
import pt.iscte.meti.healthmonitor.models.HealthData;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.content.Context;
import android.view.View;

/**
 * Simple class for GraphView library usage
 * http://android-graphview.org/
 */
public class Charts {
	private static void setStyle(Context context,GraphView graphView) {
		graphView.getGraphViewStyle().setGridColor(context.getResources().getColor(R.color.even_lines_gray));
		graphView.getGraphViewStyle().setHorizontalLabelsColor(context.getResources().getColor(R.color.blue_text_color));
		graphView.getGraphViewStyle().setVerticalLabelsColor(context.getResources().getColor(R.color.blue_text_color));
		graphView.getGraphViewStyle().setTextSize(12);
		graphView.getGraphViewStyle().setNumHorizontalLabels(2);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(30);
		
	}
	public static View drawBPMLineChart(Context context, List<HealthData> chartData) {
		int size = chartData.size();
		GraphViewData[] data = new GraphViewData[size];
		int index = 0;
		for(HealthData health : chartData) {
		  data[index++] = new GraphViewData(index,health.getBpm());
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);	 
		GraphView graphView = new LineGraphView(context, "BPM");
		graphView.addSeries(dataSeries);	
		// styles
		setStyle(context,graphView);	
		return graphView;
	}
	
	public static View drawTempLineChart(Context context, List<HealthData> chartData) {
		int size = chartData.size();
		GraphViewData[] data = new GraphViewData[size];
		int index = 0;
		for(HealthData health : chartData) {
		  data[index++] = new GraphViewData(index,health.getTemp());
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);		
		GraphView graphView = new LineGraphView(context, "Temperature");
		graphView.addSeries(dataSeries);
		// styles
		setStyle(context,graphView);		
		return graphView;
	}
}
