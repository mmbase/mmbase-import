/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.lang.*;
import java.util.*;
import org.mmbase.util.logging.*;


/**
 * Class to calculate expressions. It implements a simple LL(1)
 * grammar to calculate simple expressions with the basic
 * operators +,-,*,/ and brackets.
 * <br>
 * The grammar in EBNF notation:
 * <br>
 * &lt;expr&gt;   -&gt; &lt;term&gt; { '+' &lt;term&gt; } | &lt;term&gt; { '-' &lt;term&gt; } <br>
 * &lt;term&gt;   -&gt; &lt;fact&gt; { '*' &lt;fact&gt; } | &lt;fact&gt; { '/' &lt;fact&gt; } <br>
 * &lt;fact&gt;   -&gt; &lt;nmeral&gt; | '(' &lt;expr&gt; ')' <br>
 *
 * @author Arnold Beck
 */
public class ExprCalc {
    private static final int MC_SYMB=1;
    private static final int MC_NUM =2;
    private static final int MC_NONE=0;
    private static final int MC_EOT =-1;
    // logger
    private static Logger log = Logging.getLoggerInstance(ExprCalc.class.getName());

    // a token is represented by an tokencode (MCode)
    // and a tokenvalue (MSym or MNum) depending on
    // the tokencode

    private StringTokenizer T;

    private int	 MCode;
    private char   MSymb;
    private double MNum;

    private double Result;

    /**
     * Constructor of ExrpCalc
     * @param input a <code>String</code> representing the expression
     */
    public ExprCalc(String input) {
      T=new StringTokenizer(input,"+-*/()% \t",true);
      MCode=MC_NONE;
      Result = expr();
      if (MCode!=MC_EOT) {
          log.error("ExprCalc-> Error :"+input);
      }
    }

    /**
     * Returns the calculated value of the expression
     */
    public double getResult() {
        return Result;
    }

    /**
     * The lexer to produce a token when MCode is MC_NONE
     */
    private boolean lex() {
        String Token;
        if (MCode==MC_NONE) {
            MCode=MC_EOT;MSymb='\0';MNum=0.0;
            try {
                do {
                  Token=T.nextToken();
                } while (Token.equals(" ")||Token.equals("\t"));
            } catch(NoSuchElementException e)  {
                return false;
            }
            // numeral
            if (Character.isDigit(Token.charAt(0))) {
                int i;
                for(i=0;i<Token.length() &&
                    (Character.isDigit(Token.charAt(i)) ||
                     Token.charAt(i)=='.');i++) { };
                if (i!=Token.length()) {
                    log.error("ExprCalc-> Error");
		}
                try {
                    MNum=(Double.valueOf(Token)).doubleValue();
                } catch (NumberFormatException e) {
                    log.debug("ExprCalc-> Error");
		}
                MCode=MC_NUM;
            } else {          // symbol
                MSymb=Token.charAt(0);
                MCode=MC_SYMB;
            }
        }
        return true;
    }

    /**
     * expr implements the rule: <br>
     * &lt;expr&gt; -&lt; &lt;term&gt; { '+' &lt;term&gt; } | &lt;term&gt; { '-' &lt;term&gt; } .
     */
    private double expr() {
        double tmp=term();
        while (lex() && MCode==MC_SYMB && (MSymb=='+' || MSymb=='-')) {
            MCode=MC_NONE;
            if (MSymb=='+') {
                tmp+=term();
            } else {
                tmp-=term();
            }
        }
        if (MCode==MC_SYMB && MSymb=='('
            ||  MCode==MC_SYMB && MSymb==')'
            ||  MCode==MC_EOT) {
        } else {
          log.error("ExprCalc-> Error");
	}
        return tmp;
    }

    /**
     * term implements the rule: <br>
     * &lt;term&gt; -&lt; &lt;fact&gt; { '*' &lt;fact&gt; } | &lt;fact&gt; { '/' &lt;fact&gt; } .
     */
    private double term() {
        double tmp=fac();
        while (lex() && MCode==MC_SYMB && (MSymb=='*' || MSymb=='/' || MSymb=='%')) {
          MCode=MC_NONE;
          if (MSymb=='*') {
            tmp*=fac();
          } else if (MSymb=='/') {
            tmp/=fac();
          } else {
            tmp%=fac();
          }
        }
        return tmp;
    }

    /**
     * fac implements the rule <br>
     * &lt;fact&gt;  -&lt; &lt;nmeral&gt; | '(' &lt;expr&gt; ')' .
     */
    private double fac() {
        double tmp=-1;
        boolean minus=false;

        if(lex()&& MCode==MC_SYMB && MSymb=='-') {
            MCode=MC_NONE;
            minus=true;
        }
        if(lex() && MCode==MC_SYMB && MSymb=='(') {
            MCode=MC_NONE;
            tmp=expr();
            if(lex() && MCode!=MC_SYMB || MSymb!=')') {
                log.error("ExprCalc-> Error");
            }
            MCode=MC_NONE;
        } else if (MCode==MC_NUM) {
            MCode=MC_NONE;
            tmp=MNum;
        } else {
            log.error("ExprCalc-> Error");
	}
        if (minus) tmp=-tmp;
        return tmp;
    }
}
