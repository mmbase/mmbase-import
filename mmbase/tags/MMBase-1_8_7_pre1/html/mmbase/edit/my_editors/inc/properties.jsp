<div class="row">
  <label><strong>Properties</strong></label>
  <div class="properties">
    <mm:nodelistfunction nodemanager="properties" name="list" referids="_node@node">
      <mm:maydelete>
        <mm:link page="delete_object.jsp" referids="_node@nr"><a href="${_}" title="delete property"><img src="img/mmbase-delete.png" alt="delete" width="21" height="20" /></a></mm:link>
      </mm:maydelete>
      <mm:maywrite><mm:link page="edit_object.jsp" referids="_node@nr"><a href="${_}" title="edit property"></mm:link></mm:maywrite><mm:field name="key" /><mm:maywrite></a></mm:maywrite>:<mm:field name="value" />
    </mm:nodelistfunction>
    <mm:present referid="change">
      <c:if test="${!empty property_key && !empty property_value}">
        <mm:function nodemanager="properties" name="set" referids="_node@node,property_key@key,property_value@value" />
      </c:if>
    </mm:present>
  </div>
</div>
<div class="row">
  <label for="property_key">
    <strong>New key</strong>
	<a onmouseover="showBox('descr_prop',event);return false;" onmouseout="showBox('descr_prop',event);return false;">key</a>
  </label>
  <input type="text" id="property_key" name="property_key" size="80" maxlength="255" class="small" />
</div>
<div class="row">
  <label for="property_value">
    <strong>New value</strong>
	<a onmouseover="showBox('descr_prop',event);return false;" onmouseout="showBox('descr_prop',event);return false;">value</a>
  </label>
  <input type="text" id="property_value" name="property_value" size="80" maxlength="255" class="small" /><br />
  <div class="description" id="descr_prop"><mm:nodeinfo type="description" nodetype="properties" /></div>
</div>
