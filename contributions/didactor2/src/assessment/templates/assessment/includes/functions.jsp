<%! 
public String getProblemsByType(Cloud cloud, String typeId, String user) {
   StringBuffer sbObjects = new StringBuffer();
   NodeList nlProblems = cloud.getList(typeId,
                                       "problemtypes,related,problems,posrel,people",
                                       "problems.number",
                                       "(people.number = '" + user + "')",
                                       null,"posrel.pos",null,true);
   String problems = null;
   for(int n=0; n<nlProblems.size(); n++) {
     sbObjects.append(',');
     sbObjects.append(nlProblems.getNode(n).getStringValue("problems.number"));
   }
   return sbObjects.toString();
}
%>