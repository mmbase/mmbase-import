<%! public String getResponseVal(String questionId, String postingStr) {
    String answerValue = "";
    int qpos = postingStr.indexOf(questionId);
    if(qpos>-1) {
        int vstart = postingStr.indexOf("=",qpos)+1;
        int vend = postingStr.indexOf("|",vstart);
        answerValue = postingStr.substring(vstart,vend);
    }
    return answerValue;
}
%>