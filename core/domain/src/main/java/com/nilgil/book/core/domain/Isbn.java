package com.nilgil.book.core.domain;

import java.util.Optional;

public record Isbn(String value) {

    private static final String ISBN10_REGEX = "\\d{9}[\\dX]";
    private static final String ISBN13_REGEX = "\\d{13}";
    private static final String CONVERTIBLE_ISBN13_PREFIX = "978";
    private static final char ISBN10_CHECK_DIGIT_FOR_TEN = 'X';

    public Isbn {
        requireNonBlank(value);
        String normalized = normalize(value);
        value = ensureIsbn13(normalized);
    }

    public Optional<String> tryAsIsbn10() {
        return value.startsWith(CONVERTIBLE_ISBN13_PREFIX)
                ? Optional.of(convert13to10(value))
                : Optional.empty();
    }

    public String asIsbn13() {
        return value;
    }

    private static String ensureIsbn13(String normalized) {
        if (isValidIsbn13(normalized)) {
            return normalized;
        }
        if (isValidIsbn10(normalized)) {
            return convert10to13(normalized);
        }
        throw new IllegalArgumentException("Invalid ISBN: " + normalized);
    }

    private static void requireNonBlank(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException("ISBN must not be null or blank");
        }
    }

    private static String normalize(String s) {
        return s.replaceAll("[^0-9Xx]", "").toUpperCase();
    }

    private static boolean isValidIsbn10(String s) {
        return isLength10(s) && isDigitsOrX10(s) && hasValidCheck10(s);
    }

    private static boolean isValidIsbn13(String s) {
        return isLength13(s) && isDigits13(s) && hasValidCheck13(s);
    }

    private static boolean isLength10(String s) {
        return s.length() == 10;
    }

    private static boolean isLength13(String s) {
        return s.length() == 13;
    }

    private static boolean isDigitsOrX10(String s) {
        return s.matches(ISBN10_REGEX);
    }

    private static boolean isDigits13(String s) {
        return s.matches(ISBN13_REGEX);
    }

    private static boolean hasValidCheck10(String s) {
        char expected = calculateIsbn10Check(s.substring(0, 9));
        return expected == s.charAt(9);
    }

    private static boolean hasValidCheck13(String s) {
        int expected = calculateIsbn13Check(s.substring(0, 12));
        return expected == (s.charAt(12) - '0');
    }

    private static String convert10to13(String isbn10) {
        String base = CONVERTIBLE_ISBN13_PREFIX + isbn10.substring(0, 9);
        int check = calculateIsbn13Check(base);
        return base + check;
    }

    private static String convert13to10(String isbn13) {
        requireConvertible978(isbn13);
        String core = isbn13.substring(CONVERTIBLE_ISBN13_PREFIX.length(), 12);
        char check = calculateIsbn10Check(core);
        return core + check;
    }

    private static void requireConvertible978(String isbn13) {
        if (!isbn13.startsWith(CONVERTIBLE_ISBN13_PREFIX)) {
            throw new IllegalArgumentException("Only 978-prefixed can be converted to ISBN-10: " + isbn13);
        }
    }

    private static int calculateIsbn13Check(String first12Digits) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = first12Digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        return (10 - (sum % 10)) % 10;
    }

    private static char calculateIsbn10Check(String first9Digits) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (i + 1) * (first9Digits.charAt(i) - '0');
        }
        int mod = sum % 11;
        return (mod == 10) ? ISBN10_CHECK_DIGIT_FOR_TEN : Character.forDigit(mod, 10);
    }
}
