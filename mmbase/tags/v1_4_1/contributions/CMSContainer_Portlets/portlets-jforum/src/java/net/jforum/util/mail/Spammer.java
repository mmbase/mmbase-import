/*
 * Copyright (c) Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: 03/03/2004 - 20:29:45
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.mail;

import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.jforum.JForumExecutionContext;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * Dispatch emails to the world. TODO: should do some refactoring to send a personalized email to
 * each user.
 * 
 * @author Rafael Steil
 * @version $Id: Spammer.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class Spammer
{
	private static final Logger logger = Logger.getLogger(Spammer.class);

	private static int MESSAGE_HTML = 0;
	private static int MESSAGE_TEXT = 1;

	private Properties mailProps = new Properties();
	private static int messageFormat;
	private static Session session;
	private static String username;
	private static String password;
	private MimeMessage message;
	private String messageText;

	protected Spammer() throws EmailException
	{
		String host = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		
		if (host != null) {
			int colon = host.indexOf(':');

			if (colon > 0) {
				mailProps.put("mail.smtp.host", host.substring(0, colon));
				mailProps.put("mail.smtp.port", String.valueOf(host.substring(colon + 1)));
			}
			else {
				mailProps.put("mail.smtp.host", SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST));
			}
			
			String port = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PORT);
			
			if (port != null) {
				mailProps.put("mail.smtp.port", port);
			}
			
			String localhost = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_LOCALHOST);
			
			if (localhost != null && localhost.trim().length() > 0) {
				mailProps.put("mail.smtp.localhost", localhost);
			}
		}
		
		mailProps.put("mail.mime.address.strict", "false");
		mailProps.put("mail.mime.charset", SystemGlobals.getValue(ConfigKeys.MAIL_CHARSET));
		mailProps.put("mail.smtp.auth", SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_AUTH));

		username = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		password = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);

		messageFormat = SystemGlobals.getValue(ConfigKeys.MAIL_MESSSAGE_FORMAT).equals("html") 
			? MESSAGE_HTML
			: MESSAGE_TEXT;

		session = Session.getDefaultInstance(mailProps);
	}

	public static Session getSession()
	{
		return session;
	}

	public final Message getMesssage()
	{
		return this.message;
	}

	public boolean dispatchMessages() throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_SMTP_AUTH)) {
			if (username != null && !username.equals("") && password != null && !password.equals("")) {
				Transport transport = Spammer.getSession().getTransport("smtp");

				try {
					String host = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
					if (host != null) {
						int colon = host.indexOf(':');
						if (colon > 0) {
							transport.connect(host.substring(0, colon), Integer.parseInt(host.substring(colon + 1)),
									username, password);
						}
						else {
							transport.connect(host, username, password);
						}
					}
				}
				catch (MessagingException e) {
					throw new EmailException("Could not connect to the mail server", e);
				}

				if (transport.isConnected()) {
					Address[] addresses = message.getAllRecipients();
					for (int i = 0; i < addresses.length; i++) {
						// Tricks and tricks
						message.setRecipient(Message.RecipientType.TO, addresses[i]);
						transport.sendMessage(message, new Address[] { addresses[i] });
					}
				}
				
				transport.close();
			}
		}
		else {
			Address[] addresses = message.getAllRecipients();
			for (int i = 0; i < addresses.length; i++) {
				message.setRecipient(Message.RecipientType.TO, addresses[i]);
				Transport.send(message, new Address[] { addresses[i] });
			}
		}

		return true;
	}

	protected final void prepareMessage(List addresses, SimpleHash params, String subject, String messageFile)
			throws EmailException
	{
		this.message = new MimeMessage(session);

		try {
			InternetAddress[] recipients = new InternetAddress[addresses.size()];

			String charset = SystemGlobals.getValue(ConfigKeys.MAIL_CHARSET);

			this.message.setSentDate(new Date());
			this.message.setFrom(new InternetAddress(SystemGlobals.getValue(ConfigKeys.MAIL_SENDER)));
			this.message.setSubject(subject, charset);

			this.messageText = this.getMessageText(params, messageFile);

			if (messageFormat == MESSAGE_HTML) {
				this.message.setContent(this.messageText, "text/html; charset=" + charset);
			}
			else {
				this.message.setText(this.messageText, charset);
			}

			int i = 0;
			for (Iterator iter = addresses.iterator(); iter.hasNext(); i++) {
				recipients[i] = new InternetAddress((String) iter.next());
			}

			this.message.setRecipients(Message.RecipientType.TO, recipients);
		}
		catch (Exception e) {
			logger.warn(e);
			throw new EmailException(e);
		}
	}
	
	/**
	 * Gets the message text to send in the email.
	 * 
	 * @param params The optional params. If no need of any, just pass null
	 * @param messageFile The optional message file to load the text. 
	 * @return The email message text
	 * @throws Exception
	 */
	protected String getMessageText(SimpleHash params, String messageFile) throws Exception
	{
		String templateEncoding = SystemGlobals.getValue(ConfigKeys.MAIL_TEMPLATE_ENCODING);
		
		StringWriter sWriter = new StringWriter();
		
		Template template = null;
		
		if (templateEncoding == null || "".equals(templateEncoding.trim())) {
			template = JForumExecutionContext.templateConfig().getTemplate(messageFile);
		}
		else {
			template = JForumExecutionContext.templateConfig().getTemplate(messageFile, templateEncoding);
		}
		
		template.process(params, sWriter);
		
		return sWriter.toString();
	}

	/**
	 * Gets the email body
	 * 
	 * @return String with the email body that will be sent to the user
	 */
	public String getMessageBody()
	{
		return this.messageText;
	}
}
