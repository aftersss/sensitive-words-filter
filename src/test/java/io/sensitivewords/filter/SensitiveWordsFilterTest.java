package io.sensitivewords.filter;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * 敏感词库测试
 */
public class SensitiveWordsFilterTest extends TestCase {

	@SuppressWarnings("unchecked")
	public void setUp() {
	}

	public void testFilters() {
		SensitiveWordsFilter filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.DFA);
		testFilterInner(filter, SensitiveWordsFilterFactory.SensitiveWordsFilterType.DFA);

		filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.TIRE);
		testFilterInner(filter, SensitiveWordsFilterFactory.SensitiveWordsFilterType.TIRE);

		filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.BUCKET);
		testFilterInner(filter, SensitiveWordsFilterFactory.SensitiveWordsFilterType.BUCKET);

		filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.DAT);
		testFilterInner(filter, SensitiveWordsFilterFactory.SensitiveWordsFilterType.DAT);

//		filter = SensitiveWordsFilterFactory.newSensitiveWordsFilter(SensitiveWordsFilterFactory.SensitiveWordsFilterType.TTMP);
//		testFilterInner(filter);
	}

	private void testFilterInner(SensitiveWordsFilter filter, SensitiveWordsFilterFactory.SensitiveWordsFilterType type){
		filter.put("中国人");
		filter.put("国民");
		filter.put("中国男人");
		filter.put("中国人民");
		filter.put("人民");
		filter.put("中间");
		filter.put("女人");

		filter.put("一举");
		filter.put("一举成名");
		filter.put("一举成名走四方");
		filter.put("成名");
		filter.put("走四方");
		filter.put("屌");
		filter.put("屌人");

		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很高，中国人民经常吃饭。";
		Assert.assertTrue(filter.contains(content));
		Assert.assertTrue(filter.getWords(true, content).contains("中国人"));
		Assert.assertTrue(filter.getWords(true, content).contains("女人"));
		Assert.assertTrue(filter.getWords(true, content).contains("中间"));
		Assert.assertTrue(filter.getWords(true, content).contains("中国男人"));

		Assert.assertTrue(filter.getWords(false, content).contains("中国人"));
		Assert.assertTrue(filter.getWords(false, content).contains("女人"));
		Assert.assertTrue(filter.getWords(false, content).contains("中间"));
		Assert.assertTrue(filter.getWords(false, content).contains("中国男人"));
		Assert.assertTrue(filter.getWords(false, content).contains("中国人民"));
		Assert.assertTrue(filter.getWords(false, content).contains("人民"));

		Assert.assertEquals(filter.filter(content, '*'), "我们***都是好人，在他们**有男人和**。****很高，***民经常吃饭。");
		Assert.assertEquals(filter.highlight(content, null), "我们<font color='red'>中国人</font>都是好人，在他们<font color='red'>中间</font>有男人和<font color='red'>女人</font>。<font color='red'>中国男人</font>很高，<font color='red'>中国人</font>民经常吃饭。");

		content = "一举成名走四方的是什么";
		Assert.assertTrue(filter.getWords(true, content).contains("成名"));
		Assert.assertTrue(filter.getWords(true, content).contains("走四方"));
		Assert.assertTrue(filter.getWords(true, content).contains("一举"));

		Assert.assertTrue(filter.getWords(false, content).contains("成名"));
		Assert.assertTrue(filter.getWords(false, content).contains("走四方"));
		Assert.assertTrue(filter.getWords(false, content).contains("一举成名"));
		Assert.assertTrue(filter.getWords(false, content).contains("一举成名走四方"));

		Assert.assertEquals(filter.filter(content, '*'), "*******的是什么");
		Assert.assertEquals(filter.highlight(content, null), "<font color='red'>一举</font><font color='red'>成名</font><font color='red'>走四方</font>的是什么");

		content = "中国民";
		Assert.assertTrue(filter.getWords(true, content).contains("国民"));
		Assert.assertTrue(filter.getWords(false, content).contains("国民"));
		Assert.assertEquals(filter.filter(content, '*'), "中**");
		Assert.assertEquals(filter.highlight(content, null), "中<font color='red'>国民</font>");

		content = "中国人民很多屌人的";
		Assert.assertTrue(filter.getWords(true, content).contains("屌"));
		if(SensitiveWordsFilterFactory.SensitiveWordsFilterType.BUCKET != type) {
			Assert.assertTrue(!filter.getWords(true, content).contains("屌人"));
		}
		Assert.assertTrue(filter.getWords(false, content).contains("屌"));
		Assert.assertTrue(filter.getWords(false, content).contains("屌人"));
	}

}
