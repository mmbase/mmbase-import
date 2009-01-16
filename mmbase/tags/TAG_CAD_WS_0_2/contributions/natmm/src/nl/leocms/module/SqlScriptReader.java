/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class loads a .sql file and executes the queries in this file
 *
 * @author Freek Punt
 * @author Hillebrand Gelderblom
 */

public class SqlScriptReader {

   private ArrayList queries;
   private String fileName;
	/** MMBase logging system */
	private static Logger log = Logging.getLoggerInstance(SqlScriptReader.class.getName());

   /** Creates a new instance which automaticly runs load and execute
    */
   public SqlScriptReader(String fileName) throws SQLException, IOException {
      load(fileName);
   }

   /**
    * Loads the scriptfile
    */
   public void load(String fileName) throws IOException {
      this.fileName = fileName;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
         String query = "";
			queries = new ArrayList();
			String newLine = null;
			while((newLine = br.readLine()) != null) {
				if(!newLine.startsWith("#") && !newLine.startsWith("//") && !newLine.startsWith("--")) {
					query += newLine;
				}
            if (newLine.endsWith(";")) {
               queries.add(query);
               query="";
            }
			}
		}
		catch(Exception e) {
			log.error("Exception during loading script file: "+ fileName + " " + e);
		}
		finally {
			if(br != null)
				br.close();
		}
      log.info("SQLScriptReader.load(" + fileName + "), read: " + queries.size());
   }

   /** Getter for property queries.
    * @return Value of property queries.
    *
    */
   public ArrayList getQueries() {
      return queries;
   }   
}
