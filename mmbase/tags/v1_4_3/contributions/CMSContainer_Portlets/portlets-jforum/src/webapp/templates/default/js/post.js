// bbCode control by
// subBlue design
// www.subBlue.com

// Startup variables
var imageTag = false;
var theSelection = false;
var pollOptionCount = -1;

// Check for Browser & Platform for PC & IE specific bits
// More details from: http://www.mozilla.org/docs/web-developer/sniffer/browser_type${extension}
var clientPC = navigator.userAgent.toLowerCase();
// Get client info
var clientVer = parseInt(navigator.appVersion);
// Get browser version

var is_ie = ((clientPC.indexOf("msie") != -1) && (clientPC.indexOf("opera") == -1));
var is_nav = ((clientPC.indexOf('mozilla') != -1) && (clientPC.indexOf('spoofer') == -1)
        && (clientPC.indexOf('compatible') == -1) && (clientPC.indexOf('opera') == -1)
        && (clientPC.indexOf('webtv') == -1) && (clientPC.indexOf('hotjava') == -1));

var is_win = ((clientPC.indexOf("win") != -1) || (clientPC.indexOf("16bit") != -1));
var is_mac = (clientPC.indexOf("mac") != -1);


// Define the bbCode tags
bbcode = new Array();
bbtags = new Array('[b]', '[/b]', '[i]', '[/i]', '[u]', '[/u]', '[quote]', '[/quote]', '[code]', '[/code]', '[list]', '[/list]', '[img]', '[/img]', '[url]', '[/url]');
imageTag = false;

// Shows the help messages in the helpline window
function helpline(help) {
    document.post.helpbox.value = eval(help + "_help");
}


// Replacement for arrayname.length property
function getarraysize(thearray) {
    for (i = 0; i < thearray.length; i++) {
        if ((thearray[i] == "undefined") || (thearray[i] == "") || (thearray[i] == null))
            return i;
    }
    return thearray.length;
}

// Replacement for arrayname.push(value) not implemented in IE until version 5.5
// Appends element to the array
function arraypush(thearray, value) {
    thearray[ getarraysize(thearray) ] = value;
}

// Replacement for arrayname.pop() not implemented in IE until version 5.5
// Removes and returns the last element of an array
function arraypop(thearray) {
    thearraysize = getarraysize(thearray);
    retval = thearray[thearraysize - 1];
    delete thearray[thearraysize - 1];
    return retval;
}

function emoticon(text) {
    text = ' ' + text + ' ';
    if (document.post.message.createTextRange && document.post.message.caretPos) {
        var caretPos = document.post.message.caretPos;
        caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ? text + ' ' : text;
        document.post.message.focus();
    } else {
        document.post.message.value += text;
        document.post.message.focus();
    }
}

function bbfontstyle(bbopen, bbclose) {
    if ((clientVer >= 4) && is_ie && is_win) {
        theSelection = document.selection.createRange().text;
        if (!theSelection) {
            document.post.message.value += bbopen + bbclose;
            document.post.message.focus();
            return;
        }
        document.selection.createRange().text = bbopen + theSelection + bbclose;
        document.post.message.focus();
        return;
    } else {
        document.post.message.value += bbopen + bbclose;
        document.post.message.focus();
        return;
    }
    storeCaret(document.post.message);
}


function bbstyle(bbnumber) {

    donotinsert = false;
    theSelection = false;
    bblast = 0;

    if (bbnumber == -1) { // Close all open tags & default button names
        while (bbcode[0]) {
            butnumber = arraypop(bbcode) - 1;
            document.post.message.value += bbtags[butnumber + 1];

            var btn = eval('document.post.addbbcode' + butnumber);
            if (btn) {
                buttext = btn.value;
                btn.value = buttext.substr(0, buttext.length - 1);
            }
        }
        imageTag = false;
        // All tags are closed including image tags :D
        document.post.message.focus();
        return;
    }

    if ((clientVer >= 4) && is_ie && is_win)
        theSelection = document.selection.createRange().text; // Get text selection

    if (theSelection) {
        // Add tags around selection
        document.selection.createRange().text = bbtags[bbnumber] + theSelection + bbtags[bbnumber + 1];
        document.post.message.focus();
        theSelection = '';
        return;
    }

    // Find last occurance of an open tag the same as the one just clicked
    for (i = 0; i < bbcode.length; i++) {
        if (bbcode[i] == bbnumber + 1) {
            bblast = i;
            donotinsert = true;
        }
    }

    if (donotinsert) {        // Close all open tags up to the one just clicked & default button names
        while (bbcode[bblast]) {
            butnumber = arraypop(bbcode) - 1;
            document.post.message.value += bbtags[butnumber + 1];

            btn = eval('document.post.addbbcode' + butnumber);
            if (btn) {
                buttext = btn.value;
                btn.value = buttext.substr(0, buttext.length - 1);
            }

            imageTag = false;
        }
        document.post.message.focus();
        return;
    } else { // Open tags


        if (imageTag && (bbnumber != 14)) {        // Close image tag before adding another
            document.post.message.value += bbtags[15];
            lastValue = arraypop(bbcode) - 1;
            // Remove the close image tag from the list
            document.post.addbbcode14.value = "Img";
            // Return button back to normal state
            imageTag = false;
        }

        // Open tag
        document.post.message.value += bbtags[bbnumber];
        if ((bbnumber == 14) && (imageTag == false)) imageTag = 1; // Check to stop additional tags after an unclosed image tag
        arraypush(bbcode, bbnumber + 1);

        btn = eval('document.post.addbbcode' + bbnumber);
        if (btn) {
            btn.value += "*";
        }

        document.post.message.focus();
        return;
    }
    storeCaret(document.post.message);
}

// Insert at Claret position. Code from
// http://www.faqts.com/knowledge_base/view.phtml/aid/1052/fid/130
function storeCaret(textEl) {
    if (textEl.createTextRange) textEl.caretPos = document.selection.createRange().duplicate();
}

function previewMessage()
{
    var f = document.post;

    if (supportAjax()) {
        var p = {
            text:f.message.value,
            subject:f.subject.value,
            htmlEnabled:!f.disable_html.checked,
            bbCodeEnabled:!f.disable_bbcode.checked,
            smiliesEnabled:!f.disable_smilies.checked
        };

        AjaxUtils.previewPost(p, previewCallback);
    }
    else {
        f.preview.value = "1";
        f.submit();
    }
}

function previewCallback(post)
{
    document.getElementById("previewSubject").innerHTML = post.subject;
    document.getElementById("previewMessage").innerHTML = post.text;

    document.getElementById("previewTable").style.display = '';
    document.location = "#preview";
}


function deletePollOption(button)
{
    initPollOptionCount();

    var node = button.parentNode;
    while (node != null) {
        if (node.id == "pollOption") {
            node.parentNode.removeChild(node);
            break;
        }

        node = node.parentNode;
    }
}

function addPollOption()
{
    initPollOptionCount();
    incrementPollOptionCount();

    var addOption = document.getElementById("pollOptionWithAdd");
    var deleteOption = document.getElementById("pollOptionWithDelete");
    var newOption = deleteOption.cloneNode(true);

    if (is_nav) {
        newOption.style.display = "table-row";
    }
    else {
        newOption.style.display = "block";
    }

    newOption.id = "pollOption";

    var newTextField = newOption.getElementsByTagName("input")[0];
    var addTextField = newOption.getElementsByTagName("input")[1];

    //copy the active text data to the inserted option
    newTextField.id = "pollOption" + pollOptionCount;
    newTextField.name = "poll_option_" + pollOptionCount;
    newTextField.value = "";

    //clear out the last text field and increment the id
    addTextField.id = "pollOption" + pollOptionCount;
    addTextField.name = "poll_option_" + pollOptionCount;

    addOption.parentNode.insertBefore(newOption, addOption);
    addTextField.focus();
}

function incrementPollOptionCount()
{
    pollOptionCount++;
    document.getElementById("pollOptionCount").value = pollOptionCount;
}

function initPollOptionCount()
{
    if (pollOptionCount == -1) {
        var countField = document.getElementById("pollOptionCount");

        if (countField != null) {
            pollOptionCount = parseInt(countField.value);
        }
        else {
            pollOptionCount = 1;
        }
    }
}