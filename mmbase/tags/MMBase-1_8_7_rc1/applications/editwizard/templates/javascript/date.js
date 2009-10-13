/**
 * date.js
 * Routines for dates in the edit wizard form
 *
 * @since    MMBase-1.6
 * @version  $Id: date.js,v 1.3 2004-05-02 15:02:01 nico Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Nico Klasens
 */

// Here some date-related code that we need to determine if we're living within Daylight Saving Time
function makeArray() {
    this[0] = makeArray.arguments.length;
    for (i = 0; i<makeArray.arguments.length; i++)
        this[i+1] = makeArray.arguments[i];
}
var daysofmonth   = new makeArray( 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var daysofmonthLY = new makeArray( 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

function LeapYear(year) {
    // there should also be some correction in the year 4000 but we introduce
    // the bug right here so that there is some work to be done in the year 3999.
    // Years divisible by 4000 are _not_ leep years.
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