package io.sensitivewords.filter;

import java.util.Set;

/**
 * 抽象过滤脱敏接口实现
 */
public abstract class AbstractFilter implements SensitiveWordsFilter {

	private AbstractFilterExecutor executor;
	
	public AbstractFilter(AbstractFilterExecutor executor) {
		this.executor = executor;
	}
	
	@Override
	public boolean contains(String content) {
		return executor.contains(content);
	}

	@Override
	public String getOneWord(String content){
		return executor.getOneWord(content);
	}

	@Override
	public Set<String> getWords(boolean partMatch, String content) {
		return executor.getWords(partMatch, content);
	}

	@Override
	public String highlight(String content, String template) {
		return executor.highlight(content, template);
	}

	@Override
	public String filter(String content, char replaceChar) {
		return executor.filter(content, replaceChar);
	}

	@Override
	public boolean put(String word) {
		return executor.put(word);
	}
}
