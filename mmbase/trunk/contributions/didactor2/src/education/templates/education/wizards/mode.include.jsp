
<mm:import id="wizardlang">en</mm:import>
<mm:compare referid="language" value="nl">
  <mm:import id="wizardlang" reset="true">nl</mm:import>
</mm:compare>

<mm:import externid="showcode">false</mm:import>
<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" />?referrer=/education/wizards/ok.jsp&amp;language=<mm:write referid="wizardlang" /></mm:import>
<mm:import id="listjsp"><mm:treefile write="true" page="/editwizards/jsp/list.jsp" objectlist="$includePath" />?language=<mm:write referid="wizardlang" /></mm:import>
<mm:node number="component.pdf" notfound="skip">
  <mm:relatednodes type="providers" constraints="providers.number=$provider">
    <mm:log>Provider ${provider}</mm:log>
    <mm:import id="pdfurl"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
  </mm:relatednodes>
</mm:node>

