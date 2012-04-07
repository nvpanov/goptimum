package symboldiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import symboldiff.exceptions.DifferentiateExpression;
import symboldiff.exceptions.ExpressionException;
import symboldiff.exceptions.IncorrectExpression;
import static symboldiff.Expression.*;

public class Gradient {

	//private Expression gradient;
	private Expression[] pder;
	private static final String needCopy = "*/^";

	// private Vector<String> coords;

	// return derivative of function with arguments = arg
	// it is easier to add throws declaration that insert number of try/catch's
	private Expression derivativeOfFunction(String op, Expression arg)
			throws ExpressionException {
		if (op.equalsIgnoreCase("arccos")) {
			return new Expression(new RPN("(0-1)/sqrt(1 - " + arg.toString()
					+ "^2)"));
		}
		if (op.equalsIgnoreCase("arcsin")) {
			return new Expression(new RPN("1/sqrt(1 - " + arg.toString()
					+ "^2)"));
		}
		if (op.equalsIgnoreCase("arcctg")) {
			return new Expression(new RPN("(0-1)/(1 + " + arg.toString()
					+ "^2)"));
		}
		if (op.equalsIgnoreCase("arctg")) {
			return new Expression(new RPN("1/(1 + " + arg.toString() + "^2)"));
		}
		if (op.equalsIgnoreCase("cos")) {
			return new Expression(new RPN("(0-1)*sin(" + arg.toString() + ")"));
		}
		if (op.equalsIgnoreCase("sin")) {
			return new Expression(new RPN("cos(" + arg.toString() + ")"));
		}
		if (op.equalsIgnoreCase("ctg")) {
			return new Expression(new RPN("(0-1)/(sin(" + arg.toString()
					+ "))^2"));
		}
		if (op.equalsIgnoreCase("tg")) {
			return new Expression(new RPN("1/(cos(" + arg.toString() + "))^2"));
		}
		if (op.equalsIgnoreCase("ln")) {
			return new Expression(new RPN("1/(" + arg.toString() + ")"));
		}
		if (op.equalsIgnoreCase("exp")) {
			return new Expression(new RPN("exp(" + arg.toString() + ")"));
		}
		if (op.equalsIgnoreCase("sqrt")) {
			return new Expression(new RPN("1/(2*(sqrt(" + arg.toString()
					+ ")))"));
		}
		if (op.equals("negate")) {
			return new Expression(new RPN("-1"));
		}
		throw new RuntimeException(
				"Cannot calculate derivative for unknown function: \t" + op
						+ "\n");
	}

	private Expression derivativeOfOperation(Expression exp, String coord) throws ExpressionException {
		Expression left;// = new Expression();
		Expression right;// = new Expression();
		Expression parent = null;// = new Expression();

		try {
			if (exp.getOperation().equals("/")
					&& !exp.getRightExpression().hasVar(coord)) {
				// parent.addExpressions(calculatePartialDerivative(exp.getLeftExpression(),
				// coord),
				// exp.getRightExpression(), "/");
				Expression lTmp = calculatePartialDerivative(
						exp.getLeftExpression(), coord);
				parent = newExpression(lTmp, exp.getRightExpression(), "/");
			}
			// f(x)*g(x) = f'(x)*g(x) + f(x)*g'(x)
			// f(x)/g(x) = (f'(x)*g(x) - f(x)*g'(x))/g^2(x)
			else if (exp.getOperation().equals("*")
					|| exp.getOperation().equals("/")) {
				// g(x)*f'(x)
				if (exp.getLeftExpression().hasVar(coord)) {
					left = newExpression(
							exp.getRightExpression().clone(),
							calculatePartialDerivative(exp.getLeftExpression()
									.clone(), coord), "*");
				} else {
					// left.setOperation("0");
					left = newConstant(0);
				}
				// f(x)*g(x)
				if (exp.getRightExpression().hasVar(coord)) {
					right = newExpression(
							calculatePartialDerivative(exp.getRightExpression()
									.clone(), coord), exp.getLeftExpression()
									.clone(), "*");
				} else {
					// right.setOperation("0");
					right = newConstant(0);
				}
				// link lhs and rhs
				if (exp.getOperation().equals("*")) {
					// parent.addExpressions(left, right, "+");
					parent = newExpression(left, right, "+");
				} else {
					/*
					 * Expression left1 = new Expression(); Expression right1 =
					 * new Expression(); left1.addExpressions(left, right, "-");
					 * right1.addExpressions(exp.getRightExpression(), new
					 * Expression(new RPN("2")), "^");
					 * parent.addExpressions(left1, right1, "/");
					 */
					Expression left1 = newExpression(left, right, "-");
					Expression right1 = newExpression(exp.getRightExpression(),
							newConstant(2), "^");
					parent = newExpression(left1, right1, "/");
				}
			} else if (exp.getOperation().equals("^")) {
				if (!exp.getRightExpression().hasVar(coord)) {  /// f(x)^const 
					Expression pow = new Expression(exp.getRightExpression()
							.toString() + "-1");
					/*
					 * parent.setOperation("^");
					 * right.addExpressions(exp.getLeftExpression(), pow, "^");
					 * parent.addExpressions(exp.getRightExpression(), right,
					 * "*");
					 */
					Expression tmp = newExpression(exp.getLeftExpression()
							.clone(), pow, "^");
					Expression calculatedLeftValue = calculatePartialDerivative(
							exp.getLeftExpression(), coord);
					left = newExpression(calculatedLeftValue, tmp, "*");
					parent = newExpression(exp.getRightExpression(), left, "*");

				} /*
				 * else { Expression coef = new Expression(new RPN("ln(" +
				 * exp.getRightExpression().toString() + ") + 1"));
				 * parent.setOperation("^"); parent.addExpressions(coef, exp,
				 * "*"); }
				 */

				//__ nvp 3/18/12
				else if (!exp.getLeftExpression().hasVar(coord)) { 
					// (constant^f(x))' = ln(constant)*constant^f(x) * f(x)'
					String constant = exp.getLeftExpression().toString();
					Expression coef = new Expression("ln(" + constant + ")*(" + 
											constant + ")^(" + exp.getRightExpression().toString()+")");
					Expression deriv = calculatePartialDerivative(exp.getRightExpression().clone(), coord);
					parent = newExpression(coef, deriv, "*");
				}
				// ^^
				else { // both sides
					// (f(x)^g(x))' = (g'(x)*ln(f(x)) + f'(x)*g(x)/f(x))*f(x)^g(x)
					//                                  ^^^^ !! nvp 3/19/12
					Expression f = exp.getLeftExpression();
					Expression g = exp.getRightExpression();

					Expression df = calculatePartialDerivative(f, coord);
					Expression dg = calculatePartialDerivative(g, coord);

					Expression ln_f  = newExpression(null,      f.clone(), "ln");
					Expression gDIVf = newExpression(g.clone(), f.clone(), "/");
					
					Expression leftSum  = newExpression(dg, ln_f,  "*");
					Expression rightSum = newExpression(df, gDIVf, "*");
					Expression sum = newExpression(leftSum, rightSum, "+");
					Expression fPOWg = newExpression(f, g, "^");
					parent = newExpression(sum, fPOWg, "*");
				}
			}
		} catch (IncorrectExpression e) {
			// RPN(String) can throw an exception.
			// Here it should never happen.
			e.printStackTrace();
		}

		return parent;
	}

	private Expression calculatePartialDerivative(Expression exp, String coord) throws ExpressionException {
		Expression left = exp.getLeftExpression();
		Expression right = exp.getRightExpression();
		Expression parent;// = new Expression();
		if (!exp.hasVar(coord)) {
			exp = Expression.newConstant(0);
			return exp;
		}
		if (exp.isBinaryOperation()) {
			// for some kind of operations we have to copy left and right
			// branches.
			if (this.needCopy.contains(exp.getOperation())) {
				exp = derivativeOfOperation(exp, coord);
			} else {
				left = calculatePartialDerivative(left, coord);
				right = calculatePartialDerivative(right, coord);
				exp.setLeftExpression(left);
				exp.setRightExpression(right);
			}
		} else if (exp.isUnaryOperation()) {
			Expression arg = right.clone();
			Expression tmpLeft = null, tmpRight = null;
			// parent.setOperation("*");
			try {
				// parent.setRightExpression(functionDerivative(exp.getOperation(),
				// arg));
				tmpLeft = derivativeOfFunction(exp.getOperation(), arg);
			} catch (IncorrectExpression e) {
				// private functionDerivative marked as throwing
				// IncorrectExpression for more compact code
				// actually no exception should be thrown
			}
			// parent.setLeftExpression(calculatePartialDerivative(arg.clone(),
			// coord));
			tmpRight = calculatePartialDerivative(arg.clone(), coord);
			parent = newExpression(tmpLeft, tmpRight, "*");
			exp = parent;
		}
		// hit some constant or variable
		else {
			// exp.setOperation((exp.hasVar(coord)) ? "1" : "0");
			exp = Expression.newConstant(1);
		}
		return exp;
	}
/*
	public Expression getGradient() {
		return this.gradient;
	}
*/
	public Expression getPartialDerivative(int i) {
		return this.pder[i];
	}

	/* calculates gradient for @exp@ using given set of variables.
	 * it should be used for second derivative because some variables can be missed in first derivative:
	 * f=x2+y   =>   df/dx = 2x, df/dy = 1   =>   d2f/dx = 2, and nobody will differentiate by y -- there is no such variable   
	 */
	public Gradient(Expression exp, List<String> coords) throws DifferentiateExpression {
		init(exp, coords);
	}

	/*
	 * calculates gradient (sum of all partial derivatives by all variables that are in the expression)
	 * use Gradient(exp, coords) to calculate Gradient for given variables
	 */
	public Gradient(Expression exp) throws DifferentiateExpression {
		List<String> coords = exp.getVariables();
		init(exp, coords);
	}
	/*
	 * First constructs an @Expression@ from @formula@ than acts absolutely the same as
	 * @Gradient(Expression exp)@. Throws Exception if fails to parse @formula@  
	 */
	public Gradient(String formula) throws ExpressionException {
		Expression exp = new Expression(formula);
		List<String> coords = exp.getVariables();
		init(exp, coords);
	}
	
	/*
	 * this constructor accepts gradient so it is useful for second derivatives
	 * do not forget to specify ORIGINAL variables list 
	 */
	public Gradient(Gradient g, ArrayList<String> variables) throws ExpressionException {
		Expression exp = null;
		exp = new Expression(g.toString());
		init(exp, variables);
	}

	private void init(Expression exp, List<String> coords) throws DifferentiateExpression {
		int i;
		final int n = coords.size();
		this.pder = new Expression[n];
		for (i = 0; i < n; i++) {
			try {
				this.pder[i] = calculatePartialDerivative(exp.clone(), coords.get(i));
			} catch (ExpressionException e) {
				throw new DifferentiateExpression("Can't calculate partial derivative: " + e.getMessage());
			}
		}
//		for (i = 0; i < n; i++) {
//			Simplifier.simplify(pder[i]);
//		}
		
		for (i = 0; i < n; i++) {
			pder[i].setVariablesList();
		}			
	}
	

	String g = null;
	@Override
	public String toString() {
		if(g==null) {
			StringBuffer sb = new StringBuffer();
			int i;
			for (i = 0; i < pder.length-1; i++) {
				sb.append(pder[i].toString()).append("+");
			}
			sb.append(pder[i].toString());
			g = sb.toString();
		}
		return g;
	}
}
