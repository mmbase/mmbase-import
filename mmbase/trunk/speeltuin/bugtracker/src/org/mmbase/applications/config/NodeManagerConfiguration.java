package org.mmbase.applications.config;
import java.util.*;

public interface NodeManagerConfiguration{
	public String getExtends();
	public String getVersion();
	public String getMaintainer();
	public String getClassFile();
	public String getSearchAge();
	public String getNodeManagerName();
        public String getDescription();
	public FieldConfigurations getFieldConfigurations();
}
