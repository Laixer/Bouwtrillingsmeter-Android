package nl.gemeenterotterdam.bouwtrillingsmeter.android.frontend;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Random;

public class Graph {

    private String name;
    private String textAxisHorizontal;
    private String textAxisVertical;

    public Graph(String name, String textAxisHorizontal, String textAxisVertical) {
        this.name = name;
        this.textAxisHorizontal = textAxisHorizontal;
        this.textAxisVertical = textAxisVertical;
    }

    /**
     * TODO This is for debug purposes
     *
     * @return A series of 100 random data points
     */
    public LineGraphSeries<DataPoint> getAsSeries() {
        DataPoint[] dataPoints = new DataPoint[101];
        dataPoints[0] = new DataPoint(0, 0.5);
        for (int i = 1; i < dataPoints.length; i++) {
            dataPoints[i] = new DataPoint(i, dataPoints[i-1].getY() + (-0.5 + Math.random()) * 0.05);
        }
        return new LineGraphSeries<DataPoint>(dataPoints);
    }

    public String getName() {
        if (name == null) {
            name = "default name";
        }
        return name;
    }

    public String getTextAxisHorizontal() {
        if (textAxisHorizontal == null) {
            textAxisHorizontal = "Default horizontal axis";
        }
        return textAxisHorizontal;
    }

    public String getTextAxisVertical() {
        if (textAxisVertical == null) {
            textAxisVertical = "Default vertical axis";
        }
        return textAxisVertical;
    }

}
