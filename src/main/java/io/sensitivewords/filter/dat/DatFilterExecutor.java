package io.sensitivewords.filter.dat;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

import io.sensitivewords.filter.AbstractFilterExecutor;

/**
 * 双数组算法过滤敏感词
 */
final class DatFilterExecutor extends AbstractFilterExecutor {

	private final DatNode datNode = new DatNode();
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
				datNode.getChars().add(character);
			}
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
				if (!datNode.getChars().contains(wordChar)) {
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
				while (j < content.length()) {
					// 判断下一个字符是否属于脏字符
					wordChar = content.charAt(j);
					if (!datNode.getChars().contains(wordChar)) {
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
