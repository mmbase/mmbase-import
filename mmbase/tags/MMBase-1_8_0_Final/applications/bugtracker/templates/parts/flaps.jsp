<mm:import id="btemplate">fullview.jsp</mm:import>
<ul id="tabnav">
<!--
     <li><a href="<mm:url referids="parameters,$parameters" />"> <img border="0" src="<mm:url page="images/arrow-left.png" />" /></a></li>
-->
     <li><a href="<mm:url referids="parameters,$parameters" />"><img border="0" src="images/arrow-left.png"/>&nbsp;</a></li>
    <!-- overview flap -->
    <mm:compare referid="flap" value="overview" inverse="true">
      <li><a href="<mm:url referids="parameters,$parameters,bugreport,btemplate"><mm:param name="flap" value="overview" /></mm:url>">Overview</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="overview">
      <li><a href="#" class="active">Overview</a></li>
    </mm:compare>
    
    

    <!-- history flap -->
    <mm:compare referid="flap" value="history" inverse="true">
      <li><a href="<mm:url referids="parameters,$parameters,bugreport,btemplate" ><mm:param name="flap" value="history" /></mm:url>">History</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="history">
      <li ><a href="#" class="active">History</a></li>
    </mm:compare>
    
    
    <!-- change flap -->
    <mm:compare referid="flap" value="change" inverse="true">
      <li><a href="<mm:url referids="parameters,$parameters,bugreport,btemplate" ><mm:param name="flap" value="change" /></mm:url>">Change</a></li>
    </mm:compare>
    <mm:compare referid="flap" value="change">
      <li><a href="#" class="active">Change</a></li>
    </mm:compare>
</ul>
