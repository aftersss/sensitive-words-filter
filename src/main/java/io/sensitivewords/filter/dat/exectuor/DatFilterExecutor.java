package io.sensitivewords.filter.dat.exectuor;

import org.apache.commons.lang.StringUtils;

import io.sensitivewords.filter.AbstractFilterExecutor;

/**
 * 双数组算法过滤敏感词
 */
public final class DatFilterExecutor extends AbstractFilterExecutor {

	private DatCacheNode datCacheNode = new DatCacheNode();

	@Override
	public boolean put(String word) {
		if (StringUtils.isBlank(word)) {
			return false;
		}
		
		word = StringUtils.trim(word);

		datCacheNode.getWords().add(word);
		
        for (Character character : word.toCharArray()) {
        	datCacheNode.getChars().add(character);
        }
        
		return true;
	}

	@Override
	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}
		
		content = StringUtils.trim(content);

		for (int i = 0; i < content.length(); i++) {
            Character wordChar = content.charAt(i);
            // 判断是否属于脏字符
            if (!datCacheNode.getChars().contains(wordChar)) {
                continue;
            }

			String str = wordChar.toString();
			if (datCacheNode.getWords().contains(str)) {
				if (callback.call(str)) {
					return true;
				}

				if (partMatch) {
					continue;
				}
			}

            int j = i + 1;
            while (j < content.length()) {
            	// 判断下一个字符是否属于脏字符
            	wordChar = content.charAt(j);
                if (!datCacheNode.getChars().contains(wordChar)) {
                    break;
                }
                
                String word = content.substring(i, j + 1);
                // 判断是否是脏词
                if (datCacheNode.getWords().contains(word)) {
                	if (callback.call(word)) {
                		return true;
                	}

                	if (partMatch) {
                		i += (word.length() - 1);
                	} 
                }
                
                j++;
            }
        }
		
		return false;
	}

}
