<%--
  This template adds a document to a folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:import externid="processupload">false</mm:import>
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="portfolio.adddocument" /></title>
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

<mm:import externid="contact">-1</mm:import>
<mm:compare referid="contact" value="-1" inverse="true">
  <mm:import id="user" reset="true"><mm:write referid="contact"/></mm:import>
</mm:compare>

<div class="rows">

<div class="navigationbar">
<div class="titlebar">
<img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="portfolio.portfolio" />" alt="<di:translate key="portfolio.portfolio" />"/>
<di:translate key="portfolio.portfolio" />
</div>
</div>

<div class="folders">

<div class="folderHeader">
<di:translate key="portfolio.portfolio" />
</div>
<div class="folderBody"></div>
</div>

    <div class="mainContent">
      <div class="contentHeader">
        <di:translate key="portfolio.adddocument" />
      </div>
      <div class="contentBodywit">
      <br><br><br>

      <%-- create a html form  with method post and enctype multipart   --%>
      <form name="adddocument" method="post" enctype="multipart/form-data" action="<mm:treefile page="/portfolio/adddocument.jsp" objectlist="$includePath" referids="$referids"/>">
        <%-- parameter to indicate theres info to be uploaded --%>
        <input type="hidden" name="processupload" value="true"/>
	<input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>">
        <%-- display current folder name --%>
        <mm:node referid="mycurrentfolder">
          <mm:field name="name"/><br/>
        </mm:node>
        
        <table class="Font">
          <mm:fieldlist nodetype="attachments" fields="title,handle,description">
            <tr>
              <td><mm:fieldinfo type="guiname"/></td>
              <td><mm:fieldinfo type="input"/></td>
            </tr>
          </mm:fieldlist>
        <tr>
            <td>Leesrechten</td>
            <td><select name="_readrights">
                <option value="0" >Niet zichtbaar</option>
                <option value="1" >Zichtbaar voor studenten uit mijn klassen</option>
                <option value="2" >Zichtbaar voor mijn docenten</option>
                <option value="3" >Zichtbaar voor iedereen.</option>
                <option value="4" >Zichtbaar voor niet-ingelogde (anonieme) gebruikers.</option>
            </select>
            </td>
        </tr>
        <tr>
            <td>Reacties</td>
            <td><select name="_allowreactions">
                <option value="0" >Geen reacties toestaan</option>
                <option value="1" >Reacties toestaan</option>
                </select>
            </td>
        </tr>
       </table>

        <%-- a few hidden fields which are used in the next page --%>
        <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
        <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
        <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
        <mm:compare referid="contact" value="-1" inverse="true">
          <input type="hidden" name="contact" value="<mm:write referid="contact"/>"/>
        </mm:compare>
        
        <%-- button to upload the file --%>
        <input class="formbutton" type="submit" name="action1" value="<di:translate key="portfolio.create" />" />
        <%-- button to go back and upload nothing --%>
        <input class="formbutton" type="submit" name="action2" value="<di:translate key="portfolio.back" />" />
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
    <mm:import externid="contact" from="multipart">-1</mm:import>

    <%-- detect double clicks on submit form and redirect off the page --%>
    <mm:import externid="detectclicks" from="parameters"/>
    <mm:import externid="oldclicks" from="session"/>
    <mm:present referid="detectclicks">
	<mm:compare referid="detectclicks" value="$oldclicks">
	    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
        </mm:compare>
	<mm:write session="oldclicks" referid="detectclicks"/>
    </mm:present>


    <%-- check whether terug-button has been pressed,
         if true, go back to the previous page and don't upload anything --%>
    <mm:present referid="action2">
      <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
    </mm:present>

    <mm:node number="$currentfolder" id="mycurrentfolder"/>
    
    <%-- create attachment node --%>
    <mm:createnode type="attachments" id="currentitem">
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

    <%-- create permissions --%>
    <mm:createnode type="portfoliopermissions" id="permissions">
         <%@include file="notifyteachers.jsp"%>
        <mm:fieldlist fields="readrights,allowreactions">
            <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
    </mm:createnode>

    <mm:createrelation source="currentitem" destination="permissions" role="related"/>


    
    <%-- related uploaded attachment to the current folder --%>
    <mm:createrelation role="related" source="mycurrentfolder" destination="currentitem"/>

    <%-- go back to the previous page --%>
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
  </mm:compare>  

  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
