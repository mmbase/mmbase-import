<ul id="tabnav">
    <!-- overview flap -->	
    <mm:compare referid="flap" value="search" inverse="true">
      <li><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="search" /></mm:url>">Search</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="search">
      <li><a href="#" class="active">Search</a></li>
    </mm:compare>

    <!-- comments flap -->
    <mm:compare referid="flap" value="lastchanges" inverse="true">
       <li><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="lastchanges" /></mm:url>">Last changed</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="lastchanges">
       <li><a href="#" class="active">Last changed</a></li>
    </mm:compare>


    <!-- history flap -->
    <mm:compare referid="flap" value="stats" inverse="true">
      <li><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="stats" /></mm:url>">Statistics</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="stats">
      <li><a href="#" class="active">Statistics</a></li>
    </mm:compare>
</ul>
