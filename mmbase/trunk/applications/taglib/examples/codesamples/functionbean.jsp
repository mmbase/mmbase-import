<%!
public static class MyClass {

  public MyClass() {
  }
  private String p;
  public void setMyParameter(String p) {
    this.p = p;
  }
  public String myfunction() {
   return p;
  }
}
%>
<mm:functioncontainer>
   <mm:param name="MyParameter">Kloink</mm:param>
   <mm:function  classname="THISPAGE.MyClass" name="myfunction" />
</mm:functioncontainer>