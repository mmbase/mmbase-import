<%@page import="bsh.*"%>
<html>
<head>
<title>output</title>
<link href="../../css/style.css" rel="stylesheet" type="text/css">
<script language="javascript" src="../../js/i.js">
</script>
</head>
<body>
<%
   Interpreter i = new Interpreter();
   i.set( "myapp", this ); 
   i.set( "portnum", 1234 );
   i.eval("import org.mmbase.bridge.*"); 
   i.eval("cloudContext = ContextProvider.getDefaultCloudContext()");
   i.eval("cloud = cloudContext.getCloud(\"mmbase\")");

   //create methods to output text
   i.eval("strings = cloudContext.createStringList();");
   i.eval("p (Object o ){strings.add(o.toString()); }");
   i.eval("getOutput (){ StringBuffer sb = new StringBuffer(); StringIterator iter = strings.stringIterator(); while(iter.hasNext()){ sb.append(iter.nextString()); sb.append(\"\\n\"); } print(sb.toString()) ; return sb.toString(); }");
   
   i.eval("setAccessibility(true)"); 
   i.eval("server(portnum)"); 
%>

</body>
</html>
