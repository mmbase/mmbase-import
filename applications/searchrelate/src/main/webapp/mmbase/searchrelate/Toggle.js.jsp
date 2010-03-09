$(document).ready(function() {
    $("a.toggle").live("click",
                       function() {
                           var a = this;
                           var body = $(a).siblings("div.toggle_body");
                           body.toggle();
                           $(a).toggle();
                           $(a).siblings("a.toggle").toggle();
                           var li = $(a).closest("li");
                           $(li).trigger("mmsrToggle", [body]);
                       });
    $("div.list li").live("mmsrToggle",
                          function(ev, div) {
                              // arrange lazy loading
                              var li = ev.target;
                              div.find("div.unloaded").each(
                                  function() {
                                      var unloadedblock = $(this);
                                      // a.lazyloading child stores information about what must be loaded
                                      var a = unloadedblock.find("a.lazyloading");
                                      var href = a.attr("href");
                                      //                                 var id = blockId.replace("-", "_");;

                                      var id = $(li).attr("id"); // TODO: find a nice, reproducable, unique id for this item, preferable without the node-number, because that
                                      // changes after commit of a new node (:-()
                                      unloadedblock.load(href,
                                                         {requestID: id,
                                                          "org.mmbase.sr.relatednodes.load": "false"},
                                                         function(responseText, textStatus) {
                                                             if ("success" == textStatus) {
                                                                 // switch on mm-sr stuf in this newly loaded block too
                                                                 List.prototype.init(this);
                                                             } else {
                                                                 // well, it doesn't work.
                                                                 // Show at least that.
                                                                 unloadedblock.empty().append(textStatus);
                                                             }
                                                         }
                                                        );
                                      unloadedblock.removeClass("unloaded"); // not any more unloaded
                                      // mark the block as loaded in the 'lazyloaded' form entry too:
                                      //                                 self.getLoadedLazyBlocks(blockId);

                                  });
                          });


});