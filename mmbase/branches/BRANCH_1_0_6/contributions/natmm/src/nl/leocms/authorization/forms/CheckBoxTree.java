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
      pw.print("/></td><td colspan=\"3\" class=\"fieldname\">");
      pw.print(me.getValue());
      pw.println("</td></tr>");
      NodeList nlEditwizards = cloud.getList(sMenuNumber,"menu,posrel,editwizards","editwizards.number,editwizards.name","","posrel.pos","UP","",false);
      for (int i=0; i<nlEditwizards.size();i++){
        String sEditwizardNumber = nlEditwizards.getNode(i).getStringValue("editwizards.number");
        pw.print("<tr><td></td><td class=\"field\" style=\"width:20px\"><input type=\"checkbox\" name=\"ed_" + sMenuNumber + "_" + sEditwizardNumber + "\" ");
        NodeList nlEditwizardsUsers = cloud.getList(sEditwizardNumber,"editwizards,gebruikt,users","users.number","users.number='" + id + "'","","UP","",false);
        if (nlEditwizardsUsers.size()>0){
          pw.print("checked");
        }
        pw.print("/></td><td class=\"fieldname\">");
        pw.print(nlEditwizards.getNode(i).getStringValue("editwizards.name"));
        pw.println("</td><td class=\"fieldname\">");
        pw.print(nlEditwizards.getNode(i).getStringValue("editwizards.description"));
        pw.println("</td></tr>");
      }
    }
    pw.println("</table>");

  }

  public void setRelations(Cloud cloud, Node user, HttpServletRequest request){

    Set alMenus = new HashSet();
    Set alEditwizards = new HashSet();
    Enumeration pNames = request.getParameterNames();
    while (pNames.hasMoreElements()) {
      String name = (String) pNames.nextElement();
      if (name.startsWith("menu_")) {
        String rol = request.getParameter(name);
        if (!rol.equals("-1")) {
          alMenus.add(name.substring(5));
        }
      } else if (name.startsWith("ed_")) {
        String rol = request.getParameter(name);
        if (!rol.equals("-1")) {
          int iBeg = name.lastIndexOf("_");
          alEditwizards.add(name.substring(iBeg+1));
        }
      }
    }

    RelationManager rmMenu = cloud.getRelationManager("menu","users","gebruikt");
    RelationList list = user.getRelations("gebruikt","menu");
    for (int i = 0; i < list.size(); i++) {
       list.getNode(i).delete(true);
    }

    RelationManager rmEditwizard = cloud.getRelationManager("editwizards","users","gebruikt");
    list = user.getRelations("gebruikt","editwizards");
    for (int i = 0; i < list.size(); i++) {
       list.getNode(i).delete(true);
    }

    Iterator it = alMenus.iterator();
    while (it.hasNext()){
      String sMenuNumber = (String) it.next();
      NodeList nlEditwizards = cloud.getList(sMenuNumber,"menu,posrel,editwizards","editwizards.number,editwizards.name","","posrel.pos","UP","",false);
      // add all editwizards related to the selected menus
      for (int i=0; i<nlEditwizards.size();i++){
        String sEditwizardNumber = nlEditwizards.getNode(i).getStringValue("editwizards.number");
        alEditwizards.add(sEditwizardNumber);
      }
      rmMenu.createRelation(cloud.getNode(sMenuNumber),user).commit();
    }

    it = alEditwizards.iterator();
    while (it.hasNext()){
      String sEditwizardNumber = (String)it.next();
      rmEditwizard.createRelation(cloud.getNode(sEditwizardNumber),user).commit();
    }

  }

}
