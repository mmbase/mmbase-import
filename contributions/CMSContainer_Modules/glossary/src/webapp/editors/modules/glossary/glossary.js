function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            if (oldonload) {
                oldonload();
            }
            func();
        }
    }
}

addLoadEvent(function() {

    $A(document.getElementsByTagName("script")).findAll(function(s) {
        return (s.src && s.src.match(/glossary\.js(\?.*)?$/))
    }).each(function(s) {
        var path = s.src.replace(/glossary\.js(\?.*)?$/, '');
        if (path.indexOf("type") > -1) {
            var parameter = s.src.replace(/^.*glossary\.js(\?.*)?&scope=/, '')
            splitGlossaryInClient(parameter);
        }
    });

    var aTags = document.getElementsByTagName("a");
    for (var i = 0; i < aTags.length; i++) {

        if (aTags[i].id.indexOf("_glossary_") > -1) {
            new Tooltip(aTags[i].id)
        }
    }
})

function splitGlossaryInClient(parameter) {
    var params = parameter.split(",");
    for (i = 0; i < params.length; i++) {
        markKeyword(document.getElementById(params[i]));
    }
}

var oldWords = "";

function markKeyword(scope) {

    var content = scope.innerHTML;
    var result = "";

    for (j = 0; j < aDicArray.size(); j++) {


        var word = aDicArray[j];
        if(oldWords.indexOf(":"+word+":")>-1){
            continue;
        }
        var regKeyWord = new RegExp("\\b" + word + "\\b");
        var regLink = new RegExp("<a[^<]*" + word + "[^>]*>");


        while (true) {
            if(!regKeyWord.test(content)){
                result = result + content;
                break;
            }

            var iWordStart = content.search(regKeyWord);
            var iWordStop = iWordStart + word.length;

            if (regLink.test(content)) {
                var iLinkStart = content.search(regLink);
                var iLinkStop = iLinkStart + regLink.exec(content)[0].length;

                if(iWordStart>iLinkStart&&iWordStop<iLinkStop){
                    result = result + content.substring(0,iLinkStop);
                    content = content.substring(iLinkStop,content.length);
                    continue;
                }
            }


            result = result + content.substring(0,iWordStart)+"<a class=\"glossary\" href=\"#\" title=\""+aDesctiptionArray[j]+"\" id=\"_glossary_"+word+"\">"+word+"</a>"+content.substring(iWordStop,content.length);
            oldWords = oldWords+":"+word+":"

            break;
        }

        content = result;
        result = "";
        scope.innerHTML = content;
    }

}

 function getTermDescription() {
     return "testtest"
 }

