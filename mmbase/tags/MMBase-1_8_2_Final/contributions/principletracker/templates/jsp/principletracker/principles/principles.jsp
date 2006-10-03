<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principles/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import externid="searchchange" />
<mm:present referid="searchchange">
	<mm:write referid="searchchange" session="searchmode" />
</mm:present>

<mm:import externid="searchmode" from="session">basic</mm:import>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<mm:node referid="principleset" notfound="skip">
<tr>
	<th colspan="3">
<mm:compare referid="searchmode" value="basic"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="searchchange">advanced</mm:param></mm:url>"><img src="images/mmbase-search.gif" border="0" align="left"></a></mm:compare><mm:compare referid="searchmode" value="advanced"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="searchchange">basic</mm:param></mm:url>"><img src="images/mmbase-search.gif" border="0" align="left"></a></mm:compare>
	Principles for the <mm:field name="name" /> project 
        </tr>
	</th>
</tr>
</mm:node>
<mm:compare referid="searchmode" value="basic">
<tr>
	<mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state,principlesets.number">
	<mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />
	<mm:constraint field="principlerel.state" operator="EQUAL" value="active" />
	    <mm:list orderby="principle.principlenumber">
            <tr>
	    <td><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><mm:field name="principle.principlenumber" /></a></td>
	    <td align="left"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><mm:field name="principle.name" /></a></td>
	    <td width="24"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><img src="images/mmbase-right.gif" border="0" /></a></td>
            </tr>
	    </mm:list>
	</mm:listcontainer>
        <tr>
	   <td colspan="2">&nbsp;</td>
	   <td width="24"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">newprinciple</mm:param></mm:url>"><img src="images/mmbase-new.gif" border="0"></a></td>
        </tr>
</mm:compare>
<mm:compare referid="searchmode" value="advanced">
<tr>
	<td colspan="3">advanced</td>
</tr>
<tr>
	<mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state,principlesets.number">
	<mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />
	<mm:constraint field="principlerel.state" operator="EQUAL" value="active" />
	    <mm:list orderby="principle.principlenumber">
            <tr>
	    <td><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><mm:field name="principle.principlenumber" /></a></td>
	    <td align="left"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><mm:field name="principle.name" /></a></td>
	    <td width="24"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">principle</mm:param><mm:param name="principleid"><mm:field name="principle.number" /></mm:param><mm:param name="principlerelid"><mm:field name="principlerel.number" /></mm:param></mm:url>"><img src="images/mmbase-right.gif" border="0" /></a></td>
            </tr>
	    </mm:list>
	</mm:listcontainer>
        <tr>
	   <td colspan="2">&nbsp;</td>
	   <td width="24"><a href="<mm:url page="index.jsp" referids="main,principleset"><mm:param name="sub">newprinciple</mm:param></mm:url>"><img src="images/mmbase-new.gif" border="0"></a></td>
        </tr>
</mm:compare>
</table>
