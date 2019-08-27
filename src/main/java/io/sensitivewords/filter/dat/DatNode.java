package io.sensitivewords.filter.dat;

import java.util.HashSet;
import java.util.Set;

/**
 * 双数组脏词缓存节点
 */
class DatNode {

	//脏字库
    private Set<Character> chars = new HashSet<>();
    
    //敏感词库
    private Set<String> words = new HashSet<>();

	public Set<Character> getChars() {
		return chars;
	}

	public Set<String> getWords() {
		return words;
	}

}
