<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
<mm:include page="path.jsp?type=postthread" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
                        <tr><th colspan="2" align="left">
                                        <mm:compare referid="image_logo" value="" inverse="true">
                                        <center><img src="<mm:write referid="image_logo" />" width="100%" ></center>
                                        </mm:compare>
            </th>
            </tr>
</table>
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
    <tr><td align="left"><b>Pagina's
          <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,page">
            (<mm:field name="pagecount" />) 
            <mm:field name="navline" />
            <mm:import id="lastpage"><mm:field name="lastpage" /></mm:import>
          </mm:nodefunction>
      </b>
    </td></tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" width="95%">
          <mm:nodelistfunction set="mmbob" name="getPostings" referids="forumid,postareaid,postthreadid,posterid,page">
          <mm:first>
            <tr><th width="25%" align="left"><mm:write referid="poster" /></th><th align="left"><mm:write referid="topic" /> : <mm:field name="subject" /></th></tr>
          </mm:first>
          <mm:remove referid="tdvar" />
          <mm:even><mm:import id="tdvar"></mm:import></mm:even>
          <mm:odd><mm:import id="tdvar">listpaging</mm:import></mm:odd>

            <tr>
            <td class="<mm:write referid="tdvar" />" align="left">
            <a href="profile.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&type=poster_thread&posterid=<mm:field name="posterid" />&postthreadid=<mm:write referid="postthreadid" />">
                <b><mm:field name="poster" /></b>  (<mm:field name="firstname" /> <mm:field name="lastname" />)</a><br />
            op <mm:field name="posttime"><mm:time format="<%= timeFormat %>" /></mm:field>
            </td>
            <td class="<mm:write referid="tdvar" />" align="right">
            <mm:remove referid="postingid" />
            <mm:remove referid="toid" />
            <mm:import id="toid"><mm:field name="posterid" /></mm:import>
            <mm:import id="postingid"><mm:field name="id" /></mm:import>
            <% // hh <a href="<mm:url page="newprivatemessage.jsp" referids="forumid,postareaid,postthreadid,postingid,toid" />"><img src="<mm:write referid="image_privatemsg" />"  border="0" /></a>
               // <a href="<mm:url page="posting.jsp" referids="forumid,postareaid,postthreadid,posterid,postingid" />"><img src="<mm:write referid="image_quotemsg" />"  border="0" /></a> 
            %>
             
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
                </mm:compare>
            </mm:field>

            </td>
            </tr>
            <tr>
            <td class="<mm:write referid="tdvar" />" valign="top" align="left">
            <a href="profile.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&type=poster_thread&posterid=<mm:field name="posterid" />&postthreadid=<mm:write referid="postthreadid" />">
                <mm:field name="avatar">
                <mm:compare value="-1" inverse="true">
                       <mm:node number="$_" notfound="skipbody">
                         <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                       </mm:node>
                </mm:compare>
                </mm:field>
            </a>
            <p />
            <mm:field name="guest">
            <mm:compare value="true" inverse="true">
            <% // hh      Level : <mm:field name="level" /><br /> 
            %>
            <mm:write referid="numberofposts" /> : <mm:field name="accountpostcount" /><br />
            <% // hh      Geslacht : <mm:field name="gender" /><br />
               //         Lokatie : <mm:field name="location" /><br /> 
            %>
            Lid sinds : <mm:field name="firstlogin"><mm:time format="<%= timeFormat %>" /></mm:field><br />
            Laatste bezoek : <mm:field name="lastseen"><mm:time format="<%= timeFormat %>" /> </mm:field><br />
            </mm:compare>
            <mm:compare value="true">
            </mm:compare>
            </mm:field>
            <br /><br /><br /><br /><br />
            </td>
            <td class="<mm:write referid="tdvar" />" valign="top" align="left">
            <mm:field name="edittime"><mm:compare value="-1" inverse="true">** Laatste keer aangepast op : <mm:field name="edittime"><mm:time format="<%= timeFormat %>" /></mm:field></mm:compare><p /></mm:field>
           
            <mm:node referid="postingid">

            <mm:formatter xslt="xslt/posting2xhtml.xslt">

            <mm:function referids="imagecontext,themeid" name="escapesmilies">
            <mm:write/>
            </mm:function>
            </mm:formatter>
            </mm:node>

            <br /><br /><br /><br /><br />
            </td>
            </tr>
          </mm:nodelistfunction>
</table>


<table cellpadding="0" cellspacing="0" style="margin-top : 2px;" width="95%">
    <tr><td align="left"><b>Pagina's
          <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,page">
            <mm:field name="navline" />
          </mm:nodefunction>
      </b>
    </td></tr>
</table>


<mm:compare referid="lastpage" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
   <a name="reply" />
  <tr><th colspan="3">snelle reactie</th></tr>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,page" />#reply" method="post" name="posting">
    <tr><th width="25%">Naam</th><td>

        <mm:compare referid="posterid" value="-1" inverse="true">
        <mm:node number="$posterid">
        <mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)
        <input name="poster" type="hidden" value="<mm:field name="account" />" >
        </mm:node>
        </mm:compare>
        <mm:compare referid="posterid" value="-1">
        <input name="poster" style="width: 100%" value="gast" >
        </mm:compare>

        </td></tr>
    <tr><th>Reactie <center><table width="100"><tr><th><%@ include file="includes/smilies.jsp" %></th></tr></table></center> </th><td><textarea name="body" rows="5" style="width: 100%"></textarea></td></tr>
    <tr><td colspan="3"><input type="hidden" name="action" value="postreply">
    <center><input type="submit" value="       plaats reactie"></center>
    </td></tr>
  </form>
</table>
</mm:compare>
<p />
<p />
<p />
<p />
</mm:cloud>
</html>
