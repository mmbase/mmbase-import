package com.finalist.cmsc.linkvalidator;

import java.io.IOException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class LinkValidatorCronJob implements CronJob {

    private static final int TIMEOUT = 15000;
   private static Logger log = Logging.getLoggerInstance(LinkValidatorCronJob.class.getName());

    public void init(CronEntry cronEntry) {
        // empty
    }

    public void stop() {
        // empty
    }

    public void run() {
        checkExternalLinks();
    }

    private void checkExternalLinks() {
        log.info("LinkValidation thread started [freek]");

        Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();

        NodeList linkElements = cloud.getNodeManager("urls").getList("", "url", null);

        Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
        for (int i = 0; i < linkElements.size(); i++) {
            Node linkNode = linkElements.getNode(i);
            String url = linkNode.getStringValue("url");
            boolean valid;
            if (url.startsWith("#") || url.startsWith("mailto:")) {
                valid = true;
            } else {
                valid = isValid(url);
            }
            log.debug("Found url: [" + url + "] ("+valid+")");
            linkNode.setStringValue("valid", valid ? "1" : "0");
            linkNode.commit();
        }
    }

    private boolean isValid(String url) {
        // LCM-54: L1 uses urls with embedded spaces (works in IE but not in
        // Commons HTTP). Completely URLEncoding does not work either (slashes
        // should not be encoded for example), so just replaced all the spaces
        // with a plus sign.
       
       boolean valid = false;
       url = url.trim();
       String escapedUrl = url.replace(' ', '+');
       GetMethod httpget = null;
       
        try {
           HttpClient httpclient = new HttpClient();
           httpclient.setTimeout(TIMEOUT);
           httpclient.setConnectionTimeout(TIMEOUT);
           httpget = new GetMethod(escapedUrl);
            int responseCode = httpclient.executeMethod(httpget);
            valid = responseCode == HttpStatus.SC_OK;
            if (!valid) {
                log.debug("Got responsecode (" + responseCode + ")for url : " + escapedUrl);
            }
        } catch (IllegalArgumentException ex) {
            log.debug("Found an invalid url : " + escapedUrl, ex);
        } catch (IOException ex) {
            log.debug("Got a IOException for url : " + escapedUrl, ex);
        } catch (Throwable t) {
           log.debug("Got an unexpected Throwable for url : " + escapedUrl, t);
        } finally {
           if(httpget != null) {
              httpget.releaseConnection();
           }
        }
        return valid;
    }

}
