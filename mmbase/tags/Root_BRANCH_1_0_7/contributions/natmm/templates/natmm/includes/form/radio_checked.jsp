<mm:field name="formulierveldantwoord.standaard" jspvar="defaultValue" vartype="Integer"><%
      if((!bDefaultIsSet) && (defaultValue.intValue() != 0))
      {
         %>checked="checked"<%
         bDefaultIsSet = true;
      }
%></mm:field>
