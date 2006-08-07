package com.finalist.cmsc.builders;

import net.sf.mmapps.commons.util.EncodingUtil;
import net.sf.mmapps.commons.util.StringUtil;
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
public abstract class ChannelBuilder extends MMObjectBuilder {

    private static final String TMP_OLDPATHNAME = "_oldpathname";

    /** Logger instance. */
    private static Logger log = Logging.getLoggerInstance(ChannelBuilder.class.getName());
   
    private String[] pathManagers;
    private String nameFieldname;
    private String[] fragmentFieldnames;
    private String relationName;
    private int relationNumber;
    
   public boolean init() {
      log.debug("ChannelBuilder init");
      checkAddTmpField(TMP_OLDPATHNAME);
      checkAddTmpField(TreeUtil.OLDPATH_FIELD);
      
      pathManagers = getPathManagers();
      nameFieldname = getNameFieldname();
      fragmentFieldnames = getFragmentFieldname();

      relationName = getRelationName();
      
      relationNumber = MMBase.getMMBase().getRelDef().getNumberByName(relationName);
      
      getFragmentFieldnameForBuilder();
      
      return super.init();
   }

    protected String getFragmentFieldnameForBuilder() {
        return getFragmentFieldnameForBuilder(getTableName());
    }
    
    protected String getFragmentFieldnameForBuilder(String builderName) {
        for (int j = 0; j < pathManagers.length; j++) {
            String treeManager = pathManagers[j];
            if (treeManager.equals(builderName)) {
                return fragmentFieldnames[j];
            }
        }
        throw new IllegalStateException("Fragment field not found for builder " + builderName);
    }

    

   protected abstract String[] getPathManagers();
   protected abstract String getNameFieldname();
   protected abstract String[] getFragmentFieldname();
   protected abstract String getRelationName();

   
   
   public int insert(String owner, MMObjectNode node) {
//      don't use this method to add stuff to the channelCache in the ChannelUtil.
//      The node is inserted before the relation to other nodes is made.
       
       updateEmptyNameField(node);
       return super.insert(owner, node);
   }



   public boolean commit(MMObjectNode objectNode) {
      log.debug(objectNode.getChanged());
      String fragmentFieldname = getFragmentFieldnameForBuilder();
      if (objectNode.getChanged().contains(fragmentFieldname)) {
         
         log.debug("getChanged " + objectNode.getStringValue(fragmentFieldname));
         String pathFragment = objectNode.getStringValue(fragmentFieldname);
         if (!pathFragment.equals(pathFragment.trim())) {
            objectNode.setValue(fragmentFieldname, pathFragment.trim());
         }
         
         TreePathCache.updateCache(getTableName(),  objectNode.getNumber(), objectNode.getStringValue(fragmentFieldname));
      }
      
      updateEmptyNameField(objectNode);
      
      boolean retval = super.commit(objectNode);
      return retval;
   }

    private void updateEmptyNameField(MMObjectNode objectNode) {
        if (StringUtil.isEmpty(objectNode.getStringValue(nameFieldname))) {
            String fragmentFieldname = getFragmentFieldnameForBuilder();
            String pathFragment = objectNode.getStringValue(fragmentFieldname);
            objectNode.setValue(nameFieldname, pathFragment);
        }
        else {
            String fragmentFieldname = getFragmentFieldnameForBuilder();
            if (StringUtil.isEmpty(objectNode.getStringValue(fragmentFieldname))) {
                String name = objectNode.getStringValue(nameFieldname);
                String pathFragment = convertToFragment(name);
                objectNode.setValue(fragmentFieldname, pathFragment);
            }
        }
    }

    private String convertToFragment(String name) {
        String pathFragment = EncodingUtil.convertNonAscii(name);
        pathFragment = pathFragment.replaceAll("\\s", "_");
        pathFragment = pathFragment.replaceAll("[^a-zA-Z_0-9_.-]", "");
        pathFragment = pathFragment.toLowerCase();
        return pathFragment;
    }

   
   /**
    *  called when a value is set. used to store the old value for compare
    *  in the commit() 
    */
   public boolean setValue(MMObjectNode objectNode, String fieldName, Object originalValue) {
      String fragmentFieldname = getFragmentFieldnameForBuilder();
      if (fragmentFieldname.equals(fieldName)) {
         log.debug("setValue() "+TMP_OLDPATHNAME+" to:"+originalValue);
         objectNode.setValue(TMP_OLDPATHNAME, originalValue);
      }
      return super.setValue(objectNode, fieldName, originalValue);
   }
   
   
   public void removeNode(MMObjectNode objectNode) {
      TreePathCache.removeFromCache(getTableName(), objectNode.getNumber());
      super.removeNode(objectNode);
   }
   
   /** Executes a function on the field of a node, and returns the result.
    * In case the function equals "url" the full url including domain name and application root is returned.
    * @param node the node whose fields are queries
    * @param field The function to execute
    * @return the result of the 'function', or null if no valid functions could be determined
    */
   public Object getValue(MMObjectNode node, String field) {
      if (!"number".endsWith(field) && node.getNumber() > 0) {
         if (TreeUtil.PATH_FIELD.equals(field)) {
            String p = getPath(node);
            if (TreeUtil.getLevel(p) <= 1) {
                if (!getTableName().equals(pathManagers[pathManagers.length - 1])) {
                    throw new IllegalArgumentException("Path is requested, but the " +
                            "node is not yet added to the tree.");
                }
            }
            return p;
         }
         if (TreeUtil.OLDPATH_FIELD.equals(field)) {
            String p = getPath(node);
            String oldpathname = node.getStringValue(TMP_OLDPATHNAME);
            if(!StringUtil.isEmpty(oldpathname)) {
               return p.substring(0, p.lastIndexOf('/')+1) + oldpathname.replace(' ', '_');
            }
            return p;
         }
         if (TreeUtil.LEVEL_FIELD.equals(field)) {
            int level = TreeUtil.getLevel(getPath(node));
            return new Integer(level);
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
      String path = TreePathCache.getPathStringFromCache(getTableName(), number);
      if (path == null) {
         Cloud cloud = CloudProviderFactory.getCloudProvider().getAnonymousCloud();
         Node cloudNode = cloud.getNode(String.valueOf(number));
         path = TreeUtil.getPathToRootString(cloudNode, pathManagers, relationName, fragmentFieldnames, true);
      }
      return path;
   }

    @Override
    public void notify(NodeEvent event) {
        int source = event.getNodeNumber();
        switch (event.getType()) {
            case Event.TYPE_CHANGE:
                log.debug("change " + source);
                String path = TreePathCache.getPathStringFromCache(getTableName(), source);
                if (path != null) {
                   String fragmentFieldname = getFragmentFieldnameForBuilder(event.getBuilderName());
                   if (event.getChangedFields().contains(fragmentFieldname)) {
                       String separatedPath[] = path.split(TreeUtil.PATH_SEPARATOR);
                       String pathname = getNode(source).getStringValue(fragmentFieldname);
                       log.debug("Path : " + path + " for " + source + " with pathname : " + pathname);
                       if (!pathname.equals(separatedPath[separatedPath.length - 1])) {
                          TreePathCache.updateCache(getTableName(), source, pathname);
                       }
                   }
                }
                break;
            case Event.TYPE_DELETE:
                log.debug("delete " + source);
                TreePathCache.removeFromCache(getTableName(), source);
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
        if (getTableName().equals(event.getRelationDestinationType()) && relationNumber == event.getRole()) {
            int destination = event.getRelationDestinationNumber();
            MMObjectNode destnode = getNode(destination);

            String path = TreePathCache.getPathStringFromCache(getTableName(), destination);
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
                        TreePathCache.moveCache(getTableName(), destnode.getNumber(), newpath);
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
}
