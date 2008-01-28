
<mm:import id="templates">/editwizards/data/</mm:import>
<mm:import externid="showcode">false</mm:import>

<mm:treefile id="wizardjsp" write="false" escapeamps="false"
             page="/mmbase/edit/wizard/jsp/wizard.jsp" objectlist="$includePath" referids="templates">
  <mm:param name="language">${locale.language}</mm:param>
  <mm:param name="referrer">/education/wizards/ok.jsp?reload=true</mm:param>
  <mm:param name="loginmethod">delegate</mm:param>
</mm:treefile>

<mm:treefile id="listjsp" page="/mmbase/edit/wizard/jsp/list.jsp" objectlist="$includePath" referids="templates" write="false" escapeamps="false">
  <mm:param name="language">${locale.language}</mm:param>
  <mm:param name="loginmethod">delegate</mm:param>
</mm:treefile>

<mm:hasnode number="component.pdf">
  <mm:node number="component.pdf">
    <mm:relatednodes type="providers" constraints="providers.number=$provider">
      <mm:log>Provider ${provider}</mm:log>
      <mm:treefile write="false" escapeamps="false" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" />
    </mm:relatednodes>
  </mm:node>
</mm:hasnode>

