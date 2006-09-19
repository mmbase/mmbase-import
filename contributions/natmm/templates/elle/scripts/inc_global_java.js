//<!--
var abc = Math.random() + "";
var myNum798 = abc.substring(2,abc.length);

function popupmt(target_file,image_file,popwidth,popheight)
{
	if (popwidth == '') popwidth = 300;
	if (popheight == '') popheight = 300;
	window.open ('/common/'+target_file+'?target_file='+image_file, "popup", "toolbar=0,resizable=1,scrollbars=0,status=0,width="+popwidth+",height="+popheight);
}

//wsd - 6/8/04 - common streaming media popup
function popupgallery(section_id,article_id,pop_seq,popwidth,popheight)
{
	windowPop = window.open ('/article.asp?section_id='+section_id+'&article_id='+article_id+'&preview=y&pop_seq='+pop_seq, "articlepopup", "toolbar=0,resizable=0,scrollbars=0,status=0,top=0,left=0,width="+popwidth+",height="+popheight);
	windowPop.focus();
}

function popupart(section_id,article_id,popwidth,popheight)
{
	windowPop = window.open ('/article.asp?section_id='+section_id+'&article_id='+article_id, "articlepopup", "toolbar=0,resizable=0,scrollbars=0,status=0,width="+popwidth+",height="+popheight);
	windowPop.focus();
}
//gc; 09/18/03... func for a popup that is scrollable... 
function popupartscroll(section_id,article_id,popwidth,popheight)
{
	windowPop = window.open ('/article.asp?section_id='+section_id+'&article_id='+article_id, "articlepopup", "toolbar=0,resizable=0,scrollbars=1,status=0,width="+popwidth+",height="+popheight);
	windowPop.focus();
}

//wsd - 6/8/04 - common streaming media popup
function popStream(intStreamID)
{
	window.open ('/common/stream.asp?intStreamID='+intStreamID, "popStream", "toolbar=0,resizable=0,scrollbars=0,status=0,width=300,height=300");
}

//-->