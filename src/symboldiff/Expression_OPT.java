//package symboldiff;
//
//import java.lang.reflect.*;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Deque;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.TreeSet;
//import java.util.Vector;
//
//import com.sun.org.apache.bcel.internal.generic.ISUB;
//
//import net.sourceforge.interval.ia_math.IAMath;
//import net.sourceforge.interval.ia_math.RealInterval;
//import static net.sourceforge.interval.ia_math.IAMath.*;
//import static java.lang.Double.*;
//
//import core.Box;
//
//import symboldiff.exceptions.ExpressionException;
//import symboldiff.exceptions.IncorrectExpression;
//import symboldiff.exceptions.UnsupportedFunction;
//
//public class Expression_OPT implements Cloneable {
//	private Expression left = null;
//	private Expression right = null;
//	private Method method = null;
//	private String op;
//	
//	/*
//	 * for debug purposes. 
//	 * each expression could tell is it const or variable.
//	 * actually it uses @variableNumber@ to do this.
//	 * Its value is set during initialization and to ensure
//	 * that all methods that calls it will get properly inited
//	 * values we use this dbgInited flag.
//	 */
//	protected static final String binary_operations[] = { "+", "-", "/", "*", "^" };
//
//	protected static final String unary_operations[] = { "arccos", "arcsin", "arctg",
//			"arcctg", "sin", "cos", "tg", "ctg", "ln", "exp", "sqrt", "negate" };
//
//	private ArrayList<String> coords;
//	private static enum ExpType{unset, constant, notAconstant, variable, binaryOperation, notAbinaryOperation, unaryOperation, notAnUnaryOperation};
//	private boolean typeFinallySet = false;
//	private ExpType expType = ExpType.unset;
//
//	// List supports random access: get(i)
//	// while Set -- doesn't
//	public ArrayList<String> getVariables() {
//		if (coords == null) {
//Exception e = new Exception();
//e.printStackTrace();
//			coords = new ArrayList<String>();
//			coords.addAll(getVariables(this));
//			Collections.sort(coords);  // sorting will ensure that if f=x1+x0, x0 is var #0, and x1 is var #1.
//										// w/o sorting the numbers of vars depends on its position in expression
//			
//			if (coords.size() == 0) { //nvp 1/7/2012: there are no variables: f=1.0
//				coords.add("0xDEADBEEF"); // just a placeholder to make the size not be zero
//											// Otherwise gradients crash
//			}
//		}
//		return coords;
//	}
//	/*
//	 * when the expression is calculated root node should set ALL
//	 * variables to all subExpressions, otherwise exp.getVariable() will
//	 * return different numbers depending on which part of sub expression it is call
//	 */
//	void setVariablesList() {
////		assert(coords == null); // WTF
////		assert(!isVariable() && !isConstant()); // WTF??
//		ArrayList<String> vars = getVariables();
//		setVariablesList(vars);
//	}
//	private void setVariablesList(ArrayList<String> vars) {
//		coords = vars;
//		Expression r = getRightExpression();
//		Expression l = getLeftExpression();
//		if (r  != null) 
//			r.setVariablesList(vars);
//		if (l != null)
//			l.setVariablesList(vars);
//	}
//
//	/*
//	 * This is a private method used by public @getCoords()@.
//	 * This function traverses the expression tree @expr@
//	 * and returns an array of Strings with names of variables
//	 * example: for xyz0 + 0 - x^y * sin(x) should return
//	 * <x, xyz0, y>.
//	 * TreeSet is a sorted collection without duplicates  
//	 */
//	private static TreeSet<String> getVariables(Expression expr) {
//		TreeSet<String> variablesLeft = new TreeSet<String>();
//		TreeSet<String> variablesRight;
//		
//		// recursion: go as deep as possible		
//
//		// process all lefts
//		if (expr.getLeftExpression() != null) {
//			variablesLeft.addAll(getVariables(expr.getLeftExpression()));
//		}
//
//		// process itself
//		if (expr.getOperation() != null) {
//			if (expr.isVariable()) {
//				// we have found a variable
//				// TreeSet will not insert duplicated value so we just add it
//				// nothing will added if that variable is already in the set
//				variablesLeft.add(expr.getOperation());
//			}
//		}
//
//		// process rights
//		if (expr.getRightExpression() != null) {
//			variablesRight = getVariables(expr.getRightExpression());
//			variablesLeft.addAll(variablesRight); // no duplicates and auto-sorting thanks to TreeSet
//		}
//		return variablesLeft;
//	}
//
//
//		
//	/*
//	 * determining type of expression
//	 */
//	public boolean isConstant() {
//		if (typeFinallySet)
//			if(expType == ExpType.constant)
//				return true;
//			else
//				return false;
//		if(expType == ExpType.notAconstant)
//			return false;		
//		try {
//			if (getOperation().equals("pi") ||
//					Double.valueOf(getOperation()) == 0) {
//				; // do nothing
//			}
//			// this construction means that either the value is PI or
//			// it is ANY number 
//			// all other cases will cause exception
//			expType = ExpType.constant;
//			typeFinallySet = true;
//		} catch (Exception e) {
//			expType = ExpType.notAconstant;
//		}
//		return expType == ExpType.constant;
//	}
//	public boolean isVariable() {
//		return !(isOperation() || isConstant());
//	}
//	public boolean isOperation() {
//		return isBinaryOperation() || isUnaryOperation();
//	}
//	// checking if op at the top of the branch is unary operation
//	public boolean isUnaryOperation() {
//		if (typeFinallySet)
//			if(expType == ExpType.unaryOperation)
//				return true;
//			else
//				return false;
//		if(expType == ExpType.notAnUnaryOperation)
//			return false;
//		boolean isUnary = isOperationInternal(getOperation(), unary_operations);
//		if (!isUnary)
//			expType = ExpType.notAnUnaryOperation;
//		else {
//			expType = ExpType.unaryOperation;
//			typeFinallySet = true;
//		}
//		return expType == ExpType.unaryOperation;
//	}
//	// checking if op at the top of the branch is binary operation
//	public boolean isBinaryOperation() {
//		if (typeFinallySet)
//			if(expType == ExpType.binaryOperation)
//				return true;
//			else
//				return false;
//		if(expType == ExpType.notAbinaryOperation)
//			return false;
//		boolean isBinary = isOperationInternal(getOperation(), binary_operations);
//		if (!isBinary)
//			expType = ExpType.notAbinaryOperation;
//		else {
//			expType = ExpType.binaryOperation;
//			typeFinallySet = true;
//		}
//		return expType == ExpType.binaryOperation;		
//	}
//	public boolean isAdd() {
//		return op.equals("+");
//	}
//	public boolean isSub() {
//		return op.equals("-");
//	}
//	public boolean isMul() {
//		return op.equals("*");
//	}
//	public boolean isDiv() {
//		return op.equals("/");
//	}
//	public boolean isPow() {
//		return op.equals("^");
//	}
//	public boolean isNegate() {
//		return op.equals("negate");
//	}
//	
//	// also used by StringParser. Thats why it receives operation as string	
//	protected static boolean isOperationInternal(String thisOp, String[] operations) {
//		// Honest checking. 
//		int i;
//		for (i = 0; i < operations.length; i++) {
//			if (thisOp.equals(operations[i])) {
//				return true;
//			}
//		}
//		return false;
//	}
//	private void addExpressions(Expression lhs, Expression rhs, String op) {
//		this.op = op;
//		this.left = lhs;
//		this.right = rhs;
//		typeFinallySet = false;
//		expType = ExpType.unset;
//	}
//
//	/*
//	 *  printing staff
//	 */
//	@Override
//	public String toString() {
//		StringBuffer out = printExpression(this);
//		removeExtraBrackets(out);
//		return out.toString();
//	}
//	private static StringBuffer printExpression(Expression e) {
//		StringBuffer out = new StringBuffer();
//		return printExpression(e, Integer.MAX_VALUE, out);
//	}
//	private static StringBuffer printExpression(Expression e, int priorityOfHigherOp, StringBuffer out) {
//		if (e == null)
//			return out;
//		String strOperation = e.getOperation();
//		int thisPriority = priorityOfHigherOp;
//		if(e.isOperation() )
//			thisPriority = RPN.prior(strOperation);
//		
//		if (e.isBinaryOperation() && thisPriority < priorityOfHigherOp) {
//			out.append("(");
//		}
//		printExpression(e.getLeftExpression(), thisPriority, out);
//
//		// already print left branch, now is going to print this node
//		// if current operation is unary it requires brackets before the argument (right part)
//		boolean unary = false;
//		if (e.isUnaryOperation() ) {
//			unary = true;
//			if ("negate".equals(strOperation) ) {
//				strOperation = "-";
//				unary = false;
//			}
//		}
//
//		out.append(strOperation);
//		
//		if (unary)
//			out.append("(");
//		printExpression(e.getRightExpression(), thisPriority, out);
//		if (unary)
//			out.append(")");
//		else if (e.isBinaryOperation() && thisPriority < priorityOfHigherOp) {
//			out.append(")");
//		}
//		return out;
//	}
//	private static void removeExtraBrackets(StringBuffer out) {
//		// (x binaryOp y)
//		if (out.charAt(0) == '(') {
//			out.deleteCharAt(0);
//			out.deleteCharAt(out.length()-1);
//		}		
//	}
//	public String toStringGraph() {
//		final int rootPos = 30;
//		int step = 4;
//		StringBuffer out = new StringBuffer();
//		// actually we are going to implement a sort of BFS: breadth-first search
//		Deque/*LinkedList*/<Expression> nodes = new LinkedList<Expression>();
//		Deque/*LinkedList*/<Expression> nextLevel = new LinkedList<Expression>();
//		Deque/*LinkedList*/<Integer> positions = new LinkedList<Integer>();
//		Deque/*LinkedList*/<Integer> nextLevelPositions = new LinkedList<Integer>();
//		nodes.addLast(this);
//		positions.addLast(rootPos);
//		
//		// for each node on this level
//		// 0. cout node with its offset 
//		// 1. see how many children has each node on this level
//		// 2. compute appropriate position in the output string
//		//    for each child
//		// 3. add children to next level list.  
//		while (true){
//			int prevPos = 0;
//			while(nodes.size() != 0) {
//				Expression node = nodes.removeFirst();
//				int pos = positions.removeFirst();
//				putSimbolInStr(out, node.getOperation(), pos-prevPos);
//				prevPos = pos+1;
//				
//				Expression l = node.getLeftExpression();
//				Expression r = node.getRightExpression();
//				if (l != null) {
//					nextLevel.addLast(l);
//					if (pos - step <= 0) {
//						out.append("Too wide graph. Truncated.. ");
//						return out.toString();
//					}
//					nextLevelPositions.addLast(pos - step);
//				}
//				if (r != null) {
//					nextLevel.addLast(r);
//					nextLevelPositions.addLast(pos + step);
//				}
//			}
//			if (nextLevel.size() == 0)
//				break;
//			
//			//moveToNextLevel(nodes, nextLevel, positions, nextLevelPositions);
//			out.append("\n");
//			if (step > 1)
//				step--;
//			Deque tmp;
//			tmp = nodes;
//			nodes = nextLevel;
//			nextLevel = tmp;
//			tmp = positions;
//			positions = nextLevelPositions;
//			nextLevelPositions = tmp;			
//		}
//		return out.toString();		
//	}		
//	private static void putSimbolInStr(StringBuffer out, String mnemonic, int pos) {
//		for (int i = 0; i < pos; i++)
//			out.append(" ");
//		out.append(mnemonic);		
//	}
///*
//	@SuppressWarnings("unchecked")
//	private static void moveToNextLevel(Collection<Expression> nodes, 
//					Collection<Expression> nextLevel, 
//					Collection<Integer> positions, 
//					Collection<Integer> nextLevelPositions) {
//		Collection tmp;
//		tmp = nodes;
//		nodes = nextLevel;
//		nextLevel = tmp;
//		tmp = positions;
//		positions = nextLevelPositions;
//		nextLevelPositions = tmp;
//	}	
//*/
//	
//	public boolean hasVar(String var) {
//		boolean result = false;
//		if (left != null) {
//			result = result || _hasVar(left, var);
//		}
//		if (op != null) {
//			result = result || op.equalsIgnoreCase(var);
//		}
//		if (right != null) {
//			result = result || _hasVar(right, var);
//		}
//		return result;
//	}
//
//	private boolean _hasVar(Expression exp, String var) {
//		boolean result = false;
//		if (exp.getLeftExpression() != null) {
//			result = result || exp.getLeftExpression().hasVar(var);
//		}
//		if (exp.getOperation() != null) {
//			result = result || exp.getOperation().equalsIgnoreCase(var);
//		}
//		if (exp.getRightExpression() != null) {
//			result = result || exp.getRightExpression().hasVar(var);
//		}
//		return result;
//	}
//
//	/*
//	 * constructors and factory
//	 */
//	public Expression(RPN rpn) throws ExpressionException {
//		init(rpn);
//	}
//	public Expression(String exp) throws ExpressionException {
//		RPN rpn = new RPN(exp);
//		init(rpn);
//	}
//	protected Expression() {
//		
//	}
//	public static Expression newConstant(double constant) {
//		Expression exp = new Expression();
//		exp.setConstantValue(constant);
//		return exp;
//	}
//	/*
//	public static Expression newVariable(String varName) {
//		Expression exp = new Expression();
//		exp.op = varName;
//		return exp;
//	}
//	*/
//	public static Expression newExpression(Expression left, Expression right, String op) {
//		Expression exp = new Expression();
//		exp.addExpressions(left, right, op);
//		return exp;
//	}
//	
//	
//
//	protected void init(RPN rpn) throws ExpressionException {
//		String[] srpn = rpn.getRPN();
//		int length = srpn.length;
//		op = srpn[length - 1];
//		if (isBinaryOperation()) {
//			left = new Expression(new RPN(rpn.find_operand(length - 1, false)));
//			right = new Expression(new RPN(rpn.find_operand(length - 1, true)));
//		} else if (isUnaryOperation()) {
//			right = new Expression(new RPN(rpn.find_operand(length - 1, true)));
//		}
//		else { // could only be a variable or constant
//			if (right != null) {
//				throw new IllegalStateException("Something wrong with the expression");
//			}
//			if (isVariable()) {
//				if ( op.contains("(") ) // user entered some function that we don't know. So we decided that this is a variable.
//					throw new UnsupportedFunction("Unsupported function " + op); // But variables can't contain brackets 
//			}
//		}
//		setVariablesList();
//		
//		//System.out.println("\n\n=======\n"+toString() + "\n\n" + toStringGraph() + "\n\n");
//		
//		try {
//			evaluate(new Box(this.getDimension(), new RealInterval(1)));
//			double p[] = new double[this.getDimension()]; // all zeroes
//			evaluate(p);
//		} catch (UnsupportedOperationException e) {
//			// there is at least one unsupported function
//			throw new UnsupportedFunction("Unsupported function " + op);
//		}
//	}
//
//	// return new object of expression which has the same
//	// operations/operands inside
//	@Override
//	public Expression clone() {
//		Expression c = null; 
//		try {
//			c = new Expression(toString());
//		} catch (ExpressionException e) {
//			// we are coping a proper expression so everything should be OK here.
//			e.printStackTrace();
//		}
//		return c;
//	}
//	
//
//	
//	/*
//	 * setters
//	 */
//	public void setTo(Expression e) {
//		setLeftExpression(e.getLeftExpression());
//		setRightExpression(e.getRightExpression());
//		setOperation(e.getOperation());
//		setMethod(e.getMethod());	
//		typeFinallySet = e.typeFinallySet;
//		expType = e.expType;
//	}
//	public void setTo(double d) {
//		setLeftExpression(null);
//		setRightExpression(null);
//		setConstantValue(d);
//	}
//	
//	public void setLeftExpression(Expression e) {
//		this.left = e;
//	}
//	public void setRightExpression(Expression e) {
//		this.right = e;
//	}
//	public void setOperation(String op) {
//		this.op = op;
//		typeFinallySet = false;
//		expType = ExpType.unset;
//	}
//	public void setConstantValue(double d) {
//		if (left != null || right != null)
//			throw new IllegalArgumentException("Node has chaild nodes.");
//		this.op = Double.toString(d);
//		typeFinallySet = true;
//		expType = ExpType.constant;
//	}
//	public void setMethod(Method m) {
//		this.method = m;
//	}
//
//	/*
//	 * getters
//	 */
//	public Expression getLeftExpression() {
//		return this.left;
//	}
//
//	public Expression getRightExpression() {
//		return this.right;
//	}
//
//	public String getOperation() {
//		return this.op;
//	}
//
//	public Method getMethod() {
//		return this.method;
//	}
//	
//	public double getConstantValue() {
//		if (!isConstant())
//			throw new IllegalStateException("Not a constant");
//		double d;
//		if (getOperation().compareTo("pi") == 0) {
//			d = Math.PI;
//		} else {
//			d = Double.valueOf(getOperation());
//		}
//		return d;
//	}
//	public String getMethodName() {
//		if (isUnaryOperation() || isBinaryOperation())
//			return getOperation();
//		throw new IllegalStateException("Not an operation");
//	}
//	
//	
//	
//	
//	///////////////////////////////////////////////////////////////////
//	// trying to get rid from MethodRunner
//	// and implement 
//	// evaluate()
//	///////////////////////////////////////////////////////////////////
//	
//	public RealInterval evaluate(Box area) {
//		assert(area.getDimension() == getVariables().size());
//		if (isConstant())
//			return new RealInterval(getConstantValue());
//		if (isVariable())
//			return area.getInterval(getVariableNumber());
//		// it is an expression
//		RealInterval l = null, r;
//		if (left != null) // left could be null for unary expressions
//			l = left.evaluate(area);
//		r = right.evaluate(area);
//		return evaluate(op, l, r);		
//	}
//	// evaluate for points
//	public double evaluate(double... point) {
//		assert(point.length == getVariables().size());
//		if (isConstant())
//			return getConstantValue();
//		if (isVariable())
//			return point[getVariableNumber()];
//		// it is an expression
//		double l = Double.NaN, r;
//		if (left != null) // left could be null for unary expressions
//			l = left.evaluate(point);
//		r = right.evaluate(point);
//		return evaluate(op, l, r);		
//	}
//	private static RealInterval evaluate(String op, RealInterval l, RealInterval r) {
//		switch (op) {
//			case "+":
//				return add(l, r);
//			case "-":
//				return sub(l, r);
//			case "*":
//				return mul(l, r);
//			case "/":
//				return div(l, r);
//			case "^":
//				if (l.contains(0)) // @power@ doesn't allow 0 in argument
//					return intPow(l, r);
//				return power(l, r);
//				
//			case "negate":
//				return negate(r);
//			case "arccos":
//				return arccos(r);
//			case "arcsin":
//				return arcsin(r);
//			case "arctg":
//				return arctg(r);
//			case "arcctg":
//				return arcctg(r);
//			case "sin":
//				return sin(r);
//			case "cos":
//				return cos(r);
//			case "tg":
//				return tg(r);
//			case "ctg":
//				return ctg(r);
//			case "ln":
//				return ln(r);
//			case "exp":
//				return exp(r);
//			case "sqrt":
//				return sqrt(r);
//	
//			default:
//				throw new UnsupportedOperationException("Unsupported operation for intervals: " + op);
//		}
//	}	
//	private static double evaluate(String op, double l, double r) {
//		switch (op) {
//			case "+":
//				return l + r;
//			case "-":
//				return l - r;
//			case "*":
//				return (l * r);
//			case "/":
//				return (l / r);
//			case "^":
//				return Math.pow(l, r);
//				
//			case "negate":
//				return -(r);
//			case "arccos":
//				return Math.acos(r);
//			case "arcsin":
//				return Math.asin(r);
//			case "arctg":
//				return Math.atan(r);
//			//case "arcctg":
//				//
//			case "sin":
//				return Math.sin(r);
//			case "cos":
//				return Math.cos(r);
//			case "tg":
//				return Math.tan(r);
//			//case "ctg":
//				//
//			case "ln":
//				return Math.log(r);
//			case "exp":
//				return Math.exp(r);
//			case "sqrt":
//				return Math.sqrt(r);
//	
//			default:
//				throw new UnsupportedOperationException("Unsupported operation for doubles: " + op);
//		}
//	}
//	public int getVariableNumber() {
//		return getVariables().indexOf(getOperation());
//	}
//	public int getDimension() {
//		return numOfVars();
//	}
//	public int numOfVars() {
//		return getVariables().size();
//	}
//}