<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>POP</title>
  </mm:param>
</mm:treeinclude>
<!-- TODO where are the different roles described -->
<!-- TODO different things to do with different roles? -->

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="persoonlijk ontwikkelings plan" /> Persoonlijk ontwikkelings plan
  </div>		
</div>

<di:hasrole role="student">
<div class="folders">
  <div class="folderHeader">
    P.O.P.
  </div>
  <div class="folderBody">
     <mm:node number="$user">
     	<mm:relatedcontainer path="classrel,classes,educations">
     	  <mm:related>
            <div class="educationId">
              <mm:field name="educations.name"/>
            </div>
          </mm:related>
     	</mm:relatedcontainer>
     </mm:node>

  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
  </div>
  <div class="contentBodywit">
    <mm:node number="$user">
      <mm:relatedcontainer path="classrel,classes,educations">
        <di:table>
          <di:row>
            <di:headercell sortfield="educations.name" default="true">Titel opleiding</di:headercell>
            <di:headercell>Intake</di:headercell>
            <di:headercell>Gestart</di:headercell>
            <di:headercell>Voortgang</di:headercell>
            <di:headercell>&nbsp;</di:headercell>
          </di:row>

          <mm:related>
            <di:row>
              <di:cell>
                <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                           <mm:param name="education"><mm:field name="educations.number" /></mm:param>
                         </mm:treefile>">
                 <mm:field name="educations.name" />
                </a>
              </di:cell>
              <di:cell>vinkje</di:cell>
              <di:cell>vinkje</di:cell>
              <di:cell>leafinclude progressbar</di:cell>
              <di:cell>verder/start</di:cell>
            </di:row>
          </mm:related>
        </di:table>
      </mm:relatedcontainer>
    </mm:node>
  </div>
</div>
</div>
</di:hasrole>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
