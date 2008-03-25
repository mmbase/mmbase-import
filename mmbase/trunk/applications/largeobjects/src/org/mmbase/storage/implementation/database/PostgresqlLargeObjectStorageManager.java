/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the
 * Open Source Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 * 
 */
package org.mmbase.storage.implementation.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.database.ConnectionWrapper;
import org.mmbase.storage.StorageException;
import org.mmbase.storage.util.Scheme;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

/**
 * This class is responsible for handling binary objects using the PostgreSQL
 * LargeObject API.
 * 
 * @author E-J. van der Laan, Finalist IT Group
 */
public class PostgresqlLargeObjectStorageManager extends
		RelationalDatabaseStorageManager {

	private static final long NULL_OID = 4294967295L; // int -1

	private static final Logger log = Logging
			.getLoggerInstance(PostgresqlLargeObjectStorageManager.class);

	/**
	 * Exists because {@link DatabaseStorageManager}.BLOB_SHORTED is private!
	 */
	private static final Blob BLOB_SHORTED = new InputStreamBlob(null, -1);

	public PostgresqlLargeObjectStorageManager() {
		super();
	}

	/**
	 * Deletes all references LargeObjects and calls the overridden method in
	 * its super class afterwards. This method overrides its super class delete
	 * 
	 * @see org.mmbase.storage.implementation.database.DatabaseStorageManager#delete(MMObjectNode, MMObjectBuilder, List, String)
	 */
	protected void delete(MMObjectNode node, MMObjectBuilder builder,
			List<CoreField> blobFileField, String tablename) {

		deleteLargeObjects(node, builder, blobFileField);

		super.delete(node, builder, blobFileField, tablename);
	}

	/**
	 * Delete all binary objects stored as large object. This excludes the
	 * binary fields stored as file. See
	 * {@link org.mmbase.storage.implementation.database.DatabaseStorageManager#delete(MMObjectNode, MMObjectBuilder, List, String)}
	 * for the arguments.
	 * 
	 * @param node
	 * @param builder
	 * @param blobFileField
	 */
	protected void deleteLargeObjects(MMObjectNode node,
			MMObjectBuilder builder, List<CoreField> blobFileField) {
		// delete all binary fields stored as OID
		//
		List<CoreField> builderFields = builder.getFields(NodeManager.ORDER_CREATE);
		for (CoreField field : builderFields) {
			if (field.inStorage() && field.getType() == Field.TYPE_BINARY
					&& !blobFileField.contains(field)) {
				// TODO: implement logic to differentiate between OID and BYTEA
				// see DatabaseStorageManager.delete(MMObjectNode node,
				// MMObjectBuilder builder) for BYTEA.
				//
				deleteLargeObject(node, field);
			}
		}
	}

	/**
	 * Get the active connection. This connection has auto-commit set to false
	 * always. This is required by the PostgreSQL LargeObject API.
	 */
	protected Connection getActiveConnection() throws SQLException {
		Connection result = super.getActiveConnection();

		if (result.getAutoCommit()) {
			result.setAutoCommit(false);
		}

		return result;
	}

	/**
	 * Replaces it super class method and calls
	 * {@link #getBlobValueFromLargeObject(ResultSet, int, CoreField, boolean)}
	 * instead.
	 */
	protected Blob getBlobValue(ResultSet result, int index, CoreField field,
			boolean mayShorten) throws StorageException, SQLException {
		// TODO: implement logic to determine when to use LO or BYTEA based
		// on column size.
		//
		return getBlobValueFromLargeObject(result, index, field, mayShorten);
	}

	/**
	 * Retrieves a Blob from the database using the PostgreSQL LargeObject API.
	 * 
	 * @param result
	 *            ResultSet holding the current record to read.
	 * @param index
	 *            index of the blob field in the record set.
	 * @param field
	 *            Field data for the blob.
	 * @param mayShorten
	 *            if a Blob place holder must be returned instead of the real
	 *            blob.
	 * @return null, SHORTED_BLOB, or a InputStreamBlob containing the
	 *         inputstream returned by the LargeObject API.
	 * @throws StorageException
	 *             when no LargeObject can be found when it should exist.
	 * @throws SQLException
	 *             can be thrown when acquiring the LargeObjectManager fails.
	 * 
	 */
	protected Blob getBlobValueFromLargeObject(ResultSet result, int index,
			CoreField field, boolean mayShorten) throws StorageException,
			SQLException {
		final String logMethodName = "getBlobValueFromLargeObject(...): ";

		final long oid = result.getLong(index);
		log.debug(logMethodName + "oid=" + oid);
		if (isOidNull(oid)) {
			log.debug(logMethodName + "return null");
			return null;
		}

		if (mayShorten && shorten(field)) {
			log.debug(logMethodName + "return BLOB_SHORTED");
			return BLOB_SHORTED;
		}

		final LargeObjectManager largeObjectManager = getLargeObjectManager();
		final LargeObject obj = largeObjectManager.open(oid,
				LargeObjectManager.READ);
		if (obj == null) {
			throw new StorageException("LargeObject not found with oid=" + oid);
		}

		log.debug(logMethodName + "return obj.getInputStream()");
		return new InputStreamBlob(obj.getInputStream());
	}

	/**
	 * Returns Field.TYPE_BINARY independent of the jdbcType. In all other case
	 * it returns the result of the super class method. This is required because
	 * PostgreSQL returns jdbcType.INTEGER for OID fields.
	 */
	protected int getJDBCtoField(int jdbcType, int mmbaseType) {
		if (mmbaseType == Field.TYPE_BINARY /* && jdbcType == Types.INTEGER */) {
			// XXX: PostgreSQL returns jdbcType.INTEGER for OID fields.
			//
			log.debug("mmbaseTYpe == Field.TYPE_BINARY, jdbcType=" + jdbcType);
			return Field.TYPE_BINARY;
		}
		return super.getJDBCtoField(jdbcType, mmbaseType);
	}

	/**
	 * Writes the binary objectValue to to a LargeObject and stores the
	 * resulting oid as long in the statement. If the node already existed the
	 * existing LargeObject is deleted before creating a new one.
	 */
	protected void setBinaryValue(PreparedStatement statement, int index,
			Object objectValue, CoreField field, MMObjectNode node)
			throws StorageException, SQLException {
		// TODO: implement logic to determine when to use LO or BYTEA based
		// on column size. Code below works for LO. Call super for BYTEA.
		//

		if (log.isDebugEnabled()) {
			log.debug("Setting inputstream bytes into oid field " + field
					+ ", objectValue=" + objectValue);
		}

		// delete a possible existing LO
		//
		if (!node.isNew()) {
			deleteLargeObject(node, field);
		}

		if (objectValue == null) {
			statement.setNull(index, Types.INTEGER);
		} else {
			log.debug("Didn't set null");
			InputStream stream = Casting.toInputStream(objectValue);
			long size = -1;
			if (objectValue instanceof byte[]) {
				size = ((byte[]) objectValue).length;
			} else {
				size = node.getSize(field.getName());
			}
			log.debug("Setting " + size + " bytes for oid inputstream");
			try {
				final LargeObjectManager largeObjectManager = getLargeObjectManager();

				long oid = largeObjectManager.createLO(LargeObjectManager.WRITE
						| LargeObjectManager.READ);

				// Copy the data from the file to the large object
				LargeObject obj = largeObjectManager.open(oid,
						LargeObjectManager.WRITE);
				byte buf[] = new byte[2048];
				int s;
				int tl = 0;
				while ((s = stream.read(buf, 0, 2048)) > 0) {
					obj.write(buf, 0, s);
					tl += s;
				}

				// statement.setBinaryStream(index, stream, (int) size);
				statement.setLong(index, oid);
				stream.close();
			} catch (IOException ie) {
				throw new StorageException(ie);
			}
		}
	}

	private void deleteLargeObject(MMObjectNode node, CoreField field) {
		log.debug("deleting large object " + field + " from node " + node);
		try {
			final long oid = getLargeObjectIdFromDatabase(node, field);
			if (!isOidNull(oid)) {
				final LargeObjectManager largeObjectManager = getLargeObjectManager();
				log.debug("deleting large object with oid=" + oid);
				largeObjectManager.delete(oid);
			}
		} catch (SQLException e) {
			throw new StorageException(e);
		}
	}

	/**
	 * @param node
	 *            the node the binary data belongs to
	 * @param field
	 *            the binary field
	 */
	private long getLargeObjectIdFromDatabase(MMObjectNode node, CoreField field) {
		try {
			MMObjectBuilder builder = node.getBuilder();
			Scheme scheme = factory.getScheme(Schemes.GET_BINARY_DATA,
					Schemes.GET_BINARY_DATA_DEFAULT);
			String query = scheme.format(new Object[] { this, builder, field,
					builder.getField("number"), node });
			getActiveConnection();
			Statement s = activeConnection.createStatement();
			ResultSet result = s.executeQuery(query);
			try {
				if (result != null && result.next()) {
					return result.getLong(1);
				}

				if (result != null) {
					result.close();
				}

				s.close();
				throw new StorageException("Node with number "
						+ node.getNumber() + " of type " + builder
						+ " not found.");
			} finally {
				result.close();
			}
		} catch (SQLException se) {
			throw new StorageException(se);
		} finally {
			releaseActiveConnection();
		}
	}

	private LargeObjectManager getLargeObjectManager() throws SQLException {
		if (activeConnection == null) {
			throw new IllegalStateException("no active connection present");
		}
		final LargeObjectManager largeObjectManager;
		if (activeConnection instanceof ConnectionWrapper) {
            ConnectionWrapper connWrapper = (ConnectionWrapper) activeConnection;
            if (connWrapper.isWrapperFor(org.postgresql.PGConnection.class)) {
                org.postgresql.PGConnection pgconn = connWrapper.unwrap(org.postgresql.PGConnection.class);
                largeObjectManager = pgconn.getLargeObjectAPI();
            }
            throw new IllegalStateException("Connection does not wrap org.postgresql.PGConnection");
		}
		else { 
		    largeObjectManager = ((org.postgresql.PGConnection) activeConnection).getLargeObjectAPI();
		}
		return largeObjectManager;
	}

	private boolean isOidNull(final long oid) {
		return oid == 0 || oid == NULL_OID;
	}

}
