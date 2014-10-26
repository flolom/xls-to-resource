package com.hotcocoacup.mobiletools.xlstoresouces;

import java.util.Comparator;

public class GroupByComparator implements Comparator<String> {

	public int compare(String o1, String o2) {
		
		int compare = o1.compareTo(o2);
		
		if (compare != 0) {
			
			if ("".equals(o1)) {
				return -1;
			} else if ("".equals(o2)) {
				return 1;
			} else {
				return compare;
			}
			
		} else {
			return compare;
		}
	}

}
