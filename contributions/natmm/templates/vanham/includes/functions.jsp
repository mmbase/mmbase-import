<%! 
public String getObjects(Cloud cloud, Logger log, String objects, String source, String role, String destination, String nodeId) {
   StringBuffer sbObjects = new StringBuffer();
   String constraint =  "(" + destination + ".number = '" + nodeId + "')";
   // log.info("getObjecs objects=" + objects + ", path=" + source + "," + role + "," + destination + ", fields=" + source + ".number" + ", constraints=" + constraint);
   if(!nodeId.equals("")) {
      NodeList nlObjects = cloud.getList(objects,
                                 source + "," + role + "," + destination,
                                 source + ".number",
                                 constraint,
                                 null,null,null,true);
      for(int n=0; n<nlObjects.size(); n++) {
         if(n>0) { sbObjects.append(','); }
         sbObjects.append(nlObjects.getNode(n).getStringValue(source + ".number"));
      }
      objects = sbObjects.toString();
      // log.info(destination + ": " + objects);
   }
   return objects;
}

public String getObjectsConstraint(Cloud cloud, Logger log, String objects, String source, String path, String constraint) {
   StringBuffer sbObjects = new StringBuffer();
   // log.info("getObjecsConstraint objects=" + objects + ", path=" + path + ", fields=" + source + ".number" + ", constraints=" + constraint);
   NodeList nlObjects = cloud.getList(objects, path,source + ".number","(" + constraint + ")",null,null,null,true);
   for(int n=0; n<nlObjects.size(); n++) {
      if(n>0) { sbObjects.append(','); }
      sbObjects.append(nlObjects.getNode(n).getStringValue(source + ".number"));
   }
   objects = sbObjects.toString();
   // log.info("getObjectsConstraint: " + objects);
   return objects;
}

public NodeList getRelated(Cloud cloud, Logger log, String objects, String source, String role, String destination, String field, String field2, String language) {
   String fields = destination + ".number," + destination + "." + getLangFieldName(field,language);
   if (!field2.equals("")) {
      fields += "," + destination + "." + getLangFieldName(field2,language);
   }
   // log.info("getRelated objects=" + objects + ", path=" + source + "," + role + "," + destination + ", fields=" + fields + ", destination=" + destination + "." + field);
   NodeList nlRelated = cloud.getList(objects,
                                 source + "," + role + "," + destination,
                                 fields,
                                 null,destination + "." + field,"UP",null,true);
   int n=0;
   String lastFields = "";
   while(n<nlRelated.size()) { // make list unique
      String thisFields = nlRelated.getNode(n).getStringValue(destination + "." + field);
      if (!field2.equals("")) {
        fields += "," + nlRelated.getNode(n).getStringValue(destination + "." + field2);
      }
      if(thisFields.equals(lastFields)) {
        nlRelated.remove(n);
      } else {
        n++;
      }
      lastFields = thisFields;
   }
   return nlRelated;
}

public String getSimpleSelect(Cloud cloud, Logger log, String nodeId, NodeList related, String destination, String field, String field2, String url, String param, String language) {

   int pPos = url.indexOf(param);
   if(pPos!=-1) {
      int ampPos = url.indexOf("&",pPos);
      if(ampPos==-1) {
         url = url.substring(0,pPos);
      } else {
         url = url.substring(0,pPos) + url.substring(ampPos);
      }
   }

   String sStyle = "width:193px;";
   if (destination.equals("projecttypes")) { sStyle = "width:100%;"; }
   String sSelect = "<select name='" + param + "' class='cv_sub' style='" + sStyle +"' onChange=\"MM_jumpMenu('document',this,0)\">\n"
                  + "<option value='" + url + "&" + param + "=-1'>SELECTEER</option>\n";

   for(int n=0; n<related.size(); n++) {
      String name = related.getNode(n).getStringValue(destination + "." + getLangFieldName(field, language));
      if (!field2.equals("")) {
         name += " " + related.getNode(n).getStringValue(destination + "." + getLangFieldName(field2, language));
      }
      String number = related.getNode(n).getStringValue(destination + ".number");
      if (nodeId.equals(number)) {
         sSelect += "<option value='" + url + "&" + param + "=" + number + "' selected>" + name + "</option>\n";
      } else {
         sSelect += "<option value='" + url + "&" + param + "=" + number + "'>" + name + "</option>\n";
      }
   }
   sSelect += "</select>\n";
   return sSelect;
}

public String getLangFieldName(String field, String language) {
   String fieldname = field;
   if (!"nl".equals(language)) {
      fieldname += "_" + language;
   }
   return fieldname;
}

public String getField(Node node, String field, String language) {
   return node.getStringValue(getLangFieldName(field,language));
}

public boolean checkParam(String param) {
   if (param == null) return false;
   return (!param.equals("") && !param.equals("-1"));
}

public String getTableCells(String word, String styleName, String link) {
   String sOut = "";
   char letters[] = word.toCharArray();
   for(int i=0; i<letters.length; i++) {
      sOut += "   <td class='" + styleName + "' onclick=\"gotoURL('document','" + link + "')\">" + letters[i] + "</td>\n";
   }
   return sOut;
}
%>