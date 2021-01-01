package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * Created by XB on 2016/4/14.
 */
public class PK10Caculate {

    public static int stake(String _Stake, int _PlayID, String _BetBit) throws Exception {
        //将投注号码放入二维数组
        int _Count = 0;
        List<Selector> selectors;
        switch (_PlayID) {
            /** 前一 直选复式 **/
            case 1835:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "前一直选复式", "01|02|03|04|05|06|07|08|09|10", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "前一直选复式", GameMethod.PKZX1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3330:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "前一直选复式", "01|02|03|04|05|06|07|08", 1, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "前一直选复式", GameMethod.PKZX1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            /** 前二 直选复式  **/
            case 1836:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "前二直选复式", "01|02|03|04|05|06|07|08|09|10", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "前二直选复式", GameMethod.PKZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 3308://PK8
                //构建解析器
                selectors = GameLogic.createSelectors(2, "前二直选复式", "01|02|03|04|05|06|07|08", 1, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "前二直选复式", GameMethod.PKZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            /** 前二 直选单式  **/
            case 1837:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前二直选单式", GameMethod.PKZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3309://PK8
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前二直选单式", GameMethod.PK8ZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            /** 前三 直选复式  **/
            case 1838:
                //构建解析器
                selectors = GameLogic.createSelectors(3, "前三直选复式", "01|02|03|04|05|06|07|08|09|10", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "前三直选复式", GameMethod.PKZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            case 3310://PK8
                //构建解析器
                selectors = GameLogic.createSelectors(3, "前三直选复式", "01|02|03|04|05|06|07|08", 1, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "前三直选复式", GameMethod.PKZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            /** 前三 直选单式 **/
            case 1839:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前三直选单式 ", GameMethod.PKZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3311://PK8
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前三直选单式 ", GameMethod.PK8ZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            /** 前四 直选复式  **/
            case 3202:
                //构建解析器
                selectors = GameLogic.createSelectors(4, "前四直选复式", "01|02|03|04|05|06|07|08|09|10", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3", "前四直选复式", GameMethod.PKZX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 4, 4, false);
                break;
            case 3313://PK8
                //构建解析器
                selectors = GameLogic.createSelectors(4, "前四直选复式", "01|02|03|04|05|06|07|08", 1, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3", "前四直选复式", GameMethod.PKZX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 4, 4, false);
                break;
            /** 前四 直选单式 **/
            case 3203:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前四直选单式 ", GameMethod.PKZX4, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3314://PK8
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前四直选单式 ", GameMethod.PK8ZX4, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            /** 前五 直选复式  **/
            case 3204:
                //构建解析器
                selectors = GameLogic.createSelectors(5, "前五直选复式", "01|02|03|04|05|06|07|08|09|10", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4", "前五直选复式", GameMethod.PKZX5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 5, 5, false);
                break;
            case 3315://PK8
                //构建解析器
                selectors = GameLogic.createSelectors(5, "前五直选复式", "01|02|03|04|05|06|07|08", 1, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4", "前五直选复式", GameMethod.PKZX5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 5, 5, false);
                break;

            /** 前五 直选单式 **/
            case 3205:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "前五直选单式 ", GameMethod.PKZX5, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3316://PK8
                _Count = GameLogic.count(_Stake, "", "前五直选单式 ", GameMethod.PK8ZX5, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3206:
            case 3207:
            case 3208:
            case 3209:
            case 3210:
            case 3317://PK8 龙虎-1V8
            case 3318://PK8 龙虎-2V7
            case 3319: //PK8 龙虎-3V6
            case 3320: //PK8 龙虎-4V5
                //构建解析器
                selectors = GameLogic.createSelectors(1, "龙虎", "龙|虎", 1, 2);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "龙虎", GameMethod.LH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3218:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "3,4,18,19", "3|4|18|19", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "3,4,18,19", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3322: //PK8 冠亚车和-3,4,14,15
                //构建解析器
                selectors = GameLogic.createSelectors(1, "3,4,14,15", "3|4|14|15", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "3,4,18,19", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3219:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "5,6,16,17", "5|6|16|17", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "5,6,16,17", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3323: //PK8 冠亚车和-5,6,12,13
                //构建解析器
                selectors = GameLogic.createSelectors(1, "5,6,12,13", "5|6|12|13", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "5,6,12,13", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3220:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "7,8,14,15", "7|8|14|15", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "7,8,14,15", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3324://PK8 冠亚车和-7,8,10,11
                //构建解析器
                selectors = GameLogic.createSelectors(1, "7,8,10,11", "7|8|10|11", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "9,10,12,13", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3221:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "9,10,12,13", "9|10|12|13", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "9,10,12,13", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3222:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19", "3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19", 1, 17);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3326: //PK8 冠亚车和-9
                //构建解析器
                selectors = GameLogic.createSelectors(1, "3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19", "3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19", 1, 17);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;

            case 3223:
            case 3327://PK8 冠亚车和-大,双
                //构建解析器
                selectors = GameLogic.createSelectors(1, "大,双", "大|双", 1, 2);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "大,双", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3224:
            case 3328://PK8  冠亚车和-大,小,单,双
                //构建解析器
                selectors = GameLogic.createSelectors(1, "大,小,单,双", "大|小|单|双", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "大,小,单,双", GameMethod.GYJH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;

            /** 定位胆  **/
            case 1840:
                //构建解析器
                selectors = GameLogic.createSelectors(10, "定位胆", "01|02|03|04|05|06|07|08|09|10", 0, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4,5,6,7,8,9", "定位胆", GameMethod.PKDWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 10, false);
                break;
            case 3312: //PK8  定位胆-定位胆
                //构建解析器
                selectors = GameLogic.createSelectors(8, "定位胆", "01|02|03|04|05|06|07|08", 0, 8);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4,5,6,7", "定位胆", GameMethod.PKDWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 8, false);
                break;
        }
        return _Count;
    }

}
