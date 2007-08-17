<%--
  This template moves a folder item from one folder to another.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>


<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="foldernumber"/>

<mm:import externid="ids"/>


<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>

<mm:node number="$currentfolder" id="mycurrentfolder"/>


<%-- Check if the move button is pressed --%>
<mm:present referid="action1">
  <mm:import id="action1text"><di:translate key="workspace.move" /></mm:import>
  <mm:compare referid="action1" referid2="action1text">


    <%-- Delete the content from the current folder --%>
    <mm:node referid="mycurrentfolder">

      <!-- TODO determine in the future if there is a shorter way to code this -->
      <mm:relatednodes type="attachments" id="myattachements">
        <mm:remove referid="itemno"/>
        <mm:import id="itemno" jspvar="itemNo"><mm:field name="number"/></mm:import>
        <%
           int pos = list.indexOf( itemNo );
           if ( pos >= 0 ) {
        %>
          <mm:listrelations>
            <mm:deletenode/>
          </mm:listrelations>

        <%
           }
        %>
      </mm:relatednodes>

      <mm:relatednodes type="urls" id="myurls">
        <mm:remove referid="itemno"/>
        <mm:import id="itemno" jspvar="itemNo"><mm:field name="number"/></mm:import>
        <%
           int pos = list.indexOf( itemNo );
           if ( pos >= 0 ) {
        %>
          <mm:listrelations>
            <mm:deletenode/>
          </mm:listrelations>

        <%
           }
        %>
      </mm:relatednodes>

      <mm:relatednodes type="pages" id="mypages">
        <mm:remove referid="itemno"/>
        <mm:import id="itemno" jspvar="itemNo"><mm:field name="number"/></mm:import>
        <%
           int pos = list.indexOf( itemNo );
           if ( pos >= 0 ) {
        %>
          <mm:listrelations>
            <mm:deletenode/>
          </mm:listrelations>

        <%
           }
        %>
      </mm:relatednodes>

      <mm:relatednodes type="chatlogs" id="mychatlogs">
        <mm:remove referid="itemno"/>
        <mm:import id="itemno" jspvar="itemNo"><mm:field name="number"/></mm:import>
        <%
           int pos = list.indexOf( itemNo );
           if ( pos >= 0 ) {
        %>
          <mm:listrelations>
            <mm:deletenode/>
          </mm:listrelations>

        <%
           }
        %>
      </mm:relatednodes>

    </mm:node>

    <%-- Move the content to the new folder --%>
	<mm:node number="$foldernumber" id="mynewfolder">
              <%
                 java.util.Iterator it = list.iterator();
                 while ( it.hasNext() ) {
              %>
                 <mm:remove referid="itemno"/>
                 <mm:remove referid="mynewitems"/>
                 <mm:import id="itemno"><%=it.next()%></mm:import>
                 <mm:node number="$itemno" id="mynewitems"/>

                 <mm:createrelation role="related" source="mynewfolder" destination="mynewitems"/>
              <%
                 }
	          %>
	  </mm:node>

    <mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>

  </mm:compare>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:present referid="action2">
  <mm:import id="action2text"><di:translate key="workspace.back" /></mm:import>
  <mm:compare referid="action2" referid2="action2text">
    <mm:redirect referids="$referids,currentfolder,typeof" page="$callerpage"/>
  </mm:compare>
</mm:present>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <mm:compare referid="typeof" value="1">
      <img src="<mm:treefile write="true" page="/gfx/icon_mydocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<di:translate key="workspace.mydocuments" />" />
      <di:translate key="workspace.mydocuments" />
    </mm:compare>
    <mm:compare referid="typeof" value="2">
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<di:translate key="workspace.shareddocuments" />" />
	  <di:translate key="workspace.shareddocuments" />
    </mm:compare>
    <mm:compare referid="typeof" value="3">
      <img src="<mm:treefile write="true" page="/gfx/icon_shareddocs.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<di:translate key="workspace.shareddocuments" />" />
	  <di:translate key="workspace.workgroupdocuments" />
    </mm:compare>

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
    <di:translate key="workspace.moveselected" />
  </div>
  <div class="contentBodywit">
    <%-- Show the form --%>
    <form name="moveitems" method="post" action="<mm:treefile page="/virtualclassroom/frontoffice/workspace/moveitems.jsp" objectlist="$includePath" referids="$referids"/>">
      <table class="Font">

      <tr>
      <td>
      <di:translate key="workspace.movesingle" /> <di:translate key="workspace.filesto" />
      </td>
      </tr>

      <tr>
      <td>
      <select name="foldernumber">
        <mm:node number="$user">
          <mm:relatednodes type="workspaces">
            <mm:relatednodescontainer type="folders">
              <mm:relatednodes>
                <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                <option value="<mm:field name="number"/>"><di:translate key="workspace.mydocuments" /> &gt; <mm:field name="name"/></option>
              </mm:relatednodes>
            </mm:relatednodescontainer>
          </mm:relatednodes>
            <mm:relatednodes type="workgroups">
                <mm:field name="name" id="workgroupname">
                  <mm:relatednodes type="workspaces">
                    <mm:relatednodescontainer type="folders">
                      <mm:relatednodes>
                        <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                        <option value="<mm:field name="number"/>"><mm:write referid="workgroupname"/> &gt; <mm:field name="name"/></option>
                      </mm:relatednodes>
                    </mm:relatednodescontainer>
                  </mm:relatednodes>
                </mm:field>
             </mm:relatednodes>
        </mm:node>
        <mm:present referid="class">
          <mm:node number="$class" notfound="skipbody">
            <mm:field name="name" id="classname">
            
            <mm:relatednodes type="workspaces">
              <mm:relatednodescontainer type="folders">
                <mm:relatednodes>
                  <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                  <option value="<mm:field name="number"/>"><mm:write referid="classname"/> &gt; <mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
            </mm:field>
          </mm:node>
        </mm:present>
          <mm:node number="$user">
            <mm:relatednodes type="portfolios" constraints="m_type = 0">
              <mm:relatednodescontainer type="folders">
                <mm:relatednodes>
                  <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                  <option value="<mm:field name="number"/>"><di:translate key="portfolio.developmentportfolio" /> &gt; <mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
            <mm:relatednodes type="portfolios" constraints="m_type = 1">
              <mm:relatednodescontainer type="folders">
                <mm:relatednodes>
                  <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                  <option value="<mm:field name="number"/>"><di:translate key="portfolio.assessmentportfolio" /> &gt; <mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
            <mm:relatednodes type="portfolios" constraints="m_type = 2">
              <mm:relatednodescontainer type="folders">
                <mm:relatednodes>
                  <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
                  <option value="<mm:field name="number"/>"><di:translate key="portfolio.showcaseportfolio" /> &gt; <mm:field name="name"/></option>
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:relatednodes>
          </mm:node>
      </td>
      </tr>

      <table>

      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>"/>
      <input type="hidden" name="typeof" value="<mm:write referid="typeof"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="workspace.move" />" />
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="workspace.back" />" />
    </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
