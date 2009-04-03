<%--
  This template adds a document to a folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:import externid="processupload">false</mm:import>
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="workspace.adddocument" /></title>
    </mm:param>
  </mm:treeinclude>



  <%-- check whether there's information to be uploaded
       if not, display the form --%>
  <mm:compare referid="processupload" value="false">
    
    <%-- normal imports --%>
    <mm:import externid="currentfolder"/>
    <mm:import externid="callerpage"/>
    <mm:import externid="typeof"/>
    <mm:node number="$currentfolder" id="mycurrentfolder"/>


    <div class="rows">

    <div class="navigationbar">
      <div class="titlebar">
        <mm:compare referid="typeof" value="1">
          <mm:import id="titletext"><di:translate key="workspace.mydocuments" /></mm:import>
        </mm:compare>
        <mm:compare referid="typeof" value="2">
          <mm:import id="titletext"><di:translate key="workspace.shareddocuments" /></mm:import>
        </mm:compare>
        <mm:compare referid="typeof" value="3">
          <mm:import id="titletext"><di:translate key="workspace.workgroupdocuments" /></mm:import>
        </mm:compare>
	     <mm:compare referid="typeof" value="4">
    	    <mm:import id="titletext"><di:translate key="workspace.projectgroupdocuments" /></mm:import>
	    </mm:compare>


        <img src="<mm:treefile write="true" page="/gfx/icon_portfolio.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.mydocuments" />" alt="<di:translate key="workspace.mydocuments" />" />
        <mm:write referid="titletext"/>
      </div>
    </div>

    <div class="folders">
      <div class="folderHeader"></div>
      <div class="folderBody"></div>
    </div>

    <div class="mainContent">
      <div class="contentHeader">
        <di:translate key="workspace.adddocument" />
      </div>
      <div class="contentBodywit">

      <%-- create a html form  with method post and enctype multipart   --%>
      <form name="adddocument" method="post" enctype="multipart/form-data" action="<mm:treefile page="/workspace/adddocument.jsp" objectlist="$includePath" referids="$referids"/>">
        <%-- parameter to indicate theres info to be uploaded --%>
        <input type="hidden" name="processupload" value="true"/>
	<input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>">
        <%-- display current folder name --%>
        <mm:node referid="mycurrentfolder">
          <mm:field name="name"/><br/>
        </mm:node>
        
        <table class="Font">
          <mm:fieldlist nodetype="attachments" fields="title,description,handle">
            <tr>
              <td><mm:fieldinfo type="guiname"/></td>
              <td><mm:fieldinfo type="input"/></td>
            </tr>
          </mm:fieldlist>
        </table>

        <%-- a few hidden fields which are used in the next page --%>
        <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
        <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
        <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
        
        <%-- button to upload the file --%>
        <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.create" />" />
        <%-- button to go back and upload nothing --%>
        <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.back" />" />
      </form>
    </div>
    </div>
  
  </mm:compare>

  <%-- the form has been submitted --%>
  <mm:compare referid="processupload" value="true">

    <%-- Get fields from multipart form --%>
    <mm:import externid="_handle_name" from="multipart"/>
    <mm:import externid="_handle_type" from="multipart"/>
    <mm:import externid="_handle_size" from="multipart"/>
    <mm:import externid="_title" from="multipart"/>
    <mm:import externid="_description" from="multipart"/>
    <mm:import externid="action1" from="multipart"/>
    <mm:import externid="action2" from="multipart"/>
    <mm:import externid="currentfolder" from="multipart" />
    <mm:import externid="callerpage" from="multipart"/>
    <mm:import externid="typeof" from="multipart"/>

    <%-- detect double clicks on submit form and redirect off the page --%>
    <mm:import externid="detectclicks" from="parameters"/>
    <mm:import externid="oldclicks" from="session"/>
    <mm:present referid="detectclicks">
	<mm:compare referid="detectclicks" value="$oldclicks">
	    <mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>
        </mm:compare>
	<mm:write session="oldclicks" referid="detectclicks"/>
    </mm:present>


    <%-- check whether terug-button has been pressed,
         if true, go back to the previous page and don't upload anything --%>
    <mm:present referid="action2">
      <mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>
    </mm:present>

    <mm:node number="$currentfolder" id="mycurrentfolder"/>
    
    <%-- create attachment node --%>
    <mm:createnode type="attachments" id="myattachements">
      <mm:setfield name="title"><mm:write referid="_title"/></mm:setfield>
      <mm:setfield name="description"><mm:write referid="_description"/></mm:setfield>
      <mm:setfield name="filename"><mm:write referid="_handle_name"/></mm:setfield>
      <mm:setfield name="mimetype"><mm:write referid="_handle_type"/></mm:setfield>
      <mm:setfield name="size"><mm:write referid="_handle_size"/></mm:setfield>
      <mm:fieldlist fields="handle">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>

      <%-- set upload time --%>
      <% long currentDate = System.currentTimeMillis() / 1000; %>
      <mm:setfield name="date"><%=currentDate%></mm:setfield>
    </mm:createnode>

    <mm:import id="docId" jspvar="docId"><mm:write referid="myattachements" /></mm:import>    
    <di:event eventtype="add_document" eventvalue="<%= docId %>" note="add document" />

    <%-- related uploaded attachment to the current folder --%>
    <mm:createrelation role="related" source="mycurrentfolder" destination="myattachements"/>

    <%-- go back to the previous page --%>
    <mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>
  </mm:compare>  

  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
