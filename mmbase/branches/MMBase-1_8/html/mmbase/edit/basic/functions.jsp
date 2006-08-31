<%@page import="org.mmbase.util.Casting,org.mmbase.util.functions.*,java.util.*" %><%@ include file="page_base.jsp"
%><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<mm:cloud loginpage="login.jsp" sessionname="$config.session" rank="$rank">
<mm:write referid="style" escape="none" />
<title>View functions</title>
</head>
<body class="basic">
<mm:import externid="node_number" required="true" />
<mm:import externid="function_name" />
<mm:node number="$node_number" jspvar="node">
  <h1><mm:nodeinfo type="gui" /> (<mm:nodeinfo type="guinodemanager" />)
        <a href="<mm:url referids="node_number,node_number" page="change_node.jsp"/>">
           <span class="change"><!-- needed for IE --></span><span class="alt">[change]</span>  
         </a>

  </h1>
  <table>
    <tr><th colspan="4">Functions, with returntypes, arguments, and description</th></tr>
    <mm:nodeinfo type="type">
      <mm:listfunction nodemanager="$_" name="getFunctions" jspvar="f">
	<form>
	  <input type="hidden" name="node_number" value="<mm:write referid="node_number" />" />
	<% Function function = (Function) f; 
	%>
	<input type="hidden" name="function_name" value="<%=function.getName()%>" />
	<%
	List params = Arrays.asList(function.getParameterDefinition());
	if (params.contains(Parameter.NODE)) {;

	%>
	<tr>
	<th colspan="2"><%=function.getName()%></th>
	<td><%=function.getReturnType()%></td>
	<td><mm:write value="<%=function.getDescription()%>" /></td>
	</tr>
  <mm:functioncontainer>

    <% Iterator i = params.iterator();
     while(i.hasNext()) {
     Parameter param = (Parameter)i.next();
     if (param.getName().startsWith("_")) continue;
 %>
	<tr>
	  <td />
	  <td><%=param.getName()%><%=param.isRequired() ? " *" : ""%></td>
	  <td>
	    <%
	    	if (Casting.isStringRepresentable(param.getTypeAsClass())) {
		Object v = request.getParameter(function.getName() + "_" +param.getName());
		if (v == null) v = param.getDefaultValue();
		%>
		<input name="<%=function.getName()%>_<%=param.getName()%>" value="<mm:write value="<%=Casting.toString(v)%>" />" />
    <mm:param name="<%=param.getName()%>" value="<%=Casting.toString(v)%>" />
    <% } %>
	  </td>
	</tr>
  <% } %>
	<tr>
	  <td />
	  <td><input type="submit" name="call" value="execute" /></td>
	  <td colspan="<%=params.size()%>">
	  <mm:compare referid="function_name" value="<%=function.getName()%>">

      <mm:function name="<%=function.getName()%>" />

	  </mm:compare>
	  
	  </td>
	</tr>
  </mm:functioncontainer>
	<%
	} %>
	</form>
      </mm:listfunction>
    </mm:nodeinfo>
  </table>
</mm:node>
<%@ include file="foot.jsp"  %>
</mm:cloud>
</mm:content>