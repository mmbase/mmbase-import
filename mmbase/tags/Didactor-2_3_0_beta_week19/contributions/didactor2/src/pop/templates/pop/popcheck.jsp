<mm:import id="showlo" reset="true"><di:getsetting component="education"
                   setting="showlo"
                   arguments="$learnobjectnumber" 
/></mm:import
><mm:write referid="showlo" /><mm:compare referid="showlo" value="1"
  ><img src="<mm:treefile page="/pop/gfx/checked.gif" objectlist="$includePath" referids="$popreferids"/>" border="0" 
/></mm:compare>
