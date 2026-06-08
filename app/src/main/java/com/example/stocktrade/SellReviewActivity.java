package com.example.stocktrade;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stocktrade.data.DBHelper;
import com.example.stocktrade.data.TradeRecord;

import java.util.Date;

public class SellReviewActivity extends AppCompatActivity {
    private EditText etStockName, etCost, etProfitLoss;
    private RadioGroup rgSellRatio;
    private RadioGroup rgProfitTarget;
    private EditText etLogicFulfilled;
    private RadioGroup rgStopLoss;
    private EditText etLogicDisappear, etIrreversibleNegative;
    private CheckBox cbEmotion1, cbEmotion2, cbEmotion3, cbEmotion4;
    private RadioGroup rgSwitch;
    private EditText etBuyBackCondition;
    private RadioGroup rgEmotionConfirm;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_review);

        dbHelper = new DBHelper(this);

        initViews();

        Button submitBtn = findViewById(R.id.submit_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);

        submitBtn.setOnClickListener(v -> validateAndSubmit());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etStockName = findViewById(R.id.et_stock_name);
        etCost = findViewById(R.id.et_cost);
        etProfitLoss = findViewById(R.id.et_profit_loss);
        rgSellRatio = findViewById(R.id.rg_sell_ratio);
        rgProfitTarget = findViewById(R.id.rg_profit_target);
        etLogicFulfilled = findViewById(R.id.et_logic_fulfilled);
        rgStopLoss = findViewById(R.id.rg_stop_loss);
        etLogicDisappear = findViewById(R.id.et_logic_disappear);
        etIrreversibleNegative = findViewById(R.id.et_irreversible_negative);
        cbEmotion1 = findViewById(R.id.cb_emotion1);
        cbEmotion2 = findViewById(R.id.cb_emotion2);
        cbEmotion3 = findViewById(R.id.cb_emotion3);
        cbEmotion4 = findViewById(R.id.cb_emotion4);
        rgSwitch = findViewById(R.id.rg_switch);
        etBuyBackCondition = findViewById(R.id.et_buy_back_condition);
        rgEmotionConfirm = findViewById(R.id.rg_emotion_confirm);
        
        // 默认勾选所有情绪自查项
        cbEmotion1.setChecked(true);
        cbEmotion2.setChecked(true);
        cbEmotion3.setChecked(true);
        cbEmotion4.setChecked(true);
    }

    private void validateAndSubmit() {
        // 基础信息校验
        if (etStockName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写股票名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etCost.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写当前持仓成本", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etProfitLoss.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写当前浮盈/浮亏", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgSellRatio.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择卖出比例", Toast.LENGTH_SHORT).show();
            return;
        }

        // 卖出核心依据校验
        if (rgProfitTarget.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择是否到达预设止盈目标", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton profitTargetBtn = findViewById(rgProfitTarget.getCheckedRadioButtonId());
        if ("是".equals(profitTargetBtn.getText()) && etLogicFulfilled.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写逻辑兑现情况", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgStopLoss.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择是否跌破预设止损线", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton stopLossBtn = findViewById(rgStopLoss.getCheckedRadioButtonId());
        if ("是".equals(stopLossBtn.getText())) {
            if (etLogicDisappear.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "请填写原有上涨逻辑消失点", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etIrreversibleNegative.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "请填写不可逆利空", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 情绪自查校验
        if (cbEmotion1.isChecked() || cbEmotion2.isChecked() || cbEmotion3.isChecked() || cbEmotion4.isChecked()) {
            Toast.makeText(this, "情绪自查未通过，请取消所有选项", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgEmotionConfirm.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请确认情绪自查无问题", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton confirmBtn = findViewById(rgEmotionConfirm.getCheckedRadioButtonId());
        if (!"确认无问题".equals(confirmBtn.getText())) {
            Toast.makeText(this, "请确认情绪自查无问题", Toast.LENGTH_SHORT).show();
            return;
        }

        // 后续预案校验
        if (rgSwitch.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择是否换股", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton switchBtn = findViewById(rgSwitch.getCheckedRadioButtonId());
        if ("否".equals(switchBtn.getText()) && etBuyBackCondition.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写重新买回的条件", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建记录
        TradeRecord record = new TradeRecord();
        record.setType("卖出");
        record.setStockName(etStockName.getText().toString());
        record.setFillTime(new Date());
        record.setStatus("pending");

        StringBuilder content = new StringBuilder();
        content.append("一、基础信息\n");
        content.append("1. 标的名称：").append(etStockName.getText()).append("\n");
        content.append("2. 当前持仓成本：").append(etCost.getText()).append("\n");
        content.append("3. 当前浮盈/浮亏：").append(etProfitLoss.getText()).append("\n");
        
        int ratioId = rgSellRatio.getCheckedRadioButtonId();
        String ratio = "";
        if (ratioId == R.id.rb_partial) ratio = "部分减仓";
        else if (ratioId == R.id.rb_all) ratio = "全部清仓";
        content.append("4. 卖出比例：").append(ratio).append("\n\n");

        content.append("二、卖出核心依据\n");
        content.append("情况1：止盈卖出\n");
        content.append("1. 是否到达预设止盈目标：").append(profitTargetBtn.getText()).append("\n");
        content.append("2. 逻辑是否兑现完毕：").append(etLogicFulfilled.getText()).append("\n\n");

        content.append("情况2：止损/逻辑破坏卖出\n");
        content.append("1. 是否跌破预设止损线：").append(stopLossBtn.getText()).append("\n");
        content.append("2. 当初买入逻辑是否彻底失效：\n");
        content.append("①原有上涨逻辑消失点：").append(etLogicDisappear.getText()).append("\n");
        content.append("②个股/行业出现不可逆利空：").append(etIrreversibleNegative.getText()).append("\n\n");

        content.append("三、情绪自查（已确认无问题）\n\n");

        content.append("四、后续预案\n");
        content.append("1. 卖出后是否立刻换其他个股买入：").append(switchBtn.getText()).append("\n");
        content.append("2. 后续重新买回的条件：").append(etBuyBackCondition.getText()).append("\n\n");

        content.append("【提交时间】").append(record.getFillTime().toString()).append("\n");
        content.append("【状态】待生效（需1小时后进行最终审核）");

        record.setContent(content.toString());

        dbHelper.insertRecord(record);
        Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}
