//alert(getDomain(document.referrer));
if (!(getCookie('mlt_ok')))
{
	if (getDomain(document.location.href) != getDomain(document.referrer) && (document.referrer != ''))
	{
		document.write('<img src=http://web.elle.com/elle.gif?' + escape(document.referrer) + ' width=1 height=1 />');
	}
	setCookie ('mlt_ok', '1', '', '/', document.domain, '')
}

function setCookie(name, value, expires, path, domain, secure) {
  var curCookie = name + "=" + escape(value) +
  //    ((expires) ? "; expires=" + expires.toGMTString() : "") +
      ((path) ? "; path=" + path : "") +
      ((domain) ? "; domain=" + domain : "") +
      ((secure) ? "; secure" : "");
  document.cookie = curCookie;
}

function getCookie(name) {
  var dc = document.cookie;
  var prefix = name + "=";
  var begin = dc.indexOf("; " + prefix);
  if (begin == -1) {
    begin = dc.indexOf(prefix);
    if (begin != 0) return null;
  } else
    begin += 2;
  var end = document.cookie.indexOf(";", begin);
  if (end == -1)
    end = dc.length;
  return unescape(dc.substring(begin + prefix.length, end));
}

function getDomain(url) {
	var host, tld, sld, begin, end, remaining;
	begin = url.indexOf('//') + 2;
	if (url.indexOf('/',8)) 
	{
		end = url.indexOf('/',8);
	} else {
		end = url.length
	}
	return url.substring(begin, end);
}