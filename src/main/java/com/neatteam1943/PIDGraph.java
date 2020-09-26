package com.neatteam1943;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PIDGraph {

    private static double p;
    private static double i;
    private static double d;
    private static double f;

    private static double setpoint;
    private static double tolerance;

    private static int graphWidth;
    private static int graphHeight;

    private static double current;
    private static XYSeries series;

    private static void setup() {
        current = 0.0;
        String sb = "P=" + p + " , " +
                "I=" + i + " , " +
                "D=" + d + " , " +
                "F=" + f;
        series = new XYSeries(sb);
    }

    public static void configResolution(int width, int height) {
        graphWidth = width;
        graphHeight = height;
    }

    public static void config(double kP, double kI, double kD) {
        p = kP;
        i = kI;
        d = kD;
        f = 0.0;

        setup();
        configResolution(700, 500);
    }

    public static void config(double kP, double kI, double kD, double kF) {
        config(kP, kI, kD);
        f = kF;

        setup();
    }

    public static void configTarget(double setpoint, double tolerance) {
        PIDGraph.setpoint = setpoint;
        PIDGraph.tolerance = tolerance;
    }

    public static void setP(double value) {
        p = value;
    }

    public static void setI(double value) {
        i = value;
    }

    public static void setD(double value) {
        d = value;
    }

    public static void setF(double value) {
        f = value;
    }

    public static void setSetpoint(double value) {
        setpoint = value;
    }

    public static void setTolerance(double value) {
        tolerance = value;
    }

    public static void add(double value) {
        series.add(current, value);
        current += 0.02;
    }

    private static JFreeChart createChart() {
        return ChartFactory.createXYLineChart(
                "PIDGraph",
                "Time",
                "Value",
                null,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private static JFreeChart getGraph() {
        XYDataset values = new XYSeriesCollection(series);
        ValueMarker setpointMarker = new ValueMarker(setpoint);
        ValueMarker minToleranceMarker = new ValueMarker(setpoint - tolerance);
        ValueMarker maxToleranceMarker = new ValueMarker(setpoint + tolerance);
        BasicStroke dashedStroke = new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{10.0f, 15.0f}, 0.0f
        );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(0x1F77B4));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        setpointMarker.setPaint(Color.BLACK);
        minToleranceMarker.setPaint(Color.RED);
        maxToleranceMarker.setPaint(Color.RED);

        setpointMarker.setStroke(new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{30.0f, 10.0f}, 0.0f
        ));
        minToleranceMarker.setStroke(dashedStroke);
        maxToleranceMarker.setStroke(dashedStroke);

        JFreeChart graph = createChart();
        graph.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        XYPlot plot = graph.getXYPlot();

        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setRange(0.0, current);

        plot.setRenderer(renderer);
        plot.setDataset(0, values);

        plot.addRangeMarker(setpointMarker);
        plot.addRangeMarker(minToleranceMarker);
        plot.addRangeMarker(maxToleranceMarker);

        return graph;
    }

    public static void save(String path) throws IOException {
        JFreeChart graph = getGraph();

        File graphImg = new File(path + "/graph.png");
        FileWriter graphTxt = new FileWriter(path + "/graph.txt");

        graphTxt.write(p + " , " + i + " , " + d + " , " + f + "\n");
        graphTxt.write(setpoint + " , " + tolerance + "\n\n");

        int j = 0;
        double[][] arr = series.toArray();
        for (double i : arr[1]) {
            graphTxt.write(i + " , " + arr[0][j] + "\n");
            j++;
        }

        ChartUtils.saveChartAsPNG(graphImg, graph, graphWidth, graphHeight);
        graphTxt.close();
    }
}
