<!-- simple example of generic search tool -->
<mm:import externid="type">news</mm:import> <!-- you can add type=.. to the url -->
<mm:import externid="pagesize">20</mm:import> <!-- you can add pagesize=.. to the url -->


  <!-- for the 'paging' url's we need a 'base' url which the current search query -->
  <mm:url id="baseurl" referids="type" write="false">
    <mm:fieldlist nodetype="$type" type="list"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
  </mm:url>
  
  <mm:listnodescontainer type="$type">
    <table style="margin: 0 0 0 0;">
      <tr><td style="width: 30%">
      <form name="search">
        <table class="search">
          <tr>
            <th colspan="2">
              <nobr>
                <!-- select type -->
                <select name="type" onchange="document.forms['search'].submit();">
                  <mm:listnodes type="typedef" orderby="name">
                    <option <mm:field name="name"> value="<mm:write />" 
                                <mm:compare referid2="type"> selected="selected"</mm:compare>
                            </mm:field> 
                            >
                            <mm:nodeinfo type="gui" />
                    </option>
                  </mm:listnodes>
                </select>
                <input type="submit" value="ok" />
              </nobr>
            </th>
          </tr>
          <!-- show searchboxes -->
          <mm:fieldlist nodetype="$type" type="search">
            <tr>
              <td><mm:fieldinfo type="guiname" /></td>
              <td style="width: 100%;"><nobr><mm:fieldinfo type="searchinput" /></nobr></td>
            </tr>
          </mm:fieldlist>          
        </table>
      </form>
    </td>
    <td>
      <!-- this table presents the search result -->
      <table class="search">
        <tr>
          <!-- first row, show statics and paging links -->
          <th colspan="100">
            <!-- apply the search query -->
            <mm:fieldlist nodetype="$type" type="list"><mm:fieldinfo type="usesearchinput" /></mm:fieldlist>


            Results: <mm:size id="size" /> 

            <!-- limit and offset the result (paging)  -->
            <mm:maxnumber value="$pagesize" />
            <mm:import externid="offset">0</mm:import>
            <mm:offset    value="$offset" />

            Pages:
            <mm:previousbatches maxtotal="11" indexoffset="1">
              <mm:first><mm:index><mm:compare value="1" inverse="true">...</mm:compare></mm:index></mm:first>
              <a href="<mm:url referids="_@offset" referid="baseurl"  />"><mm:index /></a> , 
            </mm:previousbatches>
            <mm:index offset="1" />
            <mm:nextbatches maxtotal="11" indexoffset="1" >
              <mm:first>,</mm:first>
              <a href="<mm:url referids="_@offset" referid="baseurl" />"><mm:index /></a>
              <mm:last inverse="true">, </mm:last>

              <!-- this is a trick to find out if there are any more pages -->
              <mm:last>                
                <mm:write><mm:islessthan value="$[+ $size - $pagesize]">...</mm:islessthan></mm:write>
              </mm:last>
            </mm:nextbatches>
          </th>
        </tr>
        <!-- second row: show the field names -->
        <tr>
          <th>GUI</th>
          <mm:fieldlist nodetype="$type" type="list">
            <th><mm:fieldinfo type="guiname" /></th>
          </mm:fieldlist>
        </tr>
        
        <!-- and now list the result -->
        <mm:listnodes>
          <tr>
            <td><mm:nodeinfo type="gui" /></td>
            <mm:fieldlist nodetype="$type" type="list">
              <td><mm:fieldinfo type="guivalue" /></td>
              </mm:fieldlist>
            </tr>
        </mm:listnodes>
      </table>
    </td></tr>
  </table>
</mm:listnodescontainer>
  