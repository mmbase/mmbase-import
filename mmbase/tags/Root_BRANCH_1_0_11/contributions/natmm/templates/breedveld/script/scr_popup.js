<%@include file="../include/inc_screensize.jsp" %>

function popup(pageToView, toFrame, scrollbar)
{
	frameFeature= "'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=" + scrollbar + ",resizable=yes,width=<%= screenSize.width %>,height=<%= screenSize.height %>'";
	popupWindow=window.open(pageToView, toFrame, frameFeature);
	popupWindow.moveTo(-8,-32);
	popupWindow.focus();
}

function popup_small(pageToView, toFrame, scrollbar)
{
	frameFeature= "'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=" + scrollbar + ",resizable=yes,width=800,height=600'";
	popupWindow=window.open(pageToView, toFrame, frameFeature);
	popupWindow.moveTo(21,13);
	popupWindow.focus();
}
