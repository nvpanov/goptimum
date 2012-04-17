package symboldiff;

import symboldiff.exceptions.IncorrectExpression;

public class StringParser extends Expression {
  protected String orig_expr   = null;
  private String parsed_expr[] = null;
  private int findAppopriateBracketPos(String expr) {
      int i = expr.indexOf("(");
      int cntr;
        if (i == -1) {
          return expr.length();
        }
        cntr = 1;
        for (i = i+1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') {
                cntr++;
            }
            else if (expr.charAt(i) == ')') {
                cntr--;
            }
            if (cntr <= 0) {
              break;
            }
        }
      return i+1;
    }
  
    // assume expr doesn't have form such as (...)
    private Object[] find_first_binary_operation(String expr, boolean before_brackets) {
      int i; 
      int min_index = Integer.MAX_VALUE;
      String oper = null;
      int pos;
      int from = (before_brackets) ? 0 : findAppopriateBracketPos(expr);
      int to   = (before_brackets) ? expr.indexOf("(") : expr.length();
        to = (to == -1) ? expr.length() : to;
        for (i = 0; i < binary_operations.length; i++) {
            if ((pos = expr.indexOf(binary_operations[i], from)) < min_index && 
                    (pos >= from && pos <= to)) {
                min_index = pos;
                oper = binary_operations[i];
            }
        }
      return (oper != null) ? new Object[] {oper, min_index} : null;
    }
  
    private Object[] find_first_unary_operation(String expr) {
      int i;
        for (i = 0; i < unary_operations.length; i++) {
            if (expr.indexOf(unary_operations[i]) == 0) {
              return new Object[] {unary_operations[i], i};
            }
        }
      return null;
    }
    
    private Object[] find_first_operation(String expr) {
      Object[] oper = find_first_binary_operation(expr, true);
      if (oper == null) {
          oper = find_first_unary_operation(expr);
      }
      oper = (oper == null) ? find_first_binary_operation(expr, false) : oper;
      return oper;
    }
    
    private boolean embraced_by_brackets(String expr) {
      return (expr.indexOf("(") == 0 && findAppopriateBracketPos(expr) == expr.length()) ?
              true : false;
    }
    
    protected static boolean isBinaryOperation(String s) {
    	return isOperationInternal(s, binary_operations);
    }
    protected static boolean isUnaryOperation(String s) {
    	return isOperationInternal(s, unary_operations);
    }
    private String[] parse_string(String expr) throws IncorrectExpression {
      String oper      = null;
      String op1[]     = null;
      String op2[]     = null;
      String result[]  = null;
      int j = 0;
      int i;

       boolean brackets = false;
        if (embraced_by_brackets(expr)) {
            brackets = true;
            op1 = parse_string(expr.substring(1, expr.length() - 1));
        }
        else {
            Object[] r = find_first_operation(expr);
            if (r != null) {
                oper =  (String)r[0];
                int pos  = (Integer)r[1]; 
            
                if (isBinaryOperation(oper)) { 
                    op2 = parse_string(expr.substring(pos+oper.length(), expr.length()));

                    // leading minus: negate case
                	if (pos == 0 && oper.equals("-")) {
                        oper = "negate";
                    } else
                    	op1 = parse_string(expr.substring(0, pos));
                	
                	// 1e-8, 3.14e+6
                	if (pos!=0 && (oper.equals("-") || oper.equals("+")) 
                			   && (expr.charAt(pos-1) == 'e') ) {
                		try {
                			Double.parseDouble(op1[op1.length-1].substring(0, op1[op1.length-1].length()-1)); //last element in op1[] w/o last char which is 'e'
                    		// nvp 4/17/2012 {:
                			//op1[op1.length-1] += oper+op2[0]; 
                    		//op2[0] = "0"; 	// "1e-8"  => 1e  -  8    =>  1e-8 - 0
                    						//  ^string   ^op1   ^op2     ^op1	 ^op2
                			String number = op1[op1.length-1] + oper+op2[0];
                    		op1[op1.length-1] = "(";
                    		oper = number;
                    		op2[0] = ")";
                    		// }
                		} catch (NumberFormatException e) {
							// it is not our case. it just somevarfinisheswith_e+1
						}
                	}                    
                }
                else if (isUnaryOperation(oper)) {
                    op2 = parse_string(expr.substring(oper.length(), expr.length()));
                }
            }
            else {
                op1 = new String[1];
                op1[0] = expr;
            }
        }
        
        result = new String[((op1 != null) ? op1.length : 0) +
                            ((brackets) ? 2 : 0) +
                            ((op2 != null) ? op2.length : 0) +
                            ((oper != null) ? 1 : 0)];
        if (brackets) {
            result[j++] = "(";
        }
        
        if (op1 != null) {
            for (i = 0; i < op1.length; i++) {
                result[j++] = op1[i];
            }
        }
        
        if (oper != null) {
            result[j++] = oper;
        }
        
        if (op2 != null) {
            for (i = 0; i < op2.length; i++) {
                result[j++] = op2[i];
            }
        }
        
        if (brackets) {
            result[j++] = ")";
        }
      return result;
    }
    
    private static void checkBracketCorrectness(String expr) throws IncorrectExpression {
    	int pos=0, c=0;
    	do {
    		pos = expr.indexOf("(", pos)+1;
    		c++;
    	} while (pos != 0);
    	pos = 0;
    	do {
    		pos = expr.indexOf(")", pos)+1;
    		c--;
    	} while (pos != 0);
    	if (c != 0) {
    		String details;
    		if (c > 0)
    			details = " unclosed bracket" + (c==1?"":"s") + " '('";
    		else {
    			c = Math.abs(c);
    			details = " extra closing bracket" + (c==1?"":"s") + " ')'";
    		}
      		throw new IncorrectExpression("Expression >" + expr + "< is incorrect: " +
      				"it contains " + c + details);
    	}
	}
    public String[] getParsedExpr() {
      return this.parsed_expr;
    }
    
    public String getOrigExpr() {
      return this.orig_expr;
    }
    
    private static final int maxLength = 2000;
    public StringParser(String expr) throws IncorrectExpression {
        if (expr == null) {
        	throw new IncorrectExpression("null-string as an expression");
        }
        expr = expr.replace(" ", "");
        if (expr.length() == 0)
        	throw new IncorrectExpression("Empty string as an expression");
        if (expr.length() > maxLength)
        	throw new IncorrectExpression("Too long string as an expression. So far the maximum length is " + maxLength);
        checkBracketCorrectness(expr);
        expr = expr.toLowerCase();
        this.orig_expr = expr;
        this.parsed_expr = parse_string(expr);
    }
    protected StringParser() {
    }
}
