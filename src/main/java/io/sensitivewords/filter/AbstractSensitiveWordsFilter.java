package io.sensitivewords.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象过滤脱敏接口实现
 */
public abstract class AbstractSensitiveWordsFilter implements SensitiveWordsFilter {

	private static final String HTML_HIGHLIGHT = "<font color='red'>%s</font>";

	/**
	 * 匹配到敏感词的回调接口
	 */
	protected interface Callback {

		/**
		 * 匹配掉敏感词回调
		 * @param word 敏感词
		 * @return true 立即停止后续任务并返回，false 继续执行
		 */
		boolean call(String word);
	}

	/**
	 * 判断一段文字包含敏感词语，支持敏感词结果回调
	 * @param partMatch 是否部分匹配; 比如content为"中国民",敏感词库中有两个敏感词:"中国","国民",则如果partMatch=true，匹配到的敏感词为：["中国"], 反之匹配到:["中国"，"国民"],也就是说partMatch=false会匹配到重叠的部分
	 * @param content 被匹配内容
	 * @param callback 回调接口
	 * @return 是否匹配到的词语
	 */
	protected abstract boolean processor(boolean partMatch, String content, Callback callback);

	@Override
	public boolean contains(String content) {
		return processor(true, content, word -> {
			return true; // 有敏感词立即返回
		});
	}

	@Override
	public String getOneWord(String content) {
		final AtomicReference<String> ref = new AtomicReference<>();

		processor(true, content, word -> {
			ref.set(word);
			return true; // 匹配到任意一个敏感词后停止继续匹配
		});

		return ref.get();
	}

	@Override
	public Set<String> getWords(boolean partMatch, String content) {
		final Set<String> words = new HashSet<>();

		processor(partMatch, content, word -> {
			words.add(word);
			return false; // 继续匹配后面的敏感词
		});

		return words;
	}

	@Override
	public String highlight(String content, String template) {
		if (template == null) {
			template = HTML_HIGHLIGHT;
		}
		Set<String> words = this.getWords(true, content);

		Iterator<String> iter = words.iterator();
		while (iter.hasNext()) {
			String word = iter.next();
			content = content.replaceAll(word, String.format(template, word));
		}

		return content;
	}

	@Override
	public String filter(String content, char replaceChar) {
		Set<String> words = this.getWords(true, content);

		Iterator<String> iter = words.iterator();
		while (iter.hasNext()) {
			String word = iter.next();
			content = content.replaceAll(word, repeat(String.valueOf(replaceChar), word.length()));
		}

		return content;
	}

	private String repeat(String str, int repeatNums){
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<repeatNums;i++) {
			sb.append(str);
		}

		return sb.toString();
	}

}
