$(document).ready(function() {
    if ($.browser.msie) {
        alert("This page probably won't work in your sorry 'browser'");
    }

    $("h2 a").click(function(ev) {
        var a = this.href;
        var id = a.substring(a.indexOf('#'));
        $("div div").hide();
        $(id + " div").show();
        document.location.href = a;
        $(document).scrollTop($(id).offset().top);
        ev.stopPropagation();
    });
    $("h2").click(function(ev) {
        var n = (ev.pageY - $(this).offset().top) > 20 ?
          $(this).parent("div").next("div") :
          $(this).parent("div").prev("div");
        if (n !== null) {
          n.find("h2 a").click();
        }
      });
    $(document).keypress(function(ev) {
        if (ev.keyCode == 39) {
          $("div div:visible").parent("div").next().find("h2 a").click();
        } else if (ev.keyCode == 37) {
          $("div div:visible").parent("div").prev().find("h2 a").click();
        }
      });
    $("#filler").height($(window).height() - 40);
    var a = document.location.href;
    if (a.indexOf('#') != -1) {
        var id = a.substring(a.indexOf('#'));
        $(id + " h2 a").click();
    }

});
