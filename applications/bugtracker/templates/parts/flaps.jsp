<mm:import id="template">fullview.jsp</mm:import>
<table class="subnav">

  <tr>
    <td width="25">
      <a href="<mm:url referids="parameters,$parameters" />"><img border="0" src="<mm:url page="images/arrow-left.png" />" /></a>
    </td>

    <!-- overview flap -->
    <mm:compare referid="flap" value="overview" inverse="true">
      <td><a href="<mm:url referids="parameters,$parameters,bugreport,template"><mm:param name="flap" value="overview" /></mm:url>">Overview</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="overview">
      <td class="selected">Overview</td>
    </mm:compare>
    
    

    <!-- history flap -->
    <mm:compare referid="flap" value="history" inverse="true">
      <td><a href="<mm:url referids="parameters,$parameters,bugreport,template" ><mm:param name="flap" value="history" /></mm:url>">History</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="history">
      <td class="selected">History</td>
    </mm:compare>
    
    
    <!-- change flap -->
    <mm:compare referid="flap" value="change" inverse="true">
      <td><a href="<mm:url referids="parameters,$parameters,bugreport,template" ><mm:param name="flap" value="change" /></mm:url>">Change</a></td>
    </mm:compare>
    <mm:compare referid="flap" value="change">
      <td class="selected">Change</td>
    </mm:compare>
  </tr>
</table>
