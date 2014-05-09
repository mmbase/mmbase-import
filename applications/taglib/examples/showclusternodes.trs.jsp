        <mm:first>
          <tr>
            <mm:compare referid="fields" value="">
              <mm:stringlist referid="path">
                <mm:node element="$_">
                  <mm:fieldlist type="list">
                    <th><mm:fieldinfo type="guiname"/></th>
                  </mm:fieldlist>
                </mm:node>
              </mm:stringlist>
            </mm:compare>
            <mm:compare referid="fields" value="" inverse="true">
              <mm:stringlist referid="fields">
                <th>
                  <mm:write />
                </th>
              </mm:stringlist>
            </mm:compare>
          </tr>
        </mm:first>
        <tr>
          <mm:compare referid="fields" value="">
            <mm:stringlist referid="path">
              <mm:node element="$_">
                <mm:fieldlist type="list">
                  <td><mm:fieldinfo type="value"/></td>
                </mm:fieldlist>
              </mm:node>
            </mm:stringlist>
          </mm:compare>
          <mm:compare referid="fields" value="" inverse="true">
            <mm:stringlist referid="fields">
              <td>
                <mm:field name="$_" />
              </td>
            </mm:stringlist>
          </mm:compare>
        </tr>