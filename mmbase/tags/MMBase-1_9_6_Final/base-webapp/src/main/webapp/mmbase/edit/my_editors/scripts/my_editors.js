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

function showOwnerField() {
    if ($('div.owner').length > 0) {
        $('div.owner').each(function(index){
            var self = this;
            $(this).find('span.input').hide();
            $(this).find('span.guivalue').click(function(){
                $(self).find('span.guivalue').hide();
                $(self).find('span.input').show();
            });
            $(this).find('a.close').click(function(ev){
                ev.preventDefault();
                $(self).find('span.input').hide();
                $(self).find('span.guivalue').show();
            });
        });
    }
}

// (jquery) onload functions
$(document).ready(function() {
    substrGui();
    showOwnerField();
});
