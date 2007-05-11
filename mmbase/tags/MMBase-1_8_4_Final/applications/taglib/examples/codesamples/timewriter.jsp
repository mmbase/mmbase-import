<!-- working as a writer that does not
     write (only to 'context') -->
<mm:time time="now" id="now" write="false" /> 


<!-- reuse it -->
<mm:write value="$now" id="n">
   <!-- a writer in a writer -->
   <mm:write value="Now: " write="true">
<!-- use the 'writer' attribute to refer 
     to a parent writer which is not the direct parent -->
     <mm:time writer="n" format=":LONG" />
   </mm:write>
</mm:write>
<p>
  <!-- also has an escape attribute -->
  The current year in roman:
  <mm:time referid="now" format="yyyy" escape="roman,uppercase" />
</p>