package sdevsantiago.jhttp.http.headers;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ContentType {

	TEXT_HTML("text/html"),
	TEXT_CSS("text/css"),
	TEXT_XML("text/xml"),
	TEXT_CSV("text/csv"),
	TEXT_PLAIN("text/plain"),

	APPLICATION_JAVASCRIPT("application/javascript"),
	APPLICATION_JSON("application/json"),
	APPLICATION_PDF("application/pdf"),
	APPLICATION_OCTET_STREAM("application/octet-stream"),

	IMAGE_PNG("image/png"),
	IMAGE_JPEG("image/jpeg"),
	IMAGE_X_ICON("image/x-icon"),
	IMAGE_SVG("image/svg+xml"),
	IMAGE_GIF("image/gif"),
	IMAGE_WEBP("image/webp"),

	VIDEO_MP4("video/mp4"),
	VIDEO_WEBM("video/webm"),

	AUDIO_MP3("audio/mp3"),
	AUDIO_WEBM("audio/webm"),

	FONT_WOFF("font/woff"),
	FONT_WOFF2("font/woff2"),
	FONT_TTF("font/ttf");

	private final String value;

	private final static Map<String, ContentType> FILE_EXTENSIONS = Map.ofEntries(
		Map.entry("html", TEXT_HTML), Map.entry("htm", TEXT_HTML),
		Map.entry("css", TEXT_CSS),
		Map.entry("js", APPLICATION_JAVASCRIPT),
		Map.entry("xml", TEXT_XML),
		Map.entry("csv", TEXT_CSV),
		Map.entry("txt", TEXT_PLAIN),
		Map.entry("json", APPLICATION_JSON),
		Map.entry("pdf", APPLICATION_PDF),
		Map.entry("png", IMAGE_PNG),
		Map.entry("jpeg", IMAGE_JPEG), Map.entry("jpg", IMAGE_JPEG),
		Map.entry("ico", IMAGE_X_ICON),
		Map.entry("svg", IMAGE_SVG),
		Map.entry("gif", IMAGE_GIF)
	);

	public static ContentType of(final @NonNull String value) {
		return Arrays.stream(values())
			.filter(contentType -> contentType.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Content type '" + value + "' not supported"));
	}

	public static ContentType ofPath(final @NonNull Path path) {
		final var fileName = path.getFileName().toString();

		final var fileExtensionIndex = fileName.lastIndexOf('.');
		if (fileExtensionIndex == -1) {
			return APPLICATION_OCTET_STREAM;
		}

		final var fileExtension = fileName.substring(fileExtensionIndex + 1).toLowerCase();

		return FILE_EXTENSIONS.getOrDefault(fileExtension, APPLICATION_OCTET_STREAM);
	}

}
