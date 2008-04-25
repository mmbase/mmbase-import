<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><mm:cloud><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp"
%>
<div id="pagecontent">
<mm:import externid="doc"   from="parameters">-1</mm:import>
<mm:import externid="news"  from="parameters">-1</mm:import>
<mm:compare referid="doc" value="-1" inverse="true">
   <mm:node number="$doc">
       <%--@include file="/includes/backbutton.jsp"--%>
       <%@include file="/includes/article.jsp" %>
  </mm:node>
   <mm:import id="pageshown" />
</mm:compare>
<mm:notpresent referid="pageshown">
  <mm:compare referid="news" value="-1" inverse="true">
     <mm:node number="$news" >
	<%--@include file="/includes/backbutton.jsp" --%>
        <%@include file="/includes/article.jsp" %>
     </mm:node>
    <mm:import id="pageshown" />
  </mm:compare>
</mm:notpresent>
<mm:notpresent referid="pageshown">
<mm:node number="$page">
<%-- width total:
800 +/- = 150 (menu) + news (200 = +/- 30%) + whitespace (12) + articles (240 = +/- 35%) + 
  whitespace (12) + search (190 = +/- 25%)
--%>
<table cellspacing="0" cellpadding="0" border="0">
<tr valign="top">
  <td width="30%">
<%-- ### news ### --%>
    <% String nwspage = "mmbase_news"; %>
	<mm:list nodes="$portal" 
	  path="portals,category,news,mmevents" searchdir="destination"
	  fields="category.number,category.title,news.number,news.title,mmevents.start" 
	  orderby="mmevents.start" directions="DOWN"
	  max="4">
	  <mm:first>
		<h2>News</h2>		
		<!-- category : <mm:field name="category.title" /> -->
		<mm:field name="category.number" id="cat" write="false" />
		<%-- what is the newspage of this category --%>
		<mm:list nodes="$cat" path="category,pages" searchdir="destination"
		  fields="pages.number" max="1">
		  <mm:field name="pages.number" vartype="String" write="false" jspvar="nr"><% nwspage = nr; %></mm:field>
		</mm:list>
	  </mm:first>
	  <p><a href="<mm:url page="index.jsp" referids="portal">
		<mm:param name="page"><%= nwspage %></mm:param>
		<mm:param name="newsnr"><mm:field name="news.number" /></mm:param>
	  </mm:url>"><mm:field name="news.title" /></a><br /> 
	  <mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field>
	  <mm:field name="news.intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p> 
	  <mm:last><p><a href="<mm:url page="index.jsp" referids="portal">
		<mm:param name="page"><%= nwspage %></mm:param>
	  </mm:url>">Newsarchive &raquo;&raquo;</a></p></mm:last>
	  <mm:remove referid="cat" />
	</mm:list>
	<% if (nwspage.equals("mmbase_news")) { %>
	  <h2>News</h2>
	  <mm:listnodes type="news" max="4" orderby="number" directions="DOWN">
		<mm:field name="number" id="newsnr" write="false" />
		<p><a href="<mm:url page="index.jsp" referids="portal,newsnr"><mm:param name="page">mmbase_news</mm:param></mm:url>"><mm:field name="title" /></a>
		<br /><mm:related path="mmevents"><mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field></mm:related>
		<mm:field name="intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p>
		<mm:remove referid="newsnr" />
	  </mm:listnodes>
	  <p><a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page">mmbase_news</mm:param></mm:url>">Newsarchive &raquo;&raquo;</a></p>
	<% } %>
<%-- ### /news ### --%>
  </td>
  <td><img src="media/spacer.gif" alt="" border="0" width="12" height="1" /></td>
  <td width="35%">
<%-- ### articles ### --%>
	<mm:related path="articles" max="1"><h2><mm:field name="articles.title"/></h2>
		<p><mm:field name="articles.intro" escape="none" /><!-- incredibly, the intro is html?? --></p>
	</mm:related>
	<mm:related path="posrel,documentation" orderby="posrel.pos" directions="UP">
		<mm:first>
		<div style="margin-top:12px; z-index:2;">
		<form name="infoform" action="" method="post">
		<select name="doc" style="width:240px;" onchange="javascript:postInfoForm();">
		</mm:first><option value="<mm:field name="documentation.number" />"><mm:field name="documentation.title" /></option>
		<mm:last></select> | <a href="javascript:postInfoForm();">go</a>
		</form>
		</div>
		<script language="JavaScript" type="text/javascript">
		<%= "<!--" %>
		function postInfoForm() {
			href = "index.jsp?portal=<mm:write referid="portal" />&amp;page=<mm:write referid="page" />";
			var doc = document.infoform.elements["doc"].value;
			if (doc != '') { 
					href += "&doc=" + doc; 
			}
			document.location = href;
		}
		<%= "//-->" %>
		</script>
		</mm:last>
	</mm:related>
	
	<div style="margin-top:12px; z-index:2;">
	<mm:list nodes="page_mmbasewebsites" path="pages,posrel,urls" searchdir="destination"
	  fields="urls.number,urls.name,urls.url"
	  orderby="urls.number" directions="DOWN" max="5">
	  <mm:first><h5>Latest websites build with MMBase</h5>
	  <form name="sitesform" action="" method="post">
	  <select name="news" style="width:240px;" onchange="javascript:postSiteForm();">
	  </mm:first>
		<option value="<mm:field name="urls.url" />"><mm:field name="urls.name" /></option>
      <mm:last>
      </select> | <a href="javascript:postSiteForm();">go</a>
	  </form>
	  <p><a href="<mm:url page="index.jsp" referids="portal">
			<mm:param name="page">page_mmbasewebsites</mm:param>
	  </mm:url>">More MMBase websites &raquo;&raquo;</a></p>
	  </div>
	  <script language="javascript" type="text/javascript">
	  <%= "<!--" %>
	  function postSiteForm() {
		  //href = "index.jsp?portal=<mm:write referid="portal" />&amp;page=<mm:write referid="page" />";
		  var news = document.sitesform.elements["news"].value;
		  if (news != '') { 
				  href = news;
		  }
		  document.location = href;
	  }
	  <%= "//-->" %>
	  </script>
	  </mm:last>
	</mm:list>
	
<%-- ### /articles ### --%>
  </td>
  <td><img src="media/spacer.gif" alt="" border="0" width="12" height="1" /></td>
  <td width="25%">
    <a href="http://www.cmscontainer.org"><img class="logocmsc" src="<mm:url page="/media/cmsc-logo.gif" />" alt="CMSContainer" width="100" height="100" /></a>
	<mm:time time="today" id="ttoday" write="false" />
	<mm:list nodes="$portal" 
		path="portals,category,posrel,event,mmevents"
		fields="category.number,event.number,event.title,mmevents.start" 
		orderby="mmevents.start" directions="UP"
		max="5" constraints="mmevents.start >= $ttoday">
		<mm:context><mm:first>
		  <mm:field name="category.number" id="cat" write="false" />
		  <%-- what is the agendapage of this category --%>
		  <mm:list nodes="$cat" path="category,pages" max="1"><mm:field name="pages.number" write="false" id="event_page" /></mm:list>
		  <h4>Agenda</h4><p>
		</mm:first>
		<mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field><br />
		<a href="<mm:url page="index.jsp" referids="portal">
		  <mm:present referid="event_page"><mm:param name="page"><mm:write referid="event_page" /></mm:param></mm:present>
		  <mm:notpresent referid="event_page"><mm:param name="page">page_agenda</mm:param></mm:notpresent>
		  <mm:param name="item"><mm:field name="event.number" /></mm:param>
		</mm:url>"><mm:field name="event.title" /></a>
		<mm:last inverse="true"><br /></mm:last>
	    <mm:last></p>
	      <p><a href="<mm:url page="index.jsp" referids="portal">
			<mm:present referid="event_page"><mm:param name="page"><mm:write referid="event_page" /></mm:param></mm:present>
			<mm:notpresent referid="event_page"><mm:param name="page">page_agenda</mm:param></mm:notpresent>
		  </mm:url>">Agenda &raquo;&raquo;</a></p>
		</mm:last></mm:context>
	</mm:list>
	
	<h4>Bugtracker</h4>
	<p>In the last 7 days:<br />
	<mm:link page="http://www.mmbase.org/jira/secure/IssueNavigator.jspa?reset=true&mode=hide&pid=10000&created:previous=-1w&sorter/field=created&sorter/order=DESC">
	  <a href="${_}">
	</mm:link>
	<cache:cache time="3600">
	<mm:formatter escape="none">
	  <mm:include cite="true" page="http://www.mmbase.org/jira/secure/IssueNavigator.jspa">
	    <mm:param name="view">rss</mm:param>
	    <mm:param name="pid">10000</mm:param>
	    <mm:param name="created:previous">-1w</mm:param>
	    <mm:param name="sorter/field">created</mm:param>
	    <mm:param name="sorter/order">DESC</mm:param>
	    <mm:param name="tempMax">25</mm:param>
	    <mm:param name="reset">true</mm:param>
	    <mm:param name="decorator">none</mm:param>
	  </mm:include>
	  <mm:xslt>
	    <xsl:template match="channel">
	      <xsl:value-of select="count(//item)" />
	    </xsl:template>
	  </mm:xslt>
	</mm:formatter> new issues</a><br />
	<mm:link page="http://www.mmbase.org/jira/secure/IssueNavigator.jspa?reset=true&mode=hide&pid=10000&status=5&status=6&updated:previous=-1w&sorter/field=updated&sorter/order=DESC">
	  <a href="${_}">
	</mm:link>
	<mm:formatter escape="none">
	  <mm:include cite="true" page="http://www.mmbase.org/jira/secure/IssueNavigator.jspa">
	    <mm:param name="view">rss</mm:param>
	    <mm:param name="pid">10000</mm:param>
	    <mm:param name="status">5</mm:param>
	    <mm:param name="status">6</mm:param>
	    <mm:param name="updated:previous">-1w</mm:param>
	    <mm:param name="sorter/field">updated</mm:param>
	    <mm:param name="sorter/order">DESC</mm:param>
	    <mm:param name="tempMax">25</mm:param>
	    <mm:param name="reset">true</mm:param>
	    <mm:param name="decorator">none</mm:param>
	  </mm:include>
	  <mm:xslt>
	    <xsl:template match="channel">
	      <xsl:value-of select="count(//item)" />
	    </xsl:template>
	  </mm:xslt>
	</mm:formatter> issues solved</a>.<br />
	<a href="/browseproject">Go to bugtracker &raquo;&raquo;</a></p>

	<mm:node number="portal_developers"><mm:field name="number"><mm:compare value="$portal">
	<p>Recently updated:</p>
	<mm:formatter escape="none">
	  <mm:include cite="true" page="http://www.mmbase.org/jira/secure/IssueNavigator.jspa">
	    <mm:param name="view">rss</mm:param>
	    <mm:param name="pid">10000</mm:param>
	    <mm:param name="update:previous">-1w</mm:param>
	    <mm:param name="sorter/field">updated</mm:param>
	    <mm:param name="sorter/order">DESC</mm:param>
	    <mm:param name="tempMax">5</mm:param>
	    <mm:param name="decorator">none</mm:param>
	    <mm:param name="reset">true</mm:param>
	  </mm:include>
	  <mm:xslt>
	    <xsl:template match="channel">
	      <ul class="none">
		<xsl:for-each select="item">
		  <xsl:if test="position() &lt; 6">
		    <li>
		      <a href="{link}"><xsl:value-of select="title" /> (<xsl:value-of select="component" />)</a>
		    </li>
		  </xsl:if>
		</xsl:for-each>
	      </ul>
	    </xsl:template>
	  </mm:xslt>
	</mm:formatter>
	</mm:compare></mm:field></mm:node>
	</cache:cache>
<%-- ### /search, agenda, dev mail ? ### --%>
</td>
</tr>
</table>
</div>

<%-- logo's @ pagebottom --%>
<mm:related path="posrel,organisation,posrel,images" searchdir="destination"
    fields="posrel.pos,organisation.number,organisation.name"
	orderby="organisation.name" directions="UP">
	<mm:remove referid="org_name" /><mm:remove referid="org" />
	<mm:field name="organisation.name" id="org_name" write="false" />
	<mm:field name="organisation.number" id="org" write="false" />
	<mm:node element="images">
	  <mm:first><div class="sponsorlogos"></mm:first>
	  <a title="More about <mm:write referid="org_name" />" href="<mm:url referids="org"
		  ><mm:param name="portal">foundation</mm:param
		  ><mm:param name="page">organisations</mm:param></mm:url>"><img src="<mm:image template="s(60x35)" />" 
			  border="0" hspace="4" vspace="4" alt="<mm:write referid="org_name" />" /></a>
	  <mm:last></div></mm:last>
	</mm:node>
</mm:related>
</mm:node>
</mm:notpresent>

<%@ include file="/includes/alterfooter.jsp"%>
</mm:cloud>
