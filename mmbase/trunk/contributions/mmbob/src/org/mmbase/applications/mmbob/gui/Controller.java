/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.util.logging.*;

import org.mmbase.applications.mmbob.*;


/**
 * @author Daniel Ockeloen
 * @version $Id: guiController.java
 */
public class Controller {

	private static Logger log = Logging.getLoggerInstance(Controller.class.getName());
	private static Cloud cloud;
       	NodeManager manager;
	CloudContext context;


	public Controller() {
		cloud=LocalContext.getCloudContext().getCloud("mmbase");

		// hack needs to be solved
        	manager=cloud.getNodeManager("typedef");
		if (manager==null) log.error("Can't access builder typedef");
		context=LocalContext.getCloudContext();

		// start the ForumManager
		ForumManager.init();
	}

	public List getPostAreas(String id,String sactiveid) {
                List list = new ArrayList();
		try {
		int activeid=Integer.parseInt(sactiveid);
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Forum f=ForumManager.getForum(id);
		if (f!=null) {
			Enumeration e=f.getPostAreas();	
			while (e.hasMoreElements()) {
				PostArea area=(PostArea)e.nextElement();
                        	MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("name",area.getName());
				virtual.setValue("description",area.getDescription());
				virtual.setValue("id",area.getId());
				virtual.setValue("postthreadcount",area.getPostThreadCount());
				virtual.setValue("postcount",area.getPostCount());
				virtual.setValue("viewcount",area.getViewCount());
				virtual.setValue("lastposter",area.getLastPoster());
				virtual.setValue("lastposttime",area.getLastPostTime());
				virtual.setValue("lastsubject",area.getLastSubject());
				virtual.setValue("moderators",area.getModeratorsLine("profile.jsp"));
				list.add(virtual);

				if (activeid!=-1) {
					Poster ap=f.getPoster(activeid);
					ap.signalSeen();
					addActiveInfo(virtual,ap);
					if (ap!=null && f.isAdministrator(ap.getAccount())) {
						virtual.setValue("isadministrator","true");
					} else {
						virtual.setValue("isadministrator","false");
					}
				}
			}
		}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return list;
	}


	public List getForums() {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Enumeration e=ForumManager.getForums();	
		while (e.hasMoreElements()) {
			Forum f=(Forum)e.nextElement();
                       	MMObjectNode virtual = builder.getNewNode("admin");
			virtual.setValue("name",f.getName());
			virtual.setValue("id",f.getId());
			virtual.setValue("description",f.getDescription());
			virtual.setValue("postareacount",f.getPostAreaCount());
			virtual.setValue("postthreadcount",f.getPostThreadCount());
			virtual.setValue("postcount",f.getPostCount());
			virtual.setValue("postersonline",f.getPostersOnlineCount());
			virtual.setValue("posterstotal",f.getPostersTotalCount());
			virtual.setValue("postersnew",f.getPostersNewCount());
			virtual.setValue("viewcount",f.getViewCount());
			virtual.setValue("lastposter",f.getLastPoster());
			virtual.setValue("lastposttime",f.getLastPostTime());
			virtual.setValue("lastsubject",f.getLastSubject());
			list.add(virtual);
		}
		return list;
	}

	public List getPostThreads(String forumid,String postareaid,int activeid,int pagesize,int page, int overviewpagesize,String baseurl,String cssclass) {
                List list = new ArrayList();

                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		if (cssclass==null) cssclass="";
		// create a result list

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			Iterator e=a.getPostThreads(page,overviewpagesize);	
			while (e.hasNext()) {
				PostThread thread=(PostThread)e.next();
                       		MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("name",thread.getSubject());
				virtual.setValue("id",thread.getId());
				virtual.setValue("mood",thread.getMood());

				Poster ap=f.getPoster(activeid);
				if (ap!=null) {
					virtual.setValue("state",thread.getState(ap));
				} else {
					virtual.setValue("state",thread.getState());
				}
				virtual.setValue("type",thread.getType());
				virtual.setValue("creator",thread.getCreator());
				virtual.setValue("postcount",thread.getPostCount());
				virtual.setValue("replycount",thread.getPostCount()-1);
				virtual.setValue("viewcount",thread.getViewCount());
				virtual.setValue("lastposter",thread.getLastPoster());
				virtual.setValue("lastposttime",thread.getLastPostTime());
				virtual.setValue("lastsubject",thread.getLastSubject());
				//newnode.setStringValue("threadnav",thread.getLastSubject());
				virtual.setValue("navline",thread.getNavigationLine(baseurl,pagesize,cssclass));
				list.add(virtual);
			}
		}

		return list;
	}


	public List getPostings(String forumid,String postareaid,String postthreadid,int activeid,int page,int pagesize) {
                List list = new ArrayList();

                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {
					Iterator e=t.getPostings(page,pagesize);	
					int pos=((page-1)*pagesize)+1;

					while (e.hasNext()) {
						Posting p=(Posting)e.next();
                       				MMObjectNode virtual = builder.getNewNode("admin");
						virtual.setValue("pos",pos++);
						virtual.setValue("subject",p.getSubject());
						virtual.setValue("body",p.getBody());
						virtual.setValue("edittime",p.getEditTime());
						Poster po=f.getPoster(p.getPoster());
						if (po!=null) {
							virtual.setValue("poster",p.getPoster());
							addPosterInfo(virtual,po);
						} else {	
							virtual.setValue("poster",p.getPoster());
							virtual.setValue("guest","true");
						}
						virtual.setValue("posttime",p.getPostTime());
						virtual.setValue("id",p.getId());
						// should be moved out of the loop
						if (activeid!=-1) {
							Poster ap=f.getPoster(activeid);
							ap.signalSeen();
							ap.seenThread(t);
							addActiveInfo(virtual,ap);
							if (po!=null && po.getAccount().equals(ap.getAccount())) {
								virtual.setValue("isowner","true");
							} else {
								virtual.setValue("isowner","false");
							}
							if (ap!=null && a.isModerator(ap.getAccount())) {
								virtual.setValue("ismoderator","true");
							} else {
								virtual.setValue("ismoderator","false");
							}
						}

						list.add(virtual);
					}
				}
			}
		}

		return list;
	}


	public List getModerators(String forumid,String postareaid) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				Enumeration e=a.getModerators();
				while (e.hasMoreElements()) {
					Poster p=(Poster)e.nextElement();
                       			MMObjectNode virtual = builder.getNewNode("admin");
					virtual.setValue("id",p.getId());
					virtual.setValue("account",p.getAccount());
					virtual.setValue("firstname",p.getFirstName());
					virtual.setValue("lastname",p.getLastName());
					list.add(virtual);
				}
			}
		}
		return list;
	}


	public List getPostersOnline(String forumid) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Enumeration e=f.getPostersOnline();
			while (e.hasMoreElements()) {
				Poster p=(Poster)e.nextElement();
                       		MMObjectNode virtual = builder.getNewNode("admin");
				virtual.setValue("id",p.getId());
				virtual.setValue("account",p.getAccount());
				virtual.setValue("firstname",p.getFirstName());
				virtual.setValue("lastname",p.getLastName());
				virtual.setValue("location",p.getLocation());
				virtual.setValue("lastseen",p.getLastSeen());
				list.add(virtual);
			}
		}
		return list;
	}


	public List getNonModerators(String forumid,String postareaid) {
                List list = new ArrayList();
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				Enumeration e=a.getNonModerators();
				while (e.hasMoreElements()) {
					Poster p=(Poster)e.nextElement();
                       			MMObjectNode virtual = builder.getNewNode("admin");
					virtual.setValue("id",p.getId());
					virtual.setValue("account",p.getAccount());
					virtual.setValue("firstname",p.getFirstName());
					virtual.setValue("lastname",p.getLastName());
					list.add(virtual);
				}
			}
		}
		return list;
	}


	public MMObjectNode getForumInfo(String id,String sactiveid) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
               	MMObjectNode virtual = builder.getNewNode("admin");
		try {
		int activeid=Integer.parseInt(sactiveid);		

		Forum f=ForumManager.getForum(id);
		if (f!=null) {
			virtual.setValue("name",f.getName());
			virtual.setValue("language",f.getLanguage());
			virtual.setValue("description",f.getDescription());
			virtual.setValue("postareacount",f.getPostAreaCount());
			virtual.setValue("postthreadcount",f.getPostThreadCount());
			virtual.setValue("postcount",f.getPostCount());
			virtual.setValue("postersonline",f.getPostersOnlineCount());
			virtual.setValue("posterstotal",f.getPostersTotalCount());
			virtual.setValue("postersnew",f.getPostersNewCount());
			virtual.setValue("viewcount",f.getViewCount());
			virtual.setValue("lastposter",f.getLastPoster());
			virtual.setValue("lastposttime",f.getLastPostTime());
			virtual.setValue("lastsubject",f.getLastSubject());
			if (activeid!=-1) {
				Poster ap=f.getPoster(activeid);
				ap.signalSeen();
				addActiveInfo(virtual,ap);
				if (ap!=null && f.isAdministrator(ap.getAccount())) {
					virtual.setValue("isadministrator","true");
				} else {
					virtual.setValue("isadministrator","false");
				}
			}
		}
		} catch (Exception e) {
		}
		return virtual;
	}


	public MMObjectNode getPosterInfo(String id,String posterid) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		Forum f=ForumManager.getForum(id);
		if (f!=null) {
			if (posterid!=null) {
				Poster po=f.getPoster(posterid);
				addPosterInfo(virtual,po);
			}
		}
		return virtual;
	}


	public boolean editPoster(String forumid,int posterid,String firstname,String lastname,String email,String gender,String location) {

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Poster p=f.getPoster(posterid);
			if (p!=null) {
				p.setFirstName(firstname);
				p.setLastName(lastname);
				p.setEmail(email);
				p.setGender(gender);
				p.setLocation(location);
				p.savePoster();
			} else {
				return false;
			}
		}
		return true;
	}

	public String createPoster(String forumid,String account,String password,String firstname,String lastname,String email,String gender,String location) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Poster p=f.getPoster(account);
			if (p==null) {
				p=f.createPoster(account,password);	
				if (p!=null) {
					p.setFirstName(firstname);
					p.setLastName(lastname);
					p.setEmail(email);
					p.setGender(gender);
					p.setLocation(location);
					p.setPostCount(0);
					p.savePoster();
				} else {
					return "createerror";
				}
			} else {
				return "inuse";
			}
		}
		return "ok";
	}

	public MMObjectNode getPostAreaInfo(String forumid,String postareaid,int activeid,int page,int pagesize,String baseurl,String cssclass) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
	
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			virtual.setValue("name",a.getName());
			virtual.setValue("postthreadcount",a.getPostThreadCount());
			virtual.setValue("postcount",a.getPostCount());
			virtual.setValue("viewcount",a.getViewCount());
			virtual.setValue("lastposter",a.getLastPoster());
			virtual.setValue("lastposttime",a.getLastPostTime());
			virtual.setValue("lastsubject",a.getLastSubject());
                        virtual.setValue("navline",a.getNavigationLine(baseurl,page,pagesize,cssclass));
                        virtual.setValue("pagecount",a.getPageCount(pagesize));
			if (activeid!=-1) {
				Poster ap=f.getPoster(activeid);
				ap.signalSeen();
				// addActiveInfo(virtual,ap);
				if (ap!=null && f.isAdministrator(ap.getAccount())) {
					virtual.setValue("isadministrator","true");
				} else {
					virtual.setValue("isadministrator","false");
				}
			}
		}
		return virtual;
	}


	public boolean removePostArea(String forumid,String postareaid) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			boolean result=f.removePostArea(postareaid);
			return result;
		}
		return false;
	}

	public boolean profileUpdated(String forumid,int posterid) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			Poster p=f.getPoster(posterid);
			if (p!=null) {
				return p.profileUpdated();
			}
		}
		return false;
	}

	public boolean removePostThread(String forumid,String postareaid,String postthreadid) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				boolean result=a.removePostThread(postthreadid);
				return result;
			}
		}
		return false;
	}



	public MMObjectNode getPostThreadNavigation(String forumid,String postareaid,String postthreadid,int page,int pagesize,String baseurl,String cssclass) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");

		if (cssclass==null) cssclass="";

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {
					virtual.setValue("navline",t.getNavigationLine(baseurl,page,pagesize,cssclass));
					virtual.setValue("lastpage",""+t.isLastPage(page,pagesize));
					virtual.setValue("pagecount",t.getPageCount(pagesize));
				}
			}
		}
		return virtual;
	}

	public boolean postReply(String forumid,String postareaid,String postthreadid,String subject,String poster,String body) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {
					t.postReply(subject,poster,body);
				}
			}
		}
		return true;
	}


	public MMObjectNode newPost(String forumid,String postareaid,String subject,String poster,String body) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                MMObjectNode virtual = builder.getNewNode("admin");

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				int postthreadid=a.newPost(subject,poster,body);
				virtual.setValue("postthreadid",postthreadid);
			}
		}
		return virtual;
	}


	public MMObjectNode newPrivateMessage(String forumid,String subject,String poster,String to,String body) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                MMObjectNode virtual = builder.getNewNode("admin");
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			int privatemessageid=f.newPrivateMessage(poster,to,subject,body);
			virtual.setValue("privatemessageid",privatemessageid);
		}
		return virtual;
	}


	public MMObjectNode newFolder(String forumid,int activeid,String newfolder) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

                MMObjectNode virtual = builder.getNewNode("admin");
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			int folderid=f.newFolder(activeid,newfolder);
			virtual.setValue("folderid",folderid);
		}
		return virtual;
	}


	public boolean removeFolder(String forumid,int activeid,String foldername) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			return f.removeFolder(activeid,foldername);
		}
		return false;
	}


	public boolean newModerator(String forumid,String postareaid,String sactiveid,String smoderatorid) {
		try {
		int activeid=Integer.parseInt(sactiveid);
		int moderatorid=Integer.parseInt(smoderatorid);
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				Poster ap=f.getPoster(activeid);
				Poster mp=f.getPoster(moderatorid);
				if (ap!=null && f.isAdministrator(ap.getAccount())) {
					a.addModerator(mp);
				}
			}
		}
		} catch(Exception e) {}
		return true;
	}

	public boolean removeModerator(String forumid,String postareaid,int activeid,int moderatorid) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				Poster ap=f.getPoster(activeid);
				Poster mp=f.getPoster(moderatorid);
				if (ap!=null && f.isAdministrator(ap.getAccount())) {
					a.removeModerator(mp);
				}
			}
		}
		return true;
	}


	public boolean editPost(String forumid,String postareaid,String postthreadid,int postingid,int activeid,String subject,String body) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {	
					Posting p=t.getPosting(postingid);

					// am i allowed to edit ?
					Poster ap=f.getPoster(activeid);
					if (ap.getId()==activeid || a.isModerator(ap.getAccount())) {
						p.setSubject(subject);
						p.setBody(body);
						p.setEditTime((int)(System.currentTimeMillis()/1000));
						p.save();
						ap.signalSeen();
					}
				}
			}
		}
		return true;
	}


	public boolean editPostThread(String forumid,String postareaid,String postthreadid,int activeid,String mood,String state,String type) {
		//log.info("F="+forumid+" A="+postareaid+" T="+postthreadid);
		// log.info("M="+mood+" S="+state+" T="+type);
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {	
					// am i allowed to edit ?
					Poster ap=f.getPoster(activeid);
					if (a.isModerator(ap.getAccount())) {
						t.setType(type);
						t.setMood(mood);
						t.setState(state);
						t.save();
					} else {
						log.info("postthread edit tried but not allowed by poster");
					}
				}
			}
		}
		return true;
	}

	public boolean removePost(String forumid,String postareaid,String postthreadid,int postingid,int activeid) {

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				PostThread t=a.getPostThread(postthreadid);
				if (t!=null) {	
					Posting p=t.getPosting(postingid);

					// am i allowed to edit ?
					Poster ap=f.getPoster(activeid);
					if (ap.getId()==activeid || a.isModerator(ap.getAccount())) {
						p.remove();
						ap.signalSeen();
					}
				}
			}
		}
		return true;
	}


	public MMObjectNode newPostArea(String forumid,String name,String description) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");

		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			int postareaid=f.newPostArea(name,description);
			virtual.setValue("postareaid",postareaid);
		}
		return virtual;
	}


	public MMObjectNode newForum(String name,String language,String description,String account,String password) {
                VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
                MMObjectNode virtual = builder.getNewNode("admin");
		int forumid=ForumManager.newForum(name,language,description,account,password);
		virtual.setValue("forumid",forumid);
		return virtual;
	}


	public boolean changeForum(String forumid,String name,String language,String description) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			f.setName(name);
			f.setLanguage(language);
			f.setDescription(description);
			f.save();
		}
		return true;
	}


	public boolean changePostArea(String forumid,String postareaid,String name,String description) {
		Forum f=ForumManager.getForum(forumid);
		if (f!=null) {
			PostArea a=f.getPostArea(postareaid);
			if (a!=null) {
				a.setName(name);
				a.setDescription(description);
				a.save();
			}
		}
		return true;
	}


	public boolean removeForum(String sforumid) {
		try {
			int forumid=Integer.parseInt(sforumid);
			ForumManager.removeForum(forumid);
		} catch (Exception e) {}
		return true;
	}


 	private void addPosterInfo(MMObjectNode node,Poster p) {
		node.setValue("posterid",p.getId());
		node.setValue("account",p.getAccount());
		node.setValue("firstname",p.getFirstName());
		node.setValue("lastname",p.getLastName());
		node.setValue("email",p.getEmail());
		node.setValue("level",p.getLevel());
		node.setValue("location",p.getLocation());
		node.setValue("gender",p.getGender());
		node.setValue("avatar",p.getAvatar());
		node.setValue("accountpostcount",p.getPostCount());
		node.setValue("firstlogin",p.getFirstLogin());
		node.setValue("lastseen",p.getLastSeen());
	}


 	private void addActiveInfo(MMObjectNode node,Poster p) {
		node.setValue("active_id",p.getId());
		node.setValue("active_account",p.getAccount());
		node.setValue("active_firstname",p.getFirstName());
		node.setValue("active_lastname",p.getLastName());
		node.setValue("active_email",p.getEmail());
		node.setValue("active_level",p.getLevel());
		node.setValue("active_location",p.getLocation());
		node.setValue("active_gender",p.getGender());
		node.setValue("active_firstlogin",p.getFirstLogin());
		node.setValue("active_lastseen",p.getLastSeen());
		node.setValue("active_avatar",p.getAvatar());
		node.setValue("active_postcount",p.getPostCount());
	}
}
