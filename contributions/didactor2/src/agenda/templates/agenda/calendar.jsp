<%--
This piece of code renders the (little) calendar, click on a date to go to the agenda of this date.
It uses its own stylesheet (at /providers/mediators/courses/agenda/css/calendar.css),
this stylesheet can be overridden in the same manner as other parts of the site.
--%>
<%@ page import="java.util.Calendar,
                 java.text.SimpleDateFormat"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.eo.nl/calendar/1.3" prefix="cal" %>
<%@taglib uri="http://www.dynasol.com/simpletags/1.1.3" prefix="st" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

  <mm:import id="calmonth" externid="calmonth"/>
  <mm:notpresent referid="calmonth">
    <mm:remove referid="calmonth"/>
    <mm:import id="calmonth">0</mm:import>
  </mm:notpresent>

  <script>
  <!--
    function moveMonth(direction) {
      var href = document.location.href;
      if(href.indexOf('&calmonth=') != -1)
        href=href.substring(0,href.indexOf('&calmonth='));
      document.location.href = href+'&calmonth='+direction;
    }

    function selectDate(date) {
      date = ''+date;
      var year = date.substring(0,4);
      var month = date.substring(4,6)-0;
      var day = date.substring(6,8)-0;
     var newhref = '<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>&year='+year+
          '&month='+month+
          '&day='+day+
	  '&calmonth='+<mm:write referid="calmonth"/>;
      document.location.href = newhref;
    }

  //-->
  </script>

  <mm:write referid="calmonth" jspvar="calmonth" vartype="Integer" write="false">

    <%
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MONTH, calmonth.intValue());
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    %>
    <mm:remove referid="day"/>
    <mm:import id="day"><%=calendar.get(Calendar.DATE)%></mm:import>
    <mm:remove referid="month"/>
    <mm:import id="month"><%=calendar.get(Calendar.MONTH)+1%></mm:import>
    <mm:remove referid="year"/>
    <mm:import id="year"><%=calendar.get(Calendar.YEAR)%></mm:import>

    <mm:write referid="day" jspvar="day" vartype="Integer" write="false">
      <mm:write referid="month" jspvar="month" vartype="Integer" write="false">
        <mm:write referid="year" jspvar="year" vartype="Integer" write="false">
          <%
            calendar.set(year.intValue(), month.intValue()-1, day.intValue());
          %>
        </mm:write>
      </mm:write>
    </mm:write>

<%--    <link rel=StyleSheet href="<mm:treefile page="/agenda/css/calendar.css" objectlist="$includePath" referids="$referids"/>" type="text/css">--%>
    <cal:calendar view="month" language="nl" country="NL" date="<%=format.format(calendar.getTime())%>">
     <di:usercalwriter /> 
    
      <table width=130 class="cal" cellspacing=0>
        <tr>
          <td bgcolor="#DDDDDD" align="center" class="calMonth">
            <a href="javascript:moveMonth(<mm:write referid="calmonth"/>-1)">&lt;&lt;</a>
            <b><di:translate><st:data name="month" /></di:translate>&nbsp;
            <st:data name="month" />&nbsp;
            <st:data name="year"/></b>
            <a href="javascript:moveMonth(<mm:write referid="calmonth"/>+1)">&gt;&gt;</a>
          </td>
        </tr>
        <tr>
          <td>
            <table width="100%" class="calWeek" cellspacing=0>
              <tr>
                <td class="calWeek" align="center">&nbsp;&nbsp;</td>
                <td class="calWeek" align="center"><di:translate>zo</di:translate></td>
                <td class="calWeek" align="center"><di:translate>ma</di:translate></td>
                <td class="calWeek" align="center"><di:translate>di</di:translate></td>
                <td class="calWeek" align="center"><di:translate>wo</di:translate></td>
                <td class="calWeek" align="center"><di:translate>do</di:translate></td>
                <td class="calWeek" align="center"><di:translate>vr</di:translate></td>
                <td class="calWeek" align="center"><di:translate>za</di:translate></td>
                <td class="calWeek" align="center">&nbsp;&nbsp;</td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td >
            <table width="100%" class="calDay" cellspacing=0>
              <st:dataset name="week">
                <tr>
                  <td class="calDaySpacing" align="center">&nbsp;&nbsp;</td>
                  <st:dataset name="day">
                    <mm:remove referid="class"/>
                    <st:isnotset name="dayinmonth">
                      <st:isset name="dayitems"><mm:import id="class">calDayNotThisMonthHighlight</mm:import></st:isset>
                      <st:isnotset name="dayitems"><mm:import id="class">calDayNotThisMonth</mm:import></st:isnotset>
                    </st:isnotset>
                    <st:isset name="dayinmonth">
                      <st:isset name="dayitems"><mm:import id="class">calDayThisMonthHighlight</mm:import></st:isset>
                      <st:isnotset name="dayitems"><mm:import id="class">calDayThisMonth</mm:import></st:isnotset>
                    </st:isset>
                    <st:isset name="currentday">
                      <mm:import id="class">calToday <mm:write referid="class"/><mm:remove referid="class"/></mm:import>
                    </st:isset>

                    <td align="center" onclick="selectDate(<st:data name="date"/>)"  class="<mm:write referid="class"/>">
                      <st:data name="dayofmonth"/>
                    </td>
                  </st:dataset>
                  <td class="calDaySpacing" align="center">&nbsp;&nbsp;</td>
                </tr>
              </st:dataset>
            </table>
          </td>
        </tr>
      </table>
    </cal:calendar>
  </mm:write>
</mm:cloud>
</mm:content>

