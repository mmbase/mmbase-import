package nl.leocms.authorization.forms;

import java.io.PrintWriter;
import java.io.Writer;
import org.mmbase.bridge.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;


public class CheckBoxTree{

  public void render(Writer out, Cloud cloud, HttpServletRequest request){
    PrintWriter pw = new PrintWriter(out);
    pw.println("<script language=\"JavaScript\">");
    pw.println("   function checkEditwizards(menu){");
    pw.println("      with(document.forms[0]) {");
    pw.println("         var checked = document.getElementById('menu_' + menu).checked;");
    pw.println("         for (var i = 0; i<elements.length; i++) {");
    pw.println("            if (elements[i].name.indexOf('ed_' + menu)>-1){");
    pw.println("               elements[i].checked = checked;");
    pw.println("               elements[i].disabled = checked;");
    pw.println("            }");
    pw.println("         }");
    pw.println("      }");
    pw.println("   }");
    pw.println("</script>");
    String id = request.getParameter("id");
    LinkedHashMap lhmMenuEditwizards = new LinkedHashMap();
    NodeList nl = cloud.getList("","menu,posrel,editwizards","menu.number","","menu.naam","UP","",false);
    for (int i=0; i<nl.size();i++){
      String sMenuNumber = nl.getNode(i).getStringValue("menu.number");
      String sMenuName = nl.getNode(i).getStringValue("menu.naam");
      if (!lhmMenuEditwizards.containsKey(sMenuNumber)){
        lhmMenuEditwizards.put(sMenuNumber,sMenuName);
      }
    }
    pw.println("<table class=\"formcontent\">");
    Set set = lhmMenuEditwizards.entrySet();
    Iterator it = set.iterator();
    while (it.hasNext()){
      Map.Entry me = (Map.Entry)it.next();
      String sMenuNumber = (String) me.getKey();
      pw.print("<tr><td class=\"field\" style=\"width:20px\"><input type=\"checkbox\" name=\"menu_" + sMenuNumber + "\" ");
      pw.print("onClick=\"checkEditwizards('" + sMenuNumber+ "');\" ");
      NodeList nlMenuUsers = cloud.getList(sMenuNumber,"menu,gebruikt,users","users.number","users.number='" + id + "'","","UP","",false);
      if (nlMenuUsers.size()>0){
        pw.print("checked");
      }
      pw.print("></td><td colspan=\"2\" class=\"fieldname\">");
      pw.print(me.getValue());
      pw.println("</td></tr>");
      NodeList nlEditwizards = cloud.getList(sMenuNumber,"menu,posrel,editwizards","editwizards.number,editwizards.name","","posrel.pos","UP","",false);
      for (int i=0; i<nlEditwizards.size();i++){
        String sEditwizardNumber = nlEditwizards.getNode(i).getStringValue("editwizards.number");
        String sEditwizardName = nlEditwizards.getNode(i).getStringValue("editwizards.name");
        pw.print("<tr><td></td><td class=\"field\" style=\"width:20px\"><input type=\"checkbox\" name=\"ed_" + sMenuNumber + "_" + sEditwizardNumber + "\" ");
        NodeList nlEditwizardsUsers = cloud.getList(sEditwizardNumber,"editwizards,gebruikt,users","users.number","users.number='" + id + "'","","UP","",false);
        if (nlEditwizardsUsers.size()>0){
          pw.print("checked");
        }
        pw.print("></td><td class=\"fieldname\">");
        pw.print(sEditwizardName);
        pw.println("</td></tr>");
      }
    }
    pw.println("</table>");

  }

  public void setRelations(Cloud cloud, HttpServletRequest request){
    String nodeNumber = request.getParameter("nodeNumber");
    //Map menus = new HashMap();
    //Map editwizards = new HashMap();
    ArrayList alMenus = new ArrayList();
    ArrayList alEditwizards = new ArrayList();
    Enumeration enum = request.getParameterNames();
    while (enum.hasMoreElements()) {
      String name = (String) enum.nextElement();
      if (name.startsWith("menu_")) {
        String rol = request.getParameter(name);
        if (!rol.equals("-1")) {
               alMenus.add(name.substring(5));
        }
      } else if (name.startsWith("ed_")){
        String rol = request.getParameter(name);
        if (!rol.equals("-1")) {
          int iBeg = name.lastIndexOf("_");
               alEditwizards.add(name.substring(iBeg+1));
        }
      }
    }

    Node user = cloud.getNode(nodeNumber);

    RelationManager rmMenu = cloud.getRelationManager("menu","users","gebruikt");
    RelationList list = user.getRelations("gebruikt","menu");
    for (int i = 0; i < list.size(); i++) {
       list.getNode(i).delete();
    }

    RelationManager rmEdiwizard = cloud.getRelationManager("editwizards","users","gebruikt");
    list = user.getRelations("gebruikt","editwizards");
    for (int i = 0; i < list.size(); i++) {
       list.getNode(i).delete();
    }


    Iterator it = alMenus.iterator();
    while (it.hasNext()){
      String sMenuNumber = (String)it.next();
      NodeList nlEditwizards = cloud.getList(sMenuNumber,"menu,posrel,editwizards","editwizards.number,editwizards.name","","posrel.pos","UP","",false);
      for (int i=0; i<nlEditwizards.size();i++){
        String sEditwizardNumber = nlEditwizards.getNode(i).getStringValue("editwizards.number");
        Relation relation = rmEdiwizard.createRelation(cloud.getNode(sEditwizardNumber),user);
        relation.commit();
      }
      Relation relation = rmMenu.createRelation(cloud.getNode(sMenuNumber),user);
      relation.commit();
    }

    it = alEditwizards.iterator();
    while (it.hasNext()){
      String sEditwizardNumber = (String)it.next();
      Relation relation = rmEdiwizard.createRelation(cloud.getNode(sEditwizardNumber),user);
      relation.commit();
    }

  }

}
