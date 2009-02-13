package com.finalist.portlets.tagcloud.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.mmbase.module.core.MMBase;
import org.mmbase.storage.StorageManagerFactory;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.util.ServerUtil;
import com.finalist.portlets.tagcloud.Tag;

public class TagCloudUtil {

	private static final Logger log = Logging
			.getLoggerInstance(TagCloudUtil.class.getName());

	public static final String ORDERBY_COUNT = "count";

	public static final String ORDERBY_NAME = "name";

	private static final String SQL_STAGING_SELECT_TAGS = "SELECT tag.number,tag.name,tag.description,tag.number FROM mm_tag tag";
	private static final String SQL_LIVE_SELECT_TAGS = "SELECT tag.number,tag.name,tag.description,tag.number FROM live_tag tag";
/*

 */
	private static final String SQL_STAGING_SELECT_TAGS_COUNT = "  SELECT tag.number,COUNT(insrel.number) AS cnt " 
		+ "FROM mm_tag tag,mm_insrel insrel "
		+ "WHERE (tag.number=insrel.dnumber) "
		+ "GROUP BY tag.number";
	private static final String SQL_LIVE_SELECT_TAGS_COUNT = "SELECT tag.number,COUNT(contentelement.number) AS cnt "
		+ "FROM live_tag tag,live_insrel insrel,live_contentelement contentelement "
		+ "WHERE (tag.number=insrel.dnumber AND contentelement.number=insrel.snumber) "
		+ "GROUP BY tag.name";

	private static final String SQL_STAGING_SELECT_CONTENT_RELATED_TAGS = "SELECT tag.number,tag.name,tag.description "
		+ "FROM mm_tag tag,mm_insrel insrel "
		+ "WHERE (tag.number=insrel.dnumber AND CONTENTELEMENT_NUMBER=insrel.snumber) "
		+ "ORDER BY tag.name";
	private static final String SQL_LIVE_SELECT_CONTENT_RELATED_TAGS = "SELECT tag.number,tag.name,tag.description "
		+ "FROM live_tag tag,live_insrel insrel "
		+ "WHERE (tag.number=insrel.dnumber AND CONTENTELEMENT_NUMBER=insrel.snumber) "
		+ "ORDER BY tag.name";

	private static final String SQL_STAGING_SELECT_CHANNEL_RELATED_TAGS = "	 SELECT tag.number, tag.name,tag.description,COUNT(contentelement.number) AS cnt "+ 
		"FROM mm_tag tag,mm_insrel insrel,mm_contentelement contentelement, mm_contentrel contentrel "+ 
		"WHERE tag.number=insrel.dnumber AND contentelement.number=insrel.snumber AND contentelement.number = contentrel.dnumber AND contentrel.snumber = CONTENTCHANNEL_NUMBER "+  
		"GROUP BY tag.name,tag.description";

	private static final String SQL_LIVE_SELECT_CHANNEL_RELATED_TAGS = "	 SELECT tag.number, tag.name,tag.description,COUNT(contentelement.number) AS cnt "+ 
		"FROM live_tag tag,live_insrel insrel,live_contentelement contentelement, live_contentrel contentrel "+ 
		"WHERE tag.number=insrel.dnumber AND contentelement.number=insrel.snumber AND contentelement.number = contentrel.dnumber AND contentrel.snumber = CONTENTCHANNEL_NUMBER "+  
		"GROUP BY tag.name,tag.description";

	
	private static final String SQL_STAGING_SELECT_TAG_RELATED_TAGS = "SELECT target_tag.number,target_tag.name,target_tag.description,COUNT(contentelement.number) AS cnt "+  
		"FROM mm_tag source_tag,mm_insrel source_insrel,mm_contentelement contentelement, mm_insrel target_insrel, mm_tag target_tag   "+
		"WHERE source_tag.number=source_insrel.dnumber AND contentelement.number=source_insrel.snumber  "+
		"AND target_tag.number=target_insrel.dnumber AND contentelement.number=target_insrel.snumber "+
		"AND LOWER(source_tag.name) = 'TAG_NAME'  "+
		"GROUP BY target_tag.name,target_tag.description ";
 
	private static final String SQL_LIVE_SELECT_TAG_RELATED_TAGS = "SELECT target_tag.number,target_tag.name,target_tag.description,COUNT(contentelement.number) AS cnt "+  
		"FROM live_tag source_tag,live_insrel source_insrel,live_contentelement contentelement, live_insrel target_insrel, live_tag target_tag   "+
		"WHERE source_tag.number=source_insrel.dnumber AND contentelement.number=source_insrel.snumber  "+
		"AND target_tag.number=target_insrel.dnumber AND contentelement.number=target_insrel.snumber "+
		"AND LOWER(source_tag.name) = 'TAG_NAME'  "+
		"GROUP BY target_tag.name,target_tag.description ";
 
	private static Connection getConnection() {
		Connection connection = null;
		MMBase mmbase = MMBase.getMMBase();
		StorageManagerFactory sf = mmbase.getStorageManagerFactory();
		if (sf instanceof DatabaseStorageManagerFactory) {
			try {
				DatabaseStorageManagerFactory df = (DatabaseStorageManagerFactory) sf;
				connection = df.getDataSource().getConnection();
			} catch (SQLException e) {
				log.debug("Failed to get connection " + e.getMessage(), e);
			}
		} else {
			String dataSourceURI = mmbase.getInitParameter("datasource");
			if (dataSourceURI != null) {
				try {
					String contextName = mmbase
							.getInitParameter("datasource-context");
					if (contextName == null) {
						contextName = "java:comp/env";
					}
					log.service("Using configured datasource " + dataSourceURI);
					Context initialContext = new InitialContext();
					Context environmentContext = (Context) initialContext
							.lookup(contextName);
					DataSource ds = (DataSource) environmentContext
							.lookup(dataSourceURI);
					if (ds == null) {
						connection = ds.getConnection();
					}
				} catch (NamingException ne) {
					log
							.warn("Datasource '"
									+ dataSourceURI
									+ "' not available. ("
									+ ne.getMessage()
									+ "). Attempt to use JDBC Module to access database.");
				} catch (SQLException e) {
					log.warn("Failed to get connection " + e.getMessage(), e);
				}
			}
		}
		if (connection == null) {
			throw new IllegalStateException(
					"connection not set. Write code to get it from MMBase datasource!");
		}
		return connection;
	}

	@SuppressWarnings("unchecked")
	public static List<Tag> getTags(Integer max, String orderby, String direction) {

		List<Tag> tags = new ArrayList<Tag>();
		Connection con = getConnection();
		try {
			Statement st = con.createStatement();
			if (max != null) {
				st.setMaxRows(max);
			}
			String sqlSelect = ServerUtil.isLive()?SQL_LIVE_SELECT_TAGS:SQL_STAGING_SELECT_TAGS;
			ResultSet rs = st.executeQuery(sqlSelect);
			while (rs.next()) {
				Tag tag = new Tag(rs.getInt("tag.number"), rs.getString("tag.name"), rs.getString("tag.description"), 0);
				tags.add(tag);
			}

			String sqlSelectCount = ServerUtil.isLive()?SQL_LIVE_SELECT_TAGS_COUNT:SQL_STAGING_SELECT_TAGS_COUNT;
			ResultSet rsCount = st.executeQuery(sqlSelectCount);
			while (rsCount.next()) {
				
				int number = rsCount.getInt("tag.number");
				int count = rsCount.getInt("cnt");

				for(Tag tag:tags) {
					if(tag.getNumber() == number) {
						tag.setCount(tag.getCount()+count);
					}
				}
			}

			Collections.sort(tags, new TagNameComperator(orderby, direction));
		} catch (SQLException e) {
			log.error("Failed to execute", e);
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				log.warn("Failed to get connection " + e.getMessage(), e);
			}
		}
		return tags;
	}

	@SuppressWarnings("unchecked")
	public static List<Tag> getRelatedTags(Integer related) {

		List<Tag> tags = new ArrayList<Tag>();
		Connection con = getConnection();
		try {
			Statement st = con.createStatement();
			String sql = ServerUtil.isLive()?SQL_LIVE_SELECT_CONTENT_RELATED_TAGS:SQL_STAGING_SELECT_CONTENT_RELATED_TAGS;
			sql = sql.replaceAll("CONTENTELEMENT_NUMBER", "" + related);
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				tags.add(new Tag(rs.getInt("tag.number"), rs.getString("tag.name"), rs.getString("tag.description"), 1));
			}
		} catch (SQLException e) {
			log.error("Failed to execute", e);
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				log.warn("Failed to get connection " + e.getMessage(), e);
			}
		}
		return tags;
	}

	public static List<Tag> getChannelRelatedTags(Integer channel) {

		List<Tag> tags = new ArrayList<Tag>();
		Connection con = getConnection();
		try {
			Statement st = con.createStatement();
			String sql = ServerUtil.isLive()?SQL_LIVE_SELECT_CHANNEL_RELATED_TAGS:SQL_STAGING_SELECT_CHANNEL_RELATED_TAGS;
			sql = sql.replaceAll("CONTENTCHANNEL_NUMBER", "" + channel);
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				Tag tag = new Tag(rs.getInt("tag.number"), rs.getString("tag.name"), rs.getString("tag.description"), rs.getInt("cnt"));
				tags.add(tag);
			}
		} catch (SQLException e) {
			log.error("Failed to execute", e);
		}
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				log.warn("Failed to get connection " + e.getMessage(), e);
			}
		}
		return tags;
	}

	public static List<Tag> getTagRelatedTags(String tag) {

		if(tag != null) {
			List<Tag> tags = new ArrayList<Tag>();
			Connection con = getConnection();
			try {
				Statement st = con.createStatement();
				String sql = ServerUtil.isLive()?SQL_LIVE_SELECT_TAG_RELATED_TAGS:SQL_STAGING_SELECT_TAG_RELATED_TAGS;
				sql = sql.replaceAll("TAG_NAME", tag.toLowerCase());
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					String name=rs.getString("target_tag.name");
					if(!name.equalsIgnoreCase(tag)) {
						tags.add(new Tag(rs.getInt("target_tag.number"),  name, rs.getString("target_tag.description"), rs.getInt("cnt")));
					}
				}
			} catch (SQLException e) {
				log.error("Failed to execute", e);
			}
			finally {
				try {
					con.close();
				} catch (SQLException e) {
					log.warn("Failed to get connection " + e.getMessage(), e);
				}
			}
			return tags;
		}
		return null;
	}
}
