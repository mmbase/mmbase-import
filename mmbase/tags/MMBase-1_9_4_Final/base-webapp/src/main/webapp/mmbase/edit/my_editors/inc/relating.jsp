<%-- needs nr, rnr, rkind, dir --%>
<mm:import externid="changed" />
<div id="relate">
<mm:present referid="rnr">
<mm:present referid="rkind">
  <div class="firstrow">
    <h2>Objects related</h2>
  </div>
  <mm:compare referid="dir" value="source">
    <h3>Source</h3>
    <mm:node number="$rnr" id="source_node">
      <p><strong><mm:field name="gui()" /></strong><br />
      <mm:nodeinfo type="guinodemanager" /></p>
    </mm:node> 
    <h3>Destination</h3>
    <mm:node referid="nr" id="dest_node">
      <p><strong><mm:field name="gui()" /></strong><br />
      <mm:nodeinfo type="guinodemanager" /></p>
    </mm:node>
  </mm:compare>
  
  <mm:compare referid="dir" value="destination">
    <h3>Source</h3>
    <mm:node referid="nr" id="source_node">
      <p><strong><mm:field name="gui()" /></strong><br />
      <mm:nodeinfo type="guinodemanager" /></p>
    </mm:node> 
    <h3>Destination</h3>
    <mm:node number="$rnr" id="dest_node">
      <p><strong><mm:field name="gui()" /></strong><br />
      <mm:nodeinfo type="guinodemanager" /></p>
    </mm:node>
  </mm:compare>

  <mm:notpresent referid="changed">
    <%-- Create relation: go to edit relation when it has > 1 editable fields (then it is a relation with a value) --%>
    <mm:maycreaterelation source="source_node" destination="dest_node" role="$rkind">
      <mm:createrelation source="source_node" destination="dest_node" role="$rkind" id="the_relation">
        <mm:fieldlist type="edit"><mm:first><mm:import id="change">ok</mm:import></mm:first></mm:fieldlist>
      </mm:createrelation>
    </mm:maycreaterelation>
    
    <h3>Relation kind</h3> 
    <p>
      <strong><mm:write referid="rkind" /></strong><br />
      <mm:node referid="the_relation"><mm:nodeinfo type="guinodemanager" /></mm:node>
    </p>

  </mm:notpresent>    <%-- / changed --%>

  <mm:present referid="change">
    <%-- Edit relation: needed when a relation has a value (like position f.e.) --%>
    <mm:node referid="the_relation">
      <form method="post" action="<mm:url referids="ntype?,nr,rnr,rkind,dir">
        <mm:param name="the_relation"><mm:field name="number" /></mm:param>
      </mm:url>">
      <fieldset>
      <legend>Edit the relation</legend>
      <mm:fieldlist type="edit">
        <div class="row">        
          <label for="mm_<mm:fieldinfo type="name" />">
            <strong><mm:fieldinfo type="guiname" /></strong>
            <mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
            <mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
          </label>
          <span class="content"><mm:fieldinfo type="input" /></span>
          <mm:fieldinfo type="description"><mm:isnotempty><div class="description" id="descr_<mm:fieldinfo type="name" />"><mm:write /></div></mm:isnotempty></mm:fieldinfo>
        </div>
      </mm:fieldlist>
      <div class="lastrow">
        <label>&nbsp;</label>
        <input type="submit" name="changed" value="Save" />
      </div>
      </fieldset>
      </form>
    </mm:node>
  </mm:present><%-- /change --%>

  <mm:present referid="changed">
    <%-- Save relation --%>
    <mm:import externid="the_relation" required="true" />
    <p class="message">The nodes have been related and your input is saved.</p>
    <h3>The relation</h3>
    <mm:node referid="the_relation">
      <div class="row">
        <label><strong>Kind</strong> </label>
        <mm:nodeinfo type="guitype" />
        (<mm:write referid="rkind" />)
      </div>
      <mm:fieldlist type="edit">
        <mm:fieldinfo type="useinput" />
        <mm:context>
        <div class="row">        
          <label for="mm_<mm:fieldinfo type="name" />">
            <strong><mm:fieldinfo type="guiname" /></strong>
            <a onmouseover="toggle('descr_<mm:fieldinfo type="name" />');return false;" onmouseout="toggle('descr_<mm:fieldinfo type="name" />');return false;"><mm:fieldinfo type="name" /></a>
          </label>
          <mm:fieldinfo type="guivalue" />
        </div>
        </mm:context>
      </mm:fieldlist>
    </mm:node>
    <div class="lastrow">&nbsp;</div>
  </mm:present><%-- /changed --%>

  <mm:notpresent referid="the_relation">
    <div class="mesage">
      <h3>No relation</h3>
      <p>Sorry! You were not allowed to create that relation.</p>
    </div>
  </mm:notpresent><%-- /the_relation --%>


</mm:present><%-- /rkind --%>
</mm:present><%-- /rnr --%>
</div><!-- / #relate -->
