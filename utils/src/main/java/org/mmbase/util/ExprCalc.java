/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;


/**
 * Class to calculate expressions. It implements a simple LL(1)
 * grammar to calculate simple expressions with the basic
 * operators +,-,*,/ and brackets.
 * <br />
 * The grammar in EBNF notation:
 * <br />
 * &lt;expr&gt;   -&gt; &lt;term&gt; { '+' &lt;term&gt; } | &lt;term&gt; { '-' &lt;term&gt; } <br />
 * &lt;term&gt;   -&gt; &lt;fact&gt; { '*' &lt;fact&gt; } | &lt;fact&gt; { '/' &lt;fact&gt; } <br />
 * &lt;fact&gt;   -&gt; &lt;nmeral&gt; | '(' &lt;expr&gt; ')' <br />
 *
 * @author Arnold Beck
 * @version $Id$
 */
public class ExprCalc {
    private static final int MC_SYMB=1;
    private static final int MC_NUM =2;
    private static final int MC_NONE=0;
    private static final int MC_EOT =-1;

    private static final Logger log = Logging.getLoggerInstance(ExprCalc.class);

    // a token is represented by an tokencode (MCode)
    // and a tokenvalue (MSym or MNum) depending on
    // the tokencode

    private StringTokenizer tokenizer;
    private String          input;

    private int	   mCode;
    private char   mSymb;
    private double mNum;

    private double result;

    /**
     * Constructor of ExrpCalc
     * @param input a <code>String</code> representing the expression
     */
    public ExprCalc(String input) {
        this.input = input;
        tokenizer = new StringTokenizer(input, "+-*/()% \t", true);
        mCode = MC_NONE;
        result = expr();
        if (mCode != MC_EOT) {
            log.error("Could not evaluate expression: '" + input + "'");
        }
    }

    /**
     * Returns the calculated value of the expression
     */
    public double getResult() {
        return result;
    }

    /**
     * The lexer to produce a token when mCode is MC_NONE
     */
    private boolean lex() {
        String token;
        if (mCode==MC_NONE) {
            mCode=MC_EOT;mSymb='\0';mNum=0.0;
            try {
                do {
                  token = tokenizer.nextToken();
                } while (token.equals(" ")||token.equals("\t"));
            } catch(NoSuchElementException e)  {
                return false;
            }
            // numeral
            if (Character.isDigit(token.charAt(0))) {
                int i;
                for(i=0;i<token.length() &&
                    (Character.isDigit(token.charAt(i)) ||
                     token.charAt(i)=='.');i++) { };
                if (i!=token.length()) {
                    log.error("Could not evaluate expression '" + token + "' of '" + input + "'");
                }
                try {
                    mNum=(Double.valueOf(token)).doubleValue();
                } catch (NumberFormatException e) {
                    log.error("Could not evaluate expression ('" + token + "' not a number) of '" + input + "'");
                }
                mCode=MC_NUM;
            } else {          // symbol
                mSymb=token.charAt(0);
                mCode=MC_SYMB;
            }
        }
        return true;
    }

    /**
     * expr implements the rule: <br />
     * &lt;expr&gt; -&lt; &lt;term&gt; { '+' &lt;term&gt; } | &lt;term&gt; { '-' &lt;term&gt; } .
     */
    private double expr() {
        double tmp = term();
        while (lex() && mCode == MC_SYMB && (mSymb == '+' || mSymb == '-')) {
            mCode=MC_NONE;
            if (mSymb=='+') {
                tmp += term();
            } else {
                tmp -= term();
            }
        }
        if (mCode==MC_SYMB && mSymb=='('
            ||  mCode==MC_SYMB && mSymb==')'
            ||  mCode==MC_EOT) {

        } else {
            log.error("expr: Could not evaluate expression '" + input + "'");
        }
        return tmp;
    }

    /**
     * term implements the rule: <br />
     * &lt;term&gt; -&lt; &lt;fact&gt; { '*' &lt;fact&gt; } | &lt;fact&gt; { '/' &lt;fact&gt; } .
     */
    private double term() {
        double tmp=fac();
        while (lex() && mCode==MC_SYMB && (mSymb=='*' || mSymb=='/' || mSymb=='%')) {
          mCode=MC_NONE;
          if (mSymb=='*') {
            tmp *= fac();
          } else if (mSymb=='/') {
            tmp /= fac();
          } else {
            tmp %= fac();
          }
        }
        return tmp;
    }

    /**
     * fac implements the rule <br />
     * &lt;fact&gt;  -&lt; &lt;nmeral&gt; | '(' &lt;expr&gt; ')' .
     */
    private double fac() {
        double tmp =- 1;
        boolean minus=false;

        if(lex()&& mCode==MC_SYMB && mSymb=='-') {
            mCode = MC_NONE;
            minus = true;
        }
        if(lex() && mCode==MC_SYMB && mSymb=='(') {
            mCode = MC_NONE;
            tmp = expr();
            if(lex() && mCode!=MC_SYMB || mSymb!=')') {
                log.error("fac1: Could not evaluate expression '" + input + "'");
            }
            mCode=MC_NONE;
        } else if (mCode==MC_NUM) {
            mCode=MC_NONE;
            tmp=mNum;
        } else {
            log.error("fac2: Could not evaluate expression '" + input + "'");
        }
        if (minus) tmp = -tmp;
        return tmp;
    }
}
