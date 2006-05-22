<%
int maxAge = 10; // 60 * 60 * 24 * 365;
if(memberid!=null && memberid.equals(NatMMConfig.tmpMemberId)) {
   maxAge = 60 * 60 * 24;
}
Cookie thisCookie = null;
thisCookie = new Cookie("memberid", memberid); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
%>