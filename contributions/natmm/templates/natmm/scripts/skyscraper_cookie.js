function ReadCookie(cookieName) {
	var theCookie=""+document.cookie;
	var ind=theCookie.indexOf(cookieName);
	if (ind==-1 || cookieName=="") return ""; 
	var ind1=theCookie.indexOf(';',ind);
	if (ind1==-1) ind1=theCookie.length; 
	return unescape(theCookie.substring(ind+cookieName.length+1,ind1));
}

function SetCookie(cookieName,cookieValue,nDays) {
	var today = new Date();
	var expire = new Date();
	if (nDays==null || nDays==0) nDays=1;
	expire.setTime(today.getTime() + 3600000*24*nDays);
	document.cookie = cookieName+"="+escape(cookieValue) + ";expires="+expire.toGMTString() + ";path=/";
}

function loadSkyscraper() {
	var skyscraperElement = document.getElementById('skyscraper');
	
	if (skyscraperElement != null) { 
		document.getElementById('skyscraper').style.display = 'none';
	
		var skyscraperIsViewed = ReadCookie("skyscraper2_is_viewed");
		//var skyscraperIsViewed = "yes";
		//alert("skyscraperIsViewed: " + skyscraperIsViewed);
		
		if ( skyscraperIsViewed == null || skyscraperIsViewed == "") {
			//alert("showing skyscraper and setting cookie");
			document.body.style.overflow = 'hidden';
			document.getElementById('skyscraper').style.display = 'block';
			document.getElementById('skyscraper').style.visibility = 'visible';
			SetCookie("skyscraper2_is_viewed","yes",365);
		}
	}
}

function closeSkyscraper() {
   document.body.style.overflow = 'scroll';
	document.getElementById('skyscraper').style.display = 'none';
	return false;
}