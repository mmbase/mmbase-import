<p>
  <c:forEach var="index" begin="0" end="4">
    <mm:write value="$index" />
  </c:forEach>
</p>

<mm:import externid="h">hoi</mm:import>

<p>
  <c:choose>
    <c:when test="${h == 'hoi'}">
      h is default.
      <a href="<mm:url><mm:param name="h">hallo</mm:param></mm:url>">Change</a>
    </c:when>
    <c:otherwise>
      h is not default but ${h}!
      <a href="<mm:url />">Default</a>
    </c:otherwise>
  </c:choose>
</p>