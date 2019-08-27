package io.sensitivewords.filter.bucket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.sensitivewords.filter.AbstractSensitiveWordsFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hash bucket 脱敏过滤算法实现
 */
public class HashBucketFilter extends AbstractSensitiveWordsFilter {

	private static final Logger logger = LoggerFactory.getLogger(HashBucketFilter.class);

	private final Map<Character, Map<Short, Set<String>>> wordNodes = new HashMap<>();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public boolean put(String word) {
		if (StringUtils.isBlank(word)) {
			return false;
		}
		if (word.length() > Short.MAX_VALUE) {
			logger.warn("word's length must be less than " + Short.MAX_VALUE);
			return false;
		}

		word = StringUtils.trim(word);

		lock.writeLock().lock();
		try {
			char firstChar = word.charAt(0);

			Map<Short, Set<String>> buckets = wordNodes.get(firstChar);
			if (buckets == null) {
				buckets = new HashMap<>();
				wordNodes.put(firstChar, buckets);
			}

			short wordLength = (short)word.length();
			Set<String> words = buckets.get(wordLength);
			if (words == null) {
				words = new HashSet<>();
				buckets.put(wordLength, words);
			}
			words.add(word);
		} finally {
			lock.writeLock().unlock();
		}

		return true;
	}

	@Override
	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}

		content = StringUtils.trim(content);

		lock.readLock().lock();
		try {
			for (int i = 0; i < content.length(); i++) {
				Character wordChar = content.charAt(i);

				// 判断是否属于脏字符
				if (!wordNodes.containsKey(wordChar)) {
					continue;
				}

				Map<Short, Set<String>> buckets = wordNodes.get(wordChar);
				Set<Short> sizes = buckets.keySet();
				for (short size : sizes) {
					if (i + size > content.length()) {
						continue;
					}

					String word = content.substring(i, i + size);
					Set<String> words = buckets.get(size);
					// 判断是否是脏词
					if (words.contains(word)) {
						if (callback.call(word)) {
							return true;
						}

						if (partMatch) {
							i += (word.length() - 1);//最后还有个i++，所以这里要-1
						}
					}
				}
			}
		} finally {
			lock.readLock().unlock();
		}

		return false;
	}
}
