<table class="subnav">
  <tr>
    <!-- overview flap -->	
    <mm:compare referid="flap" value="search" inverse="true">
      <td><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="search" /></mm:url>">Search</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="search">
      <td class="selected">Search</td>
    </mm:compare>

    <!-- comments flap -->
    <mm:compare referid="flap" value="lastchanges" inverse="true">
       <td><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="lastchanges" /></mm:url>">Last changed</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="lastchanges">
       <td class="selected">Last changed</td>
    </mm:compare>


    <!-- history flap -->
    <mm:compare referid="flap" value="stats" inverse="true">
      <td><a href="<mm:url referids="parameters,$parameters"><mm:param name="flap" value="stats" /></mm:url>">Statistics</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="stats">
      <td class="selected">Statistics</td>
    </mm:compare>
  </tr>
</table>
