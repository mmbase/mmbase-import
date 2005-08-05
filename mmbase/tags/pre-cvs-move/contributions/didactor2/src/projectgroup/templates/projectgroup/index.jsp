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
    <title><fmt:message key="PROJECTGROUPS" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="addtoworkgroup"/>
<mm:import externid="ids" vartype="list"/>
<mm:present referid="addtoworkgroup">
    <mm:present referid="ids">
        <mm:node referid="workgroup">
            
            <mm:stringlist referid="ids" id="id">
            <mm:node number="$id">
            <mm:remove referid="alreadadded"/>
            <mm:relatednodes type="workgroups" constraints="workgroups.number=$workgroup" max="1">
                 <mm:import id="alreadadded" reset="true">1</mm:import>
            </mm:relatednodes>
            <mm:notpresent referid="alreadyadded">
                <mm:createrelation source="workgroup" destination="id" role="related"/>
            </mm:notpresent>
            </mm:node>
            </mm:stringlist>
        </mm:node>
    </mm:present>
</mm:present>


<div class="rows">

<div class="navigationbar">
<div class="titlebar">
  <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="SHAREDDOCUMENTS" />"/>
      <fmt:message key="PROJECTGROUPS" />

</div>
</div>

<div class="folders">

<div class="folderHeader">
    <fmt:message key="PROJECTGROUPS" />
</div>

<div class="folderBody">

<fmt:bundle basename="nl.didactor.component.address.AddressMessageBundle">


<a href="<mm:treefile page="/projectgroup/create.jsp" objectlist="$includePath" referids="$referids">
           <mm:param name="callerpage">/projectgroup/index.jsp</mm:param>
	 </mm:treefile>">
  <img src="<mm:treefile page="/workspace/gfx/map maken.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="CREATEGROUP" />" /></a>

<mm:isgreaterthan referid="workgroup" value="0">
  <a href="<mm:treefile page="/projectgroup/rename.jsp" objectlist="$includePath" referids="$referids">
		 <mm:param name="callerpage">/projectgroup/index.jsp</mm:param>
	       </mm:treefile>">

    <img src="<mm:treefile page="/workspace/gfx/map hernoemen.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="RENAMEGROUP" />" /></a>

  <a href="<mm:treefile page="/projectgroup/delete.jsp" objectlist="$includePath" referids="$referids">
	     <mm:param name="callerpage">/projectgroup/index.jsp</mm:param>
	   </mm:treefile>">
    <img src="<mm:treefile page="/workspace/gfx/verwijder map.gif" objectlist="$includePath" referids="$referids"/>" border="0" alt="<fmt:message key="DELETEGROUP" />" /></a>
</mm:isgreaterthan>
<br clear="all">

<mm:node referid="user"> 
  <mm:relatednodes type="workgroups" id="thisworkgroup" constraints="protected=0">
  <a href="<mm:treefile page="/projectgroup/index.jsp" referids="provider?,education?,class?" objectlist="$includePath">
    <mm:param name="workgroup"><mm:field name="number"/></mm:param>
    </mm:treefile>">
    <mm:field name="number">
        <mm:compare value="$workgroup"><b><mm:field name="name"/></b></mm:compare>
        <mm:compare value="$workgroup" inverse="true"><mm:field name="name"/></mm:compare>
    </mm:field>
    </a><br>
  </mm:relatednodes>
</mm:node>

</div>

</div>

<div class="mainContent">

<div class="contentHeader">
  </div>

    <mm:node referid="workgroup" notfound="skip">

<form action="<mm:treefile objectlist="$includePath"  referids="$referids" page="/projectgroup/removecontact.jsp"/>" method="POST">
<input type="hidden" name="callerpage" value="/projectgroup/index.jsp">
 <div class="contentSubHeader">

   	    <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="addtoworkgroup">1</mm:param>
	             </mm:treefile>">
	    <img src="<mm:treefile page="/gfx/icon_addcontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" alt="<fmt:message key="ADDCONTACT" />"/></a>

	    
	    <input type="image" name="action_delete" value="delete" src="<mm:treefile page="/gfx/icon_deletecontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" alt="<fmt:message key="DELETECONTACT" />"/></a>
    
    

  </div>
  <div class="contentBody">

     <mm:relatednodescontainer type="people">
        <di:table maxitems="50">

          <di:row>
                <di:headercell>
                    <input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"/>
                </di:headercell>
            <di:headercell><fmt:message key="TYPE" /></di:headercell>
            <di:headercell sortfield="firstname"><fmt:message key="FIRSTNAME" /></di:headercell>
            <di:headercell sortfield="lastname" default="true"><fmt:message key="LASTNAME" /></di:headercell>
            <di:headercell sortfield="email"><fmt:message key="EMAIL" /></di:headercell>
          </di:row>
            
          <mm:relatednodes>

            <mm:import id="link">
              <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                         <mm:param name="contact"><mm:field name="number"/></mm:param>
                       </mm:treefile>">
	      
            </mm:import>

            <di:row>
              <mm:remove referid="nodetype"/>
			  <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>

                    <di:cell>
                      <input type="checkbox" name="ids" value="<mm:field name="number"/>"></input>
                    </di:cell>
		
              <di:cell>
                <mm:remove referid="contactno"/>
                <mm:import id="contactno"><mm:field name="number"/></mm:import>


                <mm:compare referid="nodetype" value="contacts">
                  <img src="<mm:treefile page="/address/gfx/contact.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="CONTACT" />"/>
                </mm:compare>

                <mm:node number="$contactno">
                  <mm:remove referid="isonline"/>
                  <mm:field name="isonline" id="isonline" write="false"/>

                  <di:hasrole referid="contactno" role="contenteditor">
                    <mm:compare referid="isonline" value="0">
                      <img src="<mm:treefile page="/address/gfx/editor_offline.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="EDITOROFFLINE" />"/>
                    </mm:compare>
                    <mm:compare referid="isonline" value="1">
                      <img src="<mm:treefile page="/address/gfx/editor_online.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="EDITORONLINE" />"/>
                    </mm:compare>
                  </di:hasrole>

                  <di:hasrole referid="contactno" role="courseeditor">
                    <mm:compare referid="isonline" value="0">
                      <img src="<mm:treefile page="/address/gfx/editor_offline.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="EDITOROFFLINE" />"/>
                    </mm:compare>
                    <mm:compare referid="isonline" value="1">
                      <img src="<mm:treefile page="/address/gfx/editor_online.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="EDITORONLINE" />"/>
                    </mm:compare>
                  </di:hasrole>

                  <di:hasrole referid="contactno" role="administrator">
                    <mm:compare referid="isonline" value="0">
                      <img src="<mm:treefile page="/address/gfx/administrators_offline.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="ADMINISTRATOROFFLINE" />"/>
                    </mm:compare>
                    <mm:compare referid="isonline" value="1">
                      <img src="<mm:treefile page="/address/gfx/administrators_online.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="ADMINISTRATORONLINE" />"/>
                    </mm:compare>
                  </di:hasrole>

                  <di:hasrole referid="contactno" role="teacher">
                    <mm:compare referid="isonline" value="0">
                      <img src="<mm:treefile page="/address/gfx/teachers_offline.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="TEACHEROFFLINE" />"/>
                    </mm:compare>
                    <mm:compare referid="isonline" value="1">
                      <img src="<mm:treefile page="/address/gfx/teachers_online.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="TEACHERONLINE" />"/>
                    </mm:compare>
                  </di:hasrole>

                  <di:hasrole referid="contactno" role="student">
                    <mm:compare referid="isonline" value="0">
                      <img src="<mm:treefile page="/address/gfx/students_offline.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="STUDENTOFFLINE" />"/>
                    </mm:compare>
                    <mm:compare referid="isonline" value="1">
                      <img src="<mm:treefile page="/address/gfx/students_online.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="STUDENTONLINE" />"/>
                    </mm:compare>
                  </di:hasrole>

                </mm:node>

              </di:cell>
              <di:cell><mm:write escape="none" referid="link"/><mm:field name="firstname" /></a></di:cell>
              <di:cell><mm:write escape="none" referid="link"/><mm:field name="lastname" /></a></di:cell>
              <di:cell>
                <mm:remove referid="emailaddress"/>
		    <mm:write escape="none" referid="link"/>
                <mm:field name="email"/></a>
              </di:cell>
            </di:row>
            <mm:remove referid="link" />

          </mm:relatednodes>

        </di:table>
    </mm:relatednodescontainer>
    </mm:node>

</fmt:bundle>

  </div>
</div>
</div>

    </form>      

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
