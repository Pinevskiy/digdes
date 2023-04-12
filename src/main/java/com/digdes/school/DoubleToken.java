package com.digdes.school;

public record DoubleToken(
        Double value
) implements Token {
    @Override
    public TokenType type() {
        return TokenType.DOUBLE;
    }
}
