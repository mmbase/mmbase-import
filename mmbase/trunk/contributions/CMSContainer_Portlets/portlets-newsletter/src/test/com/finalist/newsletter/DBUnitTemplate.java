package com.finalist.newsletter;

import java.io.*;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

public class DBUnitTemplate {

   private BasicDataSource dataSource;

   private IDatabaseConnection connection;

   public DBUnitTemplate() {

   }

   public void setDataSource(BasicDataSource dataSource) throws SQLException {

      this.dataSource = dataSource;
      connection = new DatabaseConnection(this.dataSource.getConnection(), "cmsc");
   }

   public void execute(String datafileName, DatabaseOperation oper) throws FileNotFoundException, IOException, DatabaseUnitException, SQLException {
      InputStream inputStream = null;
      try {
         inputStream = Class.forName("com.finalist.newsletter.dataset.DS").getResourceAsStream(datafileName);
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      IDataSet dataset = new FlatXmlDataSet(inputStream);
      oper.execute(connection, dataset);
   }
}