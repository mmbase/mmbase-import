<%--
  This template shows all people in the addressbooks: all students that
  are related to those classes that we are in.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.address.AddressMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="ADDRESSBOOK" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="field"/><%-- submit selected email addresses to mail/write.jsp --%>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_addressbook.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="ADDRESSBOOK" />"/>
    <fmt:message key="ADDRESSBOOK" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">

    <mm:node number="$user">

      <mm:relatednodes type="addressbooks" max="1">
        <mm:import id="addressbook"><mm:field name="number"/></mm:import>
        <mm:field name="name"/>
      </mm:relatednodes>

      <mm:notpresent referid="addressbook">
        <mm:import id="addressbook">-1</mm:import>
      </mm:notpresent>

  </div>
</div>

<div class="maincontent">

  <div class="contentHeader">
  </div>

  <mm:notpresent referid="field">
  <form action="<mm:treefile page="/address/deletecontacts.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
   <input type="hidden" name="callerpage" value="/address/index.jsp?sf=lastname,firstname&so=up,up">
   <input type="hidden" name="addressbook" value="<mm:write referid="addressbook"/>"> 
  </mm:notpresent>
  
  <mm:present referid="field"><%-- refer to email/write.jsp --%>
  <mm:import externid="mailid"/>
  <form action="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
  <input type="hidden" name="id" value="<mm:write referid="mailid"/>">
  <input type="hidden" name="field" value="<mm:write referid="field"/>">
  </mm:present>
  
  
  
  <div class="contentSubHeader">

    <mm:notpresent referid="field">
      <mm:isgreaterthan referid="addressbook" value="0">
   	    <a href="<mm:treefile page="/address/addcontact.jsp" objectlist="$includePath" referids="$referids">
	               <mm:param name="addressbook"><mm:write referid="addressbook"/></mm:param>
	               <mm:param name="callerpage">/address/index.jsp?sf=lastname,firstname&so=up,up</mm:param>
	             </mm:treefile>">
	    <img src="<mm:treefile page="/gfx/icon_addcontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" alt="<fmt:message key="ADDCONTACT" />"/></a>

	    
	    <input type="image" name="action_delete" value="delete" src="<mm:treefile page="/gfx/icon_deletecontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" alt="<fmt:message key="DELETECONTACT" />"/></a>
	  </mm:isgreaterthan>
    </mm:notpresent>
    
    
    </mm:node>

  </div>

  <div class="contentBody">

    <mm:node number="$user">
      <mm:import id="emaildomain"><mm:treeinclude write="true" page="/email/init/emaildomain.jsp" objectlist="$includePath"/></mm:import>

      <mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

      <%-- Get all people of related classes except yourself --%>
      <mm:relatednodes type="classes">
        <mm:relatednodescontainer path="people">
          <mm:constraint field="number" value="$user" inverse="true"/>
          <mm:relatednodes>
            <mm:remove referid="peoplenumber"/>
            <mm:import id="peoplenumber" jspvar="peoplenumber"><mm:field name="number"/></mm:import>
            <%
              if ( !linkedlist.contains( new Integer( peoplenumber ) ) ) {
                linkedlist.add( peoplenumber );
              }
             %>
          </mm:relatednodes>

        </mm:relatednodescontainer>
      </mm:relatednodes>

      <%-- Get all contacts --%>
      <mm:relatednodes type="addressbooks">
        <mm:relatednodescontainer path="contacts">
          <mm:constraint field="number" value="$user" inverse="true"/>
          <mm:relatednodes>
            <mm:remove referid="peoplenumber"/>
            <mm:import id="peoplenumber" jspvar="peoplenumber"><mm:field name="number"/></mm:import>
            <%
              if ( !linkedlist.contains( new Integer( peoplenumber ) ) ) {
                linkedlist.add( peoplenumber );
              }
             %>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:relatednodes>


      <mm:listnodescontainer type="people">
        <mm:constraint field="number" referid="linkedlist" operator="IN"/>

        <di:table maxitems="10">

          <di:row>
            <di:headercell><input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"></input></di:headercell>
            <di:headercell><fmt:message key="TYPE" /></di:headercell>
            <di:headercell sortfield="firstname"><fmt:message key="FIRSTNAME" /></di:headercell>
            <di:headercell sortfield="lastname" default="true"><fmt:message key="LASTNAME" /></di:headercell>
            <di:headercell sortfield="email"><fmt:message key="EMAIL" /></di:headercell>
          </di:row>

          <mm:listnodes id="mycontacts">

            <mm:import id="link">
              <a href="<mm:treefile page="/address/updatecontact.jsp" objectlist="$includePath" referids="$referids">
                         <mm:param name="callerpage">/address/index.jsp?sf=lastname,firstname&so=up,up</mm:param>
                         <mm:param name="addressbook"><mm:write referid="addressbook" /></mm:param>
                         <mm:param name="contact"><mm:field name="number"/></mm:param>
                       </mm:treefile>">
	      
            </mm:import>

            <di:row>
              <mm:remove referid="nodetype"/>
			  <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>

              <di:cell>
                <mm:compare referid="nodetype" value="contacts">
                  <input type="checkbox" name="ids" value="<mm:field name="number"/>"></input>
                </mm:compare>
		<mm:compare referid="nodetype" value="contacts" inverse="true">
		  <mm:present referid="field">
		    <input type="checkbox" name="ids" value="<mm:field name="number"/>"></input>
		  </mm:present>
		</mm:compare>
		
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

          </mm:listnodes>

        </di:table>

      </mm:listnodescontainer>

    <mm:present referid="field">
    <input type="submit" value="Ok">
    </mm:present>
      
    </mm:node>

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
