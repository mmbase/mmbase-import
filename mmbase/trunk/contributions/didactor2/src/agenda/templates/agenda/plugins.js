/////////////////// Plug-in file for CalendarXP 9.0 /////////////////
// This file is totally configurable. You may remove all the comments in this file to minimize the download size.
/////////////////////////////////////////////////////////////////////

///////////// Calendar Onchange Handler ////////////////////////////
// It's triggered whenever the calendar gets changed to y(ear),m(onth),d(ay)
// d = 0 means the calendar is about to switch to the month of (y,m); 
// d > 0 means a specific date [y,m,d] is about to be selected.
// e is a reference to the triggering event object
// Return a true value will cancel the change action.
// NOTE: DO NOT define this handler unless you really need to use it.
////////////////////////////////////////////////////////////////////
// function fOnChange(y,m,d,e) {}


///////////// Calendar AfterSelected Handler ///////////////////////
// It's triggered whenever a date gets fully selected.
// The selected date is passed in as y(ear),m(onth),d(ay)
// e is a reference to the triggering event object
// NOTE: DO NOT define this handler unless you really need to use it.
////////////////////////////////////////////////////////////////////
function fAfterSelected(y,m,d,e) 
{
	var relocateURL = window.top.location;
	relocateURL = "http://" + relocateURL.host + relocateURL.pathname + "?sdate=" + d + "/" + m + "/" + y;
//	relocateURL = relocateURL + "&sdate=" + d + "/" + m + "/" + y;
//	alert(relocateURL);	

	if(document.images)
	{
		parent.location.replace(relocateURL);
	}
	else
	{
		parent.location = relocateURL;
    }
}

///////////// Calendar Cell OnDrag Handler ///////////////////////
// It triggered when you try to drag a calendar cell. (y,m,d) is the cell date. 
// aStat = 0 means a mousedown is detected (dragstart)
// aStat = 1 means a mouseover between dragstart and dragend is detected (dragover)
// aStat = 2 means a mouseup is detected (dragend)
// e is a reference to the triggering event object
// Return true (when aStat=0) to skip the set-date process, as well as any related event handlers (e.g. fAfterSelect).
// NOTE: DO NOT define this handler unless you really need to use it.
////////////////////////////////////////////////////////////////////
// function fOnDrag(y,m,d,aStat,e) {}



////////////////// Calendar OnResize Handler ///////////////////////
// It's triggered after the calendar panel has finished drawing.
// NOTE: DO NOT define this handler unless you really need to use it.
////////////////////////////////////////////////////////////////////
// function fOnResize() {}


////////////////// Calendar fOnWeekClick Handler ///////////////////////
// It's triggered when the week number is clicked.
// NOTE: DO NOT define this handler unless you really need to use it.
////////////////////////////////////////////////////////////////////
// function fOnWeekClick(year, weekNo) {}


////////////////// Calendar fIsSelected Callback ///////////////////////
// It's triggered for every date passed in as y(ear) m(onth) d(ay). And if 
// the return value is true, that date will be rendered using the giMarkSelected,
// gcFGSelected, gcBGSelected and guSelectedBGImg theme options.
// NOTE: If NOT defined here, the engine will create one that checks the gdSelect only.
////////////////////////////////////////////////////////////////////
// function fIsSelected(y,m,d) {
//		return gdSelect[2]==d&&gdSelect[1]==m&&gdSelect[0]==y;
// }


// ====== predefined utility functions for use with agendas. ========
// load an url in the window/frame designated by "framename".
function popup(url,framename) {	
	var w=parent.open(url,framename,"top=200,left=200,width=400,height=200,scrollbars=1,resizable=1");
	if (w&&url.split(":")[0]=="mailto") w.close();
	else if (w&&!framename) w.focus();
}

// return the d(ate) of the q-th n-day of a specific m(onth) in a specific y(ear)
function getDateByDOW(y,m,q,n) { 
// q: 1 - 5 ( 5 denotes the last n-day )
// n: 0 - Sunday, 1 - Monday ... 6 - Saturday
	var dom=new Date(y,m-1,1).getDay();
	var d=7*q-6+n-dom;
	if (dom>n) d+=7;
	if (d>fGetDays(y)[m]) d-=7;
	return d;	// ranged from 1 to 31
}

// ====== Following are self-defined and/or custom-built functions! =======