package io.sensitivewords.filter.dat;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.sensitivewords.filter.AbstractSensitiveWordsFilter;
import org.apache.commons.lang.StringUtils;

/**
 * DAT算法(双数组算法)实现敏感词脱敏过滤
 */
public class DatFilter extends AbstractSensitiveWordsFilter {

	private final DatNode datNode = new DatNode();
	private int maxWordLength;//最长的敏感词的长度
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public boolean put(String word) {
		if (StringUtils.isBlank(word)) {
			return false;
		}

		word = StringUtils.trim(word);

		lock.writeLock().lock();
		try {
			datNode.getWords().add(word);

			for (Character character : word.toCharArray()) {
				datNode.getCharsBitSet().set(character);
			}
			maxWordLength = Math.max(maxWordLength, word.length());
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
				if (!datNode.getCharsBitSet().get(wordChar)) {
					continue;
				}

				String str = wordChar.toString();
				if (datNode.getWords().contains(str)) {
					if (callback.call(str)) {
						return true;
					}

					if (partMatch) {
						continue;
					}
				}

				int j = i + 1;
				while (j < content.length() && (j + 1 - i) <= maxWordLength) {
					// 判断下一个字符是否属于脏字符
					wordChar = content.charAt(j);
					if (!datNode.getCharsBitSet().get(wordChar)) {
						break;
					}

					String word = content.substring(i, j + 1);
					// 判断是否是脏词
					if (datNode.getWords().contains(word)) {
						if (callback.call(word)) {
							return true;
						}

						if (partMatch) {
							i += (word.length() - 1);
						}
					}

					j++;
				}
			}
		} finally {
			lock.readLock().unlock();
		}

		return false;
	}
}
