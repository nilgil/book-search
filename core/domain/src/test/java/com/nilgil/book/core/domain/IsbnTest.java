package com.nilgil.book.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class IsbnTest {

    @Nested
    @DisplayName("생성")
    class Creation {

        @ParameterizedTest
        @ValueSource(strings = {"9783161484100", "978-3-16-148410-0", "  9783161484100  "})
        @DisplayName("유효한 ISBN-13으로 생성 시 정상적으로 생성된다")
        void valid_isbn13_should_be_normalized(String raw) {
            assertThatNoException().isThrownBy(() -> new Isbn(raw));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0321356683", "0-321-35668-3", "  0321356683  "})
        @DisplayName("유효한 ISBN-10으로 생성 시 정상적으로 생성된다")
        void valid_isbn10_should_be_normalized(String raw) {
            assertThatNoException().isThrownBy(() -> new Isbn(raw));
        }

        @ParameterizedTest(name = "\"{0}\"")
        @MethodSource("invalidIsbnProvider")
        @DisplayName("유효하지 않은 값으로 ISBN 생성 시 InvalidIsbnException 예외 발생")
        void constructor_withInvalidInput_shouldThrowException(String raw) {
            assertThatThrownBy(() -> new Isbn(raw)).isInstanceOf(InvalidIsbnException.class);
        }

        private static Stream<Arguments> invalidIsbnProvider() {
            return Stream.of(
                    // 빈 값, null, 공백
                    Arguments.of((String) null),
                    Arguments.of(""),
                    Arguments.of(" "),
                    Arguments.of("\t"),
                    Arguments.of("\n"),

                    // 유효하지 않은 Check digit
                    Arguments.of("9783161484109"),
                    Arguments.of("9780804429572"),

                    // 유효하지 않은 길이
                    Arguments.of("97831614841"),
                    Arguments.of("97808044295721"),
                    Arguments.of("123456789"),
                    Arguments.of("12345678901234"),

                    // 숫자가 아닌 문자 포함
                    Arguments.of("978A161484100"),
                    Arguments.of("978-3-1B-148410-0"),
                    Arguments.of("978316148410X")
            );
        }

    }

    @Nested
    @DisplayName("동등성 비교")
    class Equality {

        @Test
        @DisplayName("같은 값을 가진 ISBN은 동등하다")
        void same_value_should_be_equal() {
            Isbn isbn1 = new Isbn("9783161484100");
            Isbn isbn2 = new Isbn("9783161484100");
            assertThat(isbn1).isEqualTo(isbn2);
        }

        @Test
        @DisplayName("ISBN-13와 ISBN-10가 호환된다면 동등하다")
        void isbn10_and_equivalent_isbn13_should_be_equal() {
            Isbn isbn13 = new Isbn("9783161484100");
            String convertedIsbn10 = isbn13.tryAsIsbn10().orElseThrow();
            Isbn isbn10 = new Isbn(convertedIsbn10);
            assertThat(isbn13).isEqualTo(isbn10);
        }

        @Test
        @DisplayName("다른 값을 가진 ISBN은 동등하지 않다")
        void different_value_should_not_be_equal() {
            Isbn isbn1 = new Isbn("9783161484100");
            Isbn isbn2 = new Isbn("9791189585174");
            assertThat(isbn1).isNotEqualTo(isbn2);
        }

    }

    @Nested
    @DisplayName("정규화")
    class Normalization {

        @Test
        @DisplayName("하이픈이 포함된 ISBN은 하이픈이 제거된 형태로 정규화된다")
        void hyphenated_isbn_should_be_normalized() {
            Isbn isbn = new Isbn("978-3-16-148410-0");
            assertThat(isbn.asIsbn13()).isEqualTo("9783161484100");
        }

        @Test
        @DisplayName("앞뒤 공백이 포함된 ISBN은 공백이 제거된 형태로 정규화된다")
        void padded_isbn_should_be_normalized() {
            Isbn isbn = new Isbn("  9783161484100  ");
            assertThat(isbn.asIsbn13()).isEqualTo("9783161484100");
        }
    }

    @Nested
    @DisplayName("출력")
    class Output {

        @Test
        @DisplayName("ISBN-13 형식으로 출력할 수 있다")
        void should_output_as_isbn13() {
            Isbn isbn = new Isbn("9783161484100");
            assertThat(isbn.asIsbn13()).isEqualTo("9783161484100");
        }

        @Test
        @DisplayName("978로 시작하는 ISBN-13은 ISBN-10으로 출력할 수 있다")
        void convertible_isbn13_should_output_as_isbn10() {
            Isbn isbn = new Isbn("9783161484100");
            Optional<String> mayIsbn10 = isbn.tryAsIsbn10();
            assertThat(mayIsbn10.isEmpty()).isFalse();
            assertThat(mayIsbn10.get()).isEqualTo("316148410X");
        }

        @Test
        @DisplayName("978로 시작하지 않는 ISBN-13은 ISBN-10으로 조회 시 빈 값을 반환한다")
        void non_convertible_isbn13_should_not_output_as_isbn10() {
            Isbn isbn = new Isbn("9791189585174");
            Optional<String> mayIsbn10 = isbn.tryAsIsbn10();
            assertThat(mayIsbn10.isEmpty()).isTrue();
        }
    }
}
