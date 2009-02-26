package org.mmbase.remotepublishing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class CloudInfo {

    private static final Logger log = Logging.getLoggerInstance(CloudInfo.class);

    public static final String CLOUD = "cloud";
    
    private static final int DEFAULT_CLOUDINFO_NUMBER = Integer.MIN_VALUE;
    // this map used to cache cloudInfo instance. Cloud (in local) --> CloudInfo
    private static final Map<Integer, CloudInfo> instanceMap = new HashMap<Integer, CloudInfo>();
    
    private static final Map<Integer, Cloud> cloudMap = new HashMap<Integer, Cloud>();
    
    public static CloudInfo getDefaultCloudInfo() {
        if (instanceMap.containsKey(DEFAULT_CLOUDINFO_NUMBER)) {
            return instanceMap.get(DEFAULT_CLOUDINFO_NUMBER);
        }
        Cloud localCloud = CloudManager.getAdminCloud();
        CloudInfo localCloudInfo = getCloudInfo(localCloud);
        instanceMap.put(DEFAULT_CLOUDINFO_NUMBER, localCloudInfo);
        cloudMap.put(localCloudInfo.getNumber(), localCloud);
        return localCloudInfo;
    }
    
    /**
     * 
     * @param cloud
     * @return
     * @deprecated because this method use getCloudNode(cloud, cloud), it's expensive.
     */
    public static CloudInfo getCloudInfo(Cloud cloud) {
        Node cloudNode = CloudManager.getCloudNode(cloud, cloud);
        CloudInfo cloudInfo = getCloudInfo(cloudNode);
        cloudMap.put(cloudNode.getNumber(), cloud);
        return cloudInfo;
    }
    
    public static CloudInfo getCloudInfo(int cloudNumber) {
        if (instanceMap.containsKey(cloudNumber)) {
            return instanceMap.get(cloudNumber);
        }
        CloudInfo localCloudInfo = getDefaultCloudInfo();
        Node cloudNode = localCloudInfo.getCloud().getNode(cloudNumber);
        return getCloudInfo(cloudNode);
    }
    
    public static CloudInfo getCloudInfo(CloudInfo remoteCloudInfo, int remoteCloudNumber) {
        CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
        if (remoteCloudInfo==null || localCloudInfo.equals(remoteCloudInfo)) {
            return getCloudInfo(remoteCloudNumber);
        }
        Node remoteCloudNode = CloudManager.getCloudNodeByNumber(remoteCloudInfo.getCloud(),remoteCloudNumber);
        
        String cloudName = remoteCloudNode.getStringValue("name");
        Node cloudNode = CloudManager.getCloudNodeByName(localCloudInfo.getCloud(), cloudName);
          
        CloudInfo cloudInfo = getCloudInfo(cloudNode);
        return cloudInfo; 
    }
    
    private static CloudInfo getCloudInfo(Node localCloudNode) {
       if (localCloudNode == null) {
          throw new BridgeException("Local cloud node is null");
       }
        if (instanceMap.containsKey(localCloudNode.getNumber())){
            return instanceMap.get(localCloudNode.getNumber());
        }
        if (CLOUD.equals(localCloudNode.getNodeManager().getName()) == false) {
            throw new BridgeException("the node(" + localCloudNode.getNumber()
                    + ") is not a cloud node ");
        }
        CloudInfo cloudInfo = new CloudInfo();
        cloudInfo.init(localCloudNode);
        instanceMap.put(cloudInfo.getNumber(), cloudInfo);
        return cloudInfo;
    }
    
    /**
     * 
     * @param remoteCloudName
     * @return
     */
    public static CloudInfo getCloudInfoByName(String remoteCloudName) { 
        // FIXME: Does every cloud node have same name in different clouds?
        Node cloudNode = CloudManager.getCloudNodeByName(getDefaultCloudInfo().getCloud(), remoteCloudName);
        return getCloudInfo(cloudNode);
    }
    
    private String name;
    private int number;
    private String rmiurl;
    
    private final Map<Integer, Integer> remoteNumberMap = new HashMap<Integer, Integer>();

    private CloudInfo() {
        // prevent instantiation
    }
    
    private void init(Node cloudNode) {
        this.name = cloudNode.getStringValue("name");
        this.number = cloudNode.getNumber();
        this.rmiurl = cloudNode.getStringValue("rmiurl");
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getRmiurl() {
        return rmiurl;
    }

    public Cloud getCloud() {
        return CloudInfo.getCloud(this.number);
    }
    
    public Integer getNumberInRemoteCloud(int remoteCloudNumber) {
        if(remoteNumberMap.containsKey(remoteCloudNumber)==false){
            //get the name of the cloud
            CloudInfo remoteCloudInfo = getCloudInfo(remoteCloudNumber);
            Node nodeInRemoteCloud = CloudManager.getCloudNodeByName(remoteCloudInfo.getCloud(), this.name);
            int numberInRemoteCloud = nodeInRemoteCloud.getNumber();
            remoteNumberMap.put(remoteCloudNumber, numberInRemoteCloud);
        }
        return remoteNumberMap.get(remoteCloudNumber);
    }

    public Integer getNumberInRemoteCloud(CloudInfo remoteCloudInfo) {
        return getNumberInRemoteCloud(remoteCloudInfo.getNumber());
    }
    
    /*
    public Map<Integer,CloudInfo> getAllRemoteCloudInfo() {
        Map<Integer, CloudInfo> cloudInfoMap = new HashMap<Integer,CloudInfo>();
        NodeIterator nodeIterator = this.getCloud().getNodeManager(CLOUD).getList(
                 "number<>" + this.number, null, null).nodeIterator();
        while (nodeIterator.hasNext()) {
            Node cloudNode = nodeIterator.nextNode();
            CloudInfo cloudInfo = CloudInfo.getCloudInfo(cloudNode);
            cloudInfoMap.put(cloudNode.getNumber(),cloudInfo);
        }
        return cloudInfoMap;
        
    }
    */
 
    /**
     * 
     * @param cloudNumber
     * @return
     */
    public static Cloud getCloud(int cloudNumber) {
        if (cloudMap.containsKey(cloudNumber)==false) {
            cloudMap.put(cloudNumber, CloudManager.getCloud(CloudManager.getAdminCloud(),cloudNumber));
        }
        Cloud cloud = cloudMap.get(cloudNumber);
        return cloud;
    }

    public static int getCloudNumberInRemoteCloud(CloudInfo nameServerCloudInfo, CloudInfo cloudInfo) {
        CloudInfo localCloudInfo = getDefaultCloudInfo();
        if (nameServerCloudInfo.equals(localCloudInfo)) {
            return cloudInfo.getNumber();
        }
        if (cloudInfo.remoteNumberMap.containsKey(nameServerCloudInfo.getNumber())==false) {
            Node cloudNode = CloudManager.getCloudNode(nameServerCloudInfo.getCloud(), cloudInfo.getCloud());
            cloudInfo.remoteNumberMap.put(nameServerCloudInfo.getNumber(), cloudNode.getNumber());
        }
        return cloudInfo.remoteNumberMap.get(nameServerCloudInfo.getNumber());
    }

    public static void setCloudInvalid(String url) {
        Iterator<CloudInfo> iterator = instanceMap.values().iterator();
        CloudInfo localCloudInfo = getDefaultCloudInfo();
        while (iterator.hasNext()) {
            CloudInfo cloudInfo = iterator.next();
            if (url==null || "".equals(url.trim())) {
                //url==null means set all remote cloud invalid.
                if (cloudInfo.getNumber()==localCloudInfo.getNumber()) {
                    //if it is local cloud, ignore
                    continue;
                }
                log.debug("Found a cached remote cloud (name="+cloudInfo.getName()+", and rmiurl="+url+"), remove it from cloud instance cache");
                cloudMap.remove(cloudInfo.getNumber());
            } else if (url.equals(cloudInfo.getRmiurl())) {
                log.debug("Found a cached cloud (name="+cloudInfo.getName()+", and rmiurl="+url+"), remove it from cloud instance cache");
                cloudMap.remove(cloudInfo.getName());
                break;
            }
        }
    }

    public static void setRemoteCloudsInvalid() {
        //set all remote cloud to invalid
        setCloudInvalid(null);
    }

}
