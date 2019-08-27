package io.sensitivewords.filter.dat;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 双数组脏词缓存节点
 */
class DatNode {

	//脏字库
    private BitSet charsBitSet = new BitSet(Character.MAX_VALUE);
    
    //敏感词库
    private final Set<String> words = new HashSet<>();

	public BitSet getCharsBitSet() {
		return charsBitSet;
	}

	public Set<String> getWords() {
		return words;
	}

}
