package com.bdb.lottery.algorithm;


import com.bdb.lottery.utils.arr.Arrs;
import com.bdb.lottery.utils.math.Maths;
import com.bdb.lottery.utils.string.Texts;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.raistlic.common.permutation.Combination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XB on 2015/8/11.
 */
public class GameLogic {

    /**
     * @param digits 解析器个数
     * @param name   玩法名称
     * @param no     允许号码
     * @param min    最少投注个数
     * @param max    最大投注个数
     * @return
     */
    public static List<Selector> createSelectors(int digits, String name, String no, int min, int max) {
        List<Selector> list = new ArrayList<Selector>();
        for (int dt = 0; dt < digits; dt++) {
            DigitalSelector ds = new DigitalSelector();
            ds.setName(name);
            ds.setTitle(name);
            ds.setNo(no);
            ds.setSeparator(",");
            ds.setMinChosen(min);
            ds.setMaxChosen(max);
            list.add(ds);
        }
        return list;
    }

    /**
     * 计算注数
     *
     * @param content          投注内容			- 0,1,2||1,2,3||
     * @param position         投注位置			- 0,2
     * @param play             玩法名称			- 定位胆
     * @param method           玩法算法			- DWD
     * @param nonrepeatability 不可重复性			- SINGLE
     * @param selectorType     选号方式			- DIGITAL
     * @param selectors        选号器列表			- 万位、千位、百位、十位、个位
     * @param separator        分割符				- |
     * @param minchosen        最少位置个数		- 1
     * @param maxchosen        最多位置个数		- 5
     * @param isArbitrarily    是否任选			- false
     * @return 注数
     * @throws Exception
     */
    @SuppressWarnings("incomplete-switch")
    public static int count(String content, String position, String play, GameMethod method, Nonrepeatability nonrepeatability, SelectorType selectorType, List<Selector> selectors, String separator, int minchosen, int maxchosen, boolean isArbitrarily) throws Exception {

        int count = 0;                                                   // 注数
        int digits[] = Texts.INSTANCE.toIntegerArray(position, ",");           // 选择的位
        String suffix = method.name().substring(method.name().length() - 1); // 算法枚举后缀，即最后一个数字，例如：ZXZH5，suffix = 5
        int star = 0;                             // 几星

        try {
            star = Integer.valueOf(suffix);
        } catch (Exception e) {
        }

        if (StringUtils.isBlank(content)) {
            throw new Exception("投注内容不能为空");                                                                        // 异常：投注内容为空
        }


        // ======================================================== 位式 ======================================================== //

        if (selectorType == SelectorType.DIGITAL) {

            List<DigitalSelector> ds = new ArrayList<DigitalSelector>();                    // 位选择器列表
            String[][] data = new String[selectors.size()][];                      // 号码二维数组，一维：位；二维：号码
            int temp = 1;                                                   // 临时注数变量

            String[] nums = content.split(separator, -1);            // 分割位，包含空元素
            int ilen = StringUtils.split(content, separator).length; // 有包含号码的位个数，即不包含空元素的数组个数，或称有效位数

            // 校验位数，例如【后三直选】只有【百十个】3位，如果数组元素个数不等于3个则是非法注单
            if (nums.length != selectors.size()) {
                throw new Exception("投注内容有误, 位数不正确");                                                            // 异常：按位分割后的数组元素个数不等于位选择器个数
            }
            // 校验有效位数
            if (ilen < minchosen) {
                throw new Exception("投注内容有误, " + play + " 至少需要选择" + minchosen + "个位置");        // 异常：小于最少位置
            }
            if (ilen > maxchosen) {
                throw new Exception("投注内容有误, " + play + " 最多只能选择" + maxchosen + "个位置");        // 异常：大于最多位置
            }

            for (int i = 0; i < selectors.size(); i++) {
                DigitalSelector selector = (DigitalSelector) selectors.get(i);
                String[] nos = StringUtils.split(nums[i], selector.getSeparator()); // 分割号码，不包含空元素，例如【定位胆】
                // 验证有效号码个数
                if (nos.length < selector.getMinChosen()) {
                    throw new Exception(selector.getTitle() + " 至少需要选择" + selector.getMinChosen() + "个号码");        // 异常：少于最少号码个数
                }
                if (nos.length > selector.getMaxChosen()) {
                    throw new Exception(selector.getTitle() + " 最多只能选择" + selector.getMaxChosen() + "个号码");        // 异常：大于最多号码个数
                }
                // 验证号码有效性，如果该位无号码则不验证，例如【定位胆】不用每位都选号码
                if (!"".equals(nums[i])) {
                    String[] verif = nums[i].split(selector.getSeparator(), -1); // 待验证的号码数组
                    String[] allow = selector.getNo().split("\\|", -1);          // 允许的号码数组
                    for (String no : verif) {
                        if (!ArrayUtils.contains(allow, no)) {
                            throw new Exception("投注内容有误, 所投号码[" + no + "]不在允许号码范围内");                                    // 异常：所投号码不在允许号码范围内
                        }
                    }
                }
                ds.add(selector);
                data[i] = nos;
            }

            // 验证号码重复性
            switch (nonrepeatability) {
                case SINGLE: // 单位号码不能重复
                    for (String[] nos : data) {
                        if (Arrs.INSTANCE.duplicate(nos)) {
                            throw new Exception("投注内容有误, 单个位置的号码不能重复");
                        }
                    }
                    break;
                case ALL: // 完全不能重复
                    String[] all = null;
                    for (String[] nos : data) {
                        all = (String[]) ArrayUtils.addAll(all, nos);
                    }
                    if (Arrs.INSTANCE.duplicate(all)) {
                        throw new Exception("投注内容有误, 号码不能重复");
                    }
                    break;
            }


            switch (method) {

                // ----------------------------------------- 直选玩法 ----------------------------------------- //
                case ZX5:   // 五星直选
                case ZX4:   // 四星直选
                case ZX3:   // 三星直选
                case ZX2:   // 二星直选
                case BDW1:  // 不定位一码
                case YFFS:  // 一帆风顺
                case HSCS:  // 好事成双
                case SXBX:  // 四星报喜
                case SJFC:  // 四季发财
                case DXDS:  // 大小单双
                case HZWS3: // 三星和值尾数
                case TSH3:  // 三星特殊号
                case HZBZ:
                case ETHFS:
                case STHDX:
                case STHTX:
                case SLHDX:
                case SLHTX:
                case LH:
                case ZH:
                case GYJH:
                case KLBSXP:
                case KLBJOP:
                case KLBZHZDXDS: {
                    for (int i = 0; i < data.length; i++) { // 循环每位
                        if (data[i].length == 0) { // 如果某位号码为空，则注数为0
                            temp = 0;
                            break;
                        }
                        temp *= data[i].length;
                    }
                    count = temp;
                    break;
                }

                // ----------------------------------------- 直选组合 ----------------------------------------- //
                case ZXZH5: // 五星直选组合
                case ZXZH4: // 四星直选组合
                case ZXZH3: // 三星直选组合
                {
                    for (int i = 0; i < data.length; i++) { // 循环每位
                        if (data[i].length == 0) { // 如果某位号码为空，则注数为0
                            temp = 0;
                            break;
                        }
                        temp *= data[i].length;
                    }
                    count = temp * star;
                    break;
                }

                // ----------------------------------------- 五星组选 ----------------------------------------- //
                case WXZU120: // 五星组选120
                {
                    int len = data[0].length;
                    if (len > 4) {
                        count += Maths.INSTANCE.combination(len, 5);
                    }
                    break;
                }
                case WXZU60: // 五星组选60
                case WXZU30: // 五星组选30
                case WXZU20: // 五星组选20
                case WXZU10: // 五星组选10
                case WXZU5:  // 五星组选5
                {
                    String a[] = data[0]; // 二重号、二重号、三重号、三重号、四重号
                    String b[] = data[1]; // 　单号、　单号、　单号、二重号、　单号
                    int mina = ds.get(0).getMinChosen();
                    int minb = ds.get(1).getMinChosen();
                    if (a.length >= mina && b.length >= minb) {
                        int h = Arrs.INSTANCE.intersect(a, b).length; // 交集个数
                        temp = Maths.INSTANCE.combination(a.length, mina) * Maths.INSTANCE.combination(b.length, minb);
                        if (h > 0) {
                            switch (method) {
                                case WXZU60: // 五星组选60
                                {
                                    temp -= Maths.INSTANCE.combination(h, 1) * Maths.INSTANCE.combination(b.length - 1, 2);
                                    break;
                                }
                                case WXZU30: // 五星组选30
                                {
                                    temp -= Maths.INSTANCE.combination(h, 2) * Maths.INSTANCE.combination(2, 1);
                                    if (a.length - h > 0) {
                                        temp -= Maths.INSTANCE.combination(h, 1) * Maths.INSTANCE.combination(a.length - h, 1);
                                    }
                                    break;
                                }
                                case WXZU20: // 五星组选20
                                {
                                    temp -= Maths.INSTANCE.combination(h, 1) * Maths.INSTANCE.combination(b.length - 1, 1);
                                    break;
                                }
                                case WXZU10: // 五星组选10
                                case WXZU5:  // 五星组选5
                                {
                                    temp -= Maths.INSTANCE.combination(h, 1);
                                    break;
                                }
                            }
                        }
                        count += temp;
                    }
                    break;
                }

                // ----------------------------------------- 四星组选 ----------------------------------------- //
                case SXZU24: // 四星组选24
                case SXZU6:  // 四星组选6
                {
                    star = 4;
                    int len = data[0].length;
                    int min = ds.get(0).getMinChosen();
                    if (len >= min) {
                        count += Maths.INSTANCE.combination(len, min);
                    }
                    break;
                }
                case SXZU12: // 四星组选12
                case SXZU4:  // 四星组选4
                {
                    star = 4;
                    String a[] = data[0]; // 二重号、三重号
                    String b[] = data[1]; // 　单号、　单号
                    int mina = ds.get(0).getMinChosen();
                    int minb = ds.get(1).getMinChosen();
                    if (a.length >= mina && b.length >= minb) {
                        int h = Arrs.INSTANCE.intersect(a, b).length; // 交集个数
                        temp = Maths.INSTANCE.combination(a.length, mina) * Maths.INSTANCE.combination(b.length, minb);
                        if (h > 0) {
                            switch (method) {
                                case SXZU12: // 四星组选12
                                {
                                    temp -= Maths.INSTANCE.combination(h, 1) * Maths.INSTANCE.combination(b.length - 1, 1);
                                    break;
                                }
                                case SXZU4:  // 四星组选4
                                {
                                    temp -= Maths.INSTANCE.combination(h, 1);
                                    break;
                                }
                            }
                        }
                        count += temp;
                    }
                    break;
                }

                // ----------------------------------------- 跨度和值 ----------------------------------------- //
                case ZXKD3: // 三星直选跨度
                case ZXKD2: // 二星直选跨度
                case ZXHZ3: // 三星直选和值
                case ZXHZ2: // 二星直选和值
                case ZUHZ3: // 三星组选和值
                case ZUHZ2: // 二星组选和值
                {
                    Map<String, Integer> d = new HashMap<String, Integer>(); // 和值、跨度的固定注数
                    switch (method) {
                        case ZXKD3: // 三星直选跨度
                        {
                            d.put("0", 10);
                            d.put("1", 54);
                            d.put("2", 96);
                            d.put("3", 126);
                            d.put("4", 144);
                            d.put("5", 150);
                            d.put("6", 144);
                            d.put("7", 126);
                            d.put("8", 96);
                            d.put("9", 54);
                            break;
                        }
                        case ZXKD2: // 二星直选跨度
                        {
                            d.put("0", 10);
                            d.put("1", 18);
                            d.put("2", 16);
                            d.put("3", 14);
                            d.put("4", 12);
                            d.put("5", 10);
                            d.put("6", 8);
                            d.put("7", 6);
                            d.put("8", 4);
                            d.put("9", 2);
                            break;
                        }
                        case ZXHZ3: // 三星直选和值
                        {
                            d.put("0", 1);
                            d.put("1", 3);
                            d.put("2", 6);
                            d.put("3", 10);
                            d.put("4", 15);
                            d.put("5", 21);
                            d.put("6", 28);
                            d.put("7", 36);
                            d.put("8", 45);
                            d.put("9", 55);
                            d.put("10", 63);
                            d.put("11", 69);
                            d.put("12", 73);
                            d.put("13", 75);
                            d.put("14", 75);
                            d.put("15", 73);
                            d.put("16", 69);
                            d.put("17", 63);
                            d.put("18", 55);
                            d.put("19", 45);
                            d.put("20", 36);
                            d.put("21", 28);
                            d.put("22", 21);
                            d.put("23", 15);
                            d.put("24", 10);
                            d.put("25", 6);
                            d.put("26", 3);
                            d.put("27", 1);
                            break;
                        }
                        case ZXHZ2: // 二星直选和值
                        {
                            d.put("0", 1);
                            d.put("1", 2);
                            d.put("2", 3);
                            d.put("3", 4);
                            d.put("4", 5);
                            d.put("5", 6);
                            d.put("6", 7);
                            d.put("7", 8);
                            d.put("8", 9);
                            d.put("9", 10);
                            d.put("10", 9);
                            d.put("11", 8);
                            d.put("12", 7);
                            d.put("13", 6);
                            d.put("14", 5);
                            d.put("15", 4);
                            d.put("16", 3);
                            d.put("17", 2);
                            d.put("18", 1);
                            break;
                        }
                        case ZUHZ3: // 三星组选和值
                        {
                            d.put("1", 1);
                            d.put("2", 2);
                            d.put("3", 2);
                            d.put("4", 4);
                            d.put("5", 5);
                            d.put("6", 6);
                            d.put("7", 8);
                            d.put("8", 10);
                            d.put("9", 11);
                            d.put("10", 13);
                            d.put("11", 14);
                            d.put("12", 14);
                            d.put("13", 15);
                            d.put("14", 15);
                            d.put("15", 14);
                            d.put("16", 14);
                            d.put("17", 13);
                            d.put("18", 11);
                            d.put("19", 10);
                            d.put("20", 8);
                            d.put("21", 6);
                            d.put("22", 5);
                            d.put("23", 4);
                            d.put("24", 2);
                            d.put("25", 2);
                            d.put("26", 1);
                            break;
                        }
                        case ZUHZ2: // 二星组选和值
                        {
                            d.put("0", 0);
                            d.put("1", 1);
                            d.put("2", 1);
                            d.put("3", 2);
                            d.put("4", 2);
                            d.put("5", 3);
                            d.put("6", 3);
                            d.put("7", 4);
                            d.put("8", 4);
                            d.put("9", 5);
                            d.put("10", 4);
                            d.put("11", 4);
                            d.put("12", 3);
                            d.put("13", 3);
                            d.put("14", 2);
                            d.put("15", 2);
                            d.put("16", 1);
                            d.put("17", 1);
                            d.put("18", 0);
                            break;
                        }
                    }
                    for (String nos[] : data) {
                        for (String n : nos) {
                            count += d.get(n);
                        }
                    }
                    break;
                }

                // ----------------------------------------- 组六组三组二不定位二码 ----------------------------------------- //
                case ZU6: // 组六
                case SBTHBZ3:    //三不同号标准
                {
                    star = 3;
                    for (String[] nos : data) {
                        int len = nos.length;
                        if (len > 2) {
                            count += len * (len - 1) * (len - 2) / 6;
                        }
                    }
                    break;
                }
                case ZU3: // 组三
                {
                    star = 3;
                    for (String[] nos : data) {
                        int len = nos.length;
                        if (len > 1) {
                            count += len * (len - 1);
                        }
                    }
                    break;
                }
                case BDW2: // 不定位二码
                case ZU2:  // 组二
                case EBTHBZ2: {
                    star = 2;
                    for (String[] nos : data) {
                        int len = nos.length;
                        if (len > 1) {
                            count += len * (len - 1) / 2;
                        }
                    }
                    break;
                }

                // ----------------------------------------- 定位胆 ----------------------------------------- //
                case DWD: // 定位胆
                case LTDWD: // 十一选五定位胆
                case LTDDS: // 十一选五定单双
                case PKDWD: // PK10定位胆
                case KLDXDS:
                case KLWX:
                case KLSJFW:
                case KLDWD: {
                    for (String[] nos : data) {
                        count += nos.length;
                    }
                    break;
                }

                // ----------------------------------------- 不定位三码 ----------------------------------------- //
                case BDW3: // 不定位三码
                {
                    for (String[] nos : data) {
                        int len = nos.length;
                        if (len > 2) {
                            count += Maths.INSTANCE.combination(len, 3);
                        }
                    }
                    break;
                }

                // ----------------------------------------- 组选包胆 ----------------------------------------- //
                case ZUBD3: // 三星组选包胆
                {
                    count = data[0].length * 54;
                    break;
                }
                case ZUBD2: // 二星组选包胆
                {
                    count = data[0].length * 9;
                    break;
                }

                // ----------------------------------------- 任选直选 ----------------------------------------- //
                case RXZX4:
                case RXZX3:
                case RXZX2: {
                    List<Integer> pos = new ArrayList<Integer>();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i].length > 0) {
                            pos.add(i);
                        }
                    }
                    Combination<Integer> p = Combination.of(pos, star);
                    for (List<Integer> c : p) {
                        int len = c.size();
                        temp = 1;
                        for (int i = 0; i < len; i++) {
                            temp *= data[c.get(i)].length;
                        }
                        count += temp;
                    }
                    break;
                }


                // ----------------------------------------- 十一选五 ----------------------------------------- //
                case LTZX3: // 十一选五三星直选
                case PKZX3: // PK10三星直选

                    // ----------------------------------------- 快乐彩 ----------------------------------------- //
                case KLZX3: {
                    String a[] = data[0];
                    String b[] = data[1];
                    String c[] = data[2];
                    if (a.length > 0 && b.length > 0 && c.length > 0) {
                        for (int i = 0; i < a.length; i++) {
                            for (int j = 0; j < b.length; j++) {
                                for (int k = 0; k < c.length; k++) {
                                    if (!a[i].equals(b[j]) && !a[i].equals(c[k]) && !b[j].equals(c[k])) {
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case LTZX2: // 十一选五二星直选
                case PKZX2: // PK10二星直选

                case KLZX2: {
                    String a[] = data[0];
                    String b[] = data[1];
                    if (a.length > 0 && b.length > 0) {
                        int h = Arrs.INSTANCE.intersect(a, b).length;
                        count = a.length * b.length - h;
                    }
                    break;
                }
                case LTZU3:  // 十一选五三星组选
                case LTZU2:  // 十一选五二星组选
                case LTBDW1: // 十一选五不定位一码
                case LTCZW:  // 十一选五猜中位
                case LTRX1:  // 十一选五任选一中一
                case LTRX2:  // 十一选五任选二中二
                case LTRX3:  // 十一选五任选三中三
                case LTRX4:  // 十一选五任选四中四
                case LTRX5:  // 十一选五任选五中五
                case LTRX6:  // 十一选五任选六中五
                case LTRX7:  // 十一选五任选七中五
                case LTRX8:  // 十一选五任选八中五


                    // ----------------------------------------- 快乐八 ----------------------------------------- //
                case KLBRX1:
                case KLBRX2:
                case KLBRX3:
                case KLBRX4:
                case KLBRX5:
                case KLBRX6:
                case KLBRX7:

                case KLZUX3:
                case KLZUX2:
                case KLRX1:
                case KLRX2:
                case KLRX3:
                case KLRX4:
                case KLRX5:
                case KLNH: {
                    int len = data[0].length;
                    int min = ds.get(0).getMinChosen();
                    if (len >= min) {
                        count += Maths.INSTANCE.combination(len, min);
                    }
                    break;
                }
                case LTZUDT3: // 十一选五三星组选胆拖
                case LTZUDT2: // 十一选五二星组选胆拖
                case LTRXDT2: // 十一选五任选胆拖二中二
                case LTRXDT3: // 十一选五任选胆拖三中三
                case LTRXDT4: // 十一选五任选胆拖四中四
                case LTRXDT5: // 十一选五任选胆拖五中五
                case LTRXDT6: // 十一选五任选胆拖六中五
                case LTRXDT7: // 十一选五任选胆拖七中五
                case LTRXDT8: // 十一选五任选胆拖八中五
                {
                    int danlen = data[0].length;
                    int tuolen = data[1].length;
                    int sellen = Integer.parseInt(suffix);
                    if (danlen < 1 || tuolen < 1 || danlen >= sellen) {
                        count = 0;
                    } else {
                        count = Maths.INSTANCE.combination(tuolen, sellen - danlen);
                    }
                    break;
                }

                // ----------------------------------------- PK10 ----------------------------------------- //
                case PKZX5: // PK10三星直选
                {
                    String a[] = data[0];
                    String b[] = data[1];
                    String c[] = data[2];
                    String d[] = data[3];
                    String e[] = data[4];
                    if (a.length > 0 && b.length > 0 && c.length > 0) {
                        for (int i = 0; i < a.length; i++) {
                            for (int j = 0; j < b.length; j++) {
                                for (int k = 0; k < c.length; k++) {
                                    for (int n = 0; n < d.length; n++) {
                                        for (int m = 0; m < e.length; m++) {
                                            if (!a[i].equals(b[j]) && !a[i].equals(c[k]) && !a[i].equals(d[n]) && !a[i].equals(e[m])
                                                    && !b[j].equals(c[k]) && !b[j].equals(d[n]) && !b[j].equals(e[m])
                                                    && !c[k].equals(d[n]) && !c[k].equals(e[m])
                                                    && !d[n].equals(e[m])) {
                                                count++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case PKZX4: // PK10三星直选
                {
                    String a[] = data[0];
                    String b[] = data[1];
                    String c[] = data[2];
                    String d[] = data[3];
                    if (a.length > 0 && b.length > 0 && c.length > 0) {
                        for (int i = 0; i < a.length; i++) {
                            for (int j = 0; j < b.length; j++) {
                                for (int k = 0; k < c.length; k++) {
                                    for (int n = 0; n < d.length; n++) {
                                        if (!a[i].equals(b[j]) && !a[i].equals(c[k]) && !a[i].equals(d[n])
                                                && !b[j].equals(c[k]) && !b[j].equals(d[n])
                                                && !c[k].equals(d[n])) {
                                            count++;
                                        }
                                    }

                                }
                            }
                        }
                    }
                    break;
                }
                case PKZX1: // PK10一星直选
                {
                    count = data[0].length;
                    break;
                }
                default:
                    throw new Exception("玩法不存在"); // 未找到算法
            }
        }

        // ======================================================== 文本 ======================================================== //

        if (selectorType == SelectorType.TEXT) {
            String[] data = content.split(separator, -1);
            List<String> correctNums = new ArrayList<String>(); // 正确号码集合
            Set<String> wrongNums = new HashSet<String>();   // 错误号码集合
            switch (method) {
                case ZX5:   // 五星直选
                case ZX4:   // 四星直选
                case ZX3:   // 三星直选
                case ZX2:   // 二星直选
                case RXZX4: // 任四直选
                case RXZX3: // 任三直选
                case RXZX2: // 任二直选
                {
                    validate(correctNums, wrongNums, data, star, 1, false, null);
                    break;
                }
                case RXZX: {
                    validate(correctNums, wrongNums, data, digits.length, 1, false, null);
                    break;
                }
                case ZU3: // 三星组三
                {
                    star = 3;
                    validate(correctNums, wrongNums, data, star, 1, true, new ZU3Validator());
                    break;
                }
                case ZU6: // 三星组六
                {
                    star = 3;
                    validate(correctNums, wrongNums, data, star, 1, true, new ZU6Validator());
                    break;
                }
                case ZU2:   // 二星组二
                case ZUHH3: // 三星组选混合
                {
                    validate(correctNums, wrongNums, data, star, 1, true, new ZUHHValidator());
                    break;
                }

                // ----------------------------------------- 十一选五 ----------------------------------------- //
                case LTZX3: // 十一选五三星直选
                case LTZX2: // 十一选五二星直选
                case LTRX1: // 十一选五任选一中一
                {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new LTDSValidator());
                    break;
                }
                case LTZU3: // 十一选五三星组选
                case LTZU2: // 十一选五二星组选
                case LTRX2: // 十一选五任选二中二
                case LTRX3: // 十一选五任选三中三
                case LTRX4: // 十一选五任选四中四
                case LTRX5: // 十一选五任选五中五
                case LTRX6: // 十一选五任选六中五
                case LTRX7: // 十一选五任选七中五
                case LTRX8: // 十一选五任选八中五
                {
                    validate(correctNums, wrongNums, data, star * 2, 2, true, new LTDSValidator());
                    break;
                }


                // ----------------------------------------- PK10 ----------------------------------------- //
                case PKZX3:
                case PKZX2:
                case PKZX4:
                case PKZX5: {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new PKDSValidator());
                    break;
                }
                case PK8ZX3:
                case PK8ZX2:
                case PK8ZX4:
                case PK8ZX5: {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new PK8DSValidator());
                    break;
                }
                default:
                    throw new Exception("玩法不存在"); // 未找到算法
            }

            if (!wrongNums.isEmpty()) {
                throw new Exception("投注内容有误，以下号码重复或不符合规则：" + wrongNums);
            } else {
                count = correctNums.size();
            }
        }

        if (isArbitrarily) { // 任选玩法
            if (digits.length < star) {
                throw new Exception("投注内容有误，至少需要选择" + star + "个位置");
            }
            count *= digits.length == 0 ? 0 : Maths.INSTANCE.combination(digits.length, star);
        }

        if (count <= 0) {
            throw new Exception("投注内容有误，注数小于等于0");
        }

        return count;
    }


    /**
     * 计算注数
     *
     * @param content          投注内容			- 0,1,2||1,2,3||
     * @param position         投注位置			- 0,2
     * @param play             玩法名称			- 定位胆
     * @param method           玩法算法			- DWD
     * @param nonrepeatability 不可重复性			- SINGLE
     * @param selectorType     选号方式			- DIGITAL
     * @param selectors        选号器列表			- 万位、千位、百位、十位、个位
     * @param separator        分割符				- |
     * @param minchosen        最少位置个数		- 1
     * @param maxchosen        最多位置个数		- 5
     * @param isArbitrarily    是否任选			- false
     * @return 注数
     * @throws Exception
     */
    public static String StakeSingle(String content, String position, String play, GameMethod method, Nonrepeatability nonrepeatability, SelectorType selectorType, List<Selector> selectors, String separator, int minchosen, int maxchosen, boolean isArbitrarily) throws Exception {
        int count = 0;
        StringBuffer _stake = new StringBuffer();
        // 注数
        int digits[] = Texts.INSTANCE.toIntegerArray(position, ",");           // 选择的位
        String suffix = method.name().substring(method.name().length() - 1); // 算法枚举后缀，即最后一个数字，例如：ZXZH5，suffix = 5
        int star = 0;                             // 几星

        try {
            star = Integer.valueOf(suffix);
        } catch (Exception e) {
        }


        if (StringUtils.isBlank(content)) {
            throw new Exception("投注内容不能为空");                                                                        // 异常：投注内容为空
        }
        // ======================================================== 文本 ======================================================== //

        if (selectorType == SelectorType.TEXT) {
            String[] data = content.split(separator, -1);
            List<String> correctNums = new ArrayList<String>(); // 正确号码集合
            Set<String> wrongNums = new HashSet<String>();   // 错误号码集合
            switch (method) {
                case ZX5:   // 五星直选
                case ZX4:   // 四星直选
                case ZX3:   // 三星直选
                case ZX2:   // 二星直选
                case RXZX4: // 任四直选
                case RXZX3: // 任三直选
                case RXZX2: // 任二直选
                {
                    validate(correctNums, wrongNums, data, star, 1, false, null);
                    break;
                }
                case RXZX: {
                    validate(correctNums, wrongNums, data, digits.length, 1, false, null);
                    break;
                }
                case ZU3: // 三星组三
                {
                    star = 3;
                    validate(correctNums, wrongNums, data, star, 1, true, new ZU3Validator());
                    break;
                }
                case ZU6: // 三星组六
                {
                    star = 3;
                    validate(correctNums, wrongNums, data, star, 1, true, new ZU6Validator());
                    break;
                }
                case ZU2:   // 二星组二
                case ZUHH3: // 三星组选混合
                {
                    validate(correctNums, wrongNums, data, star, 1, true, new ZUHHValidator());
                    break;
                }

                // ----------------------------------------- 十一选五 ----------------------------------------- //
                case LTZX3: // 十一选五三星直选
                case LTZX2: // 十一选五二星直选
                case LTRX1: // 十一选五任选一中一
                {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new LTDSValidator());
                    break;
                }
                case LTZU3: // 十一选五三星组选
                case LTZU2: // 十一选五二星组选
                case LTRX2: // 十一选五任选二中二
                case LTRX3: // 十一选五任选三中三
                case LTRX4: // 十一选五任选四中四
                case LTRX5: // 十一选五任选五中五
                case LTRX6: // 十一选五任选六中五
                case LTRX7: // 十一选五任选七中五
                case LTRX8: // 十一选五任选八中五
                {
                    validate(correctNums, wrongNums, data, star * 2, 2, true, new LTDSValidator());
                    break;
                }


                // ----------------------------------------- PK10 ----------------------------------------- //
                case PKZX3:
                case PKZX2: {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new PKDSValidator());
                    break;
                }

                case PK8ZX3:
                case PK8ZX2: {
                    validate(correctNums, wrongNums, data, star * 2, 2, false, new PK8DSValidator());
                    break;
                }


                default:
                    throw new Exception("玩法不存在"); // 未找到算法
            }
            count = correctNums.size();
            for (String s : correctNums) {
                if (_stake.length() == 0) {
                    _stake.append(s);
                } else {
                    _stake.append(",");
                    _stake.append(s);
                }
            }
        }
        if (count <= 0) {
            throw new Exception("投注内容有误，注数小于等于0");
        }
        return _stake.toString();
    }

    /**
     * 验证号码
     *
     * @param correctNums 正确号码集合
     * @param wrongNums   错误号码集合
     * @param data        要验证的号码数组
     * @param length      每注号码长度
     * @param partition   单个号码长度
     * @param sort        是否排序单注号码
     * @param validator   验证器
     */
    private static void validate(List<String> correctNums, Set<String> wrongNums, String[] data, int length, int partition, boolean sort, Validator validator) {
        StringBuffer sb = new StringBuffer();
        sb.append("^[0-9]{");
        sb.append(length);
        sb.append("}$");
        String temp = sb.toString();
        Set<String> setNums = new HashSet<String>();
        for (String n : data) {
            //nums = sort ? sort(n, partition) : n;
            if (!n.matches(temp)) {
                wrongNums.add(n); // 错误号码
            } else if (null != validator && !validator.validate(n, length)) {
                wrongNums.add(n); // 错误号码
            } else if (setNums.contains(n)) {
                wrongNums.add(n); // 重复号码
            } else {
                setNums.add(n); // 正确号码
            }
        }
        correctNums.addAll(setNums);
        Collections.sort(correctNums);
    }

//	//排序单号
//	private static String sort(String nums){
//		char[] arr = nums.toCharArray();
//		Arrays.sort(arr);
//		return String.valueOf(arr);
//	}

    /**
     * 排序单注号码
     *
     * @param nums      单注号码
     * @param partition 单个号码长度
     * @return
     */
    private static String sort(String nums, int partition) {
        List<String> parts = Texts.INSTANCE.partition(nums, partition);
        Object[] nos = parts.toArray();
        Arrays.sort(nos);
        return Arrs.INSTANCE.join(nos, "");
    }


    /**
     * 计算注数
     *
     * @param content          投注内容			- 0,1,2||1,2,3||
     * @param position         投注位置			- 0,2
     * @param play             玩法名称			- 定位胆
     * @param method           玩法算法			- DWD
     * @param nonrepeatability 不可重复性			- SINGLE
     * @param selectorType     选号方式			- DIGITAL
     * @param selectors        选号器列表			- 万位、千位、百位、十位、个位
     * @param separator        分割符				- |
     * @param minchosen        最少位置个数		- 1
     * @param maxchosen        最多位置个数		- 5
     * @param isArbitrarily    是否任选			- false
     * @return 注数
     * @throws Exception
     */
    @SuppressWarnings("incomplete-switch")
    public static int kjcount(String content, String position, String play, GameMethod method, Nonrepeatability nonrepeatability, SelectorType selectorType, List<Selector> selectors, String separator, int minchosen, int maxchosen, boolean isArbitrarily) throws Exception {

        int count = 0;                                                   // 注数

        if (StringUtils.isBlank(content)) {
            throw new Exception("投注内容不能为空");                                                                        // 异常：投注内容为空
        }


        // ======================================================== 位式 ======================================================== //

        if (selectorType == SelectorType.DIGITAL) {

            List<DigitalSelector> ds = new ArrayList<DigitalSelector>();                    // 位选择器列表
            String[][] data = new String[selectors.size()][];                      // 号码二维数组，一维：位；二维：号码
            int temp = 1;                                                   // 临时注数变量

//			String[] nums = content.split(separator);            // 分割位，包含空元素
//			int ilen = StringUtil.split(content, separator).length; // 有包含号码的位个数，即不包含空元素的数组个数，或称有效位数
//
//			System.out.print("位数等于：" + nums.length);
//			// 校验位数，例如【后三直选】只有【百十个】3位，如果数组元素个数不等于3个则是非法注单
//			if (nums.length != selectors.size()) {
//				throw new Exception("投注内容有误, 位数不正确");	 														// 异常：按位分割后的数组元素个数不等于位选择器个数
//			}
//			// 校验有效位数
//			if (ilen < minchosen) {
//				throw new Exception("投注内容有误, " + playBetBallClickSound + " 至少需要选择" + minchosen + "个位置"); 		// 异常：小于最少位置
//			}
//			if (ilen > maxchosen) {
//				throw new Exception("投注内容有误, " + playBetBallClickSound + " 最多只能选择" + maxchosen + "个位置"); 		// 异常：大于最多位置
//			}

            for (int i = 0; i < selectors.size(); i++) {
                DigitalSelector selector = (DigitalSelector) selectors.get(i);
                if (!"".equals(position)) {
                    selector.setSeparator(position);
                }
                String[] nos = StringUtils.split(content, selector.getSeparator()); // 分割号码，不包含空元素，例如【定位胆】
                // 验证有效号码个数
                if (nos.length < selector.getMinChosen()) {
                    throw new Exception(selector.getTitle() + " 至少需要选择" + selector.getMinChosen() + "个号码");        // 异常：少于最少号码个数
                }
                if (nos.length > selector.getMaxChosen()) {
                    throw new Exception(selector.getTitle() + " 最多只能选择" + selector.getMaxChosen() + "个号码");        // 异常：大于最多号码个数
                }
                // 验证号码有效性，如果该位无号码则不验证，例如【定位胆】不用每位都选号码
                if (!"".equals(content)) {
                    String[] verif = content.split(selector.getSeparator(), -1); // 待验证的号码数组
                    String splitv = "\\|";
                    if (position.equals(separator)) {
                        splitv = separator;
                    }
                    String[] allow = selector.getNo().split(splitv, -1);          // 允许的号码数组
                    for (String no : verif) {
                        if (!ArrayUtils.contains(allow, no)) {
                            throw new Exception("投注内容有误, 所投号码[" + no + "]不在允许号码范围内");                                    // 异常：所投号码不在允许号码范围内
                        }
                    }
                }
                ds.add(selector);
                data[i] = nos;


                switch (method) {
                    case SZ1:
                        count = 1;
                        break;
                    case LM1:
                        count = (int) calculateZh(nos.length, selector.getMinChosen());
                        break;
                }
            }

            // 验证号码重复性
            switch (nonrepeatability) {
                case SINGLE: // 单位号码不能重复
                    for (String[] nos : data) {
                        if (Arrs.INSTANCE.duplicate(nos)) {
                            throw new Exception("投注内容有误, 单个位置的号码不能重复");
                        }
                    }
                    break;
                case ALL: // 完全不能重复
                    String[] all = null;
                    for (String[] nos : data) {
                        all = (String[]) ArrayUtils.addAll(all, nos);
                    }
                    if (Arrs.INSTANCE.duplicate(all)) {
                        throw new Exception("投注内容有误, 号码不能重复");
                    }
                    break;
            }
        }
        if (count <= 0) {
            throw new Exception("投注内容有误，注数小于等于0");
        }

        return count;
    }

    /**
     * 排列算法
     *
     * @param a 投注号码的长度
     * @param b 一注的号码数量
     * @return
     */
    public static long calculatePl(int a, int b) {
        long result = 1;
        if (a < b) return 0;
        for (int i = 0; i < b; i++) {
            result = result * (a - i);
        }
        return result;
    }

    public static long calculateZh(int a, int b) {
        if (a < b) return 0;
        return calculatePl(a, b) / calculatePl(b, b);
    }

    public static void main(String[] arg) {
//		String[] nums = "101".split("\\|");            // 分割位，包含空元素
//		System.out.println("nums：" + nums.length);
        //构建解析器
        List<Selector>
                selectors = null;


//				GameLogic.createSelectors(1, "数字第一球", "0|1|2|3|4|5|6|7|8|9", 1, 1);
        GameLogic game = new GameLogic();
        int _Count = 0;
        try {

            //构建解析器
//			selectors = GameLogic.createSelectors(1, "二肖连", "鼠|10,22,34,46.牛|09,21,33,45.虎|08,20,32,44", 2, 6);
            //计算注數
//			_Count = GameLogic.kjcount("鼠|10,22,34,46.牛|09,21,33,45.虎|08,20,32,44", "\\.", "连肖", GameMethod.LM1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\.", 1, 1, false);


            //构建解析器
            selectors = GameLogic.createSelectors(1, "二肖", "鼠|10,22,34,46.牛|09,21,33,45.虎|08,20,32,44.兔|07,19,31,43", 2, 12);
            //计算注數
            _Count = GameLogic.kjcount("鼠|10,22,34,46.虎|08,20,32,44.兔|07,19,31,43", "\\.", "合肖", GameMethod.LM1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\.", 1, 1, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/**
 * 验证器接口
 *
 * @author Lane
 */
interface Validator {
    boolean validate(String nums, int length);
}

/**
 * 组三验证器
 *
 * @author Lane
 */
class ZU3Validator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        if (length != 3) {
            return false;
        }
        String first = nums.substring(0, 1);
        String second = nums.substring(1, 2);
        String third = nums.substring(2, 3);
        if (first.equals(second) && second.equals(third)) {
            return false;
        }
        if (first.equals(second) || first.equals(third) || second.equals(third)) {
            return true;
        }
        return false;
    }
}

/**
 * 组六验证器
 *
 * @author Lane
 */
class ZU6Validator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        if (length != 3) {
            return false;
        }
        String first = nums.substring(0, 1);
        String second = nums.substring(1, 2);
        String third = nums.substring(2, 3);
        if (first.equals(second) || first.equals(third) || second.equals(third)) {
            return false;
        } else {
            return true;
        }
    }
}

/**
 * 组选混合验证器
 *
 * @author Lane
 */
class ZUHHValidator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        String array[];
        if (length == 2) {
            array = new String[]{"00", "11", "22", "33", "44", "55", "66", "77", "88", "99"};
        } else {
            array = new String[]{"000", "111", "222", "333", "444", "555", "666", "777", "888", "999"};
        }
        if (ArrayUtils.contains(array, nums)) {
            return false;
        } else {
            return true;
        }
    }
}

/**
 * 十一选五单式验证器
 *
 * @author Lane
 */
class LTDSValidator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        List<String> nos = Texts.INSTANCE.partition(nums, 2);
        int len = nos.size();
        for (int i = 0; i < len; i++) {
            if (Integer.parseInt(nos.get(i)) > 11 || Integer.parseInt(nos.get(i)) < 1) {
                nos.clear();
                return false;
            }
            for (int j = i + 1; j < len; j++) {
                if (nos.get(i).equals(nos.get(j))) {
                    nos.clear();
                    return false;
                }
            }
        }
        nos.clear();
        return true;
    }
}

/**
 * PK10单式验证器
 *
 * @author Lane
 */
class PKDSValidator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        List<String> nos = Texts.INSTANCE.partition(nums, 2);
        int len = nos.size();
        for (int i = 0; i < len; i++) {
            if (Integer.parseInt(nos.get(i)) > 10 || Integer.parseInt(nos.get(i)) < 1) {
                nos.clear();
                return false;
            }
            for (int j = i + 1; j < len; j++) {
                if (nos.get(i).equals(nos.get(j))) {
                    nos.clear();
                    return false;
                }
            }
        }
        nos.clear();
        return true;
    }

}


/**
 * PK8单式验证器
 *
 * @author Lane
 */
class PK8DSValidator implements Validator {
    @Override
    public boolean validate(String nums, int length) {
        List<String> nos = Texts.INSTANCE.partition(nums, 2);
        int len = nos.size();
        for (int i = 0; i < len; i++) {
            if (Integer.parseInt(nos.get(i)) > 8 || Integer.parseInt(nos.get(i)) < 1) {
                nos.clear();
                return false;
            }
            for (int j = i + 1; j < len; j++) {
                if (nos.get(i).equals(nos.get(j))) {
                    nos.clear();
                    return false;
                }
            }
        }
        nos.clear();
        return true;
    }
}
