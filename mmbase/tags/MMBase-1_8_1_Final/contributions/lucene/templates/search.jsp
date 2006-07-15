<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
  <mm:import externid="value"/>

  <form action="search.jsp" method="post">
    <input name="value" value="<mm:write referid="value" escape="text/html/attribute" />"/>
    <input type="submit" />
  </form>

  <mm:present referid="value" >
    <mm:nodelistfunction module="lucene" name="search" referids="value">
    <mm:first>
      search: <mm:write referid="value" /><br />
      hits: <mm:size /><br />
    </mm:first>
    <p><mm:function name="gui" /></p>
    </mm:nodelistfunction>
  </mm:present>

</mm:cloud>
