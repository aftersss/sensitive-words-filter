package io.sensitivewords.filter.tire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 字典树模型
 */
class TireTreeNode {

	private final char _char;
	private volatile boolean word;
	private volatile TireTreeNode[] childes;

	public TireTreeNode(char _char) {
		this._char = _char;
	}
	
	public boolean isLeaf() {
		TireTreeNode[] childesTemp = childes;
		return (childesTemp == null || childesTemp.length == 0);
	}
	
	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}
	
	public synchronized void addChildIfNotPresent(TireTreeNode child) {
		TireTreeNode[] childesTemp = childes;
		if (childesTemp != null) {
			int i = Arrays.binarySearch(childesTemp, child, Comparator.comparingInt(o -> o._char));
			if (i >= 0) {
				return;
			}
		}

		List<TireTreeNode> childesCopyOnWrite;
		if (childesTemp == null) {
			childesCopyOnWrite = new ArrayList<>();
		} else {
			childesCopyOnWrite = new ArrayList<>(Arrays.asList(childesTemp));
		}
		childesCopyOnWrite.add(child);
		childesCopyOnWrite.sort(Comparator.comparingInt(o -> o._char));
		this.childes = childesCopyOnWrite.toArray(new TireTreeNode[childesCopyOnWrite.size()]);
	}

	public TireTreeNode findChild(char _char) {
		TireTreeNode[] childesTemp = childes;
		if (childesTemp != null) {
			int i = Arrays.binarySearch(childesTemp, new TireTreeNode(_char), Comparator.comparingInt(o -> o._char));
			if (i >= 0) {
				return childesTemp[i];
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
