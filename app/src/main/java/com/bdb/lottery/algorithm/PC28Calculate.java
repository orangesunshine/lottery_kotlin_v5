package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * PC蛋蛋玩法计算器
 * Created by XB on 2016/4/12.
 */
public class PC28Calculate {

    public static int stake(String _Stake, int _PlayID) throws Exception {
        int _Count = 0;
        List<Selector> selectors  = null;
        //根据ID计算
        switch (_PlayID) {
            case 3380://三星直选复式
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星直选复式", GameMethod.ZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            case 3381://三星 直选单式
                //计算注數
                _Count = GameLogic.count(_Stake, "", "三星直选单式", GameMethod.ZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3384://三星组选组三
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星组三复式", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星组三复式", GameMethod.ZU3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3383://三星组选组六
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任三组选组六", "0|1|2|3|4|5|6|7|8|9", 3, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "任三组选组六", GameMethod.ZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 3385://二星直选复式
            case 3387:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星直选复式", GameMethod.ZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, true);
                break;
            case 3386://二星直选单式
            case 3388:
                _Count = GameLogic.count(_Stake, "", "二星直选单式", GameMethod.ZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 3389://二星 组选复式
            case 3390:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选组二", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星组选组二", GameMethod.ZU2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 3391://定位胆
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星定位胆", "0|1|2|3|4|5|6|7|8|9", 0, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星定位胆", GameMethod.DWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 3, false);
                break;
            case 3392:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "总和", "大|小|单|双", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "总和", GameMethod.ZH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3200:
            case 3393:
            case 3394:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "大小单双", "大|小|单|双", 1, 16);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "大小单双", GameMethod.DXDS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 3382:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星直选和值", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27", 1, 28);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星直选和值", GameMethod.ZXHZ3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
        }
        return _Count;
    }

}
