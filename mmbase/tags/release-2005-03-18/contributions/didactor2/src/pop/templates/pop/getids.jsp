<mm:import externid="command">no</mm:import>
<mm:import externid="returnto">-1</mm:import>
<mm:import externid="currentfolder">-1</mm:import>
<mm:compare referid="currentfolder" value="-1">
  <mm:import externid="currentprofile">-1</mm:import>
  <mm:list nodes="$user" path="people,related,pop">
    <mm:first>
      <mm:import id="currentpop"><mm:field name="pop.number"/></mm:import>
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
<mm:compare referid="currentfolder" value="2">
  <mm:list nodes="$user" path="people,related,pop">
    <mm:first>
      <mm:import id="currentpop"><mm:field name="pop.number"/></mm:import>
    </mm:first>
  </mm:list>
</mm:compare>
<mm:compare referid="currentfolder" value="-1" inverse="true">
  <mm:import id="currentprofile">-1</mm:import>
</mm:compare>
<mm:import externid="currentcomp">-1</mm:import>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="persoonlijk ontwikkelings plan" /> Persoonlijk ontwikkelings plan
  </div>		
</div>