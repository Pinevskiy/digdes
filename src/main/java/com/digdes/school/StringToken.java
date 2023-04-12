package com.digdes.school;

public record StringToken(
                          String value
) implements Token {
    @Override
    public TokenType type() {
        return TokenType.STRING;
    }
}