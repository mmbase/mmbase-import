<%--
  This template shows the search page.
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<fmt:bundle basename="nl.didactor.component.search.SearchMessageBundle">
<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Zoeken</title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="search_component" from="parameters"/>
<mm:import externid="search_query" from="parameters"/>
<mm:import externid="search_type" from="parameters"/>
 


<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
     <img src="<mm:treefile write="true" page="/gfx/icon_search.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="ADVANCEDSEARCH"/>"/>
    <fmt:message key="ADVANCEDSEARCH"/>
    </div>
  </div>

  
  <div class="folders">

  <div class="folderHeader">
  <fmt:message key="SEARCHQUERY"/>
  </div>

  <div class="folderBody">
    <di:translate id="search_for">Zoeken op:</di:translate><br />
    <form action="index.jsp" method="GET">
    <input type="text" name="search_query" style="width: 150px" value="<mm:write referid="search_query"/>"/>
    <p>
    <select name="search_type" style="width: 150px">
      <mm:compare referid="search_type" value="all">
	<option value="all" selected="selected"><di:translate id="all_words">Alle woorden</di:translate></option>
      </mm:compare>
       <mm:compare referid="search_type" value="all" inverse="true">
	<option value="all"><di:translate id="all_words">Alle woorden</di:translate></option>
      </mm:compare>
      
      <mm:compare referid="search_type" value="any">
	<option value="any" selected="selected"><di:translate id="one_or_more_words">&Eacute;&eacute;n of meer woorden</di:translate></option>
      </mm:compare>
      <mm:compare referid="search_type" value="any" inverse="true">
        <option value="any"><di:translate id="one_or_more_words">&Eacute;&eacute;n of meer woorden</di:translate></option>
	</mm:compare>
		    
      <mm:compare referid="search_type" value="exact">
	<option value="exact" selected="selected"><di:translate id="exact_match">Exacte resultaten</di:translate></option>
      </mm:compare>

      <mm:compare referid="search_type" value="exact" inverse="true">
	<option value="exact"><di:translate id="exact_match">Exacte resultaten</di:translate></option>
      </mm:compare>


    </select>
    </p>
    <p>
    <di:translate id="search_in">Zoeken in:</di:translate><br>
    <select name="search_component" style="width: 150px">
      <option value=""><di:translate id="all">Alles</di:translate></option>
      <%-- hier door alle componenten itereren ... --%>
      <mm:node number="$provider">
       <mm:related path="settingrel,components">
        <mm:node element="components">
         <mm:field name="name" id="name" write="false"/>
         <mm:treeinclude page="/$name/search/menuitem.jsp" objectlist="$includePath" referids="$referids">
          <mm:param name="name"><mm:field name="name" /></mm:param>
          <mm:param name="number"><mm:field name="number" /></mm:param>
          <mm:param name="type">option</mm:param>
          <mm:param name="scope">provider</mm:param>
         </mm:treeinclude>
        </mm:node>
       </mm:related>
      </mm:node>

    </select>
    </p>
    <p>
    <input type="submit" value="<di:translate id="search">zoek</di:translate>" />
    </p>
    </form>
   </div>

</div>
<div class="mainContent">
  <div class="contentHeader">
  <di:translate id="search_result">zoek resultaat</di:translate>
  </div>

  <div class="contentSubHeader">
  </div>

  <div class="contentBody">
    <%  if (
	    request.getParameter("search_query") != null && 
	    request.getParameter("search_query").length() > 0 && 
	    request.getParameter("search_type") != null && 
	    request.getParameter("search_component") != null
        ) { %>
      <table class="listTable">
      <tr>
	<th class="listHeader"><di:translate id="component">Onderdeel</di:translate></th>
	<th class="listHeader"><di:translate id="location">Plek</di:translate></th>
      </tr>
	<%-- include search results  for each component --%>
      <mm:node number="$provider">
       <mm:related path="settingrel,components">
        <mm:node element="components">
         <mm:field name="name" id="name2" write="false"/>
          <mm:treeinclude page="/$name2/search/results.jsp" objectlist="$includePath" referids="$referids">
           <mm:param name="name"><mm:field name="name" /></mm:param>
           <mm:param name="number"><mm:field name="number" /></mm:param>
           <mm:param name="type">option</mm:param>
           <mm:param name="scope">provider</mm:param>
	   <mm:param name="search_query"><mm:write referid="search_query"/></mm:param>
	   <mm:param name="search_type"><mm:write referid="search_type"/></mm:param>
          </mm:treeinclude>
        </mm:node>
       </mm:related>
      </mm:node>
      </table>

	

    <% } %>
    
  </div>

</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</fmt:bundle>
</mm:cloud>
</mm:content>
