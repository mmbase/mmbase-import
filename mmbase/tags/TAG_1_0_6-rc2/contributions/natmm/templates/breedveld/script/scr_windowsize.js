<%--
The window size is read through the following sequence of steps
1. scr_windowsize.js: puts the windowWidth in the cookie windowWidth (used in index.jsp)
2. inc_windowsize.jsp: reads the cookie and puts it in the session attribute width (used in index.jsp)
3. inc_windowwidth.jsp: reads the session attribute width and declares and initialises the variable windowWidth (used in nav.jsp, page.jsp and all templates)
--%>
function setWindowSize() {
	var windowWidth;
	if (window.innerWidth) { // for Netscape
		windowWidth = window.innerWidth;
	} else if (document.body) { // for IE
		windowWidth = document.body.clientWidth;
	}
	saveCookie('windowWidth',windowWidth,0);
    <!-- window.alert(readCookie('windowWidth')); -->
}
