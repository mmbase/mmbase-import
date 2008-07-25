package com.finalist.cmsc.mmbase;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;

public class RelationUtil {

   public final static String RELATION_POS_FIELD = "pos";    
    
   public static List<Integer> reorder(Cloud cloud, String parent, String childs, String role) {
       return reorder(cloud, parent, childs, role, null);
   }
    
	public static List<Integer> reorder(Cloud cloud, String parent, String children, String role, String nodeManagerName) {
		Node parentNode = cloud.getNode(parent);
		return reorder(parentNode, children, role, nodeManagerName);
	}

    public static List<Integer> reorder(Cloud cloud, String parent, String[] children, String role) {
        return reorder(cloud, parent, children, role, null);
    }
    
	public static List<Integer> reorder(Cloud cloud, String parent, String[] children, String role, String nodeManagerName) {
		Node parentNode = cloud.getNode(parent);
		return reorder(parentNode, children, role, nodeManagerName);
	}

    public static List<Integer> reorder(Node parentNode, List<String> children, String role) {
        return reorder(parentNode, children, role, null);
    }
    
    public static List<Integer> reorder(Node parentNode, Object children, String role, String nodeManagerName) {
        return reorder(parentNode, children, role, nodeManagerName, 0);
    }
    
    public static List<Integer> reorder(Node parentNode, Object children, String role, String nodeManagerName, int offset) {
    	return reorder(parentNode, children, role, nodeManagerName, RELATION_POS_FIELD, offset);
	}

    private static List<Integer> reorder(Node parentNode, Object children, String role, String nodeManagerName, String field, int offset) {

        List<String> childrenList = convertChildrenToList(children);
        
        List<Integer> changedPositions = new ArrayList<Integer>();
        RelationList list = null;
        Cloud cloud = parentNode.getCloud();
        if (StringUtils.isNotEmpty(nodeManagerName)) {
            list = parentNode.getRelations(role,  cloud.getNodeManager(nodeManagerName), "DESTINATION");
        }
        else {
		    list = parentNode.getRelations(role);
        }
		RelationIterator iter = list.relationIterator();
		while (iter.hasNext()) {
			Relation rel = iter.nextRelation();
			int destination = rel.getDestination().getNumber();
			int posOfRelation = rel.getIntValue(field);
			String destNumber = String.valueOf(destination);
            if (childrenList.contains(destNumber)) {
                int pos = offset + childrenList.indexOf(destNumber) + 1;
                if (pos != posOfRelation) {
                    rel.setIntValue(field, pos);
                    rel.commit();
                    changedPositions.add(destination);
                }
            }
		}
		return changedPositions;
    }

    private static List<String> convertChildrenToList(Object children) {
        List<String> childrenList = null;
        if (children instanceof List) {
            childrenList = (List<String>) children;
        }
        if (children instanceof String) {
            StringTokenizer tokenizer = new StringTokenizer((String) children, ",");
            childrenList = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                childrenList.add(tokenizer.nextToken());
            }
        }
        if (children instanceof String[]) {
            childrenList = new ArrayList<String>();
            for (int i = 0; i < ((String[]) children).length; i++) {
                childrenList.add(((String[]) children)[i]);
            }
        }
        if (childrenList == null) {
            throw new IllegalArgumentException("Children not of the supported types (String, String[], List)");
        }
        return childrenList;
    }

    public static void recalculateChildPositions(Node parentNode, String role, String nodeManagerName) {
        recalculateChildPositions(parentNode, role, nodeManagerName, RELATION_POS_FIELD, RELATION_POS_FIELD);
    }

    private static void recalculateChildPositions(Node parentNode, String role, String nodeManagerName, String field, String sortName) {
        if (StringUtils.isEmpty(nodeManagerName)) {
            throw new IllegalArgumentException("nodemanager name can not be null");
        }
        RelationList list = SearchUtil.findRelations(parentNode, nodeManagerName, role, sortName, null);
        int pos = 1;
        for (Iterator<Relation> iter = list.iterator(); iter.hasNext(); pos++) {
            Relation relation = iter.next();
            int currentPos = relation.getIntValue(field);
            if (pos != currentPos) {
                relation.setIntValue(field, pos);
                relation.commit();
            }

        }
    }
    
    public static Relation createRelation(Node sourceNode, Node destNode, String role) {
        Cloud cloud = sourceNode.getCloud();
        RelationManager relationManager = cloud.getRelationManager(sourceNode.getNodeManager(), 
                                                destNode.getNodeManager(), role);
        Relation relation = sourceNode.createRelation(destNode, relationManager);
        relation.commit();
        
        return relation;
    }
    
    public static Relation createCountedRelation(Node parentNode, Node childNode, String relationName, String countField) {
        int newCount = getLastCount(parentNode, relationName, countField) + 1;
        return createCountedRelation(parentNode, childNode, relationName, countField, newCount);
    }

    public static int getLastCount(Node parentNode, String relationName, String countField) {
        Cloud cloud = parentNode.getCloud();
        NodeManager manager = cloud.getRelationManager(relationName);

        NodeQuery query = manager.createQuery();
        SearchUtil.addEqualConstraint(query, manager.getField("snumber"), parentNode.getNumber());
        
        Query aggregate = query.aggregatingClone();
        aggregate.addAggregatedField(query.getStep(manager.getName()), manager.getField(countField), AggregatedField.AGGREGATION_TYPE_MAX);

        NodeList list = cloud.getList(aggregate);
        if (list.size() > 0) {
            Node aggregatedNode = list.getNode(0);
            if (aggregatedNode != null) {
                int lastCount = aggregatedNode.getIntValue(countField);
                return lastCount;
            }
        }
        return 0;
    }
    
    public static Relation createCountedRelation(Node parentNode, Node childNode, String relationName, String countField, int newCount) {
        Cloud cloud = parentNode.getCloud();
        RelationManager relationManager = cloud.getRelationManager(parentNode.getNodeManager(), childNode.getNodeManager(), relationName);
        Relation relation = parentNode.createRelation(childNode, relationManager);
        relation.setIntValue(countField, newCount);
        relation.commit();
        
        return relation;
    }
    
    public static boolean exists(NodeManager manager, int source, int destination) {
        NodeList list = getRelations(manager, source, destination);
        return !list.isEmpty();
    }

    public static Relation getRelation(NodeManager manager, int source, int destination) {
        NodeList list = getRelations(manager, source, destination);
        if (!list.isEmpty()) {
            Node node = list.getNode(0);
            if (node.isRelation()) {
                return node.toRelation();
            }
        }
        return null;
    }
    
    public static NodeList getRelations(NodeManager manager, int source, int destination) {
        NodeQuery query = manager.createQuery();
        Constraint s = SearchUtil.createEqualConstraint(query, manager.getField("snumber"), source);
        Constraint d = SearchUtil.createEqualConstraint(query, manager.getField("dnumber"), destination);
        Constraint composite = query.createConstraint(s, CompositeConstraint.LOGICAL_AND, d);
        query.setConstraint(composite);
        
        NodeList list = manager.getList(query);
        return list;
    }
}
