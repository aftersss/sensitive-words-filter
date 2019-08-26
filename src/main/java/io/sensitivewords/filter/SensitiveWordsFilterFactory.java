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

	public enum SensitiveWordsFilterType {
		DFA(DfaFilter.class),
		TIRE(TireTreeFilter.class),
		DAT(DatFilter.class),
		BUCKET(HashBucketFilter.class),
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
