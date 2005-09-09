<%--
  This template deletes a existing folder.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="DELETEPROJECTGROUP" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>


<%-- Check if the yes button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><fmt:message key="DELETEYES" /></mm:import>
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
  <mm:import id="action2text"><fmt:message key="DELETENO" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="PROJECTGROUPS" />" />
      <fmt:message key="PROJECTGROUPS" />
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
  	<fmt:message key="DELETEPROJECTGROUP" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletefolder" method="post" action="<mm:treefile page="/projectgroup/delete.jsp" objectlist="$includePath" referids="$referids"/>">

      <fmt:message key="DELETEPROJECTGROUPYESNO" />
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
      <input class="formbutton" type="submit" name="action1" value="<fmt:message key="DELETEYES" />" />
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="DELETENO" />" />
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</fmt:bundle>
</mm:cloud>
</mm:content>
