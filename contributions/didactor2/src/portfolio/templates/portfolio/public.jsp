<%--
  This template shows the personal workspace (in dutch: persoonlijke werkruimte).
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="portfolio.mydocuments" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import id="myuser" externid="contact"/>

<%-- Determine if my documents or shared documents is started --%>
<mm:import externid="typeof">-1</mm:import>
<mm:import externid="currentfolder">-1</mm:import>

<%-- Get the first folder if no folder selected --%>
<mm:compare referid="typeof" value="-1" inverse="true">
  <mm:compare referid="currentfolder" value="-1">
    <mm:node referid="myuser" >
      <mm:relatednodes type="portfolios" constraints="[type]=${typeof}">
        <mm:relatednodes type="folders" role="posrel" orderby="posrel.pos">
          <mm:first>
            <mm:remove referid="currentfolder"/>
            <mm:import id="currentfolder"><mm:field name="number"/></mm:import>
          </mm:first>
        </mm:relatednodes>
      </mm:relatednodes>
    </mm:node>
  </mm:compare>
</mm:compare>


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
  <div class="folderBody">
  </div>
</div>

<%-- folder is open --%>
<mm:compare referid="typeof" value="-1">
  <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="typeof" value="-1" inverse="true">
  <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
</mm:compare>

<a href="index.jsp">Portfolio cockpit</a><br/>


<mm:node referid="myuser" >
  <mm:relatednodes type="portfolios" orderby="type">


<mm:import id="currentportfolionumber"><mm:field name="number"/></mm:import>
<mm:import id="currentportfoliotype"><mm:field name="type"/></mm:import>
<mm:remove referid="currentportfolioisopen"/>

<mm:compare referid="currentportfoliotype" value="0">

<%-- folder is open --%>
<mm:compare referid="typeof" value="0">
  <mm:import id="currentportfolioisopen">true</mm:import>
  <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />"  alt="<di:translate key="portfolio.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="typeof" value="0" inverse="true">
  <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">0</mm:param></mm:treefile>">Ontwikkelingsgericht portfolio</a><br/>

</mm:compare>
<mm:compare referid="currentportfoliotype" value="1">

<%-- folder is open --%>
<mm:compare referid="typeof" value="1">
  <mm:import id="currentportfolioisopen">true</mm:import>
  <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="typeof" value="1" inverse="true">
  <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">1</mm:param></mm:treefile>">Assessment portfolio</a><br/>

</mm:compare>
<mm:compare referid="currentportfoliotype" value="2">

<%-- folder is open --%>
<mm:compare referid="typeof" value="2">
  <mm:import id="currentportfolioisopen">true</mm:import>
  <img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="typeof" value="2" inverse="true">
  <img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />" alt="<di:translate key="portfolio.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="index.jsp" objectlist="$includePath" referids="$referids,contact?"><mm:param name="typeof">2</mm:param></mm:treefile>">Showcase portfolio</a><br/>

</mm:compare>


  <mm:present referid="currentportfolioisopen">

    <mm:relatednodes role="posrel" type="folders" orderby="posrel.pos">

      <mm:import id="currentnumber"><mm:field name="number"/></mm:import>

      <%-- folder is open --%>
      <mm:compare referid="currentfolder" referid2="currentnumber">
        &nbsp;<img src="<mm:treefile page="/portfolio/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderopened" />" alt="<di:translate key="portfolio.folderopened" />" />
      </mm:compare>

      <%-- folder is closed --%>
      <mm:compare referid="currentfolder" referid2="currentnumber" inverse="true">

        &nbsp;<img src="<mm:treefile page="/portfolio/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="portfolio.folderclosed" />"  alt="<di:translate key="portfolio.folderclosed" />" />
      </mm:compare>

      <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,contact?">
			 <mm:param name="currentfolder"><mm:field name="number" /></mm:param>
		 <mm:param name="typeof"><mm:write referid="currentportfoliotype"/></mm:param>
		       </mm:treefile>">
			<mm:field name="name" />
      </a>
    </mm:relatednodes>

</mm:present>

  </mm:relatednodes>
</mm:node>

</div>

</div>

<div class="mainContent">

<mm:compare referid="typeof" value="-1" inverse="true">

<form action="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,typeof,contact?"/>" method="POST">
    <input type="hidden" name="currentfolder" value="<mm:write referid="currentfolder"/>">

  <div class="contentHeader">

    <mm:node number="$currentfolder" notfound="skip">
      <mm:field name="name"/>
    </mm:node>

  </div>

  <div class="contentSubHeader"></div>

  <div class="contentBodywit">
    <mm:import id="gfx_attachment"><mm:treefile page="/portfolio/gfx/mijn documenten.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_url"><mm:treefile page="/portfolio/gfx/bronnen.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_page"><mm:treefile page="/portfolio/gfx/pagina.gif" objectlist="$includePath" referids="$referids" /></mm:import>
    <mm:import id="gfx_chatlog"><mm:treefile page="/portfolio/gfx/chatverslag.gif" objectlist="$includePath" referids="$referids" /></mm:import>

    <mm:node number="$currentfolder" notfound="skip">

      <mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

      <%-- Show also the nodes below in the table --%>
      <mm:relatednodes type="attachments" id="myattachments">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="urls" id="myurls">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="pages" id="mypages">
        <mm:remove referid="objectnumber"/>
        <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
        <%
          linkedlist.add( objectnumber );
        %>
      </mm:relatednodes>
      <mm:relatednodes type="chatlogs" id="mychatlogs">
        <mm:remove referid="objectnumber"/>
	    <mm:import id="objectnumber" jspvar="objectnumber"><mm:field name="number"/></mm:import>
	    <%
	      linkedlist.add( objectnumber );
	    %>
      </mm:relatednodes>


      <mm:listnodescontainer type="object">
        <mm:constraint field="number" referid="linkedlist" operator="IN"/>

        <di:table maxitems="10">

          <di:row>
            <di:headercell><input type="checkbox" onclick="selectAllClicked(this.form, this.checked)"></input></di:headercell>
            <di:headercell><di:translate key="portfolio.type" /></di:headercell>
            <di:headercell><di:translate key="portfolio.title" /></di:headercell>
            <di:headercell><di:translate key="portfolio.reactions" /></di:headercell>
            <di:headercell><di:translate key="portfolio.date" /></di:headercell>
            <di:headercell><di:translate key="portfolio.description" /></di:headercell>

          </di:row>

          <mm:listnodes>
            <mm:import id="mayread" reset="true">false</mm:import>
                <mm:relatednodes type="portfoliopermissions" max="1">
                    <mm:field name="readrights">
                        <mm:compare value="2">
                            <mm:list nodes="$user" path="people1,classes,people2,portfolios,folders"  constraints="folders.number=$currentfolder" max="1">
                                <mm:import id="mayread" reset="true">true</mm:import>
                            </mm:list>
                        </mm:compare>
                         <mm:compare value="3">
                            <di:hasrole role="teacher">
                                <mm:list nodes="$user" path="people1,classes,people2,portfolios,folders"  constraints="folders.number=$currentfolder" max="1">
                                    <mm:import id="mayread" reset="true">true</mm:import>
                                </mm:list>
                            </di:hasrole>
                        </mm:compare>
                        <mm:compare value="3">
                            <mm:import id="mayread" reset="true">true</mm:import>
                        </mm:compare>
                        <mm:compare value="4">
                            <mm:import id="mayread" reset="true">true</mm:import>
                        </mm:compare>
                    </mm:field>
               </mm:relatednodes>
               <mm:list nodes="$user" path="people,portfolios,folders" constraints="folders.number=$currentfolder" max="1">
                  <mm:import id="mayread" reset="true">true</mm:import>
               </mm:list> 
            <mm:compare referid="mayread" value="true">

            <mm:field name="number" id="itemnumber">
                <mm:listnodes type="daymarks" constraints="mark <= $itemnumber" orderby="mark" directions="down" max="1">
                    <mm:field name="daycount" jspvar="dayCount" vartype="Integer">
                        <mm:import id="itemdate" reset="true"><%= dayCount.intValue()*60*60*24 %></mm:import>
                    </mm:field>
                </mm:listnodes>
            </mm:field>

            <di:row>
              <di:cell><input type="checkbox" name="ids" value="<mm:field name="number"/>"></input></di:cell>

              <mm:remove referid="link"/>
              <mm:import id="link"><a href="<mm:treefile page="/portfolio/showitem.jsp" objectlist="$includePath" referids="$referids">
                                  <mm:param name="currentitem"><mm:field name="number"/></mm:param>
                                  <mm:param name="currentfolder"><mm:write referid="currentfolder"/></mm:param>
                                  <mm:param name="typeof"><mm:write referid="typeof"/></mm:param>
                                </mm:treefile>">
              </mm:import>

              <mm:remove referid="objecttype"/>
              <mm:import id="objecttype"><mm:nodeinfo type="type"/></mm:import>
              <mm:compare referid="objecttype" value="attachments">
                <di:cell><img src="<mm:write referid="gfx_attachment"/>" title="<di:translate key="portfolio.folderitemtypedocument" />" alt="<di:translate key="portfolio.folderitemtypedocument" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="title" /></a></di:cell>
                <di:cell><mm:countrelations type="forummessages"/></di:cell>
                <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
              </mm:compare>
              <mm:compare referid="objecttype" value="urls">
                <mm:import id="urllink" jspvar="linkText"><mm:field name="url"/></mm:import>
			    <%
			      if ( linkText.indexOf( "http://" ) == -1 ) {
			    %>
			      <mm:remove referid="urllink"/>
			  	  <mm:import id="urllink">http://<mm:field name="url"/></mm:import>
			  	<%
			  	  }
			  	%>
                <di:cell><img src="<mm:write referid="gfx_url"/>" title="<di:translate key="portfolio.folderitemtypeurl" />" alt="<di:translate key="portfolio.folderitemtypeurl" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                 <di:cell><mm:countrelations type="forummessages"/></di:cell>
                 <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                <di:cell><mm:field name="description" /></di:cell>
              </mm:compare>
              <mm:compare referid="objecttype" value="pages">
                <di:cell><img src="<mm:write referid="gfx_page"/>" title="<di:translate key="portfolio.folderitemtypepage" />" alt="<di:translate key="portfolio.folderitemtypepage" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><mm:field name="name" /></a></di:cell>
                 <di:cell><mm:countrelations type="forummessages"/></di:cell>
                 <di:cell><mm:write referid="itemdate"><mm:time format="d/M/yyyy"/></mm:write></di:cell>
                <di:cell><mm:field name="text" /></di:cell>
              </mm:compare>
              <mm:compare referid="objecttype" value="chatlogs">
                <di:cell><img src="<mm:write referid="gfx_chatlog"/>" title="<di:translate key="portfolio.folderitemtypechatlog" />" alt="<di:translate key="portfolio.folderitemtypechatlog" />" /></di:cell>
                <di:cell><mm:write referid="link" escape="none"/><di:translate key="portfolio.folderitemtypechatlog" /><mm:field name="number"/></a></di:cell>
                  <di:cell><mm:countrelations type="forummessages"/></di:cell>
                <di:cell><mm:field name="date"></mm:field></di:cell> <!-- TODO show correct date -->
                <di:cell>&nbsp;</di:cell> <!-- TODO still empty -->
              </mm:compare>

  		    </di:row>
            </mm:compare>
  		  </mm:listnodes>

        </di:table>

      </mm:listnodescontainer>

    </mm:node>

  </div>

</form>

</mm:compare>


<mm:compare referid="typeof" value="-1">

  <div class="contentBodywit">

<mm:import externid="edit"/>
<mm:compare referid="edit" value="true" inverse="true">

<mm:node referid="myuser">

Mijn gegevens:

<table class="Font">
  <tr>
    <td>
      <table class="Font">
        <tr>
          <td>Initialen:</td>
          <td><mm:field name="initials"/></td>
        </tr>
        <tr>
          <td>Voornaam:</td>
          <td><mm:field name="firstname"/></td>
        </tr>
        <tr>
          <td>Achternaam:</td>
          <td><mm:field name="lastname"/></td>
        </tr>
        <tr>
          <td>Adres:</td>
          <td><mm:field name="address"/></td>
        </tr>
        <tr>
          <td>Postcode:</td>
          <td><mm:field name="zipcode"/></td>
        </tr>
        <tr>
          <td>Plaats:</td>
          <td><mm:field name="city"/></td>
        </tr>
        <tr>
          <td>Telefoonnummer:</td>
          <td><mm:field name="telephone"/></td>
        </tr>
      </table>
    <td>
      <mm:node number="$myuser">
        <mm:relatednodes type="images">
          <img src="<mm:image template="s(300)"/>"/>
        </mm:relatednodes>
      </mm:node>
    </td>
  <tr>
  <mm:compare referid="myuser" value="$user">
  <tr>
    <td>
      <a href="index.jsp?edit=true">edit</a>
    </td>
  <tr>
  </mm:compare>
</table>


</mm:node>


</mm:compare>


<mm:compare referid="edit" value="true">


<form name="edituser" method="post" enctype="multipart/form-data" action="<mm:treefile page="/portfolio/changeuser.jsp" objectlist="$includePath"/>">
  <%-- parameter to indicate theres info to be uploaded>
  <input type="hidden" name="processupload" value="true"/>
	<input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>" --%>
  

<table class="Font">
  <tr>
    <td>
      <table class="Font">
        <mm:node number="$user">
          <mm:fieldlist fields="initials,firstname,lastname,address,zipcode,city,telephone">
            <tr>
              <td><mm:fieldinfo type="guiname"/></td>
              <td><mm:fieldinfo type="input"/></td>
            </tr>
          </mm:fieldlist>
          <tr>
            <td>
              <mm:relatednodes type="images">
                <mm:import id="image_present" reset="true"/>
                Nieuwe pasfoto
              </mm:relatednodes>
              <mm:present referid="image_present" inverse="true">
                Pasfoto
              </mm:present>
            </td>
            <td><input type="file" name="_handle"/></td>
          </tr>
        </mm:node>
      </table>
    </td>
    <td>
      <mm:node number="$user">
        <mm:relatednodes type="images">
          <img src="<mm:image template="s(300)"/>"/>
        </mm:relatednodes>
      </mm:node>
    </td>
  </tr>
</table>

<input class="formbutton" type="submit" name="change" value="<di:translate key="portfolio.save"/>" />

</form>

<form name="cancel" method="POST" action="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath"/>">
  <input class="formbutton" type="submit" name="cancel" value="<di:translate key="portfolio.back"/>" />
</form>

</mm:compare>







</mm:compare>


</div>
</div>
<script>

      function selectAllClicked(frm, newState) {
	  if (frm.elements['ids'].length) {
	    for(var count =0; count < frm.elements['ids'].length; count++ ) {
		var box = frm.elements['ids'][count];
		box.checked=newState;
	    }
	  }
	  else {
	      frm.elements['ids'].checked=newState;
	  }
      }

</script>


<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
