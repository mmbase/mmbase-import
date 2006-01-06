<%--
  This template shows all people in the addressbooks: all students that
  are related to those classes that we are in.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.StringTokenizer"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="address.addressbook" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="field"/><%-- submit selected email addresses to mail/write.jsp --%>
<mm:import externid="mailid"/>

<mm:import externid="addtoworkgroup"/>  

<div class="rows">
<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_addressbook.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="address.addressbook" />" title="<di:translate key="address.addressbook" />" alt="<di:translate key="address.addressbook" />"/>
    <di:translate key="address.addressbook" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    <mm:node number="$user">
      <mm:import externid="addr_class"/>
      <%-- Show personal addressbook --%>
      <mm:relatednodes type="addressbooks" max="1">
        <mm:import id="addressbook"><mm:field name="number"/></mm:import>
        <mm:notpresent referid="addr_class"><b></mm:notpresent>
        <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids,addtoworkgroup?">
                   <mm:param name="addressbook"><mm:field name="number"/></mm:param>
                   <mm:present referid="field">
                     <mm:param name="field"><mm:write referid="field"/></mm:param>
                   </mm:present>
                   <mm:present referid="mailid">
                     <mm:param name="mailid"><mm:write referid="mailid"/></mm:param>
                   </mm:present>
                 </mm:treefile>"><mm:field name="name"/></a>
        <mm:notpresent referid="addr_class"></b></mm:notpresent>
        <br />
      </mm:relatednodes>

      <%-- Show class addressbook --%>
      <mm:relatednodes type="classes" orderby="name">
        <mm:import id="thisclass" reset="true"><mm:field name="number"/></mm:import>
         <mm:present referid="addr_class"><mm:compare referid="thisclass" value="$addr_class"><b></mm:compare></mm:present>
         <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids,addtoworkgroup?">
                    <mm:present referid="field">
                      <mm:param name="field"><mm:write referid="field"/></mm:param>
                    </mm:present>
                    <mm:present referid="mailid">
                      <mm:param name="mailid"><mm:write referid="mailid"/></mm:param>
                    </mm:present>
                    <mm:param name="addr_class"><mm:field name="number"/></mm:param>
                  </mm:treefile>"><mm:field name="name"/></a>
         <mm:present referid="addr_class"><mm:compare referid="thisclass" value="$addr_class"></b></mm:compare></mm:present>
         <br/>
      </mm:relatednodes>

      <%-- Show workgroup addressbook --%>
      <mm:relatednodes type="workgroups" orderby="name">
        <mm:import id="thisclass" reset="true"><mm:field name="number"/></mm:import>
        <mm:present referid="addr_class"><mm:compare referid="thisclass" value="$addr_class"><b></mm:compare></mm:present>
        <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids,addtoworkgroup?">
                   <mm:present referid="field">
                     <mm:param name="field"><mm:write referid="field"/></mm:param>
                   </mm:present>
                   <mm:present referid="mailid">
                     <mm:param name="mailid"><mm:write referid="mailid"/></mm:param>
                   </mm:present>
                   <mm:param name="addr_class"><mm:field name="number"/></mm:param>
                 </mm:treefile>"><mm:field name="name"/></a>
        <mm:present referid="addr_class"><mm:compare referid="thisclass" value="$addr_class"></b></mm:compare></mm:present>
        <br/>
      </mm:relatednodes>

      <form action="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids"/>" method="GET">
        <mm:present referid="field">
          <input type="hidden" name="field" value="<mm:write referid="field"/>"/>
        </mm:present>
        <mm:present referid="mailid">
          <input type="hidden" name="mailid" value="<mm:write referid="mailid"/>"/>
        </mm:present>
        <mm:present referid="addr_class">
          <input type="hidden" name="addr_class" value="<mm:write referid="addr_class"/>"/>
        </mm:present>
        <input type="text" size="20" name="addr_search"/><br/>
        <input type="submit" value="<di:translate key="address.search"/>" />
      </form>

      <mm:notpresent referid="addressbook">
        <mm:import id="addressbook">-1</mm:import>
      </mm:notpresent>
      
      <mm:node number="$user">
        <mm:import id="emaildomain" escape="trimmer"><mm:treeinclude write="true" page="/email/init/emaildomain.jsp" objectlist="$includePath"/></mm:import>

        <mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>
        <mm:import externid="addr_search" jspvar="addr_search" vartype="String"/>

        <mm:present referid="addr_search">
          <mm:import id="list_class">1</mm:import>
          <mm:import id="list_book">1</mm:import>
        </mm:present>
    
        <mm:present referid="addr_class">
          <mm:import id="list_class" reset="true">1</mm:import>
        </mm:present>
    
        <mm:notpresent referid="addr_class">
          <mm:import id="list_book" reset="true">1</mm:import>
        </mm:notpresent>

        <%-- Get all people of related classes except yourself --%>
        <mm:present referid="list_class">
          <mm:relatednodescontainer path="classes">
            <mm:present referid="addr_class">
              <mm:constraint field="number" value="$addr_class"/>
            </mm:present>
            <mm:relatednodes>
              <mm:relatednodescontainer path="people">
                <mm:constraint field="number" value="$user" inverse="true"/>
                <mm:present referid="addr_search">
                  <mm:composite operator="OR">
                  <%
                    StringTokenizer st = new StringTokenizer(addr_search);
                    while (st.hasMoreTokens()) {
                    %><mm:import id="addr_search_word" reset="true"><%= st.nextToken() %></mm:import>
                      <mm:constraint field="people.firstname" value="%$addr_search_word%" operator="LIKE"/>
                      <mm:constraint field="people.lastname" value="%$addr_search_word%" operator="LIKE"/>
                      <mm:constraint field="people.email" value="%$addr_search_word%" operator="LIKE"/>
                  <% } %>
                  </mm:composite>
                </mm:present>
  
                <mm:relatednodes>
                  <mm:remove referid="peoplenumber"/>
                  <mm:import id="peoplenumber" jspvar="peoplenumber"><mm:field name="number"/></mm:import>
                  <%
                    if (!linkedlist.contains(new Integer(peoplenumber))) {
                      linkedlist.add(peoplenumber);
                    }
                   %>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:present>
  
        <%-- Get all people of workgroups except yourself --%>
        <mm:present referid="list_class">
          <mm:relatednodescontainer path="workgroups">
            <mm:present referid="addr_class">
              <mm:constraint field="number" value="$addr_class"/>
            </mm:present>
            <mm:relatednodes>
              <mm:relatednodescontainer path="people">
                <mm:constraint field="number" value="$user" inverse="true"/>
                <mm:present referid="addr_search">
                  <mm:composite operator="OR">
                  <%
                    StringTokenizer st = new StringTokenizer(addr_search);
                    while (st.hasMoreTokens()) {
                    %><mm:import id="addr_search_word" reset="true"><%= st.nextToken() %></mm:import>
                      <mm:constraint field="people.firstname" value="%$addr_search_word%" operator="LIKE"/>
                      <mm:constraint field="people.lastname" value="%$addr_search_word%" operator="LIKE"/>
                      <mm:constraint field="people.email" value="%$addr_search_word%" operator="LIKE"/>
                  <% } %>
                  </mm:composite>
                </mm:present>
                <mm:relatednodes>
                  <mm:remove referid="peoplenumber"/>
                  <mm:import id="peoplenumber" jspvar="peoplenumber"><mm:field name="number"/></mm:import>
                  <%
                    if (!linkedlist.contains(new Integer(peoplenumber))) {
                      linkedlist.add(peoplenumber);
                    }
                  %>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:present>
      
        <%-- Get all contacts --%>
        <mm:present referid="list_book">
          <mm:relatednodes type="addressbooks">
            <mm:relatednodescontainer path="contacts">
              <mm:constraint field="number" value="$user" inverse="true"/>
              <mm:present referid="addr_search">
                <mm:composite operator="OR">
                <%
                  StringTokenizer st = new StringTokenizer(addr_search);
                  while (st.hasMoreTokens()) {
                  %><mm:import id="addr_search_word" reset="true"><%= st.nextToken() %></mm:import>
                    <mm:constraint field="contacts.firstname" value="%$addr_search_word%" operator="LIKE"/>
                    <mm:constraint field="contacts.lastname" value="%$addr_search_word%" operator="LIKE"/>
                    <mm:constraint field="contacts.email" value="%$addr_search_word%" operator="LIKE"/>
                <% } %>
                </mm:composite>
              </mm:present>
              <mm:relatednodes>
                <mm:remove referid="peoplenumber"/>
                <mm:import id="peoplenumber" jspvar="peoplenumber"><mm:field name="number"/></mm:import>
                <%
                  if (!linkedlist.contains(new Integer(peoplenumber))) {
                    linkedlist.add(peoplenumber);
                  }
                %>
              </mm:relatednodes>
            </mm:relatednodescontainer>
          </mm:relatednodes>
        </mm:present>
      </mm:node>

      <mm:present referid="list_book">
        <mm:import id="checkboxes" reset="true">1</mm:import>
      </mm:present>
      <mm:present referid="field">
        <mm:import id="checkboxes" reset="true">1</mm:import>
      </mm:present>
      <mm:present referid="addtoworkgroup">
        <mm:import id="checkboxes" reset="true">1</mm:import>
      </mm:present>
    </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
  </div>

  <mm:notpresent referid="field">
    <mm:notpresent referid="addtoworkgroup">
      <form action="<mm:treefile page="/address/deletecontacts.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
        <input type="hidden" name="callerpage" value="/address/index.jsp?sf=lastname,firstname&so=up,up">
        <input type="hidden" name="addressbook" value="<mm:write referid="addressbook"/>"> 
    </mm:notpresent>
  </mm:notpresent>
  
  <mm:present referid="field"><%-- refer to email/write.jsp --%>
    <form action="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
      <input type="hidden" name="id" value="<mm:write referid="mailid"/>">
      <input type="hidden" name="field" value="<mm:write referid="field"/>">
  </mm:present>
 
  <mm:present referid="addtoworkgroup">
    <form action="<mm:treefile page="/projectgroup/index.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
      <input type="hidden" name="addcontact" value="1">
  </mm:present>
  
  <%-- Buttons on top of the right part of the screen --%>
  <div class="contentSubHeader">
    <mm:notpresent referid="field">
      <mm:present referid="list_book">
        <mm:isgreaterthan referid="addressbook" value="0">
         <a href="<mm:treefile page="/address/addcontact.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="addressbook"><mm:write referid="addressbook"/></mm:param>
                 <mm:param name="callerpage">/address/index.jsp?sf=lastname,firstname&so=up,up</mm:param>
               </mm:treefile>">
        <img src="<mm:treefile page="/gfx/icon_addcontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" title="<di:translate key="address.addcontact" />" alt="<di:translate key="address.addcontact" />"/></a>
        <input type="image" name="action_delete" value="delete" src="<mm:treefile page="/gfx/icon_deletecontact.gif" objectlist="$includePath" referids="$referids"/>" width="50" height="28" border="0" title="<di:translate key="address.deletecontact" />" alt="<di:translate key="address.deletecontact" />"/></a>
    </mm:isgreaterthan>
      </mm:present>
    </mm:notpresent>
  </div>

  <div class="contentBody">
    <mm:listnodescontainer type="people">
      <mm:constraint field="number" referid="linkedlist" operator="IN"/>
      <di:table maxitems="10">
        <di:row>
          <mm:present referid="checkboxes">
            <di:headercell>
              <input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"/>
            </di:headercell>
          </mm:present>
          <di:headercell><di:translate key="address.type" /></di:headercell>
          <di:headercell sortfield="firstname"><di:translate key="address.firstname" /></di:headercell>
          <di:headercell sortfield="lastname" default="true"><di:translate key="address.lastname" /></di:headercell>
          <di:headercell sortfield="email"><di:translate key="address.email" /></di:headercell>
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
            <mm:present referid="checkboxes">
              <di:cell>
                <input type="checkbox" name="ids" value="<mm:field name="number"/>"></input>
              </di:cell>
    	    </mm:present>

            <di:cell>
              <mm:remove referid="contactno"/>
              <mm:import id="contactno"><mm:field name="number"/></mm:import>

              <mm:compare referid="nodetype" value="contacts">
                <img src="<mm:treefile page="/address/gfx/contact.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="address.contact" />" alt="<di:translate key="address.contact" />"/>
              </mm:compare>

              <mm:node number="$contactno">
                <mm:remove referid="isonline"/>
                <mm:field name="isonline" id="isonline" write="false"/>
                <di:hasrole referid="contactno" role="contenteditor">
                  <mm:compare referid="isonline" value="0">
                    <img src="<mm:treefile page="/address/gfx/editor_offline.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.editoroffline" />" alt="<di:translate key="core.editoroffline" />"/>
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                    <img src="<mm:treefile page="/address/gfx/editor_online.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.editoronline" />" alt="<di:translate key="core.editoronline" />"/>
                  </mm:compare>
                </di:hasrole>

                <di:hasrole referid="contactno" role="courseeditor">
                  <mm:compare referid="isonline" value="0">
                    <img src="<mm:treefile page="/address/gfx/editor_offline.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.editoroffline" />"  alt="<di:translate key="core.editoroffline" />"/>
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                    <img src="<mm:treefile page="/address/gfx/editor_online.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.editoronline" />" alt="<di:translate key="core.editoronline" />"/>
                  </mm:compare>
                </di:hasrole>

                <di:hasrole referid="contactno" role="administrator">
                  <mm:compare referid="isonline" value="0">
                    <img src="<mm:treefile page="/address/gfx/administrators_offline.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.administratoroffline" />" alt="<di:translate key="core.administratoroffline" />"/>
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                    <img src="<mm:treefile page="/address/gfx/administrators_online.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.administratoronline" />" alt="<di:translate key="core.administratoronline" />"/>
                  </mm:compare>
                </di:hasrole>

                <di:hasrole referid="contactno" role="teacher">
                  <mm:compare referid="isonline" value="0">
                    <img src="<mm:treefile page="/address/gfx/teachers_offline.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.teacheroffline" />" alt="<di:translate key="core.teacheroffline" />"/>
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                    <img src="<mm:treefile page="/address/gfx/teachers_online.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.teacheronline" />" alt="<di:translate key="core.teacheronline" />"/>
                  </mm:compare>
                </di:hasrole>

                <di:hasrole referid="contactno" role="student">
                  <mm:compare referid="isonline" value="0">
                    <img src="<mm:treefile page="/address/gfx/students_offline.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.studentoffline" />" alt="<di:translate key="core.studentoffline" />"/>
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                    <img src="<mm:treefile page="/address/gfx/students_online.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="core.studentonline" />" alt="<di:translate key="core.studentonline" />"/>
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
    <mm:present referid="addtoworkgroup">
      <input type="hidden" name="addtoworkgroup" value="1">
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
      } else {
        frm.elements['ids'].checked=newState;
      }
    }
</script>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
