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
 * This file creation date: Apr 5, 2003 / 11:43:46 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.Group;
import net.jforum.entities.KarmaStatus;
import net.jforum.entities.User;
import net.jforum.sso.LoginAuthenticator;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericUserDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericUserDAO extends AutoKeys implements net.jforum.dao.UserDAO
{
	private static LoginAuthenticator loginAuthenticator;

	public GenericUserDAO()
	{
		String className = SystemGlobals.getValue(ConfigKeys.LOGIN_AUTHENTICATOR);

		try {
			loginAuthenticator = (LoginAuthenticator)Class.forName(className).newInstance();
			loginAuthenticator.setUserModel(this);
		}
		catch (Exception e) {
			throw new RuntimeException("Error while trying to instantiate a "
					+ "login.authenticator instance (" + className + "): " + e);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectById(int)
	 */
	public User selectById(int userId) throws Exception
	{
	    String q = SystemGlobals.getSql("UserModel.selectById");

		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(q);
		p.setInt(1, userId);

		ResultSet rs = p.executeQuery();
		User u = new User();

		if (rs.next()) {
			this.fillUserFromResultSet(u, rs);
			u.setPrivateMessagesCount(rs.getInt("private_messages"));

			rs.close();
			p.close();

			// User groups
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectGroups"));
			p.setInt(1, userId);

			rs = p.executeQuery();
			while (rs.next()) {
				Group g = new Group();
				g.setName(rs.getString("group_name"));
				g.setId(rs.getInt("group_id"));

				u.getGroupsList().add(g);
			}
		}

		rs.close();
		p.close();

		return u;
	}

	public User selectByName(String username) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectByName"));
		p.setString(1, username);

		ResultSet rs = p.executeQuery();
		User u = null;

		if (rs.next()) {
			u = new User();
			fillUserFromResultSet(u, rs);
		}

		rs.close();
		p.close();

		return u;
	}

	protected void fillUserFromResultSet(User u, ResultSet rs) throws Exception
	{
		u.setAim(rs.getString("user_aim"));
		u.setAvatar(rs.getString("user_avatar"));
		u.setGender(rs.getString("gender"));
		u.setRankId(rs.getInt("rank_id"));
		u.setThemeId(rs.getInt("themes_id"));
		u.setPrivateMessagesEnabled(rs.getInt("user_allow_pm") == 1);
		u.setNotifyOnMessagesEnabled(rs.getInt("user_notify") == 1);
		u.setViewOnlineEnabled(rs.getInt("user_viewonline") == 1);
		u.setPassword(rs.getString("user_password"));
		u.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);
		u.setViewOnlineEnabled(rs.getInt("user_allow_viewonline") == 1);
		u.setAvatarEnabled(rs.getInt("user_allowavatar") == 1);
		u.setBbCodeEnabled(rs.getInt("user_allowbbcode") == 1);
		u.setHtmlEnabled(rs.getInt("user_allowhtml") == 1);
		u.setSmiliesEnabled(rs.getInt("user_allowsmilies") == 1);
		u.setEmail(rs.getString("user_email"));
		u.setFrom(rs.getString("user_from"));
		u.setIcq(rs.getString("user_icq"));
		u.setId(rs.getInt("user_id"));
		u.setInterests(rs.getString("user_interests"));
		u.setBiography(rs.getString("user_biography"));
		u.setLastVisit(rs.getTimestamp("user_lastvisit"));
		u.setOccupation(rs.getString("user_occ"));
		u.setTotalPosts(rs.getInt("user_posts"));
		u.setRegistrationDate(rs.getTimestamp("user_regdate"));
		u.setSignature(rs.getString("user_sig"));
		u.setWebSite(rs.getString("user_website"));
		u.setYim(rs.getString("user_yim"));
		u.setUsername(rs.getString("username"));
		u.setAttachSignatureEnabled(rs.getInt("user_attachsig") == 1);
		u.setMsnm(rs.getString("user_msnm"));
		u.setLang(rs.getString("user_lang"));
		u.setActive(rs.getInt("user_active"));
		u.setKarma(new KarmaStatus(u.getId(), rs.getDouble("user_karma")));
		u.setNotifyPrivateMessagesEnabled(rs.getInt("user_notify_pm") == 1);
		u.setDeleted(rs.getInt("deleted"));

		String actkey = rs.getString("user_actkey");
		u.setActivationKey(actkey == null || "".equals(actkey) ? null : actkey);
	}

	/**
	 * @see net.jforum.dao.UserDAO#delete(int)
	 */
	public void delete(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
		p.setInt(1, 1);
		p.setInt(2, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#update(net.jforum.User)
	 */
	public void update(User user) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.update"));

		p.setString(1, user.getAim());
		p.setString(2, user.getAvatar());
		p.setString(3, user.getGender());
		p.setInt(4, user.getThemeId());
		p.setInt(5, user.isPrivateMessagesEnabled() ? 1 : 0);
		p.setInt(6, user.isAvatarEnabled() ? 1 : 0);
		p.setInt(7, user.isBbCodeEnabled() ? 1 : 0);
		p.setInt(8, user.isHtmlEnabled() ? 1 : 0);
		p.setInt(9, user.isSmiliesEnabled() ? 1 : 0);
		p.setString(10, user.getEmail());
		p.setString(11, user.getFrom());
		p.setString(12, user.getIcq());
		p.setString(13, user.getInterests());
		p.setString(14, user.getOccupation());
		p.setString(15, user.getSignature());
		p.setString(16, user.getWebSite());
		p.setString(17, user.getYim());
		p.setString(18, user.getMsnm());
		p.setString(19, user.getPassword());
		p.setInt(20, user.isViewEmailEnabled() ? 1 : 0);
		p.setInt(21, user.isViewOnlineEnabled() ? 1 : 0);
		p.setInt(22, user.isNotifyOnMessagesEnabled() ? 1 : 0);
		p.setInt(23, user.getAttachSignatureEnabled() ? 1 : 0);
		p.setString(24, user.getUsername());
		p.setString(25, user.getLang());
		p.setInt(26, user.isNotifyPrivateMessagesEnabled() ? 1 : 0);
		p.setString(27, user.getBiography());
		p.setInt(28, user.getId());

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#addNew(net.jforum.User)
	 */
	public int addNew(User user) throws Exception
	{
		PreparedStatement p = this.getStatementForAutoKeys("UserModel.addNew");

		this.initNewUser(user, p);

		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("UserModel.lastGeneratedUserId"));
		int id = this.executeAutoKeysQuery(p);
		p.close();

		this.addToGroup(id, new int[] { SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP) });

		user.setId(id);
		return id;
	}

	protected void initNewUser(User user, PreparedStatement p) throws Exception
	{
		p.setString(1, user.getUsername());
		p.setString(2, user.getPassword());
		p.setString(3, user.getEmail());
		p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		p.setString(5, user.getActivationKey());
	}

	/**
	 * @see net.jforum.dao.UserDAO#addNewWithId(net.jforum.User)
	 */
	public void addNewWithId(User user) throws Exception
	{
		PreparedStatement p = this.getStatementForAutoKeys("UserModel.addNewWithId");

		this.initNewUser(user, p);
		p.setInt(6, user.getId());

		p.executeUpdate();
		p.close();

		this.addToGroup(user.getId(), new int[] { SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP) });
	}

	/**
	 * @see net.jforum.dao.UserDAO#decrementPosts(int)
	 */
	public void decrementPosts(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.decrementPosts"));
		p.setInt(1, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#incrementPosts(int)
	 */
	public void incrementPosts(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.incrementPosts"));
		p.setInt(1, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#incrementRanking(int)
	 */
	public void setRanking(int userId, int rankingId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.rankingId"));
		p.setInt(1, rankingId);
		p.setInt(2, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#setActive(int, boolean)
	 */
	public void setActive(int userId, boolean active) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.activeStatus"));
		p.setInt(1, active ? 1 : 0);
		p.setInt(2, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#undelete(int)
	 */
	public void undelete(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
		p.setInt(1, 0);
		p.setInt(2, userId);

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAll()
	 */
	public List selectAll() throws Exception
	{
		return selectAll(0, 0);
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAll(int, int)
	 */
	public List selectAll(int startFrom, int count) throws Exception
	{
		PreparedStatement p;

		if (count > 0) {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllByLimit"));
			p.setInt(1, startFrom);
			p.setInt(2, count);
		}
		else {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAll"));
		}

		ResultSet rs = p.executeQuery();
		List list = this.processSelectAll(rs);
		rs.close();
		p.close();

		return list;
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllWithKarma()
	 */
	public List selectAllWithKarma() throws Exception
	{
		return this.selectAllWithKarma(0, 0);
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllWithKarma(int, int)
	 */
	public List selectAllWithKarma(int startFrom, int count) throws Exception
	{
	    return this.loadKarma( this.selectAll(startFrom, count) );
	}

	protected List processSelectAll(ResultSet rs) throws Exception
	{
		List list = new ArrayList();

		while (rs.next()) {
			User u = new User();

			u.setEmail(rs.getString("user_email"));
			u.setId(rs.getInt("user_id"));
			u.setTotalPosts(rs.getInt("user_posts"));
			u.setRegistrationDate(rs.getTimestamp("user_regdate"));
			u.setUsername(rs.getString("username"));
			u.setDeleted(rs.getInt("deleted"));
			KarmaStatus karma = new KarmaStatus();
			karma.setKarmaPoints(rs.getInt("user_karma"));
			u.setKarma( karma );
			u.setFrom(rs.getString("user_from"));
			u.setWebSite(rs.getString("user_website"));
			u.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);

			list.add(u);
		}

		return list;
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllByGroup(int, int, int)
	 */
	public List selectAllByGroup(int groupId, int start, int count) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllByGroup"));
		p.setInt(1, groupId);
		p.setInt(2, start);
		p.setInt(3, count);

		ResultSet rs = p.executeQuery();
		List l = this.processSelectAll(rs);
		rs.close();

		return l;
	}

	/**
	 * @see net.jforum.dao.UserDAO#getLastUserInfo()
	 */
	public User getLastUserInfo() throws Exception
	{
		User u = new User();

		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.lastUserRegistered"));
		ResultSet rs = p.executeQuery();
		rs.next();

		u.setUsername(rs.getString("username"));
		u.setId(rs.getInt("user_id"));

		rs.close();
		p.close();

		return u;
	}

	/**
	 * @see net.jforum.dao.UserDAO#getTotalUsers()
	 */
	public int getTotalUsers() throws Exception
	{
		return this.getTotalUsersCommon(JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("UserModel.totalUsers")));
	}

	/**
	 * @see net.jforum.dao.UserDAO#getTotalUsersByGroup(int)
	 */
	public int getTotalUsersByGroup(int groupId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("UserModel.totalUsersByGroup"));
		p.setInt(1, groupId);

		return this.getTotalUsersCommon(p);
	}

	protected int getTotalUsersCommon(PreparedStatement p) throws Exception
	{
		ResultSet rs = p.executeQuery();
		rs.next();

		int total = rs.getInt(1);

		rs.close();
		p.close();

		return total;
	}

	/**
	 * @see net.jforum.dao.UserDAO#isDeleted(int user_id)
	 */
	public boolean isDeleted(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isDeleted"));
		p.setInt(1, userId);

		int deleted = 0;

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			deleted = rs.getInt("deleted");
		}

		rs.close();
		p.close();

		return deleted == 1;
	}

	/**
	 * @see net.jforum.dao.UserDAO#isUsernameRegistered(java.lang.String)
	 */
	public boolean isUsernameRegistered(String username) throws Exception
	{
		boolean status = false;

		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isUsernameRegistered"));
		p.setString(1, username);

		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("registered") > 0) {
			status = true;
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * @see net.jforum.dao.UserDAO#validateLogin(java.lang.String, java.lang.String)
	 */
	public User validateLogin(String username, String password) throws NoSuchAlgorithmException, Exception
	{
		return loginAuthenticator.validateLogin(username, password, null);
	}

	/**
	 * @see net.jforum.dao.UserDAO#addToGroup(int, int[])
	 */
	public void addToGroup(int userId, int[] groupId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.addToGroup"));
		p.setInt(1, userId);

		for (int i = 0; i < groupId.length; i++) {
			p.setInt(2, groupId[i]);
			p.executeUpdate();
		}

		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#removeFromGroup(int, int[])
	 */
	public void removeFromGroup(int userId, int[] groupId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.removeFromGroup"));
		p.setInt(1, userId);

		for (int i = 0; i < groupId.length; i++) {
			p.setInt(2, groupId[i]);
			p.executeUpdate();
		}

		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#saveNewPassword(java.lang.String, java.lang.String)
	 */
	public void saveNewPassword(String password, String email) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.saveNewPassword"));
		p.setString(1, password);
		p.setString(2, email);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#validateLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public boolean validateLostPasswordHash(String email, String hash) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.validateLostPasswordHash"));
		p.setString(1, hash);
		p.setString(2, email);

		boolean status = false;

		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("valid") == 1) {
			status = true;

			this.writeLostPasswordHash(email, "");
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * @see net.jforum.dao.UserDAO#writeLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public void writeLostPasswordHash(String email, String hash) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.writeLostPasswordHash"));
		p.setString(1, hash);
		p.setString(2, email);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#getUsernameByEmail(java.lang.String)
	 */
	public String getUsernameByEmail(String email) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getUsernameByEmail"));
		p.setString(1, email);

		String username = "";

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			username = rs.getString("username");
		}

		rs.close();
		p.close();

		return username;
	}

	/**
	 * @see net.jforum.dao.UserDAO#findByName(java.lang.String, boolean)
	 */
	public List findByName(String input, boolean exactMatch) throws Exception
	{
		List namesList = new ArrayList();

		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.findByName"));
		p.setString(1, exactMatch ? input : "%" + input + "%");

		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			User u = new User();

			u.setId(rs.getInt("user_id"));
			u.setUsername(rs.getString("username"));
			u.setEmail(rs.getString("user_email"));
			u.setDeleted(rs.getInt("deleted"));

			namesList.add(u);
		}

		rs.close();
		p.close();

		return namesList;
	}

	/**
	 * @see net.jforum.dao.UserDAO#validateActivationKeyHash(int, java.lang.String)
	 */
	public boolean validateActivationKeyHash(int userId , String hash) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.validateActivationKeyHash"));
		p.setString(1, hash);
		p.setInt(2, userId);

		boolean status = false;

		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("valid") == 1) {
			status = true;
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * @see net.jforum.dao.UserDAO#writeUserActive(int)
	 */
	public void writeUserActive(int userId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.writeUserActive"));
		p.setInt(1, userId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#updateUsername(int, String)
	 */
	public void updateUsername(int userId, String username) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.updateUsername"));
		p.setString(1, username);
		p.setInt(2, userId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.UserDAO#hasUsernameChanged(int, java.lang.String)
	 */
	public boolean hasUsernameChanged(int userId, String usernameToCheck) throws Exception
	{
		boolean status = false;

		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getUsername"));
		p.setString(1, usernameToCheck);
		p.setInt(2, userId);

		String dbUsername = null;

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			dbUsername = rs.getString("username");
		}

		if (!usernameToCheck.equals(dbUsername)) {
			status = true;
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * Load KarmaStatus from a list of users.
	 * @param users
	 * @return
	 * @throws Exception
	 */
	protected List loadKarma(List users) throws Exception{
	    List result = new ArrayList(users.size());

	    User user = null;
		Iterator iter = users.iterator();
		while (iter.hasNext()) {
		    user = (User) iter.next();
		    //load Karma
		    DataAccessDriver.getInstance().newKarmaDAO().getUserTotalKarma(user);
		    result.add(user);
        }
		return result;
	}
}
