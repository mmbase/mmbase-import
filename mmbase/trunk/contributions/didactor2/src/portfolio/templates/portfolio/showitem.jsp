<%--
  This template shows a existing folderitem.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="none" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="OPENITEM" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentitem"/>
<mm:import externid="currentfolder"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:notpresent referid="currentitem">
    <mm:import externid="currentitem" from="multipart" required="true"/>
    <mm:import externid="currentfolder" from="multipart" required="true"/>
    <mm:import externid="typeof" from="multipart"/>
    <mm:import externid="action1" from="multipart"/>
    <mm:import externid="action2" from="multipart"/>


</mm:notpresent>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
    <mm:redirect referids="$referids,currentfolder,typeof" page="index.jsp"/>
</mm:present>

<mm:notpresent referid="action2">

<%-- check if this is the current user's portfolio --%>
<mm:import id="mayeditthis">false</mm:import>
<mm:list nodes="$user" path="people,portfolios,folders" constraints="folders.number=$currentfolder">
    <mm:import id="mayeditthis" reset="true">true</mm:import>
</mm:list>

<mm:compare referid="mayeditthis" value="true">
<%-- edit the content of the object --%>
<mm:present referid="action1">
    <mm:node number="$currentitem">
      <mm:fieldlist fields="title?,name?,description?,url?,text?,filename?,handle?">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
    </mm:node>
</mm:present>
</mm:compare>

<mm:import externid="add_message"/>
<mm:present referid="add_message">
    <mm:import externid="title" reset="true"/>
    <mm:import externid="body" reset="true"/>
    <mm:isnotempty referid="title"><mm:isnotempty referid="body">
        <mm:createnode type="forummessages" id="newmessage">
            <mm:setfield name="title"><mm:write referid="title"/></mm:setfield>
            <mm:setfield name="body"><mm:write referid="body"/></mm:setfield>
            <mm:setfield name="date"><%= System.currentTimeMillis()/1000 %></mm:setfield>
        </mm:createnode>
        <mm:createrelation role="related" destination="currentitem" source="newmessage"/>
    </mm:isnotempty></mm:isnotempty>
</mm:present>

<div class="rows">


<div class="navigationbar">
  <div class="titlebar">
  <img src="<mm:treefile write="true" page="/gfx/icon_portfolio.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="MYDOCUMENTS" />" />
  <fmt:message key="MYDOCUMENTS" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <mm:node number="$currentitem">
      <fmt:message key="OPENITEM" />:
      <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>
      <mm:compare referid="nodetype" value="attachments">
        <fmt:message key="FOLDERITEMTYPEDOCUMENT" />
      </mm:compare>
      <mm:compare referid="nodetype" value="urls">
        <fmt:message key="FOLDERITEMTYPEURL" />
      </mm:compare>
      <mm:compare referid="nodetype" value="pages">
        <fmt:message key="FOLDERITEMTYPEPAGE" />
      </mm:compare>
      <mm:compare referid="nodetype" value="chatlogs">
        <fmt:message key="FOLDERITEMTYPECHATLOG" />
      </mm:compare>
    </mm:node>
  </div>

  <div class="contentBodywit">
  <br><br><br>
      <table class="Font">
    <%-- Show the form --%>
    <%-- 
        ***
        
        DO NOT USE the referid="..." construct here - MMBase gets 
        completely confused if it's more than a couple of bytes long and
        you're trying to post a multipart/form-data encoded form

        If you need to pass parameters use hidden fields, as below
        **
    --%>
    <form name="showitem" method="post" action="<mm:treefile page="/portfolio/showitem.jsp" objectlist="$includePath"/>" enctype="multipart/form-data">
    <mm:present referid="provider">
        <input type="hidden" name="provider" value="<mm:write referid="provider"/>">
    </mm:present>
      <mm:present referid="class">
        <input type="hidden" name="class" value="<mm:write referid="class"/>">
    </mm:present>
    <mm:present referid="education">
        <input type="hidden" name="education" value="<mm:write referid="education"/>">
    </mm:present>
    
    
      <mm:node number="$currentitem">
        <mm:fieldlist fields="title?,name?,description?,url?,text?,filename?,handle?">
          <tr>
          <td><mm:fieldinfo type="guiname"/></td>
          <td>
            <mm:compare referid="mayeditthis" value="true"><mm:fieldinfo type="input"/></mm:compare>
            <mm:compare referid="mayeditthis" value="false"><mm:fieldinfo type="guivalue"/></mm:compare>
          </td>
          </tr>
        </mm:fieldlist>
	
	  </mm:node>

      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <tr><td>
      <mm:compare referid="mayeditthis" value="true"><input class="formbutton" type="submit" name="action1" value="<fmt:message key="SAVE" />" /></mm:compare>
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />" />
      </td></tr>
    </form>

    <tr><td colspan="2">
    <br><br>
    <p>
    <b><fmt:message key="REACTIONS"/></b>
    </p>
    </td></tr>
    <tr><td></td><td>
    <mm:node number="$currentitem">
        <mm:relatednodes type="forummessages" orderby="number" directions="UP">
          <div style="border: solid black 1px; width: 500px; margin-bottom: 0.5em; padding: 0.25em 0.5em 0.25em 0.5em">
            <b><mm:field name="title"/> (<mm:field name="date"><mm:time format="d/M/yyyy"/></mm:field>)</b><br>
            <mm:field name="body" escape="p"/>
          </div>
        </mm:relatednodes>
    </td></tr>
        <form name="showitem" method="post" action="<mm:treefile page="/portfolio/showitem.jsp" objectlist="$includePath" referids="$referids"/>" >
      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
 
        
            <input type="hidden" name="add_message" value="1">
            <tr><td>Titel</td>
            <td><input type="text" name="title" size="80"></td>
            </tr>
            <tr>
            <td>Tekst</td>
            <td><textarea name="body" cols="80" rows="10"></textarea></td>
            </tr>
            <tr>
            <td>
            <input type="submit" value="<fmt:message key="REACT"/>" class="formbutton">
            </td></tr>
        </form>
    </mm:node>
    </table>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:notpresent>
</fmt:bundle>
</mm:cloud>
</mm:content>
