<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="true"
%><%-- why is the session used ?! --%><mm:cloud>
<%@include file="/includes/getids.jsp"%>
<%@include file="/includes/header.jsp"%>
<td class="white" colspan="2" valign="top">
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
<mm:node number="$page"><%-- width total:
800 +/- = 150 (menu) + news (200) + whitespace (12) + articles (240) + whitespace (12) + search (190)
--%>
<table cellspacing="0" cellpadding="0" width="100%" border="0">
<tr valign="top">
  <td width="200">
<%-- ### news ### --%>
	<mm:list nodes="$portal" 
		path="portals,category,news,mmevents" searchdir="destination"
		fields="category.number,category.title,news.number,news.title,mmevents.start" 
		orderby="mmevents.start" directions="DOWN"
		max="4">
		<mm:first>
			<h2>News</h2>		<!-- category : <mm:field name="category.title" /> -->
			<mm:field name="category.number" id="cat_nr" write="false" />
			<%-- what is the newspage of this category --%>
			<mm:list nodes="$cat_nr" path="category,pages" max="1"><mm:field name="pages.number" write="false" id="nws_page" /></mm:list>
			<mm:import id="nonewsyet" />
		</mm:first>
		<p><a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="nws_page"><mm:param name="page"><mm:write referid="nws_page" /></mm:param></mm:present><mm:param name="newsnr"><mm:field name="news.number" /></mm:param></mm:url>"><mm:field name="news.title" /></a><br /> 
		<mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field>
		<mm:field name="news.intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p> 
		<mm:last><p><a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page"><mm:write referid="nws_page" /></mm:param></mm:url>">Newsarchive &raquo;&raquo;</a></p></mm:last>
		<mm:remove referid="cat_nr" />
	</mm:list>
	
	<mm:notpresent referid="nonewsyet">
		<h2>News</h2>
		<mm:listnodes type="news" max="4" orderby="number" directions="DOWN">
			<mm:field name="number" id="newsnr" write="false" />
			<p><a href="<mm:url page="index.jsp" referids="portal,newsnr"><mm:param name="page">mmbase_news</mm:param></mm:url>"><mm:field name="title" /></a>
			<br /><mm:related path="mmevents"><mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field></mm:related>
			<mm:field name="intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p>
			<mm:remove referid="newsnr" />
		</mm:listnodes>
		<p><a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page">mmbase_news</mm:param></mm:url>">Newsarchive &raquo;&raquo;</a></p>
	</mm:notpresent>
<%-- ### /news ### --%>
  </td>
  <td width="12"><img src="media/spacer.gif" alt="" border="0" width="12" height="1" /></td>
  <td width="240">
<%-- ### articles ### --%>
	<mm:related path="articles" max="1"><h2><mm:field name="articles.title"/></h2>
		<p><mm:field name="articles.intro" /></p>
	</mm:related>
	<mm:related path="posrel,documentation" orderby="posrel.pos" directions="UP">
		<mm:first>
		<div style="margin-top:12px" z-index="2">
		<form name="infoform" action="" method="post">
		<select name="doc" style="width:200;" onchange="javascript:postInfoForm();">
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
	<mm:related path="posrel,news" searchdir="destination"
		orderby="posrel.pos" directions="UP" max="5">
		<mm:first><h4>Latest websites build with MMBase</h4>
		<table border="0" cellspacing="0"></mm:first>
		<tr valign="top">
		  <td>&raquo;</td>
		  <td><a href="<mm:url page="index.jsp" referids="portal,page">
			<mm:param name="news"><mm:field name="news.number" /></mm:param>
		    </mm:url>"><mm:field name="news.title" /></a></td>
		</tr>
		<mm:last></table><p><a href="<mm:url page="index.jsp" referids="portal">
			<mm:param name="page">page_mmbasewebsites</mm:param>
		</mm:url>">More MMBase websites &raquo;&raquo;</a></p></mm:last>
	</mm:related>
<%-- ### /articles ### --%>
  </td>
  <td width="12"><img src="media/spacer.gif" alt="" border="0" width="12" height="1" /></td>
  <td valign="top" width="190">
<%-- ### search etc. ### --%>
	<table cellspacing="0" cellpadding="0" width="100%" border="0">
	<tr>
	  <td>
		<form name="searchform" id="searchhome" method="post" action="<mm:url page="/development/search/search.jsp" />">
		<input type="hidden" name="exclude" value="testing" />
		<input type="text" name="keywords" size="13" />
		<input type="submit" name="search" value="Search" /><br />
		<input class="ie" type="radio" name="restrict" value="" checked="checked" /> full site
		<input class="ie" type="radio" name="restrict" value="mmdocs" /> documentation
		</form>
	  </td>
	</tr><tr>
	  <td><img src="media/spacer.gif" width="140" height="4" alt="" /></td>
	</tr><tr>
	  <td>
	<mm:time time="today" id="ttoday" jspvar="ttoday" write="false" />
	<mm:list nodes="$portal" 
		path="portals,category,posrel,event,mmevents"
		fields="category.number,event.number,event.title,mmevents.start" 
		orderby="mmevents.start" directions="UP"
		max="5" constraints="mmevents.start >= $ttoday">
		<mm:first>
		  <mm:field name="category.number" id="cat_nr" write="false" />
		  <%-- what is the agendapage of this category --%>
		  <mm:list nodes="$cat_nr" path="category,pages" max="1"><mm:field name="pages.number" write="false" id="event_page" /></mm:list>
		  <h5>Coming soon</h5>
		</mm:first>
		<mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field><br />
		<a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="event_page"><mm:param name="page"><mm:write referid="event_page" /></mm:param></mm:present><mm:param name="item"><mm:field name="event.number" /></mm:param></mm:url>"><mm:field name="event.title" /></a>
		<mm:last inverse="true"><br /></mm:last>
	</mm:list>
	    <p><a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="event_page"><mm:param name="page"><mm:write referid="event_page" /></mm:param></mm:present></mm:url>">Agenda &raquo;&raquo;</a></p>
	  </td>
	</tr><tr>
	  <td><img src="media/spacer.gif" width="140" height="4" alt="" /></td>
	</tr><tr>
	  <td>
	  <h5>Bugs this week</h5>
	  <mm:time time="today" id="lastweek" offset="-604800" write="false" />
	  <mm:listnodescontainer type="bugreports">
	      <mm:sortorder field="time"  direction="DOWN" />
              <mm:constraint field="time"    operator=">=" value="$lastweek" />
              <mm:constraint field="bstatus" operator=">" value="4" />
	      <a href="<mm:url page="/?portal=199&amp;page=546&amp;sstatus=6" />">Solved : <mm:size /></a>
	  </mm:listnodescontainer>
	  <br />
	  <mm:listnodescontainer type="bugreports">
	      <mm:sortorder field="time"  direction="DOWN" />
              <mm:constraint field="time"    operator=">=" value="$lastweek" />
              <mm:constraint field="bstatus" operator="<" value="2" />
	      <a href="<mm:url page="/?portal=199&amp;page=546&amp;sstatus=1" />">New : <mm:size /></a>
	  </mm:listnodescontainer>
	  <p><a href="/bug">Bugtracker &raquo;&raquo;</a></p>
	  </td>
	</tr>
<%--	<tr><td><img src="media/spacer.gif" width="140" height="4" alt="" /></td></tr><tr>
	  <td><b>Latest issue on developers mail:</b><br />
	  <a href="http://www.elfling.nl/projects/mmbase/layout/index.html#">Hack: Oracle support</a>
	  </td>
	</tr>
--%>	
	</table>
<%-- ### /search, agenda, dev mail ? ### --%>
</td>
</tr>
</table>
</td>
</tr><tr>
<td valign="top" colspan="3">
<%-- logo's @ pagebottom --%>
<mm:related path="posrel,organisation,posrel,images" searchdir="destination"
    fields="posrel.pos,organisation.number,organisation.name"
	orderby="organisation.name" directions="UP">
	<mm:remove referid="org_name" /><mm:remove referid="org" />
	<mm:field name="organisation.name" id="org_name" write="false" />
	<mm:field name="organisation.number" id="org" write="false" />
	<mm:node element="images">
	  <mm:first><div style="margin-bottom:12px;" align="center"></mm:first>
	  <a title="More about <mm:write referid="org_name" />" href="<mm:url referids="org"
		  ><mm:param name="portal">foundation</mm:param
		  ><mm:param name="page">organisations</mm:param></mm:url>"><img src="<mm:image template="s(60x35)" />" 
			  border="0" hspace="4" vspace="4" alt="<mm:write referid="org_name" />" /></a>
	  <mm:last></div></mm:last>
	</mm:node>
</mm:related>
</td>
</tr><tr>
<td colspan="3">
&nbsp;
</mm:node>
</mm:notpresent>
</td>
<%@include file="/includes/footer.jsp"
%></mm:cloud>

<!-- END FILE: /mmbaseweb/templates/homepage_new.jsp -->
