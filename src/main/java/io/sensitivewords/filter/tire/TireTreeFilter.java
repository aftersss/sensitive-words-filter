package io.sensitivewords.filter.tire;

import io.sensitivewords.filter.AbstractFilter;

/**
 * trie 树算法实现敏感词脱敏过滤
 * 
 */
public class TireTreeFilter extends AbstractFilter {

	public TireTreeFilter() {
		super(new TireTreeFilterExecutor());
	}
}
