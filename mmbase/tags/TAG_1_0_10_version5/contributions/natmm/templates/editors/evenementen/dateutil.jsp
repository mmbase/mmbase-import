<%!

private String CalculateTimeTableDate(int nodeNumber, long lStartDate, long lEndDate, String sDayDescr)
{

   DoubleDateNode ddn = new DoubleDateNode();
   ddn.setBegin(new Date(lStartDate));
   ddn.setEnd(new Date(lEndDate));  

   String sItemDate = "<td>" +  ddn.getReadableDate()  + "&nbsp;</td><td> " + ddn.getReadableTime() + "&nbsp;</td><td> ";
   
   if(sDayDescr!=null && !sDayDescr.equals("")) {
      sItemDate += " " + sDayDescr;
   }
   
   sItemDate += "</td>";
   
   return sItemDate;
}

%>
   