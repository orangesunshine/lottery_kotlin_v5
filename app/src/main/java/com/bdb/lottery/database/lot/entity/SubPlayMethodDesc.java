package com.bdb.lottery.database.lot.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.orange.bdb.DaoSession;
import com.orange.bdb.SubPlayMethodDao;
import com.orange.bdb.SubPlayMethodDescDao;

@Entity(nameInDb = "Sub_play_method_desc", createInDb = false)
public class SubPlayMethodDesc {
    @Id
    @Property(nameInDb = "row_id")
    private Long row_id; //id
    @NotNull
    private String play_method_name;
    private String group_name;
    private String play_method_description;
    private String icon_ids;
    private String digit;//位数(万、千、百、十、个对应0,1,2,3,4)
    private String one_zhu_item_counts;
    private String ball_groups_item_title;
    private String belongto;
    private String atleast_wei_shu;
    private String digit_titles;
    private String ball_text_list;
    @NotNull
    @Property(nameInDb = "isdanshi")
    private boolean isdanshi;
    @Property(nameInDb = "is_start_zero")
    private boolean is_start_zero;
    @Property(nameInDb = "existsLi")
    private boolean existsLi;
    @Property(nameInDb = "one_zhu_allow_repeat")
    private boolean one_zhu_allow_repeat;
    @Property(nameInDb = "is_need_show_weizhi")
    private boolean is_need_show_weizhi;
    @Property(nameInDb = "isShowZero")
    private boolean isShowZero;
    @Property(nameInDb = "isBuildAll")
    private boolean isBuildAll;
    @Property(nameInDb = "isReorder")
    private boolean isReorder;
    @Property(nameInDb = "is_show_type_select")
    private boolean is_show_type_select;
    private int item_ball_num_counts;
    private int single_num_counts;
    private int ball_groups_counts;
    private int random_group_num;
    @Generated(hash = 1288370152)
    public SubPlayMethodDesc(Long row_id, @NotNull String play_method_name,
            String group_name, String play_method_description, String icon_ids,
            String digit, String one_zhu_item_counts, String ball_groups_item_title,
            String belongto, String atleast_wei_shu, String digit_titles,
            String ball_text_list, boolean isdanshi, boolean is_start_zero,
            boolean existsLi, boolean one_zhu_allow_repeat,
            boolean is_need_show_weizhi, boolean isShowZero, boolean isBuildAll,
            boolean isReorder, boolean is_show_type_select,
            int item_ball_num_counts, int single_num_counts, int ball_groups_counts,
            int random_group_num) {
        this.row_id = row_id;
        this.play_method_name = play_method_name;
        this.group_name = group_name;
        this.play_method_description = play_method_description;
        this.icon_ids = icon_ids;
        this.digit = digit;
        this.one_zhu_item_counts = one_zhu_item_counts;
        this.ball_groups_item_title = ball_groups_item_title;
        this.belongto = belongto;
        this.atleast_wei_shu = atleast_wei_shu;
        this.digit_titles = digit_titles;
        this.ball_text_list = ball_text_list;
        this.isdanshi = isdanshi;
        this.is_start_zero = is_start_zero;
        this.existsLi = existsLi;
        this.one_zhu_allow_repeat = one_zhu_allow_repeat;
        this.is_need_show_weizhi = is_need_show_weizhi;
        this.isShowZero = isShowZero;
        this.isBuildAll = isBuildAll;
        this.isReorder = isReorder;
        this.is_show_type_select = is_show_type_select;
        this.item_ball_num_counts = item_ball_num_counts;
        this.single_num_counts = single_num_counts;
        this.ball_groups_counts = ball_groups_counts;
        this.random_group_num = random_group_num;
    }
    @Generated(hash = 1001485179)
    public SubPlayMethodDesc() {
    }
    public Long getRow_id() {
        return this.row_id;
    }
    public void setRow_id(Long row_id) {
        this.row_id = row_id;
    }
    public String getPlay_method_name() {
        return this.play_method_name;
    }
    public void setPlay_method_name(String play_method_name) {
        this.play_method_name = play_method_name;
    }
    public String getGroup_name() {
        return this.group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getPlay_method_description() {
        return this.play_method_description;
    }
    public void setPlay_method_description(String play_method_description) {
        this.play_method_description = play_method_description;
    }
    public String getIcon_ids() {
        return this.icon_ids;
    }
    public void setIcon_ids(String icon_ids) {
        this.icon_ids = icon_ids;
    }
    public String getDigit() {
        return this.digit;
    }
    public void setDigit(String digit) {
        this.digit = digit;
    }
    public String getOne_zhu_item_counts() {
        return this.one_zhu_item_counts;
    }
    public void setOne_zhu_item_counts(String one_zhu_item_counts) {
        this.one_zhu_item_counts = one_zhu_item_counts;
    }
    public String getBall_groups_item_title() {
        return this.ball_groups_item_title;
    }
    public void setBall_groups_item_title(String ball_groups_item_title) {
        this.ball_groups_item_title = ball_groups_item_title;
    }
    public String getBelongto() {
        return this.belongto;
    }
    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }
    public String getAtleast_wei_shu() {
        return this.atleast_wei_shu;
    }
    public void setAtleast_wei_shu(String atleast_wei_shu) {
        this.atleast_wei_shu = atleast_wei_shu;
    }
    public String getDigit_titles() {
        return this.digit_titles;
    }
    public void setDigit_titles(String digit_titles) {
        this.digit_titles = digit_titles;
    }
    public String getBall_text_list() {
        return this.ball_text_list;
    }
    public void setBall_text_list(String ball_text_list) {
        this.ball_text_list = ball_text_list;
    }
    public boolean getIsdanshi() {
        return this.isdanshi;
    }
    public void setIsdanshi(boolean isdanshi) {
        this.isdanshi = isdanshi;
    }
    public boolean getIs_start_zero() {
        return this.is_start_zero;
    }
    public void setIs_start_zero(boolean is_start_zero) {
        this.is_start_zero = is_start_zero;
    }
    public boolean getExistsLi() {
        return this.existsLi;
    }
    public void setExistsLi(boolean existsLi) {
        this.existsLi = existsLi;
    }
    public boolean getOne_zhu_allow_repeat() {
        return this.one_zhu_allow_repeat;
    }
    public void setOne_zhu_allow_repeat(boolean one_zhu_allow_repeat) {
        this.one_zhu_allow_repeat = one_zhu_allow_repeat;
    }
    public boolean getIs_need_show_weizhi() {
        return this.is_need_show_weizhi;
    }
    public void setIs_need_show_weizhi(boolean is_need_show_weizhi) {
        this.is_need_show_weizhi = is_need_show_weizhi;
    }
    public boolean getIsShowZero() {
        return this.isShowZero;
    }
    public void setIsShowZero(boolean isShowZero) {
        this.isShowZero = isShowZero;
    }
    public boolean getIsBuildAll() {
        return this.isBuildAll;
    }
    public void setIsBuildAll(boolean isBuildAll) {
        this.isBuildAll = isBuildAll;
    }
    public boolean getIsReorder() {
        return this.isReorder;
    }
    public void setIsReorder(boolean isReorder) {
        this.isReorder = isReorder;
    }
    public boolean getIs_show_type_select() {
        return this.is_show_type_select;
    }
    public void setIs_show_type_select(boolean is_show_type_select) {
        this.is_show_type_select = is_show_type_select;
    }
    public int getItem_ball_num_counts() {
        return this.item_ball_num_counts;
    }
    public void setItem_ball_num_counts(int item_ball_num_counts) {
        this.item_ball_num_counts = item_ball_num_counts;
    }
    public int getSingle_num_counts() {
        return this.single_num_counts;
    }
    public void setSingle_num_counts(int single_num_counts) {
        this.single_num_counts = single_num_counts;
    }
    public int getBall_groups_counts() {
        return this.ball_groups_counts;
    }
    public void setBall_groups_counts(int ball_groups_counts) {
        this.ball_groups_counts = ball_groups_counts;
    }
    public int getRandom_group_num() {
        return this.random_group_num;
    }
    public void setRandom_group_num(int random_group_num) {
        this.random_group_num = random_group_num;
    }
}
