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

import com.sun.org.apache.bcel.internal.generic.ISUB;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import static net.sourceforge.interval.ia_math.IAMath.*;

import core.Box;

import symboldiff.exceptions.IncorrectExpression;

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

	private ArrayList<String> coords;

	// List supports random access: get(i)
	// while Set -- doesn't
	public ArrayList<String> getVariables() {
		if (coords == null) {
// 			assert( !isVariable() ); // WTF??
			coords = new ArrayList<String>();
			coords.addAll(getVariables(this));
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
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean isVariable() {
		return !(isOperation() || isConstant());
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
		removeExtraBrackets(out);
		return out.toString();
	}
	private static StringBuffer printExpression(Expression e/*, int priorityOfRoot*/) {
		StringBuffer out = new StringBuffer();
		if (e == null)
			return out;
		
		if (e.isBinaryOperation()) {
			out.append("(");
		}
		out.append(printExpression(e.getLeftExpression()));

		// already print left branch, now is going to print this node
		// if t unary operation it requires brackets before the argument (right part)
		String strOperation = e.getOperation();

		boolean needsBrackets = false;
		if (e.isUnaryOperation() ) {
			needsBrackets = true;
			if ("negate".equals(strOperation) ) {
				strOperation = "-";
				needsBrackets = false;
			}
		}

		out.append(strOperation);
		
		if (needsBrackets) {
			out.append("(");
		}
		out.append(printExpression(e.getRightExpression()));
		if (needsBrackets) {
			out.append(")");
		}
		if (e.isBinaryOperation()) {
			out.append(")");
		}
		return out;
	}
	private static void removeExtraBrackets(StringBuffer out) {
		// (x binaryOp y)
		if (out.charAt(0) == '(') {
			out.deleteCharAt(0);
			out.deleteCharAt(out.length()-1);
		}		
	}
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
	public Expression(RPN rpn) {
		init(rpn);
	}
	public Expression(String exp) throws IncorrectExpression {
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
	public static Expression newExpression(Expression left, Expression right, String op) {
		Expression exp = new Expression();
		exp.addExpressions(left, right, op);
		return exp;
	}
	
	

	protected void init(RPN rpn) {
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
			if (right != null) {
				throw new IllegalStateException("Something wrong with the expression");
			}
		}
		setVariablesList();
	}

	// return new object of expression which has the same
	// operations/operands inside
	@Override
	public Expression clone() {
		Expression c = null; 
		try {
			c = new Expression(new RPN(toString()));
		} catch (IncorrectExpression e) {
			// we are coping proper expression so everything should be OK here.
			e.printStackTrace();
		}
		return c;
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
		setOperation(""+d);
		setMethod(null);		
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
			return area.getInterval(getVariableNumber());
		// it is an expression
		RealInterval l = null, r;
		if (left != null) // left could be null for unary expressions
			l = left.evaluate(area);
		r = right.evaluate(area);
		return evaluate(op, l, r);		
	}
	private static RealInterval evaluate(String op, RealInterval l, RealInterval r) {
		/*
		switch (op) {
			case "+":
				return add(l, r);
			case "-":
				return sub(l, r);
			case "*":
				return mul(l, r);
			case "\\":
				return div(l, r);
				
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
				throw new UnsupportedOperationException("Unknown operation: " + op);
		}
		*/
		return null;
	}

	public int getVariableNumber() {
		return getVariables().indexOf(getOperation());
	}
	
}