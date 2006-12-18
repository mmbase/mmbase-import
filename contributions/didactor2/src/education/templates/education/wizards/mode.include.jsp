
<mm:import id="wizardlang">en</mm:import>
<mm:compare referid="language" value="nl">
  <mm:import id="wizardlang" reset="true">nl</mm:import>
</mm:compare>
<mm:import id="templates">/editwizards/data/</mm:import>
<mm:import externid="showcode">false</mm:import>

<mm:treefile id="wizardjsp" write="false" 
             page="/mmbase/edit/wizard/jsp/wizard.jsp" objectlist="$includePath" referids="wizardlang@language,templates">
  <mm:param name="referrer">/education/wizards/ok.jsp</mm:param>
  <mm:param name="loginmethod">delegate</mm:param>
</mm:treefile>

<mm:treefile id="listjsp" page="/mmbase/edit/wizard/jsp/list.jsp" objectlist="$includePath" referids="wizardlang@language,templates" write="false">
  <mm:param name="loginmethod">delegate</mm:param>
</mm:treefile>

<mm:node number="component.pdf" notfound="skip">
  <mm:relatednodes type="providers" constraints="providers.number=$provider">
    <mm:log>Provider ${provider}</mm:log>
    <mm:import id="pdfurl"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
  </mm:relatednodes>
</mm:node>

