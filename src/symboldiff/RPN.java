package symboldiff;

import java.util.Stack;

import symboldiff.exceptions.IncorrectExpression;

public class RPN extends StringParser {
    
  private String[] parsed;
  private String[] rpn;
    
    private int prior(String str) {
        if (isUnaryOperation(str)) {
          return 4;
        }
        else if (str.equalsIgnoreCase("*") || str.equalsIgnoreCase("/") ||
                 str.equalsIgnoreCase("^")) {
          return 3;
        }
        else if (str.equalsIgnoreCase("+") || str.equalsIgnoreCase("-")) {
          return 2;
        }
        else if (str.equalsIgnoreCase("(")) {
          return 1;
        }
      return 0;
    }
  
    // find operand for corresponding operation
    protected String[] find_operand(int from, boolean right) {
      int i, j, k;
      int cntr = 0;
      String out[];
        if (from == 0) {
            out = new String[1];
            out[0] = this.rpn[0];
          return out;
        }
        for (i = from - 1; i > -1; i--) {
            if (isBinaryOperation(this.rpn[i])) {
                cntr++;
            }
            else if (!isUnaryOperation(this.rpn[i])) {
                cntr--;
            }
            
            if (cntr < 0) {
                break;
            }            
        }
        j = 0;
        out = new String[(right) ? from - i : i];
        k = (right) ? i : 0;
        for (; k < ((right) ? from : i); k++) {
            out[j++] = this.rpn[k];
        }
      return out;
    }
    
    private String[] generate_rpn(String[] parsed) {
      Stack<String> stack = new Stack<String>();
      String[] out = new String[parsed.length];
      int i, j;
        j = 0;
        for (i = 0; i < parsed.length; i++) {
            if (parsed[i].charAt(0) == ')') {
                while (stack.lastElement().charAt(0) != '(') {
                    out[j++] = stack.pop();
                }
                stack.pop();
            }
            else if (parsed[i].charAt(0) == '(') {
                stack.push(parsed[i]);
            }
            else if (isBinaryOperation(parsed[i]) || 
                        isUnaryOperation(parsed[i])) {
                if (stack.empty() || 
                        prior(stack.lastElement()) < prior(parsed[i])) {
                    stack.push(parsed[i]);
                }
                else {
                    while (!stack.empty() && prior(stack.lastElement()) >= prior(parsed[i])) {
                        out[j++] = stack.pop();
                    }
                    stack.push(parsed[i]);
                }
            }
            else {
                out[j++] = parsed[i];
            }
        }
        
        while (!stack.empty()) {
            out[j++] = stack.pop();
        }
      return out;
    }
    
    public String[] getRPN() {
      return this.rpn;
    }
    
    public String[] getParsed() {
      return this.parsed;
    }
    
    public void setOrigExpr(String str) {
      super.orig_expr = str;
    }

    public RPN(String expr) throws IncorrectExpression {
      //this.parsed = new StringParser(expr).getParsedExpr();
        super(expr);
        if (expr.length() == 0)
        	throw new IllegalArgumentException();
        this.parsed = getParsedExpr();
      String[] tmp = generate_rpn(this.parsed); 
      int i, N = 0;
        // probably after rpn generation we will have extra slots
        // in RPN. so calculate number of null elements and throw them away
        for (i = tmp.length - 1; i > -1; i--) {
            N += (tmp[i] == null) ? 1 : 0;
            if (tmp[i] != null) {
              break;
            }
        }
        this.rpn = new String[tmp.length - N];
        for (i = 0; i < this.rpn.length; i++) {
            this.rpn[i] = tmp[i];
        }
    }
    
    public RPN(String[] pexp) {
        this.rpn = pexp;
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	for (String s : rpn) {
    		sb.append(s);
    		sb.append(" ");
    	}
    	return sb.toString();
    }

}
