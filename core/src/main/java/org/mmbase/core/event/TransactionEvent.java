/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.9.3
 * @version $Id: TransactionEvent.java 41369 2010-03-15 20:54:45Z michiel $
 */
public abstract class TransactionEvent extends LocalEvent {
    private static final Logger log = Logging.getLoggerInstance(TransactionEvent.class);

    protected final String transactionName;

    TransactionEvent(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionName() {
        return transactionName;
    }


    public static abstract class End extends TransactionEvent {
        public End(String transactionName) {
            super(transactionName);
        }
    }

    public static class Commit extends End {
        private static final long serialVersionUID = 1L;

        public Commit(String transactionName) {
            super(transactionName);
        }
        @Override
        public String toString() {
            return "commit:" + getTransactionName();
        }
    }

    public static class Resolve extends TransactionEvent {
        private static final long serialVersionUID = 1L;
        private final Map<Integer, Integer> resolved;
        public Resolve(String transactionName, Map<Integer, Integer> resolved) {
            super(transactionName);
            this.resolved = resolved;
        }
        public Map<Integer, Integer> getResolution() {
            return resolved;
        }
        @Override
        public String toString() {
            return "resolve:" + getTransactionName() + " " + getResolution();
        }
    }

    public static class Cancel extends End {
        private static final long serialVersionUID = 1L;

        public Cancel(String transactionName) {
            super(transactionName);
        }
        @Override
        public String toString() {
            return "cancel:" + getTransactionName() ;
        }
    }

    public static class Create extends TransactionEvent {
        private static final long serialVersionUID = 1L;

        public Create(String transactionName) {
            super(transactionName);
        }
        @Override
        public String toString() {
            return "create:" + getTransactionName() ;
        }
    }
}
