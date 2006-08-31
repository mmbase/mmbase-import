<%@page import="org.mmbase.bridge.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud jspvar="mmbasecloud">

<html>
<head>
<title>Importeer documenten (ingelogd als <%= mmbasecloud.getUser().getIdentifier() %>)</title>
<link rel="stylesheet" type="text/css" href="../css/editors.css">
</head>
<body>
<h2>Bezig met het importeren van documenten ...</h2>
Een moment geduld a.u.b<br><br>
<mm:node number="knipsels" id="knipsels" />
<mm:node number="knipselkrant" id="knipselkrant" />
<%	String inputDirectory = "d:/data/Intranet/knipsels/";
	int imageCount = 0;
	try {
		File d = new File(inputDirectory);
		String [] imageList = d.list();
		for(int i=0; i<imageList.length; i++) {
			String imageTitle = "Gescande document " + imageList[i];
			%><mm:list path="images" constraints="<%= "images.description LIKE '%" + imageTitle + "%'" %>"
				><mm:import id="imagefound"
			/></mm:list
			><mm:notpresent referid="imagefound"><% 
				File f = new File(inputDirectory + imageList[i]);
				int fsize = (int) f.length();
				byte[] thedata = new byte[fsize];
      	      	FileInputStream instream = new FileInputStream(f);
		            instream.read(thedata);
      		      NodeManager imageItemManager = mmbasecloud.getNodeManager("images");
	      	      Node imgNode = imageItemManager.createNode();
      	      	imgNode.setValue("title",imageTitle);
		            imgNode.setValue("handle",thedata);
	      	      imgNode.setValue("description",imageTitle + " geimporteerd op " + (new Date()) 
					+ " (Deze beschrijving niet wijzigen i.v.m. het import script voor knipsels)");
      	      	imgNode.commit();
				imageCount++;
				%><mm:list path="images" constraints="<%= "images.description LIKE '%" + imageTitle + "%'" %>" 
					><mm:field name="images.number" id="imagenumber" write="false"
						><mm:node number="$imagenumber" id="thisimage"
					/></mm:field
				></mm:list
				><mm:createrelation role="posrel" source="knipsels" destination="thisimage"
				/><mm:createnode type="items" id="thisitem"
					><mm:setfield name="name">Knipsel <%=  imageList[i] %></mm:setfield
				></mm:createnode
				><mm:createrelation role="posrel" source="thisitem" destination="thisimage"
				/><mm:createrelation role="posrel" source="thisitem" destination="knipselkrant"
				/>

				<%= imageTitle %><br>
			</mm:notpresent
			><mm:remove referid="imagefound" /><%		
		} 
		if(imageCount==0) { 
			%><h3>Geen nieuwe gescande documenten gevonden.</h3><% 
		} else {
			%><h3>Klaar</h3><%
		}
	} catch (Exception e) {
           	%><h3>Geen nieuwe gescande documenten gevonden.</h3><%
	}
%>
</body>
</html>

</mm:cloud>