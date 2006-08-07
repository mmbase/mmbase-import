var repositoryUrl = 'repository/index.jsp';
var siteUrl = 'site/index.jsp';

function openRepository() {
	window.bottompane.location = repositoryUrl;
}

function openRepositoryWithChannel(channelnumber) {
	window.bottompane.location = repositoryUrl + '?channel=' + channelnumber;
}

function openRepositoryWithContent(contentnumber) {
	window.bottompane.location = repositoryUrl + '?contentnumber=' + contentnumber;
}

function openSite() {
	window.bottompane.location = siteUrl;
}

function openSiteWithPage(pagenumber) {
	window.bottompane.location = siteUrl + '?page=' + pagenumber;
}