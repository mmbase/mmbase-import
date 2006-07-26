<%
// should only be used in parts where user is found to be logged in

session.setAttribute("memberid",memberid);

int maxAge = 60 * 60 * 24 * 365; // one year for members
if(memberid!=null && memberid.equals(NatMMConfig.tmpMemberId)) {
   maxAge = 60 * 60 * 24; // one day for users with a temporary Id
}
Cookie thisCookie = null;
thisCookie = new Cookie("memberid", memberid); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie); 
%>