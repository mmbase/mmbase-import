<%@ page import=" org.mmbase.bridge.*,
                  java.lang.*,
                  java.util.*,
                  java.io.*,
                  org.mmbase.util.Encode,
                  org.mmbase.util.transformers.*,
                  javax.servlet.jsp.JspWriter,
                  javax.servlet.ServletRequest,
                  javax.servlet.http.HttpServletRequest"%>

<%!

  static final String START_NODE="kbase.root";
  //static final String KBASE_USER="kbase";
  //static final String KBASE_PWD="kbase";
  static final String FIELDS_QUESTION="name,email,question,description,comment";
  static final String FIELDS_ANSWER="name,email,answer,comment";
  static final String FIELDS_CATEGORY="name,description";
  static final String ATTRIB_EXTRA_PARAMS="kb_extraParams";
  

  //*************************************
  // extra parameters kunnen voorkomen als de hele kbase wordt geinclude in een
  // of andere bestaande website. De volgende methodes helpen de templates hier
  // mee om te gaan
  
  
  //levert een string op die de extra parameters als url gedeelte of als hidde 
  //formfields bevat. de output is afhankelijk van de parmeters 'type' die 
  // de waarde 'form' of 'url' mag hebben;
  static String getParamsFormatted(HttpServletRequest request, String type, Map paramMap){
    if(paramMap==null)return "";
    StringBuffer output=new StringBuffer();
    String param;
    String[] values;
    boolean first=true;
    for(Iterator i=paramMap.keySet().iterator();i.hasNext();){
      if(!first && type.equals("url"))output.append("&");    
      first=false;
      param=(String) i.next();
      values=(String[])paramMap.get(param);
      //de parameter kan een array zijn. voor iedere waarde moet er een
      //'instantie' van de parameter worden toegevoegd
      for(int j=0 ; j<values.length ; j++){
        if(type.equals("url")){
          output.append(param+"="+values[j]);
          if(j < (values.length-1)) output.append("&");
        }else if(type.equals("form")){
          output.append("<input type=\"hidden\" name=\""+param+"\" value=\""+values[j]+"\" />");
          output.append("\n");
        }
      }
    }
    return output.toString();
  }
  
  //deze methode moet worden gebruikt in index.jsp (de wrapper) om de extra 
  //parameters te registreren.
  public static void setExtraParams(HttpServletRequest request, Set params){
    //request.getPageContext().setAttribute(ATTRIB_EXTRA_PARAMS, params, PageContext.REQUEST_SCOPE);
    request.setAttribute(ATTRIB_EXTRA_PARAMS, params);
  }
  
  //deze methode haalt alle params die niet van de kbase zelf zijn uit de 
  //request. dit zijn de parameters die apart moeten worden doorgegeven.
  //retourneerd null als er geen extra parameters zijn gedeclareerd of
  //als de gedeclareerde parameters niet in de request voorkomen 
  //TODO: om het echt goed te doen zou ik de kbase parameters een eigen prefix moeten
  //geven.
  public static Map getExtraParams(HttpServletRequest request){
    //Set s=(Set)request.getPageContext().getAttribute(ATTRIB_EXTRA_PARAMS,PageContext.REQUEST_SCOPE);
    Set s=(Set)request.getAttribute(ATTRIB_EXTRA_PARAMS);
    if(s==null || s.isEmpty()) return null;
    Iterator i=s.iterator();
    Map extraParams=extraParams=new HashMap();

    // eerst een string maken van alle extra parameters.
    StringBuffer sb=new StringBuffer(".");
    //in het geval dat er niet op zijn mist een van de gedeclareerde parmeters
    //werkelijk voorkomt moet de methode ook null teruggeven.
    boolean atLeastOneFound=false;
    String param;
    while (i.hasNext()){
      param=(String)i.next();
      if(request.getParameter(param)!=null)atLeastOneFound=true;
      sb.append(param+".");
    }
    if(!atLeastOneFound)return null;      //er komen dus geen extraparams in request voor
    String extraParamNames=sb.toString();
    
    Map params=request.getParameterMap();
    for(i=params.keySet().iterator();i.hasNext();){
      param=(String)i.next();
      if(extraParamNames.indexOf("."+param+".")>-1){    //dit is geen kbase parameter
        extraParams.put(param, params.get(param));
      }
    }
    return extraParams;
  }
  
  
  
  /*
  *Deze methode geeft het volledige pad terug naar de locatie van de kabase op de server. Om links naar plaatjesd
  *en scripts aan te vullen
  */
  static String getRealPath(HttpServletRequest request){
    String realpath=request.getServletPath();
    realpath=request.getContextPath()+realpath.substring(0,realpath.lastIndexOf("/"));
    return realpath;
  }
  
  
  
  /*
  *Deze methode update alle velden van een node, zoals bepaald in de FIELDS_[TYPE] variabelen
  */
  static void updateNode(Node n, ServletRequest request){
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
  
  static String getFieldList(String type){
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
  static String getEditNode(ServletRequest request){
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
  static String getParent(String node, Cloud wolk){
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
  static String getPath(String node, Cloud wolk){
    String path=node;
    while(!(node=getParent(node,wolk)).equals("-1")){
      path=node+","+path;
    }
    return path;
  }
  
  //deze methode voegt twee paden samen, om een nieuwe expanded waarde te krijgen.
  //wanneer een vraag wordt verplaatst wordt de newParent de nieuwe 'node'. Als 
  //een  categorie wordt verplaatst blijft deze de 'node', en in bijde gevallen
  //wordt het 'nieuwe' pad naar de nieuwe node toegevoegd aan de huidige geopende
  //folders.
  static String mergePaths(String path1, String path2){
    Set s=new HashSet();
    StringTokenizer st;
    //eerst alle eventuele extra komma,s van de paden knippen
    path1=clipPath(path1);
    path2=clipPath(path2);
    //dan alle nodes van bijde paden in een set (dupicaten vervallen)
    st=new StringTokenizer(path1,",");
    while(st.hasMoreTokens()){
      s.add(st.nextToken());
    }
    st=new StringTokenizer(path2,",");
    while(st.hasMoreTokens()){
      s.add(st.nextToken());
    }
    //en nu alle overbleven nodes in een nieuw pad wegschrijven
    StringBuffer newPath=new StringBuffer();
    for(Iterator i=s.iterator();i.hasNext();){
      newPath.append(",");
      newPath.append((String)i.next());
    }
    newPath.append(",");
    return newPath.toString();
  }
  
  static String clipPath(String path){
    while(path.startsWith(",")){
      path=path.substring(1);
    }
    while(path.endsWith(",")){
      path=path.substring(0,path.length()-1);
    }
  return path;
  }
  
  /*
  *deze methode geeft String 'true' als de er iemand is ingelogd (met edit rechten)
  *anders String 'false'
  */
  static String isEditor(Cloud wolk){
    String result="true";
    if(wolk.getUser().getIdentifier().equals("anonymous"))result="false";
    return result;
  }

  static boolean isNodeChildOf(String possibleChildNode, String node, Cloud wolk){
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
  static HashMap getPossibleParents(String node, Cloud wolk){
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
  
  //deze methode wordt alleen aangeroepen door getPossibleParents
  private static HashMap getPossibleQuestionParents(String node, Cloud wolk){
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
  
  //deze methode wordt alleen aangeroepen door getPossibleParents
  private static HashMap getPossibleCategoryParents(String node, Cloud wolk){
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
  
  
  //returns true if the node is part of the path
  /*
  static boolean pathContains(String node, String path){
    boolean test=false;
    if (!path.equals("")){
      StringTokenizer st=getPathTokenized(path);
      while(st.hasMoreTokens()){
        if (node.equals(st.nextToken()))test=true;
      }
    }
    return test;
  }
  */
  
  // returns the first node of the path
  /*
  static String getFirstNode(String path){
		StringTokenizer st=getPathTokenized(path);
		return (String)st.nextElement();
	}
  */
 
	/*
	static String getLastNode(String path){
		StringTokenizer st=getPathTokenized(path);
		String node="";
		while (st.hasMoreElements()){
			node=(String)st.nextElement();
		}
		return node;
	}
  */
	
  /*
	static String getNextNode(String path, String node){
		StringTokenizer st=getPathTokenized(path);
		if(node.equals(getLastNode(path))){
			return node;
		} 																
		while (st.hasMoreElements()){
			if(node.equals( (String)st.nextElement())){
				return (String)st.nextElement();
			}
		}
		return "0"; // geen nextnode gevonden
	}
  */
	
	StringTokenizer getPathTokenized(String path){
		StringTokenizer st=new StringTokenizer(path,",");
		return st;
	}
  
  	
  static String replaceNewLn(String text){
		Character c=new Character('\n');
		StringTokenizer st=new StringTokenizer(text,c.toString());
		String terug="";
		while(st.hasMoreElements()){
			terug=terug+st.nextElement()+"<br/>";
		}
		return terug;
	}
	
	static String formatCodeBody(String so)throws IOException{
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
