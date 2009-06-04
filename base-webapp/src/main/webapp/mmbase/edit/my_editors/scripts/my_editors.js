/* 
 * Substrings all gui representations that are to long for the related nodes column.
 * Uses jquery.
 */
function substrGui() {
    $('.relgui').each(function(i) {
        var txt = $(this).text();
        if (txt.length > 36) {
            txt = txt.substr(0,36) + "..";
            $(this).text(txt);
        }
    });
    $(".relgui a").each(function(i) {
        var txt = $(this).text();
        if (txt.length > 36) { 
            txt = txt.substr(0,36) + "..";
            $(this).text(txt);
        };
    });
}

// (jquery) onload functions
$(document).ready(function() {
    substrGui();
});
