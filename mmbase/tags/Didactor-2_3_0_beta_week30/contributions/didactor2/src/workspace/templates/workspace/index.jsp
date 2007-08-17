<%--
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud rank="basic user">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="workspace.mydocuments" /></title>
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
    <mm:relatednodes type="workgroups" constraints="workgroups.protected=1" id="myuser" orderby="name">
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

<%-- jsp started as projectgroup documents --%>
<mm:present referid="workgroup">
<mm:compare referid="typeof" value="4">
  <mm:node number="$user" notfound="skip">
    <!-- listing workgroups for user -->
    <mm:relatednodes type="workgroups" constraints="workgroups.number=$workgroup and workgroups.protected=0" id="myuser" orderby="name">
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
</mm:present>


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
  <img src="<mm:treefile write="true" page="/gfx/icon_mydocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.mydocuments" />" alt="<di:translate key="workspace.mydocuments" />"/>
      <di:translate key="workspace.mydocuments" />
</mm:compare>
<mm:compare referid="typeof" value="2">
  <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.shareddocuments" />" alt="<di:translate key="workspace.shareddocuments" />"/>
      <di:translate key="workspace.shareddocuments" />
</mm:compare>
<mm:compare referid="typeof" value="3">
  <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.shareddocuments" />" alt="<di:translate key="workspace.shareddocuments" />"/>
      <di:translate key="workspace.workgroupdocuments" />
</mm:compare>

</div>
</div>

<div class="folders">

<div class="folderHeader">
    <di:translate key="workspace.folders" />
</div>

<div class="folderBody">

<%-- determine the folders in the used context (my documents or shared documents --%>

<mm:present referid="workgroup">
<mm:compare referid="typeof" value="4">
  <a href="<mm:treefile page="/projectgroup/index.jsp" objectlist="$includePath" referids="$referids,workgroup"/>">
<img src="<mm:treefile page="/gfx/icon_addcontact.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.addcontacts" />" alt="<di:translate key="workspace.addcontacts" />"/>
</a>
</mm:compare>	     
</mm:present>
<br clear="all">
<mm:listnodes referid="myuser" >
   <!-- listing workspaces for '<mm:field name="lastname"/>' -->
    <b>
      <mm:hasfield name="name"><mm:field name="name"/></mm:hasfield><%-- does this occur --%>
    <mm:field name="firstname"/> <mm:field name="lastname"/>
    </b><br/>


  <mm:relatednodes type="workspaces" id="workspace">

<a href="<mm:treefile page="/workspace/createfolder.jsp" objectlist="$includePath" referids="$referids">
	   <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
	   <mm:param name="workspace"><mm:write referid="workspace"/></mm:param>
           <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	   <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	 </mm:treefile>">
  <img src="<mm:treefile page="/workspace/gfx/map maken.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.createfolder" />" alt="<di:translate key="workspace.createfolder" />" /></a>

<mm:isgreaterthan referid="currentfolder" value="0">
  <a href="<mm:treefile page="/workspace/changefolder.jsp" objectlist="$includePath" referids="$referids">
	     <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		 <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	     <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	       </mm:treefile>">

    <img src="<mm:treefile page="/workspace/gfx/map hernoemen.gif" objectlist="$includePath" referids="$referids"/>" border="0"  title="<di:translate key="workspace.renamefolder" />"  alt="<di:translate key="workspace.renamefolder" />" /></a>

  <a href="<mm:treefile page="/workspace/deletefolder.jsp" objectlist="$includePath" referids="$referids">
	     <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
	     <mm:param name="callerpage">/workspace/index.jsp</mm:param>
	     <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
	   </mm:treefile>">
    <img src="<mm:treefile page="/workspace/gfx/verwijder map.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.deletefolder" />" alt="<di:translate key="workspace.deletefolder" />" /></a>
</mm:isgreaterthan>
    <br clear="all"/>

    <mm:relatednodes type="folders">

      <mm:import id="currentnumber"><mm:field name="number"/></mm:import>

      <%-- folder is open --%>
      <mm:compare referid="currentfolder" referid2="currentnumber">
	<img src="<mm:treefile page="/workspace/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="workspace.folderopened" />" alt="<di:translate key="workspace.folderopened" />" />
      </mm:compare>

      <%-- folder is closed --%>
      <mm:compare referid="currentfolder" referid2="currentnumber" inverse="true">
	<img src="<mm:treefile page="/workspace/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="workspace.folderclosed" />" alt="<di:translate key="workspace.folderclosed" />" />
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
        <img src="<mm:treefile page="/workspace/gfx/document plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.adddocument" />" alt="<di:translate key="workspace.adddocument" />" /></a>

      <a href="<mm:treefile page="/workspace/addurl.jsp" objectlist="$includePath" referids="$referids">
		            <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
		            <mm:param name="callerpage">/workspace/index.jsp</mm:param>
                    <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
		     </mm:treefile>">
        <img src="<mm:treefile page="/workspace/gfx/bron plaatsen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.addsource" />"  alt="<di:translate key="workspace.addsource" />" /></a>

	
        <input type="image" name="action_move" src="<mm:treefile page="/workspace/gfx/verplaats geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.moveselected" />" alt="<di:translate key="workspace.moveselected" />" />

        <input type="image" name="action_delete" src="<mm:treefile page="/workspace/gfx/verwijder geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="workspace.deleteselected" />" alt="<di:translate key="workspace.deleteselected" />"/>
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
            <di:headercell><di:translate key="workspace.type" /></di:headercell>
            <di:headercell><di:translate key="workspace.title" /></di:headercell>
            <di:headercell><di:translate key="workspace.description" /></di:headercell>
            <di:headercell><di:translate key="workspace.filename" /></di:headercell>
            <di:headercell><di:translate key="workspace.date" /></di:headercell>
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
                <di:cell><img src="<mm:write referid="gfx_attachment"/>" title="<di:translate key="workspace.folderitemtypedocument" />" alt="<di:translate key="workspace.folderitemtypedocument" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="title" /></a></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
                <di:cell><a href="<mm:attachment/>"><mm:field name="filename"/></a></di:cell>
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
                <di:cell><img src="<mm:write referid="gfx_url"/>" title="<di:translate key="workspace.folderitemtypeurl" />" alt="<di:translate key="workspace.folderitemtypeurl" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
                <di:cell><a href="<mm:write referid="urllink" />" target="unknownframe"><mm:field name="url"/></a></di:cell>
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
              </mm:compare>
              <mm:compare referid="objecttype" value="pages">
                <di:cell><img src="<mm:write referid="gfx_page"/>" title="<di:translate key="workspace.folderitemtypepage" />" alt="<di:translate key="workspace.folderitemtypepage" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                <di:cell><mm:field name="text" /></di:cell>
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
              </mm:compare>
              <mm:compare referid="objecttype" value="chatlogs">
                <di:cell><img src="<mm:write referid="gfx_chatlog"/>" title="<di:translate key="workspace.folderitemtypechatlog" />" alt="<di:translate key="workspace.folderitemtypechatlog" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><di:translate key="workspace.folderitemtypechatlog" /><mm:field name="number"/></a></di:cell>
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
</mm:cloud>
</mm:content>
