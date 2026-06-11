package sdevsantiago.jhttp.util;

import lombok.NonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

	/**
	 * Checks if a string is not null and has non-blank characters.
	 * @param s The string to check.
	 * @return {@code true} if a string is not null and has non-blank characters, otherwise {@code false}.
	 */
	public static boolean hasLength(String s) {
		return s != null && !s.isBlank();
	}

	/**
	 * Concatenate any number of strings, separated by the specified delimiter.
	 * @param delimiter The separator to use between strings. Must not be null.
	 * @param s Chain of strings. All null or blank strings are ignored.
	 * @return A string with all concatenated strings, separated by the specified delimiter.
	 * @throws NullPointerException If {@code delimiter} is {@code null}.
	 */
	public static String concatWith(final @NonNull String delimiter, final String... s) {
		return Arrays.stream(s)
			.filter(StringUtils::hasLength)
			.collect(Collectors.joining(delimiter));
	}

	/**
	 * Concatenate any number of strings.
	 * @param s Chain of strings. All null or blank strings are ignored.
	 * @return A string with all concatenated strings.
	 * @see #concatWith(String, String...)
	 */
	public static String concat(final String... s) {
		return concatWith("", s);
	}

	/**
	 * Concatenate any number of strings, separated by the system's line separator.
	 * @param s Chain of strings. All null or blank strings are ignored.
	 * @return A string with all concatenated strings, separated by the system's line separator.
	 */
	public static String concatLn(final String... s) {
		return concatWith(System.lineSeparator(), s);
	}

	/**
	 * Concatenate any number of strings, separated by a space.
	 * @param s Chain of strings. All null or blank strings are ignored.
	 * @return A string with all concatenated strings, separated by a space.
	 */
	public static String concatSpc(final String... s) {
		return concatWith(" ", s);
	}

}
