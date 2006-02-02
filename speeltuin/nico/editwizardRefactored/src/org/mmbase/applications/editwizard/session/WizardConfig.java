/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.session;

import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.ObjectData;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.util.xml.URIResolver;

/**
 * @javadoc
 * @author Finalist
 * @version $Id: WizardConfig.java,v 1.3 2006-02-02 12:18:33 pierre Exp $
 */
public class WizardConfig extends AbstractConfig {
    // the result objectnumber (the number of the object after a commit)
    // this value is only assigned after COMMIT is called - otherwise it is null
    public String objectNumber;
    public String parentFid;
    public String parentDid;
    public String origin;

    protected WizardWorkSpace workSpace = null;

    // stores the current formid
    private String currentFormId;

    // filename of the stylesheet which should be used to make the html form.
    private URL wizardStylesheetFile;
    // language (used where ???)
    private String language = "en";

    /**
     * Configure a wizard. The configuration object passed is updated with information retrieved
     * from the request object with which the configurator was created. The following parameters are accepted:
     *
     * <ul>
     *   <li>popupid</li>
     *   <li>objectnumber</li>
     * </ul>
     *
     * @since MMBase-1.6.4
     * @param controller the configurator containing request information
     * @throws WizardException if expected parameters were not given
     */
    public void configure(HttpServletRequest request, URIResolver uriResolver) throws WizardException {
        super.configure(request, uriResolver);

        parentFid = HttpUtil.getParam(request, "fid","");
        parentDid = HttpUtil.getParam(request, "did","");
        origin = HttpUtil.getParam(request, "origin","");
        objectNumber = HttpUtil.getParam(request, "objectnumber");
        URL wizardSchemaFile;

        try {
            wizardSchemaFile     = uriResolver.resolveToURL(getWizardName() + ".xml", null);
        } catch (Exception e) {
            throw new WizardException(e);
        }
        if (wizardSchemaFile == null) {
            throw new WizardException("Could not resolve wizard " + getWizardName() + ".xml  with "  + uriResolver);
        }
        try {
            wizardStylesheetFile = uriResolver.resolveToURL("xsl/wizard.xsl", null);
        } catch (Exception e) {
            throw new WizardException(e);
        }

        if (wizardStylesheetFile == null) {
            throw new WizardException("Could not resolve XSL " + wizardStylesheetFile + "  with "  + uriResolver);
        }
    }

    public WizardWorkSpace getWorkSpace() {
        if (workSpace == null) {
            workSpace = new WizardWorkSpace(this);
        }
        return workSpace;
    }

    /**
     * Returns available attributes in a map, so they can be passed to the list stylesheet
     */
    public Map getAttributes() {
        Map attributeMap = super.getAttributes();
        if (objectNumber!=null) {
            attributeMap.put("objectnumber", objectNumber);
        }

        return attributeMap;
    }

    /**
     * @return Returns the currentFormId.
     */
    public String getCurrentFormId(){
        if (currentFormId==null) {
            currentFormId = (String)getWizardSchema().getSteps().get(0);
        }
        return currentFormId;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * jump to the form
     */
    public boolean doGotoForm(String formId) {
        currentFormId = formId;
        return true;
    }
}

