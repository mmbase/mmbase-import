<%
if(thisNode.getLongValue("verloopdatum") * 1000 < (new Date()).getTime()) {
                  
   ticketIcon = "../img/ticket_act_no.gif";
   altText = "Deze activiteit heeft reeds plaatsgevonden";

} else if(thisNode.getStringValue("aanmelden_vooraf").equals("2")) {

   ticketIcon = "../img/ticket_act_no.gif";
   altText = "Aanmelden voor deze activiteit via een derde partij (info is beschreven in intro)";

} else if(thisNode.getStringValue("aanmelden_vooraf").equals("0")) {

   ticketIcon = "../img/ticket_act_open.gif";
   altText = "Aanmelden vooraf is niet verplicht voor deze activiteit";   

} else if(thisNode.getStringValue("aanmelden_vooraf").equals("3")) {

   ticketIcon = "../img/ticket_act.gif";
   altText = "Aanmelden voor deze activiteit kan alleen telefonisch";
   
} else if(thisNode.getStringValue("aanmelden_vooraf").equals("4")) {
  
   ticketIcon = "../img/ticket_sponsoractivity.gif";
   altText = "Sponsoractiviteit, aanmelden bij het desbetreffende bezoekerscentra";
   
}
%>