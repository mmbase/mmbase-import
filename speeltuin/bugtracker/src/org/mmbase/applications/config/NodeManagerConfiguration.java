package org.mmbase.applications.config;
import java.util.*;

public interface NodeManagerConfiguration{
	public String getExtends();
	public String getNodeManagerName();
	public FieldConfigurations getFieldConfigurations();
}
