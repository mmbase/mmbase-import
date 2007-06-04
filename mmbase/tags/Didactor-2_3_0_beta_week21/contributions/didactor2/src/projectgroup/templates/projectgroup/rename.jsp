<%--
  This template changes a existing folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="workspace.renameprojectgroup" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="submitted"/>

<mm:present referid="submitted">
<mm:notpresent referid="action2">

    <mm:import id="name" externid="_name"/>
    <mm:compare referid="name" value="" inverse="true">

      <mm:node number="$workgroup">
        <mm:fieldlist type="all" fields="name">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
      </mm:node>

      <mm:redirect referids="$referids" page="$callerpage"/>
    </mm:compare>
    <mm:compare referid="name" value="">
	  <mm:import id="error">1</mm:import>
	</mm:compare>
</mm:notpresent>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="workspace.back" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids" page="$callerpage"/>
  </mm:compare>
</mm:present>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="search.sendsearchrequest" />" alt="<di:translate key="workspace.projectgroups" />" />
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
    <di:translate key="workspace.renameprojectgroup" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="changefolder" method="post" action="<mm:treefile page="/projectgroup/rename.jsp" objectlist="$includePath" referids="$referids"/>">

      <mm:node number="$workgroup">
        <table class="Font">
        <mm:fieldlist fields="name">
          <tr>
          <td><mm:fieldinfo type="guiname"/></td>
          <td><mm:fieldinfo type="input"/></td>
          </tr>
        </mm:fieldlist>
        </table>
	  </mm:node>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="submitted" value="1">
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.rename" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.back" />" />

	  <mm:present referid="error">
	    <p/>
	    <h1><di:translate key="workspace.projectnamenotempty" /></h1>
	  </mm:present>
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
