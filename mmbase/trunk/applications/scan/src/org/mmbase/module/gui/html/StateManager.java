/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.lang.*;
import java.util.*;

import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;


/**
 * The StateManager class is a utility object for the Generic Editor Structure
 * wich connects to the MMEdit module.
 * 
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @version 26-2-2001
 */

public class StateManager implements CommandHandlerInterface {
	
	public MMBase mmBase; // Reference to the mmBase module
	Hashtable editStates; // HashTable with editstates indexed by usernames

	// Logger
	private static Logger log = Logging.getLoggerInstance(StateManager.class.getName());
	
	/**
	 * Initialises the connection to mmBase and initialises the StateManager
	 */ 
	public StateManager(MMBase mmBase) {
		this.mmBase = mmBase;
		editStates = new Hashtable();		
	}

	/**
	 * Loads all previously pesistified editstates from the database
	 */
	public void initUserStates() {
	}

	/**
	 * The EditState contains all the information an editor needs to
	 * configure the editing fields for a certain object. A new EditState is
	 * created if the user is unknown.
	 */
	public EditState getEditState(String user) {
		EditState result;

		result = (EditState)editStates.get(user);
		if (result == null) {
			result = new EditState(user,mmBase);
			editStates.put(user, result);
		}

		return result;
	}

	/**
     * This method is the entry point for the mmEdit object to signal 
	 * the statemanager that the user has gone to another section in an 
	 * editor.
	 */
	public String replace(scanpage sp, StringTokenizer commands) {
		String token;
		String userName=HttpAuth.getRemoteUser(sp);
	
		if (userName==null) return("StateManager-> not logged in");	
		EditState state = getEditState(userName);
  	//	log.debug("STATE="+state);

		if (commands.hasMoreTokens()) {
			token = commands.nextToken();
			if (token.equals("SETBUILDER")) {
				if (commands.hasMoreTokens()) {
					state.setBuilder(commands.nextToken());
					if (commands.hasMoreTokens()) {
						state.setHtmlValue("JOINNODE",commands.nextToken());
					} else {
						state.setHtmlValue("JOINNODE","");
					}
				}
			} else if (token.equals("GETBUILDER")) {
					return(state.getBuilderName());
			} else if (token.equals("DELBUILDER")) {
					state.popState();
			} else if (token.equals("CLEARBUILDERS")) {
					state.clear();
			} else if (token.equals("ADDRELATION")) {
					state.addRelation(userName);
			} else if (token.equals("SETHTMLVALUE")) {
					state.setHtmlValue(commands.nextToken(),commands.nextToken());
			} else if (token.equals("GETHTMLVALUE")) {
					return(state.getHtmlValue(commands.nextToken()));
			} else if (token.equals("SETEDITNODE")) {
					state.setEditNode(commands.nextToken(),userName);
			} else if (token.equals("GETEDITNODE")) {
					return(""+state.getEditNodeNumber());
			} else if (token.equals("GETEDITSRCDUTCHNAME")) {
					return(""+state.getEditNodeSrcDutchName());
			} else if (token.equals("GETEDITDSTDUTCHNAME")) {
					return(""+state.getEditNodeDstDutchName());
			} else if (token.equals("GETEDITSRCNAME")) {
					return(""+state.getEditNodeSrcName());
			} else if (token.equals("GETEDITDSTNAME")) {
					return(""+state.getEditNodeDstName());
			} else if (token.equals("GETEDITSRCNODE")) {
					return(""+state.getEditNodeSrcNumber());
			} else if (token.equals("GETEDITDSTNODE")) {
					return(""+state.getEditNodeDstNumber());
			} else if (token.equals("GETEDITSRCGUIINDICATOR")) {
					return(""+state.getEditNodeSrcGuiIndicator());
			} else if (token.equals("GETEDITDSTGUIINDICATOR")) {
					return(""+state.getEditNodeDstGuiIndicator());
			} else if (token.equals("NEWNODE")) {
					state.NewNode(userName);
			} else if (token.equals("NEWINSNODE")) {
					newInsNode(state,userName,commands);
			} else if (token.equals("REMOVENODE")) {
					state.removeNode();
			} else if (token.equals("REMOVEEDITOR")) {
					state.popState();
			} else if (token.equals("ISCHANGED")) {
					if (state.isChanged()) {
						return("YES");
					} else {
						return("NO");
					}
			}
			return ("");
		}

		return "Command not defined (StateManager)";
	}


    /**
    * Creates a new Node, depending on the builder name (or relation name) specified in the StringTokenizer.
    * This method is used to create relation nodes
    * @param ed Editstate in which to add the new node.
    * @param userName User who becomes owner of the new node
    * @param tok Tokens used to configure the node. The next three tokens should be:
    *	<ul>
    *	<li> The number of the node to link FROM </li>
    *	<li> The number of the node to link TO</li>
    *	<li> The name of the builder to use or relation to add (determines type of node and/or relation)</li>
    *       </ul>
    * @return Always true. If the addition was successful, a new node has been added to the EditState object.
    **/

    boolean newInsNode(EditState ed,String userName,StringTokenizer tok) {
        try {
            String tmp=tok.nextToken();
            int n1=Integer.parseInt(tmp);

            tmp=tok.nextToken();
            int n2=Integer.parseInt(tmp);

            String builder=tok.nextToken();

            int rtype=-1;

        	// tests if the 'builder' specified is actually a relationname.
            // If so, the number of the relation in RelDef is obtained,
            // and the name of the builder to use is determined (if explicitly given)).

            MMObjectBuilder bul = null;
            rtype = mmBase.getRelDef().getNumberByName(builder);

            if ((rtype!=-1) && (RelDef.usesbuilder))  { // relation found
                MMObjectNode node=mmBase.getRelDef().getNode(rtype);  // retrieve the reldef node
                bul = mmBase.getRelDef().getBuilder(node);
            } else {
                bul = mmBase.getMMObject(builder);
            }

            // check whether the builder is a valid relationbuilder.
            // otherwise assign InsRel
            // note that is the builder specified is not an InsRel-derived builder, it will be overridden

            if (!(bul instanceof InsRel)) {
                bul=mmBase.getInsRel();
            }

            ed.popState();
            ed.setBuilder(bul.getTableName());

            ed.NewNode(userName);
            MMObjectNode node=ed.getEditNode();
            node.setValue("snumber",n1);
            node.setValue("dnumber",n2);

            // If rtype is unknown, try to get the type from TypeRel

            if ((bul == mmBase.getInsRel()) && (rtype==-1)) {
                rtype=mmBase.getTypeRel().getAllowedRelationType(bul.getNodeType(n1),bul.getNodeType(n2));
            }

		    // assign rtype. Note that rtype is only set if it is actually known.
		    // in rare cases, rtype can be -1. This happens if the relationname specified is the name of a
		    // relationbuilder for which no relation definition with the same name is defined.
            if (rtype!=-1) {
                node.setValue("rnumber",rtype);
            }

        } catch (Exception e) {
            log.error("StateManager -> Can't create insnode");
            e.printStackTrace();
        }
        return(true);
    }


	/**
	* setSearchVals
	*/
	boolean setSearchValues(EditState ed, Hashtable vars) { 
		String varline;
		ed.clearSearchValues();
		for (Enumeration h=vars.keys();h.hasMoreElements();) {
			varline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(varline,"-\n\r");
				String var=tok.nextToken();	
				if (var.equals("STATE")) var=tok.nextToken();	
				if (var.equals("SEARCHVALUE")) {
					String key=tok.nextToken();	
					String keyval=(String)vars.get("STATE-SEARCHVALUE-"+key);	
					ed.setSearchValue(key,keyval);	
				}
		}
		MMObjectBuilder bul=ed.getBuilder();
		ed.setSelectionQuery(createSelectionQuery(ed.getSearchValues(),bul));
		return(true);
	}


	String createSelectionQuery(Hashtable skeys,MMObjectBuilder bul) {
		String where="MMNODE ",key,val;
		String name=bul.getTableName();
			for (Enumeration h=skeys.keys();h.hasMoreElements();) {
				key=(String)h.nextElement();	
				val=(String)skeys.get(key);	
					if (val!=null && !val.equals("")) {
					// val to lower for search
					val=val.toLowerCase();
					if (key.equals("maxage")) {
						int ival=30;
						try {
							ival=Integer.parseInt(bul.getSearchAge());
						} catch(Exception e) {}
						try {
							ival=Integer.parseInt(val);
						} catch(Exception e) {}
						DayMarkers daym=(DayMarkers)bul.mmb.getMMObject("daymarks");
						int mark=daym.getDayCountAge(ival);
						if (where.equals("MMNODE ")) {
							where+=name+".number=G"+mark;
						} else {
							where+="+"+name+".number=G"+mark;
						}
					} else {
						if (where.equals("MMNODE ")) {
							where+=name+"."+key+"==*"+val+"*";
						} else {
							where+="+"+name+"."+key+"==*"+val+"*";
						}
					}
				}
			}

		return(where);
	}

	/**
	 * An object has been selected and the EditState of the specific user
	 * is updated.
	 */
	void updateSelectedObject(String user, String objectID) {
	}

	/**
	 * A field has been selected to edit and the EditState for the specific 
	 * user is updated.
	 */
	void updateEditField(String user, String fieldName) {
	}

	/**
	 * a new relation has been initiated and the EditState for the specific 
	 * user is updated.
	 */ 
	void initLink(String user, String objectType) {
	}

	/**
	 * List commands
	 */
	public Vector getList(scanpage sp, StringTagger args, StringTokenizer command) throws org.mmbase.module.ParseException {
		String token;
		String userName=HttpAuth.getRemoteUser(sp);
		EditState state = getEditState(userName);
		Vector result = new Vector();
		
		if (command.hasMoreTokens()) {
			token = command.nextToken();
			if (token.equals("GETOPENBUILDERS")) {	
				return(getOpenBuilders(state,args));		
			}
		}
		result.addElement("No List command defined (FieldEditor)");
		return result;
	}	

	/**
	 * The hook that passes all form related pages to the correct handler
	 */
	public boolean process(scanpage sp, StringTokenizer command,Hashtable cmds, Hashtable vars) {

		String token;
		String userName=HttpAuth.getRemoteUser(sp);
		EditState state = getEditState(userName);

		String cmd,cmdline;
		for (Enumeration h=cmds.keys();h.hasMoreElements();) {
			cmdline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
			if (tok.hasMoreTokens()) {
				cmd=tok.nextToken(); // read away dummy STATE-
				cmd=tok.nextToken();	
				if (cmd.equals("SETSEARCHVALUES")) return(setSearchValues(state,vars));
				if (cmd.equals("REMOVENODE")) {
					String qw=(String)cmds.get("STATE-REMOVENODE");	
					if (qw.equals("YES")) {	
						// delete the relations to this node and
						// the node itself
						state.removeRelations();
						state.removeNode();
						state.setHtmlValue("Chooser","select");
						state.setHtmlValue("Work","empty");
					}
				} else 
				if (cmd.equals("REMOVERELATION")) {
					String qw=(String)cmds.get("STATE-REMOVERELATION");	
					if (qw.equals("YES")) {	
						state.removeNode();
						state.popState();
						state.setHtmlValue("Chooser","realFieldEdit");
						state.setHtmlValue("Work","empty");
					}
				} else 
				if (cmd.equals("NEXTFIELD")) {
					String currentfield=(String)cmds.get("STATE-NEXTFIELD");	
					state.setHtmlValue("Work","nextfield");
					MMObjectBuilder bul=state.getBuilder();
					FieldDefs ndefs=bul.getNextField(currentfield);
					if (ndefs!=null) {
						state.setHtmlValue("NEXTFIELD",""+ndefs.getGUIType()+".shtml?"+ndefs.getDBName()+"+"+ndefs.getGUIName(state.getLanguage()));
					} else {
						state.setHtmlValue("NEXTFIELD","empty.shtml");
					}
				} else 
				if (cmd.equals("SETHTMLVALUE")) {
					String field=tok.nextToken();
					String value=(String)cmds.get("STATE-SETHTMLVALUE-"+field);	
					state.setHtmlValue(field,value);
				}
			}
		}
		return false;
	}

	public Vector getOpenBuilders(EditState state,StringTagger args) {
		Vector results=new Vector();
		Vector nodes=state.getEditStates();
		EditStateNode node;
		MMObjectNode curnode=state.getEditNode(); // problem
		for (Enumeration h=nodes.elements();h.hasMoreElements();) {
			node=(EditStateNode)h.nextElement();	
			results.addElement(node.getDutchBuilderName());
			if (curnode==node.getEditNode()) {
				results.addElement("a");
			} else { 
				results.addElement("n");
			}
		}
		args.setValue("ITEMS","2");
		return(results);
	}

	/**
	 * The EditState contains all the information an editor needs to
	 * configure the editing fields for a certain object. A new EditState is
	 * created if the user is unknown.
	 */
	public EditState getState(String user) {
		EditState result;

		result = (EditState)editStates.get(user);
		if (result == null) result = new EditState(user,mmBase);
		
		return result;
	}

}

