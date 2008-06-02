package nl.vpro.redactie.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class WizardConfig {
    protected List<PathElement> pathElements = new ArrayList<PathElement>();
    protected String title;
    protected Boolean flush;
    protected String flushName;
    protected List<TagConfig> tags = new ArrayList<TagConfig>();
    

    public void setPathElement(PathElement pathElement){
        pathElements.add(pathElement);
        
    }
    
    

    public Boolean isFlush() {
        return flush;
    }



    public void setFlush(Boolean flush) {
        this.flush = flush;
    }



    public String getFlushName() {
        return flushName;
    }



    public void setFlushName(String flushName) {
        this.flushName = flushName;
    }



    public List<PathElement> getPathElements() {
        return pathElements;
    }



    public void setPathElements(List<PathElement> pathElements) {
        this.pathElements = pathElements;
    }



    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }



    public String toString(){
        return new ToStringBuilder(this).append(title).append(pathElements).append(flush).append(flushName).toString();
    }
}
