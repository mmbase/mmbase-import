function showEmail(beforeAt, afterAt)
{
	return beforeAt + "@" + afterAt;
}

var starOn = new Image();
starOn.src = "${contextPath}/templates/${templateName}/images/star_on.gif";

var starOff = new Image();
starOff.src = "${contextPath}/templates/${templateName}/images/star_off.gif";

function writeStars(q, postId)
{
	for (var i = 0; i < 5; i++) {
		var name = "star" + postId + "_" + i;
		document.write("<img name='" + name + "' alt='*' />");
		document.images[name].src = q > i ? starOn.src : starOff.src;
	}
}

function addBookmark(relationType, relationId)
{
	//var w = window.open('${JForumContext.encodeURL("/bookmarks/insert/' + relationType + '/' + relationId + '")}', 'bookmark_add', 'width="700", height="200", scrollbars="no"');
	// portlet fork: not encode url
    var w = window.open('${contextPath}/bookmarks/insert/' + relationType + '/' + relationId + '${extension}', 'bookmark_add', 'width="700", height="200", scrollbars="no"');
    w.focus();
    //document.location = '${JForumContext.encodeURL("/bookmarks/insert/' + relationType + '/' + relationId + '")}';
}

function supportAjax()
{
	if (typeof(AjaxUtils) != 'undefined') {
		if (window.ActiveXObject) {
			var r = new ActiveXObject("Microsoft.XMLHTTP");
            return r != undefined;
		}
		else if (window.XMLHttpRequest) {
            return true;
		}
	}
	return false;
}
