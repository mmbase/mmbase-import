package stats;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;


public class Stats  extends FormPanel {
    StatsView statsView;
    FormAccessor accessor ;
    JComboBox timeSpanSelect;
    
    public void up2date(){
        getButton("prevButton").setEnabled(statsView.startX >statsView.getMinX());            
        getButton("nextButton").setEnabled(statsView.startX + statsView.timeSpan < statsView.getMaxX());
        getLabel("startDate").setText("" + new Date(statsView.startX));
        getLabel("endDate").setText("" + new Date(statsView.startX + statsView.timeSpan));
    }
    
    public Stats(){
        super("stats/gui.jfrm");
        accessor = (FormAccessor)getFormAccessor( "main" );
        statsView = new StatsView();
        accessor.replaceBean( "stats.view", statsView );
        setText("urlTextField","http://localhost:8080/mmbase/admin/cpustats.jsp");
        JButton button = (JButton)getButton("loadButton");
        
        button.setAction(new LoadStats());
        button.setText("load");
        
        getButton("prevButton").setAction(new prevAction());
        getButton("prevButton").setText("<<");
        
        getButton("nextButton").setAction(new nextAction());
        getButton("nextButton").setText(">>");
        timeSpanSelect = accessor.getComboBox("timeSpanSelect");
        timeSpanSelect.addItem(new Option("2 hours",1000 * 60 * 60 *2));
        timeSpanSelect.addItem(new Option("1 day",1000* 60 * 60 *24));
        timeSpanSelect.addItemListener(new TimeSpanListener() );
        
    }
    
    public static void main(String[] argv){
        JFrame frame = new JFrame();
        frame.setBackground(Color.WHITE);
        frame.setSize(800, 600);
        frame.setLocation(100, 100);
        frame.getContentPane().add(new Stats());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    class LoadStats extends AbstractAction {
        
        public void actionPerformed(ActionEvent arg0) {
            try {
                statsView.load(getText("urlTextField"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            up2date();
        }
    }
    
    static class Option{
        public Option(String name,long value){
            this.name = name;
            this.value = value;
        }
        
        public String name;
        public long value;
        public String toString(){
            return name;
        }
    }
    class prevAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent arg0) {
            
            statsView.setStartX(statsView.getStartX() - statsView.timeSpan /2);
            up2date();
        }
    }
    
    class nextAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent arg0) {
            
            statsView.setStartX(statsView.getStartX() + statsView.timeSpan /2);
            up2date();
        }
    }
    
    class TimeSpanListener implements ItemListener{
        public void itemStateChanged( ItemEvent event ){
            if( event.getSource() == timeSpanSelect
                    && event.getStateChange() == ItemEvent.SELECTED )
            {
                statsView.setTimeSpan( ((Option)timeSpanSelect.getSelectedItem() ).value);
                //System.out.println( "Change:"	+ timeSpanSelect.getSelectedItem() );
                up2date();
            }
        }
    }
    
}
