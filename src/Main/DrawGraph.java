package Main;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class DrawGraph extends JFrame {

    // Class constructor. Creates and displays graph with input ArrayLists and saves graph as PNG
    public DrawGraph(ArrayList<Double> timeVals, ArrayList<Double> chargeVals, String fullFilePath) throws IOException {
        XYDataset dataset = CreateData(timeVals, chargeVals);
        JFreeChart chart = createGraph(dataset);

        // modify and display graph
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        // pack and display chart
        pack();
        setTitle("RLC Circuit q(t) vs t");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        // save graph as PNG image
        String fileName = fullFilePath + ".png";
        ChartUtilities.saveChartAsPNG(new File(fileName), chart, chartPanel.getWidth(), chartPanel.getHeight());
    }

    // Creates XYDataset series for chart
    private XYDataset CreateData(ArrayList<Double> timeVals, ArrayList<Double> chargeVals) {
        XYSeries series = new XYSeries("ChargeVStime");
        for (int i=0; i<chargeVals.size();i++) {
            series.add(timeVals.get(i), chargeVals.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    // Creates Graph with XYDataset input
    private JFreeChart createGraph(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart("Charge q(t) vs time (seconds)", "time (seconds)",
                "q(t) (Coulombs)", dataset, PlotOrientation.VERTICAL, false, true, false);

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        return chart;
    }
}
