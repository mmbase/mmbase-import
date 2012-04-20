<mm:present referid="ntype">
<form id="search" method="post" action="<mm:url referids="ntype,nr?,rkind?,dir?" />">
<fieldset>
  <div class="firstrow">
    <img src="img/mmbase-search.png" alt="search" width="21" height="20" />
    <h2>Search</h2>
  </div>
  <div class="row">
    <label for="conf_days">Days old</label>
    <input class="small" type="text" name="days" id="days" value="<mm:write referid="days" />" size="9" maxlength="9" />
  </div>
  <c:if test="${ntype ne 'typedef' and ntype ne 'typerel' and ntype ne 'reldef'}">
    <!-- see MMB-2032 : can cause a field not found exc. on searchinput -->
    <mm:fieldlist nodetype="$ntype" type="search">
      <mm:import id="fldname" reset="true"><mm:fieldinfo type="name" /></mm:import>
      <div class="row ${fldname}">
        <label for="mm_<mm:fieldinfo type="name" />"><mm:fieldinfo type="guiname" /></label>
        <c:choose>
          <c:when test="${fldname ne 'owner'}"><mm:fieldinfo type="searchinput" /></c:when>
          <c:otherwise>
            <mm:fieldinfo type="searchinput" datatype="eline" />
            <c:choose>
              <c:when test="${empty param._owner_search}"><input type="checkbox" name="_owner_search" id="mm_owner_search" /></c:when>
              <c:otherwise><input type="checkbox" name="_owner_search" id="mm_owner_search" checked="checked" /></c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>        
      </div>
    </mm:fieldlist>
  </c:if>
  <div class="lastrow"><input type="submit" name="search" value="Search" /></div>
</fieldset>
</form>
</mm:present><%-- /ntype --%>
