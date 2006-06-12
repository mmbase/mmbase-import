var repositoryUrl = 'repository/index.jsp';
var siteUrl = 'site/index.jsp';

function openRepository() {
	window.toppane.selectMenu('repository');
	window.bottompane.location = repositoryUrl;
}

function openRepositoryWithChannel(channelnumber) {
	window.toppane.selectMenu('repository');
	window.bottompane.location = repositoryUrl + '?channel=' + channelnumber;
}

function openRepositoryWithContent(contentnumber) {
	window.toppane.selectMenu('repository');
	window.bottompane.location = repositoryUrl + '?contentnumber=' + contentnumber;
}

function openSite() {
	window.toppane.selectMenu('site');
	window.bottompane.location = siteUrl;
}

function openSiteWithPage(pagenumber) {
	window.toppane.selectMenu('site');
	window.bottompane.location = siteUrl + '?page=' + pagenumber;
}