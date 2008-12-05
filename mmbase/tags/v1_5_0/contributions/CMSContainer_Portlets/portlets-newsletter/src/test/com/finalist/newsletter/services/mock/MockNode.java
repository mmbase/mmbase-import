package com.finalist.newsletter.services.mock;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.*;

public class MockNode implements Node {

   private Map map = new HashMap();

   public Cloud getCloud() {
      return null;
   }

   public NodeManager getNodeManager() {
      return null;
   }

   public int getNumber() {
      return 0;
   }

   public boolean isRelation() {
      return false;
   }

   public Relation toRelation() {
      return null;
   }

   public boolean isNodeManager() {
      return false;
   }

   public NodeManager toNodeManager() {
      return null;
   }

   public boolean isRelationManager() {
      return false;
   }

   public RelationManager toRelationManager() {
      return null;
   }

   public void setValue(String s, Object o) {

   }

   public void setValueWithoutProcess(String s, Object o) {

   }

   public void setObjectValue(String s, Object o) {

   }

   public void setBooleanValue(String s, boolean b) {

   }

   public void setNodeValue(String s, Node node) {

   }

   public void setIntValue(String s, int i) {
      map.put("s",i);
   }

   public void setFloatValue(String s, float v) {

   }

   public void setDoubleValue(String s, double v) {

   }

   public void setByteValue(String s, byte[] bytes) {

   }

   public void setInputStreamValue(String s, InputStream inputStream, long l) {

   }

   public void setLongValue(String s, long l) {

   }

   public void setStringValue(String s, String s1) {

   }

   public void setDateValue(String s, Date date) {

   }

   public void setListValue(String s, List list) {

   }

   public boolean isNull(String s) {
      return false;
   }

   public long getSize(String s) {
      return 0;
   }

   public Object getValue(String s) {
      return null;
   }

   public Object getObjectValue(String s) {
      return null;
   }

   public Object getValueWithoutProcess(String s) {
      return null;
   }

   public boolean getBooleanValue(String s) {
      return false;
   }

   public Node getNodeValue(String s) {
      return null;
   }

   public int getIntValue(String s) {
      return (Integer)map.get(s);
   }

   public float getFloatValue(String s) {
      return 0;
   }

   public long getLongValue(String s) {
      return 0;
   }

   public double getDoubleValue(String s) {
      return 0;
   }

   public byte[] getByteValue(String s) {
      return new byte[0];
   }

   public InputStream getInputStreamValue(String s) {
      return null;
   }

   public String getStringValue(String s) {
      return null;
   }

   public Date getDateValue(String s) {
      return null;
   }

   public List getListValue(String s) {
      return null;
   }

   public FieldValue getFieldValue(String s) throws NotFoundException {
      return null;
   }

   public FieldValue getFieldValue(Field field) {
      return null;
   }

   public Collection validate() {
      return null;
   }

   public void commit() {

   }

   public void cancel() {

   }

   public void delete() {

   }

   public boolean isNew() {
      return false;
   }

   public boolean isChanged(String s) {
      return false;
   }

   public Set getChanged() {
      return null;
   }

   public boolean isChanged() {
      return false;
   }

   public void delete(boolean b) {

   }

   public Document getXMLValue(String s) throws IllegalArgumentException {
      return null;
   }

   public Element getXMLValue(String s, Document document) throws IllegalArgumentException {
      return null;
   }

   public void setXMLValue(String s, Document document) {

   }

   public boolean hasRelations() {
      return false;
   }

   public void deleteRelations() {

   }

   public void deleteRelations(String s) {

   }

   public RelationList getRelations() {
      return null;
   }

   public RelationList getRelations(String s) {
      return null;
   }

   public RelationList getRelations(String s, String s1) {
      return null;
   }

   public RelationList getRelations(String s, NodeManager nodeManager) {
      return null;
   }

   public RelationList getRelations(String s, NodeManager nodeManager, String s1) {
      return null;
   }

   public int countRelations() {
      return 0;
   }

   public int countRelations(String s) {
      return 0;
   }

   public NodeList getRelatedNodes() {
      return null;
   }

   public NodeList getRelatedNodes(String s) {
      return null;
   }

   public NodeList getRelatedNodes(NodeManager nodeManager) {
      return null;
   }

   public NodeList getRelatedNodes(String s, String s1, String s2) {
      return null;
   }

   public NodeList getRelatedNodes(NodeManager nodeManager, String s, String s1) {
      return null;
   }

   public int countRelatedNodes(String s) {
      return 0;
   }

   public int countRelatedNodes(NodeManager nodeManager, String s, String s1) {
      return 0;
   }

   public StringList getAliases() {
      return null;
   }

   public void createAlias(String s) {

   }

   public void deleteAlias(String s) {

   }

   public Relation createRelation(Node node, RelationManager relationManager) {
      return null;
   }

   public void setContext(String s) {

   }

   public String getContext() {
      return null;
   }

   public StringList getPossibleContexts() {
      return null;
   }

   public boolean mayWrite() {
      return false;
   }

   public boolean mayDelete() {
      return false;
   }

   public boolean mayChangeContext() {
      return false;
   }

   public Collection getFunctions() {
      return null;
   }

   public Function getFunction(String s) {
      return null;
   }

   public Parameters createParameters(String s) {
      return null;
   }

   public FieldValue getFunctionValue(String s, List list) {
      return null;
   }

   public int compareTo(Object o) {
      return 0;
   }
}
