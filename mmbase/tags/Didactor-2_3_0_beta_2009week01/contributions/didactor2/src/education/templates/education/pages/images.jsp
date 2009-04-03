<div class="images">
  <c:choose>
    <c:when test="${_node.imagelayout eq 0}">
      <table border="0" class="Font">
        <tr>
          <mm:relatednodes role="sizerel" type="images" orderby="sizerel.pos">
            <td>
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3> <mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>
              <mm:field name="width">
                <mm:isgreaterthan value="0">
                  <mm:image mode="img" border="0" template="s(${_}${_node.height gt 0 ? 'x' : ''}${_node.height gt 0 ? _node.height : ''})" />
                </mm:isgreaterthan>
                <mm:islessthan value="1">
                  <mm:image mode="img"  border="0"/>
                </mm:islessthan>
              </mm:field>
              <br clear="all"/>
              <mm:field name="description"/>
            </td>
          </mm:relatednodes>
        </tr>
      </table>
    </c:when>

    <c:when test="${_node.imagelayout eq 1}">
      <mm:relatednodes role="sizerel" type="images" orderby="sizerel.pos">
        <mm:field name="showtitle">
          <mm:compare value="1">
            <h3> <mm:field name="title"/></h3>
          </mm:compare>
        </mm:field>
        <mm:field name="width">
          <mm:isgreaterthan value="0">
            <mm:image mode="img" border="0" template="s(${_}${_node.height gt 0 ? 'x' : ''}${_node.height gt 0 ? _node.height : ''})" />
          </mm:isgreaterthan>
          <mm:islessthan value="1">
            <mm:image mode="img" border="0"/>
          </mm:islessthan>
        </mm:field>
        <br clear="all"/>
        <mm:field name="description"/>
        <br clear="all"/>
      </mm:relatednodes>
    </c:when>
  </c:choose>
</div>

