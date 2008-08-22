<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">
      <mm:import externid="command">-1</mm:import>
      <mm:compare referid="command" value="deleterole">
        <di:has editcontext="rollen" action="rwd">
          <mm:import externid="rolenumber"/>
          <mm:node number="$rolenumber">
            <mm:deletenode deleterelations="true"/>
          </mm:node>
        </di:has>
        <di:has editcontext="rollen" action="rwd" inverse="true">
          <p>No rights to delete role</p>
        </di:has>
      </mm:compare>
      <mm:compare referid="command" value="accept">
        <di:has editcontext="rollen" action="rw">
          <mm:listnodes type="editcontexts" orderby="number" id="this_editcontext">
            <mm:context>
              <mm:listnodes type="roles" orderby="number" id="this_role">
                <mm:import id="select" externid="select_${this_editcontext}_${this_role}">0</mm:import>
                <mm:related path="posrel,editcontexts" constraints="editcontexts.number='$this_editcontext'">
                  <mm:node element="posrel" id="old_rel">
                    <mm:field name="pos" write="false">
                      <c:if test="${_ ne select}">
                        <p>Setting relation ${_node}.pos=${_node.pos} -> ${select}</p>
                        <mm:setfield>${select}</mm:setfield>
                      </c:if>
                    </mm:field>
                  </mm:node>
                </mm:related>
                <c:if test="${empty old_rel}">
                  <mm:createrelation role="posrel" source="this_role" destination="this_editcontext">
                    <p>Creating relation ${this_role} -> ${this_editcontext}</p>
                    <mm:setfield name="pos">${select}</mm:setfield>
                  </mm:createrelation>
                </c:if>
              </mm:listnodes>
            </mm:context>
          </mm:listnodes>
        </di:has>
        <di:has editcontext="rollen" action="rw" inverse="true">
          <p>No rights to edit role</p>
        </di:has>
      </mm:compare>
    </mm:cloud>
    <jsp:include page="roles.jsp"/>
  </mm:content>
</jsp:root>
