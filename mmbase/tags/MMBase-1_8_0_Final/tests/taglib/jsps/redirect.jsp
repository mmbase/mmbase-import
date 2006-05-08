<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>


<mm:url id="test" page="/" write="false">
  <mm:param name="test" value="test" />
</mm:url>

<mm:redirect referid="test" />