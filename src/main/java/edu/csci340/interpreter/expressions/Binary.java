package edu.csci340.interpreter.expressions;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.parser.ast.nodetypes.ASTNode;
import edu.csci340.parser.ast.nodetypes.expressions.literals.Literal;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Binary {

    private static Binary instance;

    private Binary() {
    }

    public static Binary getInstance() {
        if (instance == null) instance = new Binary();
        return instance;
    }

    private Map<String, BinaryOperator<Object>> binOps = new Hashtable<>() {
        {
            put("+", add);
            put("-", minus);
            put("*", mult);
            put("/", divide);
            put("%", mod);
            put("==", equals);
            put("!=", notEquals);
            put("<", lt);
            put(">", gt);
            put("<=", lteq);
            put(">=", gteq);
            put("<<", read);
            put(">>", write);
            put(">>+", append);
            put("&&", and);
            put("||", or);
            put("[]", access);
            put("[??]", filter);
        }
    };

    public ASTNode eval(ASTNode exp) {
        if (exp.type() == ASTNode.Type.NUMERIC_LITERAL ||
                exp.type() == ASTNode.Type.STRING_LITERAL ||
                exp.type() == ASTNode.Type.BOOLEAN_LITERAL) return exp;

        ASTNode l = exp.firstChild(StreamerInterpreter.eval(exp.firstChild()));
        ASTNode r = exp.lastChild(StreamerInterpreter.eval(exp.lastChild()));

        Object lhs = getValue(l);
        Object rhs = getValue(r);

        Object res = binOps.get(exp.value()).apply(lhs, rhs);

        if (res instanceof String s) return new Literal(s);

        if (res instanceof Double d) {
            if (d == d.intValue()) res = d.intValue();
            return new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(res));
        }

        if (res instanceof Integer) {
            return new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(res));
        }

        if (res instanceof Boolean) return new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(res));

        if (res instanceof List<?> list) {
            edu.csci340.parser.ast.nodetypes.List ls = new edu.csci340.parser.ast.nodetypes.List(ASTNode.Type.EXPRESSION_LIST);
            list.forEach(v -> ls.append((ASTNode) v));
            return ls;
        }

        if (res instanceof edu.csci340.parser.ast.nodetypes.List ls) {
            return ls;
        }

        return exp;
    }

    private Object getValue(ASTNode l) {
        return switch (l.type()) {
            case STRING_LITERAL -> String.valueOf(l.value());
            case NUMERIC_LITERAL -> {
                if (l.value() instanceof Double d)
                    if (d == d.intValue()) yield d.intValue();
                    else yield d;
                yield Double.valueOf(String.valueOf(l.value()));
            }
            case BOOLEAN_LITERAL -> Boolean.valueOf(String.valueOf(l.value()));
            case EXPRESSION_LIST -> l.children();
            default -> throw new IllegalArgumentException("Cannot retrieve a value from this node: \n" + l.toString(1));
        };
    }

    /**
     * String + String = String
     * String + Number = String
     * String + Boolean = String
     * Number + Number = Number
     * Number + Boolean = Number
     * Boolean + Boolean = Boolean
     */
    static BinaryOperator<Object> add = (l, r) -> {
        if (l instanceof String s1) return s1 + r;

        if (r instanceof String s2) return l + s2;

        if (l instanceof Double d1) {
            if (r instanceof Double d2) return d1 + d2;
            if (r instanceof Integer i2) return d1 + i2;
        }

        if (l instanceof Integer i1) {
            if (r instanceof Double d2) return i1 + d2;
            if (r instanceof Integer i2) return i1 + i2;
        }

        if (l instanceof Boolean a)
            if (r instanceof Boolean b) return a || b;
            else if (r instanceof Double d) return d + (a ? 1 : 0);
            else if (r instanceof Integer i) return i + (a ? 1 : 0);

        throw new IllegalArgumentException("Incompatible types");
    };

    /**
     * String - String = String
     * String - Number = String
     * String - Boolean = String
     * Number - Number = Number
     * Number - Boolean = Number
     * Boolean - Boolean = Boolean
     */
    static BinaryOperator<Object> minus = (l, r) -> {
        if (l instanceof String s1) return s1.replaceAll(r.toString(), "");

        if (r instanceof String s2) return l.toString().replaceAll(s2, "");

        if (l instanceof Double d1) {
            if (r instanceof Double d2) return d1 - d2;
            if (r instanceof Integer i2) return d1 - i2;
        }

        if (l instanceof Integer i1) {
            if (r instanceof Double d2) return i1 - d2;
            if (r instanceof Integer i2) return i1 - i2;
        }

        if (l instanceof Boolean a)
            if (r instanceof Boolean b) return a || !b;
            else if (r instanceof Double d) return d - (a ? 1 : 0);
            else if (r instanceof Integer i) return i - (a ? 1 : 0);

        throw new IllegalArgumentException("Incompatible types");
    };

    /**
     * Number * Number = Number
     * Number * Boolean = Number
     * Boolean * Boolean = Boolean
     */
    static BinaryOperator<Object> mult = (l, r) -> {
        if (l instanceof Double d1) {
            if (r instanceof Double d2) return d1 * d2;
            if (r instanceof Integer i2) return d1 * i2;
        }

        if (l instanceof Integer i1) {
            if (r instanceof Double d2) return i1 * d2;
            if (r instanceof Integer i2) return i1 * i2;
        }

        if (l instanceof Boolean a)
            if (r instanceof Boolean b) return a && b;
            else if (r instanceof Double d) return d * (a ? 1 : 0);
            else if (r instanceof Integer i) return i * (a ? 1 : 0);

        throw new IllegalArgumentException("Incompatible types");
    };

    /**
     * String / String = List<String>
     * String / Number = List<String>
     * String / Boolean = List<String>
     * Number / Number = Number
     */
    static BinaryOperator<Object> divide = (l, r) -> {
        if (l instanceof String s1) return new ArrayList<String>() {
            {
                addAll(List.of(s1.split(r.toString())));
            }
        };
        if (r instanceof String s2) return l.toString().split(s2);

        if (l instanceof Double d1) {
            if (r instanceof Double d2) return d1 / d2;
            if (r instanceof Integer i2) return d1 / i2;
        }

        if (l instanceof Integer i1) {
            if (r instanceof Double d2) return i1 / d2;
            if (r instanceof Integer i2) return i1 / i2;
        }

        throw new IllegalArgumentException("Incompatible types");
    };

    /**
     * Number % Number = Number
     */
    static BinaryOperator<Object> mod = (l, r) -> {
        if (l instanceof Double d1) {
            if (r instanceof Double d2) return d1 - d2;
            if (r instanceof Integer i2) return d1 - i2;
        }

        if (l instanceof Integer i1) {
            if (r instanceof Double d2) return i1 - d2;
            if (r instanceof Integer i2) return i1 - i2;
        }

        throw new IllegalArgumentException("Incompatible types");
    };

    static BinaryOperator<Object> read = (l, r) -> {
        return null;
    };

    static BinaryOperator<Object> write = (l, r) -> {
        return null;
    };

    static BinaryOperator<Object> append = (l, r) -> {
        return null;
    };

    static BinaryOperator<Object> equals = Object::equals;

    static BinaryOperator<Object> notEquals = (l, r) -> !(Boolean) equals.apply(l, r);

    static BinaryOperator<Object> lt = (l, r) -> {
        if (l instanceof String sl) {
            if (r instanceof String rl) return sl.compareTo(rl) < 0;
        }
        throw new IllegalArgumentException("Incomparable types");
    };

    static BinaryOperator<Object> gt = (l, r) -> {
        if (l instanceof String sl) {
            if (r instanceof String rl) return sl.compareTo(rl) > 0;
        }
        throw new IllegalArgumentException("Incomparable types");
    };

    static BinaryOperator<Object> lteq = (l, r) -> {
        if (l instanceof String sl) {
            if (r instanceof String rl) return sl.compareTo(rl) <= 0;
        }
        throw new IllegalArgumentException("Incomparable types");
    };

    static BinaryOperator<Object> gteq = (l, r) -> {
        if (l instanceof String sl) {
            if (r instanceof String rl) return sl.compareTo(rl) >= 0;
        }
        throw new IllegalArgumentException("Incomparable types");
    };

    /**
     * Boolean && Boolean = Boolean
     * Boolean && Number = Boolean
     */
    static BinaryOperator<Object> and = (l, r) -> {
        if (l instanceof Boolean lb) {
            if (!lb) return false;
            if (r instanceof Boolean rb) return rb;
            if (r instanceof Number n) return !n.equals(0);
        }
        if (r instanceof Boolean rb) {
            if (!rb) return false;
            if (l instanceof Number n) return !n.equals(0);
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };

    /**
     * Boolean || Boolean = Boolean
     */
    static BinaryOperator<Object> or = (l, r) -> {
        if (l instanceof Boolean lb) {
            if (lb) return true;
            if (r instanceof Boolean rb) return rb;
            if (r instanceof Number n) return !n.equals(0);
        }
        if (r instanceof Boolean rb) {
            if (rb) return true;
            if (l instanceof Number n) return !n.equals(0);
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };

    /**
     *
     */
    static BinaryOperator<Object> access = (l, r) -> {
        if (l instanceof List<?> ls) {
            if (r instanceof Integer i) return ls.get(i);
            if (r instanceof Double d) return ls.get(d.intValue());
        }

        if (l instanceof edu.csci340.parser.ast.nodetypes.List ls) {
            if (r instanceof Integer i) return StreamerInterpreter.eval(ls.children().get(i)).value();
            if (r instanceof Double d) return StreamerInterpreter.eval(ls.children().get(d.intValue())).value();
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };

    /**
     *
     */
    static BinaryOperator<Object> filter = (l, r) -> {
        if (l instanceof List<?> ls) {
            return ls.stream().filter(v -> v.equals(r)).collect(Collectors.toList());
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };
}
