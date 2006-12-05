<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*,org.mmbase.bridge.*,nl.leocms.util.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud" method="http" rank="basic user">
<mm:import externid="ID" jspvar="id">-1</mm:import>
<html>
<head>
   <title>Used in items</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
<div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>
<% ContentHelper contentHelper = new ContentHelper(cloud);
	PaginaHelper ph = new PaginaHelper(cloud);
	String usedIn = "";
	String sTitle = cloud.getNode(id).getNodeManager().getGUIName() + " " + cloud.getNode(id).getStringValue("titel") + " wordt ";
	NodeList nl = contentHelper.usedInItems(id);
	boolean aIsOpen = false;
   if (nl!=null){
		sTitle += "gebruikt in:";
      for (int i = 0; i < nl.size(); i++){
			String sBuilderName = nl.getNode(i).getNodeManager().getName();
			if (sBuilderName.equals("pagina")){
				usedIn += "<a href=\"" + ph.createPaginaUrl(nl.getNode(i).getStringValue("number"),request.getContextPath()) + "\">";
				aIsOpen = true;
			} else {//checking is node related to pagina
				Node node =  nl.getNode(i);
				NodeList rn = node.getRelatedNodes();
				for (Iterator it1 = rn.iterator(); it1.hasNext();){
					String sNumber = ((Node)it1.next()).getStringValue("number");
					if(cloud.getNode(sNumber).getNodeManager().getName().equals("pagina")){
						usedIn += "<a href=\"" + ph.createItemUrl(nl.getNode(i).getStringValue("number"), sNumber, null, request.getContextPath()) + "\">";
						aIsOpen = true;
						break;
					}
				}
			}
         usedIn +=  sBuilderName + " " + nl.getNode(i).getStringValue("titel");
			if (aIsOpen){
				usedIn += "</a>";
			}
			usedIn +=  "<br/>";
      }
   } else {
		sTitle += "nergens gebruikt";
	}
 %>
 <h4><%= sTitle %></h4>
 <br/>
 <%= usedIn %>
</body>
</html>
</mm:cloud>
