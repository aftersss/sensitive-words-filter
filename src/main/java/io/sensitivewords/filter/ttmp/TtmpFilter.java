package io.sensitivewords.filter.ttmp;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.sensitivewords.filter.AbstractSensitiveWordsFilter;
import org.apache.commons.lang.StringUtils;

/**
 * ttmp 算法过滤
 * 
 */
public class TtmpFilter extends AbstractSensitiveWordsFilter {

	private TtmpNode ttmpNode = new TtmpNode();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public boolean put(String word) {
		lock.writeLock().lock();
		try {
			this.ttmpNode.setMaxWordLength(Math.max(this.ttmpNode.getMaxWordLength(), word.length()));
			this.ttmpNode.setMinWordLength(Math.min(this.ttmpNode.getMinWordLength(), word.length()));

			for (int i = 0; i < 7 && i < word.length(); i++) {
				byte[] fastCheck = this.ttmpNode.getFastCheck();
				fastCheck[word.charAt(i)] |= (byte) (1 << i);

				this.ttmpNode.setFastCheck(fastCheck);
			}

			for (int i = 7; i < word.length(); i++) {
				byte[] fastCheck = this.ttmpNode.getFastCheck();
				fastCheck[word.charAt(i)] |= 0x80;

				this.ttmpNode.setFastCheck(fastCheck);
			}

			if (word.length() == 1) {
				ttmpNode.getCharCheck()[word.charAt(0)] = true;
			}
			else {
				ttmpNode.getEndCheck()[word.charAt(word.length() - 1)] = true;

				byte[] fastLength = ttmpNode.getFastLength();
				fastLength[word.charAt(0)] |= (byte) (1 << (Math.min(7, word.length() - 2)));

				ttmpNode.setFastLength(fastLength);

				ttmpNode.getHash().add(word);
			}
		} finally {
			lock.writeLock().unlock();
		}

		return false;
	}

	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}
		content = StringUtils.trim(content);

		int index = 0;

		lock.readLock().lock();
		try {
			while (index < content.length()) {
				int count = 1;

				if (partMatch) {
					if (index > 0 || (ttmpNode.getFastCheck()[content.charAt(index)] & 1) == 0) {
						// 匹配到下一个“可能是脏词”首字符的位置
						while (index < content.length() - 1 && (ttmpNode.getFastCheck()[content.charAt(++index)] & 1) == 0)
							;
					}
				}

				// 取得下一个脏词文本的第一个字符
				char begin = content.charAt(index);

				// 表示是简单脏词，单个字脏词
				if (ttmpNode.getMinWordLength() == 1 && ttmpNode.getCharCheck()[begin]) {
					if (callback.call(String.valueOf(begin))) {
						return true;
					}
				}

				// 比对的次数是 当前文本剩余比对长度 或者 脏词的最大长度
				for (int j = 1; j <= Math.min(ttmpNode.getMaxWordLength(), content.length() - index - 1); j++) {
					char current = content.charAt(index + j);

					if ((ttmpNode.getFastCheck()[current] & 1) == 0) { // 非首字符
						++count;
					}

					if ((ttmpNode.getFastCheck()[current] & (1 << Math.min(j, 7))) == 0) { // 当前字符在脏词中的位置超过7位
						break;
					}

					if (j + 1 >= ttmpNode.getMinWordLength()) { // 当前比对词长度小于等于最大脏词的长度
						// 判断当前字符是否是脏词最后一个字符
						if ((ttmpNode.getFastLength()[begin] & (1 << Math.min(j - 1, 7))) > 0 && ttmpNode.getEndCheck()[current]) {
							String sub = content.substring(index, index + j + 1);

							if (ttmpNode.getHash().contains(sub)) { // 判断是否是脏词
								if (callback.call(String.valueOf(sub))) {
									return true;
								}
							}
						}
					}
				}

				if (partMatch) {
					index++;
				}
				else {
					index += count;
				}
			}
		} finally {
			lock.readLock().unlock();
		}

		return false;
	}
}
