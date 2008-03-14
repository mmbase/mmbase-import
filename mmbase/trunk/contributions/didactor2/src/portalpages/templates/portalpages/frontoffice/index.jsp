<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:cloud method="asis">
    <mm:link page="/education/js/frontend_tree.jsp" referids="$referids">
      <script type="text/javascript" src="${_}">
        <!-- help IE -->
      </script>
    </mm:link>
    <div class="rows">
      <div class="navigationbar">
        <div class="pathbar">
          <mm:node number="$provider">
            <mm:field name="name"/>
          </mm:node>
        </div>
        <div class="stepNavigator">
          <di:include page="/education/prev_next.jsp" />
        </div>
      </div>

      <mm:time time="today" write="false" id="today" />
      <div class="folders">
        <div class="folderLesBody">

          <mm:listnodes type="portalpagescontainers" max="1" > <!-- This makes no sense whatsoever -->

            <mm:relatednodescontainer type="portalpagesnodes" role="related" searchdirs="destination">
              <mm:constraint field="active" value="true"/>
              <mm:sortorder field="order_number" direction="up" />
              <!-- main div for all root portal pages -->
              <div class="lbLevel1">
                ach ach
                <mm:treecontainer
                    role="childppnn"
                    searchdirs="destination"
                    type="portalpagesnodes">
                  <mm:sortorder field="order_number" direction="up" />
                  <mm:constraint field="active" value="true" />
                  <mm:tree maxdepth="3">
                    <mm:nodeinfo type="type" id="nodetype" write="false" />

                    <div id="div" class="lbLevel">
                      bloe bloe
                      <mm:relatednodescontainer path="simplecontents" searchdirs="destination">
                        <mm:constraint referid="today" field="online_date"  operator="LESS" />
                        <mm:constraint referid="today" field="offline_date" operator="GREATER" />
                        <mm:relatednodes>
                          <div style="padding: 0px 0px 0px  10px;">
                            <img class="imgClosed" src=""
                                 id="img${_node}"
                                 onclick="openClose('div${_node}','img${_node}')"
                                 style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />
                            <a href="javascript:openContent('simplecontents', '${_node}' ); openOnly('div${_node}','img${_node}');"
                               style="padding-left: 0px"><mm:field name="title"/></a>
                          </div>
                        </mm:relatednodes>
                      </mm:relatednodescontainer>
                    </div>
                  </mm:tree>
                </mm:treecontainer>
              </div>
            </mm:relatednodescontainer>
          </mm:listnodes>
        </div>

      </div>
    </div>
  </mm:cloud>
</jsp:root>
