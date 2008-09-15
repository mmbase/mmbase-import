package com.finalist.portlets.tagcloud.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

import com.finalist.portlets.tagcloud.Tag;

public class TagCloudUtil {

	private static final Logger log = Logging
			.getLoggerInstance(TagCloudUtil.class.getName());

	public static final String ORDERBY_COUNT = "count";

	public static final String ORDERBY_NAME = "name";

	private static final String SQL_SELECT_TAGS = "SELECT tag.name,tag.description,COUNT(contentelement.number) AS cnt "
			+ "FROM mm_tag tag,mm_insrel insrel,mm_contentelement contentelement "
			+ "WHERE (tag.number=insrel.dnumber AND contentelement.number=insrel.snumber) "
			+ "GROUP BY tag.name,tag.description ORDER BY cnt DESC";

	private static final String SQL_SELECT_CONTENT_RELATED_TAGS = "SELECT tag.name,tag.description "
			+ "FROM mm_tag tag,mm_insrel insrel "
			+ "WHERE (tag.number=insrel.dnumber AND CONTENTELEMENT_NUMBER=insrel.snumber) "
			+ "ORDER BY tag.name";

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
	public static List<Tag> getTags(Integer max, String orderby) {

		List<Tag> tags = new ArrayList<Tag>();
		try {
			Connection con = getConnection();
			Statement st = con.createStatement();
			if (max != null) {
				st.setMaxRows(max);
			}
			String sql = SQL_SELECT_TAGS;
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				tags.add(new Tag(rs.getString("tag.name"), rs
						.getString("tag.description"), rs.getInt("cnt")));
			}

			if (ORDERBY_NAME.equals(orderby)) {
				Collections.sort(tags, new TagNameComperator());
			}
		} catch (SQLException e) {
			log.error("Failed to execute " + SQL_SELECT_TAGS, e);
		}
		return tags;
	}

	@SuppressWarnings("unchecked")
	public static List<Tag> getRelatedTags(Integer related) {

		List<Tag> tags = new ArrayList<Tag>();
		try {
			Connection con = getConnection();
			Statement st = con.createStatement();
			String sql = SQL_SELECT_CONTENT_RELATED_TAGS.replaceAll(
					"CONTENTELEMENT_NUMBER", "" + related);
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				tags.add(new Tag(rs.getString("tag.name"), rs
						.getString("tag.description"), 1));
			}
		} catch (SQLException e) {
			log.error("Failed to execute " + SQL_SELECT_TAGS, e);
		}
		return tags;
	}
}
