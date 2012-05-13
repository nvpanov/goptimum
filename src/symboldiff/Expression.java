package symboldiff;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.runner.Computer;

import com.sun.corba.se.spi.extension.ZeroPortPolicy;
import com.sun.org.apache.bcel.internal.generic.ISUB;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import net.sourceforge.interval.ia_math.exceptions.IAComputationalException;
import static net.sourceforge.interval.ia_math.IAMath.*;
import static java.lang.Double.*;

import core.Box;

import symboldiff.exceptions.ExpressionException;
import symboldiff.exceptions.IncorrectExpression;
import symboldiff.exceptions.UnsupportedFunction;

public class Expression implements Cloneable {
	private Expression left = null;
	private Expression right = null;
	private Method method = null;
	private String op;
	
	/*
	 * for debug purposes. 
	 * each expression could tell is it const or variable.
	 * actually it uses @variableNumber@ to do this.
	 * Its value is set during initialization and to ensure
	 * that all methods that calls it will get properly inited
	 * values we use this dbgInited flag.
	 */
	protected static final String binary_operations[] = { "+", "-", "/", "*", "^" };

	protected static final String unary_operations[] = { "arccos", "arcsin", "arctg",
			"arcctg", "sin", "cos", "tg", "ctg", "ln", "exp", "sqrt", "negate" };
	protected static final String ambiguous[] = {"%", "&", "~", "|", "`", "\"", "\\", "<", ">", "=", ",", ".", "?", "!"};

	private ArrayList<String> coords;
	//private static enum ExpType{unset, constant, notAconstant, variable, binaryOperation, notAbinaryOperation, unaryOperation, notAnUnaryOperation};

	// List supports random access: get(i)
	// while Set -- doesn't
	public ArrayList<String> getVariables() {
		if (coords == null) {
			coords = new ArrayList<String>();
			coords.addAll(getVariables(this));
			Collections.sort(coords);  // sorting will ensure that if f=x1+x0, x0 is var #0, and x1 is var #1.
										// w/o sorting the numbers of vars depends on its position in expression
			
			if (coords.size() == 0) { //nvp 1/7/2012: there are no variables: f=1.0
				coords.add("0xDEADBEEF"); // just a placeholder to make the size not be zero
											// Otherwise gradients crash
			}
		}
		return coords;
	}
	/*
	 * when the expression is calculated root node should set ALL
	 * variables to all subExpressions, otherwise exp.getVariable() will
	 * return different numbers depending on which part of sub expression it is call
	 */
	void setVariablesList() {
//		assert(coords == null); // WTF
//		assert(!isVariable() && !isConstant()); // WTF??
		ArrayList<String> vars = getVariables();
		setVariablesList(vars);
	}
	private void setVariablesList(ArrayList<String> vars) {
		coords = vars;
		Expression r = getRightExpression();
		Expression l = getLeftExpression();
		if (r  != null) 
			r.setVariablesList(vars);
		if (l != null)
			l.setVariablesList(vars);
	}

	/*
	 * This is a private method used by public @getCoords()@.
	 * This function traverses the expression tree @expr@
	 * and returns an array of Strings with names of variables
	 * example: for xyz0 + 0 - x^y * sin(x) should return
	 * <x, xyz0, y>.
	 * TreeSet is a sorted collection without duplicates  
	 */
	private static TreeSet<String> getVariables(Expression expr) {
		TreeSet<String> variablesLeft = new TreeSet<String>();
		TreeSet<String> variablesRight;
		
		// recursion: go as deep as possible		

		// process all lefts
		if (expr.getLeftExpression() != null) {
			variablesLeft.addAll(getVariables(expr.getLeftExpression()));
		}

		// process itself
		if (expr.getOperation() != null) {
			if (expr.isVariable()) {
				// we have found a variable
				// TreeSet will not insert duplicated value so we just add it
				// nothing will added if that variable is already in the set
				variablesLeft.add(expr.getOperation());
			}
		}

		// process rights
		if (expr.getRightExpression() != null) {
			variablesRight = getVariables(expr.getRightExpression());
			variablesLeft.addAll(variablesRight); // no duplicates and auto-sorting thanks to TreeSet
		}
		return variablesLeft;
	}


		
	/*
	 * determining type of expression
	 */
	public boolean isConstant() {
		try {
			if (getOperation().equals("pi") ||
					Double.valueOf(getOperation()) == 0) {
				; // do nothing
			}
			// this construction means that either the value is PI or
			// it is ANY number 
			// all other cases will cause exception
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public boolean isVariable() {
		if (isOperation() || isConstant() )
			return false;
		assert(left == null && right == null);
		return true;
	}
	public boolean isOperation() {
		return isBinaryOperation() || isUnaryOperation();
	}
	// checking if op at the top of the branch is unary operation
	public boolean isUnaryOperation() {
		return isOperationInternal(getOperation(), unary_operations);
	}
	// checking if op at the top of the branch is binary operation
	public boolean isBinaryOperation() {
		return isOperationInternal(getOperation(), binary_operations);
	}
	public boolean isAdd() {
		return op.equals("+");
	}
	public boolean isSub() {
		return op.equals("-");
	}
	public boolean isMul() {
		return op.equals("*");
	}
	public boolean isDiv() {
		return op.equals("/");
	}
	public boolean isPow() {
		return op.equals("^");
	}
	public boolean isNegate() {
		return op.equals("negate");
	}
	
	// also used by StringParser. Thats why it receives operation as string	
	protected static boolean isOperationInternal(String thisOp, String[] operations) {
		// Honest checking. 
		int i;
		for (i = 0; i < operations.length; i++) {
			if (thisOp.equals(operations[i])) {
				return true;
			}
		}
		return false;
	}
	private void addExpressions(Expression lhs, Expression rhs, String op) {
		this.op = op;
		this.left = lhs;
		this.right = rhs;
	}

	/*
	 *  printing staff
	 */
	@Override
	public String toString() {
		StringBuffer out = printExpression(this);
//		removeExtraBrackets(out);
		return out.toString();
	}
	private static StringBuffer printExpression(Expression e) {
		StringBuffer out = new StringBuffer();
		int priority = e.isBinaryOperation() ? RPN.prior(e.getOperation()) : Integer.MAX_VALUE;
		return printExpression(e, priority, out);
	}
	private static StringBuffer printExpression(Expression e, int priorityOfHigherOp, StringBuffer out) {
		if (e == null)
			return out;
		String strOperation = e.getOperation();
		int thisPriority = priorityOfHigherOp;
		Expression left = e.getLeftExpression();
		Expression right = e.getRightExpression();

		// does this binary needs brackets?
		boolean binaryOpNeedsBrackets = e.isBinaryOperation(); // at least it should be binary
		// if current operation is unary it requires brackets before the argument (right part)
		boolean rightNeedsBrackets = false;
		
		if(binaryOpNeedsBrackets) {
			thisPriority = RPN.prior(strOperation);
			binaryOpNeedsBrackets = thisPriority < priorityOfHigherOp; // it definitely needs brackets if next op has higher priority.
			// also brackets are required if left or right part contains non-comutative operation: x-(y+z). priority is the same.
			if (thisPriority == priorityOfHigherOp && 				// operations have equal priority but
					(e.isSub() || e.isDiv() || e.isPow()) &&        // this ops are non-commutative  
					nextBinaryOperationsSamePriorityNeedsBrackets(right, strOperation) ) { // right is an expression contains equal or lower operations
				rightNeedsBrackets = true; // in this case right part does need brackets
			}
		} else if (e.isUnaryOperation() ) { // initially binaryOpNeedsBrackets = e.isBinaryOperation()
			rightNeedsBrackets = true;
			thisPriority = -1;
			// ^^ nvp 3/23/2012: get rid of double brackets in case of complex arguments in unary functions: ln((x+y));
			if ("negate".equals(strOperation) ) {
				strOperation = "-";
				rightNeedsBrackets = (right.isAdd() || right.isSub()); // nvp 3/23/2012 : -(a+b); while -(a*b) is OK
			}
		} 
		
		if (binaryOpNeedsBrackets)
			out.append("(");
		printExpression(left, thisPriority, out);

		// already print left branch, now is going to print this node

		out.append(strOperation);
		
		if (rightNeedsBrackets) // unary op or complex right part in non-commutative binary op
			out.append("(");
		printExpression(right, thisPriority, out);
		if (rightNeedsBrackets || binaryOpNeedsBrackets)
			out.append(")");
		return out;
	}
	//private static boolean nextBinaryOperationsSameOrLowPriorityNeedsBrackets(Expression nextE, String prevOp) {
	private static boolean nextBinaryOperationsSamePriorityNeedsBrackets(Expression nextE, String prevOp) {	
		if (nextE == null)
			return false;
		if ( nextE.isBinaryOperation() ) {
			if (!prevOp.equals(nextE.getOperation() )) {
				//if (RPN.prior(prevOp) >= RPN.prior(nextE.getOperation())) 
				// ^^ nvp 3/23/2012 : not "OrLow Priority any more (otherwise double brackets in some cases)
				if (RPN.prior(prevOp) == RPN.prior(nextE.getOperation()))
					return true;
				return false;
			} else { // this op equals previous
				if (prevOp.equals("+") || prevOp.equals("*")) // + and * are commutative operations  
					return false;
				return true; // '-' and '/' and '^' 
			}
		}
		return false; // not a binary
	}
/*	private static void removeExtraBrackets(StringBuffer out) {
		// (x binaryOp y)
		if (out.charAt(0) == '(') {
			out.deleteCharAt(0);
			out.deleteCharAt(out.length()-1);
		}		
	}
*/	
	public String toStringGraph() {
		final int rootPos = 30;
		int step = 4;
		StringBuffer out = new StringBuffer();
		// actually we are going to implement a sort of BFS: breadth-first search
		Deque/*LinkedList*/<Expression> nodes = new LinkedList<Expression>();
		Deque/*LinkedList*/<Expression> nextLevel = new LinkedList<Expression>();
		Deque/*LinkedList*/<Integer> positions = new LinkedList<Integer>();
		Deque/*LinkedList*/<Integer> nextLevelPositions = new LinkedList<Integer>();
		nodes.addLast(this);
		positions.addLast(rootPos);
		
		// for each node on this level
		// 0. cout node with its offset 
		// 1. see how many children has each node on this level
		// 2. compute appropriate position in the output string
		//    for each child
		// 3. add children to next level list.  
		while (true){
			int prevPos = 0;
			while(nodes.size() != 0) {
				Expression node = nodes.removeFirst();
				int pos = positions.removeFirst();
				putSimbolInStr(out, node.getOperation(), pos-prevPos);
				prevPos = pos+1;
				
				Expression l = node.getLeftExpression();
				Expression r = node.getRightExpression();
				if (l != null) {
					nextLevel.addLast(l);
					if (pos - step <= 0) {
						out.append("Too wide graph. Truncated.. ");
						return out.toString();
					}
					nextLevelPositions.addLast(pos - step);
				}
				if (r != null) {
					nextLevel.addLast(r);
					nextLevelPositions.addLast(pos + step);
				}
			}
			if (nextLevel.size() == 0)
				break;
			
			//moveToNextLevel(nodes, nextLevel, positions, nextLevelPositions);
			out.append("\n");
			if (step > 1)
				step--;
			Deque tmp;
			tmp = nodes;
			nodes = nextLevel;
			nextLevel = tmp;
			tmp = positions;
			positions = nextLevelPositions;
			nextLevelPositions = tmp;			
		}
		return out.toString();		
	}		
	private static void putSimbolInStr(StringBuffer out, String mnemonic, int pos) {
		for (int i = 0; i < pos; i++)
			out.append(" ");
		out.append(mnemonic);		
	}
/*
	@SuppressWarnings("unchecked")
	private static void moveToNextLevel(Collection<Expression> nodes, 
					Collection<Expression> nextLevel, 
					Collection<Integer> positions, 
					Collection<Integer> nextLevelPositions) {
		Collection tmp;
		tmp = nodes;
		nodes = nextLevel;
		nextLevel = tmp;
		tmp = positions;
		positions = nextLevelPositions;
		nextLevelPositions = tmp;
	}	
*/
	
	public boolean hasVar(String var) {
		boolean result = false;
		if (left != null) {
			result = result || _hasVar(left, var);
		}
		if (op != null) {
			result = result || op.equalsIgnoreCase(var);
		}
		if (right != null) {
			result = result || _hasVar(right, var);
		}
		return result;
	}

	private boolean _hasVar(Expression exp, String var) {
		boolean result = false;
		if (exp.getLeftExpression() != null) {
			result = result || exp.getLeftExpression().hasVar(var);
		}
		if (exp.getOperation() != null) {
			result = result || exp.getOperation().equalsIgnoreCase(var);
		}
		if (exp.getRightExpression() != null) {
			result = result || exp.getRightExpression().hasVar(var);
		}
		return result;
	}

	/*
	 * constructors and factory
	 */
	public Expression(RPN rpn) throws ExpressionException {
		init(rpn);
	}
	public Expression(String exp) throws ExpressionException {
		RPN rpn = new RPN(exp);
		init(rpn);
	}
	protected Expression() {
		
	}
	public static Expression newConstant(double constant) {
		Expression exp = new Expression();
		exp.setConstantValue(constant);
		return exp;
	}
	/*
	public static Expression newVariable(String varName) {
		Expression exp = new Expression();
		exp.op = varName;
		return exp;
	}
	*/
	public static Expression newExpression(Expression left, Expression right, String op) {
		Expression exp = new Expression();
		exp.addExpressions(left, right, op);
		return exp;
	}
	
	

	protected void init(RPN rpn) throws ExpressionException {
		String[] srpn = rpn.getRPN();
		int length = srpn.length;
		op = srpn[length - 1];
		if (isBinaryOperation()) {
			left = new Expression(new RPN(rpn.find_operand(length - 1, false)));
			right = new Expression(new RPN(rpn.find_operand(length - 1, true)));
		} else if (isUnaryOperation()) {
			right = new Expression(new RPN(rpn.find_operand(length - 1, true)));
		}
		else { // could only be a variable or constant
			if (right != null) 
				throw new IllegalStateException("Something wrong with the expression");
				// some sanity checks were here. moved down
/* -- impossible case. input expression is trimmed, so 'x y' => 'xy' 
 			if ( op.contains(" ") ) { // user entered something like x y,  we decided that this is a variable.
				// But variables can't contain spaces
				throw new IncorrectExpression("Something is wrong with the expression. Probably some operation is missed between this variables: '" + op + "'"); 
			}
*/				
		}
		setVariablesList();
		
		//System.out.println("\n\n=======\n"+toString() + "\n\n" + toStringGraph() + "\n\n");
		
		// sanity checks:
		List<String> vars = getVariables();
		for (String v : vars) {
			if ( v.contains("(") ) // user entered some function that we don't know. So we decided that this is a variable.
				throw new UnsupportedFunction("Unsupported function " + op); // But variables can't contain brackets
			for (String amb : ambiguous)
				if ( v.contains(amb) )
					throw new IncorrectExpression("Ambiguous name of following variable: '" + v + 
							"'. Actually nothing is really wrong, but just to avoid any confusion...");
			for (int i = 0; i < v.length(); i++)
				if (v.charAt(i) > 127) // Simplifyer.isEqualsAccurateWithinConstants() depends on it!
					throw new IncorrectExpression("Please use only ASCII (non-unicode) characters for variable names. " +
							"Just to avoid a possibility to mess up similar-looking but actually different characters.");
			/* ^^^
			 * Java Strings are conceptually encoded as UTF-16. In UTF-16, the ASCII character set is encoded 
			 * as the values 0 - 127 and the encoding for any non ASCII character (which may consist of more 
			 * than one Java char) is guaranteed not to include the numbers 0 - 127
			 */
		}
		try {
			evaluate(new Box(this.getDimension(), new RealInterval(1)));
			double p[] = new double[this.getDimension()]; // all zeroes
			evaluate(p);
		} catch (UnsupportedOperationException e) {
			// there is at least one unsupported function
			throw new IncorrectExpression("The expression can't be evaluated. " + e.getMessage() + "");
		} 
		catch (Exception | AssertionError e) {
			// it is OK here -- input data is zeroes
			// do nothing. will fall later in evaluation if there is a real problem
		}
	}

	// return new object of expression which has the same
	// operations/operands inside
	@Override
	public Expression clone() {
		Expression clone = newExpression(null, null, op);
		if (this.left != null)
			clone.left = this.left.clone();
		else
			clone.left = null;
		if (this.right != null)
			clone.right = this.right.clone();
		else
			clone.right = null;
		clone.setVariablesList(this.getVariables());
		assert(this.equals(clone));
		return clone;
		
/*		
		try {
			c = new Expression(toString());
		} catch (ExpressionException e) {
			// we are coping a proper expression so everything should be OK here.
			e.printStackTrace();
		}
		return c;
*/		
	}
	

	
	/*
	 * setters
	 */
	public void setTo(Expression e) {
		setLeftExpression(e.getLeftExpression());
		setRightExpression(e.getRightExpression());
		setOperation(e.getOperation());
		setMethod(e.getMethod());	
	}
	public void setTo(double d) {
		setLeftExpression(null);
		setRightExpression(null);
		setConstantValue(d);
	}
	
	public void setLeftExpression(Expression e) {
		this.left = e;
	}
	public void setRightExpression(Expression e) {
		this.right = e;
	}
	public void setOperation(String op) {
		this.op = op;
	}
	public void setConstantValue(double d) {
		if (left != null || right != null)
			throw new IllegalArgumentException("Node has chaild nodes.");
		// nvp 3/23/2012 : get rid from all this 1.0, 2.0, etc.
		if (d == (int)d) // xxx.0
			this.op = Integer.toString((int)d);
		else
			this.op = Double.toString(d);
	}
	public void setMethod(Method m) {
		this.method = m;
	}

	/*
	 * getters
	 */
	public Expression getLeftExpression() {
		return this.left;
	}

	public Expression getRightExpression() {
		return this.right;
	}

	public String getOperation() {
		return this.op;
	}

	public Method getMethod() {
		return this.method;
	}
	
	public double getConstantValue() {
		if (!isConstant())
			throw new IllegalStateException("Not a constant");
		double d;
		if (getOperation().compareTo("pi") == 0) {
			d = Math.PI;
		} else {
			d = Double.valueOf(getOperation());
		}
		return d;
	}
	public String getMethodName() {
		if (isUnaryOperation() || isBinaryOperation())
			return getOperation();
		throw new IllegalStateException("Not an operation");
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////
	// trying to get rid from MethodRunner
	// and implement 
	// evaluate()
	///////////////////////////////////////////////////////////////////
	
	public RealInterval evaluate(Box area) {
		if (isConstant())
			return new RealInterval(getConstantValue());
		if (isVariable())
			return area.getInterval(getNumberOfThisVariable());
		// it is an expression
		RealInterval l = null, r;
		if (left != null) // left could be null for unary expressions
			l = left.evaluate(area);
		r = right.evaluate(area);
		RealInterval res = evaluate(op, l, r);
//		if( !Double.isInfinite(res.lo()) && !Double.isInfinite(res.hi()) )
//			System.out.println("Infinity as a bound!");
		assert( !Double.isNaN(res.lo()) && !Double.isNaN(res.hi()) );
		return res;
	}
	// evaluate for points
	public double evaluate(double... point) {
		assert(point.length == getVariables().size());
		if (isConstant())
			return getConstantValue();
		if (isVariable())
			return point[getNumberOfThisVariable()];
		// it is an expression
		double l = Double.NaN, r;
		if (left != null) // left could be null for unary expressions
			l = left.evaluate(point);
		r = right.evaluate(point);
		double res = evaluate(op, l, r);
		assert( /*!Double.isInfinite(res) &&*/ !Double.isNaN(res) ); // 5/12/12 -- infinity is possible: divizion by zero
		return res;
	}
	private static RealInterval evaluate(String op, RealInterval l, RealInterval r) {
		try {
		switch (op) {
			case "+":
				return add(l, r);
			case "-":
				return sub(l, r);
			case "*":
				return mul(l, r);
			case "/":
				return div(l, r);
			case "^":
				if (l.contains(0)) // @power@ doesn't allow 0 in argument
					return intPow(l, r);
				return power(l, r);
				
			case "negate":
				return negate(r);
			case "arccos":
				return arccos(r);
			case "arcsin":
				return arcsin(r);
			case "arctg":
				return arctg(r);
			case "arcctg":
				return arcctg(r);
			case "sin":
				return sin(r);
			case "cos":
				return cos(r);
			case "tg":
				return tg(r);
			case "ctg":
				return ctg(r);
			case "ln":
				return ln(r);
			case "exp":
				return exp(r);
			case "sqrt":
				return sqrt(r);
	
			default:
				throw new UnsupportedOperationException("Unsupported operation for intervals: " + op);
			}
		} catch (IAComputationalException e) {
			if ("Division by Zero".equals(e.getMessage()) ) 
				return new RealInterval();
			else
				throw new RuntimeException("Issue during interval evaluation of '" + op + "' :" + e.getMessage() );				
		} catch (Exception e) {
			throw new RuntimeException("Issue during interval evaluation of '" + op + "' :" + e.getMessage() );
		}
	}	
	/*private */static double evaluate(String op, double l, double r) {
	// ^^^ because is used in Simplifier.calcFunctionsFromConsts() 	
		try {	
		switch (op) {
			case "+":
				return l + r;
			case "-":
				return l - r;
			case "*":
				return (l * r);
			case "/":
				return (l / r);
			case "^":
				return Math.pow(l, r);
				
			case "negate":
				return -(r);
			case "arccos":
				return Math.acos(r);
			case "arcsin":
				return Math.asin(r);
			case "arctg":
				return Math.atan(r);
			//case "arcctg":
				//
			case "sin":
				return Math.sin(r);
			case "cos":
				return Math.cos(r);
			case "tg":
				return Math.tan(r);
			//case "ctg":
				//
			case "ln":
				return Math.log(r);
			case "exp":
				return Math.exp(r);
			case "sqrt":
				return Math.sqrt(r);
	
			default:
				throw new UnsupportedOperationException("Unsupported operation for doubles: " + op);
			}
		} catch (Exception e) {
			throw new RuntimeException("Issue during evaluation of '" + op + "' : " + e.getMessage() );
		}
	}
	/*
	 * returns position in list of variables of the variable
	 * represented by this Expression.
	 * returns -1 if this is not a variable.
	 */
	public int getNumberOfThisVariable() {
		return getVariableNum(this.getOperation());
	}
	public int getVariableNum(String varName) {
		return getVariables().indexOf(varName);
	}
	
	public int getDimension() {
		return numOfVars();
	}
	public int numOfVars() {
		return getVariables().size();
	}
	public int length() {
		return length(this);
	}
	public static int length(Expression exp) {
		if (exp == null)
			return 0;
		return 1 + length(exp.left) + length(exp.right);
	}
	
	// as far as we implementing custom equals
	// we need to implement hashCode as well
	@Override
	public int hashCode() {
		// not very fast implementation
		// but simple. 
		// Anyway it uses the same fields as
		// equals() do, so it is correct.
		// TODO: rewrite on something like
		//        int hash = 37;
		//        hash = hash*17 + areaCode;
		//        hash = hash*17 + number;
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
	    //check for self-comparison
	    if ( this == object ) return true;

	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explicit check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    if ( !(object instanceof Expression) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    Expression that = (Expression)object;
	    return this.equals(that);
	}
	public boolean equals(Expression that) {
	    //now a proper field-by-field evaluation can be made
	    if (!this.op.equals(that.op))
	    	return false;
	    if (this.left == null && that.left != null ||
	    	this.left != null && that.left == null ||
	    	this.right== null && that.right!= null ||
	    	this.right!= null && that.right== null )
	    		return false;

	    if (this.left != null) {
	    	if (!this.left.equals(that.left) )
	    		return false; 
	    }
	    if (this.right != null) {
	    	if (!this.right.equals(that.right) )
	    		return false; 
	    }
	    return true;
	}
}