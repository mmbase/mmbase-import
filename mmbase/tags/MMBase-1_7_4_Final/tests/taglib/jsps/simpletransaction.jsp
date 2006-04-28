<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<h1>Testing taglib</h1>

<mm:import id="curtime"><%= System.currentTimeMillis()%></mm:import>

<mm:cloud logon="michiel" pwd="michiel">


<mm:list path="pools,pools5" >
 <mm:field name="pools5.name" /> / <mm:field name="pools.name" /><br />
</mm:list>
<hr />
<mm:list path="vacancies,departments" >
hoi <br />
</mm:list>


</mm:cloud>
<hr />
<a href="<mm:url page="node.jsp" />">node.jsp</a>
<hr />
</body>
</html>