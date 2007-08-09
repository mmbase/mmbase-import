var tmpWeekControl
var tmpYearControl

// popup calendar
// p_week : form field
// p_year : form field
// usage : <input type=button onClick="popup_calendar('document.testform.week', 'document.testform.jaar')" value='...'>
function popup_calendar(p_week, p_year) {
   var newWindow;
   var urlstring = "../../calendar/calendar.html?week=" + p_week + "&year=" + p_year;
   this.tmpWeekControl = p_week;
   this.tmpYearControl = p_year;
   newWindow = window.open(urlstring,'_popUpCal','height=200,width=280,toolbar=no,minimize=no,status=no,memubar=no,location=no,scrollbars=no')
}

//
function popup_calendar_year(p_year) {
   var newWindow;
   var urlstring = "../../calendar/calendar.html";
   this.tmpWeekControl = "0";
   this.tmpYearControl = p_year;
   newWindow = window.open(urlstring,'_popUpCal','height=200,width=280,toolbar=no,minimize=no,status=no,memubar=no,location=no,scrollbars=no');
}
