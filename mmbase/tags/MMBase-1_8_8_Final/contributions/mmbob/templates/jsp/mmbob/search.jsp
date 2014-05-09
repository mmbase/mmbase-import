<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="postareaid">-1</mm:import>
<mm:import externid="postthreadid">-1</mm:import>
<mm:import externid="searchkey" />
<mm:import externid="pathtype">search</mm:import>
<mm:import externid="posterid" id="profileid" />
<mm:import externid="searchmode">internal</mm:import>
<mm:import externid="searchareaid">-1</mm:import>


<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">
<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
<mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
<mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
<mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
<mm:include page="path.jsp?type=$pathtype" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>

<mm:compare referid="searchmode" value="database">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <form action="<mm:url page="search.jsp" referids="forumid" />" method="post">
    <mm:compare referid="postthreadid" value="-1" inverse="true">
    <tr>
        <th><mm:write referid="mlg.Search" /> in thread</th>
        <td>
        <input name="searchkey" size="20" value="<mm:write referid="searchkey" />">
        </td>
    </tr>
    </mm:compare>
    <mm:compare referid="postthreadid" value="-1">
    <tr>
        <th><mm:write referid="mlg.Search" /> key <mm:field name="postthreadid" />)</th>
        <td>
        <input name="searchkey" size="20" value="<mm:write referid="searchkey" />">
        </td>
    </td>
    </tr>
    </mm:compare>
    </form>
</table>

<mm:present referid="searchkey">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%">
    <tr><th><mm:write referid="mlg.Area" /></th><th><mm:write referid="mlg.Topic" /></th><th>Poster</th></tr>
    <mm:listcontainer path="forums,postareas,postthreads,postings" fields="forums.number,postareas.number,postthreads.number,postthreads.subject,postings.c_poster,postareas.name">
      <mm:constraint field="postings.body" operator="LIKE" value="%$searchkey%" />
       <mm:list max="10">
       <mm:first><mm:import id="resultfound">true</mm:import></mm:first>
        <tr>
          <td>
        <mm:field name="postareas.name" />
          </td>
          <td>
            <a href="<mm:url page="thread.jsp">
        <mm:param name="forumid"><mm:field name="forums.number" /></mm:param>
        <mm:param name="postareaid"><mm:field name="postareas.number" /></mm:param>
        <mm:param name="postthreadid"><mm:field name="postthreads.number" /></mm:param>
        <mm:param name="postingid"><mm:field name="postings.number" /></mm:param>
        </mm:url>#p<mm:field name="postings.number" />"><mm:field name="postthreads.subject" /></a>
          </td>
          <td>
        <mm:field name="postings.c_poster" />
          </td>
       </mm:list>
       <mm:present referid="resultfound">
       </mm:present>
    </mm:listcontainer>
</table>
</mm:present>
</mm:compare>

<mm:compare referid="searchmode" value="internal">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="50%">
    <form action="<mm:url page="search.jsp" referids="forumid,postthreadid,postareaid" />" method="post">
    <mm:compare referid="postthreadid" value="-1" inverse="true">
    <tr>
        <th colspan="1">Current thread</th>
        <th colspan="3"><mm:node referid="postthreadid"><mm:field name="subject" /></mm:node></th>
    </tr>
    <tr>
        <th><mm:write referid="mlg.Search" /></th>
        <th>
        in <select name="searchareaid">
           <mm:compare referid="postareaid" value="-1" inverse="true">
           <mm:node referid="postareaid">
           <option value="<mm:field name="number" />">Current
           </mm:node>
           </mm:compare>

           <option value="-1"><mm:write referid="mlg.All_Areas" />
                  <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
            <mm:field name="id">
            <option value="<mm:field name="id" />" <mm:compare referid2="searchareaid">selected</mm:compare>><mm:field name="name" />
            </mm:field>
          </mm:nodelistfunction>
           </select>
        </th>
        <td>
        <input name="searchkey" size="20" value="<mm:write referid="searchkey" />">
        </td>
        <td>
        <input type="submit" value="<mm:write referid="mlg.Search" />" />
        </td>
    </td>
    </tr>
    </mm:compare>
    <mm:compare referid="postthreadid" value="-1">
    <tr>
        <th><mm:write referid="mlg.Search" /> </th>
        <th>
        in <select name="searchareaid">
           <mm:compare referid="postareaid" value="-1" inverse="true">
           <mm:node referid="postareaid">
           <option value="<mm:field name="number" />">Current
           </mm:node>
           </mm:compare>
           <option value="-1"><mm:write referid="mlg.All_Areas" />
                  <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
            <mm:field name="id">
            <option value="<mm:field name="id" />" <mm:compare referid2="searchareaid">selected</mm:compare>><mm:field name="name" />
            </mm:field>
          </mm:nodelistfunction>
           </select>
        </th>
        <td>
        <input name="searchkey" size="20" value="<mm:write referid="searchkey" />">
        </td>
        <td>
        <input type="submit" value="<mm:write referid="mlg.Search" />" />
        </td>
    </td>
    </tr>
    </mm:compare>
    </form>
</table>


<mm:present referid="searchkey">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%">
    <mm:import id="page">1</mm:import>
    <mm:import id="pagesize">10</mm:import>
    <mm:nodelistfunction set="mmbob" name="searchPostings" referids="forumid,searchareaid,postthreadid@searchpostthreadid,searchkey,posterid,page,pagesize">
           <mm:first>
        <tr><th><mm:write referid="mlg.Area" /></th><th><mm:write referid="mlg.Topic" /></th><th>Poster</th></tr>
        <mm:import id="resultfound">true</mm:import>
       </mm:first>
        <tr>
          <td>
        <mm:field name="postareaname" />
          </td>
          <td>
            <a href="<mm:url page="thread.jsp" referids="forumid">
        <mm:param name="postareaid"><mm:field name="postareaid" /></mm:param>
        <mm:param name="postthreadid"><mm:field name="postthreadid" /></mm:param>
        <mm:param name="postingid"><mm:field name="postingid" /></mm:param>
        </mm:url>#p<mm:field name="postingid" />"><mm:field name="subject" /></a>
          </td>
          <td>
        <mm:field name="poster" />
          </td>
            </tr>
    </mm:nodelistfunction>
       <mm:notpresent referid="resultfound">
       <th><mm:write referid="mlg.warning" /></th>
       <tr><td colspan="3"><b><mm:write referid="mlg.NoResultsFound" /></b></td></tr>
       </mm:notpresent>
</table>
</mm:present>
</mm:compare>


</div>
<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>
