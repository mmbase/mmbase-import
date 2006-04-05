<%! 
public String getObjects(Cloud cloud, Logger log, String objects, String source, String role, String destination, String nodeId) {
   StringBuffer sbObjects = new StringBuffer();
   if(!nodeId.equals("")) {
      NodeList nlObjects = cloud.getList(objects,
                                 source + "," + role + "," + destination,
                                 source + ".number",
                                 "(" + destination + ".number = '" + nodeId + "')",
                                 null,null,null,true);
      for(int n=0; n<nlObjects.size(); n++) {
         if(n>0) { sbObjects.append(','); }
         sbObjects.append(nlObjects.getNode(n).getStringValue(source + ".number"));
      }
      objects = sbObjects.toString();
      log.info(destination + ": " + objects);
   }
   return objects;
}
public NodeList getRelated(Cloud cloud, Logger log, String objects, String source, String role, String destination, String field) {
   NodeList nlRelated = cloud.getList(objects,
                                 source + "," + role + "," + destination,
                                 destination + ".number," + destination + "." + field,
                                 null,destination + "." + field,"UP",null,true);
   return nlRelated;
}
public String getSelect(Cloud cloud, Logger log, String title, String cssClassName, String nodeId, NodeList related, String destination, String field, String url, String param) {
   String sSelect = 
            "<table style='width:190px;margin-bottom:3px;' border='0' cellpadding='0' cellspacing='0'>" 
         +     "<tr>"
         +        "<td class='bold'><div align='left' class='light_" + cssClassName + "'>&nbsp;" + title + "</div></td>"
         +     "</tr>"
         +  "</table>";
   if(!nodeId.equals("")) { // a node has been selected 
      sSelect +=
            "<table width='190' height='18' border='0' cellpadding='0' cellspacing='0'>" 
         +     "<tr>"
         +        "<td class='light_" + cssClassName + "'>&nbsp;" + cloud.getNode(nodeId).getStringValue(field) + "</td>"
         +     "</tr>"
         +  "</table>";
   } else {
      sSelect += 
            "<select name='menu1' class='" + cssClassName + "' style='width:180px;' onChange=\"MM_jumpMenu('parent',this,0)\">"
         +     "<option value='" + url + "'>Selecteer</option>";
      int pPos = url.indexOf(param);
      if(pPos!=-1) {
         int ampPos = url.indexOf("&",pPos);
         if(ampPos==-1) {
            url = url.substring(0,pPos);
         } else {
            url = url.substring(0,pPos) + url.substring(ampPos);
         }
      } else {
         log.error("Url " + url + " does not contain param " + param );
      }
      for(int n=0; n<related.size(); n++) {
         String name = related.getNode(n).getStringValue(destination + "." + field);
         String number = related.getNode(n).getStringValue(destination + ".number");      
         sSelect += 
               "<option value='" + url + "&" + param + "=" + number + "'>" + name + "</option>";
      }
      sSelect += 
   	      "</select>"
   	   +  "<br/>";
   } 
   return sSelect;
}

%>