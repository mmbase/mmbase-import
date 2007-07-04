<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:content postprocessor="reducespace" expires="0" type="text/html">
<mm:cloud method="asis">
  <jsp:directive.include file="/shared/setImports.jsp" />
  
  <mm:import externid="reset" />
  
  <mm:hasnode number="component.portal">
    <mm:log>THERE IS A PORTAL</mm:log>
    <mm:log><mm:cloudinfo type="rank" /></mm:log>
    <mm:hasrank value="anonymous">
      <mm:log>ANONYMOUS</mm:log>
      <mm:redirect page="/portal" />
    </mm:hasrank>
    
    <mm:hasrank value="anonymous" inverse="true">
      <mm:treefile page="cockpit.jsp" objectlist="$includePath" referids="$referids,reset?"
                   id="redirpage" write="false" />
      <mm:redirect referid="redirpage" />
    </mm:hasrank>
    
  </mm:hasnode>
  
  <mm:hasnode number="component.portal" inverse="true">
    <mm:treefile page="cockpit.jsp" objectlist="$includePath" referids="$referids,reset?"
                 id="redirpage" write="false" />
    <mm:redirect referid="redirpage" />
  </mm:hasnode>
</mm:cloud>
</mm:content>
