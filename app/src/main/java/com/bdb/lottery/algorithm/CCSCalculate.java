package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * 时时彩玩法计算器
 * Created by XB on 2015/8/11.
 */
public class CCSCalculate {

    public static int stake(String _Stake, int _PlayID, String _DigBit) throws Exception {
        //将投注号码放入二维数组
        int _Count = 0;
        //位数选择器
        List<Selector> selectors  = null;
        switch (_PlayID) {
            case 9:
            case 289:
            case 235://三星  前三直选
            case 50:
            case 11:
            case 291://三星  中三直选
            case 237:
            case 52:
            case 7:
            case 287:// 三星  后三直选
            case 233:
            case 48:
            case 1649://西甲分分彩
            case 1749://巴西五分彩
            case 1951://二分彩
            case 1954:
            case 1963:
            case 1695:
            case 1751:
            case 1768:
            case 1672:
            case 2398://秒秒彩  直选复式
            case 2404:
            case 2410:
            case 2789:
            case 2793:
            case 2791:
            case 2889:
            case 2893:
            case 2891:
            case 2989:
            case 2993:
            case 2991:
            case 3089:
            case 3093:
            case 3091:
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星直选复式", GameMethod.ZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            case 203:
            case 204:
            case 205:
            case 324:
            case 322:
            case 323://三星 直选跨度
            case 270:
            case 269:
            case 268:
            case 216:
            case 215:
            case 217:
            case 1673://五分，分分
            case 1738:
            case 1817:
            case 1650:
            case 1696:
            case 1795:
            case 1948:
            case 1969:
            case 1975:
            case 2425://秒秒彩 直选跨度
            case 2428:
            case 2431:
            case 2795:
            case 2797:
            case 2796:
            case 2895:
            case 2897:
            case 2896:
            case 2995:
            case 2997:
            case 2996:
            case 3095:
            case 3097:
            case 3096:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星直选跨度", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星直选跨度", GameMethod.ZXKD3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 423:
            case 424:
            case 425:
            case 471:
            case 472:
            case 473://三星 直选和值
            case 455:
            case 457:
            case 456:
            case 439:
            case 441:
            case 440:
            case 1761://分分，五分
            case 1725:
            case 1626:
            case 1802:
            case 1719:
            case 1779:
            case 1957:
            case 1966:
            case 1972:
            case 2416://秒秒彩 直选和值
            case 2419:
            case 2422:
            case 2798:
            case 2800:
            case 2799:
            case 2898:
            case 2900:
            case 2899:
            case 2998:
            case 3000:
            case 2999:
            case 3098:
            case 3100:
            case 3099:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星直选和值", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27", 1, 28);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星直选和值", GameMethod.ZXHZ3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 10:
            case 12:
            case 8:
            case 290:
            case 288:
            case 292://三星 直选单式
            case 234:
            case 238:
            case 236:
            case 53:
            case 51:
            case 49:
            case 1831://分分，五分
            case 1707:
            case 1764:
            case 1704:
            case 1734:
            case 1643:
            case 1945:
            case 1960:
            case 1978:
            case 2401://秒秒彩 直选单式
            case 2407:
            case 2413:
            case 2792:
            case 2794:
            case 2790:
            case 2892:
            case 2894:
            case 2890:
            case 2992:
            case 2994:
            case 2990:
            case 3092:
            case 3094:
            case 3090:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "三星直选单式", GameMethod.ZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 17:
            case 14:
            case 20:
            case 294:
            case 297:
            case 300://三星 组三复式
            case 246:
            case 243:
            case 240:
            case 61:
            case 55:
            case 58:
            case 1766://分分，五分
            case 1663:
            case 1819:
            case 1689:
            case 1775:
            case 1625:
            case 2062:
            case 2068:
            case 2086:
            case 2446:  //秒秒彩 组三复式
            case 2455:
            case 2437:
            case 2802:
            case 2808:
            case 2805:
            case 2902:
            case 2908:
            case 2905:
            case 3002:
            case 3008:
            case 3005:
            case 3102:
            case 3108:
            case 3105:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星组三复式", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星组三复式", GameMethod.ZU3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 418:
            case 420:
            case 422:
            case 468:
            case 466:
            case 470://三星 组三单式
            case 454:
            case 450:
            case 452:
            case 434:
            case 438:
            case 436:
            case 1763://分分，五分
            case 1692:
            case 1796:
            case 1644:
            case 1688:
            case 1731:
            case 2050:
            case 2059:
            case 2080:
            case 2464://秒秒彩 组三单式
            case 2470:
            case 2476:
            case 2811:
            case 2815:
            case 2813:
            case 2911:
            case 2915:
            case 2913:
            case 3011:
            case 3015:
            case 3013:
            case 3111:
            case 3115:
            case 3113:
                //计算注數
                _Count = GameLogic.count(_Stake, "", "三星组选单式", GameMethod.ZU3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 19:
            case 16:
            case 13:
            case 299:
            case 296:
            case 293://三星 组六复式
            case 245:
            case 239:
            case 242:
            case 60:
            case 57:
            case 54:
            case 1717://分分，五分
            case 1782:
            case 1825:
            case 1720:
            case 1718:
            case 1730:
            case 2047:
            case 2089:
            case 2071:
            case 2443://秒秒彩 组六复式
            case 2434:
            case 2452:
            case 2801:
            case 2807:
            case 2804:
            case 2901:
            case 2907:
            case 2904:
            case 3001:
            case 3007:
            case 3004:
            case 3101:
            case 3107:
            case 3104:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "组选组六", "0|1|2|3|4|5|6|7|8|9", 3, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "组选组六", GameMethod.ZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 417:
            case 419:
            case 421:
            case 469:
            case 467:
            case 465:
            case 453://三星 组六单式
            case 451:
            case 449:
            case 437:
            case 435:
            case 433:
            case 1800://分分，五分
            case 1701:
            case 1733:
            case 1623:
            case 1658:
            case 1787:
            case 2053:
            case 2074:
            case 2083:
            case 2461://秒秒彩 组六单式
            case 2467:
            case 2473:
            case 2810:
            case 2814:
            case 2812:
            case 2910:
            case 2914:
            case 2912:
            case 3010:
            case 3014:
            case 3012:
            case 3110:
            case 3114:
            case 3112:
                _Count = GameLogic.count(_Stake, "", "三星组六单式", GameMethod.ZU6, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 15:
            case 18:
            case 21:
            case 298:
            case 295:
            case 301: //三星 组选混合
            case 247:
            case 241:
            case 244:
            case 59:
            case 56:
            case 62:
            case 1788://分分，五分
            case 1698:
            case 1774:
            case 1634:
            case 1705:
            case 1748:
            case 2056:
            case 2065:
            case 2077:
            case 2440://秒秒彩 组选混合
            case 2449:
            case 2458:
            case 2803:
            case 2809:
            case 2806:
            case 2903:
            case 2909:
            case 2906:
            case 3003:
            case 3009:
            case 3006:
            case 3103:
            case 3109:
            case 3106:
                _Count = GameLogic.count(_Stake, "", "组选混合", GameMethod.ZUHH3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 22:
            case 304:// 二星 前二直选复式
            case 250:
            case 65:
            case 24:
            case 302://  二星 后二直选复式
            case 248:
            case 63:
            case 1758://分分，五分
            case 1648:
            case 1713:
            case 1832:
            case 1990:
            case 2011:
            case 2479:// 秒秒彩 二星 前二直选复式
            case 2485:
            case 2816:
            case 2818:
            case 2916:
            case 2918:
            case 3016:
            case 3018:
            case 3116:
            case 3118:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星直选复式", GameMethod.ZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 3189:
            case 3190:
            case 3191:
            case 3192:
            case 3193:
            case 3194:
            case 3195:
            case 3196:
            case 3197:
            case 3198:
            case 3290:
            case 3291:
            case 3292:
            case 3293:
            case 3294:
            case 3295:
            case 3296:
            case 3297:
            case 3298:
            case 3299:
            case 3277:
            case 3278:
            case 3279:
            case 3280:
            case 3281:
            case 3282:
            case 3283:
            case 3284:
            case 3285:
            case 3286:
            case 3251:
            case 3252:
            case 3253:
            case 3254:
            case 3255:
            case 3256:
            case 3257:
            case 3258:
            case 3259:
            case 3260:
            case 3264:
            case 3265:
            case 3266:
            case 3267:
            case 3268:
            case 3269:
            case 3270:
            case 3271:
            case 3272:
            case 3273:
            case 3238:
            case 3239:
            case 3240:
            case 3241:
            case 3242:
            case 3243:
            case 3244:
            case 3245:
            case 3246:
            case 3247:
            case 3225:
            case 3226:
            case 3227:
            case 3228:
            case 3229:
            case 3230:
            case 3231:
            case 3232:
            case 3233:
            case 3234:
            case 3329:
            case 3330:
            case 3331:
            case 3332:
            case 3333:
            case 3334:
            case 3335:
            case 3336:
            case 3337:
            case 3338:
            case 3316:
            case 3317:
            case 3318:
            case 3319:
            case 3320:
            case 3321:
            case 3322:
            case 3323:
            case 3324:
            case 3325:
            case 3303:
            case 3304:
            case 3305:
            case 3306:
            case 3307:
            case 3308:
            case 3309:
            case 3310:
            case 3311:
            case 3312:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "龙虎", "龙|虎|和", 1, 3);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "龙虎", GameMethod.LH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3199:
            case 3300:
            case 3287:
            case 3261:
            case 3274:
            case 3248:
            case 3235:
            case 3339:
            case 3326:
            case 3313:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "总和", "大|小|单|双", 1, 4);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "总和", GameMethod.ZH, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 3201:
            case 3200:
            case 3301:
            case 3302:
            case 3288:
            case 3289:
            case 3262:
            case 3263:
            case 3275:
            case 3276:
            case 3249:
            case 3250:
            case 3236:
            case 3237:
            case 3340:
            case 3341:
            case 3327:
            case 3328:
            case 3314:
            case 3315:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "大小单双", "大|小|单|双", 1, 16);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "大小单双", GameMethod.DXDS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;

            case 23:
            case 25:
            case 305:
            case 303://二星 直选单式
            case 251:
            case 249:
            case 64:
            case 66:
            case 1678://分分，五分
            case 1809:
            case 1783:
            case 1646:
            case 1999:
            case 2005:
            case 2482://秒秒彩 二星 直选单式
            case 2488:
            case 2817:
            case 2819:
            case 2917:
            case 2919:
            case 3017:
            case 3019:
            case 3117:
            case 3119:
                _Count = GameLogic.count(_Stake, "", "二星直选单式", GameMethod.ZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 431:
            case 432:
            case 480:
            case 479://二星 直选和值
            case 463:
            case 464:
            case 448:
            case 447:
            case 1633://分分，五分
            case 1769:
            case 1724:
            case 1760:
            case 1993:
            case 1996:
            case 2491: //秒秒彩 二星 直选和值
            case 2494:
            case 2822:
            case 2823:
            case 2922:
            case 2923:
            case 3022:
            case 3023:
            case 3122:
            case 3123:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星直选和值", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18", 1, 19);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二星直选和值", GameMethod.ZXHZ2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 206:
            case 207:
            case 326:
            case 325://二星 直选跨度
            case 271:
            case 272:
            case 218:
            case 219:
            case 1810://分分，五分
            case 1631:
            case 1677:
            case 1804:
            case 2002:
            case 2008:
            case 2497: //秒秒彩 二星 直选跨度
            case 2500:
            case 2820:
            case 2821:
            case 2920:
            case 2921:
            case 3020:
            case 3021:
            case 3120:
            case 3121:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星直选跨度", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二星直选跨度", GameMethod.ZXKD2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 26:
            case 28:
            case 306:
            case 308:
            case 252:// 二星 组选组二
            case 254:
            case 67:
            case 69:
            case 1675://分分，五分
            case 1813:
            case 1752:
            case 1706:
            case 1876:
            case 1879:
            case 2503://秒秒彩 二星 组选复式
            case 2509:
            case 2826:
            case 2828:
            case 2926:
            case 2928:
            case 3026:
            case 3028:
            case 3126:
            case 3128:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选组二", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星组选组二", GameMethod.ZU2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 29:
            case 27:
            case 309:
            case 307://二星 组选混合
            case 253:
            case 255:
            case 70:
            case 68:
            case 1685://分分，五分
            case 1746:
            case 1714:
            case 1822:
            case 1873:
            case 1888:
            case 2506: //秒秒彩 二星 组选混合
            case 2512:
            case 2827:
            case 2829:
            case 2927:
            case 2929:
            case 3027:
            case 3029:
            case 3127:
            case 3129:
                _Count = GameLogic.count(_Stake, "", "二星组选混合", GameMethod.ZU2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 208:
            case 209:
            case 327:
            case 328://二星 组选包胆
            case 274:
            case 273:
            case 221:
            case 220:
            case 1820://分分，五分
            case 1632:
            case 1712:
            case 1814:
            case 1882:
            case 1885:
            case 2515://秒秒彩 二星 组选包胆
            case 2518:
            case 2824:
            case 2825:
            case 2924:
            case 2925:
            case 3024:
            case 3025:
            case 3124:
            case 3125:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选包胆", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二星组选包胆", GameMethod.ZUBD2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 37:
            case 38:
            case 39:
            case 210:
            case 318:
            case 317:
            case 329://不定位胆 三星，四星 一码
            case 319:
            case 263:
            case 275:
            case 265:
            case 264:
            case 222:
            case 80:
            case 78:
            case 79:
            case 1636://分分，五分
            case 1826:
            case 1630:
            case 1816:
            case 1686:
            case 1759:
            case 1680:
            case 1806:
            case 1870:
            case 1861:
            case 1849:
            case 1858:
            case 2542://秒秒彩 不定位胆 三星，四星 一码
            case 2545:
            case 2548:
            case 2569:
            case 2838:
            case 2839:
            case 2837:
            case 2841:
            case 2938:
            case 2939:
            case 2937:
            case 2941:
            case 3038:
            case 3039:
            case 3037:
            case 3041:
            case 3138:
            case 3139:
            case 3137:
            case 3141:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "一码不定位胆", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "一码不定位胆", GameMethod.BDW1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 426:
            case 428:
            case 427:
            case 429:
            case 40:
            case 476:
            case 475:
            case 320:
            case 474:
            case 477:
            case 458://不定位胆 三星 四星 五星 二码
            case 266:
            case 459:
            case 460:
            case 461:
            case 442:
            case 445:
            case 443:
            case 81:
            case 444:
            case 1641://分分，五分
            case 1753:
            case 1777:
            case 1657:
            case 1662:
            case 1823:
            case 1824:
            case 1693:
            case 1797:
            case 1726:
            case 1846:
            case 1852:
            case 1855:
            case 1864:
            case 1867:
            case 2551://秒秒彩 不定位胆 三星 四星 五星 二码
            case 2554:
            case 2557:
            case 2560:
            case 2563:
            case 2843:
            case 2844:
            case 2842:
            case 2845:
            case 2840:
            case 2943:
            case 2944:
            case 2942:
            case 2945:
            case 2940:
            case 3043:
            case 3044:
            case 3042:
            case 3045:
            case 3040:
            case 3143:
            case 3144:
            case 3142:
            case 3145:
            case 3140:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二码不定位胆", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二码不定位胆", GameMethod.BDW2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 430:
            case 478://不定位胆 五星三码
            case 462:
            case 446:
            case 1737://分分，五分
            case 1702:
            case 1843:
            case 2566://秒秒彩 不定位胆 五星三码
            case 2846:
            case 2946:
            case 3046:
            case 3146:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三码不定位胆", "0|1|2|3|4|5|6|7|8|9", 3, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三码不定位胆", GameMethod.BDW3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 41:
            case 321://五星定位胆
            case 267:
            case 82:
            case 1656://分分，五分
            case 1773:
            case 2035:
            case 2572://秒秒彩 五星定位胆
            case 2847:
            case 2947:
            case 3047:
            case 3147:
                //构建解析器
                selectors = GameLogic.createSelectors(5, "定位胆", "0|1|2|3|4|5|6|7|8|9", 0, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4", "定位胆", GameMethod.DWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 5, false);
                break;
            case 276://一帆风顺
            case 330:
            case 223:
            case 211:
            case 1637:
            case 1791:
            case 2221:
            case 2575:
            case 2848:
            case 2948:
            case 3048:
            case 3148:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "一帆风顺", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "一帆风顺", GameMethod.YFFS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 224://双喜临门
            case 331:
            case 212:
            case 277:
            case 1660:
            case 1808:
            case 1894:
            case 2578:
            case 2849:
            case 2949:
            case 3049:
            case 3149:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "双喜临门", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "双喜临门", GameMethod.HSCS, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 225://三星报喜
            case 332:
            case 278:
            case 213:
            case 1635:
            case 1801:
            case 1891:
            case 2581:
            case 2850:
            case 2950:
            case 3050:
            case 3150:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星报喜", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星报喜", GameMethod.SXBX, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 279://四季发财
            case 333:
            case 226:
            case 214:
            case 1683:
            case 1811:
            case 1897:
            case 2584:
            case 2851:
            case 2951:
            case 3051:
            case 3151:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "四季发财", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "四季发财", GameMethod.SJFC, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 697:
            case 713://五星直选 直选复式
            case 729:
            case 745:
            case 1798://分分,五分
            case 1645:
            case 1906:
            case 2650://秒秒彩 五星直选 直选复式
            case 2873:
            case 2973:
            case 3073:
            case 3173:
                //构建解析器
                selectors = GameLogic.createSelectors(5, "五星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4", "五星直选复式", GameMethod.ZX5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 5, 5, false);
                break;
            case 698:
            case 714://五星直选 直选单式
            case 730:
            case 746:
            case 1690://分分，五分
            case 1784:
            case 1903:
            case 2653://秒秒彩 五星直选 直选单式
            case 2874:
            case 2974:
            case 3074:
            case 3174:
                _Count = GameLogic.count(_Stake, "", "五星直选单式", GameMethod.ZX5, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 699:
            case 715://五星直选 五星组合
            case 731:
            case 747:
            case 1670://分分，五分
            case 1776:
            case 1900:
            case 2656://秒秒彩 五星直选 五星组合
            case 2875:
            case 2975:
            case 3075:
            case 3175:
                //构建解析器
                selectors = GameLogic.createSelectors(5, "五星组合", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4", "五星组合", GameMethod.ZXZH5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 5, 5, false);
                break;
            case 700:
            case 716://五星组选 组选120
            case 732:
            case 748:
            case 1744://分分，五分
            case 1661:
            case 2113:
            case 2659://秒秒彩 五星组选 组选120
            case 2876:
            case 2976:
            case 3076:
            case 3176:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "五星组选120", "0|1|2|3|4|5|6|7|8|9", 5, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "五星组选120", GameMethod.WXZU120, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 701:
            case 717://五星组选 组选60
            case 733:
            case 749:
            case 1778://分分，五分
            case 1668:
            case 2119:
            case 2662://秒秒彩 五星组选 组选60
            case 2877:
            case 2977:
            case 3077:
            case 3177:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "五星组选60", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                ((DigitalSelector)selectors.get(1)).setMinChosen(3);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "五星组选60", GameMethod.WXZU60, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 702:
            case 718://五星组选 组选30
            case 734:
            case 750:
            case 1799://分分，五分
            case 1664:
            case 2128:
            case 2665://秒秒彩 五星组选 组选30
            case 2878:
            case 2978:
            case 3078:
            case 3178:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "五星组选30", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                ((DigitalSelector)selectors.get(0)).setMinChosen(2);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "五星组选30", GameMethod.WXZU30, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 703:
            case 719://五星组选 组选20
            case 735:
            case 751:
            case 1728://分分，五分
            case 1765:
            case 2116:
            case 2668://秒秒彩 五星组选 组选20
            case 2879:
            case 2979:
            case 3079:
            case 3179:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "五星组选20", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                ((DigitalSelector)selectors.get(1)).setMinChosen(2);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "五星组选20", GameMethod.WXZU20, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 704:
            case 720://五星组选 组选10
            case 736:
            case 752:
            case 1789://分分，五分
            case 1681:
            case 2122:
            case 2671://秒秒彩 五星组选 组选10
            case 2880:
            case 2980:
            case 3080:
            case 3180:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "五星组选10", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "五星组选10", GameMethod.WXZU10, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 705:
            case 721://五星组选 组选5
            case 737:
            case 753:
            case 1715://分分，五分
            case 1794:
            case 2125:
            case 2674://秒秒彩 五星组选 组选5
            case 2881:
            case 2981:
            case 3081:
            case 3181:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "五星组选5", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "五星组选5", GameMethod.WXZU5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 706:
            case 722://四星直选 直选复式
            case 738:
            case 754:
            case 1834://分分，五分
            case 1666:
            case 2044:
            case 2677://秒秒彩 四星直选 直选复式
            case 2882:
            case 2982:
            case 3082:
            case 3182:
                //构建解析器
                selectors = GameLogic.createSelectors(4, "四星直选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3", "四星直选复式", GameMethod.ZX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 4, 4, false);
                break;
            case 707:
            case 723://四星直选 直选单式
            case 739:
            case 755:
            case 1812://分分，五分
            case 1687:
            case 2038:
            case 2680://秒秒彩 四星直选 直选单式
            case 2883:
            case 2983:
            case 3083:
            case 3183:
                _Count = GameLogic.count(_Stake, "", "四星直选单式", GameMethod.ZX4, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 708:
            case 724://四星直选 四星组合
            case 740:
            case 756:
            case 1723://分分，五分
            case 1830:
            case 2041:
            case 2683://秒秒彩 四星直选 四星组合
            case 2884:
            case 2984:
            case 3084:
            case 3184:
                //构建解析器
                selectors = GameLogic.createSelectors(4, "四星直选组合", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3", "四星直选组合", GameMethod.ZXZH4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 4, 4, false);
                break;
            case 709:
            case 725://四星组选 组选24
            case 741:
            case 757:
            case 1772://分分，五分
            case 1665:
            case 2140:
            case 2686://秒秒彩 四星组选 组选24
            case 2885:
            case 2985:
            case 3085:
            case 3185:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "四星组选24", "0|1|2|3|4|5|6|7|8|9", 4, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "四星组选24", GameMethod.SXZU24, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 710:
            case 726://四星组选 组选12
            case 742:
            case 758:
            case 1745://分分，五分
            case 1682:
            case 2137:
            case 2689://秒秒彩 四星组选 组选12
            case 2886:
            case 2986:
            case 3086:
            case 3186:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "四星组选12", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                ((DigitalSelector)selectors.get(1)).setMinChosen(2);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "四星组选12", GameMethod.SXZU12, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 711:
            case 727://四星组选 组选6
            case 743:
            case 759:
            case 1805://分分，五分
            case 1697:
            case 2143:
            case 2692://秒秒彩 四星组选 组选6
            case 2887:
            case 2987:
            case 3087:
            case 3187:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "四星组选6", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "四星组选6", GameMethod.SXZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 712:
            case 728://四星组选 组选4
            case 744:
            case 760:
            case 1638://分分，五分
            case 1739:
            case 2134:
            case 2695://秒秒彩 四星组选 组选4
            case 2888:
            case 2988:
            case 3088:
            case 3188:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "四星组选4", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "四星组选4", GameMethod.SXZU4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 613:
            case 634://任选二 直选复式
            case 655:
            case 676:
            case 1655://分分，五分
            case 1754:
            case 1927:
            case 2587://秒秒彩 任选二 直选复式
            case 2852:
            case 2952:
            case 3052:
            case 3152:
                //构建解析器
                selectors = GameLogic.createSelectors(_DigBit.split(",", -1).length, "任选二直选复式", "0|1|2|3|4|5|6|7|8|9|万位:0|万位:1|万位:2|万位:3|万位:4|万位:5|万位:6|万位:7|万位:8|万位:9|千位:0|千位:1|千位:2|千位:3|千位:4|千位:5|千位:6|千位:7|千位:8|千位:9|百位:0|百位:1|百位:2|百位:3|百位:4|百位:5|百位:6|百位:7|百位:8|百位:9|十位:0|十位:1|十位:2|十位:3|十位:4|十位:5|十位:6|十位:7|十位:8|十位:9|个位:0|个位:1|个位:2|个位:3|个位:4|个位:5|个位:6|个位:7|个位:8|个位:9", 0, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选二直选复式", GameMethod.RXZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 5, false);
                break;
            case 614:
            case 635://任选二 直选单式
            case 656:
            case 677:
            case 1667://分分，五分
            case 1786:
            case 1939:
            case 2590://秒秒彩
            case 2853:
            case 2953:
            case 3053:
            case 3153:
                _Count = GameLogic.count(_Stake, _DigBit, "任选二直选单式", GameMethod.RXZX2, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 615:
            case 636://任选二 直选和值
            case 657:
            case 678:
            case 1770://分分，五分
            case 1654:
            case 1933:
            case 2593://秒秒彩
            case 2854:
            case 2954:
            case 3054:
            case 3154:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选二直选和值", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18", 1, 19);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选二直选和值", GameMethod.ZXHZ2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 616:
            case 637://任选二 组选复式
            case 658:
            case 679:
            case 1781://分分，五分
            case 1709:
            case 1936:
            case 2596:	//秒秒彩
            case 2855:
            case 2955:
            case 3055:
            case 3155:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选二组选复式", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选二组选复式", GameMethod.ZU2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 617:
            case 638://任选二 组选单式
            case 659:
            case 680:
            case 1676://分分，五分
            case 1767:
            case 1930:
            case 2599://秒秒彩
            case 2856:
            case 2956:
            case 3056:
            case 3156:
                _Count = GameLogic.count(_Stake, _DigBit, "任选二组选单式", GameMethod.ZU2, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 618:
            case 639://任选二 组选和值
            case 660:
            case 681:
            case 1821://分分，五分
            case 1708:
            case 1942:
            case 2602://秒秒彩
            case 2857:
            case 2957:
            case 3057:
            case 3157:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选二组选和值", "1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17", 1, 17);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选二组选和值", GameMethod.ZUHZ2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 619:
            case 640://任选三 直选复式
            case 661:
            case 682:
            case 1674://分分，五分
            case 1742:
            case 2185:
            case 2605://秒秒彩
            case 2858:
            case 2958:
            case 3058:
            case 3158:
                //构建解析器
                selectors = GameLogic.createSelectors(_DigBit.split(",").length, "任选三直选复式", "0|1|2|3|4|5|6|7|8|9|万位:0|万位:1|万位:2|万位:3|万位:4|万位:5|万位:6|万位:7|万位:8|万位:9|千位:0|千位:1|千位:2|千位:3|千位:4|千位:5|千位:6|千位:7|千位:8|千位:9|百位:0|百位:1|百位:2|百位:3|百位:4|百位:5|百位:6|百位:7|百位:8|百位:9|十位:0|十位:1|十位:2|十位:3|十位:4|十位:5|十位:6|十位:7|十位:8|十位:9|个位:0|个位:1|个位:2|个位:3|个位:4|个位:5|个位:6|个位:7|个位:8|个位:9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选三直选复式", GameMethod.RXZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 5, false);
                break;
            case 620:
            case 641://任选三 直选单式
            case 662:
            case 683:
            case 1671://分分，五分
            case 1755:
            case 2191:
            case 2608://秒秒彩
            case 2859:
            case 2959:
            case 3059:
            case 3159:
                _Count = GameLogic.count(_Stake, _DigBit, "任选三直选单式", GameMethod.RXZX3, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 621:
            case 642://任选三 直选和值
            case 663:
            case 684:
            case 1785://分分，五分
            case 1653:
            case 2200:
            case 2611://秒秒彩
            case 2860:
            case 2960:
            case 3060:
            case 3160:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选三直选和值", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27", 1, 28);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选三直选和值", GameMethod.ZXHZ3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 622:
            case 643://任选三 组三复式
            case 664:
            case 685:
            case 1647://分分，五分
            case 1732:
            case 2176:
            case 2614://秒秒彩
            case 2861:
            case 2961:
            case 3061:
            case 3161:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选三组三复式", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选三组三复式", GameMethod.ZU3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 623:
            case 644://任选三 组三单式
            case 665:
            case 686:
            case 1627://分分，五分
            case 1756:
            case 2194:
            case 2617://秒秒彩
            case 2862:
            case 2962:
            case 3062:
            case 3162:
                _Count = GameLogic.count(_Stake, _DigBit, "任选三组选单式", GameMethod.ZU3, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 624:
            case 645://任选三 组六复式
            case 666:
            case 687:
            case 1741://分分，五分
            case 1703:
            case 2182:
            case 2620://秒秒彩
            case 2863:
            case 2963:
            case 3063:
            case 3163:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任三组六复式", "0|1|2|3|4|5|6|7|8|9", 3, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任三组六复式", GameMethod.ZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 625:
            case 646://任选三 组六单式
            case 667:
            case 688:
            case 1743://分分，五分
            case 1699:
            case 2188:
            case 2623://秒秒彩
            case 2864:
            case 2964:
            case 3064:
            case 3164:
                _Count = GameLogic.count(_Stake, _DigBit, "三星组六单式", GameMethod.ZU6, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 626:
            case 647://任选三 混合组选
            case 668:
            case 689:
            case 1639://分分，五分
            case 1736:
            case 2179:
            case 2626://秒秒彩
            case 2865:
            case 2965:
            case 3065:
            case 3165:
                _Count = GameLogic.count(_Stake, _DigBit, "三星混合组选", GameMethod.ZUHH3, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 628:
            case 649://任选四 直选复式
            case 670:
            case 691:
            case 1669://分分，五分
            case 1829:
            case 2104:
            case 2632://秒秒彩
            case 2867:
            case 2967:
            case 3067:
            case 3167:
                //构建解析器
                selectors = GameLogic.createSelectors(_DigBit.split(",", -1).length, "任四直选复式", "0|1|2|3|4|5|6|7|8|9|万位:0|万位:1|万位:2|万位:3|万位:4|万位:5|万位:6|万位:7|万位:8|万位:9|千位:0|千位:1|千位:2|千位:3|千位:4|千位:5|千位:6|千位:7|千位:8|千位:9|百位:0|百位:1|百位:2|百位:3|百位:4|百位:5|百位:6|百位:7|百位:8|百位:9|十位:0|十位:1|十位:2|十位:3|十位:4|十位:5|十位:6|十位:7|十位:8|十位:9|个位:0|个位:1|个位:2|个位:3|个位:4|个位:5|个位:6|个位:7|个位:8|个位:9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任四直选复式", GameMethod.RXZX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 4, 5, false);
                break;
            case 629:
            case 650://任选四 直选单式
            case 671:
            case 692:
            case 1757://分分，五分
            case 1659:
            case 2095:
            case 2635://秒秒彩
            case 2868:
            case 2968:
            case 3068:
            case 3168:
                _Count = GameLogic.count(_Stake, _DigBit, "任选四直选单式", GameMethod.ZX4, null, SelectorType.TEXT, null, ",", -1, -1, true);
                break;
            case 630:
            case 651://任选四 组选24
            case 672:
            case 693:
            case 1740://分分，五分
            case 1624:
            case 2092:
            case 2638://秒秒彩
            case 2869:
            case 2969:
            case 3069:
            case 3169:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选四组选24", "0|1|2|3|4|5|6|7|8|9", 4, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选四组选24", GameMethod.SXZU24, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 631:
            case 652://任选四 组选12
            case 673:
            case 694:
            case 1694://分分，五分
            case 1833:
            case 2107:
            case 2641://秒秒彩
            case 2870:
            case 2970:
            case 3070:
            case 3170:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "任选四组选12", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                ((DigitalSelector)selectors.get(1)).setMinChosen(2);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选四组选12", GameMethod.SXZU12, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, true);
                break;
            case 632:
            case 653://任选四 组选6
            case 674:
            case 695:
            case 1815://分分，五分
            case 1629:
            case 2101:
            case 2644://秒秒彩
            case 2871:
            case 2971:
            case 3071:
            case 3171:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任三组选组三", "0|1|2|3|4|5|6|7|8|9", 2, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任三组选组三", GameMethod.SXZU6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, true);
                break;
            case 633:
            case 654://任选四 组选4
            case 675:
            case 696:
            case 1771://分分，五分
            case 1642:
            case 2098:
            case 2647://秒秒彩
            case 2872:
            case 2972:
            case 3072:
            case 3172:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "任选四组选4", "0|1|2|3|4|5|6|7|8|9", 1, 10);
                //计算注數
                _Count = GameLogic.count(_Stake, _DigBit, "任选四组选4", GameMethod.SXZU4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, true);
                break;
        }
        return _Count;
    }
}
