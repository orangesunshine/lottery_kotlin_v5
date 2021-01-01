package com.bdb.lottery.algorithm;

import java.util.List;

/**
 * Created by XB on 2016/4/12.
 */
public class ElevenChoiceFiveCalculate {

    public static int stake(String _Stake, int _PlayID) throws Exception {
        //将投注号码放入二维数组
        int _Count = 0;
        List<Selector> selectors  = null;
        //根据ID计算
        switch (_PlayID) {
            case 342://11选五-(三星直选 直选复式	前、中、后)
            case 344:
            case 340:
            case 483:
            case 485:
            case 487:
            case 534:
            case 536:
            case 538:
            case 168:
            case 170:
            case 172:
            case 2230:
            case 2236:
            case 2224:
                //构建解析器
                selectors = GameLogic.createSelectors(3, "三星直选复式", "01|02|03|04|05|06|07|08|09|10|11", 1, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2", "三星直选复式", GameMethod.LTZX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 3, 3, false);
                break;
            case 341://11选五-(三星直选 直选单式	前、中、后)
            case 345:
            case 343:
            case 484:
            case 486:
            case 488:
            case 535:
            case 537:
            case 539:
            case 169:
            case 171:
            case 173:
            case 2233:
            case 2239:
            case 2227:
                _Count = GameLogic.count(_Stake, "", "三星直选单式", GameMethod.LTZX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 349://11选五-(三星组选 组选复式	前、中、后)
            case 352:
            case 346:
            case 489:
            case 492:
            case 495:
            case 540:
            case 543:
            case 546:
            case 174:
            case 177:
            case 180:
            case 2251:
            case 2260:
            case 2242:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星组选复式", "01|02|03|04|05|06|07|08|09|10|11", 3, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星组选复式", GameMethod.LTZU3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 350://11选五-(三星组选 组选单式	前、中、后)
            case 347:
            case 353:
            case 490:
            case 493:
            case 496:
            case 541:
            case 544:
            case 547:
            case 175:
            case 178:
            case 181:
            case 2254:
            case 2263:
            case 2245:
                _Count = GameLogic.count(_Stake, "", "三星组选单式", GameMethod.LTZU3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 348://11选五-(三星组选 组选胆拖	前、中、后)
            case 354:
            case 351:
            case 491:
            case 494:
            case 497:
            case 542:
            case 545:
            case 548:
            case 176:
            case 179:
            case 182:
            case 2257:
            case 2266:
            case 2248:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "三星组选胆拖", "01|02|03|04|05|06|07|08|09|10|11", 1, 2);
                ((DigitalSelector)selectors.get(1)).setMinChosen(1);
                ((DigitalSelector)selectors.get(1)).setMaxChosen(11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "三星组选胆拖", GameMethod.LTZUDT3, Nonrepeatability.ALL, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 355://11选五-(二星直选 直选复式	前、后)
            case 357:
            case 498:
            case 500:
            case 549:
            case 551:
            case 183:
            case 185:
            case 2275:
            case 2269:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星直选复式", "01|02|03|04|05|06|07|08|09|10|11", 1, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星直选复式", GameMethod.LTZX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 356://11选五-(二星直选 直选单式	前、后)
            case 358:
            case 499:
            case 501:
            case 550:
            case 552:
            case 184:
            case 186:
            case 2278:
            case 2272:
                _Count = GameLogic.count(_Stake, "", "二星直选单式", GameMethod.LTZX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 361://11选五-(二星组选 组选复式	前、后)
            case 359:
            case 502:
            case 504:
            case 553:
            case 555:
            case 187:
            case 189:
            case 2287:
            case 2281:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "二星组选复式", "01|02|03|04|05|06|07|08|09|10|11", 2, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "二星组选复式", GameMethod.LTZU2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 360://11选五-(二星组选 组选单式	前、后)
            case 362:
            case 503:
            case 505:
            case 554:
            case 556:
            case 188:
            case 190:
            case 2290:
            case 2284:
                _Count = GameLogic.count(_Stake, "", "二星组选单式", GameMethod.LTZU2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 577://11选五-(二星组选 组选胆拖	前、后)
            case 578:
            case 595:
            case 596:
            case 604:
            case 605:
            case 586:
            case 587:
            case 2356:
            case 2353:
                //构建解析器
                selectors = GameLogic.createSelectors(2, "二星组选胆拖", "01|02|03|04|05|06|07|08|09|10|11", 1, 1);
                ((DigitalSelector)selectors.get(1)).setMinChosen(1);
                ((DigitalSelector)selectors.get(1)).setMaxChosen(11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1", "二星组选胆拖", GameMethod.LTZUDT2, Nonrepeatability.ALL, SelectorType.DIGITAL, selectors, "\\|", 2, 2, false);
                break;
            case 401: //11选五-(任选复式 任选一中一)
            case 385:
            case 518:
            case 561:
            case 2305:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选一中一", "01|02|03|04|05|06|07|08|09|10|11", 1, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选一中一", GameMethod.LTRX1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 402://11选五-(任选复式 任选二中二)
            case 562:
            case 386:
            case 519:
            case 2308:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选二中二", "01|02|03|04|05|06|07|08|09|10|11", 2, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选二中二", GameMethod.LTRX2, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 403://11选五-(任选复式 任选三中三)
            case 387:
            case 563:
            case 520:
            case 2311:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选三中三", "01|02|03|04|05|06|07|08|09|10|11", 3, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选三中三", GameMethod.LTRX3, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 404://11选五-(任选复式 任选四中四)
            case 521:
            case 388:
            case 564:
            case 2314:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选四中四", "01|02|03|04|05|06|07|08|09|10|11", 4, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选四中四", GameMethod.LTRX4, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 405://11选五-(任选复式 任选五中五)
            case 389:
            case 522:
            case 565:
            case 2317:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选五中五", "01|02|03|04|05|06|07|08|09|10|11", 5, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选五中五", GameMethod.LTRX5, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 406://11选五-(任选复式 任选六中五)
            case 523:
            case 390:
            case 566:
            case 2320:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选六中五", "01|02|03|04|05|06|07|08|09|10|11", 6, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选六中五", GameMethod.LTRX6, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 407://11选五-(任选复式 任选七中五)
            case 391:
            case 524:
            case 567:
            case 2323:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选七中五", "01|02|03|04|05|06|07|08|09|10|11", 7, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选七中五", GameMethod.LTRX7, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 408://11选五-(任选复式 任选八中五)
            case 568:
            case 392:
            case 525:
            case 2326:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "任选八中五", "01|02|03|04|05|06|07|08|09|10|11", 8, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "任选八中五", GameMethod.LTRX8, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 409://11选五-(任选单式 任选一中一)
            case 393:
            case 569:
            case 526:
            case 2329:
                _Count = GameLogic.count(_Stake, "", "任选一中一单式 ", GameMethod.LTRX1, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 410://11选五-(任选单式 任选二中二)
            case 570:
            case 527:
            case 394:
            case 2332:
                _Count = GameLogic.count(_Stake, "", "任选二中二单式 ", GameMethod.LTRX2, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 411://11选五-(任选单式 任选三中三)
            case 395:
            case 528:
            case 571:
            case 2335:
                _Count = GameLogic.count(_Stake, "", "任选三中三单式 ", GameMethod.LTRX3, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 412://11选五-(任选单式 任选四中四)
            case 572:
            case 396:
            case 529:
            case 2338:
                _Count = GameLogic.count(_Stake, "", "任选四中四单式 ", GameMethod.LTRX4, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 413://11选五-(任选单式 任选五中五)
            case 573:
            case 397:
            case 530:
            case 2341:
                _Count = GameLogic.count(_Stake, "", "任选五中五单式 ", GameMethod.LTRX5, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 414://11选五-(任选单式 任选六中五)
            case 398:
            case 531:
            case 574:
            case 2344:
                _Count = GameLogic.count(_Stake, "", "任选六中五单式 ", GameMethod.LTRX6, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 415://11选五-(任选单式 任选七中五)
            case 575:
            case 532:
            case 399:
            case 2347:
                _Count = GameLogic.count(_Stake, "", "任选七中五单式 ", GameMethod.LTRX7, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 416://11选五-(任选单式 任选八中五)
            case 533:
            case 400:
            case 576:
            case 2350:
                _Count = GameLogic.count(_Stake, "", "任选八中五单式 ", GameMethod.LTRX8, null, SelectorType.TEXT, null, ",", -1, -1, false);
                break;
            case 370://11选五-(不定位胆 )
            case 371:
            case 372:
            case 514:
            case 557:
            case 513:
            case 559:
            case 198:
            case 558:
            case 200:
            case 199:
            case 515:
            case 2293:
            case 2296:
            case 2299:
                //构建解析器
                selectors = GameLogic.createSelectors(1, "三星不定位胆", "01|02|03|04|05|06|07|08|09|10|11", 1, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0", "三星不定位胆", GameMethod.LTBDW1, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 1, false);
                break;
            case 374://11选五-(定位胆	五星定位胆)
            case 202:
            case 560:
            case 517:
            case 2302:
                //构建解析器
                selectors = GameLogic.createSelectors(5, "五星定位胆", "01|02|03|04|05|06|07|08|09|10|11", 0, 11);
                //计算注數
                _Count = GameLogic.count(_Stake, "0,1,2,3,4,5", "五星定位胆", GameMethod.LTDWD, Nonrepeatability.SINGLE, SelectorType.DIGITAL, selectors, "\\|", 1, 5, false);
                break;
        }
        return _Count;
    }

}
