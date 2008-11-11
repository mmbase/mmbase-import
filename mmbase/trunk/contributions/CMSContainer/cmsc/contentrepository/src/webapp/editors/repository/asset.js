function showInfo(assetType, objectnumber) {
      var infoURL;
      infoURL = '../resources/';
      infoURL += assetType.toLowerCase();
      infoURL += 'info.jsp?objectnumber=';
      infoURL += objectnumber;
      openPopupWindow('imageinfo', '900', '500', infoURL);
}

function unpublish(parentchannel, objectnumber) {
    var url = "AssetDeleteAction.do";
    url += "?channelnumber=" + parentchannel;
    url += "&action=unlink";
    url += "&returnurl=" + escape(document.location + "&refreshchannel=true");
    url += "&objectnumber=" + objectnumber;

    document.location.href = url;
}

function selectChannel(channel, path) {
	var newDirection=document.forms['initForm'].direction.value;
	var type=document.forms['initForm'].order.value;
	var offset = document.forms['initForm'].offset.value;
	document.location = "../Asset.do?action=moveAssetToChannel&parentchannel=" + moveParentChannel
			+ "&newparentchannel=" + channel + "&objectnumber="
			+ moveContentNumber + "&orderby=" + type + "&direction="
			+ newDirection + '&offset=' + offset;
}