package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Calculator {

    private final Lexer lexer = new Lexer();
    private final PostfixConverter converter = new PostfixConverter();
    private final StackMachine stackMachine = new StackMachine();

    public Boolean calculate(String expression, Map<String, Object> row) throws Exception {
        List<Token> tokens = lexer.getTokens(expression);

        var postfixExpression = converter.convertToPostfix(tokens);
        var result = stackMachine.evaluate(postfixExpression, row);
        return result;
    }
}
