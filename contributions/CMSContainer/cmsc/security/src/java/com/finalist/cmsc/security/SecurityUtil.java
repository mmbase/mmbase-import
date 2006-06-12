/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.security;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreePathComparator;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.security.builders.Contexts;
import com.finalist.cmsc.util.NameUtil;


public class SecurityUtil {

    private static final String RANKREL = "rank";

    private static final String RANK = "mmbaseranks";

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(SecurityUtil.class.getName());

    private static final String CMSC_USERROLES = "cmsc.userroles.";

    public static final String ROLEREL = "rolerel";
    public static final String CONTAINS = "contains";

    public static final String USER = "user";
    public static final String GROUP = "mmbasegroups";

    public static final String NUMBER_FIELD = "number";
    public static final String USERNAME_FIELD = "username";
    public static final String ROLE_FIELD = "role";
    
    private static final String DESTINATION = "DESTINATION";
    private static final String SOURCE = "SOURCE";

    private SecurityUtil() {
        // utility
    }
    
    public static Node getUserNode(Cloud userCloud) {
//        User user = (User) userCloud.getUser();
//        return userCloud.getNode(user.getNode().getNumber());
        return SearchUtil.findNode(userCloud, USER, USERNAME_FIELD, userCloud.getUser().getIdentifier());
    }

    public static Node getUserNode(Cloud cloud, String username) {
        return SearchUtil.findNode(cloud, USER, USERNAME_FIELD, username);
    }
    
    public static boolean isLoggedInUser(Cloud cloud, Node userNode) {
//        User user = (User) cloud.getUser();
        return cloud.getUser().getIdentifier().equals(userNode.getStringValue(USERNAME_FIELD));
    }

    public static UserRole getRoleForUser(Node channel, boolean rightsInherited, TreeMap<String,UserRole> channelsWithRole) {
       String path = channel.getStringValue(TreeUtil.PATH_FIELD);
       UserRole resultRole = getRoleForUser(path, channelsWithRole);
       if (resultRole == null) {
           resultRole = new UserRole(Role.NONE, rightsInherited);
       }
       return resultRole;
    }

    private static UserRole getRoleForUser(String path, TreeMap<String, UserRole> channelsWithRole) {
       UserRole resultRole = null;
       Iterator keysIter = channelsWithRole.keySet().iterator();
       while (keysIter.hasNext()) {
          String keyPath = (String) keysIter.next();

          // most specific keys are first in order so when the path of the channel startswith
          // the keypath we have the channel where the rights are from inherited.
          if (path.startsWith(keyPath)) {
             UserRole userRole = channelsWithRole.get(keyPath);
             // when path is equal to the keypath than is the role not inherited
             resultRole = new UserRole(userRole.getRole(), !path.equals(keyPath));
             break;
          }
       }
       return resultRole;
    }
    
    public static Map<Integer,UserRole> buildRolesFromRequest(HttpServletRequest request) {
        Map<Integer,UserRole> roles = new HashMap<Integer,UserRole>();
        Enumeration requestEnum = request.getParameterNames();
        while (requestEnum.hasMoreElements()) {
           String name= (String) requestEnum.nextElement();
           if (name.startsWith("role_")) {
              String role = request.getParameter(name);
              if (!role.equals("-1")) {
                 roles.put( new Integer(name.substring(5)), new UserRole( Integer.parseInt(role),false));
              }
              else {
                 roles.put( new Integer(name.substring(5)), null);
              }
           }
        }
        return roles;
    }

    public static TreeMap<String,UserRole> getLoggedInRoleMap(Cloud cloud, String managerName,
            String relationName, String fragmentFieldname) {

        String[] treeManagers = new String[] { managerName };
        String[] fragmentFieldnames = new String[] { fragmentFieldname };
        return getLoggedInRoleMap(cloud, treeManagers, relationName, fragmentFieldnames);
    }

    
    public static TreeMap<String, UserRole> getLoggedInRoleMap(Cloud cloud, String[] treeManagers,
            String relationName, String[] fragmentFieldname) {

        String channelName = treeManagers[0];
        TreeMap<String, UserRole> channelsWithRole = (TreeMap<String, UserRole>) cloud
                .getProperty(CMSC_USERROLES + channelName);
        if (channelsWithRole == null) {
            // use a TreeMap so the that keys (in our case paths) are ordered
            channelsWithRole = getNewRolesMap();

            Node user = getUserNode(cloud);
            NodeList groups = getGroups(user);
            if (groups.size() >= 1) {
                Node group = groups.getNode(0);
                fillChannelsWithRole(group, channelsWithRole, treeManagers, relationName, fragmentFieldname);
                if (groups.size() > 1) {
                    for (int i = 1; i < groups.size(); i++) {
                        TreeMap<String, UserRole> extraRoles = getNewRolesMap();
                        Node extraGroup = groups.getNode(i);
                        fillChannelsWithRole(extraGroup, extraRoles, treeManagers, relationName, fragmentFieldname);

                        for (Map.Entry<String, UserRole> entry : channelsWithRole.entrySet()) {
                            String path = entry.getKey();
                            UserRole channelRole = entry.getValue();
                            UserRole extraRole = getRoleForUser(path, extraRoles);
                            if (extraRole != null && channelRole.getRole().getId() < extraRole.getRole().getId()) {
                                channelsWithRole.put(path, extraRole);
                            }
                        }
                        
                        for (Map.Entry<String, UserRole> entry : extraRoles.entrySet()) {
                            String extraPath = entry.getKey();
                            UserRole extraRole = entry.getValue();
                            UserRole channelRole = getRoleForUser(extraPath, channelsWithRole);
                            if (channelRole != null && channelRole.getRole().getId() < extraRole.getRole().getId()) {
                                channelsWithRole.put(extraPath, extraRole);
                            }
                        }
                    }
                }
            }

            cloud.setProperty(CMSC_USERROLES + channelName, channelsWithRole);
        }
        return channelsWithRole;
    }

    public static TreeMap<String,UserRole> getNewRolesMap() {
        return new TreeMap<String,UserRole>(new TreePathComparator());
    }

    public static void fillChannelsWithRole(Node user, TreeMap<String,UserRole> channelsWithRole, String managerName,
            String relationName, String fragmentFieldname) {
        String[] treeManagers = new String[] { managerName };
        String[] fragmentFieldnames = new String[] { fragmentFieldname };
        fillChannelsWithRole(user, channelsWithRole, treeManagers, relationName, fragmentFieldnames);
    }

    /**
     * Fills a Map with the role for the channels with a rolerel to the given group.
     * @param group Node of group
     * @param channelsWithRole Map to fill with roles
     * @param treeManagers managers used in the tree
     * @param relationName relation between the managers
     * @param fragmentFieldname fieldname the path is constructed with
     */
    public static void fillChannelsWithRole(Node group, TreeMap<String,UserRole> channelsWithRole,
            String[] treeManagers, String relationName, String[] fragmentFieldname) {
        Cloud cloud = group.getCloud();

        for (int i = treeManagers.length - 1; i >= 0; i--) {
            String managerName = treeManagers[i];

            // retrieve a list of all channels with a rolerel to this group
            NodeList channelNodeList = cloud.getList(null, 
                    managerName + "," + ROLEREL + "," + GROUP, 
                    ROLEREL + "." + ROLE_FIELD+ ", " + managerName + "." + NUMBER_FIELD, 
                    "[" + GROUP + "." + NUMBER_FIELD + "] = " + group.getNumber(),
                    null, null, null, true);

            NodeIterator nodeIterator = channelNodeList.nodeIterator();
            while (nodeIterator.hasNext()) {
                Node channelRoleGroupNode = nodeIterator.nextNode();
                Node channelNode = cloud.getNode( channelRoleGroupNode.getStringValue(managerName + "." + NUMBER_FIELD));
                // no path found for this channel in cache retrieve it from mmbase
                String path = TreeUtil.getPathToRootString(channelNode, treeManagers, relationName, fragmentFieldname, true);
                int role = channelRoleGroupNode.getIntValue(ROLEREL + "." + ROLE_FIELD);
                channelsWithRole.put(path, new UserRole(role, false));
            }
        }
    }

    public static void setGroupRights(Cloud cloud, Node user, Map rights, String managerName) {
        String[] treeManagers = new String[] { managerName };
        setGroupRights(cloud, user, rights, treeManagers);
    }
    
    public static void setGroupRights(Cloud cloud, Node group, Map rights, String[] treeManagers) {
        clearUserRoles(cloud, treeManagers);
        
        List<Integer> rolesDone = new ArrayList<Integer>();
        
        for (int i = 0; i < treeManagers.length; i++) {
            String managerName = treeManagers[i];
            RelationList list = group.getRelations(ROLEREL, cloud.getNodeManager(managerName), DESTINATION);
            for (RelationIterator iter = list.relationIterator(); iter.hasNext();) {
               Relation rolerel = iter.nextRelation();
               Integer channelNumber = new Integer(rolerel.getDestination().getNumber());
               if (rights.containsKey(channelNumber)) {
                  rolesDone.add(channelNumber);
                  UserRole role = (UserRole) rights.get(channelNumber);
                  if (role == null) {
                     rolerel.delete();
                  } else {
                     rolerel.setIntValue(ROLE_FIELD, role.getRole().getId());
                     rolerel.commit();
                  }
               }
            }
        }

        Iterator keys = rights.keySet().iterator();
        while (keys.hasNext()) {
           Integer channelNumber = (Integer) keys.next();
           UserRole role = (UserRole) rights.get(channelNumber);
           if (!rolesDone.contains(channelNumber) && role != null) {
              Node channelNode = cloud.getNode(channelNumber.intValue());
              addRole(group, channelNode, role.getRole());
           }
        }
    }

    public static void clearUserRoles(Cloud cloud, String[] treeManagers) {
        cloud.setProperty(CMSC_USERROLES + treeManagers[0], null);
    }


    private static void clearUserRoles(Cloud cloud) {
        for (Iterator iter = cloud.getProperties().keySet().iterator(); iter.hasNext();) {
            String property = (String) iter.next();
            if (property.startsWith(CMSC_USERROLES)) {
                cloud.setProperty(property, null);
            }
        }
    }
    
    public static void addRole(Cloud cloud, String channelNumber, Node group, Role role,  String[] treeManagers) {
        Node channelNode = cloud.getNode(channelNumber);
        addRole(cloud, channelNode, group, role, treeManagers);
    }
    
    public static void addRole(Cloud cloud, Node channelNode, Node group, Role role,  String[] treeManagers) {
        addRole(group, channelNode, role);
        clearUserRoles(cloud, treeManagers);
    }
    
    public static void addRole(Node group, Node channelNode, Role role) {
        RelationManager rolemanager = 
            group.getCloud().getRelationManager(GROUP, channelNode.getNodeManager().getName(), ROLEREL);
          Relation relation = rolemanager.createRelation(group, channelNode);
          relation.setIntValue(ROLE_FIELD, role.getId());
          relation.commit();
    }

    public static List setGroupRights(Node channel, Role requiredRole, String managerName, String relationName) {
        String[] treeManagers = new String[] { managerName };
        return setGroupRights(channel, requiredRole, treeManagers, relationName);
    }
    
   public static List setGroupRights(Node channel, Role requiredRole, String[] treeManagers, String relationName) {
      Cloud cloud = channel.getCloud();
      
      // get all channel nodes in path. First is root nofe. last is the same aame as channel parameter
      List path = TreeUtil.getPathToRoot(channel, treeManagers, relationName);
      List<Node> users = new ArrayList<Node>();

      Iterator iter = path.iterator();
      while (iter.hasNext()) {
         Node channelNode = (Node) iter.next();

         RelationManager rolerelManager = cloud.getRelationManager(channelNode.getNodeManager().getName(), USER, ROLEREL);

         RelationList rolerels = rolerelManager.getRelations(channelNode);

         RelationIterator rels = rolerels.relationIterator();
         while (rels.hasNext()) {
            Relation relation = rels.nextRelation();

            Node destinationUser = relation.getDestination();

            // add users with at least required role
            if (relation.getIntValue(ROLE_FIELD) >= requiredRole.getId()) {
               addToList(users, destinationUser);
            }
            // Remove users with a lower role then required
            if (relation.getIntValue(ROLE_FIELD) < requiredRole.getId()) {
               removeFromList(users, destinationUser);
            }
         }
      }
      return users;
   }

   private static void addToList(List<Node> l, Node user) {
       for (int i = 0; i < l.size(); i++) {
          Node n = l.get(i);
          if (n.getNumber() == user.getNumber()) {
             return;
          }
       }
       l.add(user);
    }

    private static void removeFromList(List<Node> ret, Node destinationUser) {
       for (int i = 0; i < ret.size(); i++) {
          Node user = ret.get(i);
          if (user.getNumber() == destinationUser.getNumber()) {
             ret.remove(i);
          }
       }
    }
    
    public static boolean isWriter(UserRole role) {
        return role.getRole().getId() >= Role.WRITER.getId();
    }

    public static boolean isEditor(UserRole role) {
        return role.getRole().getId() >= Role.EDITOR.getId();
    }

    public static boolean isChiefEditor(UserRole role) {
        return role.getRole().getId() >= Role.CHIEFEDITOR.getId();
    }

    public static boolean isWebmaster(UserRole role) {
        return role.getRole().getId() >= Role.WEBMASTER.getId();
    }

    public static TreeMap<String, UserRole> getRoleMap(String[] treeManagers, String relationName, String[] fragmentFieldnames, Node group) {
        TreeMap<String, UserRole> channelsWithRole = getNewRolesMap();
        fillChannelsWithRole(group, channelsWithRole, treeManagers, relationName, fragmentFieldnames);
        return channelsWithRole;
    }

    public static TreeMap<String, UserRole> getRoleMap(String managerName, String relationName, String fragmentFieldname, Node group) {
        String[] treeManagers = new String[] { managerName };
        String[] fragmentFieldnames = new String[] { fragmentFieldname };
        return getRoleMap(treeManagers, relationName, fragmentFieldnames, group);
    }

    public static Node getAdministratorsGroup(Cloud cloud) {
        return SearchUtil.findNode(cloud, GROUP, "name", "Administrators");
    }

    public static NodeList getMembers(Node group) {
        return SearchUtil.findRelatedOrderedNodeList(group, USER, CONTAINS, "username");
    }

    public static NodeList getUsers(Cloud cloud) {
        NodeList users = SearchUtil.findOrderedNodeList(cloud, USER, "username");
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            Node user = (Node) iter.next();
            if ("anonymous".equalsIgnoreCase(user.getStringValue("username"))) {
                iter.remove();
                break;
            }
        }
        return users;
    }

    public static void setGroupMembers(Cloud cloud, Node groupNode, String[] newMembers) {
        List<String> currentMembers = new ArrayList<String>();

        NodeList membersList = SecurityUtil.getMembers(groupNode);
        for (Iterator iter = membersList.iterator(); iter.hasNext();) {
            Node member = (Node) iter.next();
            currentMembers.add(String.valueOf(member.getNumber()));
        }
        if (newMembers != null) {
            for (String newMember : newMembers) {
                if (currentMembers.contains(newMember)) {
                    currentMembers.remove(newMember);
                }
                else {
                    Node userNode = cloud.getNode(newMember);
                    addUser(cloud, groupNode, userNode);
                }
            }
        }

        RelationList relations = groupNode.getRelations(CONTAINS, USER);
        RelationIterator iter = relations.relationIterator();
        while (iter.hasNext()) {
            Relation r = iter.nextRelation();
            if (currentMembers.contains(String.valueOf(r.getDestination().getNumber()))) {
                r.delete();
            }
        }
        
        clearUserRoles(cloud);
    }

    private static void addUser(Cloud cloud, Node groupNode, Node userNode) {
        RelationManager GroupRelManager = cloud.getRelationManager(GROUP, USER, CONTAINS);
        if (GroupRelManager != null) {
            Relation GroupRel = groupNode.createRelation(userNode, GroupRelManager);
            GroupRel.commit();
        }
    }

    public static NodeList getGroups(Node user) {
        return user.getRelatedNodes(GROUP, CONTAINS, SOURCE);
    }
    
    public static Node getRank(Node userNode) {
        Node rankNode = null;
        NodeList userRanks = userNode.getRelatedNodes(RANK, RANKREL, "DESTINATION");
        if (userRanks.size() > 0) {
            rankNode = userRanks.getNode(0);
        }
        return rankNode;
    }

    public static void setRank(Cloud cloud, Node userNode, Node newRankNode) {
        RelationManager rankRelManager = cloud.getRelationManager(USER, RANK, RANKREL);
         if (rankRelManager != null) {
             userNode.deleteRelations(RANKREL);
             Relation rankRel = userNode.createRelation(newRankNode, rankRelManager);
             rankRel.commit();
         }
         else {
             log.warn("Relation manager for ranks not found: "+USER+", "+RANK+", " + RANKREL);
         }
    }

    public static Node getContext(Cloud cloud) {
        int newContext = ((Contexts) Contexts.getBuilder()).getDefaultContextNode().getNumber();
        Node newContextNode = cloud.getNode(newContext);
        return newContextNode;
    }

    public static String getFullname(Node user) {
        return NameUtil.getFullName(user.getStringValue("firstname"), 
                user.getStringValue("prefix"), 
                user.getStringValue("surname"));
    }
    
}
