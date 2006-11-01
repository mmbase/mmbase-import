<%--
  This template deletes a existing folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="workspace.deleteprojectgroup" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>


<%-- Check if the yes button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="workspace.deleteyes" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:node referid="workgroup">
        <mm:field name="protected">
            <mm:compare value="0">
            
              <mm:deletenode deleterelations="true"/>
            </mm:compare>
        </mm:field>
    </mm:node>

    <%-- Remove the reference to the currentfolder --%>
    <mm:remove referid="workgroup"/>

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
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="workspace.projectgroups" />" alt="<di:translate key="workspace.projectgroups" />" />
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
  	<di:translate key="workspace.deleteprojectgroup" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletefolder" method="post" action="<mm:treefile page="/projectgroup/delete.jsp" objectlist="$includePath" referids="$referids"/>">

      <di:translate key="workspace.deleteprojectgroupyesno" />
      <p/>

      <table class="Font">
      <mm:node referid="workgroup">
        <mm:fieldlist fields="name">
          <tr>
          <td><mm:fieldinfo type="guiname"/></td>
          <td><mm:fieldinfo type="value"/></td>
          </tr>
        </mm:fieldlist>
      </mm:node>
      </table>
      <p/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.deleteyes" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.deleteno" />" />
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
