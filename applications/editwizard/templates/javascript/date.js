/**
 * date.js
 * Routines for dates in the edit wizard form
 *
 * @since    MMBase-1.6
 * @version  $Id: date.js,v 1.1 2003-12-19 11:09:08 nico Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Nico Klasens
 */

// Here some date-related code that we need top determine if we're living within Daylight Saving Time
function makeArray() {
    this[0] = makeArray.arguments.length;
    for (i = 0; i<makeArray.arguments.length; i++)
        this[i+1] = makeArray.arguments[i];
}
var daysofmonth   = new makeArray( 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var daysofmonthLY = new makeArray( 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

function LeapYear(year) {
    return ((year  % 4 == 0) && !( (year % 100 == 0) && (year % 400 != 0)));
}

function NthDay(nth,weekday,month,year) {
    if (nth > 0) return (nth-1)*7 + 1 + (7 + weekday - DayOfWeek((nth-1)*7 + 1,month,year))%7;
    if (LeapYear(year)) var days = daysofmonthLY[month];
    else                var days = daysofmonth[month];
    return days - (DayOfWeek(days,month,year) - weekday + 7)%7;
}

function DayOfWeek(day,month,year) {
    var a = Math.floor((14 - month)/12);
    var y = year - a;
    var m = month + 12*a - 2;
    var d = (day + y + Math.floor(y/4) - Math.floor(y/100) + Math.floor(y/400) + Math.floor((31*m)/12)) % 7;
    return d+1;
}

function getDate(elementvalue) {
    var ms = 1000 * elementvalue;

    var d = new Date();
    d.setTime(ms);
    var year = d.getFullYear();

    // Here we'll  calculate the start and end of Daylight Saving Time
    // We need that in order to display correct date and times in IE on Macintosh
    var DSTstart = new Date(year,4-1,NthDay(1,1,4,year),2,0,0);
    var DSTend   = new Date(year,10-1,NthDay(-1,1,10,year),2,0,0);
    var DSTstartMS = Date.parse(DSTstart);
    var DSTendMS = Date.parse(DSTend);

    // If Daylight Saving Time is active and clientNavigator=MSIE/Mac, add 60 minutes 
    if ((navigator.appVersion.indexOf('MSIE')!=-1) 
        && (navigator.appVersion.indexOf('Mac')!=-1)
        && (ms > DSTstartMS) && (ms < DSTendMS)) {
        
        d.setTime((1000 * elementvalue) + (1000*60*60));
    }
    return d;
}

function getDateSeconds(ms) {
    var d = new Date();
    d.setTime(ms);
    var year = d.getFullYear();

    // Here we'll  calculate the start and end of Daylight Saving Time
    // We need that in order to display correct date and times in IE on Macintosh
    var DSTstart = new Date(year,4-1,NthDay(1,1,4,year),2,0,0);
    var DSTend   = new Date(year,10-1,NthDay(-1,1,10,year),2,0,0);
    var DSTstartMS = Date.parse(DSTstart);
    var DSTendMS = Date.parse(DSTend);

    // If Daylight Saving Time is active and clientNavigator=MSIE/Mac, add 60 minutes 
    if ((navigator.appVersion.indexOf('MSIE') != -1)
         && (navigator.appVersion.indexOf('Mac') != -1)
         && (ms > DSTstartMS) && (ms < DSTendMS)) {

        return Math.round(ms/1000-(60*60));
    } else {
        return Math.round(ms/1000); // - (60*d.getTimezoneOffset()));
    }
}