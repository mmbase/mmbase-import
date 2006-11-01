<%--
  This template delete existing items of a folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="workspace.deletecontacts" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="ids"/>

<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>

<mm:import externid="action1"/>
<mm:import externid="action2"/>


<%-- Check if the yes button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="workspace.deleteyes" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:node number="$workgroup">
      <mm:relatednodescontainer type="people">
        <mm:constraint field="number" referid="list" operator="IN"/>
        <mm:relatednodes>

            <mm:listrelationscontainer type="workgroups">
              <mm:constraint field="workgroups.number" value="$workgroup"/>
              <mm:listrelations>
                <mm:deletenode/>
              </mm:listrelations>
            </mm:listrelationscontainer>

        </mm:relatednodes>
      </mm:relatednodescontainer>
    </mm:node>

    <%-- Show the previous page --%>
    <mm:redirect referids="$referids" page="$callerpage"/>

  </mm:compare>
</mm:present>


<%-- Check if the no button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="workspace.deleteno" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids" page="$callerpage"/>
  </mm:compare>
</mm:present>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.shareddocuments" />"  alt="<di:translate key="workspace.shareddocuments" />" />
      <di:translate key="workspace.projectgroups" />
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
  	<di:translate key="workspace.deletecontacts" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletefolderitems" method="post" action="<mm:treefile page="/projectgroup/removecontact.jsp" objectlist="$includePath" referids="$referids"/>">
      <di:translate key="workspace.deletecontactsyesno" />
      <p/>

      <!-- TODO show data in near future -->
      <table class="Font">
       </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.deleteyes" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.deleteno" />" />
    </form>
  </div>
</div>
</div>
</mm:cloud>
</mm:content>
