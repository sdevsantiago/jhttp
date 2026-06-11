package sdevsantiago.jhttp.util;

import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ObjectUtils {

	/**
	 * Determine whether the given object is empty.
	 * @param obj The object to check.
	 * @return {@code true} if the object is {@code null} or empty, {@code false} otherwise.
	 */
	public static boolean isEmpty(final Object obj) {
		if (obj == null) return true;

		if (obj instanceof CharSequence sequence) return sequence.isEmpty();
		if (obj.getClass().isArray()) return Array.getLength(obj) == 0;
		if (obj instanceof Optional<?> optional) return optional.isEmpty();
		if (obj instanceof Collection<?> collection) return collection.isEmpty();
		if (obj instanceof Map<?, ?> map) return map.isEmpty();

		return false;
	}

	/**
	 * Performs the action with the given object if it's present.
	 * @param obj The object to check.
	 * @param action The action to perform.
	 * @see #isEmpty(Object) 
	 */
	public static <T> void consumeIfPresent(final T obj, final @NonNull Consumer<? super @NonNull T> action) {
		if (!isEmpty(obj)) action.accept(obj);
	}

	/**
	 * Performs the action with the given object if it's present,
	 * otherwise performs the given empty-based action.
	 * @param obj The object to check.
	 * @param action The action to perform.
	 * @param emptyAction The action to perform if the object is not present.
	 * @see #isEmpty(Object)   
	 */
	public static <T> void consumeIfPresentOrElse(final T obj,
												  final @NonNull Consumer<? super @NonNull T> action,
												  final @NonNull Runnable emptyAction) {
		if (!isEmpty(obj)) action.accept(obj);
		else emptyAction.run();
	}

}
