package io.sensitivewords.filter.ttmp;

import io.sensitivewords.filter.AbstractFilter;
import io.sensitivewords.filter.ttmp.executor.TtmpFilterExecutor;

/**
 * ttmp 算法过滤
 * 
 */
public class TtmpFilter extends AbstractFilter {

	public TtmpFilter() {
		super(new TtmpFilterExecutor());
	}
}
