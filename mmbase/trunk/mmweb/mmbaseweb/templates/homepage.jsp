<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud>

<%@include file="/includes/getids.jsp"
%><%@include file="/includes/header.jsp"

%><td class="white" colspan="2" valign="top">
<mm:import externid="doc"   from="parameters">-1</mm:import
><mm:import externid="news"  from="parameters">-1</mm:import
><mm:compare referid="doc" value="-1" inverse="true"
	><mm:node number="$doc"
		><%@include file="/includes/backbutton.jsp"
		%><%@include file="/includes/article.jsp"
	%></mm:node
><mm:import id="pageshown" 
/></mm:compare
><mm:notpresent referid="pageshown"
><mm:compare referid="news" value="-1" inverse="true"
	><mm:node number="$news"
		><%@include file="/includes/backbutton.jsp"
		%><%@include file="/includes/article.jsp"
	%></mm:node
><mm:import id="pageshown" 
/></mm:compare
></mm:notpresent
><mm:notpresent referid="pageshown"
><mm:node number="$page"
><table cellspacing="0" cellpadding="0" class="layout">
<tr><td>
<table class="layout">
	<tr>
	<td valign="top" width="258">
		<%-- ### articles ### --%>
		<mm:related path="articles" max="1"><h2><mm:field name="articles.title"/></h2>
			<mm:field name="articles.intro"	/>
		</mm:related>
		<mm:related path="posrel,documentation" orderby="posrel.pos" directions="UP">
			<mm:first>
			<div z-index="2">
			<form name="infoform" action="" method="post">
			<select name="doc" style="width:200;">
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
		<mm:related path="posrel,news" orderby="posrel.pos" directions="UP" constraints="posrel.pos < 10">
		 	<mm:first
				><h4>Latest websites build with MMBase</h4>
				<form name="portofolioform" action="" method="post">
					<select name="news" style="width:200;">
			</mm:first
					><option value="<mm:field name="news.number" />"><mm:field name="news.title" /></option>
			<mm:last
					></select> | <a href="javascript:postPortofolioForm();">go</a>
				</form>
				<script language="JavaScript" type="text/javascript">
				<%= "<!--" %>
				function postPortofolioForm() {
					href = "index.jsp?portal=<mm:write referid="portal" />&amp;page=<mm:write referid="page" />";
					var news = document.portofolioform.elements["news"].value;
					if (news != '') { 
							href += "&news=" + news; 
					}
					document.location = href;
				}
				<%= "//-->" %>
				</script>
			</mm:last
		></mm:related>

     </td>

	<td width="17"><img src="media/spacer.gif" alt="" border="0" width="17" height="1" /></td>
	<td valign="top" width="163">
<%-- ### news ### --%>
<mm:list nodes="$portal" 
	path="portals,category,news,mmevents"
	fields="category.number,category.title,news.number,news.title,mmevents.start" 
	orderby="mmevents.start" directions="DOWN"
	max="5">
	<mm:first>
		<h4>News</h4>		<!-- category : <mm:field name="category.title" /> -->
		<mm:field name="category.number" id="cat_nr" write="false" />
		<%-- what is the newspage of this category --%>
		<mm:list nodes="$cat_nr" path="category,pages" max="1"><mm:field name="pages.number" write="false" id="nws_page" /></mm:list>
		<mm:import id="nonewsyet" />
	</mm:first>
	<p><a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="nws_page"><mm:param name="page"><mm:write referid="nws_page" /></mm:param></mm:present><mm:param name="newsnr"><mm:field name="news.number" /></mm:param></mm:url>"><mm:field name="news.title" /></a><br /> 
	<mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field>
	<mm:field name="news.intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p> 
	<mm:last><p><a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page"><mm:write referid="nws_page" /></mm:param></mm:url>">Newsarchive</a></p></mm:last>
	<mm:remove referid="cat_nr" />
</mm:list>

<mm:notpresent referid="nonewsyet">
	<h4>News</h4>
	<mm:listnodes type="news" max="3" orderby="number" directions="DOWN">
		<mm:field name="number" id="newsnr" write="false" />
		<p><a href="<mm:url page="index.jsp" referids="portal,newsnr"><mm:param name="page">mmbase_news</mm:param></mm:url>"><mm:field name="title" /></a>
		<br /><mm:related path="mmevents"><mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field></mm:related>
		<mm:field name="intro"><mm:isnotempty> - <mm:write /></mm:isnotempty></mm:field></p>
		<mm:remove referid="newsnr" />
	</mm:listnodes>
	<p><a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page">mmbase_news</mm:param></mm:url>">Newsarchive</a></p>
</mm:notpresent>
<%-- ### /news ### --%>
	 </td>
     <td width="17"><img src="media/spacer.gif" alt="" border="0" width="17" height="1" /></td>
 </tr>
</table>
</td>
<td valign="top" width="205">
<%-- ### search, agenda, dev mail ? ### --%>
	<table cellspacing="0" cellpadding="0" width="140" class="layout">
	<tr>
	  <td>
		<form name="searchform" method="get" action="/development/search/search_results.jsp">
		<h4>Search</h4>
		<input name="words" size="13" type="text"> | <a href="javascript:void(document.searchform.submit())">go</A> 
		</form>
	  </td>
	</tr><tr><td><img src="media/spacer.gif" width="140" height="4" alt="" /></td></tr><tr>
	  <td>
	<mm:time time="today" id="ttoday" write="false" />
	<mm:list nodes="$portal" 
		path="portals,category,posrel,event,mmevents"
		fields="category.number,event.number,event.title,mmevents.start" 
		orderby="mmevents.start" directions="DOWN"
		max="5" constraints="mmevents.start >= $ttoday">
		<mm:first>
		  <mm:field name="category.number" id="cat_nr" write="false" />
		  <%-- what is the agendapage of this category --%>
		  <mm:list nodes="$cat_nr" path="category,pages" max="1"><mm:field name="pages.number" write="false" id="event_page" /></mm:list>
		  <b>Coming soon:</b><br />
		</mm:first>
		<mm:field name="mmevents.start"><mm:time format=":MEDIUM" /></mm:field><br />
		<a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="event_page"><mm:param name="page"><mm:write referid="event_page" /></mm:param></mm:present><mm:param name="item"><mm:field name="event.number" /></mm:param></mm:url>"><mm:field name="event.title" /></a><br />
	</mm:list>
	  </td>
	</tr><tr><td><img src="media/spacer.gif" width="140" height="4" alt="" /></td></tr><tr>
	  <td>
	  <b>Last bugfixes:</b><br />
	  <mm:listnodes type="bugreports" orderby="time" directions="DOWN" max="5" constraints="bstatus > 4">
		<mm:last><mm:index /></mm:last>
	  </mm:listnodes>

	  <b>Last new repported bugs:</b><br />
	  <mm:listnodes type="bugreports"
	  	orderby="time" directions="DOWN" max="5"
	  	constraints="bstatus = 1">
	  	status: <mm:field name="bstatus" />
	  	issue: <mm:field name="issue" /><br />
	  </mm:listnodes>
	  
		<a href="http://www.elfling.nl/projects/mmbase/layout/index.html#">3 bugs fixed</a>
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
</td></tr>
</table>

<%-- ************* things above this line: some still in html *** --%>
<%	int imageNumber = 0;
%><mm:related path="posrel,organisation,images" orderby="posrel.pos" directions="UP"
><mm:remove referid="org_name"
/><mm:remove referid="org"
/><mm:field name="organisation.name" id="org_name" write="false"
/><mm:field name="organisation.number" id="org" write="false"
/><mm:node element="images"
	><mm:first
		><div align="center">
		<table cellspacing="3" cellPadding="0">
		<tr><td colspan="10"><img src="media/spacer.gif" height="10" width="1"></td></tr>
		<tr><td colspan="10" class="black"><img src="media/spacer.gif" height="1" width="1"></td></tr>
	<tr>
	</mm:first>
	<td><a href="<mm:url referids="org"
			><mm:param name="portal">foundation</mm:param
			><mm:param name="page">organisations</mm:param
		></mm:url
		>"><img src="<mm:image template="s(60x60)"
		/>" border="0" title="Click for details on <mm:write referid="org_name" />"></a></td>
	<% 	imageNumber++; 
		if((imageNumber%10)==0) { 
			%></tr></table></div>
			<div align="center"><table cellspacing="3" cellpadding="0"><tr><%
		}
	%><mm:last
		></tr>
		</table>
		</div>
	</mm:last
></mm:node
></mm:related
></mm:node
></mm:notpresent
>

</td>
<%@include file="/includes/footer.jsp"
%></mm:cloud>



