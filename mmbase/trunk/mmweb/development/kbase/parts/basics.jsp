<%@ page import="org.mmbase.bridge.*,java.lang.*,java.util.*,java.io.*,org.mmbase.util.Encode,javax.servlet.jsp.JspWriter,javax.servlet.ServletRequest"%><%--
    Dit is een verzameling basis bewerkingen op het pad en de nodes
--%><%!

// Deze functie geeft de parent van een bepaalde node
  
  static final String START_NODE="kbase.root";
  //static final String KBASE_USER="kbase";
  //static final String KBASE_PWD="kbase";
  static final String FIELDS_QUESTION="name,email,question,description,comment";
  static final String FIELDS_ANSWER="name,email,answer,comment";
  static final String FIELDS_CATEGORY="name,description";
  
  /*
  *Deze methode update alle velden van een node, zoals bepaald in de FIELDS_[TYPE] variabelen
  */
  void updateNode(Node n, ServletRequest request){
    String type=request.getParameter("type");
    StringTokenizer st=new StringTokenizer(getFieldList(type),",");
    String field;
    String param;
    while(st.hasMoreTokens()){
      field=st.nextToken();
      param=request.getParameter(field);
      //TODO: this is a hack to cirumvent an apperant taglib bug in mmbase 1.7
      if(param==null)param=request.getParameter("_"+field);
      n.setStringValue(field,param);
    }
  }
  
  String getFieldList(String type){
    if("question".equals(type))return(FIELDS_QUESTION);
    if("answer".equals(type))return(FIELDS_ANSWER);
    if("category".equals(type))return(FIELDS_CATEGORY);
    return "";
  }
  
 /*
 *Deze methode wordt door edtipart gebruikt en moet bepalen 
 *welke node er geeidit moet worden.
 *dit kan node, qnode of anode zijn
 *op basis van de waarde van de parameter 'type' wordt de juiste node teruggegeven
 *de voornaamste reden om hier een methode van te maken is omdat het
 *zowel vor de form als de afhandeling moet worden bekeken
 *@param request is de request naar de pagina met alle parameters erin
 *@return een string met het nodenummer
 */
  String getEditNode(ServletRequest request){
    String editNode="-1";
      String type=request.getParameter("type");
      //test for category
      if("category".equals(type))
        editNode=request.getParameter("node");
      //test for question
      if("question".equals(type))
        editNode=request.getParameter("qnode");
      //test for answer
      if("answer".equals(type))
        editNode=request.getParameter("anode");        
    return editNode;
  }
 
  /*
  * De parentnode kan of een category of een vraag zijn.
  * node=category >> parent=category
  * node=question >> parent=category
  * node=answer   >> parent=question
  */
  String getParent(String node, Cloud wolk){
    String parent="-1";
    Node n;
    NodeList nl;  
    try{
      n=wolk.getNode(node);
      if(n.getNodeManager().getName().equals("kb_answer")){
        //parent must be of type question
        nl=n.getRelatedNodes("kb_question","related","source");
      }else{
        //parent must be of type category
        nl=n.getRelatedNodes("kb_category","related","source");
      }
      parent=nl.getNode(0).getStringValue("number");
    }catch(Exception e){
      return "-1";
    }
    return parent;
  }

  //deze methode berekent het pad van de active node naar de kbase.root
  String getPath(String node, Cloud wolk){
    String path=node;
    while(!(node=getParent(node,wolk)).equals("-1")){
      path=node+","+path;
    }
    return path;
  }
  
  /*
  *deze methode geeft String 'true' als de er iemand is ingelogd (met edit rechten)
  *anders String 'false'
  */
  String isEditor(Cloud wolk){
    String result="true";
    if(wolk.getUser().getIdentifier().equals("anonymous"))result="false";
    return result;
  }

  boolean isNodeChildOf(String possibleChildNode, String node, Cloud wolk){
    boolean isChild=false;
    Node n=wolk.getNode(node);
    Node n1;
    //eerst iteratie van alle childnodes
    NodeIterator ni=n.getRelatedNodes("kb_category","related","destination").nodeIterator();
    while(ni.hasNext()){
      n1=ni.nextNode();
      if(possibleChildNode.equals(n1.getStringValue("number"))){
        //deze child is gelijk aan mogelijke child (die gecheckt wordt
        isChild=true;
      } else {
        //ongelijk, dus controlleer of er nog meer childnodes zijn
        isChild=isNodeChildOf(possibleChildNode, n1.getStringValue("number"), wolk);
      }
    }
    return isChild;
  }
  

  //als het type node question is, kan iedere category worden teruggegeven
  //als het type categorie is moeten alle children worden uitgesloten
  //er wordt een arrayList teruggegeven met als key het nummer en als value de naam van de category
  HashMap getPossibleParents(String node, Cloud wolk){
    Node n=wolk.getNode(node);
    String nodeManager=n.getNodeManager().getName();
    if (nodeManager.equals("kb_question")){
      return getPossibleQuestionParents(node, wolk);
    }else if(nodeManager.equals("kb_category")){
      return getPossibleCategoryParents(node, wolk);
    }else{
      return null;
    }
  }
  
  HashMap getPossibleQuestionParents(String node, Cloud wolk){
    //een question kan aan iedere category worden gehangen, inc de root
    HashMap result=new HashMap();
    NodeIterator ni =wolk.getNodeManager("kb_category").getList(null,null,null).nodeIterator();
    Node n;
    while(ni.hasNext()){
      n=ni.nextNode();
      result.put(n.getStringValue("number"),n.getStringValue("name"));
    }
    return result;
  }
  
  HashMap getPossibleCategoryParents(String node, Cloud wolk){
    HashMap result=new HashMap();
    NodeIterator ni =wolk.getNodeManager("kb_category").getList(null,null,null).nodeIterator();
    Node n;
    String nodeNr;
    while(ni.hasNext()){
      //mag geen child zijn
      n=ni.nextNode();
      String possibleChild=n.getStringValue("number");
      if(!isNodeChildOf(possibleChild,node, wolk) && !possibleChild.equals(node)){
        result.put(n.getStringValue("number"),n.getStringValue("name"));
      }
    }
    return result;
  }
  
  
  boolean pathContains(String node, String path){
    boolean test=false;
    if (!path.equals("")){
      StringTokenizer st=getPathTokenized(path);
      while(st.hasMoreTokens()){
        if (node.equals(st.nextToken()))test=true;
      }
    }
    return test;
  }
  
  	String getFirstNode(String path){
		StringTokenizer st=getPathTokenized(path);
		return (String)st.nextElement();
	}
	
	String getLastNode(String path){
		StringTokenizer st=getPathTokenized(path);
		//String node=getFirstNode(path);
		String node="";
		while (st.hasMoreElements()){
			node=(String)st.nextElement();
		}
		return node;
	}
	
	String getNextNode(String path, String node){
		StringTokenizer st=getPathTokenized(path);
		if(node.equals(getLastNode(path))){
			return node;
		} 																
// werkt niet als node al de laatste is
		while (st.hasMoreElements()){
			if(node.equals( (String)st.nextElement())){
				return (String)st.nextElement();
			}
		}
		return "0"; // geen nextnode gevonden
	}
	
	StringTokenizer getPathTokenized(String path){
		StringTokenizer st=new StringTokenizer(path,",");
		return st;
	}
  
  	
		String replaceNewLn(String text){
		Character c=new Character('\n');
		StringTokenizer st=new StringTokenizer(text,c.toString());
		String terug="";
		while(st.hasMoreElements()){
			terug=terug+st.nextElement()+"<br/>";
		}
		return terug;
	}
	
	String formatCodeBody(String so)throws IOException{
		Encode encoder=new Encode("ESCAPE_HTML");
		String s1;
		String sn="";
		so=so.trim();
		int i;
		while(so!=""){
		i=so.toLowerCase().indexOf("<code>");
			if (i>-1){
				s1=so.substring(0,i);
				sn=sn+encoder.encode(s1)+"<PRE>";
				so=so.substring(i+6);
				i=so.toLowerCase().indexOf("</code>");
				if(i>-1){
					s1=so.substring(0,i);
					sn=sn+encoder.encode(s1)+"</PRE>";
					so=so.substring(i+7);
				} else{
					sn=sn+encoder.encode(so)+"</PRE>";
					so="";
				}
			} else {
			sn=sn+encoder.encode(so);
			so="";
			}
		// newln conversie
		}
		return replaceNewLn(sn);
	}

%>
