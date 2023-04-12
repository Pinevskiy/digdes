package com.digdes.school;

import java.util.*;

public class Lexer {
    private static final String DELIMITERS = ". !=<>()'";

    public List<Token> getTokens(String source) {
        var tokenizer = new StringTokenizer(source, DELIMITERS, true);
        List<Token> tokens = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        boolean bracket = false;
        boolean dot = false;
        while (tokenizer.hasMoreTokens()) {
            var token = tokenizer.nextToken();
            if (Objects.equals(token, "'") && bracket == false) {
                bracket = true;
                continue;
            }
            if (!Objects.equals(token, "'") && bracket) {
                temp.append(token);
                continue;
            }
            else if (Objects.equals(token, "'") && bracket) {
                tokens.add(new StringToken(String.valueOf(temp)));
                bracket = false;
                temp.setLength(0);
                continue;
            }
            if (token.isBlank()) {
                continue;
            }
            if (token.equals("!")){
                temp.append(token);
                continue;
            }
            else if (token.equals("<")){
                temp.append(token);
                continue;
            }
            else if (token.equals(">")){
                temp.append(token);
                continue;
            }

            boolean b = String.valueOf(temp).equals("!") || String.valueOf(temp).equals("<") || String.valueOf(temp).equals(">");
            if (token.equals("=") && b){
                if (String.valueOf(temp).equals("!")) {
                    tokens.add(new BooleanOperationToken(OperationType.NOT_EQUALS));
                }
                if (String.valueOf(temp).equals("<")) {
                    tokens.add(new BooleanOperationToken(OperationType.LESS_EQUALS));
                }
                if (String.valueOf(temp).equals(">")) {
                    tokens.add(new BooleanOperationToken(OperationType.MORE_EQUALS));
                }
                temp.setLength(0);
                continue;
            }
            else if (!token.equals("=") && (String.valueOf(temp).equals("<") || String.valueOf(temp).equals(">"))){
                if (String.valueOf(temp).equals("<")) {
                    tokens.add(new BooleanOperationToken(OperationType.LESS));
                }
                if (String.valueOf(temp).equals(">")) {
                    tokens.add(new BooleanOperationToken(OperationType.MORE));
                }
                temp.setLength(0);
            }
            if (Objects.equals(token, ".") && isNumber(String.valueOf(temp))) {
                temp.append(token);
                dot = true;
                continue;
            }
            if (dot && isNumber(token)) {
                temp.append(token);
                dot = false;
                tokens.add(new DoubleToken(Double.parseDouble(String.valueOf(temp))));
                temp.setLength(0);
                continue;
            }
            if (isNumber(token)) {
                temp.append(token);
                continue;
            }
            if (!Objects.equals(token, ".") && isNumber(String.valueOf(temp))) {
                tokens.add(new LongToken(Long.parseLong(String.valueOf(temp))));
                temp.setLength(0);
            }
            tokens.add(
                    switch (token.toLowerCase(Locale.ROOT)) {
                        case "=" -> new BooleanOperationToken(OperationType.EQUALS);
                        case "like" -> new BooleanOperationToken(OperationType.LIKE);
                        case "ilike" -> new BooleanOperationToken(OperationType.ILIKE);
                        case "and" -> new BooleanOperationToken(OperationType.AND);
                        case "or" -> new BooleanOperationToken(OperationType.OR);
                        case "(" -> new OtherToken(TokenType.OPEN_BRACKET);
                        case ")" -> new OtherToken(TokenType.CLOSE_BRACKET);
                        case "true" -> new BooleanToken(true);
                        case "false" -> new BooleanToken(false);
                        default -> throw new RuntimeException("Unexpected token: " + token);
                    }
            );
        }
        if (!temp.isEmpty()) {
            tokens.add(new LongToken(Long.parseLong(String.valueOf(temp))));
        }
        return tokens;
    }

    private boolean isNumber(String token) {
        if (token.isBlank()) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (i == 0 && Objects.equals('-', token.charAt(i))){
                continue;
            }
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
