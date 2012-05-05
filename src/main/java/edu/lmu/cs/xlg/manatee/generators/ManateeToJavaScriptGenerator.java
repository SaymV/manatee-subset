package edu.lmu.cs.xlg.manatee.generators;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.lmu.cs.xlg.manatee.entities.ArrayConstructor;
import edu.lmu.cs.xlg.manatee.entities.AssignmentStatement;
import edu.lmu.cs.xlg.manatee.entities.BinaryExpression;
import edu.lmu.cs.xlg.manatee.entities.Block;
import edu.lmu.cs.xlg.manatee.entities.BooleanLiteral;
import edu.lmu.cs.xlg.manatee.entities.CallStatement;
import edu.lmu.cs.xlg.manatee.entities.CharacterLiteral;
import edu.lmu.cs.xlg.manatee.entities.CollectionLoop;
import edu.lmu.cs.xlg.manatee.entities.ConditionalStatement;
import edu.lmu.cs.xlg.manatee.entities.ConditionalStatement.Arm;
import edu.lmu.cs.xlg.manatee.entities.Declaration;
import edu.lmu.cs.xlg.manatee.entities.DecrementStatement;
import edu.lmu.cs.xlg.manatee.entities.DoNothingStatement;
import edu.lmu.cs.xlg.manatee.entities.ExitStatement;
import edu.lmu.cs.xlg.manatee.entities.Expression;
import edu.lmu.cs.xlg.manatee.entities.FunctionCall;
import edu.lmu.cs.xlg.manatee.entities.IdentifierExpression;
import edu.lmu.cs.xlg.manatee.entities.IncrementStatement;
import edu.lmu.cs.xlg.manatee.entities.Literal;
import edu.lmu.cs.xlg.manatee.entities.ModifiedStatement;
import edu.lmu.cs.xlg.manatee.entities.NullLiteral;
import edu.lmu.cs.xlg.manatee.entities.NumberLiteral;
import edu.lmu.cs.xlg.manatee.entities.PlainLoop;
import edu.lmu.cs.xlg.manatee.entities.RangeLoop;
import edu.lmu.cs.xlg.manatee.entities.ReadStatement;
import edu.lmu.cs.xlg.manatee.entities.ReturnStatement;
import edu.lmu.cs.xlg.manatee.entities.Script;
import edu.lmu.cs.xlg.manatee.entities.Statement;
import edu.lmu.cs.xlg.manatee.entities.StringLiteral;
import edu.lmu.cs.xlg.manatee.entities.Subroutine;
import edu.lmu.cs.xlg.manatee.entities.SubscriptExpression;
import edu.lmu.cs.xlg.manatee.entities.TimesLoop;
import edu.lmu.cs.xlg.manatee.entities.Type;
import edu.lmu.cs.xlg.manatee.entities.TryStatement;
import edu.lmu.cs.xlg.manatee.entities.UnaryExpression;
import edu.lmu.cs.xlg.manatee.entities.Variable;
import edu.lmu.cs.xlg.manatee.entities.WhileLoop;
import edu.lmu.cs.xlg.manatee.entities.WholeNumberLiteral;
import edu.lmu.cs.xlg.manatee.entities.WriteStatement;
import edu.lmu.cs.xlg.manatee.entities.ConditionalStatement.Arm;

/**
 * A generator that translates an Manatee program into JavaScript.
 */
public class ManateeToJavaScriptGenerator extends Generator {

    @Override
    public void generate(Script script, PrintWriter writer) {
        this.writer = writer;

        emit("(function () {");
        generateBlock(script);
        emit("}());");
    }

    /**
     * Emits JavaScript code for the given Manatee block.
     */
    private void generateBlock(Block block) {
        indentLevel++;
        for (Statement s: block.getStatements()) {
            generateStatement(s);
        }
        indentLevel--;
    }

    /**
     * Emits JavaScript code for the given Manatee statement.
     */
    private void generateStatement(Statement s) {
        if (s instanceof Declaration) {
            generateDeclaration(Declaration.class.cast(s));

        } else if (s instanceof DoNothingStatement) {
            emit(";");

        } else if (s instanceof AssignmentStatement) {
            AssignmentStatement a = AssignmentStatement.class.cast(s);
            Variable v = new Variable("srcguard", Type.ARBITRARY);
            String target, source;
            if (a.getTargetLength() > 1) {
                for (int i = 0; i < a.getTargetLength(); i++) {
                    target = id(v) + "_" + i;
                    source = generateExpression(a.getSource(i));
                    emit(String.format("%s = %s;", target, source));
                }
                for (int i = 0; i < a.getTargetLength(); i++) {
                    target = generateExpression(a.getTarget(i));
                    source = id(v) + "_" + i;
                    emit(String.format("%s = %s;", target, source));
                }
            } else {
                target = generateExpression(a.getTarget(0));
                source = generateExpression(a.getSource(0));
                emit(String.format("%s = %s;", target, source));
            }
        } else if (s instanceof ReadStatement) {
            // What exactly does the ReadStatement do?
            Expression e = ReadStatement.class.cast(s).getExpression();
            emit("console.log(" + generateExpression(e) + ");");

        } else if (s instanceof WriteStatement) {
            Expression e = WriteStatement.class.cast(s).getExpression();
            emit("console.log(" + generateExpression(e) + ");");

        } else if (s instanceof ExitStatement) {
            emit("break;");

        } else if (s instanceof ReturnStatement) {
            Expression e = ReturnStatement.class.cast(s).getExpression();
            emit("return" + (e == null ? "" : " " + generateExpression(e)) + ";");

        } else if (s instanceof CallStatement) {
            CallStatement call = CallStatement.class.cast(s);
            String procedure = id(call.getProcedure());
            List<String> arguments = new ArrayList<String>();
            for (Expression argument: call.getArgs()) {
                arguments.add(generateExpression(argument));
            }
            emit(String.format("%s(%s)", procedure, StringUtils.join(arguments, ", ")));

        } else if (s instanceof ModifiedStatement) {
            ModifiedStatement m = ModifiedStatement.class.cast(s);
            String key;
            switch (m.getModifier().getType()) {
            case IF: key = "if"; break;
            case WHILE: key = "while"; break;
            default: throw new RuntimeException("Internal error: unknown modifier");
            }
            String condition = generateExpression(m.getModifier().getCondition());
            emit(String.format("%s (%s) {", key, condition));
            indentLevel++;
            generateStatement(m.getStatement());
            indentLevel--;
            emit("}");

        } else if (s instanceof ConditionalStatement) {
            generateConditionalStatement(ConditionalStatement.class.cast(s));

        } else if (s instanceof PlainLoop) {
            emit("while (true) {");
            generateBlock(PlainLoop.class.cast(s).getBody());
            emit("}");

        } else if (s instanceof TimesLoop) {
            TimesLoop loop = TimesLoop.class.cast(s);
            Variable counter = new Variable("", Type.WHOLE_NUMBER);
            String count = generateExpression(loop.getCount());
            emit(String.format("for (var %s = %s; %s > 0; %s--) {",
                    id(counter), count, id(counter), id(counter)));
            generateBlock(loop.getBody());
            emit("}");

        } else if (s instanceof CollectionLoop) {
            CollectionLoop loop = CollectionLoop.class.cast(s);
            String index = id(loop.getIterator());
            String collection = generateExpression(loop.getCollection());
            emit(String.format("%s%s.forEach(function (%s) {",
                    collection,
                    loop.getCollection().getType() == Type.STRING ? ".split('')" : "",
                    index));
            generateBlock(loop.getBody());
            emit("});");

        } else if (s instanceof RangeLoop) {
            RangeLoop loop = RangeLoop.class.cast(s);
            String index = id(loop.getIterator());
            String low = generateExpression(loop.getLow());
            String high = generateExpression(loop.getHigh());
            String step = loop.getStep() == null ? "1" : generateExpression(loop.getStep());
            emit(String.format("for (var %s = %s; %s <= %s; %s += %s) {",
                    index, low, index, high, index, step));
            generateBlock(loop.getBody());
            emit("}");

        } else if (s instanceof WhileLoop) {
            WhileLoop loop = WhileLoop.class.cast(s);
            emit("while (" + generateExpression(loop.getCondition()) + ") {");
            generateBlock(loop.getBody());
            emit("}");
        } else if (s instanceof DecrementStatement) {
            DecrementStatement ds = DecrementStatement.class.cast(s);
            String target = generateExpression(ds.getTarget());
            String delta = generateExpression(ds.getDelta());
            emit(String.format("%s -= %s;", target, delta));
        } else if (s instanceof IncrementStatement) {
            IncrementStatement is = IncrementStatement.class.cast(s);
            String target = generateExpression(is.getTarget());
            String delta = generateExpression(is.getDelta());
            emit(String.format("%s += %s;", target, delta));
        } else if (s instanceof TryStatement) {
            TryStatement ts = TryStatement.class.cast(s);
            Block tryBlock = ts.getTryBlock();
            Block recoverBlock = ts.getRecoverBlock();
            emit("try {");
            generateBlock(tryBlock);
            emit("} catch (e) {");
            generateBlock(recoverBlock);
            emit("}");
        }
    }
    
    /**
     * Generates JavaScript code for conditional statement s.
     */
    private void generateConditionalStatement(ConditionalStatement s) {

        boolean firstArm = true;
        for (Arm arm: s.getArms()) {
            String lead = firstArm ? "if" : "} else if";
            emit(lead + " (" + generateExpression(arm.getCondition()) + ") {");
            generateBlock(arm.getBlock());
            firstArm = false;
        }
        if (s.getElsePart() != null) {
            emit("} else {");
            generateBlock(s.getElsePart());
        }
        emit("}");
    }

    /**
     * Returns a JavaScript expression for the given Manatee expression.
     */
    private String generateExpression(Expression e) {
        if (e instanceof Literal) {
            return generateLiteral(Literal.class.cast(e));

        } else if (e instanceof IdentifierExpression) {
            return id(IdentifierExpression.class.cast(e).getReferent());

        } else if (e instanceof UnaryExpression) {
            return generateUnaryExpression(UnaryExpression.class.cast(e));

        } else if (e instanceof BinaryExpression) {
            return generateBinaryExpression(BinaryExpression.class.cast(e));

        } else if (e instanceof ArrayConstructor) {
            List<String> values = new ArrayList<String>();
            for (Expression element: ArrayConstructor.class.cast(e).getExpressions()) {
                values.add(generateExpression(element));
            }
            return String.format("[%s]", StringUtils.join(values, ", "));

        } else if (e instanceof SubscriptExpression) {
            SubscriptExpression s = SubscriptExpression.class.cast(e);
            String base = generateExpression(s.getBase());
            String index = generateExpression(s.getIndex());
            return String.format("%s[%s]", base, index);

        } else if (e instanceof FunctionCall) {
            FunctionCall call = FunctionCall.class.cast(e);
            String f = generateExpression(call.getFunction());
            List<String> arguments = new ArrayList<String>();
            for (Expression a: call.getArgs()) {
                arguments.add(generateExpression(a));
            }
            return String.format("%s(%s)", f, StringUtils.join(arguments, ", "));

        } else {
            throw new RuntimeException("Internal Operator: statement");
        }
    }

    /**
     * Emits JavaScript code for the given Manatee declaration.
     */
    private void generateDeclaration(Declaration d) {

        if (d instanceof Variable) {
            Variable v = Variable.class.cast(d);
            if (v.getInitializer() == null) {
                emit("var " + id(d) + ";");
            } else {
                emit("var " + id(d) + " = " + generateExpression(v.getInitializer()) + ";");
            }

        } else {
            Subroutine s = Subroutine.class.cast(d);
            List<String> parameters = new ArrayList<String>();
            for (Variable v: s.getParameters()) {
                parameters.add(id(v));
            }
            emit(String.format("function %s(%s) {", id(s), StringUtils.join(parameters, ", ")));
            generateBlock(s.getBody());
            emit("}");
        }
    }

    private String generateLiteral(Literal e) {

        if (BooleanLiteral.FALSE.equals(e)) {
            return "false";

        } else if (BooleanLiteral.TRUE.equals(e)) {
            return "true";

        } else if (e instanceof CharacterLiteral) {
            // Note: Unicode escapes are not part of the subset language.
            return e.getLexeme();

        } else if (e instanceof StringLiteral) {
            // Note: Unicode escapes are not part of the subset language.
            return e.getLexeme();

        } else if (e instanceof NumberLiteral) {
            return NumberLiteral.class.cast(e).getValue() + "";

        } else if (e instanceof WholeNumberLiteral) {
            return WholeNumberLiteral.class.cast(e).getValue() + "";

        } else if (e instanceof NullLiteral) {
            return "null";

        } else {
            throw new RuntimeException("Internal Error: unknown literal type");
        }
    }

    /**
     * Returns JavaScript source for the given Manatee unary expression.
     */
    private String generateUnaryExpression(UnaryExpression e) {

        String operand = generateExpression(e.getOperand());
        if ("-".equals(e.getOp())) {
            return "(-(" + operand + "))";
        } else if ("not".equals(e.getOp())) {
            return "(!(" + operand + "))";
        } else if ("length".equals(e.getOp())) {
            return "((" + operand + ").length)";
        } else if ("complement".equals(e.getOp())) {
            return "(~(" + operand + "))";
        } else {
            throw new RuntimeException("Internal Error: unknown unary operator");
        }
    }

    /**
     * Returns JavaScript source for the given Manatee unary expression.
     */
    private String generateBinaryExpression(BinaryExpression e) {

        String op = e.getOp();
        String left = generateExpression(e.getLeft());
        String right = generateExpression(e.getRight());

        if (op.equals("+")) {
            if (e.getLeft().isArrayOrString() || e.getRight().isArray()) {
                if (e.getLeft().isArray()) {
                    if (e.getRight().isArray()) {
                        return left + ".concat(" + right + ")" ;
                    } else {
                        return left + ".push(" + right + ")" ;
                    }
                } else if (e.getRight().isArray()) {
                    return right + ".unshift(" + left + ")" ;
                } else {
                    return left + ".concat(" + right + ")" ;
                }
            }
        } else if (op.equals("*")) {
            if (e.getLeft().getType() == Type.STRING) {
                Variable counter = new Variable("", Type.WHOLE_NUMBER);
                Variable result = new Variable("", Type.STRING);
                String value = generateExpression(e.getLeft());
                String count = generateExpression(e.getRight());
                emit(String.format("for (var %s = \"\", %s = %s; %s > 0; %s--) {",
                        id(result), id(counter), count, id(counter), id(counter)));
                indentLevel++;
                emit(String.format("%s = %s.concat(%s);", id(result), id(result), value));
                indentLevel--;
                emit("}");
                return id(result);
            }
        } else if (op.equals("and")) {
            op = "&&";
        } else if (op.equals("or")) {
            op = "||";
        } else if (op.equals("=") || op.equals("is")) {
            op = "===";
        } else if (op.equals("≠") || op.equals("is not")) {
            op = "!==";
        } else if (op.equals("in")) {
            return String.format("(%s.indexOf(%s) >= 0)", right, left);
        } else if (op.equals("right shifted") || op.equals(">>")) {
            op = ">>";
        } else if (op.equals("left shifted") || op.equals("<<")) {
            op = "<<";
        } else if (op.equals("bit or")) {
            op = "|";
        } else if (op.equals("bit xor")) {
            op = "^";
        } else if (op.equals("bit and")) {
            op = "&";
        } else if (op.equals("-")) {
            op = "-";
        } else if (op.equals("/")) {
            op = "/";
        } else if (op.equals("<")) {
            op = "<";
        } else if (op.equals("<=")) {
            op = "<=";
        } else if (op.equals(">")) {
            op = ">";
        } else if (op.equals(">=")) {
            op = ">=";
        }  else if (op.equals("modulo")) {
            op = "%";
        } else if (op.equals("divides")) {
            // We tried to use String.format() but for some reason it
            // was not cooperating
            //return String.format("(!(%s \\% %s))", left, right);
            return "(!(" + left + " % " + right + "))";
        } else {
            throw new RuntimeException("Internal Error: unknown binary operator");
        }
        return String.format("(%s %s %s)", left, op, right);
    }
}
