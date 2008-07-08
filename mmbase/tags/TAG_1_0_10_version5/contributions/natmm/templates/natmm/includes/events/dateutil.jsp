<%!

private String CalculateTimeTableDate(
   String thisEvent,
   String selectedEvent,
   DoubleDateNode ddn,
   String sDayDescr,
   boolean bIsFullyBooked,
   boolean bIsCancelled,
   long nowSec)
{

   String sItemDate = "";
   if(ddn.getEnd().getTime()/1000 > nowSec ) { 
   
      sItemDate = "<tr><td style=\"vertical-align:top\">";
      if(!thisEvent.equals(selectedEvent)) { 
         sItemDate += "<a href=\"events.jsp?p=agenda&e=" + thisEvent + "\">";
      }
      sItemDate += ddn.getReadableDate(" | "); 
      if(!thisEvent.equals(selectedEvent)) { 
         sItemDate += "</a>";
      }
      sItemDate += "&nbsp;</td>" 
         + "<td style=\"vertical-align:top\">| " + ddn.getReadableTime() + "&nbsp;</td>" 
         + "<td style=\"vertical-align:top\">";
      
      if(sDayDescr!=null && !sDayDescr.equals("")) {
         sItemDate += "| " + sDayDescr;
      }
        
      if(bIsFullyBooked) { sItemDate += "<b>Volgeboekt</b>"; }
      if(bIsCancelled) { sItemDate += "<b>Geannuleerd</b>"; }
      
      sItemDate += "</td></tr>";
   }
   
   return sItemDate;
}

%>
   