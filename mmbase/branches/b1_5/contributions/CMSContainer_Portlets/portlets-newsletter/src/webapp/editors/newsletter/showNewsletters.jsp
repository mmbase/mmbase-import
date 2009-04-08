<%@page language="java" contentType="text/html;charset=utf-8"
%><%@page import="org.apache.struts.Globals"
%><%@include file="globals.jsp"
%><%@taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"
%><%@taglib prefix="edit" tagdir="/WEB-INF/tags/edit" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <head>
      <title><fmt:message key="newsletterlog.summary.newsletter"/></title>
      <link rel="icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
      <link rel="shortcut icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
      <link href="<c:url value='/editors/css/main.css'/>"type="text/css" rel="stylesheet" />
      <link href="<c:url value='/editors/newsletter/styles/newsletter.css'/>" type="text/css" rel="stylesheet" />
      
      <!-- calendar stylesheet -->
      <link rel="stylesheet" type="text/css" media="all"
         href="<c:url value='/editors/newsletter/styles/calendar-win2k-cold-1.css'/>"
         title="win2k-cold-1" />

      <!-- main calendar program --> 
      <script type="text/javascript" src="<c:url value='/editors/newsletter/js/calendar.js'/>"></script>


      <!-- language for the calendar -->
      <script type="text/javascript" src="<c:url value='/editors/newsletter/js/lang/calendar-en.js'/>" ></script>

      <!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
      <script type="text/javascript" src="<c:url value='/editors/newsletter/js/calendar-setup.js'/>"></script>

      <!-- the format needs -->
      <script src="<c:url value='/editors/utils/rowhover.js'/>" type="text/javascript"></script>
      <script src="<c:url value='/js/window.js'/>" type="text/javascript"></script>
      <script src="<c:url value='/js/transparent_png.js'/>" type="text/javascript"></script>

      <!--the reset button needs  -->
      <script src="<c:url value='/editors/newsletter/newsletter.js'/>" type="text/javascript"></script>
      <script language="javascript"> 
         function submits(){
         var startDate = document.getElementsByName("startDate");
         var endDate = document.getElementsByName("endDate");
         if(startDate[0].value!=''&&!strDateTime(startDate[0].value)) {
            alert("<fmt:message key='newsletterlog.datefrom.advice'/>");
            return;
         }if(endDate[0].value!='' && !strDateTime(endDate[0].value)) {
            alert("<fmt:message key='newsletterlog.dateto.advice'/>");
            return;
         }
            document.forms[0].submit();
         }
      </script>
   </head>
   <body>
      <script type="text/javascript">
         addLoadEvent(alphaImages);
      </script>

      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a name="activetab">
                     <fmt:message key="newsletterlog.newsletter.log" /> 
                  </a>
               </div>
            </div>
         </div>
      </div>

      <div class="editor">
         <div class="body">
            <html:form method="post" action="/editors/newsletter/NewsletterStatistic">
               <table border="0">
                  <tbody>
                  <tr>
                     <td style="width:13%"> <fmt:message key="newsletterlog.newsletter" /> </td>
                     <td style="width:25%">
                        <html:select property="newsletters" styleId="newsletters" style="width:150px">
                           <html:optionsCollection name="newsletters" label="title" value="id" />
                        </html:select>
                     </td>
                     <td style="width:10%">&nbsp;</td>
                     <td style="width:52%">&nbsp;</td>
                  </tr>
                  <tr>
                     <td> <fmt:message key="newsletterlog.from" /> </td>
                     <td nowrap>
                        <html:text property="startDate" styleId="f_date_b" style="width:150px" />
                        <img src="<cmsc:staticurl page='/editors/editwizards_new/media/datepicker/calendar.gif'/>" id="f_trigger_b" class="img"/>
                        
                        <script type="text/javascript">
                           Calendar.setup({
                              inputField     :    "f_date_b",      // id of the input field
                              ifFormat       :    "%Y-%m-%d",       // format of the input field
                              button         :    "f_trigger_b",   // trigger for the calendar (button ID)
                              step           :    1                // show all years in drop-down boxes (instead of every other year as default)
                           });
                       </script>
                     </td>
                     <td> <fmt:message key="newsletterlog.to" /> </td>
                     <td nowrap>
                        <html:text property="endDate" styleId="f_date_be" style="width:150px" />
                        <img src="<cmsc:staticurl page='/editors/editwizards_new/media/datepicker/calendar.gif'/>" id="f_trigger_be" class="img"/>
                           
                        <script type="text/javascript">
                           Calendar.setup({
                              inputField     :    "f_date_be",      // id of the input field
                              ifFormat       :    "%Y-%m-%d",       // format of the input field
                              button         :    "f_trigger_be",   // trigger for the calendar (button ID)
                              step           :    1                // show all years in drop-down boxes (instead of every other year as default)
                           });
                        </script>
                     </td>
                  </tr>
                  <tr>
                     <td>&nbsp;</td>
                     <c:choose>
                        <c:when test="${requestScope.searchForm==null}">
                           <td>
                              <input type="radio" name="detailOrSum" value="1" checked="checked" />
                                 <fmt:message key="newsletterlog.summary" />
                              <input type="radio" name="detailOrSum" value="2" />
                                 <fmt:message key="newsletterlog.detail" />
                           </td>
                        </c:when>
                        <c:when test="${requestScope.searchForm.detailOrSum=='1'}">
                           <td>
                              <input type="radio" name="detailOrSum" value="1" checked="checked" />
                                 <fmt:message key="newsletterlog.summary" />
                              <input type="radio" name="detailOrSum" value="2" />
                                 <fmt:message key="newsletterlog.detail" />
                           </td>
                        </c:when>
                        <c:when test="${requestScope.searchForm.detailOrSum=='2'}">
                           <td>
                              <input type="radio" name="detailOrSum" value="1" />
                                 <fmt:message key="newsletterlog.summary" />
                              <input type="radio" name="detailOrSum" value="2" checked="checked" />
                                 <fmt:message key="newsletterlog.detail" />
                           </td>
                        </c:when>
                     </c:choose>
                     <td colspan="2">&nbsp;</td>
                  </tr>

                  </tbody>
               </table>
            </html:form>
            <table>
               <tr>
                  <td style="width:13%">&nbsp;</td>
                  <td>
                      <input type="submit" onclick="javascript:submits()" 
                        value="<fmt:message key="newsletterlog.submit" />" />
                     <input type="submit" onclick="javascript:resets()"  
                        value="<fmt:message key="newsletterlog.reset" />" />
                  </td>
                  <td colspan="2">&nbsp;</td>
               </tr>
            </table>
         </div>
      </div>

      <div class="editor">
         <div class="ruler_green">
            <div>
               <fmt:message key="newsletterlog.showMessages" />
            </div>
         </div>
         <c:if test="${empty requestScope.records && not empty requestScope.result}">
            <div class="body">
               <table>
                  <thead>
                     <tr>
                        <th>&nbsp;</th>
                        <th> <fmt:message key="newsletterlog.summary.newsletter" /> </th>
                        <th> <fmt:message key="newsletterlog.summary.removed" /> </th>
                        <th> <fmt:message key="newsletterlog.summary.subscribe" /> </th>
                        <th> <fmt:message key="newsletterlog.summary.unsubscribe" /> </th>
                        <th> <fmt:message key="newsletterlog.summary.bounces" /> </th>
                        <th>&nbsp;</th>
                     </tr>
                  </thead>
                  <tbody class="hover">
                     <tr class="swap">
                        <td>&nbsp;</td>
                        <td onMouseDown="objClick(this);"> 
                           <c:if test="${requestScope.result.name=='newsletter.summary.bydate'}" >
                              ${requestScope.newsletterName}
                           </c:if>
                           <c:if test="${requestScope.result.name=='newsletter.summary.all'}" >
                              <fmt:message key="newsletterlog.summary.statistic" />
                           </c:if>
                           <c:if test="${requestScope.result.name=='newsletter.summary.all.bydate'}" >
                              <fmt:message key="newsletterlog.summary.statistic" />
                           </c:if>
                           <c:if test="${requestScope.result.name=='newsletter.summary'}" >
                              ${requestScope.newsletterName}
                           </c:if>
                        </td>
                        <td onMouseDown="objClick(this);"> ${requestScope.result.removed} </td>
                        <td onMouseDown="objClick(this);"> ${requestScope.result.subscribe} </td>
                        <td onMouseDown="objClick(this);"> ${requestScope.result.unsubscribe} </td>
                        <td onMouseDown="objClick(this);"> ${requestScope.result.bounches} </td>
                        <td>&nbsp;</td>
                     </tr>
                  </tbody>
               </table>
            </div>
         </c:if>

         <c:if test="${not empty requestScope.records}">
            <div class="body">
               <edit:ui-table items="${requestScope.records}" var="newsletterlog" size="${totalCount}" requestURI="/editors/newsletter/NewsletterStatistic.do">
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.newsletter">
                     ${newsletterlog.name}
                  </edit:ui-tcolumn>
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.logdate">
                     ${newsletterlog.showingdate}
                  </edit:ui-tcolumn>
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.removed">
                     ${newsletterlog.removed}
                  </edit:ui-tcolumn>
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.subscribe">
                     ${newsletterlog.subscribe}
                  </edit:ui-tcolumn>
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.unsubcribe">
                     ${newsletterlog.unsubscribe}
                  </edit:ui-tcolumn>
                  <edit:ui-tcolumn titlekey="newsletterlog.detail.bounces">
                     ${newsletterlog.bounches}
                  </edit:ui-tcolumn>
               </edit:ui-table>
            </div>
         </c:if>
         <c:if test="${empty requestScope.records && empty requestScope.result}">
            <div class="body">
               <fmt:message key="newsletterlog.noResults" />
            </div>
         </c:if>
      </div>
   </body>
</html>