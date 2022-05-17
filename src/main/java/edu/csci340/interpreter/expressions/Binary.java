package edu.csci340.interpreter.expressions;

import edu.csci340.interpreter.StreamerInterpreter;
import edu.csci340.interpreter.scopes.Scope;
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
            put("&&", and);
            put("||", or);
            put("[]", access);
            put("[??]", filter);
        }
    };

    public ASTNode eval(ASTNode exp, Scope scope) {
        if (exp.type() == ASTNode.Type.NUMERIC_LITERAL ||
                exp.type() == ASTNode.Type.STRING_LITERAL ||
                exp.type() == ASTNode.Type.BOOLEAN_LITERAL ||
                exp.type() == ASTNode.Type.EXPRESSION_LIST) return exp;

        ASTNode l = exp.firstChild(StreamerInterpreter.eval(exp.firstChild(), scope));
        ASTNode r = exp.lastChild(StreamerInterpreter.eval(exp.lastChild(), scope));

        Object lhs = getValue(l, scope);
        Object rhs = getValue(r, scope);

        Object res = binOps.get(exp.value()).apply(lhs, rhs);

        if (res instanceof ASTNode a) return a;

        if (res instanceof String s) return new Literal(s);

        if (res instanceof Character c) return new Literal(String.valueOf(c));

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
            list.forEach(v -> {
                if (v instanceof ASTNode n) ls.append(n);
                else if (v instanceof String s) ls.append(new Literal(s));
                else if (v instanceof Double || v instanceof Integer) ls.append(new Literal(ASTNode.Type.NUMERIC_LITERAL, String.valueOf(v)));
                else if ( v instanceof Boolean b) ls.append(new Literal(ASTNode.Type.BOOLEAN_LITERAL, String.valueOf(b)));
            });
            return ls;
        }

        if (res instanceof edu.csci340.parser.ast.nodetypes.List ls) {
            return ls;
        }

        return exp;
    }

    private Object getValue(ASTNode l, Scope scope) {
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
            case IDENTIFIER -> {
                ASTNode s = StreamerInterpreter.eval(l, scope);
                System.out.println(l.type());
                yield s;
            }
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

    static BinaryOperator<Object> equals = Object::equals;

    static BinaryOperator<Object> notEquals = (l, r) -> !(Boolean) equals.apply(l, r);

    static BinaryOperator<Object> lt = (l, r) -> {
        if (l instanceof Integer i) l = Double.valueOf(i);
        if (r instanceof Integer i) r = Double.valueOf(i);

        if (l instanceof String sl) {
            if (r instanceof Double rd) return sl.compareTo(String.valueOf(rd)) < 0;
            if (r instanceof String rl) return sl.compareTo(rl) < 0;
        }

        if (l instanceof Double ld) {
            if (r instanceof Double rd) return ld < rd;
            if (r instanceof String rs) return String.valueOf(ld).compareTo(rs) < 0;
        }

        throw new IllegalArgumentException("Incomparable types" + l + r);
    };

    static BinaryOperator<Object> gt = (l, r) -> {
        if (l instanceof Integer i) l = Double.valueOf(i);
        if (r instanceof Integer i) r = Double.valueOf(i);

        if (l instanceof String sl) {
            if (r instanceof Number rd) return sl.compareTo(String.valueOf(rd)) > 0;
            if (r instanceof String rl) return sl.compareTo(rl) > 0;
        }

        if (l instanceof Double ld) {
            if (r instanceof Double rd) return ld > rd;
            if (r instanceof String rs) return String.valueOf(ld).compareTo(rs) > 0;
        }

        throw new IllegalArgumentException("Incomparable types");
    };

    static BinaryOperator<Object> lteq = (l, r) -> {
        if (l instanceof Integer i) l = Double.valueOf(i);
        if (r instanceof Integer i) r = Double.valueOf(i);

        if (l instanceof String sl) {
            if (r instanceof Double rd) return sl.compareTo(String.valueOf(rd)) <= 0;
            if (r instanceof String rl) return sl.compareTo(rl) <= 0;
        }

        if (l instanceof Double ld) {
            if (r instanceof Double rd) return ld <= rd;
            if (r instanceof String rs) return String.valueOf(ld).compareTo(rs) <= 0;
        }

        throw new IllegalArgumentException("Incomparable types");
    };

    static BinaryOperator<Object> gteq = (l, r) -> {
        if (l instanceof Integer i) l = Double.valueOf(i);
        if (r instanceof Integer i) r = Double.valueOf(i);

        if (l instanceof String sl) {
            if (r instanceof Double rd) return sl.compareTo(String.valueOf(rd)) >= 0;
            if (r instanceof String rl) return sl.compareTo(rl) >= 0;
        }

        if (l instanceof Double ld) {
            if (r instanceof Double rd) return ld >= rd;
            if (r instanceof String rs) return String.valueOf(ld).compareTo(rs) >= 0;
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
            if (r instanceof Integer i) return StreamerInterpreter.eval(ls.children().get(i), null).value();
            if (r instanceof Double d) return StreamerInterpreter.eval(ls.children().get(d.intValue()), null).value();
        }

        if (l instanceof Literal s && s.type() == ASTNode.Type.STRING_LITERAL) {
            if (r instanceof Integer i) return ((String)s.value()).charAt(i);
            if (r instanceof Double d) return ((String)s.value()).charAt(d.intValue());
        }

        if (l instanceof String s) {
            if (r instanceof Integer i) return s.charAt(i);
            if (r instanceof Double d) return s.charAt(d.intValue());
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };

    /**
     *
     */
    static BinaryOperator<Object> filter = (l, r) -> {
        if (l instanceof List<?> ls) {
            if (r instanceof Literal lit) {
                return ls.stream().filter(v -> {
                    if (v instanceof ASTNode k) {
                        k = StreamerInterpreter.eval((ASTNode) v, null);
                        if (k instanceof Literal lit2) {
                            return String.valueOf(lit2.value()).matches(String.valueOf(lit.value()));
                        }
                    }
                    return v.equals(r);
                }).collect(Collectors.toList());
            } else if (r instanceof String str) {
                return ls.stream().filter(v -> {
                    if (v instanceof ASTNode k) {
                        k = StreamerInterpreter.eval((ASTNode) v, null);
                        if (k instanceof Literal lit) {
                            return String.valueOf(lit.value()).matches(str);
                        }
                    }
                    return v.equals(r);
                }).collect(Collectors.toList());
            } else throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
        }

        if (l instanceof edu.csci340.parser.ast.nodetypes.List ls) {
            if (r instanceof Integer i) return StreamerInterpreter.eval(ls.children().get(i),null).value();
            if (r instanceof Double d) return StreamerInterpreter.eval(ls.children().get(d.intValue()), null).value();
        }

        throw new IllegalArgumentException("Incompatible types obj1: " + l + " obj2: " + r);
    };
}
