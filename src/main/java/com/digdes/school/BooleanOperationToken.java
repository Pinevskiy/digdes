package com.digdes.school;

public record BooleanOperationToken(
        OperationType operationType
) implements Token {
    @Override
    public TokenType type() {
        return TokenType.BOOLEAN_OPERATION;
    }
}
