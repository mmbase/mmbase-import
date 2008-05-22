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
int numberOfPhotos = 0;
String sFotoDir =  NMIntraConfig.getIncomingDir() + "fotos/";

try {

    NodeList thisnodesList =  cloud.getNodeManager("medewerkers").getList(null,null,null);

    File fotoDir = new File(sFotoDir);
    File [] files =  fotoDir.listFiles();
    for(int f=0; f<files.length; f++) {
    
      String fileName = files[f].getName().replaceAll(" ","");
      boolean foundEmployee = false;
      
      for(int i=0; i<thisnodesList.size(); i++) {
        Node thisNode = thisnodesList.getNode(i);
        String employeeName = thisNode.getStringValue("titel");
        
        // check if the filename starts with the name of the employee
        if( fileName.indexOf(employeeName.replaceAll(" ",""))>-1 ) {
        
          // check if this person already has an image attached
          RelationList relations = thisNode.getRelations("posrel","images");
          
          if(relations.size()==0) {
          
            int fsize = (int) files[f].length();
            if(fsize!=0) {
                byte[] thedata = new byte[fsize];
                FileInputStream instream = new FileInputStream(files[f]);
                instream.read(thedata);
        
                Node imgNode = cloud.getNodeManager("images").createNode();
                imgNode.setValue("titel",employeeName);
                imgNode.setValue("handle",thedata);
                imgNode.setValue("omschrijving","Imported " + (new Date()));
                imgNode.commit();
        
                Relation thisRelation = thisNode.createRelation(imgNode, cloud.getRelationManager("posrel"));
                thisRelation.commit();
                out.println("Added photo for: " + employeeName + "<br/>");
                foundEmployee = true;
                numberOfPhotos++;
                instream.close();
            } else {
               out.println("File " + files[f].getName() + " is empty<br/>");
            }
          } else {
            out.println("Employee " + employeeName + " already has an image<br/>");
          }
        }
      }
      if(!foundEmployee) {
        out.println("Could not relate " +files[f].getName() + " to an employee<br/>");
      }
    }
} catch (Exception e) {
  if(debug) { out.println("the photo directory " + sFotoDir + " can not be found<br/>"); }
}
if(numberOfPhotos==0) out.println("No new photos found in " + sFotoDir);
%>
</mm:cloud>
</body>
</html>