/*
 * @(#)StatsView.java Jun 28, 2005
 *
 * Copyright 2005 Ex Machina. All rights reserved.
 * Ex Machina Proprietary/Confidential. Use is subject to license terms.
 */
package stats;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.rmi.CORBA.Tie;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.plot.CartesianCoordinateSystem;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;
import simplexml.XMLElement;

/**
 * @author Kees Jongenburger <kees@exmachina.nl>
 * @version $Id: StatsView.java,v 1.1 2005-06-29 13:09:44 keesj Exp $
 */
public class StatsView extends JPanel{
    
    GraphicsPlotCanvas plotCanvas;
    private DataPlot dataPlot;
    
    public long minX;
    public long maxX;
    public long startX;
    public long stopX;
    public long timeSpan = 1000 * 60 *60 *2;
    public int maxY = 400;
    
    public StatsView() {
        
        try {
            setLayout(new GridLayout(1,1));
            
            
            
            dataPlot = new DataPlot();
            dataPlot.addElement(new DataCurve(""));
            
            
            plotCanvas = createPlotCanvas(0,1000);
            plotCanvas.connect(dataPlot);
            add(plotCanvas.getGraphicsCanvas());
            validate();
            //System.err.println("done loading");
            setVisible(true);
            //System.err.println(getPreferredSize());
            
            
            //invalidate();
            
        } catch (Exception e){};
        
    }
    
    public void setCoordinateSystem(double xMin, double yMin, 
            double xMax, double yMax)
    {
        Properties props = new Properties();
        props.put("xAxis/minimum", Double.toString(xMin));
        props.put("xAxis/maximum", Double.toString(xMax));
        props.put("xAxis/ticLabelFormat", "%1.3f");
        props.put("xAxis/axisLabel", "");
        props.put("yAxis/minimum", Double.toString(yMin));
        props.put("yAxis/maximum", Double.toString(yMax));
        props.put("yAxis/ticLabelFormat", "%1.3f");
        props.put("yAxis/axisLabel", "");
        props.put("yAxis/axisLength", "0.8");
        props.put("xAxis/axisLabel", "time");
        props.put("xAxis/ticLabelFormat/className","stats.TicDateFormat");
        props.put("yAxis/axisLabel", "server load");
        //props.put("yAxis/maximum", "400");
        
        CartesianCoordinateSystem cs = new CartesianCoordinateSystem(
                new ConfigParameters(new PropertiesBasedConfigData(props)));
        plotCanvas.getPlot().setCoordinateSystem(cs);
    }
    
    public void load(String urlString) throws Exception{
        //removeAll();
        URL url = new URL(urlString);
        //URL url = new URL("http://quizengine.kennisnet.asp4all.nl/mmbase/admin/cpustats.jsp");
        //URL url = new URL("http://10.0.0.202:8080/stats/stats.jsp");
        XMLElement xmle = new XMLElement();
        xmle.parseFromReader(new InputStreamReader(url.openStream()));
        DataCurve curve = new DataCurve("");	
        startX= Long.parseLong(xmle.getChildAt(0).getProperty("time",""));
        stopX = Long.parseLong(xmle.getChildAt(xmle.countChildren() -1).getProperty("time",""));
        for (int x =0 ; x < xmle.countChildren() ; x++){
            XMLElement stat = xmle.getChildAt(x);        
            long time =  Long.parseLong(stat.getProperty("time",""));
            double y = Double.parseDouble(stat.getProperty("value",""));
            curve.addElement(new DataPoint(time ,y));
            //System.err.println("add elemet " + stat + " " + time );
        }
        minX = startX;
        maxX = stopX;
        if ( (stopX- startX)  > timeSpan ){
            startX = stopX  -  timeSpan;
        }
        dataPlot.replaceElementAt(0,curve);
        setCoordinateSystem(startX,0,startX + timeSpan,maxY);
        //plotCanvas = createPlotCanvas(start,stop);
        //plotCanvas.connect(dataPlot);
       ///add(plotCanvas.getGraphicsCanvas());
        //validate();
    }
    
    
    public long getMinX(){
        return minX;
    }
    
    public long getMaxX(){
        return maxX;
    }
    
    public long getStartX(){
        return startX;
    }
    
    public long getStopX(){
        return stopX;
    }
    
    public void setTimeSpan(long span){
        this.timeSpan = span;
        setCoordinateSystem(startX,0,startX + timeSpan,maxY);
    }
    
    public void setStartX(long startX){
        this.startX = startX;
        setCoordinateSystem(startX,0,startX + timeSpan,maxY);
    }
    
    public GraphicsPlotCanvas createPlotCanvas(long x1, long x2) {
        Properties props = new Properties();
        ConfigParameters config = new ConfigParameters(new PropertiesBasedConfigData(props));
        
        props.put("plot/legendVisible", "false");
        
        props.put("plot/coordinateSystem/xAxis/minimum", "" + x1);
        props.put("plot/coordinateSystem/xAxis/maximum", "" + x2);
        props.put("plot/coordinateSystem/xAxis/axisLabel", "time");
        props.put("plot/coordinateSystem/xAxis/ticLabelFormat/className","stats.TicDateFormat");
        props.put("plot/coordinateSystem/yAxis/axisLabel", "server load");
        props.put("plot/coordinateSystem/yAxis/maximum", "400");
        //props.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%d%%");
        
        props.put("plot/curveFactory/definitions", "curve");
        props.put("plot/curveFactory/curve/withLine", "true");
        //props.put("plot/curveFactory/curve/symbolFactory/className", "jcckit.plot.BarFactory");
        //props.put("plot/curveFactory/curve/symbolFactory/attributes/className","jcckit.graphic.ShapeAttributes");
        //props.put("plot/curveFactory/curve/symbolFactory/attributes/fillColor", "0xfe8000");
        //props.put("plot/curveFactory/curve/symbolFactory/attributes/lineColor", "0");
        props.put("plot/curveFactory/curve/symbolFactory/size", "0.08");
        props.put("plot/initialHintForNextCurve/className", "jcckit.plot.PositionHint");
        props.put("plot/initialHintForNextCurve/position", "0 0.1");
        
        return new GraphicsPlotCanvas(config);
    }
    
    
    public static void main(String[] argv) throws Exception{
        JFrame f  = new JFrame("test");
        f.setSize(800,600);
        f.setLocation(20,20);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new StatsView(),BorderLayout.CENTER);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.Component#setPreferredSize(java.awt.Dimension)
     */
    public Dimension getPreferredSize() {
        // TODO Auto-generated method stub
        //System.err.println("pref " + super.getPreferredSize());
        return new Dimension(super.getPreferredSize().width,400);
    }
    /* (non-Javadoc)
     * @see java.awt.Component#getSize()
     */
    public Dimension getSize() {
        //System.err.println("size " + super.getSize());
        // TODO Auto-generated method stub
        return super.getSize();
    }
}
