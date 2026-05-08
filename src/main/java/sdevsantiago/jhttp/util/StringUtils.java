package sdevsantiago.jhttp.util;

public class StringUtils {

	/**
	 * Checks if a string is not null and has non-blank characters.
	 * @param s The string to check.
	 * @return {@code true} if a string is not null and has non-blank characters, otherwise {@code false}.
	 */
	public static boolean hasLength(String s) {
		return s != null && !s.isBlank();
	}

}
