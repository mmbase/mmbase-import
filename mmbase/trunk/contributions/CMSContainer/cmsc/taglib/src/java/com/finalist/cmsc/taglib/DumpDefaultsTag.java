package com.finalist.cmsc.taglib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;

public class DumpDefaultsTag extends SimpleTagSupport {
	
	private static Log log = LogFactory.getLog(DumpDefaultsTag.class);
	
	private String path;

	private class DumpingNode {
		private String nodeType;
		private List<DumpingConstraint> childConstraints = new ArrayList<DumpingConstraint>();
		private List<DumpingNode> childNodes = new ArrayList<DumpingNode>();
		
		public DumpingNode(String nodeType) {
			this.nodeType = nodeType;
		}
		public void addChildConstraint(DumpingConstraint constraint) {
			childConstraints.add(constraint);
		}
		public void addChildNode(DumpingNode node) {
			childNodes.add(node);
		}
		public List<DumpingNode> getChildNodes() {
			return childNodes;
		}
		public List<DumpingConstraint> getChildConstraints() {
			return childConstraints;
		}
		public String getNodeType() {
			return nodeType;
		}
		public boolean meetsChildConstraints(Node node) {
			for(Iterator<DumpingConstraint> i = childConstraints.iterator(); i.hasNext();) {
				if(!i.next().meets(node)) {
					return false;
				}
			}
			return true;
		}
	}
	
	
	private class DumpingConstraint {
		public final static int OPERATOR_EQUALS = 0;
		private String field;
		private int operator;
		private String value;
		public DumpingConstraint(String field, int operator, String value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}
		public boolean meets(Node node) {
			String fieldValue = node.getStringValue(field);
			switch(operator) {
				case OPERATOR_EQUALS: return value.equals(fieldValue);
			}
			return false;
		}
		public String getField() {
			return field;
		}
		public int getOperator() {
			return operator;
		}
		public String getValue() {
			return value;
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void doTag() throws JspException, IOException {

		PageContext ctx = (PageContext) getJspContext();
		List<DumpingNode> dumpingNodes = buildDumpingNodesForDefaults();
		String report = doBackup(dumpingNodes);
		ctx.getOut().write(report);
		
	}
	
	private List<DumpingNode> buildDumpingNodesForDefaults() {
		List<DumpingNode> dumpingNodes = new ArrayList<DumpingNode>();
		dumpingNodes.add(new DumpingNode("stylesheet"));
		dumpingNodes.add(new DumpingNode("view"));
		dumpingNodes.add(new DumpingNode("portletdefinition"));
	 	 												  
		DumpingNode dnPortletparameter = new DumpingNode("portletparameter");
		
		DumpingNode dnPortlet = new DumpingNode("portlet");
		dnPortlet.addChildNode(dnPortletparameter);
		
		DumpingNode dnPortletdefinition = new DumpingNode("portletdefinition");
		dnPortletdefinition.addChildConstraint(new DumpingConstraint("type", DumpingConstraint.OPERATOR_EQUALS, "single"));
		dnPortletdefinition.addChildNode(dnPortlet);
		
		DumpingNode dnLayout = new DumpingNode("layout");
		dnLayout.addChildNode(dnPortletdefinition);
		dumpingNodes.add(dnLayout);
		
		return dumpingNodes;
	}


	private String doBackup(List<DumpingNode> dumpingNodes) throws IOException {
		HashMap<String,HashSet<Node>> backupMap = buildBackupMap(dumpingNodes);
		String report = exportBackupMap(backupMap);
		return report;
	}


	private String exportBackupMap(HashMap<String,HashSet<Node>> backupMap) throws IOException {
		StringBuffer report = new StringBuffer();
		
		for(Iterator<String> i = backupMap.keySet().iterator(); i.hasNext();) {
			String key = i.next();
			HashSet<Node> values = backupMap.get(key);
			
			report.append(key).append(" ").append(values.size()).append(" nodes. <br/>");
			
			Calendar cal = Calendar.getInstance();
			long htimestamp = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100
					+ cal.get(Calendar.DAY_OF_MONTH);
			long ltimestamp = cal.get(Calendar.AM_PM) * 120000 + cal.get(Calendar.HOUR) * 10000 + cal.get(Calendar.MINUTE)
					* 100 + cal.get(Calendar.SECOND);
			long timestamp = (htimestamp * 1000000) + ltimestamp;
			
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("<").append(key).append(" exportsource=\"cmsc file dumper\" timestamp=\"").append(timestamp).append("\">\n");
			for(Iterator ni = values.iterator(); ni.hasNext();) {
				Node node = (Node) ni.next();
				sb.append("\t<node number=\"").append(node.getNumber()).append("\" owner=\"").append(node.getContext());
				if(node instanceof Relation) {
					Relation relation = (Relation) node;
					sb.append("\" snumber=\"").append(relation.getSource().getNumber());
					sb.append("\" dnumber=\"").append(relation.getDestination().getNumber());
					sb.append("\" rtype=\"").append(key);
					
					if (relation.getIntValue("dir") == 1) {
						sb.append("\" dir=\"").append("unidirectional");
					} else {
						sb.append("\" dir=\"").append("bidirectional");
					}
				}
				sb.append("\">\n");
				
				
				for(FieldIterator fi = node.getNodeManager().getFields().fieldIterator(); fi.hasNext();) {
					Field field = fi.nextField();
					if(field.getState() == Field.STATE_PERSISTENT) {
						String fieldName = field.getName();
						if(!node.isNull(fieldName)) {
							sb.append("\t\t<").append(fieldName).append(">");
							sb.append(node.getStringValue(fieldName)); 
							sb.append("</").append(fieldName).append(">\n");
						}
					}
				}
				sb.append("\t</node>\n");
			}
			sb.append("</" + key + ">");
			
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path+"/"+key+".xml");
				fos.write(sb.toString().getBytes());
			}
			finally {
				if(fos != null) {
					fos.close();
				}
			}
		}
		
		return report.toString();
	}


	private HashMap<String,HashSet<Node>> buildBackupMap(List<DumpingNode> dumpingNodes) {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		HashMap<String,HashSet<Node>> backupMap = new HashMap<String,HashSet<Node>>();
		for(Iterator<DumpingNode> i = dumpingNodes.iterator(); i.hasNext();) {
			buildBackupMapNode(cloud, backupMap, i.next());
		}
		
		return backupMap;
	}


	private void buildBackupMapNode(Cloud cloud, HashMap<String, HashSet<Node>> backupMap, DumpingNode dumpingNode) {
		NodeList nodeList = cloud.getNodeManager(dumpingNode.getNodeType()).getList(null, null, null);

		for(NodeIterator ni = nodeList.nodeIterator(); ni.hasNext();) {
			Node node = ni.nextNode();
			addNodeToBackup(backupMap, node, dumpingNode);
		}
	}

	
	private void addNodeToBackup(HashMap<String,HashSet<Node>> backupMap, Node node, DumpingNode dumpingNode) {
		String type = node.getNodeManager().getName();
		HashSet<Node> set = backupMap.get(type);
		if(set == null) {
			set = new HashSet<Node>();
			backupMap.put(type, set);
		}
		
		set.add(node);
			
		if(dumpingNode.meetsChildConstraints(node)) {
			// add child nodes
			for(Iterator<DumpingNode> i = dumpingNode.getChildNodes().iterator(); i.hasNext();) {
				DumpingNode childDumpingNode = i.next();
				
				RelationList relationList = node.getRelations(null, childDumpingNode.getNodeType());
				for(RelationIterator ri = relationList.relationIterator(); ri.hasNext();) {
					Relation relation = ri.nextRelation();
					addRelationToBackup(backupMap, relation);
					Node childNode = relation.getDestination();
					if(childNode.getNumber() == node.getNumber()) {
						childNode = relation.getSource();
						log.info("using source!!!");
					}
					addNodeToBackup(backupMap, childNode, childDumpingNode);
				}
			}
		}
	}

	
	private void addRelationToBackup(HashMap<String,HashSet<Node>> backupMap, Relation relation) {
		String type = relation.getNodeManager().getName();
		HashSet<Node> set = backupMap.get(type);
		if(set == null) {
			set = new HashSet<Node>();
			backupMap.put(type, set);
		}
		set.add(relation);
	}
}
