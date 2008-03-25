    <mm:import externid="wgroup"/>

    <mm:compare referid="t_mode" value="true" inverse="true">
      <mm:treeinclude page="/pop/s_leftpanel.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="t_rights"><mm:write referid="rights"/></mm:param>
      </mm:treeinclude>
    </mm:compare>
    <mm:compare referid="t_mode" value="true">
      <mm:treeinclude page="/pop/t_leftpanel.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="wgroup"><mm:write referid="wgroup"/></mm:param>
        <mm:param name="whatselected"><mm:write referid="whatselected"/></mm:param>
        <mm:param name="t_mode"><mm:write referid="t_mode"/></mm:param>
      </mm:treeinclude>
    </mm:compare>
