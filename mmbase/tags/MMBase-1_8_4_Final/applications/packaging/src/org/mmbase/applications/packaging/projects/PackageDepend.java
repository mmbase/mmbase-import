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
public class PackageDepend {

    String name;
    String type;
    String maintainer;
    String version;
    String versionmode;


    /**
     *  Gets the name attribute of the PackageDepend object
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the type attribute of the PackageDepend object
     *
     * @return    The type value
     */
    public String getType() {
        return type;
    }


    /**
     *  Gets the maintainer attribute of the PackageDepend object
     *
     * @return    The maintainer value
     */
    public String getMaintainer() {
        return maintainer;
    }


    /**
     *  Gets the version attribute of the PackageDepend object
     *
     * @return    The version value
     */
    public String getVersion() {
        return version;
    }


    /**
     *  Gets the versionMode attribute of the PackageDepend object
     *
     * @return    The versionMode value
     */
    public String getVersionMode() {
        if (versionmode == null) {
            return "atleast";
        }
        return versionmode;
    }


    /**
     *  Sets the versionMode attribute of the PackageDepend object
     *
     * @param  versionmode  The new versionMode value
     */
    public void setVersionMode(String versionmode) {
        this.versionmode = versionmode;
    }


    /**
     *  Sets the name attribute of the PackageDepend object
     *
     * @param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the version attribute of the PackageDepend object
     *
     * @param  version  The new version value
     */
    public void setVersion(String version) {
        this.version = version;
    }


    /**
     *  Sets the type attribute of the PackageDepend object
     *
     * @param  type  The new type value
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     *  Gets the id attribute of the PackageDepend object
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
     *  Sets the maintainer attribute of the PackageDepend object
     *
     * @param  maintainer  The new maintainer value
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

}

