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
  <mm:fieldlist nodetype="$ntype" type="search">
    <div class="row">
      <label for="mm_<mm:fieldinfo type="name" />"><mm:fieldinfo type="guiname" /></label>
      <mm:fieldinfo type="searchinput" />
    </div>
  </mm:fieldlist>
  <div class="lastrow"><input type="submit" name="search" value="Search" /></div>
</fieldset>
</form>
</mm:present><%-- /ntype --%>
