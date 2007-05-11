<%--
  This template shows the search page.
--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="search.search_caption" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="search_component" from="parameters"/>
<mm:import externid="search_query" from="parameters"/>
<mm:import externid="search_type" from="parameters"/>
 


<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
     <img src="<mm:treefile write="true" page="/gfx/icon_search.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="search.advancedsearch" />" alt="<di:translate key="search.advancedsearch" />" />
    <di:translate key="search.advancedsearch" />
    </div>
  </div>

  
  <div class="folders">

  <div class="folderHeader">
  <di:translate key="search.searchquery" />
  </div>

  <div class="folderBody">
    <di:translate key="search.search_for" /><br />
    <form action="index.jsp" method="GET">
    <input type="text" name="search_query" style="width: 150px" value="<mm:write referid="search_query"/>"/>
    <p>
    <select name="search_type" style="width: 150px">
      <mm:compare referid="search_type" value="all">
	<option value="all" selected="selected"><di:translate key="search.all_words" /></option>
      </mm:compare>
       <mm:compare referid="search_type" value="all" inverse="true">
	<option value="all"><di:translate key="search.all_words" /></option>
      </mm:compare>
      
      <mm:compare referid="search_type" value="any">
	<option value="any" selected="selected"><di:translate key="search.one_or_more_words" /></option>
      </mm:compare>
      <mm:compare referid="search_type" value="any" inverse="true">
        <option value="any"><di:translate key="search.one_or_more_words" /></option>
	</mm:compare>
		    
      <mm:compare referid="search_type" value="exact">
	<option value="exact" selected="selected"><di:translate key="search.exact_match" /></option>
      </mm:compare>

      <mm:compare referid="search_type" value="exact" inverse="true">
	<option value="exact"><di:translate key="search.exact_match" /></option>
      </mm:compare>


    </select>
    </p>
    <p>
    <di:translate key="search.search_in" /><br>
    <select name="search_component" style="width: 150px">
      <option value=""><di:translate key="search.all" /></option>
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
    <input type="submit" value="<di:translate key="search.search" />" />
    </p>
    </form>
   </div>

</div>
<div class="mainContent">
  <div class="contentHeader">
  <di:translate key="search.search_result" />
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
	<th class="listHeader"><di:translate key="search.component" /></th>
	<th class="listHeader"><di:translate key="search.location" /></th>
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

</mm:cloud>
</mm:content>
