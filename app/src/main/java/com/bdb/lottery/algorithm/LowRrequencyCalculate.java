package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * Created by XB on 2016/4/12.
 */
public class LowRrequencyCalculate {

    public static int stake(String _Stake, int _PlayID) throws Exception {
        int _Count = 0;
        List<Selector> selectors  = null;
        //根据ID计算
        switch (_PlayID) {
            case 125://排列三-(三星直选复式)
            case 87://时时乐-(三星直选复式)
            case 106://福彩3D-(三星直选复式)
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星直选复式", GameMethod.ZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            case 88://时时乐-(三星 直选单式)
            case 107://福彩3D-(三星 直选单式)
            case 126://排列三-(三星 直选单式)
                //计算注數
                _Count = GameLogic.count(_Stake, "", "三星直选单式", GameMethod.ZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 90://时时乐-(三星组选组三)
            case 109://福彩3D-(三星组选组三)
            case 128://排列三-(三星组选组三)
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星组三复式", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星组三复式", GameMethod.ZU3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 89://时时乐-(三星组选组六)
            case 108://福彩3D-(三星组选组六)
            case 127://排列三-(三星组选组六)
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任三组选组六", "0|1|2|3|4|5|6|7|8|9", 3, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "任三组选组六", GameMethod.ZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 91://时时乐-(三星组选混合)
            case 110://福彩3D-(三星组选混合)
            case 129://排列三-(三星组选混合)
                _Count = GameLogic.count(_Stake, "", "组选混合", GameMethod.ZUHH3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 92://时时乐-(二星 直选复式)
            case 94:
            case 111://福彩3D-(二星直选复式)
            case 113:
            case 130://排列三-(二星直选复式)
            case 132:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星直选复式", GameMethod.ZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, true);
                break;
            case 93://时时乐-(二星 直选单式)
            case 95:
            case 112://福彩3D-(二星 直选单式)
            case 114:
            case 131://排列三-(二星直选单式)
            case 133:
                _Count = GameLogic.count(_Stake, "", "二星直选单式", GameMethod.ZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 96://时时乐-(二星 组选组二)
            case 98:
            case 115://福彩3D-(二星 组选组二)
            case 117:
            case 134://排列三-(二星 组选组二)
            case 136:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选组二", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星组选组二", GameMethod.ZU2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 97://时时乐-(二星 组选混合)
            case 99:
            case 116://福彩3D-(二星 组选混合)
            case 118:
            case 135://排列三-(二星 组选混合)
            case 137:
                _Count = GameLogic.count(_Stake, "", "二星组选混合", GameMethod.ZU2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 119://一码不定位胆
            case 138://一码不定位胆
                //构建解析器
                selectors = GameLogic.createSelectors(1, "一码不定位胆", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "一码不定位胆", GameMethod.BDW1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 481://福彩3D-(二码不定位胆)
            case 482://排列三-(二码不定位胆)
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二码不定位胆", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二码不定位胆", GameMethod.BDW2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 101://时时乐-(三星定位胆)
            case 120://福彩3D-(三星定位胆)
            case 139://排列三-(三星定位胆)
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星定位胆", "0|1|2|3|4|5|6|7|8|9", 0, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星定位胆", GameMethod.DWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 3, false);
                break;
        }
        return _Count;
    }

}
