
<mm:import id="templates">/editwizards/data/</mm:import>
<mm:import id="referrer">/education/wizards/ok.jsp?reload=true</mm:import>
<mm:import externid="showcode">false</mm:import>

<mm:treefile id="wizardjsp" write="false" escapeamps="false"
             page="/mmbase/edit/wizard/jsp/wizard.jsp" objectlist="$includePath" referids="templates,referrer">
  <mm:param name="language">${locale.language}</mm:param>
</mm:treefile>

<mm:treefile id="listjsp" page="/mmbase/edit/wizard/jsp/list.jsp" objectlist="$includePath" referids="templates,referrer" write="false" escapeamps="false">
  <mm:param name="language">${locale.language}</mm:param>
</mm:treefile>

<mm:write request="templates" referid="templates" />
<mm:write request="referrer" referid="referrer" />
<mm:write request="wizardjsp" referid="wizardjsp" />
<mm:write request="listjsp" referid="listjsp" />

<mm:hasnode number="component.pdf">
  <mm:node number="component.pdf">
    <mm:relatednodes type="providers" constraints="providers.number=$provider">
      <mm:log>Provider ${provider}</mm:log>
      <mm:treefile write="false" escapeamps="false" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" />
    </mm:relatednodes>
  </mm:node>
</mm:hasnode>

