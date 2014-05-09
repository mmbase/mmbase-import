<%@ include file="jspbase.jsp" %>

<mm:cloud>
<mm:import externid="bname" />
<mm:import externid="bvalue" />
<mm:function set="mmbob" name="getBirthDateString" referids="bname,bvalue" />
</mm:cloud>
