<mm:listnodes type="news" max="1" orderby="number" directions="down">
  <p>
    The info function:
    <mm:functioncontainer name="info">
      <mm:param name="function" value="info" />
      <mm:function />
    </mm:functioncontainer>
  </p>
  <p>All instance functions:</p>
  <mm:functioncontainer name="getFunctions">
    <ul>
      <mm:listfunction>
        <li><mm:write /></li>
      </mm:listfunction>
    </ul>
  </mm:functioncontainer>
</mm:listnodes>
<hr />
<p>
  'nodemanager functions' include also the node-functions. (with a 'node' argument).
</p>
<ul>
  <mm:listfunction nodemanager="news" name="getFunctions">
    <li><mm:write /></li>
  </mm:listfunction>
</ul>