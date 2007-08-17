<%--
  This template deletes a existing contact.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="address.deletecontact" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="addressbook"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<mm:import externid="ids"/>

<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>

<%-- Check if the yes button is pressed --%>
<mm:import id="action1text"><di:translate key="address.deleteyes" /></mm:import>
<mm:compare referid="action1" referid2="action1text">
  
  <%-- Determine the contacts to be deleted --%>
  <mm:listnodescontainer type="object">
  
    <mm:constraint field="number" referid="list" operator="IN"/>
    <mm:listnodes>

      <mm:remove referid="nodetype"/>
      <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>
     
      <%-- When the nodetype is a contact delete the node with relation --%>
      <mm:compare referid="nodetype" value="contacts">
        <mm:deletenode deleterelations="true"/>
      </mm:compare>

      <%-- When the nodetype is a people object only delete the relation --%>
      <mm:compare referid="nodetype" value="people">
        <%--  Deleting of people from the addressbook not possible anymore.
        <mm:listrelations type="addressbooks">
          <mm:deletenode/>
        </mm:listrelations>
        --%>
      </mm:compare>
 
    </mm:listnodes>
   
  </mm:listnodescontainer>

  <%-- Show the previous page --%>
  <mm:redirect referids="$referids" page="$callerpage"/>

</mm:compare>


<%-- Check if the no button is pressed --%>
<mm:import id="action2text"><di:translate key="address.deleteno" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,addressbook" page="$callerpage"/>
</mm:compare>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">

    <mm:node number="$addressbook">
	  <mm:field name="name"/><br/>
	</mm:node>

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
    <di:translate key="address.deletecontact" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletecontact" class="formInput" method="post" action="<mm:treefile page="/address/deletecontacts.jsp" objectlist="$includePath" referids="$referids"/>">

      <di:translate key="address.deleteselectedcontactsyesno" />
      <p/>
      <table class="font">
      </table>
      <p/>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="addressbook" value="<mm:write referid="addressbook"/>"/>
      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input type="submit" class="formbutton" name="action1" value="<di:translate key="address.deleteyes" />"/>
      <input type="submit" class="formbutton" name="action2" value="<di:translate key="address.deleteno" />"/>

    </form>

  </div>
</div>
</div>

</mm:cloud>
</mm:content>
