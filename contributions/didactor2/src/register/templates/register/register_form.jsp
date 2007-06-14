<%@page session="true" language="java" contentType="text/html; charset=UTF-8"  buffer="500kb"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:cloud method="delegate" authenticate="asis" id="cloud">
<jsp:directive.include file="/shared/setImports.jsp" />
<mm:content postprocessor="none">  

  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <h2><di:translate key="register.registration" /></h2>
      <mm:import externid="formsubmit" />
      <mm:form>

        <mm:import id="buffer">
          <table class="registerTable" border="0">
            
            <mm:treeinclude page="/register/people.fields.jspx" objectlist="$includePath" />
            <mm:treeinclude page="/register/education.jspx"     objectlist="$includePath" />
            <mm:treeinclude page="/register/remarks.jspx"       objectlist="$includePath" />
            
            <tr class="registerSubmit">
              <td colspan="2">
                <input type="hidden" name="formsubmit" value="true" />
                <mm:present referid="provider">
                  <input type="hidden" name="provider" value="${provider}" />
                </mm:present>
                <input type="submit" class="formSubmit" value="${di:translate(pageContext, 'register.submit')}" />
              </td>
            </tr>
          </table>
        </mm:import>
        <mm:present referid="formsubmit">
          <mm:valid>
            <p>
              Form is valid, can be submitted! 
            </p>
            <mm:treeinclude page="/register/submit.jspx"       objectlist="$includePath" />
            
            <mm:commit />

            <mm:treefile id="thanks" cloud="cloud" page="/register/index.jsp"
                         objectlist="$includePath" referids="$referids">
              <mm:param name="thanks" />
            </mm:treefile>


            <mm:log>Redirecting to ${thanks}</mm:log>
            <mm:redirect referid="thanks" />

          </mm:valid>
        </mm:present>

        <mm:write referid="buffer" escape="none" />
      </mm:form>

      <p>
        <di:translate key="register.extra" />
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:content>
</mm:cloud>

