/*
<%@page contentType="text/javascript; charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/javascript">
*/
$(document).ready(
    function() {
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
                              function(ev, div, b) {
                                  // arrange lazy loading
                                  var li = ev.target;
                                  var pdiv = $(li).closest("div.list");
                                  var rid = pdiv[0].list.rid;
                                  // Hmm, in jquery 1.3 it seems that div is not correctly arriving
                                  div.find("div.unloaded").each(
                                      function() {
                                          var unloadedblock = $(this);
                                          // a.lazyloading child stores information about what must be loaded
                                          var a = unloadedblock.find("a.lazyloading");
                                          var classes = a.attr("class").split(' ');
                                          var node;
                                          for (var i in classes) {
                                              var cl = classes[i];
                                              if (cl.indexOf("node_") == 0) {
                                                  node = cl.substring("node_".length);
                                              }
                                          }
                                          var href = a.attr("href");
                                          var lazypart = "${mm:link('/mmbase/searchrelate/list/lazy-part.jspx')}";

                                          var id = $(li).attr("id"); // TODO: find a nice, reproducable, unique id for this item, preferable without the node-number, because that
                                          id = id.replace("-", "_"); // should be valid context identifier

                                          // changes after commit of a new node (:-()
                                          unloadedblock.load(lazypart,
                                                             {requestID: id,
                                                              href: href,
                                                              rid: rid,
                                                              node: node
                                                             },
                                                             function(responseText, textStatus) {
                                                                 if ("success" == textStatus) {
                                                                     $(this).trigger("mmsr_lazyloaded", []);
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


    }
);

/*</mm:content>*/