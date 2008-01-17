/*
 * Copyright (c) 2003, 2004 Rafael Steil
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
 * Created on 29/11/2004 23:07:10
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.jforum.ActionServletRequest;
import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.entities.User;
import net.jforum.util.I18n;
import net.jforum.util.MD5;
import net.jforum.util.SafeHtml;
import net.jforum.util.image.ImageUtils;
import net.jforum.util.legacy.commons.fileupload.FileItem;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: UserCommon.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class UserCommon
{
	private static final Logger logger = Logger.getLogger(UserCommon.class);

	/**
	 * Updates the user information
	 *
	 * @param userId The user id we are saving
	 * @throws Exception
	 */
	public static List saveUser(int userId) throws Exception
	{
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(userId);

		ActionServletRequest request = JForumExecutionContext.getRequest();
		if (SessionFacade.getUserSession().isAdmin()) {
			String username = request.getParameter("username");

			if (username != null) {
				u.setUsername(username.trim());
			}
		}

		u.setId(userId);
		u.setEmail(SafeHtml.makeSafe(request.getParameter("email")));
		u.setIcq(SafeHtml.makeSafe(request.getParameter("icq")));
		u.setAim(SafeHtml.makeSafe(request.getParameter("aim")));
		u.setMsnm(SafeHtml.makeSafe(request.getParameter("msn")));
		u.setYim(SafeHtml.makeSafe(request.getParameter("yim")));
		u.setFrom(SafeHtml.makeSafe(request.getParameter("location")));
		u.setOccupation(SafeHtml.makeSafe(request.getParameter("occupation")));
		u.setInterests(SafeHtml.makeSafe(request.getParameter("interests")));
		u.setBiography(SafeHtml.makeSafe(request.getParameter("biography")));
		u.setSignature(SafeHtml.makeSafe(request.getParameter("signature")));
		u.setViewEmailEnabled(request.getParameter("viewemail").equals("1"));
		u.setViewOnlineEnabled(request.getParameter("hideonline").equals("0"));
		u.setNotifyPrivateMessagesEnabled(request.getParameter("notifypm").equals("1"));
		u.setNotifyOnMessagesEnabled(request.getParameter("notifyreply").equals("1"));
		u.setAttachSignatureEnabled(request.getParameter("attachsig").equals("1"));
		u.setHtmlEnabled(request.getParameter("allowhtml").equals("1"));
		u.setLang(request.getParameter("language"));

		String website = SafeHtml.makeSafe(request.getParameter("website"));
		if (website != null && !"".equals(website.trim()) && !website.toLowerCase().startsWith("http://")) {
			website = "http://" + website;
		}

		u.setWebSite(website);

		if (request.getParameter("new_password") != null && request.getParameter("new_password").length() > 0) {
			u.setPassword(MD5.crypt(request.getParameter("new_password")));
		}

		if (request.getParameter("avatardel") != null) {
			File f = new File(SystemGlobals.getApplicationPath() + "/images/avatar/"+ u.getAvatar());
			f.delete();

			u.setAvatar(null);
		}

		List warns = new ArrayList();
		if (request.getObjectParameter("avatar") != null) {
			try {
				UserCommon.handleAvatar(u);
			}
			catch (Exception e) {
				UserCommon.logger.warn("Problems while uploading the avatar: " + e);
                                 e.printStackTrace();
				warns.add(I18n.getMessage("User.avatarUploadError"));
			}
		}
		else {
			String avatarUrl = request.getParameter("avatarUrl");
			if (avatarUrl != null && !"".equals(avatarUrl.trim())) {
				if (avatarUrl.toLowerCase().startsWith("http://")) {
					u.setAvatar(avatarUrl);
				}
				else {
					warns.add(I18n.getMessage("User.avatarUrlShouldHaveHttp"));
				}
			}
		}

		um.update(u);

		SessionFacade.getUserSession().setLang(u.getLang());

		return warns;
	}

	/**
	 * @param u
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void handleAvatar(User u) throws Exception
	{
		String fileName = MD5.crypt(Integer.toString(u.getId()));
		FileItem item = (FileItem)JForumExecutionContext.getRequest().getObjectParameter("avatar");
		UploadUtils uploadUtils = new UploadUtils(item);

		// Gets file extension
		String extension = uploadUtils.getExtension().toLowerCase();
		int type = ImageUtils.IMAGE_UNKNOWN;

		if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
			type = ImageUtils.IMAGE_JPEG;
		}
		else if (extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("png")) {
			type = ImageUtils.IMAGE_PNG;
		}

		if (type != ImageUtils.IMAGE_UNKNOWN) {
			String avatarTmpFileName = SystemGlobals.getApplicationPath()
				+ "/images/avatar/" + fileName + "_tmp." + extension;

			// We cannot handle gifs
			if (extension.toLowerCase().equals("gif")) {
				extension = "png";
			}

			String avatarFinalFileName = SystemGlobals.getApplicationPath() +
				"/images/avatar/" + fileName + "." + extension;

			uploadUtils.saveUploadedFile(avatarTmpFileName);

			// OK, time to check and process the avatar size
			int maxWidth = SystemGlobals.getIntValue(ConfigKeys.AVATAR_MAX_WIDTH);
			int maxHeight = SystemGlobals.getIntValue(ConfigKeys.AVATAR_MAX_HEIGHT);

			BufferedImage image = ImageUtils.resizeImage(avatarTmpFileName, type, maxWidth, maxHeight);
			ImageUtils.saveImage(image, avatarFinalFileName, type);

			u.setAvatar(fileName + "." + extension);

			// Delete the temporary file
			new File(avatarTmpFileName).delete();
		}
	}
}
