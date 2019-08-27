package io.sensitivewords.filter.dfa;

import io.sensitivewords.filter.AbstractFilter;

/**
 * DFA 算法实现敏感词脱敏过滤
 */
public class DfaFilter extends AbstractFilter {

	public DfaFilter() {
		super(new DfaFilterExecutor());
	}
}
