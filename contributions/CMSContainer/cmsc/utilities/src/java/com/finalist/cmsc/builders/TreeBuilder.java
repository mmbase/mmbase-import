package com.finalist.cmsc.builders;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.core.event.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreePathCache;
import com.finalist.cmsc.mmbase.TreeUtil;

/**
 * @author Nico Klasens
 */
public abstract class TreeBuilder extends MMObjectBuilder {

    private static final String TMP_OLDPATHNAME = "_oldpathname";

    /** Logger instance. */
    private static final Logger log = Logging.getLoggerInstance(TreeBuilder.class.getName());
   
   @Override
   public boolean init() {
      log.debug("TreeBuilder init");
      checkAddTmpField(TMP_OLDPATHNAME);
      checkAddTmpField(TreeUtil.OLDPATH_FIELD);
      registerTreeManager();
      return super.init();
   }

    protected String getFragmentFieldnameForBuilder() {
        return getFragmentFieldnameForBuilder(getTableName());
    }
    
    protected String getFragmentFieldnameForBuilder(String builderName) {
        LinkedHashMap<String, String> pathManagers = getPathManagers();
        if (pathManagers.containsKey(builderName)) {
            return pathManagers.get(builderName);
        }
        throw new IllegalStateException("Fragment field not found for builder " + builderName);
    }

    protected int getRelationNumber() {
        int relationNumber = MMBase.getMMBase().getRelDef().getNumberByName(getRelationName());
        return relationNumber;
    }
    
   protected abstract LinkedHashMap<String, String> getPathManagers();
   protected abstract String getNameFieldname();
   protected abstract String getRelationName();

   protected abstract String getFragmentField();
   protected abstract boolean isRoot();

   protected abstract void registerTreeManager();

   
   @Override
   public int insert(String owner, MMObjectNode node) {
//      don't use this method to add stuff to the channelCache in the ChannelUtil.
//      The node is inserted before the relation to other nodes is made.
       
       updateEmptyNameField(node);
       return super.insert(owner, node);
   }



   @Override
   public boolean commit(MMObjectNode objectNode) {
      log.debug(objectNode.getChanged());
      String fragmentFieldname = getFragmentFieldnameForBuilder();
      if (objectNode.getChanged().contains(fragmentFieldname)) {
         
         log.debug("getChanged " + objectNode.getStringValue(fragmentFieldname));
         String pathFragment = objectNode.getStringValue(fragmentFieldname);
         if (!pathFragment.equals(pathFragment.trim())) {
            objectNode.setValue(fragmentFieldname, pathFragment.trim());
         }
         String managerOfRootNode = getRootManagerName();
         TreePathCache.updateCache(managerOfRootNode,  objectNode.getNumber(), objectNode.getStringValue(fragmentFieldname));
      }
      
      updateEmptyNameField(objectNode);
      
      boolean retval = super.commit(objectNode);
      return retval;
   }

    private void updateEmptyNameField(MMObjectNode objectNode) {
        String nameFieldname = getNameFieldname();
        if (StringUtils.isEmpty(objectNode.getStringValue(nameFieldname))) {
            String fragmentFieldname = getFragmentFieldnameForBuilder();
            String pathFragment = objectNode.getStringValue(fragmentFieldname);
            objectNode.setValue(nameFieldname, pathFragment);
        }
        else {
            String fragmentFieldname = getFragmentFieldnameForBuilder();
            if (StringUtils.isEmpty(objectNode.getStringValue(fragmentFieldname))) {
                String name = objectNode.getStringValue(nameFieldname);
                String pathFragment = TreeUtil.convertToFragment(name);
                objectNode.setValue(fragmentFieldname, pathFragment);
            }
        }
    }

   
   /**
    *  called when a value is set. used to store the old value for compare
    *  in the commit() 
    */
   @Override
   public boolean setValue(MMObjectNode objectNode, String fieldName, Object originalValue) {
      String fragmentFieldname = getFragmentFieldnameForBuilder();
      if (fragmentFieldname.equals(fieldName)) {
         log.debug("setValue() "+TMP_OLDPATHNAME+" to:"+originalValue);
         objectNode.setValue(TMP_OLDPATHNAME, originalValue);
      }
      return super.setValue(objectNode, fieldName, originalValue);
   }
   
   
   @Override
   public void removeNode(MMObjectNode objectNode) {
      String managerOfRootNode = getRootManagerName();
      TreePathCache.removeFromCache(managerOfRootNode, objectNode.getNumber());
      super.removeNode(objectNode);
   }
   
   /** Executes a function on the field of a node, and returns the result.
    * In case the function equals "url" the full url including domain name and application root is returned.
    * @param node the node whose fields are queries
    * @param field The function to execute
    * @return the result of the 'function', or null if no valid functions could be determined
    */
   @Override
   public Object getValue(MMObjectNode node, String field) {
      if (!"number".endsWith(field) && node.getNumber() > 0) {
         if (TreeUtil.PATH_FIELD.equals(field)) {
            String p = getPath(node);
            if (TreeUtil.getLevel(p) <= 1) {
                String managerOfRootNode = getRootManagerName();
                if (!getTableName().equals(managerOfRootNode)) {
                    throw new IllegalArgumentException("Path is requested, but the " +
                            "node (" + node.getNumber() + ") is not yet added to the tree.");
                }
            }
            return p;
         }
         if (TreeUtil.OLDPATH_FIELD.equals(field)) {
            String p = getPath(node);
            String oldpathname = node.getStringValue(TMP_OLDPATHNAME);
            if(StringUtils.isNotEmpty(oldpathname)) {
               return p.substring(0, p.lastIndexOf('/')+1) + oldpathname.replace(' ', '_');
            }
            return p;
         }
         if (TreeUtil.LEVEL_FIELD.equals(field)) {
            int level = TreeUtil.getLevel(getPath(node));
            return Integer.valueOf(level);
         }

      }
      
      return super.getValue(node, field);
   }

   private String getPath(MMObjectNode node) {
      int number = node.getNumber();
      return getPath(number);
   }

   private String getPath(int number) {
      // reduce creating useless cloud objects, because this code is executed in the core of MMBase 
      // and clouds belong to the bridge
       
      String managerOfRootNode = getRootManagerName();
      String path = TreePathCache.getPathStringFromCache(managerOfRootNode, number);
      if (path == null) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
         Node cloudNode = cloud.getNode(String.valueOf(number));
         path = TreeUtil.getPathToRootString(cloudNode, getPathManagers(), getRelationName(), true);
      }
      return path;
   }

    @Override
    public void notify(NodeEvent event) {
        int source = event.getNodeNumber();
        switch (event.getType()) {
            case Event.TYPE_CHANGE:
                log.debug("change " + source);
                String managerOfRootNode = getRootManagerName();
                String path = TreePathCache.getPathStringFromCache(managerOfRootNode, source);
                if (path != null) {
                   String fragmentFieldname = getFragmentFieldnameForBuilder(event.getBuilderName());
                   if (event.getChangedFields().contains(fragmentFieldname)) {
                       String separatedPath[] = path.split(TreeUtil.PATH_SEPARATOR);
                       String pathname = getNode(source).getStringValue(fragmentFieldname);
                       log.debug("Path : " + path + " for " + source + " with pathname : " + pathname);
                       if (!pathname.equals(separatedPath[separatedPath.length - 1])) {
                          TreePathCache.updateCache(managerOfRootNode, source, pathname);
                       }
                   }
                }
                break;
            case Event.TYPE_DELETE:
                log.debug("delete " + source);
                TreePathCache.removeFromCache(getRootManagerName(), source);
                break;
            case Event.TYPE_NEW:
                log.debug("new " + source);

                break;
            case NodeEvent.TYPE_RELATION_CHANGE:
                log.debug("relation change " + event);
                
                break;
            default:
                log.debug("default? " + source);
                break;
        }
        super.notify(event);
        
    }

    @Override
    public void notify(RelationEvent event) {
        int relationNumber = getRelationNumber();
        if (getTableName().equals(event.getRelationDestinationType()) && relationNumber == event.getRole()) {
            int destination = event.getRelationDestinationNumber();
            MMObjectNode destnode = getNode(destination);

            String managerOfRootNode = getRootManagerName();
            String path = TreePathCache.getPathStringFromCache(managerOfRootNode, destination);
            switch (event.getType()) {
                case Event.TYPE_DELETE:
                    log.debug("delete relation to " + destination + " " + path);
                    if (path != null) {
                        log.debug("delete cut action " + destination + " " + path + " (this should happen after the new relation)");
                    }
                    break;
                case Event.TYPE_NEW:
                    log.debug("new relation to " + destination + " " + path);
                    if (path != null) {
                        int source = event.getRelationSourceNumber();
                        String fragmentFieldname = getFragmentFieldnameForBuilder();
                        String newparentpath = getPath(source);
                        String pathname = destnode.getStringValue(fragmentFieldname);

                        String newpath = newparentpath + TreeUtil.PATH_SEPARATOR + pathname;
                        TreePathCache.moveCache(managerOfRootNode, destnode.getNumber(), newpath);
                        log.debug("new cut action " + destination + " " + path + " " + newpath);
                    }
                    break;
                default:
                    log.debug("default? " + destination);
                    break;
            }
        }
        
        super.notify(event);
    }

    private String getRootManagerName() {
        String managerOfRootNode = TreeUtil.getRootManager(getPathManagers());
        return managerOfRootNode;
    }
}
