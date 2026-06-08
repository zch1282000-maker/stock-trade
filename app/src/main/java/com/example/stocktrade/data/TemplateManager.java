package com.example.stocktrade.data;

import android.content.Context;
import android.content.SharedPreferences;

public class TemplateManager {
    private static final String PREF_NAME = "StockTradeTemplates";
    private static final String KEY_BUY_TEMPLATE = "buy_template";
    private static final String KEY_SELL_TEMPLATE = "sell_template";

    private static final String DEFAULT_BUY_TEMPLATE = 
        "一、基础信息\n" +
        "1. 标的名称/代码：\n" +
        "2. 当前总账户总资产：\n" +
        "3. 本次买入金额：\n" +
        "4. 本次买入后，该个股占总仓位比例：\n" +
        "5. 本次操作类型：首次建仓 / 加仓 / 做T\n\n" +
        "二、买入核心逻辑\n" +
        "1. 支撑上涨3条核心理由：\n" +
        "①\n" +
        "②\n" +
        "③\n" +
        "2. 本次持仓周期规划：短线（1个月内）/中线（1-6个月）/长线（1年以上）\n\n" +
        "三、风险测算\n" +
        "1. 单只个股最大容忍亏损比例：____%\n" +
        "2. 硬性止损价格：____元\n" +
        "3. 3个潜在重大利空：\n" +
        "①\n" +
        "②\n" +
        "③\n" +
        "4. 极端情况评估：如果跌到止损位，这笔亏损会不会影响日常生活、睡眠心态？□会 □不会\n\n" +
        "四、仓位约束\n" +
        "1. 单只个股总仓位上限：≤15%\n" +
        "2. 全仓短线投机个股总仓位上限：≤20%\n" +
        "3. 账户整体浮亏超8%，是否执行加仓？□是 □否\n\n" +
        "五、止盈规划\n" +
        "1. 第一止盈目标价：，到达减仓%\n" +
        "2. 第二止盈目标价：____，到达清仓剩余筹码\n" +
        "3. 中途上涨后心态失控追高预案：\n\n" +
        "六、情绪自查\n" +
        "□ 刚大涨，眼红追涨\n" +
        "□ 连续亏损，急于回本加仓摊薄\n" +
        "□ 刷短视频、股吧、群聊听别人推荐\n" +
        "□ 熬夜、烦躁、焦虑、心态浮躁时操作\n" +
        "□ 短期连续盈利，自我膨胀想重仓梭哈\n" +
        "□ 急于快速赚钱，追求短期暴利\n" +
        "□ 害怕踏空，不分析逻辑直接买入\n\n" +
        "七、最终审核结论\n" +
        "1. 以上所有条目全部如实填写完成：□是 □否\n" +
        "2. 冷静等待缓冲期已到期：□是 □否\n" +
        "3. 综合判断：□执行本次下单 □放弃本次操作\n" +
        "操作人签字：\n" +
        "日期：";

    private static final String DEFAULT_SELL_TEMPLATE =
        "一、基础信息\n" +
        "1. 标的名称/代码：\n" +
        "2. 当前持仓成本：\n" +
        "3. 当前浮盈/浮亏：\n" +
        "4. 卖出比例：部分减仓 / 全部清仓\n\n" +
        "二、卖出核心依据\n" +
        "情况1：止盈卖出\n" +
        "1. 是否到达预设止盈目标：□是 □否\n" +
        "2. 逻辑是否兑现完毕：\n\n" +
        "情况2：止损/逻辑破坏卖出\n" +
        "1. 是否跌破预设止损线：□是 □否\n" +
        "2. 当初买入逻辑是否彻底失效：\n" +
        "①原有上涨逻辑消失点：\n" +
        "②个股/行业出现不可逆利空：\n\n" +
        "三、情绪自查\n" +
        "□ 短期大跌恐慌，害怕继续下跌无脑割肉\n" +
        "□ 小幅回撤，承受不住波动急于卖出\n" +
        "□ 小幅盈利，拿不住一点利润就跑路\n" +
        "□ 看到其他股票大涨，想换股操作\n\n" +
        "四、后续预案\n" +
        "1. 卖出后是否立刻换其他个股买入：□是 □否\n" +
        "2. 后续重新买回的条件：\n\n" +
        "五、最终结论\n" +
        "综合判断：□正常执行卖出 □暂缓操作，隔日再评估\n" +
        "日期：";

    private SharedPreferences prefs;

    public TemplateManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getBuyTemplate() {
        return prefs.getString(KEY_BUY_TEMPLATE, DEFAULT_BUY_TEMPLATE);
    }

    public void setBuyTemplate(String template) {
        prefs.edit().putString(KEY_BUY_TEMPLATE, template).apply();
    }

    public String getSellTemplate() {
        return prefs.getString(KEY_SELL_TEMPLATE, DEFAULT_SELL_TEMPLATE);
    }

    public void setSellTemplate(String template) {
        prefs.edit().putString(KEY_SELL_TEMPLATE, template).apply();
    }

    public void resetBuyTemplate() {
        setBuyTemplate(DEFAULT_BUY_TEMPLATE);
    }

    public void resetSellTemplate() {
        setSellTemplate(DEFAULT_SELL_TEMPLATE);
    }
}
