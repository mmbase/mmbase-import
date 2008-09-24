/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects;

/**
 * @author     Daniel Ockeloen
 *
 */
public class IncludedPackage {

    String name;
    String type;
    String maintainer;
    String version;
    boolean included;


    /**
     *  Gets the name attribute of the IncludedPackage object
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the type attribute of the IncludedPackage object
     *
     * @return    The type value
     */
    public String getType() {
        return type;
    }


    /**
     *  Gets the maintainer attribute of the IncludedPackage object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        return maintainer;
    }


    /**
     *  Gets the version attribute of the IncludedPackage object
     *
     * @return    The version value
     */
    public String getVersion() {
        return version;
    }


    /**
     *  Gets the included attribute of the IncludedPackage object
     *
     * @return    The included value
     */
    public boolean getIncluded() {
        return included;
    }


    /**
     *  Sets the included attribute of the IncludedPackage object
     *
     * @param  included  The new included value
     */
    public void setIncluded(boolean included) {
        this.included = included;
    }


    /**
     *  Sets the name attribute of the IncludedPackage object
     *
     * @param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the version attribute of the IncludedPackage object
     *
     * @param  version  The new version value
     */
    public void setVersion(String version) {
        this.version = version;
    }


    /**
     *  Sets the type attribute of the IncludedPackage object
     *
     * @param  type  The new type value
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     *  Gets the id attribute of the IncludedPackage object
     *
     * @return    The id value
     */
    public String getId() {
        String id = name + "@" + maintainer + "_" + type;
        id = id.replace(' ', '_');
        id = id.replace('/', '_');
        return id;
    }


    /**
     *  Sets the maintainer attribute of the IncludedPackage object
     *
     * @param  maintainer  The new maintainer value
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

}

