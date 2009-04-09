package com.finalist.portlets.newsletter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.RegisterPortlet;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

/**
 * This porlet is used to subscribe to newsletters with automatic registration in the community module.
 * <code>validateInputFields</code> can be overriden to limit the number of mandatory fields.
 * <code>getTemplate</code> can be overriden to return a different view.
 * 
 * @author rob
 *
 */
public class NewsletterRegistrationPortlet extends RegisterPortlet {
    private static Logger log = Logging.getLoggerInstance(NewsletterRegistrationPortlet.class.getName());
    private static final String ALLOWED_NEWSLETTERS = "allowednewsletters";

    @Override
    protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        // first add some specific newsletter settings
        PortletPreferences preferences = request.getPreferences();
        String[] newsletters = preferences.getValues(ALLOWED_NEWSLETTERS, null);
        if (newsletters != null) {
            request.setAttribute(ALLOWED_NEWSLETTERS, newsletters);
        }
        // process request as if this was a normal community portlet
        super.doEditDefaults(request, response);
    }

    @Override
    public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // first add some specific newsletter settings
        String[] availableNewsletters = request.getParameterValues(ALLOWED_NEWSLETTERS);
        PortletPreferences preferences = request.getPreferences();
        String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
        if (portletId != null) {
            setPortletNodeParameter(portletId, ALLOWED_NEWSLETTERS, availableNewsletters);
        }
        // process request as if this was a normal community portlet
        super.processEditDefaults(request, response);
    }

    @Override
    public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // first process as if this was a normal community registration
        super.processView(request, response);
        // if the community registration is successful then subscribe to all the
        // available newsletters
        if (request.getPortletSession().getAttribute(ERRORMESSAGES) == null) {
            PortletPreferences preferences = request.getPreferences();
            NewsletterSubscriptionServices services = (NewsletterSubscriptionServices) ApplicationContextFactory
                    .getBean("subscriptionServices");
            Long subscriberId = (Long) request.getAttribute(AUTHENTICATION_ID_KEY);
            if (subscriberId != null) {
                // subscribe to new newsletters, ignore existing ones, don't remove any other
                String[] newsletters = preferences.getValues(ALLOWED_NEWSLETTERS, null);
                if (newsletters != null && newsletters.length > 0) {
                    List<String> newslettersList = Arrays.asList(newsletters);
                    List<Subscription> subscriptions = services.getSubscriptions(newsletters, subscriberId.intValue());
                    List<String> existingNewsletters = new ArrayList<String>();
                    for (Subscription sub : subscriptions) {
                        String newsletterId = String.valueOf(sub.getNewsletter().getId());
                        existingNewsletters.add(newsletterId);
                    }
                    for (String newsletterId : newslettersList) {
                        if (!existingNewsletters.contains(newsletterId)) {
                            services.addNewRecord(subscriberId.intValue(), Integer.valueOf(newsletterId));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String getTemplate(String key) {
        if ("register_success".equals(key)) {
            return "newsletter/register_success.jsp";
        }
        if ("register".equals(key)) {
            // provide basic register page with only email address required
            return "newsletter/register_basic.jsp";
        }
        return super.getTemplate(key);
    }

    @Override
    protected boolean isExistingUserAllowed() {
        return true;
    }
}
