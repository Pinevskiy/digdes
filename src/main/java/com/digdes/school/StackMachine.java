package com.digdes.school;

import java.util.*;

public class StackMachine {

    public static Object getValue(Map<String,Object> map, String name) throws Exception {
        JavaSchoolStarter.Field f = JavaSchoolStarter.getFieldByName(name);
        if(f == null) {
            throw new Exception("unknown field " + name);
        } else {
            return map.get(f.getName());
        }
    }

    public Boolean evaluate(List<Token> postfixExpression, Map<String, Object> row) throws Exception {
        Deque<Object> valueStack = new LinkedList<>();
        for (Token token : postfixExpression) {
            if (token instanceof LongToken longToken) {
                valueStack.push(longToken.value());
            }
            else if (token instanceof DoubleToken doubleToken) {
                valueStack.push(doubleToken.value());
            }
            else if (token instanceof StringToken stringToken) {
                valueStack.push(stringToken.value());
            }
            else if (token instanceof BooleanToken booleanToken) {
                valueStack.push(booleanToken.value());
            }
            else if (token instanceof BooleanOperationToken operation) {
                Object right = valueStack.pop();
                Object left = valueStack.pop();
                if (left instanceof String && JavaSchoolStarter.getFieldByName((String) left) != null) {
                    left = getValue(row, String.valueOf(left));
                }
                if (right instanceof String  && JavaSchoolStarter.getFieldByName((String) right) != null) {
                    right = getValue(row, String.valueOf(right));
                }
                boolean result = false;
                double l;
                double r;

                switch (operation.operationType()) {
                    case EQUALS ->  result = Objects.equals(left, right);
                    case NOT_EQUALS -> {
                        if (left == null && right != null) {
                            return true;
                        }
                        result = !Objects.equals(left, right);
                    }
                    case LIKE -> {
                        if(left instanceof String && right instanceof String) {
                            if (((String) right).startsWith("%") && ((String) right).endsWith("%")) {
                                result = ((String) left).contains((CharSequence) right);
                            }
                            else if (((String) right).startsWith("%")) {
                                result = ((String) left).endsWith((String) right);
                            }
                            else if (((String) right).endsWith("%")) {
                                result = ((String) left).startsWith((String) right);
                            }
                            else {
                                result = left.equals(right);
                            }
                        }
                    }
                    case ILIKE -> {
                        if(left instanceof String && right instanceof String) {
                            if (((String) right).startsWith("%") && ((String) right).endsWith("%")) {
                                result = ((String) left).toLowerCase().contains(((String) right).toLowerCase());
                            }
                            else if (((String) right).startsWith("%")) {
                                result = ((String) left).toLowerCase(Locale.ROOT).endsWith(((String) right).toLowerCase());
                            }
                            else if (((String) right).endsWith("%")) {
                                result = ((String) left).toLowerCase(Locale.ROOT).startsWith(((String) right).toLowerCase());
                            }
                            else {
                                result = ((String) left).equalsIgnoreCase(((String) right));
                            }
                        }
                    }
                    case LESS -> {
                        if (left instanceof Long) {
                            l = ((Long) left).doubleValue();
                        }
                        else l = (Double) left;
                        if (right instanceof Long) {
                            r = ((Long) right).doubleValue();
                        }
                        else r = (Double) right;
                        result = l < r;
                    }
                    case MORE -> {
                        if (left instanceof Long) {
                            l = ((Long) left).doubleValue();
                        }
                        else l = (Double) left;
                        if (right instanceof Long) {
                            r = ((Long) right).doubleValue();
                        }
                        else r = (Double) right;
                        result = l > r;
                    }
                    case LESS_EQUALS -> {
                        if (left instanceof Long) {
                            l = ((Long) left).doubleValue();
                        }
                        else l = (Double) left;
                        if (right instanceof Long) {
                            r = ((Long) right).doubleValue();
                        }
                        else r = (Double) right;
                        result = l <= r;
                    }
                    case MORE_EQUALS -> {
                        if (left instanceof Long) {
                            l = ((Long) left).doubleValue();
                        }
                        else l = (Double) left;
                        if (right instanceof Long) {
                            r = ((Long) right).doubleValue();
                        }
                        else r = (Double) right;
                        result = l >= r;
                    }
                    case AND -> {
                        if(left instanceof Boolean && right instanceof Boolean) {
                            result = (Boolean) left && (Boolean) right;
                        }
                    }
                    case OR -> {
                        if(left instanceof Boolean && right instanceof Boolean) {
                            result = (Boolean) left || (Boolean) right;
                        }
                    }
                }
                valueStack.push(result);
            }
        }
        return (Boolean) valueStack.pop();
    }
}