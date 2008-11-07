function showInfo(assetType, objectnumber) {
      var infoURL;
      infoURL = '../resources/';
      infoURL += assetType.toLowerCase();
      infoURL += 'info.jsp?objectnumber=';
      infoURL += objectnumber;
      openPopupWindow('imageinfo', '900', '500', infoURL);
}

function unpublish(parentchannel, objectnumber) {
    var url = "DeleteAssetAction.do";
    url += "?channelnumber=" + parentchannel;
    url += "&action=unlink";
    url += "&returnurl=" + escape(document.location + "&refreshchannel=true");
    url += "&objectnumber=" + objectnumber;

    document.location.href = url;
}
