package org.mmbase.applications.crontab.builders;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import org.mmbase.applications.crontab.*;
import org.mmbase.bridge.*;
/**
 *
 * @mmbase-application-name MMCrontabApp
 *
 * @mmbase-nodemanager-name crontab
 * @mmbase-nodemanager-classfile org.mmbase.applications.crontab.builders.JCrontabBuilder
 * @mmbase-nodemanager-field name string
 * @mmbase-nodemanager-field crontime string
 * @mmbase-nodemanager-field classfile string
 *
 * @mmbase-relationtype-name related
 *
 * @mmbase-relationmanager-source crontab
 * @mmbase-relationmanager-destination mmservers
 * @mmbase-relationmanager-type related
 */
public class JCrontabBuilder extends MMObjectBuilder{
    
    JCronDaemon jCronDaemon= null;
    
    public JCrontabBuilder() {
    }
    
    public void setMMBase(MMBase mmbase){
        super.setMMBase(mmbase);
        //start the jCronDaemon
        jCronDaemon = JCronDaemon.getInstance();
        NodeIterator nodeIterator = LocalContext.getCloudContext().getCloud("mmbase").getNodeManager("crontab").getList(null,null,null).nodeIterator();
        while(nodeIterator.hasNext()){
            jCronDaemon.add(createJCronEntry(nodeIterator.nextNode()));
        }
        
    }
    
    public int insert(String owner, MMObjectNode objectNodenode) {
        int number =super.insert(owner,objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(number);
        jCronDaemon.add(createJCronEntry(node));
        return number;
    }
    
    private JCronEntry createJCronEntry(Node node){
        try {
            return new JCronEntry("" + node.getNumber(),node.getStringValue("crontime"),node.getStringValue("name"),node.getStringValue("classfile"));
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public boolean commit(MMObjectNode objectNodenode){
        boolean retval = super.commit(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        return retval;
    }
    
    public void removeNode(MMObjectNode objectNodenode){
        super.removeNode(objectNodenode);
        Node node = LocalContext.getCloudContext().getCloud("mmbase").getNode(objectNodenode.getNumber());
        //jCronDaemon.getJCronEntries().getJCronEntry("" +  node.getNumber()).();
    }
}
