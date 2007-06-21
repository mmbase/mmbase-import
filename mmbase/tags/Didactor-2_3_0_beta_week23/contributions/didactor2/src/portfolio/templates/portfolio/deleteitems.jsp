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
    <title><di:translate key="portfolio.deletefolderitems" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>

<mm:import externid="ids"/>

<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>

<mm:import externid="action1"/>
<mm:import externid="action2"/>

<mm:import externid="contact">-1</mm:import>
<mm:compare referid="contact" value="-1" inverse="true">
  <mm:import id="user" reset="true"><mm:write referid="contact"/></mm:import>
</mm:compare>

<%-- Check if the yes button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="portfolio.deleteyes" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">

    <mm:node number="$currentfolder">
      <mm:relatednodescontainer type="object">
        <mm:constraint field="number" referid="list" operator="IN"/>
        <mm:relatednodes>

          <mm:remove referid="nodetype"/>
          <mm:import id="nodetype"><mm:nodeinfo type="type"/></mm:import>

          <%-- delete the object also --%>
          <mm:compare referid="nodetype" valueset="attachments,urls">
            <mm:deletenode deleterelations="true"/>
          </mm:compare>

          <%-- only delete the relation --%>
          <mm:compare referid="nodetype" valueset="pages,chatlogs">
            <mm:listrelationscontainer type="folders">
              <mm:constraint field="folders.number" value="$currentfolder"/>
              <mm:listrelations>
                <mm:deletenode/>
              </mm:listrelations>
            </mm:listrelationscontainer>
          </mm:compare>

        </mm:relatednodes>
      </mm:relatednodescontainer>
    </mm:node>

    <%-- Show the previous page --%>
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>

  </mm:compare>
</mm:present>


<%-- Check if the no button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="portfolio.deleteno" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentfolder,typeof,contact?" page="$callerpage"/>
  </mm:compare>
</mm:present>


<div class="rows">

<div class="rows">

<div class="navigationbar">
<div class="titlebar">
<img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="portfolio.portfolio" />" alt="<di:translate key="portfolio.portfolio" />"/>
<di:translate key="portfolio.portfolio" />
</div>
</div>

<div class="folders">

<div class="folderHeader">
<di:translate key="portfolio.portfolio" />
</div>
<div class="folderBody"></div>
</div>

<div class="mainContent">

  <div class="contentHeader">
  	<di:translate key="portfolio.deletefolderitems" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletefolderitems" method="post" action="<mm:treefile page="/portfolio/deleteitems.jsp" objectlist="$includePath" referids="$referids"/>">
      <di:translate key="portfolio.deletefolderitemsyesno" />
      <p/>

      <!-- TODO show data in near future -->
      <table class="Font">
       </table>

      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <mm:compare referid="contact" value="-1" inverse="true">
        <input type="hidden" name="contact" value="<mm:write referid="contact"/>"/>
      </mm:compare>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="portfolio.deleteyes" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="portfolio.deleteno" />" />
    </form>
  </div>
</div>
</div>
</mm:cloud>
</mm:content>
