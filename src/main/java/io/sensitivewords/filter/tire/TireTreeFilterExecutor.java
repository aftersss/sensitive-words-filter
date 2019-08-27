package io.sensitivewords.filter.tire;

import io.sensitivewords.filter.AbstractFilterExecutor;
import org.apache.commons.lang.StringUtils;

/**
 * tire tree 算法脱敏词库支持类
 * 
 */
final class TireTreeFilterExecutor extends AbstractFilterExecutor {

	private final TireTreeNode rootNode = new TireTreeNode(' ');

	@Override
	public boolean put(String word) {
		if (StringUtils.isBlank(word)) {
			return false;
		}

		word = StringUtils.trim(word);

//		char firstChar = word.charAt(0);
//		TireTreeNode node = tireTreeNode.findChild(firstChar);
//		if (node == null) {
//			node = new TireTreeNode(firstChar);
//			tireTreeNode.addChildIfNotPresent(node);
//		}

		TireTreeNode node = rootNode;
		for (int i = 0; i < word.length(); i++) {
			char nextChar = word.charAt(i); // 转换成char型

			TireTreeNode nextNode = node.findChild(nextChar);
			if (nextNode == null) {
				nextNode = new TireTreeNode(nextChar);
			}
			if (i == word.length() - 1) {
				nextNode.setWord(true);
			}

			node.addChildIfNotPresent(nextNode);
			node = nextNode;
		}

		return true;
	}

	/**
	 * 判断一段文字包含敏感词语，支持敏感词结果回调
	 * @param partMatch 是否部分匹配; 比如content为"中国民",敏感词库中有两个敏感词:"中国","国民",则如果partMatch=true，匹配到的敏感词为：["中国"], 反之匹配到:["中国"，"国民"],也就是说partMatch=false会匹配到重叠的部分
	 * @param content 被匹配内容
	 * @return 是否匹配到的词语
	 */
	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}
		
		content = StringUtils.trim(content);

		for (int index = 0; index < content.length();index++) {
			char firstChar = content.charAt(index);
			
			TireTreeNode node = rootNode.findChild(firstChar);
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
				
				node = node.findChild(wordChar);
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
				index += (charCount - 1); //最后要i++，所以这里要-1
			}
		}
		
		return false;
	}

}
