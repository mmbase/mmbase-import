<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="cmsc-calendar" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="calendar.head.title">
<link href="../../../../editors/css/main.css" type="text/css" rel="stylesheet">
<c:url var="actionUrl" value="/editors/newsletter/Schedule.do"/>
<c:set var="type" value="${param.type}"/>

<script type="text/javascript">
   var type =1; 
   type = '${type}';

   var gotoString = '<fmt:message key="calendar.goto"/>';
   var todayString = '<fmt:message key="calendar.today"/>';
   var weekString = '<fmt:message key="calendar.week.short"/>';
   var scrollLeftMessage = '<fmt:message key="calendar.scrollLeftMessage"/>';
   var scrollRightMessage = '<fmt:message key="calendar.scrollRightMessage"/>';
   var selectMonthMessage = '<fmt:message key="calendar.selectMonthMessage"/>';
   var selectYearMessage = '<fmt:message key="calendar.selectYearMessage"/>';
   var selectDateMessage = '<fmt:message key="calendar.selectDateMessage"/>'; // do not replace [date], it will be replaced by date.
   var monthName =	new	Array(
                  '<fmt:message key="calendar.month.january"/>','<fmt:message key="calendar.month.february"/>',
                  '<fmt:message key="calendar.month.march"/>','<fmt:message key="calendar.month.april"/>',
                  '<fmt:message key="calendar.month.may"/>','<fmt:message key="calendar.month.june"/>',
                  '<fmt:message key="calendar.month.july"/>','<fmt:message key="calendar.month.august"/>',
                  '<fmt:message key="calendar.month.september"/>','<fmt:message key="calendar.month.october"/>',
                  '<fmt:message key="calendar.month.november"/>','<fmt:message key="calendar.month.december"/>');
    var dayName = new Array	('<fmt:message key="calendar.week.sunday.short"/>','<fmt:message key="calendar.week.monday.short"/>',
                '<fmt:message key="calendar.week.tuesday.short"/>','<fmt:message key="calendar.week.wednesday.short"/>',
                '<fmt:message key="calendar.week.thursday.short"/>','<fmt:message key="calendar.week.friday.short"/>',
                '<fmt:message key="calendar.week.saturday.short"/>');

   function initDatetime(){
      var now = new Date(); 
      if(type == '1' || type == '2'){
         document.getElementById("date").value = now.getDate()+"-"+eval(now.getMonth()+1)+"-"+now.getFullYear();
      }
      document.getElementById("hour").value = now.getHours();
      document.getElementById("minute").value = now.getMinutes();

	  var hour = document.getElementById("hour");
	  var minute = document.getElementById("minute");

	  for(var i = 0 ; i <24 ;i++) {
		var opt = document.createElement('OPTION');
		if(i ==  now.getHours()) {
		   opt.selected = true;
		}
		if(i <10) {
			opt.value ="0"+ i;
			opt.text = "0"+i;
		}
		else {
			opt.value =i;
			opt.text = i;
		}
		hour.options.add(opt);
	  }
	  for(var j = 0 ; j <60 ;j++) {
		var opt = document.createElement('OPTION');
		if(j ==  now.getMinutes()) {
		   opt.selected = true;
		}
		if(j <10) {
			opt.value ="0"+ j;
			opt.text = "0"+j;
		}
		else {
			opt.value =j;
			opt.text = j;
		}
		minute.options.add(opt);
	  }
   }
</script>
<script language="javascript">

   var message = ""; 
   var id='${param.id}';

   function createCalendar(arg) {
      type = arg;
      if(type == '1' || type == '2'){
        var date   = document.getElementById("date").value;
        if(!strDateTime(date)) {
            alert('<fmt:message key="calendar.validator.date"/>');
            return;
        }
      }
      if(type == '2'){
        var interval   = document.getElementById("interval");
        var strategy = document.getElementsByName("strategy");
        for(var i = 0 ; i < strategy.length;i++) {
            if(strategy[i].checked) {
               if(strategy[i].value == '2') {
                   if(!checkNumber(interval.value)) {
                      alert('<fmt:message key="calendar.validator.interval"/>');
                      interval.focus();
                      return;
                   }
               }
            }
         }
      }
      if(type == '3'){
        var interval   = document.getElementById("interval");
        if(!checkNumber(interval.value)) {
           alert('<fmt:message key="calendar.validator.interval"/>');
           interval.focus();
           return;
        }
      }

      if(type == '4'){
      var interval   = document.getElementById("day");
      var strategy = document.getElementsByName("strategy");
      for(var i = 0 ; i < strategy.length;i++) {
            if(strategy[i].checked) {
               if(strategy[i].value == '0') {
                   if(!checkNumber(interval.value)) {
                      alert('<fmt:message key="calendar.validator.interval"/>');
                      interval.focus();
                      return;
                   }
               }
            }
         }
      }
      sendRequest();
   }
 	var XMLHttpReq;
   var flag = false;
 	function createXMLHttpRequest() {	
		if(window.XMLHttpRequest) { 
			XMLHttpReq = new XMLHttpRequest();
		}
		else if (window.ActiveXObject) { 
			try {
				XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
			} 
         catch (e) {
				try {
					XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
				} 
            catch (e) {}
			}
		}
	}
	//send request 
	function sendRequest() {
		createXMLHttpRequest();
      var url = "${actionUrl}";
		XMLHttpReq.open("POST", url);
      XMLHttpReq.setRequestHeader("CONTENT-TYPE","application/x-www-form-urlencoded");
		XMLHttpReq.onreadystatechange = processResponse;
		XMLHttpReq.send(getParameters()); 
	}

   function getParameters(){
      var parameters = "";
      parameters += "type="+type;
      var date = document.getElementById("date");
      var hour = document.getElementById("hour");
      var minute = document.getElementById("minute");
      parameters += "&hour="+hour.value+"&minute="+minute.value;

      if(type == '1') {
         parameters += "&date="+date.value;
         message += '<fmt:message key="calendar.once"/>,<fmt:message key="calendar.start.datetime"/>'+date.value+' '+hour.value+':'+minute.value;
      }
      else if(type == '2') {
         parameters += "&date="+date.value;
         var approach = 0;
         var strategy = document.getElementsByName("strategy");
         for(var i = 0 ; i < strategy.length;i++) {
            if(strategy[i].checked) {
               parameters += "&strategy="+strategy[i].value;
               approach = strategy[i].value;
               break;
            }
         }
         var interval = document.getElementById("interval");
         parameters += "&interval="+interval.value;
         message += '<fmt:message key="calendar.daily"/>,<fmt:message key="calendar.start.datetime"/>'+date.value+' '+hour.value+':'+minute.value;

         if(approach == "0") {
            message += '<br/>  <fmt:message key="calendar.approach.interval.pre"/> <fmt:message key="calendar.approach.interval.day"/>'; 
         }
         else if(approach == "1") {
            message += '<br/>  <fmt:message key="calendar.approach.interval.pre"/> <fmt:message key="calendar.approach.weekday"/>'; 
         }
          else if(approach == "2") {
            message += '<br/>  <fmt:message key="calendar.approach.interval.pre"/> '+interval.value+' <fmt:message key="calendar.approach.interval.day"/>'; 
         }
      }
      else if(type == '3') {

         var varWeek = "";
         var interval = document.getElementById("interval");
         parameters += "&interval="+interval.value;
         var weeks = document.getElementsByName("weeks");
         for(var i = 0 ; i < weeks.length;i++) {
            if(weeks[i].checked) {
               parameters += "&weeks="+weeks[i].value;
               if(weeks[i].value == "1") {
                  varWeek += '<fmt:message key="calendar.week.monday"/>,';
               }
               else if(weeks[i].value == "2") {
                  varWeek += '<fmt:message key="calendar.week.tuesday"/>,';
               }
               else if(weeks[i].value == "3") {
                  varWeek += '<fmt:message key="calendar.week.wednesday"/>,';
               }
               else if(weeks[i].value == "4") {
                  varWeek += '<fmt:message key="calendar.week.thursday"/>,';
               }
               else if(weeks[i].value == "5") {
                  varWeek += '<fmt:message key="calendar.week.friday"/>,';
               }
               else if(weeks[i].value == "6") {
                  varWeek += '<fmt:message key="calendar.week.saturday"/>,';
               }
               else if(weeks[i].value == "7") {
                  varWeek += '<fmt:message key="calendar.week.sunday"/>,';
               }
            }
         }
        if(varWeek != null && varWeek != ""){
           if(varWeek.substr(varWeek.length-1,1) ==","){
              varWeek = varWeek.substr(0,varWeek.length-1) ;
           }
        }
         message += '<fmt:message key="calendar.weekly"/>,<fmt:message key="calendar.start.datetime"/>'+hour.value+':'+minute.value;
         message += '<br/> <fmt:message key="calendar.approach.interval.pre"/>'+interval.value+' <fmt:message key="calendar.approach.interval.week"/>';
         message += '<br/> <fmt:message key="calendar.approach.interval.week"/>: '+varWeek;
      }
      else {
         var strategy = document.getElementsByName("strategy");
         var months = "";
         var approach = "0";
         for(var i = 0 ; i < strategy.length;i++) {
            if(strategy[i].checked) {
               parameters += "&strategy="+strategy[i].value;
               approach = strategy[i].value;
               break;
            }
         }
         var day = document.getElementById("day");
         parameters += "&day="+day.value;
         var whichweek = document.getElementById("whichweek");
         parameters += "&whichweek="+whichweek.value;

         var week = document.getElementById("week");
         parameters += "&week="+week.value;
         var month = document.getElementsByName("month");
         for(var i = 0 ; i < month.length;i++) {
            if(month[i].checked) {
               parameters += "&month="+month[i].value;

               if(month[i].value == "0") {
                  months+= '<fmt:message key="calendar.month.january"/>,'
               }
               else if(month[i].value == "1") {
                  months+='<fmt:message key="calendar.month.february"/>,'
               }
               else if(month[i].value == "2") {
                  months+='<fmt:message key="calendar.month.march"/>,'
               }
               else if(month[i].value == "3") {
                  months+='<fmt:message key="calendar.month.april"/>,'
               }
               else if(month[i].value == "4") {
                  months+='<fmt:message key="calendar.month.may"/>,'
               }
               else if(month[i].value == "5") {
                  months+='<fmt:message key="calendar.month.june"/>,'
               }
               else if(month[i].value == "6") {
                  months+='<fmt:message key="calendar.month.july"/>,'
               }
               else if(month[i].value == "7") {
                  months+='<fmt:message key="calendar.month.august"/>,'
               }
               else if(month[i].value == "8") {
                  months+='<fmt:message key="calendar.month.september"/>,'
               }
               else if(month[i].value == "9") {
                  months+='<fmt:message key="calendar.month.october"/>,'
               }
               else if(month[i].value == "a") {
                  months+='<fmt:message key="calendar.month.november"/>,'
               }
               else if(month[i].value == "b") {
                  months+='<fmt:message key="calendar.month.december"/>,'
               }
            }
         }
        if(months != null && months != ""){
           if(months.substr(months.length-1,1) ==","){
              months = months.substr(0,months.length-1) ;
           }
        }
         message += '<fmt:message key="calendar.monthly"/>,<fmt:message key="calendar.start.datetime"/>'+hour.value+':'+minute.value;
         if(approach == "0") {
            message += '<br/>  <fmt:message key="calendar.approach.interval.pre"/> '+day.value+'  <fmt:message key="calendar.approach.interval.day"/>';
         }
         else if(approach == "1") {
            message += '<br/> <fmt:message key="calendar.week"/> ';
            message += whichweek.options[whichweek.selectedIndex].text;
            message += " "+week.options[week.selectedIndex].text; 
         }
         message += '<br/> <fmt:message key="calendar.month"/> '+months; 
      }
      return parameters;
   }
	//deal with the response 
    function processResponse() {
    	if (XMLHttpReq.readyState == 4) { 
        	if (XMLHttpReq.status == 200) { 
				DisplayHot();
         } 
        }
    }
    function DisplayHot() {	
	    var expression = XMLHttpReq.responseXML.getElementsByTagName("expression")[0].firstChild.nodeValue;
       window.opener.document.getElementById(id).value = expression;
       window.opener.document.getElementById("calendar-expression").innerHTML = message;
       this.close();
	}
   function strDateTime(str){
      var reg = /^(\d{1,2})(-|\/)(\d{1,2})\2(\d{1,4})$/;
      var r = str.match(reg);
      if(r==null)return false;
      var d= new Date(r[4], r[3]-1,r[1]);
      var newStr =d.getDate()+r[2]+(d.getMonth()+1)+r[2]+d.getFullYear();
      return newStr==str;
  }
  function check(reg,str){
     if( reg.test(str)) {
        return true;
     }
     return false;
  }
  function checkNumber(s){
     var reg = /^[0-9]*[1-9][0-9]*$/;
     return check(reg,s);
  }
</script>
<script src="../javascript/datepicker-new.js" type="text/javascript"></script>
</cmscedit:head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">

<body onload=" initPopCalendar();initDatetime()">
<form method="post" name="form1" action="">
<div >
<p><fmt:message key="calendar.title"/></p>
<c:choose>
    <c:when test="${type == '1'}">         
        <fmt:message key="calendar.startdate"/> <input type="text" name="date" id="date" size="12"/> <input type="image" class="calendar" src="../media/datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 ,this.form, 'date',event);return false;"/>
        <p>
       <fmt:message key="calendar.starttime"/> <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select><br/>
       <p>  <a href="#"  onclick="javascript:window.close()"/> <fmt:message key="calendar.cancel"/></a> <a href="#"  onclick="createCalendar('1')"/> <fmt:message key="calendar.ok"/></a></p>
    </c:when>
    <c:when test="${type == '2'}">
        <fmt:message key="calendar.startdate"/> <input type="text" name="date" id="date" size="12"/><input type="image" class="calendar" src="../media/datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 , document.forms[0], 'date',event);return false;"/>
        <p>
        <fmt:message key="calendar.starttime"/><select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
        </p>
        <fmt:message key="calendar.approach"/></br>                
        <input type="radio" name="strategy" checked id ="strategy" value="0"/><fmt:message key="calendar.daily"/></br>
        <input type="radio" name="strategy" id ="strategy"  value="1"/><fmt:message key="calendar.approach.weekday"/></br>
        <input type="radio" name="strategy" id ="strategy"  value="2"/><fmt:message key="calendar.approach.interval.pre"/><input type="text" size="4" name="interval" id="interval" value="1"/> <fmt:message key="calendar.approach.interval.day"/></br>         
                    
       <p> <a href="#"  onclick="javascript:window.close()"/><fmt:message key="calendar.cancel"/></a> <a href="#"  onclick="createCalendar('2')"/><fmt:message key="calendar.ok"/></a></p>
    </c:when>
    <c:when test="${type == '3'}">
        <fmt:message key="calendar.starttime"/> <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
        <p/>
        <fmt:message key="calendar.approach.interval.pre"/> <input type="text" name="interval" size="2"  id="interval" value="1"/> <fmt:message key="calendar.approach.interval.week"/>  <p/>
        <fmt:message key="calendar.week"/> <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="1" checked/><fmt:message key="calendar.week.monday"/>
        <input type="checkbox" name="weeks" id="weeks"  value="2" checked/><fmt:message key="calendar.week.tuesday"/> 
        <input type="checkbox" name="weeks" id="weeks"  value="3" checked/><fmt:message key="calendar.week.wednesday"/> <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="4" checked/><fmt:message key="calendar.week.thursday"/>
        <input type="checkbox" name="weeks" id="weeks"  value="5" checked/><fmt:message key="calendar.week.friday"/>
        <input type="checkbox" name="weeks" id="weeks"  value="6" checked/><fmt:message key="calendar.week.saturday"/> <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="7" checked/><fmt:message key="calendar.week.sunday"/>  <br/>
        <p> <a href="#"  onclick="javascript:window.close()"/><fmt:message key="calendar.cancel"/></a> <a href="#"  onclick="createCalendar('3')"/><fmt:message key="calendar.ok"/></a></p>
    </c:when>
    <c:otherwise>
       <fmt:message key="calendar.starttime"/> <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
       <br/><p></p>
       <input type="radio" checked name="strategy" id="strategy" value="0"/> <fmt:message key="calendar.approach.interval.day"/> <input size="2" value="1"  type="text" name="day" id="day"><br/>
       <input type="radio" name="strategy" id="strategy" value="1"/> 
       <select name="whichweek" id="whichweek">
          <option value="1"><fmt:message key="calendar.which.week.first"/> </option>
          <option value="2"><fmt:message key="calendar.which.week.second"/> </option>
          <option value="3"><fmt:message key="calendar.which.week.third"/> </option>
          <option value="4"><fmt:message key="calendar.which.week.forth"/> </option>
          <option value="5"><fmt:message key="calendar.which.week.last"/> </option>
       </select>
       <select name="week" id="week">
          <option value="1"> <fmt:message key="calendar.week.monday"/> </option>
          <option value="2"> <fmt:message key="calendar.week.Tuesday"/> </option>
          <option value="3"> <fmt:message key="calendar.week.Wednesday"/> </option>
          <option value="4"> <fmt:message key="calendar.week.Thursday"/> </option>
          <option value="5"> <fmt:message key="calendar.week.Friday"/> </option>
          <option value="6"> <fmt:message key="calendar.week.Saturday"/> </option>
          <option value="7"> <fmt:message key="calendar.week.Sunday"/></option>
      </select>
      <p>
      <fmt:message key="calendar.month"/><br/>
      <input type="checkbox" name="month" id="month" value="0" checked/><fmt:message key="calendar.month.january"/>
      <input type="checkbox" name="month" id="month" value="1" checked/><fmt:message key="calendar.month.february"/>
      <input type="checkbox" name="month" id="month" value="2" checked/><fmt:message key="calendar.month.march"/>
      <input type="checkbox" name="month" id="month" value="3" checked/><fmt:message key="calendar.month.april"/><br/>
      <input type="checkbox" name="month" id="month" value="4" checked/><fmt:message key="calendar.month.may"/>
      <input type="checkbox" name="month" id="month" value="5" checked/><fmt:message key="calendar.month.june"/>
      <input type="checkbox" name="month" id="month" value="6" checked/><fmt:message key="calendar.month.july"/>
      <input type="checkbox" name="month" id="month" value="7" checked/><fmt:message key="calendar.month.august"/><br/>
      <input type="checkbox" name="month" id="month" value="8" checked/><fmt:message key="calendar.month.september"/>
      <input type="checkbox" name="month" id="month" value="9" checked/><fmt:message key="calendar.month.october"/>
      <input type="checkbox" name="month" id="month" value="a" checked/><fmt:message key="calendar.month.november"/>
      <input type="checkbox" name="month" id="month" value="b" checked/><fmt:message key="calendar.month.december"/> <br/>
      <p> <a href="#"  onclick="javascript:window.close()"/><fmt:message key="calendar.cancel"/></a> <a href="#"  onclick="createCalendar('4')"/><fmt:message key="calendar.ok"/></a></p>
   </c:otherwise>
</c:choose>
</div>
</form>
</body>
</mm:cloud>
</html:html>
</mm:content>