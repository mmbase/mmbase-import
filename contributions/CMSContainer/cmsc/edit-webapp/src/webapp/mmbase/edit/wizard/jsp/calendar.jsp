<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="content.title">
<link href="../../../../editors/css/main.css" type="text/css" rel="stylesheet">
<c:url var="actionUrl" value="/editors/newsletter/Schedule.do"/>
<c:set var="type" value="${param.type}"/>

<script type="text/javascript">
   var type =1; 
   type = '${type}';
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

   var gotoString = 'Go to current month';
   var todayString = 'Today is';
   var weekString = 'Wk';
   var scrollLeftMessage = 'Click here to scroll to the previous month. Press the mousebutton to scroll automatically.';
   var scrollRightMessage = 'Click here to scroll to the next month. Press the mousebutton to scroll automatically.';
   var selectMonthMessage = 'Click here to select a month.';
   var selectYearMessage = 'Click here to select a year.';
   var selectDateMessage = 'Select [datum] as the date.'; // do not replace [date], it will be replaced by date.
   var monthName =	new	Array(
                  'january','february',
                  'march','april',
                  'may','june',
                  'july','august',
                  'september','october',
                  'november','december');
    var dayName = new Array	('Sun','Mon',
                'Tue','Wed',
                'Thu','Fri',
                'Sat');
</script>
<script language="javascript">

   var message = ""; 
   var id='${param.id}';


   function sheding(arg) {
      type = arg;
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
			} catch (e) {
				try {
					XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
				} catch (e) {}
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
         message += "Once,start datetime:"+date.value+" "+hour.value+":"+minute.value;
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
         message += "Daily,start datetime:"+date.value+" "+hour.value+":"+minute.value;

         if(approach == "0") {
            message += "<br/>  every day"; 
         }
         else if(approach == "1") {
            message += "<br/>  every weekday"; 
         }
          else if(approach == "2") {
            message += "<br/>  every "+interval.value+" day"; 
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
                  varWeek += "Monday,";
               }
               else if(weeks[i].value == "2") {
                  varWeek += "Tuesday,";
               }
               else if(weeks[i].value == "3") {
                  varWeek += "Wednesday,";
               }
               else if(weeks[i].value == "4") {
                  varWeek += "Thursday,";
               }
               else if(weeks[i].value == "5") {
                  varWeek += "Friday,";
               }
               else if(weeks[i].value == "6") {
                  varWeek += "Saturday,";
               }
               else if(weeks[i].value == "7") {
                  varWeek += "Sunday,";
               }
            }
         }
         message += "Weekly,start time:"+hour.value+":"+minute.value;
         message += "<br/> Every "+interval.value+" week";
         message += "<br/> week: "+varWeek;
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
                  months+="January,";
               }
               else if(month[i].value == "1") {
                  months+="February,";
               }
               else if(month[i].value == "2") {
                  months+="March,";
               }
               else if(month[i].value == "3") {
                  months+="April,";
               }
               else if(month[i].value == "4") {
                  months+="May,";
               }
               else if(month[i].value == "5") {
                  months+="June,";
               }
               else if(month[i].value == "6") {
                  months+="July,";
               }
               else if(month[i].value == "7") {
                  months+="August,";
               }
               else if(month[i].value == "8") {
                  months+="September,";
               }
               else if(month[i].value == "9") {
                  months+="October,";
               }
               else if(month[i].value == "a") {
                  months+="November,";
               }
               else if(month[i].value == "b") {
                  months+="December,";
               }
            }
         }
         message += "Monthly,start time:"+hour.value+":"+minute.value;
         if(approach == "0") {
            message += "<br/> Every "+day.value+" day";
         }
         else if(approach == "1") {
            message += "<br/> Week: ";
            message += whichweek.options[whichweek.selectedIndex].text;
            message += " "+week.options[week.selectedIndex].text; 
         }
         message += "<br/> Month: "+months; 
      }
      return parameters;
   }
	//deal with the response 
    function processResponse() {
    	if (XMLHttpReq.readyState == 4) { 
        	if (XMLHttpReq.status == 200) { 
				DisplayHot();
            if(!flag) {
				  // setTimeout("sendRequest()", 1000*60);
            }
         } 
         else { 
                window.alert("request error!");
         }
        }
    }
    function DisplayHot() {	
	    var expression = XMLHttpReq.responseXML.getElementsByTagName("expression")[0].firstChild.nodeValue;
       window.opener.document.getElementById(id).value = expression;
       window.opener.document.getElementById("calendar-expression").innerHTML = message;
       this.close();
	}
</script>
<script src="../javascript/datepicker-new.js" type="text/javascript"></script>
</cmscedit:head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">

<body onload=" initPopCalendar();initDatetime()">
<form method="post" name="form1" action="">
<div >
<p>Please choose the date and time.</p>
<c:choose>
    <c:when test="${type == '1'}">         
        start date : <input type="text" name="date" id="date" size="12"/> <input type="image" class="calendar" src="../media/datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 ,this.form, 'date',event);return false;"/>
        <p>
        start time : <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select><br/>
       <p>  <a href="#"  onclick="javascript:window.close()"/>Cancel</a> <a href="#"  onclick="sheding('1')"/>OK</a></p>
    </c:when>
    <c:when test="${type == '2'}">
        start date : <input type="text" name="date" id="date" size="12"/><input type="image" class="calendar" src="../media/datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 , document.forms[0], 'date',event);return false;"/>
        <p>
        start time :<select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
        </p>
        run the task :</br>                
        <input type="radio" name="strategy" checked id ="strategy" value="0"/>every day</br>
        <input type="radio" name="strategy" id ="strategy"  value="1"/>weekday</br>
        <input type="radio" name="strategy" id ="strategy"  value="2"/>every<input type="text" size="4" name="interval" id="interval" value="1"/> day</br>         
                    
       <p> <a href="#"  onclick="javascript:window.close()"/>Cancel</a> <a href="#"  onclick="sheding('2')"/>OK</a></p>
    </c:when>
    <c:when test="${type == '3'}">
        start time : <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
        <p/>
        every <input type="text" name="interval" size="2"  id="interval" value="1"/> week  <p/>
        select week: <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="1" checked/>Monday
        <input type="checkbox" name="weeks" id="weeks"  value="2" checked/>Tuesday 
        <input type="checkbox" name="weeks" id="weeks"  value="3" checked/>Wednesday <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="4" checked/>Thursday
        <input type="checkbox" name="weeks" id="weeks"  value="5" checked/>Friday
        <input type="checkbox" name="weeks" id="weeks"  value="6" checked/>Saturday <br/>
        <input type="checkbox" name="weeks" id="weeks"  value="7" checked/>Sunday  <br/>
        <p> <a href="#"  onclick="javascript:window.close()"/>Cancel</a> <a href="#"  onclick="sheding('3')"/>OK</a></p>
    </c:when>
    <c:otherwise>
       start time : <select id="hour" name="hour"></select>:<select id="minute" name="minute"></select>
       <br/><p></p>
       <input type="radio" checked name="strategy" id="strategy" value="0"/> day <input size="2" value="1"  type="text" name="day" id="day"><br/>
       <input type="radio" name="strategy" id="strategy" value="1"/> 
       <select name="whichweek" id="whichweek">
          <option value="1">First </option>
          <option value="2">Second </option>
          <option value="3">Third </option>
          <option value="4">Forth </option>
          <option value="5">Last </option>
       </select>
       <select name="week" id="week">
          <option value="1">Monday </option>
          <option value="2">Tuesday </option>
          <option value="3">Wednesday </option>
          <option value="4">Thursday </option>
          <option value="5">Friday </option>
          <option value="6">Saturday </option>
          <option value="7">Sunday</option>
      </select>
      <p>
      Month:<br/>
      <input type="checkbox" name="month" id="month" value="0" checked/>January
      <input type="checkbox" name="month" id="month" value="1" checked/>February
      <input type="checkbox" name="month" id="month" value="2" checked/>March
      <input type="checkbox" name="month" id="month" value="3" checked/>April<br/>
      <input type="checkbox" name="month" id="month" value="4" checked/>May
      <input type="checkbox" name="month" id="month" value="5" checked/>June
      <input type="checkbox" name="month" id="month" value="6" checked/>July
      <input type="checkbox" name="month" id="month" value="7" checked/>August<br/>
      <input type="checkbox" name="month" id="month" value="8" checked/>September
      <input type="checkbox" name="month" id="month" value="9" checked/>October
      <input type="checkbox" name="month" id="month" value="a" checked/>November
      <input type="checkbox" name="month" id="month" value="b" checked/>December <br/>
      <p> <a href="#"  onclick="javascript:window.close()"/>Cancel</a> <a href="#"  onclick="sheding('4')"/>OK</a></p>
   </c:otherwise>
</c:choose>
</div>
</form>
</body>
</mm:cloud>
</html:html>
</mm:content>