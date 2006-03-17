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
package nl.ou.rdmc.stats.model;

public class FFinishReason {

  public static final String TIMEOUT_REASON = "TIMEOUT";
  public static final String FILEEND_REASON = "FILEEND";
  public static final String NEWDAY_REASON = "NEWDAY";
  public static final String NEWSUBTRACECAME_REASON = "NEWSUBTRACECAME";
  public static final String SUBTRACEPASS_REASON = "SUBTRACEPASS";


  public static final String[] REASONS = {TIMEOUT_REASON, FILEEND_REASON, NEWDAY_REASON, NEWSUBTRACECAME_REASON, SUBTRACEPASS_REASON};

  public static final FFinishReason TIMEOUT = new FFinishReason(0);
  public static final FFinishReason FILEEND = new FFinishReason(1);
  public static final FFinishReason NEWDAY = new FFinishReason(2);

  // only for subtraces
  public static final FFinishReason NEWSUBTRACECAME = new FFinishReason(3);
  public static final FFinishReason SUBTRACEPASS = new FFinishReason(4);

  private int reason;

  private FFinishReason(int reason) {
    this.reason = reason;
  }

  public String toString() {
    return REASONS[reason];
  }

}
