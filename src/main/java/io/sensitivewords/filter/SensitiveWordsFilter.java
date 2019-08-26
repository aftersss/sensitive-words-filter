package io.sensitivewords.filter;

import java.util.Set;

/**
 * 敏感词库接口定义
 */
public interface SensitiveWordsFilter {

	/**
	 * 指定内容中是否包含敏感字符
	 * @param content 被匹配内容
	 * @return 是否包含敏感字符
	 */
	boolean contains(String content);

	/**
	 * 返回匹配到的第一个敏感词语
	 * @param content 被匹配的语句
	 * @return 返回匹配到的第一个敏感词
	 */
	String getOneWord(String content);

	/**
	 * 返回匹配到的敏感词语
	 * @param content 被匹配的语句
	 * @return 返回匹配的敏感词语集合
	 */
	default Set<String> getWords(String content) {
		return getWords(true, content);
	}

	/**
	 * 返回匹配到的敏感词语
	 * @param partMatch 是否部分匹配; 比如content为"中国民",敏感词库中有两个敏感词:"中国","国民",则如果partMatch=true，匹配到的敏感词为：["中国"], 反之匹配到:["中国"，"国民"],也就是说partMatch=false会匹配到重叠的部分
	 * @param content 被匹配的语句
	 * @return 返回匹配的敏感词语集合
	 */
	Set<String> getWords(boolean partMatch, String content);

	/**
	 * 高亮敏感词
	 * @param content 被匹配的语句
	 * @param template 敏感词替换模板，模板中必须包含一个%s，比如
	 *                    <xmp>
	 *                        <font color='red'>%s</font>
	 *                    </xmp>
	 * @return 返回html高亮敏感词
	 */
	String highlight(String content, String template);
	
	/**
	 * 过滤敏感词，并把敏感词替换为指定字符
	 *
	 * @param content 被匹配的语句
	 * @param replaceChar 替换字符, 比如'*'
	 * @return 过滤后的字符串
	 */
	String filter(String content, char replaceChar);

	/**
	 * 添加敏感词
	 * @param word 敏感词
	 * @return 添加是否成功
	 */
	boolean put(String word);


}
