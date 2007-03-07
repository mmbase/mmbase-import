
<%@page import="java.util.*" %>
<%@page import="java.net.*" %>
<%@page import="org.mmbase.bridge.*" %>
<%!
    /**
     * any questions -> keesj@dds.nl
     **/
    /**
     * encode a string to be used in an url
     * this method lowercases the text
     * replaces spaces quotes an double quotes and amp sings to underscores
     * after convertion is finished the java.net.URLEncoder is called
     * to encode the remaining miscoded chars this is a onw way operation in order to keep the url pretty
     * @param text
     * @return the encoded url
     */
    public static String toURL(String text) {

        char[] chars = text.toLowerCase().toCharArray();

        StringBuffer sb = new StringBuffer();

        for (int x = 0; x < chars.length; x++) {
            char current = chars[x];
            switch (current) {
                case ' ' :
                case '"' :
                case '&' :
                    sb.append('_');
                case '\'' :
                    break;
                default :
                    sb.append(current);
            }
        }
        return URLEncoder.encode(sb.toString());
    }
%>
<%!
   public class Nav{
	public Vector childs = new Vector();
	String name;
	String baseURL;
	int id;
	public boolean isPortal = false;

	Nav parent  =null;
	public Nav(String name,int id){
		this.name = name;
		this.id = id;
	}

	public Nav(String name,int id,String baseURL){
		this.name = name;
		this.id = id;
		this.baseURL = baseURL;
	}

	public Nav addChild(String name,int id){
		return addChild(new Nav(name,id));	
	}

	public Nav addChild(Nav nav){
		nav.setParent(this);
		nav.baseURL = baseURL;
		childs.add(nav);
		return nav;
	}

	public boolean hasParent(){
		return parent != null;
	}

	public Nav getParent(){
	       return parent;
	}

	public void setParent(Nav parent){
	        this.parent = parent;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		toString(0,sb);
		return sb.toString();
	}

	public String toDiv(){
		StringBuffer sb = new StringBuffer();
		toDiv("menu",0,0,sb);
		return sb.toString();
	}

	public void toDiv(String menuid,int deep,int count ,StringBuffer sb){
                if (deep == 0 && count ==0){
		   sb.append("\n<div class='navilayer' style='visibility: visible; left: 0px; z-index: 10' id='"+ menuid +"'>");
		} else {
		   sb.append("\n<div class='navilayer' style='left: "+ (deep * 140 )+"px' id='"+ menuid +"'>");
		}
		for (int x =0 ; x < count; x++){
			//sb.append("\n  <div class='fill'><a>&nbsp;</a></div>");
			sb.append("\n  <div class=\"fill\">&nbsp;</div>");
		}
		for (int y =0 ; y < childs.size() ; y++){
			Nav nav = (Nav)childs.get(y);
			sb.append("\n  <div>");
                        if (nav.childs.size() >0){
			   sb.append("<a href=\""+ baseURL +"&amp;page="+ nav.id +"\" onmouseover=\"show('"+menuid+"_"+ y+"',"+deep+",this)\">");
			} else {
			   sb.append("<a href=\""+ baseURL +"&amp;page="+ nav.id +"\" onmouseover=\"show('',"+deep+",this)\">");
			}
			sb.append(nav.name);
			sb.append("</a>");
			sb.append("</div>");
		}

		sb.append("\n</div>\n");
		//sb.append("[" + id +","+ name+"]\n");

		for (int y =0 ; y < childs.size() ; y++){
			Nav nav = (Nav)childs.get(y);
			//if has childs..
			if (nav.childs.size() >0){
			   nav.toDiv(menuid+"_"+ y,deep +1 , count,sb);
			}
			count ++;
		}
	}

	public void toString(int indent,StringBuffer sb){
		for (int x =0 ; x < indent ; x ++){
			sb.append(" ");
		}
		sb.append("[" + id +","+ name+"]\n");
		for (int y =0 ; y < childs.size() ; y++){
			Nav nav = (Nav)childs.get(y);
			nav.toString(indent +1 ,sb);
		}
	}
   }
%>
<%!

        public Nav addChildPagesOfPortal(Nav root,NodeList portalPages,NodeList subpages,String currentPage){
                Nav curentPageNav= null;
                if (currentPage.equals("" + root.id)){ curentPageNav = root; }
                int id = root.id;
                for (int x = 0 ; x < portalPages.size() ; x ++){
                        Node pNode = portalPages.getNode(x);
                        if (pNode.getIntValue("portals.number") == id){
                                Node page = pNode.getNodeValue("pages.number");
                                Nav childNav = root.addChild(page.getStringValue("title"),page.getNumber());
                                Nav tmp = addChildPagesOfPages(childNav,subpages,currentPage);
				if (tmp != null ){ curentPageNav = tmp; }
                        }
                }
                return curentPageNav;
        }

        public Nav addChildPagesOfPages(Nav root,NodeList subpages,String currentPage){
                Nav curentPageNav= null;
                int id = root.id;
                if (currentPage.equals("" + root.id)){ curentPageNav = root; }
                for (int x = 0 ; x < subpages.size() ; x ++){
                        Node pNode = subpages.getNode(x);
                        if (pNode.getIntValue("pages0.number") == id){
                                Node page = pNode.getNodeValue("pages1.number");
                                Nav childNav = root.addChild(page.getStringValue("title"),page.getNumber());
                                Nav tmp = addChildPagesOfPages(childNav,subpages,currentPage);
				if (tmp != null ){ curentPageNav = tmp; }
                        }
                }
                return curentPageNav;
        }
%>
<%
       Nav nav = null;
       Nav currentNav = null;
%>
<mm:cloud jspvar="cloud">
<%
       Node rootNode = cloud.getNode("home");
       NodeList portals = cloud.getNodeManager("portals").getList(null,null,null);
       NodeList subportal = rootNode.getRelatedNodes(rootNode.getNodeManager());

        NodeList portalPages = cloud.getList(
                        "" ,//startNodes
                        "portals,posrel,pages",//nodePath
                        "portals.number,posrel.pos,pages.number",//fields
                        "",//constraints
                        "posrel.pos",//orderby
                        "UP",//directions
                        "destination",//searchDir
                        false);

        NodeList subpages = cloud.getList(
                        "",//startNodes
                        "pages0,posrel,pages1",//nodePath
                        "pages0.number,posrel.pos,pages1.number",//fields
                        "",//constraints
                        "posrel.pos",//orderby
                        "UP",//directions
                        "destination",//searchDir
                        false);


%>
<%
       String pnumber = request.getParameter("portal");
       String currentPageString = request.getParameter("page");
       if (currentPageString == null){
		currentPageString ="";
	}
       if (pnumber == null){
		pnumber = "202";
	}
       Node portal = cloud.getNode(pnumber);
       nav = new Nav(portal.getStringValue("name"),portal.getNumber(),request.getContextPath() + "/?portal=" + portal.getNumber());
       nav.isPortal = true;
       currentNav = addChildPagesOfPortal(nav,portalPages,subpages,currentPageString);
       if (currentNav == null){
            %><!-- current Nav was null... --><%
            currentNav = nav.childs.size() > 0 ? (Nav)nav.childs.get(0) : null;
       }
       //for (int x =0 ; x < subportal.size() ; x ++){
//                Node portalNode = subportal.getNode(x);
//                Nav realportal = nav.addChild(portalNode.getStringValue("name"),portalNode.getNumber());
//                realportal.isPortal = true;
//                addChildPagesOfPortal(realportal,portalPages,subpages,currentPageString);
//        }

%>
<%= nav.childs.size() > 0 ? ((Nav)nav.childs.get(0)).toDiv() : "NO NAV" %>
</mm:cloud>
