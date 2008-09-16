<%@include file="/taglibs.jsp" %>
<%@page import="org.mmbase.bridge.*,java.io.*" %>
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    <title>Import script to add photos to employees</title>
</head>
<body style="overflow:auto;">
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
    boolean debug = false;
    String fotoDir =  NMIntraConfig.getIncomingDir() + "fotos/";
    NodeList thisnodesList =  cloud.getNodeManager("medewerkers").getList(null,null,null);
    int i = 0;
    int numberOfPhotos = 0;
    if(debug) { out.println("Start reading images for medewerkers.<br/>"); }
    while(i<thisnodesList.size()) {
        Node thisNode = thisnodesList.getNode(i);
        if(debug) { out.println("Checking " + thisNode.getStringValue("titel") + "<br/>"); }
        // *** check if this person already has an image attached ***
        RelationList relations = thisNode.getRelations("posrel","images");
        if(relations.size()==0) {
            if(debug) { out.println("no related images found<br/>"); }
            String alias = (String) thisNode.getValue("account");
            String [] sFile = { fotoDir + alias + ".jpg", fotoDir + alias.toUpperCase() + ".jpg", fotoDir + alias.toLowerCase() + ".jpg" };
            File f = null;
            int fsize = 0;
            for(int j=0; j< sFile.length && fsize==0; j++) {                  
               if(debug) { out.println("trying photo " + sFile[j] + ".<br/>"); }
               try {
                  f = new File(sFile[j]);
                  fsize = (int) f.length();
               } catch (Exception e) {
                  if(debug) { out.println("this photo can not be found<br/>"); }
               }
            }
            if(fsize!=0) {
                byte[] thedata = new byte[fsize];
                FileInputStream instream = new FileInputStream(f);
                instream.read(thedata);

                Node imgNode = cloud.getNodeManager("images").createNode();
                imgNode.setValue("titel",thisNode.getValue("titel") + ", " + alias);
                imgNode.setValue("handle",thedata);
                imgNode.setValue("omschrijving","Imported " + (new Date()));
                imgNode.commit();

                Relation thisRelation = thisNode.createRelation(imgNode, cloud.getRelationManager("posrel"));
                thisRelation.commit();
                out.println("Added photo for: " + alias + "<br/>");
                numberOfPhotos++;
                instream.close();
            }
        }
        i++;
    }
    if(numberOfPhotos==0) out.println("No new photos found.");
%>
</mm:cloud>
</body>
</html>