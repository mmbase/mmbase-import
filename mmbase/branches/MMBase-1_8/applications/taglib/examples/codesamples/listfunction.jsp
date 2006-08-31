<%!
public static java.util.List listFunction() {
   return java.util.Arrays.asList(new String[] {"abc", "def", "ghij"});
}
%>
<mm:listfunction set="THISPAGE" name="listFunction">
   <mm:write /><mm:last inverse="true">, </mm:last>
</mm:listfunction>