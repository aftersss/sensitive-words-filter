package io.sensitivewords.filter.dfa.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * dfa多叉树模型
 */
public class DfaNode {

	private final char _char;
	/**
	 * 如果为true表示是敏感词的最后一个单词
	 */
	private volatile boolean word;
	private volatile Map<Character, DfaNode> childes;

	public DfaNode(char _char) {
		this._char = _char;
	}

	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}
	
	public boolean isLeaf() {
		Map<Character, DfaNode> childesTemp = childes;
		return (childesTemp == null || childesTemp.isEmpty());
	}

	public char getChar() {
		return _char;
	}

	public synchronized void addChildIfNotPresent(DfaNode child) {
		Map<Character, DfaNode> childesTemp = childes;
		if (childesTemp != null && childesTemp.containsKey(child._char)) {
			return;
		}

		Map<Character, DfaNode> copyOnWriteMap;
		if (childesTemp == null) {
			copyOnWriteMap = new HashMap<>();
		} else {
			copyOnWriteMap = new HashMap<>(childesTemp);
		}

		copyOnWriteMap.put(child.getChar(), child);
		childes = copyOnWriteMap;

	}

	public DfaNode getChild(Character _char){
		Map<Character, DfaNode> childesTemp = childes;
		if (childesTemp == null || childesTemp.isEmpty()) {
			return null;
		}

		return childesTemp.get(_char);
	}

	public void print(DfaNode node) {
		System.out.println(node.getChar());
		Map<Character, DfaNode> childesTemp = node.childes;
		if (childesTemp != null) {
			Set<Character> keys = childesTemp.keySet();
			for (Character _char: keys) {
				print(childesTemp.get(_char));
			}
		}
	}

	@Override
	public String toString() {
		return _char +
				"(" + word +
				") childes=" + childes;
	}

}
