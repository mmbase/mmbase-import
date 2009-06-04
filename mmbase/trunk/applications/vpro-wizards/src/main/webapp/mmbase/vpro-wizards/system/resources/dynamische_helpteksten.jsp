<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:directive.page import="org.mmbase.bridge.Cloud"/>
<jsp:directive.page import="org.mmbase.bridge.NodeManager"/>
<jsp:directive.page import="org.mmbase.bridge.NodeList"/>
<jsp:directive.page import="org.mmbase.bridge.Node"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!

	public String getEditorHref(Cloud cloud, String basename, String key) {
    	return "<a href=\""+getEditorEntry(cloud, basename, key)+"\">"+key+"</a>";
	}

	public String getEditorEntry(Cloud cloud, String basename, String key) {
    	return "http://edit.vpro.nl/preditor/nodeEdit.jsp?nodeNumber=" +
    	        getBundleValueNumberFromBundleKeyName(cloud, basename, key);
	}

	public int getBundleValueNumberFromBundleKeyName(Cloud cloud, String basename, String key) {
    	int result = -1;
		NodeManager bundlekeys = cloud.getNodeManager("bundlekeys");
		NodeList l = bundlekeys.getList("key='"+basename+key+"'", "number", "down");
		if(l != null && l.size()>0) {
		    Node n = l.getNode(0);
		    NodeList l2 = n.getRelatedNodes("bundlevalues");
			if(l2 != null && l2.size()>0) {
			    Node n2 = l2.getNode(0);
				result = n2.getNumber();
			}
		} else {
		    System.out.println("key("+basename+key+") not found!");
		}
		return result;
	}
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Dynamische teksten aanpassen</title>
		<style type="text/css">
			body {
    			font-family: Verdana,Arial,sans-serif,monospace;
			}

			table.Design4 {
			    border-spacing: 0px;
			    border-collapse: collapse;
			}
			table.Design4 th {
			    text-align: left;
			    font-weight: normal;
			    padding: 0.1em 0.5em;
			    border-bottom: 2px solid #FFFFFF;
 			   background: #DBE2F1;
			}
			table.Design4 td {
 			   text-align: left;
			    border-bottom: 2px solid #FFFFFF;
			    padding: 0.1em 0.5em;
			    background: #DBE2F1;
			}

			table.Design4 th {
			    background: #687EAB;
 			    color: #FFFFFF;
			    text-align: left;
			    font-weight: bold;
			}

			table.Design4 th.Corner {
 			   text-align: left;
			}
		</style>
	</head>
	<body>
	<mm:cloud id="cloud" jspvar="cloud">

	<table class="Design4" >
		<fmt:bundle basename="nl.vpro.dvt.communities.util.resource.CommunityResourceBundle" prefix="jsp.user.">

		<th align="left" colspan="2">Profiel pagina</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "profiel.aanmelding") %></td><td valign="top"><fmt:message key="profiel.aanmelding"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "profiel.uitleg") %></td><td valign="top"><fmt:message key="profiel.uitleg"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "profiel.geenprofiel") %></td><td valign="top"><fmt:message key="profiel.geenprofiel"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "profiel.edit") %></td><td><fmt:message key="profiel.edit"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "profiel.nieuwplaatje") %></td><td><fmt:message key="profiel.nieuwplaatje"/></td></tr>

		<th align="left" colspan="2">Vragen</th>
			<mm:list nodes="questionspool" path="pools,questions" fields="questions.number,questions.title">
				<tr><td><a href="http://edit.vpro.nl/preditor/nodeEdit.jsp?nodeNumber=<mm:field name="questions.number" />"><mm:field name="questions.number" /></a></td><td><mm:field name="questions.title" /></td></tr>
			</mm:list>

		<th align="left" colspan="2">Weblog</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "weblog.leeg.eigen") %></td><td><fmt:message key="weblog.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "weblog.leeg.ander") %></td><td><fmt:message key="weblog.leeg.ander"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "weblog.nieuw") %></td><td><fmt:message key="weblog.nieuw"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "weblog.uitleg") %></td><td><fmt:message key="weblog.uitleg"/></td></tr>

		<th align="left" colspan="2">Tags</th>
	    	<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.leeg.eigen") %></td><td><fmt:message key="tags.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.leeg.ander") %></td><td><fmt:message key="tags.leeg.ander"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.uitleg") %></td><td><fmt:message key="tags.uitleg"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.uitklapmenu") %></td><td><fmt:message key="tags.uitklapmenu"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.uitklapmenu.site") %></td><td>(volgende versie del.icio.us) <fmt:message key="tags.uitklapmenu.site"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "tags.uitklapmenu.tag") %></td><td><fmt:message key="tags.uitklapmenu.tag"/></td></tr>

		<th align="left" colspan="2">Feeds</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "feeds.leeg.eigen") %></td><td><fmt:message key="feeds.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "feeds.leeg.ander") %></td><td><fmt:message key="feeds.leeg.ander"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "feeds.toevoegen.site") %></td><td>(volgende versie del.icio.us) <fmt:message key="feeds.toevoegen.site"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "feeds.toevoegen.profiel") %></td><td>(volgende versie) <fmt:message key="feeds.toevoegen.profiel"/></td></tr>

		<th align="left" colspan="2">Bookmarks</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "bookmarks.leeg.eigen") %></td><td><fmt:message key="bookmarks.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "bookmarks.uitklapmenu") %></td><td><fmt:message key="bookmarks.uitklapmenu"/></td></tr>

		<th align="left" colspan="2">Hitlijsten</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "hitlijst.leeg.eigen") %></td><td><fmt:message key="hitlijst.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "hitlijst.leeg.ander") %></td><td><fmt:message key="hitlijst.leeg.ander"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "hitlijst.uitleg") %></td><td><fmt:message key="hitlijst.uitleg"/></td></tr>

		<th align="left" colspan="2">Vrienden</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "vrienden.leeg.eigen") %></td><td><fmt:message key="vrienden.leeg.eigen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "vrienden.leeg.ander") %></td><td><fmt:message key="vrienden.leeg.ander"/></td></tr>

		<th align="left" colspan="2">Reageer</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "reageer.redactie.waarschuwen") %></td><td><fmt:message key="reageer.redactie.waarschuwen"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "reageer.toevoegen") %></td><td><fmt:message key="reageer.toevoegen"/></td></tr>



		<th align="left" colspan="2">Fouten</th>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "error.required") %></td><td>(NIET AANPASBAAR) <fmt:message key="error.required"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "error.charactersnotallowed") %></td><td>(NIET AANPASBAAR) <fmt:message key="error.charactersnotallowed"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "error.notunique") %></td><td>(NIET AANPASBAAR) <fmt:message key="error.notunique"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "jsp.user.", "upload.sizeExceeded") %></td><td><fmt:message key="upload.sizeExceeded"/></td></tr>

	</fmt:bundle>

	<fmt:bundle basename="nl.vpro.dvt.communities.util.resource.CommunityResourceBundle" prefix="nl.vpro.dvt.communities.services.MailService.">
		<th align="left" colspan="2">Email</th>
			<tr><td><%= getEditorHref(cloud, "nl.vpro.dvt.communities.services.MailService.", "becomeFriend") %></td><td><fmt:message key="becomeFriend"/></td></tr>
			<tr><td><%= getEditorHref(cloud, "nl.vpro.dvt.communities.services.MailService.", "sendToFriend") %></td><td><fmt:message key="sendToFriend"/></td></tr>
	</fmt:bundle>

	</table>

	<p />
		<a href="http://edit.vpro.nl/preditor/nodeEdit.jsp?nodeNumber=33858345">* Bekijk alle 3voor12 resources</a><br />
		<a href="nieuwe_resource.jsp">* Maak nieuwe resource aan</a>
	</p>
	</mm:cloud>
</body>
</html>