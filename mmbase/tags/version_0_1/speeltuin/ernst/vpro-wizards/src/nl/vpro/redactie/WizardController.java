package nl.vpro.redactie;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.vpro.redactie.cache.CacheFlushHint;
import nl.vpro.redactie.cache.CacheHandlerInterceptor;

import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Deze controller verwerkt acties van de redactieleden. Spring binding wordt gebruikt om de acties te verzamelen en een wizardservice wordt
 * gebruikt om de acties daadwerkelijk te verwerken. Vervolgens is logica geimplementeerd om naar de juiste pagina te springen.
 * 
 * @author Rob Vermeulen (VPRO)
 */
public class WizardController implements Controller {
    private static Logger log = Logging.getLoggerInstance(WizardController.class);

    private WizardService wizardService;

    private String dummyView;

    // private OscacheFlush oscacheFlush = new OscacheFlush();

    public void setDummyView(String dummyView) {
        this.dummyView = dummyView;
    }

    @SuppressWarnings("unchecked")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Container met alle acties die verwerkt moeten worden.
        request.setCharacterEncoding("UTF-8");
        Command command = new Command();

        if (log.isDebugEnabled()) {
            log.debug("page(" + request.getRequestURI() + ")");
            Enumeration<String> e;

            for (e = request.getParameterNames(); e.hasMoreElements();) {
                String name = e.nextElement();
                Object value = request.getSession().getAttribute(name);
                log.info(" - p: " + name + "(" + value + ")");
            }

            for (e = request.getAttributeNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                Object value = request.getSession().getAttribute(name);
                log.info(" - a: " + name + "(" + value + ")");
            }

            for (e = request.getSession().getAttributeNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                Object value = request.getSession().getAttribute(name);
                log.info(" - s.a: " + name + "(" + value + ")");
            }

            log.info("attribute.flush(" + request.getAttribute("flush") + "), attribute.session.flush("
                    + request.getSession().getAttribute("flush") + "), param.flush(" + request.getParameter("flush") + ")");

            log.info("attribute.flushname(" + request.getAttribute("flush") + "), attribute.session.flushname("
                    + request.getSession().getAttribute("flushname") + "), param.flush(" + request.getParameter("flushname") + ")");
        }

        // Zet alle post informatie om naar acties in de actie container
        // (command).
        ServletRequestDataBinder binder = new ServletRequestDataBinder(command, "formGraphBean");
        binder.bind(request);

        // Verwerk alle acties.
        Cloud cloud = (Cloud) request.getSession().getAttribute("cloud_mmbase");
        ResultContainer resultContainer = wizardService.processActions(command, cloud, request, response);

        Map<String, Object> model = new HashMap<String, Object>();

        // Als er errors zijn laat die dan zien.
        // en geen cache flush hints!
        if (resultContainer.containsErrors()) {
            if (log.isDebugEnabled()) {
                log.debug("Errors found, redirecting to error page.");
            }
            model.put("errors", resultContainer.getErrors());
            return new ModelAndView("system/error.jsp", model);
        }

        // this can be null if the wizard was called through an httpxml request
        String urlToShow = generateURL(resultContainer, request);
        log.debug("adding cache flush hint type 'request'");

        // add the 'request' cache flush hint
        resultContainer.addCacheFlushHint(new CacheFlushHint(CacheFlushHint.TYPE_REQUEST));

        // set all the cache flush hints in the request.
        request.setAttribute(CacheHandlerInterceptor.PARAM_NAME, resultContainer.getCacheFlushHints());
        return new ModelAndView(new RedirectView(urlToShow), model);
    }

    public WizardService getWizardService() {
        return wizardService;
    }

    public void setWizardService(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    /**
     * bepaal de nieuwe view voor het redactielid.
     */
    private String generateURL(ResultContainer result, HttpServletRequest request) {

        // Heeft de redactie een nieuw object aangemaakt?
        String newObject = result.getNewObject();
        if (newObject != null) {
            request.setAttribute("newObject", newObject);
            if (log.isDebugEnabled()) {
                log.debug("object number " + newObject);
            }
        }

        // this can be null if the wizard was called through an httpxml request
        String referer = request.getHeader("Referer");

        String newPage = null;

        if (newObject != null) {
            if (log.isDebugEnabled()) {
                log.debug("new object created.");
            }

            if (referer.indexOf('?') == -1) {
                newPage = referer + "?nodenr=" + result.getNewObject();
            } else {
                newPage = referer + "&nodenr=" + result.getNewObject();
            }
        } else {
            newPage = referer;
        }

        if (newPage != null && newPage.contains("editnodenr")) {
            if (log.isDebugEnabled()) {
                log.debug("cutting editnodenr");
            }

            newPage = newPage.substring(0, referer.indexOf("editnodenr"));
        }

        if (newPage == null) {
            newPage = dummyView;
        }
        return newPage;
    }

}
