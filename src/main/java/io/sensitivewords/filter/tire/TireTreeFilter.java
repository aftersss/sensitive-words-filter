package io.sensitivewords.filter.tire;

import io.sensitivewords.filter.AbstractFilter;
import io.sensitivewords.filter.tire.executor.TireTreeFilterExecutor;

/**
 * trie 树算法实现敏感词脱敏过滤
 * 
 */
public class TireTreeFilter extends AbstractFilter {

	public TireTreeFilter() {
		super(new TireTreeFilterExecutor());
	}
}
