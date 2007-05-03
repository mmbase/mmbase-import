<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud method="delegate" authenticate="asis">
  <%@include file="/shared/setImports.jsp" %>  
<mm:content postprocessor="reducespace" language="$language" >


  <mm:import externid="firstname" />
  <mm:import externid="lastname" />
  <mm:import externid="suffix" />
  <mm:import externid="address" />
  <mm:import externid="zipcode" />
  <mm:import externid="city" />
  <mm:import externid="email" jspvar="email"/>
  <mm:import externid="country" />
  <mm:import externid="remarks" />
  <mm:import externid="formsubmit">false</mm:import>

  <mm:import externid="error" />
  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <h2><di:translate key="register.registration" /></h2>
      <mm:write referid="error" escape="none">
        <mm:isnotempty>
          <ul>
            <mm:write escape="none" />
          </ul>
        </mm:isnotempty>
      </mm:write>
      <form method="post">          
        <table class="registerTable" border="0">
          <mm:fieldlist nodetype="people" fields="${di:setting(pageContext, 'core', 'admin_personfields')},address,zipcode,city,email,country" id="field">
            <tr>
              <td><mm:fieldinfo type="guiname" />:</td>
              <mm:write referid="${field.name}">
                <td><input name="${field.name}" value="${_}"/></td>
              </mm:write>
            </tr>
          </mm:fieldlist>
          <tr>
            <td><di:translate key="register.education" />:</td>
            <td>
              <mm:present referid="education">
                <input type="hidden" name="education" value="<mm:write referid="education" />" />
                <mm:node number="$education">
                  <mm:field name="name" />
                </mm:node>
              </mm:present>
              <mm:notpresent referid="education">
                <mm:node number="component.register">
                  <mm:relatednodescontainer type="educations">
                    <mm:size id="nreducations" write="false" />
                    <mm:islessthan referid="nreducations" value="2">
                      <mm:relatednodes>
                        <input type="hidden" name="education" value="<mm:field name="number" />" />
                        <mm:field name="name" />
                      </mm:relatednodes>
                    </mm:islessthan>
                    <mm:islessthan referid="nreducations" value="2" inverse="true">
                      <mm:relatednodes>
                        <mm:first>
                          <select name="education">
                        </mm:first>
                        <option value="<mm:field name="number" />"><mm:field name="name" /></option>
                        <mm:last>
                          </select>
                        </mm:last>
                      </mm:relatednodes>
                    </mm:islessthan>
                  </mm:relatednodescontainer>
                </mm:node>
              </mm:notpresent>
            </td>
          </tr>
          <tr>
            <td><di:translate key="register.remarks" />:</td>
            <td><textarea name="remarks">${remarks}</textarea></td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="hidden" name="formsubmit" value="true" />
              <mm:present referid="provider">
                <input type="hidden" name="provider" value="<mm:write referid="provider" />" />
              </mm:present>
              <input type="submit" class="formSubmit" value="<di:translate key="register.submit" />" />
            </td>
          </tr>
        </table>
      </form>                
      <p>
        <di:translate key="register.extra" />
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:content>
</mm:cloud>

