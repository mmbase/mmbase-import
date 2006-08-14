<%! 
public String getProblemsByType(Cloud cloud, String typeId, String user) {
   StringBuffer sbObjects = new StringBuffer();
   NodeList nlProblems = cloud.getList(typeId,
                                       "problemtypes,related,problems,posrel,people",
                                       "problems.number",
                                       "(people.number = '" + user + "')",
                                       "posrel.pos",null,null,true);
   String problems = null;
   for(int n=0; n<nlProblems.size(); n++) {
     sbObjects.append(',');
     sbObjects.append(nlProblems.getNode(n).getStringValue("problems.number"));
   }
   return sbObjects.toString();
}

public int getMaxPos(Cloud cloud, String start, String destination) {
   int max = 0;
   NodeList nlObjects = cloud.getList(start,
                                       "object,posrel," + destination,
                                       "posrel.pos",
                                       null,
                                       "posrel.pos","DOWN",null,true);
   if (nlObjects.size()>0) {
     int maxindb = nlObjects.getNode(0).getIntValue("posrel.pos");
     if (maxindb > max) {
       max = maxindb;
     }
   }
   return max;
}

public String getTestpath(Cloud cloud, String testId) {
   StringBuffer sbObjects = new StringBuffer();
   NodeList nlQuestions = cloud.getList(testId,
                                        "tests,posrel,questions",
                                        "questions.number",
                                        null,
                                        "posrel.pos",null,null,true);
   for(int n=0; n<nlQuestions.size(); n++) {
     if (n>0) { sbObjects.append(','); }
     sbObjects.append(nlQuestions.getNode(n).getStringValue("questions.number"));
   }
   return sbObjects.toString();
}
%>