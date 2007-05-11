<mm:cloud jspvar="cloud">
  <jsp:scriptlet>cloud.setLocale(java.util.Locale.FRENCH);</jsp:scriptlet>
  <mm:listnodes type="object" max="1">
    <p>
      <mm:fieldlist><mm:fieldinfo type="guiname" />, </mm:fieldlist>
    </p>
    <mm:include page="codesamples/locale.node.jsp" />
  </mm:listnodes>
</mm:cloud>
