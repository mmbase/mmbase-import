package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/** Class to wrap a ServletRequest so it can be used as a RenderRequest */
public class RenderRequestWrapper extends PortletRequestWrapper implements RenderRequest {

	public RenderRequestWrapper(ServletRequest request){
		super(request);
	}
}