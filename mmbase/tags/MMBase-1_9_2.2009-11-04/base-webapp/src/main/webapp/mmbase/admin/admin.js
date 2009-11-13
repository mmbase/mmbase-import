$(document).ready(function() {
    $("div.mm_c.iframe iframe").height($(window).height());
});

$(window).resize(function() {
    $("div.mm_c.iframe iframe").height($(window).height());
});
