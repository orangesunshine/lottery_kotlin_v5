package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * 江苏快三彩种计算器
 */
public class K3Caculate {
	
	public static int stake(String stake, int playID) throws Exception {
		//将投注号码放入二维数组
		int _Count = 0;
		List<Selector> selectors  = null;
		switch (playID) {
			/** 和值直选  **/	
			case 2745:
			case 2753:
			case 2769:
			case 2761:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "和值直选", "3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|大|小|单|双", 0, 16);
				//计算注數
				_Count = GameLogic.count(stake, "0", "和值直选", GameMethod.HZBZ, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;				
			case 2746:
			case 2754:
			case 2770:
			case 2762:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "二同号复式", "11|22|33|44|55|66", 0, 6);
				//计算注數
				_Count = GameLogic.count(stake, "0", "二同号复式", GameMethod.ETHFS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;					
			case 2747:
			case 2755:
			case 2771:
			case 2763:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "二不同号标准", "1|2|3|4|5|6", 2, 6);
				//计算注數
				_Count = GameLogic.count(stake, "1,2", "二不同号标准", GameMethod.EBTHBZ2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;						
			case 2749:
			case 2757:
			case 2773:
			case 2765:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "三同号单选", "111|222|333|444|555|666", 0, 6);
				//计算注數
				_Count = GameLogic.count(stake, "0", "三同号单选", GameMethod.STHDX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;				
			case 2748:
			case 2756:
			case 2772:
			case 2764:
				//构建解析器
				selectors = GameLogic.createSelectors(1, "三同号通选", "全", 0, 1);
				//计算注數
				_Count = GameLogic.count(stake, "0", "三同号通选", GameMethod.STHTX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;				
			case 2750:
			case 2758:
			case 2774:
			case 2766:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "三不同号标准", "1|2|3|4|5|6", 3, 6);
				//计算注數
				_Count = GameLogic.count(stake, "1,2,3", "三不同号标准", GameMethod.SBTHBZ3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;					
			case 2752:
			case 2760:
			case 2776:
			case 2768:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "三连号单选", "123|234|345|456", 0, 4);
				//计算注數
				_Count = GameLogic.count(stake, "0", "三连号单选", GameMethod.SLHDX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;					
			case 2751:	
			case 2759:
			case 2775:
			case 2767:	
				//构建解析器
				selectors = GameLogic.createSelectors(1, "三连号通选", "全", 0, 1);
				//计算注數
				_Count = GameLogic.count(stake, "0", "三连号通选", GameMethod.SLHTX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
				break;					
		}
		return _Count;
	}

}
