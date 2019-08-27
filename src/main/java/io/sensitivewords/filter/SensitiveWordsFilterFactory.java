package io.sensitivewords.filter;

import io.sensitivewords.filter.bucket.HashBucketFilter;
import io.sensitivewords.filter.dat.DatFilter;
import io.sensitivewords.filter.dfa.DfaFilter;
import io.sensitivewords.filter.tire.TireTreeFilter;
import io.sensitivewords.filter.ttmp.TtmpFilter;

/**
 * 敏感词库接口工厂
 */
public class SensitiveWordsFilterFactory {
	private SensitiveWordsFilterFactory(){}

	/**
	 * 创建一个敏感词过滤的实例
	 * @param type
	 * @return
	 */
	public static SensitiveWordsFilter newSensitiveWordsFilter(SensitiveWordsFilterType type){
		return type.makeInstance();
	}

	/**
	 * 敏感词过滤算法枚举类，建议使用TIRE算法
	 */
	public enum SensitiveWordsFilterType {

		/**
		 * 字典树敏感词过滤算法，算法表现良好，在内存占用和速度上比较均衡，适用情况比较广，比较实用；
		 */
		TIRE(TireTreeFilter.class),

		/**
		 * hash桶敏感词过滤算法， 算法表现良好，在内存占用和速度上比较均衡；
		 */
		HASH_BUCKET(HashBucketFilter.class),

		/**
		 * dfa敏感词过滤算法， 算法用的内存最多，但速度最快。
		 */
		DFA(DfaFilter.class),

		/**
		 * 双数组敏感词过滤算法， 算法用的内存最少，但速度最慢.
		 */
		DAT(DatFilter.class),

		/**
		 * ttmp敏感词过滤算法， 存在漏词的问题, 不建议使用。
		 */
		TTMP(TtmpFilter.class);

		private Class<? extends SensitiveWordsFilter> filterClass;

		SensitiveWordsFilterType(Class<? extends SensitiveWordsFilter> filterClass){
			this.filterClass = filterClass;
		}

		private SensitiveWordsFilter makeInstance(){
			try {
				return filterClass.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
