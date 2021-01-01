package com.bdb.lottery.algorithm;

/**
 * Created by XB on 2015/8/11.
 */
public class DigitalSelector implements Selector {
    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 允许投注号码规范
     */
    private String no;
    /**
     * 最小选择位数
     */
    private Integer minChosen;
    /**
     * 最大选择位数
     */
    private Integer maxChosen;
    /**
     * 注内分隔符
     */
    private String separator;

    public DigitalSelector(){}

    public DigitalSelector(String name, String title, String no, Integer minchosen, Integer maxChosen, String separator){
        this.name=name;
        this.title=title;
        this.no=no;
        this.minChosen=minchosen;
        this.maxChosen=maxChosen;
        this.separator=separator;
    }

    public DigitalSelector(String name, Integer minchosen, Integer maxChosen, String separator){
        this.name=name;
        this.minChosen=minchosen;
        this.maxChosen=maxChosen;
        this.separator=separator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Integer getMinChosen() {
        return minChosen;
    }

    public void setMinChosen(Integer minChosen) {
        this.minChosen = minChosen;
    }

    public Integer getMaxChosen() {
        return maxChosen;
    }

    public void setMaxChosen(Integer maxChosen) {
        this.maxChosen = maxChosen;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }




}
