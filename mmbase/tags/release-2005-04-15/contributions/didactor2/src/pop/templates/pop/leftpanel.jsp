<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">

<%-- folder is open --%>
<mm:compare referid="currentfolder" value="-1">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDEROPENED" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="-1" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDERCLOSED" />" />
</mm:compare>
<a href="index.jsp">Competenties</a><br/>

     <mm:node number="$user">
     	<mm:relatedcontainer path="pop,profiles">
     	  <mm:related>

            <mm:import id="currentnumber"><mm:field name="profiles.number"/></mm:import>

            <%-- folder is open --%>
            <mm:compare referid="currentprofile" referid2="currentnumber">
               &nbsp;<img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDEROPENED" />" />
            </mm:compare>

            <%-- folder is closed --%>
            <mm:compare referid="currentprofile" referid2="currentnumber" inverse="true">
              &nbsp;<img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDERCLOSED" />" />
            </mm:compare>

            <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids">
                <mm:param name="currentprofile"><mm:field name="profiles.number" /></mm:param>
		        </mm:treefile>">
			  <mm:field name="profiles.name" />
            </a><br />

          </mm:related>
     	</mm:relatedcontainer>
     </mm:node>

<%-- folder is open --%>
<mm:compare referid="currentfolder" value="1">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDEROPENED" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="1" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDERCLOSED" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="currentfolder">1</mm:param>
  </mm:treefile>">Voortgangsmonitor
</a><br />

<%-- folder is open --%>
<mm:compare referid="currentfolder" value="2">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDEROPENED" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="2" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" alt="<fmt:message key="FOLDERCLOSED" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="currentfolder">2</mm:param>
  </mm:treefile>">Persoonlijke taken
</a><br />


  </div>
</div>