<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="getids.jsp" %>

<%@include file="/education/wizards/roles_defs.jsp" %>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">

<mm:import externid="t_rights"/>
<mm:islessthan inverse="true" referid="t_rights" referid2="RIGHTS_RW">
  <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids">
             <mm:param name="t_mode">true</mm:param>
           </mm:treefile>"><u>to teacher mode</u>
  </a><br/><br/>
</mm:islessthan>
     <mm:node number="$student">
       <%-- folder is open --%>
       <mm:compare referid="currentfolder" value="-1">
         <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
       </mm:compare>
       <%-- folder is closed --%>
       <mm:compare referid="currentfolder" value="-1" inverse="true">
         <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />"  alt="<di:translate key="pop.folderclosed" />" />
       </mm:compare>
       <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids"/>"><di:translate key="pop.competencies" /></a><br/>
     	<mm:relatedcontainer path="pop,profiles">
     	  <mm:related>

            <mm:import id="currentnumber"><mm:field name="profiles.number"/></mm:import>

            <%-- folder is open --%>
            <mm:compare referid="currentprofile" referid2="currentnumber">
               &nbsp;<img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
            </mm:compare>

            <%-- folder is closed --%>
            <mm:compare referid="currentprofile" referid2="currentnumber" inverse="true">
              &nbsp;<img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
            </mm:compare>

            <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids">
                       <mm:param name="currentprofile"><mm:field name="profiles.number" /></mm:param>
		        </mm:treefile>">
			  <mm:field name="profiles.name" />
            </a><br />

          </mm:related>
     	</mm:relatedcontainer>
     </mm:node>

<%-- folder is open --%>
<mm:compare referid="currentfolder" value="1">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="1" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids">
    <mm:param name="currentfolder">1</mm:param>
  </mm:treefile>"><di:translate key="pop.progressmonitor" />
</a><br />

<%-- folder is open --%>
<mm:compare referid="currentfolder" value="2">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="2" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids">
    <mm:param name="currentfolder">2</mm:param>
  </mm:treefile>"><di:translate key="pop.todoitems" />
</a><br />

  </div>
</div>

</mm:cloud>
</mm:content>
