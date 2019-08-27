package io.sensitivewords.filter.dfa;

import io.sensitivewords.filter.AbstractSensitiveWordsFilter;
import org.apache.commons.lang.StringUtils;

/**
 * DFA 算法实现敏感词脱敏过滤
 */
public class DfaFilter extends AbstractSensitiveWordsFilter {

	private final DfaNode rootNode = new DfaNode(' ');

	@Override
	public boolean put(String word) {
		if (StringUtils.isBlank(word)) {
			return false;
		}

		word = StringUtils.trim(word);

		DfaNode node = rootNode;
		for (int i = 0; i < word.length(); i++) {
			Character nextChar = word.charAt(i);

			DfaNode nextNode = node.getChild(nextChar);
			if (nextNode == null) {
				nextNode = new DfaNode(nextChar);
			}

			if (i == word.length() - 1) {
				nextNode.setWord(true);
			}

			node.addChildIfNotPresent(nextNode);
			node = nextNode;
		}

		return true;
	}

	@Override
	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}

		content = StringUtils.trim(content);

		for (int index = 0; index < content.length();index++) {
			char firstChar = content.charAt(index);

			DfaNode node = rootNode.getChild(firstChar);
			if (node == null) {
				continue;
			}

			if (node.isWord()) {
				if (callback.call(new String(new char[]{firstChar}))) {
					return true;
				}
				if(partMatch) {
					continue;
				}
			}

			int charCount = 1;
			boolean found = false;
			for (int i = index + 1; i < content.length(); i++) {
				char wordChar = content.charAt(i);

				node = node.getChild(wordChar);
				if (node != null) {
					charCount++;
				} else {
					break;
				}

				if (partMatch && node.isWord()) {
					found = true;
					if (callback.call(StringUtils.substring(content, index, index + charCount))) {
						return true;
					}
					break;
				} else if (node.isWord()) {
					found = true;
					if (callback.call(StringUtils.substring(content, index, index + charCount))) {
						return true;
					}
				}

				if (node.isLeaf()) {
					break;
				}
			}

			if (partMatch && found) {
				index += (charCount - 1);//最后要i++，所以这里要-1
			}
		}

		return false;
	}

}
