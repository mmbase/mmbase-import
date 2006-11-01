<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<html>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

migrating...<br/>


<mm:createnode type="competencetypes">
   <mm:setfield name="name">beroepscompetenties vakmatig-methodisch (VM)</mm:setfield>
   <mm:setfield name="pos">1</mm:setfield>
</mm:createnode>
<mm:createnode type="competencetypes">
   <mm:setfield name="name">beroepscompetenties bestuurlijk-organisatorisch en strategisch (BOS)</mm:setfield>
   <mm:setfield name="pos">2</mm:setfield>
</mm:createnode>
<mm:createnode type="competencetypes">
   <mm:setfield name="name">beroepscompetenties sociaal-communicatief (SC)</mm:setfield>
   <mm:setfield name="pos">3</mm:setfield>
</mm:createnode>
<mm:createnode type="competencetypes">
   <mm:setfield name="name">beroepscompetenties bijdragen aan ontwikkeling (ON)</mm:setfield>
   <mm:setfield name="pos">4</mm:setfield>
</mm:createnode>
<mm:createnode type="competencetypes">
   <mm:setfield name="name">leercompetenties</mm:setfield>
   <mm:setfield name="pos">5</mm:setfield>
</mm:createnode>
<mm:createnode type="competencetypes">
   <mm:setfield name="name">burgerschapscompetenties</mm:setfield>
   <mm:setfield name="pos">6</mm:setfield>
</mm:createnode>



<mm:createnode type="profiletypes">
   <mm:setfield name="name">beroepscompetentie profiel</mm:setfield>
   <mm:setfield name="pos">1</mm:setfield>
</mm:createnode>
<mm:createnode type="profiletypes">
   <mm:setfield name="name">kwalificatiedossier profiel</mm:setfield>
   <mm:setfield name="pos">2</mm:setfield>
</mm:createnode>




<mm:createnode type="priorities">
   <mm:setfield name="name">Zeer belangrijk</mm:setfield>
   <mm:setfield name="pos">1</mm:setfield>
</mm:createnode>
<mm:createnode type="priorities">
   <mm:setfield name="name">Redelijk belangrijk</mm:setfield>
   <mm:setfield name="pos">2</mm:setfield>
</mm:createnode>
<mm:createnode type="priorities">
   <mm:setfield name="name">Minder belangrijk</mm:setfield>
   <mm:setfield name="pos">3</mm:setfield>
</mm:createnode>
<mm:createnode type="priorities">
   <mm:setfield name="name">Niet belangrijk</mm:setfield>
   <mm:setfield name="pos">4</mm:setfield>
</mm:createnode>



<mm:createnode type="ratings">
   <mm:setfield name="name">Uitstekend</mm:setfield>
   <mm:setfield name="pos">9</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Goed</mm:setfield>
   <mm:setfield name="pos">8</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Ruim voldoende</mm:setfield>
   <mm:setfield name="pos">7</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Voldoende</mm:setfield>
   <mm:setfield name="pos">6</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Matig</mm:setfield>
   <mm:setfield name="pos">5</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Slecht</mm:setfield>
   <mm:setfield name="pos">4</mm:setfield>
</mm:createnode>
<mm:createnode type="ratings">
   <mm:setfield name="name">Zeer slecht</mm:setfield>
   <mm:setfield name="pos">3</mm:setfield>
</mm:createnode>

done<br/>

</mm:cloud>
</mm:content>
</html>
