<%--
  This template creates a new folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="workspace.createfolder" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="detectclicks" from="parameters"/>
<mm:import externid="oldclicks" from="session"/>

<mm:present referid="detectclicks">
    <mm:compare referid="detectclicks" value="$oldclicks">
	<mm:redirect referids="$referids" page="$callerpage"/>
    </mm:compare>
    <mm:write session="oldclicks" referid="detectclicks"/>
</mm:present>

<%-- Check if the back button is not pressed --%>
<mm:notpresent referid="action2">
    <mm:import id="name" externid="_name"/>
    <mm:compare referid="name" value="" inverse="true">
        <mm:createnode type="workgroups" id="thisworkgroup">
           <mm:fieldlist type="all" fields="name">
               <mm:fieldinfo type="useinput" />
            </mm:fieldlist>
            <mm:setfield name="protected">0</mm:setfield>
        </mm:createnode>
        <mm:createrelation source="user" destination="thisworkgroup" role="related"/>
    <mm:redirect referids="$referids" page="$callerpage"/>

    </mm:compare>

    
    <mm:compare referid="name" value="">
	  <mm:import id="error">1</mm:import>
    </mm:compare>


</mm:notpresent>


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
    <di:translate key="workspace.createprojectgroup" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="createfolder" method="post" action="<mm:treefile page="/projectgroup/create.jsp" objectlist="$includePath" referids="$referids"/>">

      <table class="Font">
      <mm:fieldlist nodetype="workgroups" fields="name">
        <tr>
        <td><mm:fieldinfo type="guiname"/></td>
        <td><mm:fieldinfo type="input"/></td>
        </tr>
      </mm:fieldlist>
      </table>
    <script>
	document.forms['createfolder'].elements['_name'].focus();
    </script>
      <input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>">
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.create" />" />
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
