<%@ page errorPage="exception.jsp"%>
<%@ include file="settings.jsp"%>
<mm:locale language="<%=ewconfig.language%>">
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<%@page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,javax.servlet.jsp.JspException"%>

<mm:import externid="newfromlist" jspvar="sNewfromlist" vartype="String" required="true" />
<mm:import externid="selected" jspvar="sSelected" vartype="String" required="true" />

<%
  if (sSelected != null) {
    if (sSelected != "") {
      String tmp[] = sNewfromlist.split(",",0);
%>   
     <mm:node number="<%=tmp[0]+""%>" notfound="skip" id="sourcenode">
     </mm:node>
<%      
      String splitSelected[] = sSelected.split(",",0);
      for(int i=0; i<splitSelected.length; i++) {
%>        
        <mm:remove referid="relatednode"/>
        <mm:node number="<%=splitSelected[i]%>" notfound="skip" id="relatednode">
          <mm:createrelation role="<%=tmp[1]%>" source="sourcenode" destination="relatednode" />
        </mm:node>
<%    
      }
    }
  }
%>

</mm:log></mm:cloud></mm:locale>
