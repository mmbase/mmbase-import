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
 * The Initial Developer of the Original Code is the
 * Ruud de Moor Centrum of the Open University.
 */
package nl.ou.rdmc.stats;

import nl.ou.rdmc.stats.process.ModelBuilder;
import java.util.Iterator;
import nl.ou.rdmc.stats.model.*;
import java.io.PrintStream;


public class LogViewer {

  private ModelBuilder modelBuilder;
  private PrintStream out;

  public LogViewer(ModelBuilder modelBuilder, PrintStream out) {
    this.modelBuilder = modelBuilder;
    this.out = out;
  }

  public void view() {
    for (Iterator it=modelBuilder.getModelIterator();it.hasNext();) {
      FUser user = (FUser)it.next();
      out.println("User: "+user.getName() + "\n");
      Iterator it2=user.getSessionsIterator();
      if (it2!=null) {
        while (it2.hasNext()) {
          FSession session = (FSession) it2.next();
          out.println("Session: " + session.getStart() + " | " + session.getEnd() +
                      " | " + session.getLength() +
                      " | " + session.getFinishReason() + "\n");
          Iterator it3 = session.getSubtracesIterator();
          if (it3!=null) {
            while (it3.hasNext() ) {
              FSubtrace test = (FSubtrace) it3.next();
              out.println("Test type:" + test.getTestType() + " | " +
                          test.getStart() + " | " + test.getEnd() + " | " +
                          test.getLength() +
                      " | " + test.getFinishReason() + "\n");
            }
          }
        }
      }

    }
  }

}
