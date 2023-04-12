package com.digdes.school;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PostfixConverter {

    public List<Token> convertToPostfix(List<Token> source) {
        List<Token> postfixExpression = new ArrayList<>();
        Deque<Token> operationStack = new LinkedList<>();
        for (Token token : source) {
            switch (token.type()) {
                case LONG, DOUBLE, STRING, BOOLEAN -> postfixExpression.add(token);
                case OPEN_BRACKET -> operationStack.push(token);
                case CLOSE_BRACKET -> {
                    while (!operationStack.isEmpty() && operationStack.peek().type() != TokenType.OPEN_BRACKET) {
                        postfixExpression.add(operationStack.pop());
                    }
                    operationStack.pop(); // открывающая скобка
                }
                case BOOLEAN_OPERATION -> {
                    while (!operationStack.isEmpty() && getPriority(operationStack.peek()) >= getPriority(token)) {
                        postfixExpression.add(operationStack.pop());
                    }
                    operationStack.push(token);
                }
            }
        }
        while (!operationStack.isEmpty()) {
            postfixExpression.add(operationStack.pop());
        }
        return postfixExpression;
    }

    private int getPriority(Token token) {
        if (token instanceof BooleanOperationToken operation) {
            return switch (operation.operationType()) {
                case EQUALS, NOT_EQUALS, LIKE, ILIKE, LESS, MORE, LESS_EQUALS, MORE_EQUALS -> 2;
                case AND, OR -> 1;
            };
        }
        return 0; // для открывающей скобки
    }
}
