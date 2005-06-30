<%--
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="MYDOCUMENTS" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentfolder">-1</mm:import>

<%-- Determine if my documents or shared documents is started --%>
<mm:import externid="typeof"/>

<%-- jsp started as my documents --%>
<mm:compare referid="typeof" value="1">
  <mm:listnodes type="people" constraints="number=$user" id="myuser" >
    <mm:relatednodes type="workspaces" id="myworkspaces">
      <mm:relatednodes type="folders" orderby="name" id="myfolders">
        <mm:first>
          <mm:compare referid="currentfolder" value="-1">

            <mm:remove referid="currentfolder"/>
            <mm:import id="currentfolder"><mm:field name="number"/></mm:import>

          </mm:compare>
        </mm:first>
      </mm:relatednodes>
    </mm:relatednodes>
  </mm:listnodes>
</mm:compare>

<%-- jsp started as shared documents --%>
<mm:compare referid="typeof" value="2">
  <mm:listnodes constraints="number=$class" type="classes" id="myuser" >
    <mm:relatednodes type="workspaces" id="myworkspaces">
      <mm:relatednodes type="folders" orderby="name" id="myfolders">
        <mm:first>
          <mm:compare referid="currentfolder" value="-1">

            <mm:remove referid="currentfolder"/>
            <mm:import id="currentfolder"><mm:field name="number"/></mm:import>

          </mm:compare>
        </mm:first>
      </mm:relatednodes>
    </mm:relatednodes>
  </mm:listnodes>
</mm:compare>

<%-- jsp started as workgroup documents --%>
<mm:compare referid="typeof" value="3">
  <mm:node number="$user" notfound="skip">
    <!-- listing workgroups for user -->
    <mm:relatednodes type="workgroups" id="myuser">
        <!-- found workgroup -->
        <mm:relatednodes type="workspaces" id="myworkspaces">
          <mm:relatednodes type="folders" orderby="name" id="myfolders">
            <mm:first>
              <mm:compare referid="currentfolder" value="-1">

                <mm:remove referid="currentfolder"/>
                <mm:import id="currentfolder"><mm:field name="number"/></mm:import>

              </mm:compare>
            </mm:first>
          </mm:relatednodes>
        </mm:relatednodes>
    </mm:relatednodes>
  </mm:node>
</mm:compare>



<mm:import externid="action_delete.x" id="action_delete" from="parameters"/>
<mm:import externid="action_move.x" id="action_move" from="parameters"/>
<mm:import externid="ids" vartype="List"/>

<mm:present referid="action_delete">
    <mm:redirect page="/workspace/deleteitems.jsp" referids="$referids,currentfolder,ids,typeof">
	<mm:param name="callerpage">/workspace/index.jsp</mm:param>
    </mm:redirect>
</mm:present>
<mm:present referid="action_move">
<mm:import id="currenttime"><%= System.currentTimeMillis() %></mm:import>
<mm:redirect page="/workspace/moveitems.jsp" referids="$referids,currentfolder,ids,typeof,currenttime">
    <mm:param name="callerpage">/workspace/index.jsp</mm:param>
</mm:redirect>
</mm:present>


<div class="rows">

<div class="navigationbar">
<div class="titlebar">
<%-- determine if the context is my documents or shared documents --%>
<mm:compare referid="typeof" value="1">
  <img src="<mm:treefile write="true" page="/gfx/icon_mydocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="MYDOCUMENTS" />"/>
      <fmt:message key="MYDOCUMENTS" />
</mm:compare>
<mm:compare referid="typeof" value="2">
  <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="SHAREDDOCUMENTS" />"/>
      <fmt:message key="SHAREDDOCUMENTS" />
</mm:compare>
<mm:compare referid="typeof" value="3">
  <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="SHAREDDOCUMENTS" />"/>
      <fmt:message key="WORKGROUPDOCUMENTS" />
</mm:compare>

</div>
</div>

<div class="folders">

<div class="folderHeader">
    <fmt:message key="FOLDERS" />
</div>

<div class="folderBody">

<%-- determine the folders in the used context (my documents or shared documents --%>
<mm:listnodes referid="myuser" >
   <!-- listing workspaces for '<mm:field name="name"/>' -->
  <mm:relatednodes type="workspaces" id="workspace">

    <b><mm:field name="name"/></b><br>

<a href="<mm:treefile page="/workspace/createfolder.jsp" objectlist="$includePath" referids="$referids">
	   <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
	   <mm:param name="workspace"><mm:write referid="workspace"/></mm:param>
           <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	   <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	 </mm:treefile>">
  <img src="<mm:treefile page="/workspace/gfx/map maken.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="CREATEFOLDER" />" /></a>

<mm:isgreaterthan referid="currentfolder" value="0">
  <a href="<mm:treefile page="/workspace/changefolder.jsp" objectlist="$includePath" referids="$referids">
	     <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		 <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	     <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	       </mm:treefile>">

    <img src="<mm:treefile page="/workspace/gfx/map hernoemen.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="RENAMEFOLDER" />" /></a>

  <a href="<mm:treefile page="/workspace/deletefolder.jsp" objectlist="$includePath" referids="$referids">
	     <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
	     <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	     <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	   </mm:treefile>">
    <img src="<mm:treefile page="/workspace/gfx/verwijder map.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="DELETEFOLDER" />" /></a>
</mm:isgreaterthan>
    <br clear="all"/>

    <mm:relatednodes type="folders">

      <mm:import id="currentnumber"><mm:field name="number"/></mm:import>

      <%-- folder is open --%>
      <mm:compare referid="currentfolder" referid2="currentnumber">
	<img src="<mm:treefile page="/workspace/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDEROPENED" />" />
      </mm:compare>

      <%-- folder is closed --%>
      <mm:compare referid="currentfolder" referid2="currentnumber" inverse="true">
	<img src="<mm:treefile page="/workspace/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDERCLOSED" />" />
      </mm:compare>

      <a href="<mm:treefile page="/workspace/index.jsp" objectlist="$includePath" referids="$referids">
			 <mm:param name="currentfolder"><mm:field name="number" /></mm:param>
		 <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
		       </mm:treefile>">
			<mm:field name="name" />
      </a><br />

    </mm:relatednodes>
    <br/>
  </mm:relatednodes>
</mm:listnodes>

</div>

</div>

<div class="mainContent">

<form action="<mm:treefile page="/workspace/index.jsp" objectlist="$includePath" referids="$referids,typeof"/>" method="POST">
    <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>">

  <div class="contentHeader">

    <mm:node number="$currentfolder" notfound="skip">
      <mm:field name="name"/>
    </mm:node>

  </div>

  <div class="contentSubHeader">

    <mm:isgreaterthan referid="currentfolder" value="0">
      <a href="<mm:treefile page="/workspace/adddocument.jsp" objectlist="$includePath" referids="$referids">
	  	            <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		            <mm:param name="callerpage">/workspace/index.jsp</mm:param>
                    <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
		     </mm:treefile>">
        <img src="<mm:treefile page="/workspace/gfx/document plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="ADDDOCUMENT" />" /></a>

      <a href="<mm:treefile page="/workspace/addurl.jsp" objectlist="$includePath" referids="$referids">
		            <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		            <mm:param name="callerpage">/workspace/index.jsp</mm:param>
                    <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
		     </mm:treefile>">
        <img src="<mm:treefile page="/workspace/gfx/bron plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="ADDSOURCE" />" /></a>

	
        <input type="image" name="action_move" src="<mm:treefile page="/workspace/gfx/verplaats geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="MOVESELECTED" />" />

        <input type="image" name="action_delete" src="<mm:treefile page="/workspace/gfx/verwijder geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="DELETESELECTED" />"/>
    </mm:isgreaterthan>

  </div>

  <div class="contentBody">

    <mm:import id="gfx_attachment"><mm:treefile page="/workspace/gfx/mijn documenten.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_url"><mm:treefile page="/workspace/gfx/bronnen.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_page"><mm:treefile page="/workspace/gfx/pagina.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_chatlog"><mm:treefile page="/workspace/gfx/chatverslag.gif" objectlist="$includePath" referids="$referids" /></mm:import>

    <mm:node number="$currentfolder" notfound="skip">

      <mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

      <%-- Show also the nodes below in the table --%>
      <mm:relatednodes type="attachments" id="myattachments">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="urls" id="myurls">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="pages" id="mypages">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="chatlogs" id="mychatlogs">
        <mm:remove referid="objectnumber"/>
	    <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
	    <%
	      linkedlist.add( objectnumber );
	    %>
      </mm:relatednodes>


      <mm:listnodescontainer type="object">
        <mm:constraint field="number" referid="linkedlist" operator="IN"/>

        <di:table maxitems="10">

          <di:row>
            <di:headercell><input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"></input></di:headercell>
            <di:headercell><fmt:message key="TYPE" /></di:headercell>
            <di:headercell><fmt:message key="TITLE" /></di:headercell>
            <di:headercell><fmt:message key="DESCRIPTION" /></di:headercell>
            <di:headercell><fmt:message key="FILENAME" /></di:headercell>
            <di:headercell><fmt:message key="DATE" /></di:headercell>
          </di:row>

          <mm:listnodes>

            <di:row>
              <di:cell><input type="checkbox" name="ids" value="<mm:field name="number"/>"></input></di:cell>

              <mm:remove referid="link"/>
              <mm:import id="link"><a href="<mm:treefile page="/workspace/showitem.jsp" objectlist="$includePath" referids="$referids">
                                  <mm:param name="currentitem"><mm:field name="number"/></mm:param>
                                  <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		                          <mm:param name="callerpage">/workspace/index.jsp</mm:param>
                                  <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                                </mm:treefile>">
              </mm:import>

              <mm:remove referid="objecttype"/>
              <mm:import id="objecttype"><mm:nodeinfo type="type"/></mm:import>
              <mm:compare referid="objecttype" value="attachments">
                <di:cell><img src="<mm:write referid="gfx_attachment"/>" alt="<fmt:message key="FOLDERITEMTYPEDOCUMENT" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="title" /></a></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
                <di:cell><mm:field name="filename" /></di:cell>
                <di:cell><mm:field name="date"><mm:time format="d/M/yyyy"/></mm:field></di:cell>
              </mm:compare>
              <mm:compare referid="objecttype" value="urls">
                <mm:import id="urllink" jspvar="linkText"><mm:field name="url"/></mm:import>
			    <%
			      if ( linkText.indexOf( "http://" ) == -1 ) {
			    %>
			      <mm:remove referid="urllink"/>
			  	  <mm:import id="urllink">http://<mm:field name="url"/></mm:import>
			  	<%
			  	  }
			  	%>
                <di:cell><img src="<mm:write referid="gfx_url"/>" alt="<fmt:message key="FOLDERITEMTYPEURL" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
                <di:cell><a href="<mm:write referid="urllink" />" target="unknownframe"><mm:field name="url"/></a></di:cell>
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
              </mm:compare>
              <mm:compare referid="objecttype" value="pages">
                <di:cell><img src="<mm:write referid="gfx_page"/>" alt="<fmt:message key="FOLDERITEMTYPEPAGE" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                <di:cell><mm:field name="text" /></di:cell>
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
              </mm:compare>
              <mm:compare referid="objecttype" value="chatlogs">
                <di:cell><img src="<mm:write referid="gfx_chatlog"/>" alt="<fmt:message key="FOLDERITEMTYPECHATLOG" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><fmt:message key="FOLDERITEMTYPECHATLOG" /><mm:field name="number"/></a></di:cell>
                <di:cell>&nbsp;</di:cell>
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
                <di:cell><mm:field name="date"></mm:field></di:cell> <!-- TODO show correct date -->
              </mm:compare>

  		    </di:row>

  		  </mm:listnodes>

        </di:table>

      </mm:listnodescontainer>

    </mm:node>

  </div>
</div>
</div>
<script>

      function selectAllClicked(frm, newState) {
	  if (frm.elements['ids'].length) {
	    for(var count =0; count < frm.elements['ids'].length; count++ ) {
		var box = frm.elements['ids'][count];
		box.checked=newState;
	    }
	  }
	  else {
	      frm.elements['ids'].checked=newState;
	  }
      }

</script>


<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
