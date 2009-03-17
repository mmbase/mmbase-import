<%
if(thisEvent.getLongValue("einddatum") * 1000 < (new Date()).getTime()) {
   
   ticketIcon = "../img/ticket_no.gif";
   altText = "Deze activiteit heeft reeds plaatsgevonden";

} else if(thisEvent.getStringValue("isspare").equals("true")) {

   ticketIcon = "../img/ticket_no.gif";
   altText = "Dit is een reserve activiteit";

} else if(thisEvent.getStringValue("iscanceled").equals("true")) {

   ticketIcon = "../img/ticket_no.gif";
   altText = "Deze activiteit is geannuleerd";
         
} else if(Evenement.isFullyBooked(parentEvent,thisEvent)) {
   
   if(Evenement.isGroupExcursion(cloud,parentEvent.getStringValue("number"))
       && Evenement.hasStatus(cloud,thisEvent,"booked")) {
      ticketIcon = "../img/ticket_waitingforconfirm.gif";
      altText = "Deze groepsexcursie moet nog worden bevestigd.";
   } else {
      ticketIcon = "../img/ticket_fully_booked.gif";
      altText = "Het maximum aantal deelnemers voor deze activiteit is bereikt";
   }

} else if(Evenement.subscriptionClosed(parentEvent,thisEvent)) {

   ticketIcon = "../img/ticket_no.gif";
   altText = "De aanmelding voor deze activiteit is gesloten";

} else if(parentEvent.getStringValue("aanmelden_vooraf").equals("2")) {

   ticketIcon = "../img/ticket_no.gif";
   altText = "Aanmelden voor deze activiteit via een derde partij (info is beschreven in intro)";

} else if(parentEvent.getStringValue("aanmelden_vooraf").equals("0")) {

   ticketIcon = "../img/ticket_open.gif";
   altText = "Aanmelden vooraf is niet verplicht voor deze activiteit";

} else if(parentEvent.getStringValue("aanmelden_vooraf").equals("3")) {

   ticketIcon = "../img/ticket.gif";
   altText = "Aanmelden voor deze activiteit kan alleen telefonisch";

} else if(parentEvent.getStringValue("aanmelden_vooraf").equals("4")) {

   ticketIcon = "../img/ticket_sponsoractivity.gif";
   altText = "Sponsoractiviteit, aanmelden bij het desbetreffende bezoekerscentra";

}
%>