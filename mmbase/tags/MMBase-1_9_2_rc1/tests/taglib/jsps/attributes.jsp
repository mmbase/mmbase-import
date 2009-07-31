<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<mm:import externid="abcde">ABC</mm:import>
<mm:import externid="test" vartype="Integer" >1234</mm:import>
<mm:import externid="list" vartype="List" >fiets,auto,,step</mm:import>

<table width="100%" border="1">
 <tr><th>description</th><th>Should be</th><th>Is really</th><th>EL version</th><th>Remarks</th></tr>
 <tr><td>referid</td><td>ABC</td><td><mm:write referid="abcde" /></td><td>${abcde}</td><td>&nbsp;</td></tr>
 <mm:write value="$abcde">
    <tr><td>$abcde + $_</td><td>ABC</td><td><mm:write value="$_" /></td><td>${_}</td><td>&nbsp;</td></tr>
 </mm:write>
 <mm:remove referid="abcde" />
 <mm:import externid="abcde">DEF</mm:import>
  <tr><td>After change</td><td>DEF</td><td><mm:write value="$abcde" /></td><td>${abcde}</td><td>fails in mmbase 1.6.0/resin</td></tr>
  <tr><td>String + var</td><td>ABCDEF</td><td><mm:write value="ABC$abcde" /></td><td>ABC${abcde}</td><td>&nbsp;</td></tr>
  <mm:import id="DEF">xyz</mm:import>
  <tr><td>Var in var</td><td>xyz</td><td><mm:write value="$[$abcde]" /></td><td>(impossible)</td><td>fails before 1.7.0.20030326,  ok in 1.6</tr>
  <tr><td>Expr. without var</td><td>20</td><td><mm:write value="$[+2*10]" /></td><td>${2*10}</td><td>Since 1.8 we use $[+]-notation for native mmbase expressions. \${+} would still work if EL is disabled.</td></tr>
  <mm:import id="a">3</mm:import>
  <mm:import id="b">10</mm:import>
  <tr><td>Expr. with var</td><td>30</td><td><mm:write value="$[+$a*$b]" /></td><td>${a*b}</td><td>&nbsp;</td></tr>
  <tr><td>Expr. with var (explicit cast to integer)</td><td>30</td><td><mm:write value="$[+$a*$b]" vartype="integer" /></td><td>${a*b}</td><td>&nbsp;</td></tr>
  <tr><td>Writing list with mm:write value</td><td>fiets,auto,,step</td><td><mm:write value="$list" /></td><td>${list}</td><td>[fiets, auto, step] in 1.6</td></tr>
  <tr><td>Writing list with mm:write referid</td><td>fiets,auto,,step</td><td><mm:write referid="list" /></td><td>${list}</td><td>[fiets, auto, step] in 1.6</td></tr>
  <tr><td>AliasList (or stringlist)</td><td>fiets, auto, , step</td>
      <td><mm:aliaslist referid="list"><mm:write /><mm:last inverse="true">, </mm:last></mm:aliaslist></td>
      <td><mm:aliaslist referid="list">${_}<mm:last inverse="true">, </mm:last></mm:aliaslist></td>
      <td>&nbsp;</td></tr>
 </table>
  
<hr />
<a href="<mm:url page="." />">back </a>
</body>
</html>
