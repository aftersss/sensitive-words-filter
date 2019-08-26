package io.sensitivewords.filter.bucket;

import io.sensitivewords.filter.AbstractFilter;
import io.sensitivewords.filter.bucket.executor.HashBucketFilterExecutor;

/**
 * hash bucket 脱敏过滤算法实现
 */
public class HashBucketFilter extends AbstractFilter {

	public HashBucketFilter() {
		super(new HashBucketFilterExecutor());
	}
}
