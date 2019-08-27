package io.sensitivewords.filter.dat;

import io.sensitivewords.filter.AbstractFilter;

/**
 * DAT 算法实现敏感词脱敏过滤
 */
public class DatFilter extends AbstractFilter {

	public DatFilter() {
		super(new DatFilterExecutor());
	}
}
