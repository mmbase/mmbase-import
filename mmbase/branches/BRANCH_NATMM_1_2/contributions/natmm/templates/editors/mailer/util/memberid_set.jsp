<%
// should only be used in parts where user is found to be logged in
// can not set null values therefore change null to -1

session.setAttribute("memberid",(memberid == null ? "-1" : memberid));

int maxAge = 60 * 60 * 24 * 365; // one year for members
if(memberid!=null && memberid.equals(NatMMConfig.getTmpMemberId())) {
   maxAge = 60 * 60 * 24; // one day for users with a temporary Id
}
Cookie thisCookie = null;
thisCookie = new Cookie("memberid", (memberid == null ? "-1" : memberid));
thisCookie.setMaxAge(maxAge);
response.addCookie(thisCookie); 
%>
