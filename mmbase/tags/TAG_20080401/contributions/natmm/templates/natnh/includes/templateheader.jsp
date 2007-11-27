<base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
<%@page import="java.text.*,java.io.*,org.mmbase.bridge.*" %>
<mm:import jspvar="ID" externid="id">-1</mm:import>
<mm:import jspvar="rubriekId" externid="r">-1</mm:import>
<mm:import jspvar="paginaID" externid="p">-1</mm:import>
<mm:import jspvar="articleId" externid="a">-1</mm:import>
<mm:import jspvar="imageId" externid="i">-1</mm:import>
<%

PaginaHelper ph = new PaginaHelper(cloud);
String path = ph.getTemplate(request);

// Id finding for the following request parameters (only the object types in NatMMConfig can be used)

HashMap ids = new HashMap();
ids.put("object", ID);
ids.put("rubriek", rubriekId);
ids.put("pagina", paginaID);
ids.put("artikel", articleId);
ids.put("images", imageId);

ids = ph.findIDs(ids, path, "natuurherstel_home");

ID = (String) ids.get("object");
rubriekId = (String) ids.get("rubriek");
paginaID = (String) ids.get("pagina");
articleId = (String) ids.get("artikel");
imageId = (String) ids.get("images");

String rootId = ph.getSubsiteRubriek(cloud, paginaID);
String offsetId = request.getParameter("offset"); if(offsetId==null){ offsetId=""; }

String requestURL = javax.servlet.http.HttpUtils.getRequestURL(request).toString();
requestURL = requestURL.substring(0,requestURL.lastIndexOf("/")) + "/"; 

%>
