<p>
  <c:forEach var="index" begin="0" end="4">
    <mm:write value="$index" /> <mm:last inverse="true">, </mm:last>
  </c:forEach>
</p>

<mm:import externid="h">hoi</mm:import>

<p>
  <c:choose>
    <c:when test="${h == 'hoi'}">
      h is default.     
      <mm:link>
        <mm:param name="h">hallo</mm:param>
        <a href="${_}">Change</a>
      </mm:link>
    </c:when>
    <c:otherwise>
      h is not default but ${h}!
      <mm:link>
        <a href="${_}">Default</a>
      </mm:link>
    </c:otherwise>
  </c:choose>
</p>

<mm:write referid="h">
  <c:if test="${_ == 'hoi'}">
    DEFAULT
  </c:if>
</mm:write>