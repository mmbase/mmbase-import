<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<mm:listnodescontainer type="reldef">
  <mm:constraint field="sname" value="classrel" />
  <mm:listnodes>
    <mm:node>
      <mm:field name="number" id="t_clsrel"/>
    </mm:node>
  </mm:listnodes>
</mm:listnodescontainer>
  
<mm:listnodescontainer type="typedef">
  <mm:constraint field="name" value="educations"/>
  <mm:listnodes>
    <mm:node>
      <mm:field name="number" id="t_edu" />
    </mm:node>
  </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodescontainer type="typedef">
  <mm:constraint field="name" value="classes"/>
  <mm:listnodes>
    <mm:node>
      <mm:field name="number" id="t_cls" />
    </mm:node>
  </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodescontainer type="typerel">
  <mm:constraint field="snumber" referid="t_edu" />
  <mm:constraint field="dnumber" referid="t_cls" />
  <mm:listnodes>
    <mm:node>
      <mm:setfield name="rnumber"><mm:write referid="t_clsrel" /></mm:setfield>
      Updated reldef between Educations and Classes <br />
    </mm:node>
  </mm:listnodes>
</mm:listnodescontainer>

<mm:listnodes type="educations">
  <mm:node id="n_edu" />
  <mm:related path="related,classes">
    <mm:node element="classes" id="n_cls" />
    <mm:deletenode element="related" />
    <mm:createrelation source="n_edu" destination="n_cls" role="classrel" />
    <mm:remove referid="n_cls" />
    Updated relation between Education and Class <br />
  </mm:related>
  <mm:remove referid="n_edu" />
</mm:listnodes>

</mm:cloud>
</mm:content>
</html>

