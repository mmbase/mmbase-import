function callEditWizard(objectNumber) {
    var url = '../WizardInitAction.do?objectnumber=' + objectNumber;
    url += '&returnurl=' + escape(document.location);
    document.location = url;
}

function unpublish(channelnumber, objectnumber) {
    var url = "LinkToChannelAction.do";
    url += "?channelnumber=" + channelnumber;
    url += "&action=unlink";
    url += "&returnurl=" + escape(document.location + "&refreshchannel=true");
    url += "&objectnumber=" + objectnumber;

    document.location.href = url;
}

function openPreview(url) {
    return openPopupWindow("preview", 750, 550, url);
}

function showItem(objectnumber) {
    return openPopupWindow("showItem", 500, 500, 'showitem.jsp?objectnumber=' + objectnumber);
}

function info(objectNumber) {
    openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
}

function moveUp(objectNumber, channel) {
    move("up", objectNumber, channel);
}

function moveDown(objectNumber, channel) {
    move("down", objectNumber, channel);
}

function move(direction, objectNumber, channel) {
    var url = 'MoveContent.do?direction=' + direction + '&objectnumber=' + objectNumber + '&parentchannel=' + channel;
    document.location = url;
}

var moveContentNumber;
var moveParentChannel;
function moveContent(objectNumber, parentChannel) {
    moveContentNumber = objectNumber;
    moveParentChannel = parentChannel;
    openPopupWindow('selectchannel', 340, 400);
}

function selectChannel(channel, path) {
    document.location = "../MoveContentToChannel.do?parentchannel=" + moveParentChannel + "&newparentchannel=" + channel + "&objectnumber=" + moveContentNumber;
}

function refreshChannels() {
    refreshFrame('channels');
    if (window.opener) {
        window.close();
    }
}

function deleteContent(objectnumber, confirmmessage) {
    if (confirmmessage) {
        if (confirm(confirmmessage)) {
            document.forms[0].deleteContentRequest.value = "permanentDelete:" + objectnumber;
            document.forms[0].submit();
        }
    }
    else {
        document.forms[0].deleteContentRequest.value = "moveToRecyclebin:" + objectnumber;
        document.forms[0].submit();
    }

}
