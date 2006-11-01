<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
  <%@include file="/shared/setImports.jsp" %>

  <mm:import externid="firstname" jspvar="firstname" />
  <mm:import externid="lastname" jspvar="lastname" />
  <mm:import externid="address" />
  <mm:import externid="zipcode" />
  <mm:import externid="city" />
  <mm:import externid="email" jspvar="email"/>
  <mm:import externid="country" />
  <mm:import externid="formsubmit">false</mm:import>

  <mm:import externid="error" />
  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <h2><di:translate key="register.registration" /></h2>
      <ul>
        <mm:write referid="error" escape="none" />
      </ul>
      <table class="registerTable" border="0">
        <form method="post">
          <tr>
            <td><di:translate key="register.firstname" />:</td>
            <td><input name="firstname" value="<mm:write referid="firstname" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.lastname" />:</td>
            <td><input name="lastname" value="<mm:write referid="lastname" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.address" />:</td>
            <td><input name="address" value="<mm:write referid="address" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.zipcode" />:</td>
            <td><input name="zipcode" value="<mm:write referid="zipcode" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.city" />:</td>
            <td><input name="city" value="<mm:write referid="city" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.email" />:</td>
            <td><input name="email" value="<mm:write referid="email" />"/></td>
          </tr>

          <tr>
            <td><di:translate key="register.country" />:</td>
            <td><input name="country" value="<mm:write referid="country" />"/></td>
          </tr>
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
            <td colspan="2">
              <input type="hidden" name="formsubmit" value="true" />
              <mm:present referid="provider">
                <input type="hidden" name="provider" value="<mm:write referid="provider" />" />
              </mm:present>
              <input type="submit" class="formSubmit" value="<di:translate key="register.submit" />" />
            </td>
          </tr>
        </form>
      </table>
      <p>
        <di:translate key="register.extra" />
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:cloud>
</mm:content>
