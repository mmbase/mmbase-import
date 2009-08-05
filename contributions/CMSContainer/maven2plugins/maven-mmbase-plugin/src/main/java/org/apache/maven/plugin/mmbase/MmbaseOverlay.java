package org.apache.maven.plugin.mmbase;

import java.util.Properties;

import org.apache.maven.artifact.Artifact;

public class MmbaseOverlay {
    private String groupId;
    
    private String artifactId;
    
    private String version;
    
    /**
     * default type is mmbase-module
     */
    private String type = "mmbase-module";
    
    private Properties myProperties;
    
    private Artifact artifact;
    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }
    public MmbaseOverlay(){
        this.myProperties = new Properties();
    }
    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the artifact
     */
    public Artifact getArtifact() {
        return artifact;
    }

    /**
     * @param artifact the artifact to set
     */
    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the myProperties
     */
    public Properties getMyProperties() {
        return myProperties;
    }

    /**
     * @param myProperties the myProperties to set
     */
    public void setMyProperties(Properties myProperties) {
        this.myProperties = myProperties;
    }
}
