<%
String memberid = (String) session.getAttribute("memberid");
if(memberid==null) {
   Cookie[] cookies = request.getCookies();
   if(cookies!=null){ 
      for (int c = 0; c < cookies.length; c++) {
         String thisName = cookies[c].getName();
         String thisValue = cookies[c].getValue();
         if (thisName!=null&&thisValue!=null) {
            if(thisName.equals("memberid")) {  memberid = thisValue; }
      	}
      }
   }
}
%>