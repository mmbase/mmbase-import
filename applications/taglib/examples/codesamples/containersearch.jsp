<!-- simple example of generic search tool -->
<mm:import externid="type">news</mm:import> <!-- you can add type=.. to the url -->


  <!-- for the 'paging' url's we need a 'base' url which the current search query -->
  <mm:url id="baseurl" write="false">
    <mm:fieldlist nodetype="$type" type="list"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
  </mm:url>
  
  <mm:listnodescontainer type="$type">
    <table><tr>
    <td style="width: 100%">
    <form name="search">
      <table class="search">
        <tr>
          <td>
            <!-- select type -->
            <select name="type" onchange="document.forms['search'].submit();">
              <mm:listnodes type="typedef" orderby="name">
                <option <mm:field name="name"> value="<mm:write />" <mm:compare referid2="type"> selected="selected"</mm:compare></mm:field>"><mm:nodeinfo type="gui" /></option>
              </mm:listnodes>
            </select>
          </td>
          <td> Results: <mm:size /> <input type="submit" /></td>
        </tr>
        <mm:fieldlist nodetype="$type" type="search"><tr><td><mm:fieldinfo type="guiname" /></td><td><nobr><mm:fieldinfo type="searchinput" /></nobr></td></tr></mm:fieldlist>

      </table>
    </form>
    </td>
    <td>
    <table class="search">
      <!-- show the field names -->
      <tr><mm:fieldlist nodetype="$type" type="list"><th><mm:fieldinfo type="guiname" /></th></mm:fieldlist></tr>
      
      <!-- apply the search query -->
      <mm:fieldlist nodetype="$type" type="list"><mm:fieldinfo type="usesearchinput" /></mm:fieldlist>
      
      <!-- 'page size' -->
      <mm:maxnumber value="20" />
      
      <mm:listnodes>
        <tr><mm:fieldlist nodetype="$type" type="list"><td><mm:fieldinfo type="guivalue" /></td></mm:fieldlist></tr>
      </mm:listnodes>
    </table>
    </td>
    </tr></table>
  </mm:listnodescontainer>
  


