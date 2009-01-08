package nl.mmatch;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.*;
import nl.leocms.util.tools.*;

/**
 * @author Henk Hangyi (MMatch)
 */

public class CSVReader implements Runnable {

    int importType;

    private static final Logger log = Logging.getLoggerInstance(CSVReader.class);
    private static final ServerUtil su = new ServerUtil();
    
    private static final String IGNORE_BEAUFORT = "inactive (en negeer Beaufort)";
    
    public static int FULL_IMPORT = 1;
    public static int ONLY_MEMBERLOAD = 2;
    public static int ONLY_ZIPCODELOAD = 3;
    
    private BufferedReader getBufferedReader(String sFileName) throws FileNotFoundException, UnsupportedEncodingException {
      FileInputStream fin = new FileInputStream(sFileName);
      InputStreamReader isr = new InputStreamReader(fin,"ISO-8859-1");
      return new BufferedReader(isr);
    }
    
    private String getValue(String entry, String startStr, String endStr)
    {   String value = "";
        int sPos = entry.indexOf(startStr);
        if(sPos>-1){
            sPos += startStr.length();
            int ePos = entry.indexOf(endStr,sPos);
            if(ePos>-1) {
                value = entry.substring(sPos,ePos);
            }
        }
        return value;
    }
    
    private Date lastModifiedDate(String dataFile) {
      return new Date( (new File(dataFile)).lastModified() );
    }
    
    private String getShowInfo(String value) {
        String showInfo = "0";
        if(value.equals("0")) showInfo = "1";
        return showInfo;
    }
    
    private String getReadmoreJobs(String value) {
        String readmoreJobs = "0";
        if(value.equals("1")) readmoreJobs = "1";
        return readmoreJobs;
    }
    
    private Integer getDate(String thisDate, int defaultYear) {
        int thisDay = 1;
        int thisMonth = 0;
        int thisYear = defaultYear;
        int pPos = thisDate.indexOf("+");
        if(pPos>-1){
            String monthStr = thisDate.substring(0,pPos);
            if(monthStr.equals("January")) thisMonth = 0;
            if(monthStr.equals("February")) thisMonth = 1;
            if(monthStr.equals("March")) thisMonth = 2;
            if(monthStr.equals("April")) thisMonth = 3;
            if(monthStr.equals("Mai")) thisMonth = 4;
            if(monthStr.equals("June")) thisMonth = 5;
            if(monthStr.equals("July")) thisMonth = 6;
            if(monthStr.equals("August")) thisMonth = 7;
            if(monthStr.equals("September")) thisMonth = 8;
            if(monthStr.equals("October")) thisMonth = 9;
            if(monthStr.equals("November")) thisMonth = 10;
            if(monthStr.equals("December")) thisMonth = 11;
            thisDate = thisDate.substring(pPos+1);
        }
        try {
            thisYear = (new Integer(thisDate)).intValue();
        } catch (Exception e) { }
        return new Integer(thisYear);
    }
    
    private Date parseDate(String thisDate, int defaultYear) {
        int thisDay = 1;
        int thisMonth = 0;
        int thisYear = defaultYear;
        if(thisDate.indexOf("-")>-1){
            int sPos = thisDate.indexOf("-");
            thisYear = (new Integer(thisDate.substring(0,sPos))).intValue();
            thisDate = thisDate.substring(sPos+1);
            sPos = thisDate.indexOf("-");
            thisMonth = (new Integer(thisDate.substring(0,sPos))).intValue()-1;
            thisDate = thisDate.substring(sPos+1);
            sPos = thisDate.indexOf(" ");
            thisDay = (new Integer(thisDate.substring(0,sPos))).intValue();
        }
        Calendar cal = Calendar.getInstance();
        cal.set(thisYear,thisMonth,thisDay);
        return cal.getTime();
    }
    
    private String getGender(String value) {
        String gender = "0";
        if(value.toUpperCase().equals("M")) gender = "1";
        return gender;
    }
    
    private String createAlias(TreeMap thisPerson, String lastname, String firstname) {
        // create field alias, take care of ' which would ruin the search
        String alias = (String) thisPerson.get(lastname);
        alias = alias.replace('\'',' ').replace('\\',' ').trim();
        int sPos = alias.indexOf(" ");
        if(sPos>-1) { // people with double family names
            alias = alias.substring(0,sPos);
        }
        String firstName = (String) thisPerson.get(firstname);
        if(firstName!=null&&firstName.length()>0) {
            firstName = firstName.replace('\'',' ').replace('\\',' ').trim();
            alias += firstName.substring(0,1);
        }
        return alias;
    }
    
    private Node getNode(Cloud cloud, String path, String constraint) {
          NodeList list = cloud.getNodeManager(path).getList(constraint, null, null);
          Node node = null;
          if(!list.isEmpty()){ node = list.getNode(0); }
          return node;
    }
    
    private int markNodesAndRelations(Cloud cloud, String thisType, String [] thisRelations, String [] thisFields) {
        // mark all relations for employees and departments
        log.info("markNodesAndRelations " + thisType);
        NodeManager relatedNodeNM = cloud.getNodeManager(thisType);
        NodeList thisnodesList = relatedNodeNM.getList("importstatus not like '%" + IGNORE_BEAUFORT + "%'",null,null);
        RelationList relations = null;
        Node thisnode = null;
        int i = 0;
        int nodesMarked = 0;
        while(i<thisnodesList.size()) {
            thisnode = thisnodesList.getNode(i);
            for(int t=0; t< thisRelations.length; t=t+2) {
                relations = thisnode.getRelations(thisRelations[t],thisRelations[t+1]);
                for(int r=0; r<relations.size(); r++) {
                    Relation relation = relations.getRelation(r);
                    if(!"inactive".equals(relation.getStringValue("readmore2"))) {
                      relation.setValue("readmore2","inactive");
                      relation.commit();
                      nodesMarked++;
                    }
                }
            }
            // set to value of [1] if not already but don't set if it is [2] - reserved for cases of ignoring csv overrides.
            if( !thisFields[1].equals(thisnode.getStringValue(thisFields[0])) && !thisFields[2].equals(thisnode.getStringValue(thisFields[0]))      ) {
              thisnode.setValue(thisFields[0],thisFields[1]);
              thisnode.commit();
              nodesMarked++;
            }
            i++;
        }
        return nodesMarked;
    }
    
    private int deleteNodesAndRelations(Cloud cloud, String thisType, String [] thisRelations, String [] thisFields) {
        // deletes all nodes relations for employees and departments, which are inactive
        log.info("deleteNodesAndRelations " + thisType);
        NodeManager relatedNodeNM = cloud.getNodeManager(thisType);
        NodeList thisnodesList = relatedNodeNM.getList("importstatus not like '%" + IGNORE_BEAUFORT + "%'",null,null);
        RelationList relations = null;
        Node thisnode = null;
        int i = 0;
        int nodesDeleted = 0;
        while(i<thisnodesList.size()) {
            thisnode = thisnodesList.getNode(i);
            log.info("trying to access node " + thisnode.getValue(thisFields[0]).toString());
            if((thisnode.getValue(thisFields[0]).toString()).equals(thisFields[1])) {
                thisnode.delete(true);
                nodesDeleted++;
            } else {
                for(int t=0; t< thisRelations.length; t=t+2) {
                    relations = thisnode.getRelations(thisRelations[t],thisRelations[t+1]);
                    for(int r=0; r<relations.size(); r++) {
                        Relation relation = relations.getRelation(r);
                        log.info("trying to access relation " + relation.getValue("readmore2"));
                        if(relation.getValue("readmore2").equals("inactive")) {
                            relation.delete(true);
                        }
                    }
                }
            }
            i++;
        }
        return nodesDeleted;
    }
    
    private int updateDepartments(Cloud cloud) {
        // for the search we need a list with descendants for all deparments
        log.info("updateDepartments");
        int numberOfEmptyDept = 0;
        TreeMap departments = new TreeMap();
        NodeManager departmentNM = cloud.getNodeManager("afdelingen");
        NodeList departmentList = departmentNM.getList(null,null,null);
        for(int d=0; d<departmentList.size(); d++) {
            Node departmentNode = departmentList.getNode(d);
            String departments_number = "" + departmentNode.getNumber();
            String lastDept = departments_number;
            Vector descendants = new Vector();
            descendants.add(lastDept);
            while(!lastDept.equals("")) {
                String currentDept = lastDept;
                lastDept = "";
                NodeList relatedDeptList = cloud.getNode(currentDept).getRelations("readmore",departmentNM,"source");
                for(int r=0; r<relatedDeptList.size(); r++) {
                    Node relatedNode = relatedDeptList.getNode(r);
                    String departments2_number = "" + relatedNode.getNumber();
                    if(!descendants.contains(departments2_number)) {
                       lastDept = departments2_number;
                       descendants.add(lastDept);
                    }
                }
            }
            departments.put(departments_number,descendants);
        }
        // traverse the tree bottom-up to findout which department contains employees
        Vector deptWithEmployees = new Vector();
        for(int d=0; d<departmentList.size(); d++) {
            Node departmentNode = departmentList.getNode(d);
            String departments_number = "" + departmentNode.getNumber();
            NodeList relatedEmplList = departmentNode.getRelations("readmore",cloud.getNodeManager("medewerkers"));
            if(relatedEmplList.size()>0) {
                if(!deptWithEmployees.contains(departments_number)) {
                    deptWithEmployees.add(departments_number);
                }
                String lastDept = departments_number;
                while(!lastDept.equals("")) {
                    String currentDept = lastDept;
                    lastDept = "";
                    NodeList relatedDeptList = cloud.getNode(currentDept).getRelations("readmore",departmentNM,"destination");
                    for(int r=0; r<relatedDeptList.size(); r++) {
                        Node relatedNode = relatedDeptList.getNode(r);
                        String departments2_number = "" + relatedNode.getNumber();
                        if(!deptWithEmployees.contains(departments2_number)) {
                            deptWithEmployees.add(departments2_number);
                        }
                    }
                }
            }
        }
        for(int d=0; d<departmentList.size(); d++) {
            Node departmentNode = departmentList.getNode(d);
            String departments_number = departmentNode.getStringValue("number");
            // update the descendants field
            String descendants = departments.get(departments_number).toString();
            departmentNode.setValue("descendants",descendants.substring(1,descendants.length()-1));
            // update the importstatus field
            if(!deptWithEmployees.contains(departments_number)) {
                departmentNode.setValue("importstatus","inactive");
                numberOfEmptyDept++;
            }
            departmentNode.commit();
        }
        return numberOfEmptyDept;
    }
    
    private Node relatedNodes(
        Cloud cloud,
        TreeMap tmThisNode,
        Node sourceNode,
        String relatedNode,
        String relatedNodeKeyField,
        String relatedRole,
        String relatedRoleField,
        String relatedRoleValue,
        boolean logPerson) {

        // items are merged on the value of relatedNode.relatedNodeKeyField (use unique keys if you do not want a merge)
        // use superSearchString to replace characters which will ruin the search by wildcards

        Node destination = null;
        String relatedNodeKeyValue = (String) tmThisNode.get(relatedNode);
        if(!relatedNodeKeyValue.equals("")) {
            // find the destination
            NodeManager relatedNodeNM = cloud.getNodeManager(relatedNode);
            NodeList constrainedList = relatedNodeNM.getList(relatedNodeKeyField
                        + " LIKE '" + (new SearchUtil()).superSearchString(relatedNodeKeyValue) + "'",null,null);
            if(constrainedList.size()>0) {
                destination = constrainedList.getNode(0);
            } else {
                destination = relatedNodeNM.createNode();
                destination.setValue(relatedNodeKeyField,relatedNodeKeyValue);
                destination.commit();
            }
            // find the relation
            RelationList relations = sourceNode.getRelations(relatedRole,relatedNode);
            Relation thisRelation = null;
            for(int r=0; r<relations.size() && thisRelation==null; r++) {
                Relation relation = relations.getRelation(r);
                if( ( relation.getSource().getNumber()==destination.getNumber()
                      || relation.getDestination().getNumber()==destination.getNumber() )
                    && ( relatedRoleField.equals("")
                      || relation.getStringValue(relatedRoleField).equals(tmThisNode.get(relatedRoleValue)) )
                   ) {
                    thisRelation = relation;
                }
            }
            if(thisRelation==null){ // create the relation
                thisRelation = sourceNode.createRelation(destination,cloud.getRelationManager(relatedRole));
                if(logPerson) {
                   log.info("added " + relatedRole + " relation to " + destination.getStringValue("titel")
                    + "for " + sourceNode.getStringValue("titel"));
                }
            } else {
                if(logPerson) {
                   log.info("found relation " + thisRelation.getNumber() + " to "
                    + destination.getStringValue("titel") + "for person " + sourceNode.getStringValue("titel"));
                }
            }
            if(!relatedRoleField.equals("")) {
                thisRelation.setValue(relatedRoleField, tmThisNode.get(relatedRoleValue));
            }
            thisRelation.setValue("readmore2", "active");
            thisRelation.commit();
        }
        return destination;
    }
    
    private Node updatePerson(Cloud cloud, TreeMap thisPerson, String thisPersonStr, boolean logPerson) {
        SearchUtil su = new SearchUtil();
        Node personsNode = getNode(cloud, "medewerkers", "externid='" + su.superSearchString((String) thisPerson.get("SOFI_NR")) + "'");
        if(personsNode==null) {
            String aliasFB = ((String) thisPerson.get("ALIAS")).toUpperCase();
            personsNode = getNode(cloud, "medewerkers", "externid='' AND UPPER(account) LIKE '" + su.superSearchString(aliasFB) + "'");
            if(personsNode==null) {
                int aliasLength = aliasFB.length();
                if(aliasLength>0) {
                    aliasFB = aliasFB.substring(0,aliasLength-1);
                    personsNode = getNode(cloud, "medewerkers", "externid='' AND UPPER(account) LIKE '" + su.superSearchString(aliasFB) + "_'");
                }
                if(personsNode==null) {
                    NodeManager persons = cloud.getNodeManager("medewerkers");
                    personsNode = persons.createNode();
                    if(logPerson) { log.info("create new medewerker for " + thisPerson.get("SOFI_NR")); }
                } else {
                    if(logPerson) { log.info("found medewerker node " + personsNode.getNumber() + " for " + thisPerson.get("SOFI_NR")); }
                }

            }
        }
        
        // if null means not in db - new medewerker. set to active to prevent NullPE. also being in beaufort means it is an active medewerker
        if ((personsNode.getValue("importstatus") == null) || (!personsNode.getValue("importstatus").equals(IGNORE_BEAUFORT))) {        
        
            personsNode.setValue("externid", thisPerson.get("SOFI_NR"));
            String alias = (String) personsNode.getValue("account");
            if(alias==null||alias.equals("")) { // no alias found, use the created one
               personsNode.setValue("account", thisPerson.get("ALIAS"));
            }
            personsNode.setValue("prefix", thisPerson.get("E_TITUL"));
            personsNode.setValue("firstname", thisPerson.get("E_ROEPNAAM"));
            personsNode.setValue("initials", thisPerson.get("E_VRLT"));
            if(thisPerson.get("GBRK_OMS").equals("Partnernaam-geboortenaam")) {
               personsNode.setValue("suffix", thisPerson.get("P_VRVG"));
               personsNode.setValue("lastname", thisPerson.get("P_NAAM") + " - " + thisPerson.get("E_VRVG") + " " + thisPerson.get("E_NAAM"));
            } else {
               personsNode.setValue("suffix", thisPerson.get("E_VRVG"));
               personsNode.setValue("lastname", thisPerson.get("E_NAAM"));
            }
            personsNode.setValue("gender",getGender((String) thisPerson.get("GENDER")));

            personsNode.setValue("importstatus","active");
            personsNode.commit();
        }
        return personsNode;
    }

    private String updateOrg(Cloud cloud, String orgFile) {
        log.info("updateOrg " + orgFile);
        String logMessage = "";
        TreeMap thisTree = new TreeMap();
        try {
            // read the organogram input file
            BufferedReader dataFileReader = getBufferedReader(orgFile);
            String nextLine = dataFileReader.readLine();
            if(nextLine.indexOf("DPIB015_SL")==-1) log.info("expecting DPIB015_SL ... on first line of " + orgFile);
            nextLine = dataFileReader.readLine();
            if(nextLine.indexOf("-----|")==-1) log.info("expecting -----| ... on second line of " + orgFile);

            Node thisNode = null;
            Node relatedNode = null;
            String [] labels = { "DPIB015_SL", "OE_HOGER_N", "OE_KORT", "OE_VOL_NM" };
            String thisType = "afdelingen";
            nextLine = dataFileReader.readLine();
            while(nextLine!=null&&!nextLine.trim().equals("")) {
                nextLine += "|";
                thisTree.clear();
                int v = 0;
                while(nextLine!=null&&v<labels.length) {
                    int cPos = nextLine.indexOf("|");
                    String value = "";
                    if(cPos>-1) {
                        value = nextLine.substring(0,cPos).trim();
                        nextLine = nextLine.substring(cPos+1);
                        cPos = nextLine.indexOf(",");
                    } else {
                        log.info("Line ends before last label for " + thisTree.get("OE_VOL_NM") + " in " + orgFile);
                    }
                    thisTree.put(labels[v],value);
                    v++;
                }
                // update this node
                thisNode = getNode(cloud, thisType, "externid='" + thisTree.get("DPIB015_SL") + "'");
                if(thisNode==null) {
                    thisNode = cloud.getNodeManager(thisType).createNode();
                    thisNode.setValue("externid",thisTree.get("DPIB015_SL"));
                }
                // only departments with employees or descendants with employees are active
                // the field importstatus is set in updateDepartments()
                thisNode.setValue("naam",thisTree.get("OE_VOL_NM"));
                thisNode.commit();
                thisTree.put(thisType,thisTree.get("OE_HOGER_N"));
                Node destination = relatedNodes(cloud, thisTree, thisNode, thisType, "externid", "readmore", "", "",false);
                nextLine = dataFileReader.readLine();
            }
            dataFileReader.close();
       } catch(Exception e) {
            log.info(e);
            log.info(thisTree);
            log.info(logMessage);
       }
       logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize() + " - Organisational structure is read from: " + orgFile;
       return logMessage;
    }

    private TreeMap getEmails(String emailFile){
      log.info("getEmails " + emailFile);
      TreeMap emails = new TreeMap();
      String separator = ";";
      
      try {
        BufferedReader dataFileReader = getBufferedReader(emailFile);
        String nextLine = dataFileReader.readLine();

        while(nextLine!=null) {
        	String alias = "";
        	String email = "";
        	String[] tokens = nextLine.split(separator);
        	if (tokens.length < 4) {
        		log.warn("email file row contains less then expected tokens.");
        	} else {
        		alias = tokens[0];
        		if (tokens[2].indexOf("@") != -1) {
        			email = tokens[2];
        		}
        	}
        	
          if(!alias.equals("")&&!email.equals("")) { // use uppercase on alias for searching
            if(email.length()>64) {
              log.warn("email address " + email + " for alias " + alias + " is longer than 255 characters, therefore it will be truncated.");
              email = email.substring(0,64);
            }
            emails.put(alias.toUpperCase(),email);
          }
          nextLine = dataFileReader.readLine();
        }
        dataFileReader.close();
      } catch(Exception e) {
        log.info(e);
      }
      return emails;
   }

   private String updatePersons(Cloud cloud, TreeMap emails, String dataFile) {
    log.info("updatePersons " + dataFile);
    String logMessage = "";
    String logId = ""; // can be used to log update info on one person
    try {
      
      // read the person input file
      BufferedReader dataFileReader = getBufferedReader(dataFile);
      String nextLine = dataFileReader.readLine();
      if(nextLine.indexOf("PERS_NR")==-1) log.info("expecting PERS_NR ... on first line of " + dataFile);
      nextLine = dataFileReader.readLine();
      if(nextLine.indexOf("-----|")==-1) log.info("expecting -----| ... on second line of " + dataFile);

      TreeMap thisPerson = new TreeMap();
      Node personsNode = null;
      String lastId = "";
      // GENDER has label G in the datafile, duplicate label G
      String [] labels = {
          "PERS_NR", "OBJECT_ID", "SOFI_NR", "E_NAAM", "E_VRVG", "P_NAAM", "P_VRVG", "G", "GBRK_OMS",
          "GBRK_EXT", "GENDER", "E_TITUL", "E_VRLT", "E_ROEPNAAM", "OE_HIER_SL", "PRIMFUNC_K", "FUNC_OMS",
          "FUNC_EXT", "KOSTEN", "K_S_WAARDE", "K_OF_S_OMSCHRIJVING" };
      int persons=0;
      int entries=0;
      int noemails=0;
      nextLine = dataFileReader.readLine();
      while(nextLine!=null&&!nextLine.trim().equals("")) {
          nextLine += "|";
          thisPerson.clear();
          int v = 0;
          while(nextLine!=null&&v<labels.length) {
              int cPos = nextLine.indexOf("|");
              String value = "";
              if(cPos>-1) {
                  value = nextLine.substring(0,cPos).trim();
                  nextLine = nextLine.substring(cPos+1);
              } else {
                  log.info("Line ends before last label for person " + thisPerson.get("PERS_NR") + " in " + dataFile);
              }
              thisPerson.put(labels[v],value);
              v++;
          }

          // if SOFI_NR is empty use PERS_NR
          if(thisPerson.get("SOFI_NR").equals("")) {
               thisPerson.put("SOFI_NR",thisPerson.get("PERS_NR"));
          }
          
          boolean logPerson = logId.equals(thisPerson.get("SOFI_NR"));
          
          thisPerson.put("ALIAS",createAlias(thisPerson,"E_NAAM","E_ROEPNAAM"));

          String thisPersonStr = (String) thisPerson.get("E_ROEPNAAM");
          if(!thisPerson.get("E_VRVG").equals("")) {
              thisPersonStr += " " + thisPerson.get("E_VRVG");
          }
          thisPersonStr += " " + thisPerson.get("E_NAAM") + " (" + thisPerson.get("SOFI_NR") + ")";

          // unique key for departments is naam
          // so we have to do some mapping here, to prevent duplicates because of variations in name
          String departmentStr = (String) thisPerson.get("K_OF_S_OMSCHRIJVING");
          departmentStr = departmentStr.replaceAll("BC ","Bezoekerscentrum ");
          thisPerson.put("K_OF_S_OMSCHRIJVING",departmentStr);

          // use the info to update the next person
          if(!lastId.equals(thisPerson.get("SOFI_NR"))) {
              personsNode = updatePerson(cloud,thisPerson,thisPersonStr,logPerson);

              // update email address (if allowed)
              String updateInfo = (String) personsNode.getValue("updateinfo");
              if(updateInfo==null) updateInfo = "1";
              if(!updateInfo.equals("0")) {
                  String alias = (String) personsNode.getValue("account");
                  String email = (String) emails.get(alias.toUpperCase());
                  boolean emailFound = false;
                  if(email!=null) {
                      personsNode.setValue("email", email);
                      personsNode.commit();
                      emailFound = true;
                  } else if(thisPerson.get("GBRK_OMS").equals("Partnernaam-geboortenaam")) { // try alternative alias
                      alias =  createAlias(thisPerson,"P_NAAM","E_ROEPNAAM");
                      email = (String) emails.get(alias.toUpperCase());
                      if(email!=null) {
                          personsNode.setValue("account", alias);
                          personsNode.setValue("email", email);
                          personsNode.commit();
                          emailFound = true;
                      }
                  }
                  if(!emailFound) {
                      logMessage += "\n<br>Could not find email address for: " + thisPersonStr + " (used " + alias + " as alias)";
                      noemails++;
                  }
              } else {
                  logMessage += "\n<br>Not allowed to update email address for: " + thisPersonStr;
              }
              persons++;
          }

          if(thisPerson.get("KOSTEN").equals("E00302")) { // afdeling
              thisPerson.put("afdelingen", thisPerson.get("K_OF_S_OMSCHRIJVING"));
              Node destination = relatedNodes(cloud, thisPerson, personsNode, "afdelingen", "naam", "readmore", "readmore", "FUNC_OMS", logPerson);
              // this department is in use, so set to active
              destination.setValue("importstatus", "active");
              destination.setValue("omschrijving", thisPerson.get("K_S_WAARDE")); 
              destination.commit();
          } else { // locatie, column KOSTEN equals E00375
              thisPerson.put("locations",thisPerson.get("K_S_WAARDE"));
              Node destination = relatedNodes(cloud, thisPerson, personsNode, "locations", "externid", "readmore", "readmore", "FUNC_OMS", logPerson);
              if(destination.getValue("name")==null||!destination.getValue("name").equals(thisPerson.get("K_OF_S_OMSCHRIJVING"))) {
                  destination.setValue("naam", thisPerson.get("K_OF_S_OMSCHRIJVING"));
                  destination.commit();
              }
          }
          lastId = (String) thisPerson.get("SOFI_NR");

          nextLine = dataFileReader.readLine();
          entries++;
      }
      dataFileReader.close();
      logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize() 
         + " - Number of NM employees loaded from: " + dataFile + " is " + persons + " (number of entries is " + entries + ")"
         + "\n<br>Number of email addresses parsed: " + emails.size()
         + "\n<br>Number of persons for which no email address could be found: " + noemails;
    } catch(Exception e) {
      log.info(e);
    }
    return logMessage;
   }

   private String updateNMV(Cloud cloud, String dataFile) {
    log.info("updateNMV " + dataFile);
    String logMessage = "";
    String logId = ""; // can be used to log update info on one person
    // read the person input file
    try {
      BufferedReader dataFileReader = getBufferedReader(dataFile);
      String nextLine = dataFileReader.readLine();
      if(nextLine.indexOf("Voornaam")==-1) log.info("expecting Voornaam ... on first line of " + dataFile);
      nextLine = dataFileReader.readLine();

      TreeMap thisPerson = new TreeMap();
      Node personsNode = null;
      String lastId = "";
      // GENDER has label G in the datafile, duplicate label G

      // nmvFile:
      //   "Voornaam", "Voorletters", "Voorvoegsel", "Achternaam", "Geslacht",
      //   "Telefoon", "TelefoonMobiel", "MailPrive", "Geboortedatum", "Beheereenheid"
      // dataFile:
      //   "PERS_NR", "OBJECT_ID", "SOFI_NR", "E_NAAM", "E_VRVG", "P_NAAM", "P_VRVG", "G", "GBRK_OMS",
      //   "GBRK_EXT", "GENDER", "E_TITUL", "E_VRLT", "E_ROEPNAAM", "OE_HIER_SL", "PRIMFUNC_K", "FUNC_OMS",
      //   "FUNC_EXT", "KOSTEN", "K_S_WAARDE", "K_OF_S_OMSCHRIJVING"
      String [] labels = {
         "E_ROEPNAAM", "E_VRLT", "E_VRVG", "E_NAAM", "GENDER",
         "Telefoon", "TelefoonMobiel", "MailPrive", "Geboortedatum", "K_S_WAARDE",
         "PERS_NR", "OBJECT_ID", "SOFI_NR", "P_NAAM", "P_VRVG", "G", "GBRK_OMS",
         "GBRK_EXT", "E_TITUL", "OE_HIER_SL", "PRIMFUNC_K", "FUNC_OMS",
         "FUNC_EXT", "KOSTEN", "K_OF_S_OMSCHRIJVING" };
      int persons=0;
      int entries=0;
      int noemails=0;
      nextLine = dataFileReader.readLine();
      while(nextLine!=null) {
			 thisPerson.clear();
          int v = 0;
          nextLine += "\t";
          while(nextLine!=null&&v<10) {
              int tPos = nextLine.indexOf("\t");
              String value = "";
              if(tPos>-1) {
                  value = nextLine.substring(0,tPos).trim();
                  if(value.equals("NULL")) { value = ""; }
                  nextLine = nextLine.substring(tPos+1);
              } else {
                  log.info("Line ends before last label for person " + thisPerson.get("E_NAAM") + " in " + dataFile);
              }
              thisPerson.put(labels[v],value);
              v++;
          }
          while(v<labels.length){
              thisPerson.put(labels[v],"");
              v++;
          }
          thisPerson.put("ALIAS",createAlias(thisPerson,"E_NAAM","E_ROEPNAAM"));
          // can we use something different, people get duplicated if there name changes ?
          thisPerson.put("SOFI_NR","NMV_" + thisPerson.get("ALIAS"));
          boolean logPerson = logId.equals(thisPerson.get("SOFI_NR"));

          String thisPersonStr = (String) thisPerson.get("E_ROEPNAAM");
          if(!thisPerson.get("E_VRVG").equals("")) {
              thisPersonStr += " " + thisPerson.get("E_VRVG");
          }
          thisPersonStr += " " + thisPerson.get("E_NAAM") + " (" + thisPerson.get("SOFI_NR") + ")";

          // use the info to update the next person
          if(!lastId.equals(thisPerson.get("SOFI_NR"))) {

              personsNode = updatePerson(cloud,thisPerson,thisPersonStr,logPerson);

              // update email address (if allowed)
              String updateInfo = (String) personsNode.getValue("updateinfo");
              if(updateInfo==null) updateInfo = "1";
              if(!updateInfo.equals("0")) {
                  personsNode.setValue("email", thisPerson.get("MailPrive") );
              } else {
                  logMessage += "\n<br>Not allowed to update email address for: " + thisPersonStr;
              }
              personsNode.setValue("companyphone", thisPerson.get("Telefoon") );
              personsNode.setValue("cellularphone", thisPerson.get("TelefoonMobiel") );
              // not used in CAD: personsNode.setValue("dayofbirth", thisPerson.get("Geboortedatum") );
              personsNode.commit();
              persons++;
          }

          if(!thisPerson.get("K_S_WAARDE").equals("")) {
             thisPerson.put("locations",thisPerson.get("K_S_WAARDE"));
             Node destination = relatedNodes(cloud, thisPerson, personsNode, "locations", "externid",  "readmore", "readmore", "FUNC_OMS",logPerson);
             if(destination.getValue("naam")==null||destination.getValue("naam").equals("")) {
                 destination.setValue("naam", thisPerson.get("K_OF_S_OMSCHRIJVING"));
                 destination.commit();
             }
          }

          lastId = (String) thisPerson.get("SOFI_NR");

          nextLine = dataFileReader.readLine();
          entries++;
      }
      dataFileReader.close();
      logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize()
          + " - Number of NM vrijwilligers loaded from: " + dataFile + " is " + persons + " (number of entries is " + entries + ")";
    } catch(Exception e) {
      log.info(e);
    }
    return logMessage;
   }

   
   
   
   
   /**
    * Returns all addresses that match the specified zip code from the specified map.
    * 
    * @param zipCodeMap The map to retrieve the addresses from
    * @param zipCode The zip code to retrieve the addresses for
    * 
    * @return The retrieved addresses in an arraylist or null if none could be found
    */
   public static ArrayList getAddresses(TreeMap zipCodeMap, String zipCode) {
	   if(zipCodeMap != null && zipCode != null)
		   return (ArrayList)zipCodeMap.get(zipCode);
	   return null;
   }
   
   public static String getAddress(TreeMap zipCodeMap, String zipCode) {
      String address = null;
      
      // map contains arraylists now
      ArrayList addresses = getAddresses(zipCodeMap, zipCode);
      if(addresses != null)
         address = (String)addresses.get(0);
      return address;
   }

   /**
    * Returns all street names for the specified zip code from the specified map.
    * 
    * @param zipCodeMap The map to retrieve the addresses from
    * @param zipCode The zip code to retrieve the addresses for
    * @param street The value to return if the specified zip code could not be found in the map
    * 
    * @return The retrieved street names or the value of the parameter specified, if no street could be found
    */
   public static ArrayList getStreets(TreeMap zipCodeMap, String zipCode, String street) {
      ArrayList addresses = getAddresses(zipCodeMap, zipCode);
      if(addresses == null) {
    	  addresses = new ArrayList();
        if (street != null) addresses.add(street);
    	  return addresses;
      }
      ArrayList returner = new ArrayList();
      for(int i=0, j=addresses.size();i<j;i++) {
    	  String address = (String)addresses.get(i);
    	  if(address != null && address.indexOf(";") > 0)
    		  returner.add(address.substring(0, address.indexOf(";")));
      }
      
      return returner;
   }

   public static String getStreet(TreeMap zipCodeMap, String zipCode, String street) {
      ArrayList streets = getStreets(zipCodeMap, zipCode, street);
      
      return (String)streets.get(0);
   }

   public static String getCity(TreeMap zipCodeMap, String zipCode, String city) {
      String address = getAddress(zipCodeMap,zipCode);
      return (address!=null ? address.substring(address.lastIndexOf(";")+1) : city );
   }

   /**
    * This checks whether the specified house number is within the valid range on the streets of the specified zip code.
    *  
    * @param zipCodeMap The map to extract the streets from
    * @param zipCode The zip code to check against
    * @param iHouseNumber The house number to verify
    * 
    * @return True if one of the addresses is within the valid range, false otherwise
    */
   public static boolean isInRange(TreeMap zipCodeMap, String zipCode, int iHouseNumber) {
      ArrayList addresses = getAddresses(zipCodeMap,zipCode);

      boolean isInRange = false;
      for(int i=0, j=addresses.size();i<j;i++) {
	      String address = (String)addresses.get(i);
      if(address!=null) {
         int p1 = address.indexOf(";");
         int p2 = address.indexOf("_");
         int p3 = address.lastIndexOf("_");
         int p4 = address.lastIndexOf(";");
         int iHouseNumberLow = Integer.parseInt(address.substring(p1+1,p2));
         int iHouseNumberHigh = Integer.parseInt(address.substring(p2+1,p3));
         String sCode = address.substring(p3+1,p4);
         isInRange = (iHouseNumberLow <= iHouseNumber) && (iHouseNumber <= iHouseNumberHigh);
         if(isInRange) {
            if(sCode.equals("E")) { // E = even
               isInRange = (iHouseNumber % 2)==0;
            } else if(sCode.equals("O")) { // O = odd
               isInRange = (iHouseNumber % 2)==1;
            } else { // B = both
            }
         }
      }
	      if(isInRange)
	    	  break;
      }
      return isInRange;
   }

   public String loadZipCodes(ServletContext application, String zipFile, String temp) {
      // the zipcode table should be loaded in such a way that
      // based on zipcode and housenumber the related streetname and city can be found
      log.info("loadZipCodes " + zipFile);

      TreeMap zipCodeMap = new TreeMap();      // mapping of zipcodes to vector of streetname;housenumber_low;house_number_high;code;city

      String nextLine = "";
      String sZipCode = "";
      String sStreetName = "";
      String sHouseNumberLow = "";
      String sHouseNumberHigh = "";
      String sCode = "";
      String sCity = "";

      String logMessage = "";
      
      try {

        ZipUtil zu = new ZipUtil();
        Vector files = zu.unZip(zipFile,temp);

        if(files.size()>0) {

           String dataFile = (String) files.get(0);
           BufferedReader dataFileReader = getBufferedReader(temp + "/" + dataFile);
           nextLine = dataFileReader.readLine();
           int zipcodes = 0;
           int errors = 0;
           while(nextLine!=null) {

              sZipCode = "";
              sStreetName = "";
              sHouseNumberLow = "";
              sHouseNumberHigh = "";
              sCode = "";
              sCity = "";

              try {
                sZipCode = nextLine.substring(0,6);
                sHouseNumberHigh = nextLine.substring(6,12).trim();
                sCode = nextLine.substring(12,13).trim();
                sStreetName = nextLine.substring(13,31).trim();
                sCity = nextLine.substring(31,49).trim();
                sHouseNumberLow = nextLine.substring(50).trim();

              } catch (Exception e) {
                errors++;
                if("".equals(logMessage)) {
                  logMessage += "\n<br>Warning the following lines in " + dataFile + " do not contain a valid zipcode, housenumber_low, house_number_high, code, streetname, city";
                }
                logMessage += "\n<br>" + nextLine;
              }
              if(!sZipCode.equals("")&&!sStreetName.equals("POSTBUS")){
                 StringBuffer sbAddress =  new StringBuffer();
                 sbAddress.append(sStreetName);
                 sbAddress.append(';');
                 sbAddress.append(sHouseNumberLow);
                 sbAddress.append('_');
                 sbAddress.append(sHouseNumberHigh);
                 sbAddress.append('_');
                 sbAddress.append(sCode);
                 sbAddress.append(';');
                 sbAddress.append(sCity);
                 
                 // we might have multiple entries for one zip code
                 // (e.g. left and right side of street different names)
                 // thus we will use an arraylist of strings
                 ArrayList streets = (ArrayList)zipCodeMap.get(sZipCode);
                 if(streets == null) {
                	 streets = new ArrayList();
                	 zipCodeMap.put(sZipCode, streets);
                 }
                 streets.add(sbAddress.toString());
                 
                 zipcodes++;
              }
              nextLine = dataFileReader.readLine();
           }
           dataFileReader.close();
           application.setAttribute("zipCodeMap",zipCodeMap);
           logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize()
           + " - Number of zipcodes loaded from: " + dataFile + " unzipped from " 
           + zipFile + " (lm=" + lastModifiedDate(zipFile) + ") is " + zipcodes + " (number of errors " + errors + ")";
         }

      } catch(Exception e) {
        log.info("Error in reading " + zipFile + " on zipcode " + sZipCode);
        log.info("In line: " + nextLine);
        log.info(e);
      }
      return logMessage;
   }

   public String loadNMMembers(ServletContext application, String zipFile, String temp){
      // the NM Members table should be loaded in such a way that
      // based on a memberid the related zipcode can be found and compared with the zipcode entered by the user
      log.info("loadNMMembers " + zipFile);

      TreeMap zipCodeTable = new TreeMap();      // mapping of memberids to zipcodes
      TreeMap invZipCodeTable = new TreeMap();   // mapping from zipcode to vector of memberids
      TreeMap houseNumberTable = new TreeMap();  // mapping of memberids to housenumbers
      TreeMap houseExtTable = new TreeMap();     // mapping of memberids to houseext
      TreeMap lastNameTable = new TreeMap();     // mapping of memberids to lastname
      TreeMap invLastNameTable = new TreeMap();  // mapping from lastname to vector of memberids

      String nextLine = "";
      String sMemberId = "";
      String sZipCode = "";
      String sHouseNumber = "";
      String sHouseExt = "";
      String sLastName = "";
      
      String logMessage = "";
      
      try {

        ZipUtil zu = new ZipUtil();
        Vector files = zu.unZip(zipFile,temp);

        if(files.size()>0) {
          
           String dataFile = (String) files.get(0);          
           BufferedReader dataFileReader = getBufferedReader(temp + "/" + dataFile);
           nextLine = dataFileReader.readLine();
           int persons = 0;
           int errors = 0;
           while(nextLine!=null) {

              persons++;
              sMemberId = "";
              sZipCode = "";
              sHouseNumber = "";
              sHouseExt = "";
              sLastName = "";

              try {
                sMemberId = nextLine.substring(0,7);
                // delete trailing zero's
                while(!sMemberId.equals("")&&sMemberId.charAt(0)=='0') { sMemberId = sMemberId.substring(1); }

                sZipCode = nextLine.substring(7,13).trim();
                sHouseNumber = nextLine.substring(13,19).trim();
                sHouseExt = nextLine.substring(19,25).trim();
                sLastName = nextLine.substring(25).trim();
              } catch (Exception e) {
                errors++;
                if("".equals(logMessage)) { 
                  logMessage += "\n<br>Warning the following lines in " + dataFile + " do not contain a valid memberid, zipcode, housenumber, houseextension, lastname";
                }
                logMessage += "\n<br>" + nextLine;
              }
              if(!sMemberId.equals("")){
                 zipCodeTable.put(sMemberId,sZipCode); // there has to be an entry for members without zipcode (= foreign members)
                 if(!sZipCode.equals("")) {
                     Vector memberIdVector = null;
                     if(invZipCodeTable.containsKey(sZipCode)) {
                        memberIdVector = (Vector) invZipCodeTable.get(sZipCode);
                     } else {
                        memberIdVector = new Vector();
                     }
                     memberIdVector.add(sMemberId);
                     invZipCodeTable.put(sZipCode,memberIdVector);
                 }
                 if(!sHouseNumber.equals("")) { houseNumberTable.put(sMemberId,sHouseNumber); }
                 if(!sHouseExt.equals("")) { houseExtTable.put(sMemberId,sHouseExt); }
                 if(!sLastName.equals("")) {
                     lastNameTable.put(sMemberId,sLastName);
                     Vector memberIdVector = null;
                     if(invLastNameTable.containsKey(sLastName)) {
                        memberIdVector = (Vector) invLastNameTable.get(sLastName);
                     } else {
                        memberIdVector = new Vector();
                     }
                     memberIdVector.add(sMemberId);
                     invLastNameTable.put(sLastName,memberIdVector);
                 }
              }
              nextLine = dataFileReader.readLine();
           }
           dataFileReader.close();
           application.setAttribute("zipCodeTable",zipCodeTable);
           application.setAttribute("invZipCodeTable",invZipCodeTable);
           application.setAttribute("houseNumberTable",houseNumberTable);
           application.setAttribute("houseExtTable",houseExtTable);
           application.setAttribute("lastNameTable",lastNameTable);
           application.setAttribute("invLastNameTable",invLastNameTable);
           logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize() 
              + " - Number of persons loaded from: " + dataFile + " unzipped from "
              + zipFile + " (lm=" + lastModifiedDate(zipFile) + ") is " + persons + " (number of errors " + errors + ")";
         }

      } catch(Exception e) {
        log.info("Error in reading " + zipFile + " on memberid " + sMemberId + " & zipcode " + sZipCode);
        log.info("In line: " + nextLine);
        log.info(e);
      }
      return logMessage;
   }
   
   /**
   * logs the fields of all objects belonging to nodeManager
   */
   public void logList(Cloud cloud, String nodeManager, String [] fields) {
   
      NodeList nl = cloud.getNodeManager(nodeManager).getList(null,fields[0],"UP");
      for(int i=0;i<nl.size();i++) {
        Node n = nl.getNode(i);
        String logLine = "";
        for(int j=0; j< fields.length; j++) {
          logLine += n.getStringValue(fields[j]) + "  ";
        }
        log.info(logLine);
      }
   }

   public void readCSV(Cloud cloud, int importType) {

        // HashMap user = new HashMap();
        // user.put("username","admin");
        // user.put("password","");
        // Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase","name/password",user);

        MMBaseContext mc = new MMBaseContext();
        ServletContext application = mc.getServletContext();
        String requestUrl = (String) application.getAttribute("request_url");
        if(requestUrl==null) { requestUrl = "www.natuurmonumenten.nl"; }

        String logSubject = "Log import " +  requestUrl;

        ApplicationHelper ap = new ApplicationHelper(cloud);
        String toEmailAddress = ap.getToEmailAddress();
        String fromEmailAddress = ap.getFromEmailAddress();
        String incoming = ap.getIncomingDir();
        String temp = ap.getTempDir();

        String beauZip = incoming + "beaudata.zip"; // will be unzipped to temp
        String dataFile = temp + "beauexport.csv";
        String emailFile = temp + "mbexport.csv";
        String orgFile = temp + "orgschemaexport.csv";
        String nmvZip = incoming + "nmvdata.zip"; // will be unzipped to temp
        String nmvFile = temp + "nmvexport.csv";
        String membersZip = incoming + "lrscad.zip";
        String zipCodeZip = incoming + "postcode.zip";

        String fileList = "";
        if(importType==ONLY_MEMBERLOAD) {
             fileList = membersZip;
        } else if(importType==ONLY_ZIPCODELOAD) {
             fileList = zipCodeZip;
        } else {
           fileList += membersZip+"\n"+zipCodeZip+"\n"+dataFile+"\n"+nmvFile+"\n"+emailFile+"\n"+orgFile;
        }

        try {
              ZipUtil zu = new ZipUtil();
              
              log.info("Started import of: " + fileList);
              
              int nodesMarked = 0;
              int nodesDeleted = 0;
              int numberOfEmptyDept = 0;
              String logMessage =  "\n<br>" + su.getDateTimeString() + su.jvmSize() + " - Started import for " +  requestUrl;
              
              if(importType==FULL_IMPORT) {
              
              Vector files = new Vector();
              logMessage += "\nUnzipping " + beauZip + " (lm=" + lastModifiedDate(beauZip) + ")";
              files.addAll(zu.unZip(beauZip,temp));
              if(files.size()!=0) {
               
                // start with marking all relations as inactive
                String [] employeeRelations = {"readmore","afdelingen","readmore","locations"};
                  //setting to value of [1] unless it is equal to [2] when we don't change
                String [] employeeFields = {"importstatus","inactive",IGNORE_BEAUFORT};
                String [] departmentRelations = {"posrel","afdelingen"};
                // the importstatus field of afdelingen can be 'inactive', 'active' or a comma seperated list of descendants
                String [] departmentFields = {"importstatus","inactive",""};
                nodesMarked = markNodesAndRelations(cloud,"medewerkers",employeeRelations,employeeFields);
                nodesMarked += markNodesAndRelations(cloud,"afdelingen",departmentRelations,departmentFields);
                
                TreeMap emails = getEmails(emailFile);
                logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize() + " - Emails are imported from: " + emailFile;
                logMessage += updateOrg(cloud,orgFile);
                logMessage += updatePersons(cloud, emails, dataFile);
               
                // finish with deleting all inactive relations; employees and departments are never deleted because they could be created manually
                employeeFields[1] = "-1"; // prevent employees from being deleted
                departmentFields[1] = "-1";  // prevent departments from being deleted
                nodesDeleted = deleteNodesAndRelations(cloud,"medewerkers",employeeRelations,employeeFields);
                nodesDeleted += deleteNodesAndRelations(cloud,"afdelingen",departmentRelations,departmentFields);
              
                numberOfEmptyDept = updateDepartments(cloud);
                logMessage +=  "\n<br>" + su.getDateTimeString() + su.jvmSize()
                   + " - Number of nodes and relations marked as inactive before update: " + nodesMarked
                   + "\n<br>Number of inactive nodes deleted: " + nodesDeleted
                   + "\n<br>Number of departments without employees: " + numberOfEmptyDept;
              }

              if(ap.isInstalled("NatMM")) {
                 logMessage += "\nUnzipping " + nmvZip + " (lm=" + lastModifiedDate(nmvZip) + ")";
                 files.addAll(zu.unZip(nmvZip,temp));
                 logMessage += updateNMV(cloud, nmvFile);
              }
            }
            if(ap.isInstalled("NatMM")) {
               if( importType==FULL_IMPORT || importType==ONLY_MEMBERLOAD) {
                  logMessage += loadNMMembers(application,membersZip,temp);
               }
               if(importType==FULL_IMPORT || importType==ONLY_ZIPCODELOAD) {
                  logMessage += loadZipCodes(application,zipCodeZip,temp);
               }
            }

            logMessage += "\n<br>" + su.getDateTimeString() + su.jvmSize() + " - Finished import";

            Node emailNode = cloud.getNodeManager("email").createNode();
            emailNode.setValue("to", toEmailAddress);
            emailNode.setValue("from", fromEmailAddress);
            emailNode.setValue("subject", logSubject);
            emailNode.setValue("replyto", fromEmailAddress);
            emailNode.setValue("body","<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\"></multipart>"
                            + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                            + "<html>" + logMessage + "</html>"
                            + "</multipart>");
            emailNode.commit();
            emailNode.getValue("mail(oneshot)");

            log.info("Finished import");

            // log.info(thisPerson);
            // log.info(logMessage);
            // log.info(emails);

        } catch(Exception e) {
            log.info(e);
        }
    }

    private Thread getKicker(){
       Thread  kicker = Thread.currentThread();
       if(kicker.getName().indexOf("CSVReaderThread")==-1) {
            kicker.setName("CSVReaderThread / " + su.getDateTimeString());
            kicker.setPriority(Thread.MIN_PRIORITY+1); // does this help ??
       }
       return kicker;
    }

    public CSVReader() {
      this.importType = FULL_IMPORT;
      Thread kicker = getKicker();
      log.info("CSVReader(): " + kicker);
    }

    public CSVReader(int importType) {
      this.importType = importType;
      Thread kicker = getKicker();
      log.info("CSVReader(" + importType + "): " + kicker);
    }

    public void run () {
      Thread kicker = getKicker();
      log.info("run(): " + kicker);
      Cloud cloud = CloudFactory.getCloud();
      ApplicationHelper ap = new ApplicationHelper(cloud);
      if(ap.isInstalled("NatMM") || ap.isInstalled("NMIntra")) {
        readCSV(cloud, this.importType);
      }
    }
}
