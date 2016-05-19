package se.panok.music.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.util.StringUtils;

public final class LogMessage {

	private static final String ACTION_PREFIX = "action=";

	private static final char KEY_VALUE_SEPARATOR = '=';

	private static final char PART_SEPARATOR = ',';

	private static final String QUOTE = "\"";

	private static final int DEFAULT_CAPACITY = 50;

	private final String actionName;

	private final Map<String, Object> logMessageParts = new HashMap<String, Object>();

	private LogMessage(final String actionName) {
		this.actionName = actionName;
	}

	public LogMessage addPart(final String key, final Object value) {

		if (!StringUtils.isEmpty(key)) {
			this.logMessageParts.put(key.trim(), value);
		}

		return this;
	}

	public static LogMessage createForAction(final String actionName) {
		if (StringUtils.isEmpty(actionName)) {
			throw new IllegalArgumentException(
					"The action name cannot be blank!");
		}
		return new LogMessage(actionName);
	}

	@Override
	public String toString() {
		final StringBuilder logMessageBuilder = new StringBuilder(
				DEFAULT_CAPACITY).append(ACTION_PREFIX).append(this.actionName);

		final Set<String> sortedKeys = new TreeSet<String>(
				this.logMessageParts.keySet());
		for (final String key : sortedKeys) {
			logMessageBuilder.append(PART_SEPARATOR).append(key)
					.append(KEY_VALUE_SEPARATOR)
					.append(this.wrap(this.logMessageParts.get(key)));
		}

		return logMessageBuilder.toString();
	}

	private Object wrap(final Object value) {
		if (value instanceof String) {
			final String stringValue = (String) value;
			if (!StringUtils.isEmpty(stringValue)) {
				return QUOTE + value + QUOTE;
			}
		}
		return value;
	}

}