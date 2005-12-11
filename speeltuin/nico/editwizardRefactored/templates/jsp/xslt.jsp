<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@page import="org.mmbase.applications.editwizard.util.XmlUtil"
%><%@page import="org.mmbase.applications.editwizard.Controller"
%><%@page import="org.w3c.dom.Document,java.util.Map,java.net.URL"
%><%@page import="org.mmbase.util.xml.URIResolver"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%
// get xslt informations
Document doc = (Document)request.getAttribute(Controller.ATTRKEY_XSLT_DOCUMENT);
Map params = (Map)request.getAttribute(Controller.ATTRKEY_XSLT_PARAMS);
URL xslfilepath = (URL)request.getAttribute(Controller.ATTRKEY_XSLT_FILEPATH);
URIResolver uriResolver = (URIResolver)request.getAttribute(Controller.ATTRKEY_XSLT_URIRESOLVER);
// execute a xlt transform
XmlUtil.transformNode(doc, xslfilepath, uriResolver, out, params);
%>