<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>
<h2>Forms</h2>
<mm:cloud rank="basic user">

  <mm:hasnodemanager name="datatypes">

    <mm:form>
      <mm:createnode type="datatypes" id="validnode1" commitonclose="false"> 
        <!-- all defaults are valid of this type -->
        <mm:fieldlist type="edit">
          <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="errors" />
          <mm:last inverse="true">, </mm:last>
        </mm:fieldlist>
      </mm:createnode>
      <mm:valid>
        <h2>Ok, valid. Node will be commited.</h2>
        <mm:commit />
      </mm:valid>
      <mm:valid inverse="true"><h2>ERROR, node was valid, but reported not to be.</h2></mm:valid>

    </mm:form>

    <mm:node referid="validnode1">
      <p>Must show a real node number: <mm:field name="number" /></p>
    </mm:node>

    <hr />

    <mm:form>
      <mm:createnode type="datatypes" id="invalidnode1" commitonclose="false">
        <p>
          <mm:setfield name="zipcode">aaaaaaa</mm:setfield>
          <mm:field name="zipcode">
            <mm:fieldinfo type="guiname" />: <mm:fieldinfo type="errors" />
          </mm:field>
        </p>
      </mm:createnode>
      <mm:valid><h2>Error, not should not be valid!</h2></mm:valid>
      <mm:valid inverse="true">
        <h2>Ok this node is not valid!. There should be a remark about an invalid zipcode</h2>
      </mm:valid>
    </mm:form>

    <mm:node referid="invalidnode1" commitonclose="false">
      <p>This node is invalid, so it has not been committed, so has no positive number yet: <mm:field name="number" /></p>
    </mm:node>
    <hr />

    
    
    <mm:form>
      <form action="${_}" method="post">
        <mm:fieldlist id="newnode1" nodetype="datatypes" type="edit">
          <mm:fieldinfo type="guiname" />: 
          <mm:fieldinfo type="input" />
          <mm:fieldinfo type="errors" />
          <br />
        </mm:fieldlist>
        <input type="submit" />
      </form>
    </mm:form>

  </mm:hasnodemanager>
  <mm:hasnodemanager name="datatypes" inverse="true">
    <h1>The 'datatypes' nodemanager was not installed, these test cannot be performed.</h1>
  </mm:hasnodemanager>
</mm:cloud>
</body>
</html>