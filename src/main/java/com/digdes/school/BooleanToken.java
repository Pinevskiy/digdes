package com.digdes.school;

public record BooleanToken(
        Boolean value
) implements Token {
    @Override
    public TokenType type() {
        return TokenType.BOOLEAN;
    }
}
