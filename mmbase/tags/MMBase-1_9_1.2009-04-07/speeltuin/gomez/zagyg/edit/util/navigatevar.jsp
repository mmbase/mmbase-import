<mm:notpresent referid="navigateparameters">
  <mm:import id="navigateparameters" >channel,thread?,</mm:import>
</mm:notpresent>

<mm:write id="totalpages" vartype="integer" value="${+ (($size - 1 ) / $max) }" write="false" />
<mm:write id="totalsize" vartype="integer" value="${+ ($totalpages * $max}" write="false" />
<mm:import id="navigate">
<mm:compare referid="totalpages" value="0" inverse="true">
<tr>
  <td colspan="5">
    <table style="width:100%;">
     <tr>
  <mm:previousbatches id="prev1" max="1" >
    <td><a href="<mm:url referids="${navigateparameters}prev1@offset" />">&lt;&lt;</a></td>
  </mm:previousbatches>
  <td colspan="3" style="width:100%;">
    <div align="center">
      <table>
        <tr>
          <td><a href="<mm:url referids="${navigateparameters}" />">eerste</a>&nbsp;</td>
          <mm:previousbatches id="prev" max="10" >
            <td ><a href="<mm:url referids="${navigateparameters}prev@offset" />"><mm:index ><mm:write vartype="integer" value="${+ $_ + 1 }" /></mm:index></a></td>
          </mm:previousbatches>
          <td><mm:write vartype="integer" value="${+ 1 + $offset / $max }" /></td>

          <mm:nextbatches id="next" max="10">
            <td><a href="<mm:url referids="${navigateparameters}next@offset" />"><mm:index ><mm:write vartype="integer" value="${+ $_ + 1 }" /></mm:index></a></td>
          </mm:nextbatches>
          <td>&nbsp;<a href="<mm:url referids="${navigateparameters}totalsize@offset" />">laatste</a></td>
        </tr>
       </table>
    </div>
  </td>
  <mm:nextbatches id="next1" max="1">
    <td><a href="<mm:url referids="${navigateparameters}next1@offset" />">&gt;&gt;</a></td>
  </mm:nextbatches>
      </tr>
    </table>
  </td>
</tr>
</mm:compare>
</mm:import>

