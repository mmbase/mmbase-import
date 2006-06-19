<%@page import="org.mmbase.bridge.*,org.mmbase.util.logging.Logger" %>
<%@include file="/taglibs.jsp" %>
<%! public void migratePToBR(Node n, List htmlFields, Logger log) {
	boolean fieldHasChanged = false;
	for(int f=0; f<htmlFields.size(); f++) {
		String field = (String) htmlFields.get(f);
		String oldValue = n.getStringValue(field);
		if(oldValue!=null && !oldValue.equals("")) {
			String newValue = oldValue.replaceAll("<P>","").replaceAll("</P>","<BR/><BR/>");
			if(!newValue.equals(oldValue)) {
				n.setStringValue(field,newValue);
				fieldHasChanged = true;
			}
		}
	}
	if(fieldHasChanged) {
		n.commit();
	}
}
%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
	Replacing all &lt;p&gt;'s by &lt;br&gt;&lt;br&gt;'s<br/> 
	<%
	NodeManagerList nml = cloud.getNodeManagers();
	for(int i=0; i< nml.size(); i++) {
		NodeManager nm = nml.getNodeManager(i);
		String tmp = nm.getProperty("htmlFields");
		if (tmp != null) {
			List htmlFields = new ArrayList();
			StringTokenizer tokenizer = new StringTokenizer(tmp, ", ");
			while(tokenizer.hasMoreTokens()) {
				String field = tokenizer.nextToken();
				 htmlFields.add(field);
			}
			if(htmlFields.size()>0) {
				log.info("migrating nodes of type " + nm.getName());
				NodeList nl = nm.getList(null,null,null);
				for(int j=0; j<nl.size(); j++) {
					Node n = nl.getNode(j);
					migratePToBR(n,htmlFields,log);
				}
			}
		}
	}
	%>
	</body>
</html>
</mm:log>
</mm:cloud>
