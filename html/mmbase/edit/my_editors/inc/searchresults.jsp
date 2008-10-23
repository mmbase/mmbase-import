<mm:present referid="ntype">
<mm:import externid="search" />
<mm:import id="colspan" />
<mm:fieldlist nodetype="$ntype" type="list" ><mm:import id="colspan" reset="true"><mm:size /></mm:import></mm:fieldlist>
<table id="results" border="0" cellpadding="0" cellspacing="0">
  
  <mm:listnodescontainer type="$ntype">
  <mm:size id="total" write="false" />    <%-- total # of this nodetype --%>
  <mm:sortorder field="number" direction="DOWN" />
  <mm:ageconstraint minage="0" maxage="$days" />
  
  <mm:present referid="search">
    <mm:context>
      <mm:fieldlist nodetype="$ntype" type="search">
        <mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
      </mm:fieldlist>             
    </mm:context>
  </mm:present>  

  <mm:size write="false" id="size" />   <%-- calculating # found after a search --%>
 
  <mm:maxnumber value="$max" />
  <mm:offset value="$offset" />
  
  <caption>
    <mm:write referid="size" /> out of <mm:write referid="total" /> of type
    <strong><mm:nodeinfo nodetype="$ntype" type="guitype" /></strong> (<mm:write referid="ntype" />)
  </caption>
  <tr>
    <th>&nbsp;</th>
    <th>&nbsp;</th>
    <mm:fieldlist nodetype="$ntype" type="list">
    <th scope="col"><mm:fieldinfo type="guiname" /></th>
    </mm:fieldlist>
    <th class="icon">
      <mm:maycreate type="$ntype"><a href="<mm:url page="new_object.jsp" referids="ntype,nr?,rkind?,dir?" />" title="a new node of this type"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a></mm:maycreate>
    </th>
  </tr>

  <mm:listnodes id="foundnode">
  <tr <mm:odd>class="odd"</mm:odd><mm:even>class="even"</mm:even>>
    <td class="icon"><mm:index offset="${offset + 1}" /></td>
    <td class="relicon">
      <mm:present referid="nr"> <%-- if there is a nr, there is a node and thus we are trying to find another to relate to --%>
        <mm:field name="number"><mm:compare value="$nr" inverse="true"><%-- don't relate to self --%>
        <mm:compare referid="dir" value="destination"><mm:maycreaterelation role="$rkind" source="nr" destination="foundnode">
          <a title="relate object (child)" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir">
            <mm:param name="rnr"><mm:field name="number" /></mm:param>
          </mm:url>"><img src="img/mmbase-relright.png" alt="relate &rarr;" width="21" height="20" border="0" /></a>
        </mm:maycreaterelation></mm:compare>
        <mm:compare referid="dir" value="source"><mm:maycreaterelation role="$rkind" source="foundnode" destination="nr">
          <a title="relate object (parent)" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir">
            <mm:param name="rnr"><mm:field name="number" /></mm:param>
          </mm:url>"><img src="img/mmbase-relleft.png" alt="&larr; relate" width="21" height="20" /></a>
        </mm:maycreaterelation></mm:compare>
        </mm:compare></mm:field>
      </mm:present>
    </td>
    <mm:fieldlist nodetype="$ntype" type="list">
    <td> <mm:first><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />"></mm:maywrite></mm:first><mm:fieldinfo type="guivalue" /><mm:first><mm:maywrite></a></mm:maywrite></mm:first> </td>
    </mm:fieldlist>
    <td class="icon">
      <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />"><img src="img/mmbase-edit.png" alt="edit" width="21" height="20" /></a></mm:maywrite>
      <mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />"><img src="img/mmbase-delete.png" alt="delete" width="21" height="20" /></a></mm:maydelete>
    </td>
  </tr>
  </mm:listnodes>
  <tr>
    <td colspan="${colspan + 3}"<mm:compare referid="size" value="0" inverse="true"> class="batches"</mm:compare>>
      <mm:compare referid="size" value="0">
        <div class="message">
          <h3>No nodes found</h3>
          <mm:compare referid="total" value="0"><p>There are no nodes of this type.</p></mm:compare>
          <mm:compare referid="total" value="0" inverse="true"><p>Try other search criteria, adjust 'Days old' for example.</p></mm:compare>
        </div>
      </mm:compare>
     
      <p class="pager">
		<mm:url id="search_str" referids="ntype,search,days,nr?,rkind?,dir?" write="false">
		  <mm:fieldlist nodetype="$ntype" type="search">
			<mm:fieldinfo type="reusesearchinput" />
		  </mm:fieldlist>
		</mm:url>
        
        <c:if test="${offset != 0}">
          <strong><a href="<mm:url referid="search_str">
            <mm:param name="offset">${(offset - max)}</mm:param>
          </mm:url>">&laquo;&laquo;</a></strong>
        </c:if>
        
        <fmt:formatNumber var="iend" type="number" pattern="#" value="${(size / max) - 0.51}" />
        <c:if test="${iend < 0}"><c:set var="iend" value="0" /></c:if>
        <c:forEach var="i" begin="0" end="${iend}">
          <c:choose>
            <c:when test="${(i * max) == offset}">
              <strong>${(i + 1)}</strong>
            </c:when>
            <c:otherwise>
              <a href="<mm:url referid="search_str">
                <mm:param name="offset">${(i * max)}</mm:param>
              </mm:url>">${(i + 1)}</a>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        
        <c:if test="${(size - offset) > max}">
          <strong><a href="<mm:url referid="search_str">
            <mm:param name="offset">${(max + offset)}</mm:param>
          </mm:url>">&raquo;&raquo;</a></strong>
        </c:if>
      </p>
      
    </td>
  </tr>
  </mm:listnodescontainer>
  
</table>
</mm:present>
