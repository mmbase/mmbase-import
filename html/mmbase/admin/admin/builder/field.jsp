<%@ page import="org.mmbase.module.core.MMBase,org.mmbase.bridge.*,java.util.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<div
  class="component ${requestScope.className}"
  id="${requestScope.componentId}">
  <mm:import externid="builder" jspvar="builder" />
  <mm:import externid="field"   jspvar="field" />

  <h3>View builder <%=builder%>, field <%=field%></h3>
<% 
   String cmd = request.getParameter("cmd");
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
%>
<table summary="builder field properties" cellspacing="0" cellpadding="3">
  <caption>
    Description of the field <strong>${field}</strong> of <strong>${builder}</strong>
  </caption>

  <tr>
    <th>Property</th>
    <th>Value</th>
    <th class="center">Explain</th>
  </tr>
  <tr>
    <td>Name</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbname",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_name" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Type</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbmmbasetype",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_type" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Data Type</td>
    <td><%=cloud.getNodeManager(builder).getField(field).getDataType().toXml()%></td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#data_type" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>State</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbstate",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_state" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Required</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbnotnull",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_notnull" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Unique/Key</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbkey",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_key" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Size</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbsize",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_size" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  
  <tr><td colspan="3">&nbsp;</td></tr>
  
  <tr>
    <th>Editor property</th>
    <th>Value</th>
    <th class="center">Explain</th>
  </tr>
  
  <tr>
    <td>Input</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorinput",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_input" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>List</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorlist",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_list" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  <tr>
    <td>Search</td>
    <td><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorsearch",request,response)%>&nbsp;</td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_search" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
  
  <tr><td colspan="3">&nbsp;</td></tr>
  
  <tr>
    <th>GUI Property</th>
    <th>Value</th>
    <th class="center">Explain</th>
  </tr>
  
<%
   java.util.Map params = new java.util.Hashtable();
   params.put("CLOUD", cloud);
    NodeList names=mmAdmin.getList("ISOGUINAMES-"+builder+"-"+field, params,request,response);
    for (int i=0; i<names.size(); i++) {
        Node name=names.getNode(i);
%>

  <tr>
    <td>Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%></td>
    <td><%=name.getStringValue("item2")%></td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_guiname" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
<% } %>

  <tr><td colspan="3">&nbsp;</td></tr>
  
  <tr>
    <th>GUI Descriptions</th>
    <th>Value</th>
    <th class="center">Explain</th>
  </tr>

<%
    params.clear();
    params.put("CLOUD", cloud);
    names=mmAdmin.getList("ISODESCRIPTIONS-"+builder+"-"+field, params,request,response);
    for (int i=0; i<names.size(); i++) {
        Node name=names.getNode(i);
%>
  <tr>
    <td>Description&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%></td>
    <td><%=name.getStringValue("item2")%></td>
    <td class="center"><a href="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_description" target="_blank"><img src="<mm:url page="/mmbase/style/images/help.png" />" alt="explain" /></a></td>
  </tr>
<% } %>

</table>

<p>
  <mm:link page="builders-actions" referids="builder">
    <a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
  </mm:link>
  Return to Builder Administration
</p>

</div>

</mm:cloud>
