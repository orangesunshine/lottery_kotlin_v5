package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * Created by XB on 2017/11/13.
 */

public class KL10FCaculate {

    public static int stake(String stake, int playID) throws Exception {
        int count = 0;
        List<Selector> selectors  = null;
        switch (playID) {
            /** 三星直选复式  **/
            case 3229:
            case 3226:
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星直选", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 1, 20);
                //计算注數
                count = GameLogic.count(stake, "0,1,2", "三星直选", GameMethod.KLZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            /** 三星组选 **/
            case 3235:
            case 3232:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星组选", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 3, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "三星组选", GameMethod.KLZUX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3241:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星直选", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 1, 20);
                //计算注數
                count = GameLogic.count(stake, "0,1", "二星直选", GameMethod.KLZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 3238:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 2, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "二星组选", GameMethod.KLZUX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3244:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "一中一", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 1, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "一中一", GameMethod.KLRX1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3247:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二中二", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 2, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "二中二", GameMethod.KLRX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3250:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三中三", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 3, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "三中三", GameMethod.KLRX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3253:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "四中四", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 4, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "四中四", GameMethod.KLRX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3256:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "五中五", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 5, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "五中五", GameMethod.KLRX5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3259:
                //构建解析器
                selectors = GameLogic.createSelectors(8, "大小单双", "大|小|单|双|尾大|尾小|合单|合双", 0, 8);
                //计算注數
                count = GameLogic.count(stake, "0,1,2,3,4,5,6,7", "大小单双", GameMethod.KLDXDS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 8, false);
                break;
            case 3262:
                //构建解析器
                selectors = GameLogic.createSelectors(8, "五行", "金|木|水|火|土", 0, 5);
                //计算注數
                count = GameLogic.count(stake, "0,1,2,3,4,5,6,7", "五行", GameMethod.KLWX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 8, false);
                break;
            case 3265:
                //构建解析器
                selectors = GameLogic.createSelectors(8, "四季方位", "春|夏|秋|冬|东|南|西|北", 0, 8);
                //计算注數
                count = GameLogic.count(stake, "0,1,2,3,4,5,6,7", "四季方位", GameMethod.KLSJFW, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 8, false);
                break;
            case 3268:
            case 3271:
            case 3274:
            case 3277:
            case 3280:
            case 3283:
            case 3286:
            case 3289:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "定位胆", "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20", 1, 20);
                //计算注數
                count = GameLogic.count(stake, "0", "定位胆", GameMethod.KLDWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3292:
            case 3294:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "龙虎", "1V2|1V3|1V4|1V5|1V6|1V7|1V8|2V3|2V4|2V5|2V6|2V7|2V8|3V4|3V5|3V6|3V7|3V8|4V5|4V6|4V7|4V8|5V6|5V7|5V8|6V7|6V8|7V8", 1, 28);
                //计算注數
                count = GameLogic.count(stake, "0", "龙虎", GameMethod.KLNH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
        }
        return count;
    }

}
