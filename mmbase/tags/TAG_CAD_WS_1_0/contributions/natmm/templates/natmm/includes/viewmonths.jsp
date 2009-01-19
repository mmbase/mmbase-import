<%@page import="java.text.SimpleDateFormat"%>
<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<%@include file="../includes/calendar.jsp" %>
<%@include file="../includes/getstyle.jsp" %>
<script type="text/javascript">
<!--
 function moveMonth(direction) {
   var href = document.location.href;
   if(href.indexOf('offset=') != -1)
     href=href.substring(0,href.indexOf('offset=')-1);
   if(href.indexOf('e=') != -1)
     href=href.substring(0,href.indexOf('e=')-1);
   if(href.indexOf('?') != -1)
     document.location.href = href+'&offset='+direction;
   else
     document.location.href = href+'?offset='+direction;
 }

 function selectEvent(event) {
   var href = document.location.href;
   if(href.indexOf('e=') != -1)
     href=href.substring(0,href.indexOf('e=')-1);
   if(href.indexOf('?') != -1)
     document.location.href = href+'&e='+event;
   else
     document.location.href = href+'?e='+event;
 }

//-->
</script>
<style>
   .calGreyLeft {
       background-color: #<%= NatMMConfig.color1[iRubriekStyle] %>;
       border-top: black 1px solid;
       border-left: black 1px solid;
       border-bottom: black 1px solid;
       color: #<%= NatMMConfig.color3[iRubriekStyle] %>;
   }   
   .calGreyCenter {
       background-color: #<%= NatMMConfig.color1[iRubriekStyle] %>;
       border-top: black 1px solid;
       border-bottom: black 1px solid;
       color: #<%= NatMMConfig.color3[iRubriekStyle] %>;
   }   
   .calGreyRight {
       background-color: #<%= NatMMConfig.color1[iRubriekStyle] %>;
       border-top: black 1px solid;
       border-right: black 1px solid;
       border-bottom: black 1px solid;
       color: #<%= NatMMConfig.color3[iRubriekStyle] %>;
   }  
   .calGreyPoint {
       background-color: #<%= NatMMConfig.color1[iRubriekStyle] %>;
       border: black 1px solid;
       padding-left:1px;
       padding-right:1px;
       color: #<%= NatMMConfig.color3[iRubriekStyle] %>;
   }
</style>
<mm:cloud jspvar="cloud">
<%
PaginaHelper ph = new PaginaHelper(cloud);

cal.add(Calendar.MONTH, new Integer(offsetID).intValue());
SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

int year = cal.get(Calendar.YEAR);
int month = cal.get(Calendar.MONTH);
int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

cal.set(year,month,1,0,0);
long start = cal.getTimeInMillis()/1000;
cal.add(Calendar.MONTH, 1);
long stop = cal.getTimeInMillis()/1000;
String mainConstraint = "(NOT ((evenement.begindatum < " + start + " AND evenement.einddatum < " + start
                      + ") OR (evenement.begindatum > " + stop + " AND evenement.einddatum > " + stop + ")) )";

String [] styleClasses = { "", "calGreyLeft", "calGreyCenter", "calGreyRight", "calGreyPoint"}; 
int[] events = new int[daysInMonth+1]; // event number
int[] style = new int[daysInMonth+1];
int styleLeft = 1;
int styleCenter = 2;
int styleRight = 3;
int stylePoint = 4;

for(int i=0; i<=daysInMonth; i++) {
   events[i] = 0;
   style[i] = 0;
}
int lastEvent = -1;
long lastStop = 0;
String overlappingEventError = null;
 
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,evenement" fields="evenement.number,evenement.begindatum,evenement.einddatum"
   orderby="evenement.begindatum" directions="UP" constraints="<%= mainConstraint %>">
  <mm:field name="evenement.number" jspvar="eventNumber" vartype="Integer" write="false">
  <mm:field name="evenement.begindatum" jspvar="eventStart" vartype="Long" write="false">
  <mm:field name="evenement.einddatum" jspvar="eventStop" vartype="Long" write="false">
    <%
      if (lastStop > eventStart.longValue()) {
        if (overlappingEventError == null) {
          overlappingEventError = "event ";
          %>
          <mm:node number="<%= ""+lastEvent %>">
            <mm:field name="titel" jspvar="dummy" vartype="String" write="false">
              <% overlappingEventError += dummy + " (" + lastEvent + ") and event "; %>
            </mm:field>
          </mm:node>
          <mm:field name="titel" jspvar="dummy" vartype="String" write="false">
            <% overlappingEventError += dummy + " (" + eventNumber + ")  are overlapping"; %>
          </mm:field>
          <%
        }
      } else {
        int eventNum = eventNumber.intValue();
        int firstDay = 0;
        int lastDay = 0;
        int startStyle = styleLeft;
        int endStyle = styleRight;
        if (eventStart.longValue() < start) {
          firstDay = 1;
          startStyle = styleCenter;
        } else {
          cal.setTimeInMillis(eventStart.longValue() * 1000);
          firstDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        if (events[firstDay] != 0) { // two events in one day
          firstDay++;
        }
        if (eventStop.longValue() > stop) {
          lastDay = daysInMonth;
          endStyle = styleCenter;
        } else {
          cal.setTimeInMillis(eventStop.longValue() * 1000);
          lastDay = cal.get(Calendar.DAY_OF_MONTH);
        }
           
        if (firstDay == lastDay) { // one day
          events[firstDay] = eventNum;
          style[firstDay] = stylePoint; // point
          if (startStyle != styleLeft) {
            style[firstDay] = styleRight; //one day from prev month
          }                    
          if (endStyle != styleRight) {
            style[firstDay] = styleLeft; //one day from next month
          }                    
        } else {
          events[firstDay] = eventNum;
          style[firstDay] = startStyle;
          for (int i=firstDay+1; i<lastDay; i++) {
            events[i] = eventNum;
            style[i] = styleCenter;
          }
          events[lastDay] = eventNum;
          style[lastDay] = endStyle;
        }
        lastStop = eventStop.longValue();
        lastEvent = eventNum;
      }
    %>
   </mm:field>
   </mm:field>
   </mm:field>
</mm:list>
<%
 if (overlappingEventError != null) {
   %><%= overlappingEventError %><%
 } else {
   %>
   <table cellspacing="0" style="text-align:center;border-collapse:collapse;">
     <tr>
       <td colspan="9" style="line-height:85%;padding:0px;">
         <span class="navbutton" style="width:100%;">
            <a href="javascript:moveMonth(<mm:write referid="offset"/>-1)" class="klikpad">&lt;&lt;</a>
            <b><%= months_lcase[month]%>&nbsp;<%= year %></b>
            <a href="javascript:moveMonth(<mm:write referid="offset"/>+1)" class="klikpad">&gt;&gt;</a>
         </span>
       </td>
     </tr>
     <tr>
       <td class="leftnavpage_high">&nbsp;&nbsp;</td>
       <% for(int i=0;i<7;i++) { %>
           <td class="leftnavpage_high"><%= days_abbr[i].replace('.',' ').toLowerCase() %></td>
       <% } %>
       <td class="leftnavpage_high">&nbsp;&nbsp;</td>
     </tr>
     <tr><td>&nbsp;&nbsp;</td>
     <%
       cal.set(Calendar.DAY_OF_MONTH, 1);
       for(int i=1;i<cal.get(Calendar.DAY_OF_WEEK);i++) {
         %>
         <td>&nbsp;&nbsp;</td>
         <%
       }
       for(int i=1; i<=daysInMonth; i++) {
         cal.set(Calendar.DAY_OF_MONTH, i);
         if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && i!=1) {
           %>
           <tr><td>&nbsp;&nbsp;</td>
           <%
         }
         if (events[i] == 0) {
           %>
           <td><%= (i<10 ? "&nbsp;" : "" ) + i %></td>
           <%
         } else {
           %>
           <td onclick="selectEvent(<%= events[i] %>)"  class="<%= styleClasses[style[i]] 
               %>"><a href="<%= ph.createItemUrl("" + events[i], paginaID,null,request.getContextPath()) 
               %>" class="hover"><%= (i<10 ? "&nbsp;" : "" ) + i %></a></td>
           <%
         } 
         if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) {
           %>
           <td>&nbsp;&nbsp;</td></tr>
           <%
         }
       }
     %>
   </table>
   <table class="dotline"><tr><td height="3"></td></tr></table>
   <%
} 
%>          
</mm:cloud>

