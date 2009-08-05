<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="true"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<mm:content postprocessor="reducespace">
<mm:cloud>
  <mm:import externid="template" />

  <mm:present referid="template"><%-- template url explicity specified --%>
    <mm:include page="$template" />
  </mm:present>

  <mm:notpresent referid="template"><%-- no template specified, using 'page' en 'portal' arguments' --%>
    
    <%@ include file="includes/getids.jsp" %>
 
    <mm:node number="$page" notfound="skipbody">
      
      <mm:relatednodes type="templates">
        <mm:field name="url" id="templatefound">
          <mm:include page="$_" />
        </mm:field>
      </mm:relatednodes>
 
      <mm:notpresent referid="templatefound"><%-- still not found? then show error-page --%>
        <%@ include file="/includes/header.jsp"%> 
        <td colspan="2">
          <strong><font color="#CC0000">Error:</font></strong>
          <p>
            A template should be added to page '<mm:field name="title" />'.
          </p>
        </td>
        <%@ include file="/includes/footer.jsp"%>
      </mm:notpresent>
 
    </mm:node>
  </mm:notpresent>
</mm:cloud>
</mm:content>
