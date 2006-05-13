<%@include file="/taglibs.jsp" 
%><mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 
%><%
String sTemplateUrl = "info.jsp";
if(!articleId.equals("")) { 
   String articleTemplate = "article.jsp" + templateQueryString;
   %>
   <mm:present referid="newsletter_layout">
      <% articleTemplate = "news.jsp" + templateQueryString; %>
   </mm:present>
	<% response.sendRedirect(articleTemplate); %>
   <%--jsp:include page="<%= articleTemplate %>" /--%>
   <%

} else {  
      
      int objectPerPage = 10;
      int thisOffset = 1;
      try{
          if(!offsetId.equals("")){
              thisOffset = Integer.parseInt(offsetId);
              offsetId ="";
          }
      } catch(Exception e) {} 
      
      String thisPool = "-1";
      if(!poolId.equals("")){ 
          thisPool = poolId; 
          poolId = ""; 
      }
      
      int fromDay = 0; int fromMonth = 0; int fromYear = 0;
      int toDay = 0; int toMonth = 0; int toYear = 0;
      int thisDay = cal.get(Calendar.DAY_OF_MONTH);
      int thisMonth = cal.get(Calendar.MONTH)+1;
      int thisYear = cal.get(Calendar.YEAR);
      int startYear = 2004;
      long fromTime = 0;
      long toTime = 0;
      
      boolean checkOnPeriod = false;
      boolean periodExceedsMonth = true;
      
      if(!periodId.equals("")) {
          try{
              fromDay = new Integer(periodId.substring(0,2)).intValue(); 
              fromMonth = new Integer(periodId.substring(2,4)).intValue();
              fromYear = new Integer(periodId.substring(4,8)).intValue();
      
              toDay = new Integer(periodId.substring(8,10)).intValue();
              toMonth = new Integer(periodId.substring(10,12)).intValue();
              toYear = new Integer(periodId.substring(12)).intValue();
              if((fromDay+fromMonth+fromYear+toDay+toMonth+toYear)>0)
              {   // if not set use defaults for day, month and year
                  if(fromDay==0) fromDay = 1;
                  if(fromMonth==0) fromMonth = 1;
                  if(fromYear==0) fromYear = startYear; 
                  if(toDay==0) toDay = thisDay;
                  if(toMonth==0) toMonth = thisMonth;
                  if(toYear==0) toYear = thisYear;
      
                  cal.set(fromYear,fromMonth-1,fromDay,0,0,0);
                  fromTime = (cal.getTime().getTime()/1000);
      
                  cal.set(toYear,toMonth-1,toDay,23,60,0);
                  toTime = (cal.getTime().getTime()/1000);    
                  checkOnPeriod = (fromTime<=toTime);
                  periodExceedsMonth = toTime > (fromTime + 31*24*3600);
              }
          } catch (Exception e) { }
      } else {
          periodId = "";
      }
      // *** delete expired articles from this page (if it is not the archive) ***
      boolean isArchive = false;
      %><mm:node number="<%= pageId %>"
         ><mm:aliaslist
            ><mm:write  jspvar="alias" vartype="String" write="false"><%
	            isArchive = (alias.indexOf("archief") > -1); 
	         %></mm:write
	      ></mm:aliaslist
      ></mm:node
      ><%@include file="includes/movetoarchive.jsp" %><%
      boolean hasPools = false;
      %><mm:list nodes="<%= pageId %>" path="pagina,contentrel,artikel,posrel,pools"
		orderby="artikel.embargo" searchdir="destination" max="1"><%
         hasPools = true;
      %></mm:list
      ><%@include file="includes/header.jsp" 
      %><td><%@include file="includes/pagetitle.jsp" %></td>
      <td><%
         String rightBarTitle = "";
         if(isArchive) {
            rightBarTitle = "Zoek&nbsp;in&nbsp;archief";
         } else if(hasPools) {
            rightBarTitle = "Selecteer&nbsp;categorie";
         }
         %><%@include file="includes/rightbartitle.jsp" %></td>
      </tr>
      <tr>
      <td class="transperant">
      <div class="<%= infopageClass %>">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%@include file="includes/relatedteaser.jsp" %><%
             
                String articleConstraint = "";
                String articlePath = "pagina,contentrel,artikel";
                if(!thisPool.equals("-1")) {
                    articleConstraint = "( pools.number = '" + thisPool + "' )";
                    articlePath += ",posrel,pools";
                }
                if(checkOnPeriod) {
                  if(!articleConstraint.equals("")) articleConstraint += " AND ";
                  articleConstraint += "(( artikel.embargo > '" + fromTime + "') AND (artikel.embargo < '" + toTime + "'))";
                }
                if(!termSearchId.equals("")) {
                  if(!articleConstraint.equals("")) articleConstraint += " AND ";
                  articleConstraint += "(( UPPER(artikel.titel) LIKE '%" + termSearchId.toUpperCase() + "%') OR ( UPPER(artikel.intro) LIKE '%" + termSearchId.toUpperCase() + "%') ";
                  if(!thisPool.equals("-1")) {
                     articleConstraint += " OR ( UPPER(pools.name) LIKE '%" + termSearchId.toUpperCase() + "%' )";
                  }
                  articleConstraint += ")";
                }
                String extTemplateQueryString = templateQueryString; 
                if(!periodId.equals("")){ extTemplateQueryString += "&d=" + periodId; }
                int listSize = 0; 
                %>
                <%--
                <%= articlePath %><br/>
                <%= articleConstraint %><br/>
                --%>
                <mm:list nodes="<%= pageId %>" path="<%= articlePath %>" constraints="<%= articleConstraint %>"
				         orderby="artikel.embargo" searchdir="destination"
                  ><mm:first><mm:size jspvar="dummy" vartype="Integer" write="false"><% listSize = dummy.intValue();  %></mm:size></mm:first
                ></mm:list
                ><%@include file="includes/info/offsetlinks.jsp" %><%
                if(listSize>0) {
                   %><mm:list nodes="<%= pageId %>" path="<%= articlePath %>" orderby="artikel.embargo" searchdir="destination" directions="DOWN" 
                       offset="<%= "" + (thisOffset-1)*10 %>" max="<%= "" + objectPerPage %>" constraints="<%= articleConstraint %>"><%
                       String titleClass = "pageheader"; 
                       String readmoreUrl = "info.jsp";
                       if(isIPage) readmoreUrl = "ipage.jsp";
                       %><mm:field name="artikel.number" jspvar="article_number" vartype="String" write="false"><%
                           readmoreUrl += "?p=" + pageId + "&article=" + article_number; 
                       %></mm:field
                       ><mm:field name="pagina.titel_fra" jspvar="showExpireDate" vartype="String" write="false"
                           ><%@include file="includes/summaryrow.jsp" 
                       %></mm:field
                     ></mm:list><%
               } else { 
                  %><mm:list nodes="<%= pageId %>" path="<%= articlePath %>" max="1">
								Er zijn geen artikelen gevonden, die voldoen aan uw selectie criteria.
								<mm:import id="pagehasarticles" />
				        </mm:list>
						  <mm:notpresent referid="pagehasarticles">
								Dit archief bevat geen artikelen.
						  </mm:notpresent><%
               }
               %><%@include file="includes/pageowner.jsp" 
        %></td>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
      </tr>
      </table>
      </div>
      </td><td><%
      // *************************************** right bar *******************************
      %><%@include file="includes/info/relatedpools.jsp" %></td><%
} 
%><%@include file="includes/footer.jsp" 
%></mm:cloud>
