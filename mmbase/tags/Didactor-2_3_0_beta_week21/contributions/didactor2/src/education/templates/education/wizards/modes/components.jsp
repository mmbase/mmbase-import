<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:cloud rank="basic user">
  <jsp:directive.include file="/shared/setImports.jsp" />

  <jsp:directive.include file="../roles_defs.jsp" />
  <mm:import id="editcontextname" >componenten</mm:import>
  <jsp:directive.include file="../roles_chk.jsp" />

  <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
    <mm:listnodes type="components" orderby="name">
      <mm:treefile id="file" page="/components/edit.jsp" objectlist="$includePath"
                   referids="_node@component" write="false" /> 
      &nbsp;&nbsp;&nbsp;
      <a target="text" href="${file}">
        <mm:field name="name" />
      </a> 
      <br />	
    </mm:listnodes>
  </mm:islessthan>
</mm:cloud>
