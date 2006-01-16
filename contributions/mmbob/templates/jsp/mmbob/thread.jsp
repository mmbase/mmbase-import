<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<body>
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>
<mm:import externid="postingid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
  <mm:field name="postingsperpage" id="pagesize" write="false"/>
  <mm:field name="replyoneachpage" id="replyoneachpage" write="false"/>
</mm:nodefunction>

<mm:notpresent referid="pagesize">
<mm:import id="pagesize">20</mm:import>
</mm:notpresent>

<mm:present referid="postingid">
	<mm:import id="page" reset="true"><mm:function set="mmbob" name="getPostingPageNumber" referids="forumid,postareaid,postthreadid,postingid,pagesize" /></mm:import>
</mm:present>


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:nodefunction set="mmbob" name="getPostThreadInfo" referids="forumid,postareaid,postthreadid,pagesize">
<mm:compare referid="page" value="-1">
<mm:import id="page" reset="true"><mm:field name="pagecount" /></mm:import>
</mm:compare>
  <mm:import id="threadstate"><mm:field name="threadstate" /></mm:import>
  <mm:import id="threadmood"><mm:field name="threadmood" /></mm:import>
  <mm:import id="threadtype"><mm:field name="threadtype" /></mm:import>
</mm:nodefunction>

<%--Check if the poster is an moderator --%>
<mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
  <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
  <mm:import id="guestwritemodetype"><mm:field name="guestwritemodetype" /></mm:import>
  <mm:import id="smileysenabled"><mm:field name="smileysenabled" /></mm:import>
  <mm:import id="privatemessagesenabled"><mm:field name="privatemessagesenabled" /></mm:import>
  <mm:compare referid="posterid" value="-1" inverse="true">
	<mm:import id="guestwritemodetype" reset="true">open</mm:import>
  </mm:compare>
</mm:nodefunction>

<%-- reset the threadstate if the poster is a moderator --%>
<mm:compare referid="ismoderator" value="true">
  <mm:import reset="true" id="threadstate">normal</mm:import>
</mm:compare>

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                                              
<div class="bodypart">
<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
<mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
<mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
<mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
<mm:import id="active_firstname"><mm:field name="active_firstname" /></mm:import>
<mm:import id="active_lastname"><mm:field name="active_lastname" /></mm:import>
<mm:include page="path.jsp?type=postthread" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr><th colspan="2" align="left">
                                        <mm:compare referid="image_logo" value="" inverse="true">
                                        <center><img src="<mm:write referid="image_logo" />" width="60%" ></center>
                                        </mm:compare>
			</th>
			</tr>
</table>


<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
	<tr>
	<form action="<mm:url page="postarea.jsp" referids="forumid" />" method="post">
	<td align="left" />
	<a href=""><mm:write referid="mlg.Area_name"/></a> <select name="postareaid" onChange="submit()">
            <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
		<mm:field name="id">
		<option value="<mm:field name="id" />" <mm:compare referid2="postareaid">selected</mm:compare>><mm:field name="name" />
		</mm:field>
	    </mm:nodelistfunction>
	</select>
	<!-- <input type="submit" value="go"> -->
	</td>
	</form>
	</tr>
</table>
<table cellpadding="0" cellspacing="0" style="margin-top : 4px;" width="95%">
	<tr><td align="left"><b><mm:write referid="mlg.Pages"/>
   	 <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
			(<mm:field name="pagecount" id="pagecount" />) 
			<mm:field name="navline" />
			<mm:import id="lastpage"><mm:field name="lastpage" /></mm:import>
	  </b>
	</td>
	<td align="right">
	<a href="<mm:field name="emailonchange"><mm:compare value="false"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">threademailon</mm:param></mm:url>">Email : <mm:write referid="mlg.off" /></a></mm:compare><mm:compare value="true"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">threademailoff</mm:param></mm:url>">Email : <mm:write referid="mlg.on" /></a></mm:compare></mm:field> | 
	<a href="<mm:field name="bookmarked"><mm:compare value="false"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">bookmarkedon</mm:param></mm:url>">Bookmarked : <mm:write referid="mlg.off" /></a></mm:compare><mm:compare value="true"><mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid"><mm:param name="action">bookmarkedoff</mm:param></mm:url>">Bookmarked : <mm:write referid="mlg.on" /></a></mm:compare></mm:field> | <a href="<mm:url page="bookmarked.jsp" referids="forumid" />">Bookmarked</a> | <a href="<mm:url page="search.jsp" referids="forumid,postareaid,postthreadid" />"><mm:write referid="mlg.Search" /></a>&nbsp;
	</td></tr>
        </mm:nodefunction>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
  		  <mm:nodelistfunction set="mmbob" name="getPostings" referids="forumid,postareaid,postthreadid,posterid,page,pagesize,imagecontext">
		  <mm:first>
			<tr align="left"><th width="25%" align="left"><mm:write referid="mlg.Member"/></th><th align="left"><mm:write referid="mlg.Topic"/>: <mm:field name="subject" /></th></tr>
		  </mm:first>
			<tr align="left">
			<td class="<mm:field name="tdvar" />" align="left">
			<a name="p<mm:field name="id" />">&nbsp;</a>
			<mm:field name="posttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
			</td>
			<td class="<mm:field name="tdvar" />" align="right">
			<mm:remove referid="postingid" />
			<mm:remove referid="toid" />
			<mm:import id="toid"><mm:field name="posterid" /></mm:import>
			<mm:import id="postingid"><mm:field name="id" /></mm:import>
                                
                           <mm:compare referid="guestwritemodetype" value="open"> 
                                <mm:compare referid="privatemessagesenabled" value="true">
                               <a href="<mm:url page="newprivatemessage.jsp" referids="forumid,postareaid,postthreadid,postingid,toid" />"><img src="<mm:write referid="image_privatemsg" />"  border="0" /></a>
                               <a href="<mm:url page="newreportmessage.jsp" referids="forumid,postareaid,postthreadid,postingid" />"><img src="<mm:write referid="image_reportmsg" />"  border="0" /></a>
			 	</mm:compare>
                               <mm:compare referid="threadstate" value="closed" inverse="true">
                                    <a href="<mm:url page="posting.jsp" referids="forumid,postareaid,postthreadid,posterid,pagesize,page,postingid" />"><img src="<mm:write referid="image_quotemsg" />"  border="0" /></a>
                               </mm:compare>
                           </mm:compare>
		     
			   <mm:field name="ismoderator">
				<mm:compare value="true">
  				<a href="<mm:url page="editpost.jsp">
				<mm:param name="forumid" value="$forumid" />
				<mm:param name="postareaid" value="$postareaid" />
				<mm:param name="postthreadid" value="$postthreadid" />
				<mm:param name="postingid" value="$postingid" />
				</mm:url>"><img src="<mm:write referid="image_medit" />"  border="0" /></a>

  				<a href="<mm:url page="removepost.jsp">
				<mm:param name="forumid" value="$forumid" />
				<mm:param name="postareaid" value="$postareaid" />
				<mm:param name="postthreadid" value="$postthreadid" />
				<mm:param name="postingid" value="$postingid" />
				</mm:url>"><img src="<mm:write referid="image_mdelete" />"  border="0" /></a>

				</mm:compare>
			</mm:field>
			&nbsp;
                        <mm:compare referid="threadstate" value="closed" inverse="true">
			  <mm:field name="isowner">
				<mm:compare value="true">
				<mm:remove referid="postingid" />
				<mm:import id="postingid"><mm:field name="id" /></mm:import>
  				<a href="<mm:url page="editpost.jsp">
				<mm:param name="forumid" value="$forumid" />
				<mm:param name="postareaid" value="$postareaid" />
				<mm:param name="postthreadid" value="$postthreadid" />
				<mm:param name="postingid" value="$postingid" />
				</mm:url>"><img src="<mm:write referid="image_editmsg" />"  border="0" /></a>

                                <a href="<mm:url page="removepost.jsp">
                                <mm:param name="forumid" value="$forumid" />
                                <mm:param name="postareaid" value="$postareaid" />
                                <mm:param name="postthreadid" value="$postthreadid" />
                                <mm:param name="postingid" value="$postingid" />
                                </mm:url>"><img src="<mm:write referid="image_mdelete" />" border="0" /></a>

				</mm:compare>
			</mm:field>
                        </mm:compare>

			</td>
			</tr>
			<tr>
			<td class="<mm:field name="tdvar" />" valign="top" align="left">
                        <p>
                        <mm:field name="guest">
                        <mm:compare value="true">
				<b><mm:field name="poster" /></b>
                        </mm:compare>

			<mm:compare value="true" inverse="true">
			
                            <b><a href="profile.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&type=poster_thread&posterid=<mm:field name="posterid" />&postthreadid=<mm:write referid="postthreadid" />"><mm:field name="poster" /></b>  (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
                            <mm:field name="avatar">
                              <mm:compare value="-1" inverse="true">
                                <mm:node number="$_">
                                  <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                                </mm:node>
                              </mm:compare>
                            </mm:field>
                        </a>
                        <p />

			<mm:write referid="mlg.Level"/> : <mm:field name="levelgui" /><br /> <img src="<mm:field name="levelimage" />" /><br />
			<mm:write referid="mlg.Posts"/> : <mm:field name="accountpostcount" /><br />
			<mm:write referid="mlg.Gender"/> : <mm:field name="gender" /><br />
			<mm:write referid="mlg.Location"/> : <mm:field name="location" /><br />
			<mm:write referid="mlg.Member_since"/> : <mm:field name="firstlogin"><mm:time format="d MMMM  yyyy" /></mm:field><br />
			<mm:write referid="mlg.Last_visit"/> : <mm:field name="lastseen"><mm:time format="d/MM/yy HH:mm" /> </mm:field><br />
			</mm:compare>
			</mm:field>
			<br /><br />
                        </p>
			</td>
			<td class="<mm:field name="tdvar" />" valign="top" align="left">
			<mm:field name="edittime"><mm:compare value="-1" inverse="true"><mm:write referid="mlg.last_time_edited"/> : <mm:field name="edittime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></mm:compare><p /></mm:field>
           
            <mm:field name="body" />

		<br />
		<mm:field name="signature">
		<mm:compare value="" inverse="true">
		_______<br />
		<mm:field name="signature" escape="p" />
		</mm:compare>
		</mm:field>
		<br />
			</td>
			</tr>
		  </mm:nodelistfunction>
</table>


<table cellpadding="0" cellspacing="0" style="margin-top : 2px;" width="95%">
	<tr><td align="left"><b><mm:write referid="mlg.Pages"/>
   	 	  <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,posterid,page,pagesize">
			<mm:field name="navline" />
		  </mm:nodefunction>
	  </b>
	</td></tr>
</table>

<mm:import id="showreply">true</mm:import>
<mm:compare referid="replyoneachpage" value="false">
	<mm:compare referid="lastpage" value="false">
		<mm:import id="showreply" reset="true">false</mm:import>
	</mm:compare>
</mm:compare>

<mm:compare referid="showreply" value="true">
<mm:compare referid="threadstate" value="closed" inverse="true">

<mm:compare referid="guestwritemodetype" value="open">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
  <tr><th colspan="3"><mm:write referid="mlg.Reply"/></th></tr>
	<mm:import externid="error" from="session">none</mm:import>
	<mm:compare referid="error" value="none" inverse="true">
	<tr><th colspan="3">
	<mm:compare referid="error" value="no_subject">
	<font color="red"><mm:write referid="mlg.problem_missing_topic" /></font>
	</mm:compare>
	<mm:compare referid="error" value="no_body">
	<font color="red"><mm:write referid="mlg.problem_missing_body" /></font>
	</mm:compare>
	<mm:compare referid="error" value="duplicate_post">
	<font color="red"><mm:write referid="mlg.problem_already_posted" /></font>
	</mm:compare>
	<mm:compare referid="error" value="illegal_html">
	<font color="red"><mm:write referid="mlg.problem_illegal_html" /></font>
	</mm:compare>
	<mm:compare referid="error" value="speed_posting">
	<mm:import externid="speedposttime" from="session">60</mm:import>
	<font color="red"><mm:write referid="mlg.problem_speedposting" /><mm:write referid="speedposttime" /> sec ***</font>
	</mm:compare>
	</th></tr>
	</mm:compare>
  <mm:import id="page" reset="true">-1</mm:import>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,page" />#reply" method="post" name="posting">
	<tr><th width="25%"><mm:write referid="mlg.Name"/></th><td>

		<mm:compare referid="posterid" value="-1" inverse="true">
		<mm:write referid="active_nick" /> (<mm:write referid="active_firstname" /> <mm:write referid="active_lastname" />)
		<input name="poster" type="hidden" value="<mm:write referid="active_nick" />" >
		</mm:compare>
		<mm:compare referid="posterid" value="-1">
		<input name="poster" type="hidden" style="width: 99%" value="<mm:write referid="mlg.guest"/>" >
		<mm:write referid="mlg.guest"/>
		</mm:compare>

		</td></tr>
	<tr><th><mm:write referid="mlg.Reply"/> <center><table width="100"><tr><th><mm:compare referid="smileysenabled" value="true"><%@ include file="includes/smilies.jsp" %></mm:compare></th></tr></table></center> </th><td><textarea name="body" rows="5" style="width: 99%"><mm:compare referid="error" value="none" inverse="true"><mm:import externid="body" from="session"></mm:import><mm:write referid="body" /><mm:import id="error" reset="true">none</mm:import><mm:write referid="error" session="error" /></mm:compare></textarea></td></tr>
	<tr><td colspan="3"><input type="hidden" name="action" value="postreply">
	<center><input type="submit" value="<mm:write referid="mlg.Post_reply"/>"/></center>
	</td></tr>
  </form>
  <a name="reply">&nbsp;</a>
</table>
</mm:compare>
</mm:compare>
</mm:compare>
<br />
<br />
</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</mm:locale>

</body>
</html>
</mm:content>
</mm:cloud>
