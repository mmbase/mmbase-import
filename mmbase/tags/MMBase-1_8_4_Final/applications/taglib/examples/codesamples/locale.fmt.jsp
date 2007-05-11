<mm:import id="number">123456789</mm:import>
<mm:locale language="en-US">
  <p>US-style: <fmt:formatNumber value="${number / 10000}" minFractionDigits="1" maxFractionDigits="1" /></p>
  <mm:locale language="nl-NL">
      <p>NL-style: <fmt:formatNumber value="${number / 10000}" minFractionDigits="1" maxFractionDigits="1" /></p>
  </mm:locale>
  <p>US-style again: <fmt:formatNumber value="${number / 10000}" minFractionDigits="1" maxFractionDigits="1" /></p>
</mm:locale>
  