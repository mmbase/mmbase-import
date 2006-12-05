<mm:import id="number" vartype="integer">12345678</mm:import>
<mm:locale language="nl">
  <p><fmt:formatNumber value="${number / 10000}" minFractionDigits="1" maxFractionDigits="1" /></p>
</mm:locale>
<mm:locale language="en">
  <p><fmt:formatNumber value="${number / 10000}" minFractionDigits="1" maxFractionDigits="1" /></p>
</mm:locale>