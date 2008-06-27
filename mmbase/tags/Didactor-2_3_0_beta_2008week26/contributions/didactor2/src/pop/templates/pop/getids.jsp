<mm:import externid="whatselected" reset="true">0</mm:import>
<mm:import id="currentpop">-1</mm:import>
<mm:import externid="popcmd">no</mm:import>
<mm:import externid="returnto">-1</mm:import>
<mm:import externid="currentfolder">-1</mm:import>
<mm:import externid="t_mode">-1</mm:import>
<mm:import id="popreferids"><mm:write referid="referids"/>,student,whatselected,t_mode?</mm:import>
<mm:import externid="student"><mm:write referid="user"/></mm:import>
<mm:node number="$student" notfound="skip">
<mm:compare referid="currentfolder" value="-1">
  <mm:import externid="currentprofile">-1</mm:import>
  <mm:list nodes="$student" path="people,related,pop">
    <mm:first>
      <mm:import id="currentpop" reset="true"><mm:field name="pop.number"/></mm:import>
      <mm:compare referid="currentprofile" value="-1">
        <mm:list nodes="$currentpop" path="pop,related,profiles">
          <mm:first>
            <mm:remove referid="currentprofile"/>
            <mm:import id="currentprofile"><mm:field name="profiles.number"/></mm:import>
          </mm:first>
        </mm:list>
      </mm:compare>
    </mm:first>
  </mm:list>
</mm:compare>
<mm:compare referid="currentfolder" value="1">
  <mm:list nodes="$student" path="people,related,pop">
    <mm:first>
      <mm:import id="currentpop" reset="true"><mm:field name="pop.number"/></mm:import>
    </mm:first>
  </mm:list>
</mm:compare>
<mm:compare referid="currentfolder" value="2">
  <mm:list nodes="$student" path="people,related,pop">
    <mm:first>
      <mm:import id="currentpop" reset="true"><mm:field name="pop.number"/></mm:import>
    </mm:first>
  </mm:list>
</mm:compare>
</mm:node>
<mm:compare referid="currentfolder" value="-1" inverse="true">
  <mm:import id="currentprofile">-1</mm:import>
</mm:compare>
<mm:import externid="currentcomp">-1</mm:import>
