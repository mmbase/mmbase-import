package org.mmbase.applications.config;
import java.util.*;

/**
 * configuration of a node manager
 * @author Kees Jongenburger
 * @version $Id: NodeManagerConfiguration.java,v 1.6 2002-06-27 19:20:30 kees Exp $
 **/
public interface NodeManagerConfiguration{
    public String getExtends();
    public String getVersion();
    public String getMaintainer();
    public String getClassFile();
    public String getSearchAge();
    public String getName();
    public String getDescription();
    public FieldConfigurations getFieldConfigurations();
}
