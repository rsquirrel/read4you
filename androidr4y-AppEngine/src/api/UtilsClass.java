package api;

public class UtilsClass {
	static public int convertPageNum(String page_str) {
		int page_num;
		if (page_str != null) {
			try {
				page_num = Integer.parseInt(page_str);
				if (page_num < 1) {
					page_num = 1;
				}
			} catch (NumberFormatException e) {
				page_num = 1;
			}
		} else {
			page_num = 1;
		}
		return page_num;
	}
}
