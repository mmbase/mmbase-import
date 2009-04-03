<%--
  This template shows a existing folderitem.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:import externid="currentitem"/>
<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
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
  <img src="<mm:treefile write="true" page="/gfx/icon_portfolio.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<di:translate key="workspace.mydocuments" />" />
  <di:translate key="workspace.mydocuments" />
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
      <di:translate key="workspace.openitem" />:
      <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>
      <mm:compare referid="nodetype" value="attachments">
        <di:translate key="workspace.folderitemtypedocument" />
      </mm:compare>
      <mm:compare referid="nodetype" value="urls">
        <di:translate key="workspace.folderitemtypeurl" />
      </mm:compare>
      <mm:compare referid="nodetype" value="pages">
        <di:translate key="workspace.folderitemtypepage" />
      </mm:compare>
      <mm:compare referid="nodetype" value="chatlogs">
        <di:translate key="workspace.folderitemtypechatlog" />
      </mm:compare>
    </mm:node>
  </div>

  <div class="contentBodywit">
    <%-- Show the form --%>
    <form name="showitem" method="post" enctype='multipart/form-data' action="<mm:treefile page="/virtualclassroom/frontoffice/workspace/updateitems.jsp" objectlist="$includePath" referids="$referids"/>">
    <table class="Font">
    <mm:node number="$currentitem">
       <mm:fieldlist fields="title?,name?,description?,url?,text?,filename?,handle?">
         <tr>
           <td><mm:fieldinfo type="guiname"/></td>
           <td><mm:fieldinfo type="input"/></td> 
         </tr>
       </mm:fieldlist>
       <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
       <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
       <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
       <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
       <tr>
         <td colspan="2"> 
           <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.save" />" />
           <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.back" />" />
         </td>
       </tr>
    </mm:node>
    </table>
    </form>
    <table> 
      <mm:compare referid="typeof" value="4">
      <tr><td colspan="2">
      <p>
      <b><di:translate key="workspace.reactions" /></b>
      </p>
      </td></tr>
      <tr><td>
        <mm:relatednodes type="forummessages" orderby="number" directions="UP">
          <div style="border: solid black 1px; width: 500px; margin-bottom: 0.5em; padding: 0.25em 0.5em 0.25em 0.5em">
            <b><mm:field name="title"/> (<mm:field name="date"><mm:time format="d/M/yyyy"/></mm:field>)</b><br>
            <mm:field name="body" escape="p"/>
          </div>
        </mm:relatednodes>
      </td></tr>
        <form name="showitem" method="post"  action="<mm:treefile page="/virtualclassroom/frontoffice/workspace/showitem.jsp" objectlist="$includePath" referids="$referids"/>" >
        <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
        <input type="hidden" name="currentitem" value="<mm:write referid="currentitem"/>"/>
        <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
        <input type="hidden" name="add_message" value="1">
          <tr>
            <td>Titel</td>
            <td><input type="text" name="title" size="80"></td>
          </tr>
          <tr>
            <td>Tekst</td>
            <td><textarea name="body" cols="80" rows="10"></textarea></td>
          </tr>
          <tr>
            <td>
            <input type="submit" value="<di:translate key="workspace.react" />" class="formbutton">
            </td>
          </tr>
        </form>
      </mm:compare>
    </table>
    </div>
  </div>

</mm:cloud>
</mm:content>
