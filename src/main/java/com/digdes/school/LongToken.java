package com.digdes.school;

public record LongToken(
        Long value
) implements Token {
    @Override
    public TokenType type() {
        return TokenType.LONG;
    }
}
