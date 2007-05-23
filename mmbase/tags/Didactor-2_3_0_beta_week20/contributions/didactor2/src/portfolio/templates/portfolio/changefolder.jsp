<%--
  This template changes a existing folder.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<jsp:directive.include file="/shared/setImports.jsp" />
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="portfolio.renamefolder" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="submitted"/>

<mm:import externid="contact">-1</mm:import>
<mm:compare referid="contact" value="-1" inverse="true">
  <mm:import id="user" reset="true"><mm:write referid="contact"/></mm:import>
</mm:compare>

<%-- Retrieve the currentfolder and set the new name --%>
<mm:present referid="submitted">
<mm:notpresent referid="action2">

   <%-- check if a foldername is given --%>
    <mm:import id="foldername" externid="_name"/>
    <mm:compare referid="foldername" value="" inverse="true">

      <mm:node number="$currentfolder">
        <mm:fieldlist type="all" fields="name">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
      </mm:node>

      <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
    </mm:compare>
    <mm:compare referid="foldername" value="">
	  <mm:import id="error">1</mm:import>
	</mm:compare>
</mm:notpresent>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="portfolio.back" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
  </mm:compare>
</mm:present>


<div class="rows">

<div class="navigationbar">
<div class="titlebar">
<img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="portfolio.portfolio" />"  alt="<di:translate key="portfolio.portfolio" />"/>
<di:translate key="portfolio.portfolio" />
</div>
</div>

<div class="folders">

<div class="folderHeader">
  <di:translate key="portfolio.portfolio" />
</div>
<div class="folderBody">
</div>

</div>

<div class="mainContent">

  <div class="contentHeader">
    <di:translate key="portfolio.renamefolder" />
  </div>

  <div class="contentBodywit">
<br/>
<br/>
<br/>
    <%-- Show the form --%>
    <form name="changefolder" method="post" action="<mm:treefile page="/portfolio/changefolder.jsp" objectlist="$includePath" referids="$referids"/>">

      <mm:node number="$currentfolder">
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
      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <input type="hidden" name="submitted" value="1">
      <mm:compare referid="contact" value="-1" inverse="true">
        <input type="hidden" name="contact" value="<mm:write referid="contact"/>"/>
      </mm:compare>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="portfolio.rename" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="portfolio.back" />" />

	  <mm:present referid="error">
	    <p/>
	    <h1><di:translate key="portfolio.foldernamenotempty" /></h1>
	  </mm:present>
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
