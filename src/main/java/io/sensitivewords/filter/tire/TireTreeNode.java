package io.sensitivewords.filter.tire;

import java.util.ArrayList;
import java.util.List;

/**
 * 多叉树模型
 */
class TireTreeNode {

	private final char _char;
	private volatile boolean word;
	private volatile List<TireTreeNode> childes;

	public TireTreeNode(char _char) {
		this._char = _char;
	}
	
	public boolean isLeaf() {
		List<TireTreeNode> childesTemp = childes;
		return (childesTemp == null || childesTemp.isEmpty());
	}
	
	public char getChar() {
		return _char;
	}

	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}
	
	public synchronized void addChildIfNotPresent(TireTreeNode child) {
		List<TireTreeNode> childesTemp = childes;
		if (childesTemp != null) {
			if (childesTemp.stream().anyMatch(node -> node._char == child._char)) {
				return;
			}
		}

		List<TireTreeNode> childesCopyOnWrite;
		if (childesTemp == null) {
			childesCopyOnWrite = new ArrayList<>();
		} else {
			childesCopyOnWrite = new ArrayList<>(childesTemp);
		}
		childesCopyOnWrite.add(child);
		this.childes = childesCopyOnWrite;
	}

	public TireTreeNode findChild(char _char) {
		List<TireTreeNode> childesTemp = childes;
		if (childesTemp != null) {
			for (TireTreeNode item : childesTemp) {
				if (item.getChar() == _char) {
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return _char +
				"(" + word +
				") childes=" + childes;
	}
}
