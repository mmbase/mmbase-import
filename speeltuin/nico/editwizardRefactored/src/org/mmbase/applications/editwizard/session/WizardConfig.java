package org.mmbase.applications.editwizard.session;

import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.ObjectData;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.xml.URIResolver;

public class WizardConfig extends AbstractConfig {
    // the result objectnumber (the number of the object after a commit)
    // this value is only assigned after COMMIT is called - otherwise it is null
    public String objectNumber;
    public String parentFid;
    public String parentDid;
    public String origin;
    
    public ObjectData wizardData = null;
    
    // stores the current formid
    public String currentFormId;

    // filename of the stylesheet which should be used to make the html form.
    private URL wizardStylesheetFile;
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
    public void configure(HttpServletRequest request, Cloud cloud, URIResolver uriResolver) throws WizardException {
        super.configure(request, cloud, uriResolver);

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

}

    