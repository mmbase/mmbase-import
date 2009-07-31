<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblog/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import externid="searchchange" />
<mm:present referid="searchchange">
	<mm:write referid="searchchange" session="searchmode" />
</mm:present>

<mm:import externid="searchmode" from="session">basic</mm:import>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<mm:node referid="weblogid" notfound="skip">
<tr>
	<th colspan="4">
<mm:compare referid="searchmode" value="basic"><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="searchchange">advanced</mm:param></mm:url>"><img src="images/mmbase-search.gif" border="0" align="left"></a></mm:compare><mm:compare referid="searchmode" value="advanced"><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="searchchange">basic</mm:param></mm:url>"><img src="images/mmbase-search.gif" border="0" align="left"></a></mm:compare>
	Entries for the <mm:field name="name" /> project 
        </tr>
	</th>
</tr>
</mm:node>
<mm:compare referid="searchmode" value="basic">
<tr>
	<mm:listcontainer path="weblogs,weblogrel,weblogentries" fields="weblogs.name,weblogrel.state,weblogentries.number">
	<mm:constraint field="weblogs.number" operator="EQUAL" value="$weblogid" />
	    <mm:list>
            <tr>
	    <td><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="sub">entry</mm:param><mm:param name="weblogentryid"><mm:field name="weblogentries.number" /></mm:param><mm:param name="weblogrelid"><mm:field name="weblogrel.number" /></mm:param></mm:url>"><mm:field name="weblogentries.title" /></a></td>
	    <td><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="sub">entry</mm:param><mm:param name="weblogentryid"><mm:field name="weblogentries.number" /></mm:param><mm:param name="weblogrelid"><mm:field name="weblogrel.number" /></mm:param></mm:url>"><mm:field name="weblogrel.state" /></a></td>
	    <td><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="sub">entry</mm:param><mm:param name="weblogentryid"><mm:field name="weblogentries.number" /></mm:param><mm:param name="weblogrelid"><mm:field name="weblogrel.number" /></mm:param></mm:url>"><mm:field name="weblogentries.postdate"><mm:time format="dd MMMM yyyy" /></mm:field></a></td>
	    <td><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="sub">entry</mm:param><mm:param name="weblogentryid"><mm:field name="weblogentries.number" /></mm:param><mm:param name="weblogrelid"><mm:field name="weblogrel.number" /></mm:param></mm:url>"><img src="images/mmbase-right.gif" border="0" /></a></td>
            </tr>
	    </mm:list>
	</mm:listcontainer>
        <tr>
	   <td colspan="3">&nbsp;</td>
	   <td width="24"><a href="<mm:url page="index.jsp" referids="main,weblogid"><mm:param name="sub">newentry</mm:param></mm:url>"><img src="images/mmbase-new.gif" border="0"></a></td>
        </tr>
</mm:compare>
<mm:compare referid="searchmode" value="advanced">
</mm:compare>
</table>
