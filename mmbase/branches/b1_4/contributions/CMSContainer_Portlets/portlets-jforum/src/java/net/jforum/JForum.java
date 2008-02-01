/*
 *  Copyright (c) Rafael Steil
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms,
 *  with or without modification, are permitted provided
 *  that the following conditions are met:
 *
 *  1) Redistributions of source code must retain the above
 *  copyright notice, this list of conditions and the
 *  following  disclaimer.
 *  2)  Redistributions in binary form must reproduce the
 *  above copyright notice, this list of conditions and
 *  the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *  3) Neither the name of "Rafael Steil" nor
 *  the names of its contributors may be used to endorse
 *  or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 *  HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 *  IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 *  This file creation date: Mar 3, 2003 / 11:43:35 AM
 *  The JForum Project
 *  http://www.jforum.net
 */
package net.jforum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jforum.dao.DatabaseWorkarounder;
import net.jforum.exceptions.ExceptionWriter;
import net.jforum.exceptions.ForumStartupException;
import net.jforum.repository.ModulesRepository;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.SecurityRepository;
import net.jforum.repository.SmiliesRepository;
import net.jforum.util.I18n;
import net.jforum.util.MD5;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 *  Front Controller.
 *
 *@author     Rafael Steil
 *@version    $Id: JForum.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class JForum extends JForumBaseServlet {
	private static boolean isDatabaseUp;
	private static Logger logger = Logger.getLogger(JForum.class);


	/**
	 *@param  config                servlet configuration
	 *@exception  ServletException  Description of Exception
	 *@see                          javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		super.startFrontController();

		// Start database
		isDatabaseUp = ForumStartup.startDatabase();

		try {
			Connection conn = DBConnection.getImplementation().getConnection();
			conn.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS));

			// Try to fix some MySQL problems
			DatabaseWorkarounder dw = new DatabaseWorkarounder();
			dw.handleWorkarounds(conn);

			// Continues loading the forum
			JForumExecutionContext ex = JForumExecutionContext.get();
			ex.setConnection(conn);
			JForumExecutionContext.set(ex);

			// Init general forum stuff
			ForumStartup.startForumRepository();
			RankingRepository.loadRanks();
			SmiliesRepository.loadSmilies();
		}
		catch (Exception e) {
			throw new ForumStartupException("Error while starting jforum", e);
		}
		finally {
			JForumExecutionContext.finish();
		}
	}


	/**
	 *@param  req                   Description of Parameter
	 *@param  response              Description of Parameter
	 *@exception  IOException       Description of Exception
	 *@exception  ServletException  Description of Exception
	 *@see                          javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		Writer out = null;
		ActionServletRequest request = null;
		String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
		logger.debug("Call JForum Servlet, service() method.");


      try {
			// Request
			request = new ActionServletRequest(req);

			// Initializes the execution context
			JForumExecutionContext ex = JForumExecutionContext.get();
			ex.setRequest(request);
			ex.setResponse(response);

			if (!isDatabaseUp) {
				ForumStartup.startDatabase();
			}

			JForumExecutionContext.set(ex);

			// Setup stuff
			SimpleHash context = JForumExecutionContext.getTemplateContext();

			ControllerUtils utils = new ControllerUtils();
			utils.refreshSession();

			context.put("logged", SessionFacade.isLogged());

			// Process security data
			SecurityRepository.load(SessionFacade.getUserSession().getUserId());

			request.setJForumContext(new JForumContext(request.getContextPath(),
					SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION),
					request,
					response,
					SessionFacade.getUserSession().isBot()));

			utils.prepareTemplateContext(context, request.getJForumContext());

			String module = request.getModule();

			// Gets the module class name
			String moduleClass = module != null
					? ModulesRepository.getModuleClass(module)
					: null;

			context.put("moduleName", module);
			context.put("action", request.getAction());
			context.put("language", I18n.getUserLanguage());
			context.put("securityHash", MD5.crypt(request.getSession().getId()));
			context.put("session", SessionFacade.getUserSession());
			context.put("request", req);
			context.put("response", response);

			logger.debug("From JForum servlet: Action is: " + request.getAction() + " Module is: " + request.getModule());

			if (moduleClass != null) {
				// Here we go, baby
				Command c = (Command) Class.forName(moduleClass).newInstance();
				Template template = c.process(request, response, context);

				if (JForumExecutionContext.getRedirectTo() == null) {
					String contentType = JForumExecutionContext.getContentType();

				//	if (contentType == null) {
						contentType = "text/html; charset=" + encoding;
				//	}

					response.setContentType(contentType);

					// Binary content are expected to be fully
					// handled in the action, including outputstream
					// manipulation
					if (!JForumExecutionContext.isCustomContent()) {
						out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
						template.process(JForumExecutionContext.getTemplateContext(), out);
						out.flush();
					}
				}
			}
			else {
				// Module not found, send 404 not found response
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			JForumExecutionContext.enableRollback();

			if (e.toString().indexOf("ClientAbortException") == -1) {
				response.setContentType("text/html; charset=" + encoding);
				if (out != null) {
					new ExceptionWriter().handleExceptionData(e, out);
				}
				else {
					new ExceptionWriter().handleExceptionData(e, new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
				}
			}
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
			}
			catch (Exception e) {
			}

			String redirectTo = JForumExecutionContext.getRedirectTo();
			JForumExecutionContext.finish();

			if (redirectTo != null) {
				if (request.getJForumContext().isEncodingDisabled()) {
					response.sendRedirect(redirectTo);
				}
				else {
					response.sendRedirect(response.encodeRedirectURL(redirectTo));
				}
			}
		}
	}


	/**
	 *@see    javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		super.destroy();
		logger.info("Destroying JForum...");

		try {
			DBConnection.getImplementation().realReleaseAllConnections();
			ConfigLoader.stopCacheEngine();
			logger.info("Release all DB connection ...");
		}
		catch (Exception e) {
			logger.error("Error during destroying JForum ...", e);
		}
	}
}
