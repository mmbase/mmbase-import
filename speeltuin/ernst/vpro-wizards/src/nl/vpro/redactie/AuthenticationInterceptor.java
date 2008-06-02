package nl.vpro.redactie;

import javax.servlet.http.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Deze interceptor contoleert of het redactielid is ingelogd.
 * Indien dit het geval is gebeurt er niets. Is het redactielid
 * niet ingelogd dan krijgt hij een inlogpagina.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
	private static final Logger log = Logging.getLoggerInstance(AuthenticationInterceptor.class);
	private String loginPage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {

		boolean isAuthenticated = isAuthenticated(request);
		if(isAuthenticated) {
			return true;
		}

		if(log.isDebugEnabled()){
			log.debug("redirecting to "+request.getContextPath() +  loginPage);
		}
		response.sendRedirect(request.getContextPath() + loginPage);
		return false;
	}

	/**
	 * deze methode controleert of er een mmbase cloud sessie aanwezig is.
	 * Als dat het geval is dan is er iemand ingelogd.
	 */
	private boolean isAuthenticated(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if(session != null && session.getAttribute("cloud_mmbase")!=null) {
			if(log.isDebugEnabled()){
				log.debug("Authenticated");
			}
			return true;
		}
		if(log.isDebugEnabled()){
			log.debug("Not Authenticated");
		}

		return false;
	}

	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}
}