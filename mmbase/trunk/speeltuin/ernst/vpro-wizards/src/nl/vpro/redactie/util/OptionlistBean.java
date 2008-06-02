package nl.vpro.redactie.util;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



/**
 * this class is used to create optiolists for multivalue fields in the taglib
 * @author ebunders
 * 
 */
public class OptionlistBean {
    private List<Option> options = new ArrayList<Option>();
    Logger log =  Logging.getLoggerInstance(OptionlistBean.class);
    String value =  null;
    String label = null;

    public void setLabel(String label){
        this.label = label;
        addOption();
    }
    
    public void setValue(String value) {
        this.value = value;
        addOption();
    }
    
    private void addOption() {
        if(label != null && value != null){
            options.add(new Option(value, label));
            label = null;
            value = null;
        }
    }

    public List<Option> getList(){
        return options;
    }
    
    public Option getNewOption(){
        return new Option();
    }
    
    public void setNewOption(Option option){
        options.add(option);
    }
}
