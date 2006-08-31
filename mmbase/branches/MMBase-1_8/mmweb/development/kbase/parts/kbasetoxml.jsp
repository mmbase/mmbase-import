<%@page contentType="text/xml"%><%@
 taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm" 
 %><?xml version="1.0"?>
<mm:cloud jspvar="wolk" method="asis"><
  mm:import externid="realpath" /><
  mm:import externid="expanded" jspvar="expanded" vartype="String">0</mm:import><
  mm:import externid="node" jspvar="currentDir" vartype="String">0</mm:import
  
  ><treeview title="Kbase category structure">
	<custom-parameters>
		<param name="deploy-treeview" value="true"/>
		<param name="shift-width" value="15"/>
		<param name="img-directory" value="/wieditleestisgek/img/"/>
	</custom-parameters>
  <mm:node number="kbase.root" jspvar="root">
  <mm:field name="number" jspvar="rootnumber" vartype="String" id="nn">
    <%
      //eerst checken of folder expanded is
      //als de parameter 'expanded' de waarde 'all' meekrijgt zijn alle folders expanded
      String thisExpanded=" expanded=\"true\" ";
      //als de boolean editor 'true' is, moeten de folders en vragen waarvan de visibility op 'false'
      //staat wel getoond worden, maar dan met aangepaste icoontjes.
      //als editor 'false' is, moeten deze categorien, vragen en antwoorden niet getoond worden
      boolean editor=true;
      if(root.mayWrite()){
        editor=true;
      }else{
        editor=false;
      }
      if (!"all".equals(expanded))
        thisExpanded=(expanded.indexOf(","+rootnumber+",")>-1?" expanded=\"true\" ":" expanded=\"false\" ");
      String thisImg=(rootnumber.equals(currentDir)?"currentFolder.gif":"folder.gif");
      
    %>
    <folder title="root" <%=thisExpanded%> img="<%=thisImg%>" code="<mm:write referid="nn"/>">
    <% showChildCategories(wolk,out,root,0,expanded,currentDir,editor); %>
    </folder>
    </mm:field>
    </mm:node>
    
</treeview>
</mm:cloud>
<%!

  void showChildCategories(Cloud wolk, JspWriter out, Node category, int indent, String expanded, String currentDir, boolean editor) throws java.io.IOException{
    //eerst alle child categorien ophalen
    //alleen doen als huidige node zichtbaar is of editor='true'

      NodeIterator childCategories=category.getRelatedNodes("kb_category",null,"destination").nodeIterator();
      //en nu aan de xml toevoegen
      Node child;
      boolean visible;
      String thisExpanded, thisImg="", thisImgPrefix="";
      indent++;
      while(childCategories.hasNext()){
        child=childCategories.nextNode();
        visible=child.getBooleanValue("visible");
        if(visible==true || editor==true){
          //en nu de childNodes van deze node
          //eerst checken of folder expanded is
          thisExpanded=" expanded=\"true\" ";
          thisImgPrefix="";
          if (!"all".equals(expanded)){
            thisExpanded=(expanded.indexOf(","+child.getStringValue("number")+",")>-1?" expanded=\"true\" ":" expanded=\"false\" ");
          }
          
          //prefix voor icoonnaam in geval van editor en invisible..
          if(visible==false)thisImgPrefix="hidden";
          
          //dan checken of folder current folder is
          thisImg=(child.getStringValue("number").equals(currentDir)?"currentFolder"+thisImgPrefix+".gif":"folder"+thisImgPrefix+".gif");
          
          //dan xml printen
          out.println(indent(indent)+"<folder title=\""+Xml.XMLEscape(child.getStringValue("name"))+"\" "+
                thisExpanded+"img=\""+thisImg+"\" "+
                (visible?" visible=\"true\" ":" visible=\"false\" ")+
                "code=\""+child.getStringValue("number")+"\">");
          
          //dan de child directories van deze dir uitdraaien
          showChildCategories(wolk,out,child,indent, expanded,currentDir, editor);
          
          //en als laatste de vragen in deze directory uitdraaien
          showQuestions(wolk, out, child, indent, editor);
          out.println(indent(indent)+"</folder>");
        }// else: gewoon niet tonen, en geen recursie meer
      }
    
  }
  
  void showQuestions(Cloud wolk, JspWriter out, Node category,int indent, boolean editor) throws java.io.IOException{
    NodeIterator questions=category.getRelatedNodes("kb_question").nodeIterator();
    Node question;
    boolean visible;
    String thisImgPrefix;
    while (questions.hasNext()){
      thisImgPrefix="";
      question=questions.nextNode();
      visible=question.getBooleanValue("visible");
      if(visible==true ||editor==true){
        
        //prefix voor icoonnaam in geval van editor en invisible..
        if(visible==false)thisImgPrefix="hidden";
        
        out.println(indent(indent) + "<leaf title=\""+Xml.XMLEscape(question.getStringValue("question"))+
            "\" code=\""+question.getStringValue("number")+"\" "+
            (visible?" visible=\"true\" ":" visible=\"false\" ")+
            " img=\"question"+thisImgPrefix+".gif\"/>");
       }
    }
  }
  
  String indent(int indent){
  StringBuffer sb=new StringBuffer();
  for (int i=0;i<indent;i++){
    sb.append("   ");
  }  
  return sb.toString();
  }
%>
<%@
include file="basics.jsp"%>
