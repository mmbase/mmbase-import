<%! 
public String getSimpleSelect(String nodeId, NodeList related, String destination, String field, String field2, String url, String param, String language) {

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
      String name = related.getNode(n).getStringValue(destination + "." + LocaleUtil.getLangFieldName(field, language));
      if (!field2.equals("")) {
         name += " " + related.getNode(n).getStringValue(destination + "." + LocaleUtil.getLangFieldName(field2, language));
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

public String getTableCells(String word, String styleName, String link, boolean isIE) {
   String sOut = "";
   char letters[] = word.toCharArray();
   for(int i=0; i<letters.length; i++) {
      String style = "";
      // hacks to vertical center letters in the navigation
      if(!isIE) { // firefox
         if(letters[i]!='@') {
            style="style='padding-top:3px;'";
         }
      } else { // ie
         if(letters[i]=='@') {
            style="style='padding-bottom:3px;'";
         }         
      }
      sOut += "   <td class='" + styleName + "' " + style + "onclick=\"gotoURL('document','" + link + "')\">" + letters[i] + "</td>\n";
   }
   return sOut;
}

public boolean checkParam(String param) {
   if (param == null) return false;
   return (!param.equals("") && !param.equals("-1"));
}

%>