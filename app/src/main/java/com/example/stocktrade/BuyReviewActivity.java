package com.example.stocktrade;

import android.os.Bundle;
import android.view.View;
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

public class BuyReviewActivity extends AppCompatActivity {
    private EditText etStockName, etTotalAssets, etBuyAmount;
    private EditText etPositionRatio, etVolatility, etVolatilityValue;
    private EditText etReason1, etReason2, etReason3;
    private EditText etMaxLoss, etStopPrice, etRisk1, etRisk2, etRisk3;
    private EditText etFirstTarget, etFirstRatio, etSecondTarget, etChasePlan;
    private RadioGroup rgOperationType, rgCycle;
    private CheckBox cbEmotion1, cbEmotion2, cbEmotion3, cbEmotion4, cbEmotion5, cbEmotion6, cbEmotion7;
    private RadioGroup rgExtreme, rgPositionAdd, rgEmotionConfirm;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_review);

        dbHelper = new DBHelper(this);

        initViews();
        setupListeners();

        Button submitBtn = findViewById(R.id.submit_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);

        submitBtn.setOnClickListener(v -> validateAndSubmit());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etStockName = findViewById(R.id.et_stock_name);
        etTotalAssets = findViewById(R.id.et_total_assets);
        etBuyAmount = findViewById(R.id.et_buy_amount);
        etPositionRatio = findViewById(R.id.et_position_ratio);
        etVolatility = findViewById(R.id.et_volatility);
        etVolatilityValue = findViewById(R.id.et_volatility_value);
        rgOperationType = findViewById(R.id.rg_operation_type);
        etReason1 = findViewById(R.id.et_reason1);
        etReason2 = findViewById(R.id.et_reason2);
        etReason3 = findViewById(R.id.et_reason3);
        rgCycle = findViewById(R.id.rg_cycle);
        etMaxLoss = findViewById(R.id.et_max_loss);
        etStopPrice = findViewById(R.id.et_stop_price);
        etRisk1 = findViewById(R.id.et_risk1);
        etRisk2 = findViewById(R.id.et_risk2);
        etRisk3 = findViewById(R.id.et_risk3);
        rgExtreme = findViewById(R.id.rg_extreme);
        rgPositionAdd = findViewById(R.id.rg_position_add);
        etFirstTarget = findViewById(R.id.et_first_target);
        etFirstRatio = findViewById(R.id.et_first_ratio);
        etSecondTarget = findViewById(R.id.et_second_target);
        etChasePlan = findViewById(R.id.et_chase_plan);
        cbEmotion1 = findViewById(R.id.cb_emotion1);
        cbEmotion2 = findViewById(R.id.cb_emotion2);
        cbEmotion3 = findViewById(R.id.cb_emotion3);
        cbEmotion4 = findViewById(R.id.cb_emotion4);
        cbEmotion5 = findViewById(R.id.cb_emotion5);
        cbEmotion6 = findViewById(R.id.cb_emotion6);
        cbEmotion7 = findViewById(R.id.cb_emotion7);
        rgEmotionConfirm = findViewById(R.id.rg_emotion_confirm);
        
        // 默认勾选所有情绪自查项
        cbEmotion1.setChecked(true);
        cbEmotion2.setChecked(true);
        cbEmotion3.setChecked(true);
        cbEmotion4.setChecked(true);
        cbEmotion5.setChecked(true);
        cbEmotion6.setChecked(true);
        cbEmotion7.setChecked(true);
    }

    private void setupListeners() {
        etTotalAssets.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePositionRatio();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etBuyAmount.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePositionRatio();
                calculateVolatilityValue();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etVolatility.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateVolatilityValue();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void calculatePositionRatio() {
        try {
            double totalAssets = Double.parseDouble(etTotalAssets.getText().toString());
            double buyAmount = Double.parseDouble(etBuyAmount.getText().toString());
            
            if (totalAssets > 0) {
                double ratio = (buyAmount / totalAssets) * 100;
                etPositionRatio.setText(String.format("%.2f", ratio));
                
                if (ratio > 10) {
                    etPositionRatio.setTextColor(getResources().getColor(R.color.red_500));
                } else {
                    etPositionRatio.setTextColor(getResources().getColor(R.color.green_500));
                }
            }
        } catch (Exception e) {
            etPositionRatio.setText("");
        }
    }

    private void calculateVolatilityValue() {
        try {
            double buyAmount = Double.parseDouble(etBuyAmount.getText().toString());
            double volatility = Double.parseDouble(etVolatility.getText().toString());
            
            if (buyAmount > 0 && volatility >= 0) {
                double value = buyAmount * volatility / 100;
                etVolatilityValue.setText(String.format("¥%.2f", value));
            }
        } catch (Exception e) {
            etVolatilityValue.setText("");
        }
    }

    private void validateAndSubmit() {
        // 基础信息校验
        if (etStockName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写股票名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etTotalAssets.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写总账户总资产", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etBuyAmount.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写本次买入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPositionRatio.getText().toString().isEmpty()) {
            Toast.makeText(this, "请先填写总资产和买入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etVolatility.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写股票波动率", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etVolatilityValue.getText().toString().isEmpty()) {
            Toast.makeText(this, "请先填写买入金额和波动率", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgOperationType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择操作类型", Toast.LENGTH_SHORT).show();
            return;
        }

        // 仓位校验
        double ratio = Double.parseDouble(etPositionRatio.getText().toString());
        if (ratio > 15) {
            Toast.makeText(this, "仓位超限（超过15%）", Toast.LENGTH_SHORT).show();
            return;
        }

        // 核心逻辑校验
        if (etReason1.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写支撑上涨理由①", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etReason2.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写支撑上涨理由②", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etReason3.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写支撑上涨理由③", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgCycle.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择持仓周期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 风险测算校验
        if (etMaxLoss.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写最大容忍亏损比例", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etStopPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写硬性止损价格", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etRisk1.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写潜在重大利空①", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etRisk2.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写潜在重大利空②", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etRisk3.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写潜在重大利空③", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgExtreme.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择极端情况评估", Toast.LENGTH_SHORT).show();
            return;
        }

        // 极端情况校验
        RadioButton extremeYes = findViewById(rgExtreme.getCheckedRadioButtonId());
        if (extremeYes != null && extremeYes.getText().toString().equals("会")) {
            Toast.makeText(this, "极端情况评估选择'会'，请放弃本次操作", Toast.LENGTH_SHORT).show();
            return;
        }

        // 仓位约束校验
        if (rgPositionAdd.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择是否执行加仓", Toast.LENGTH_SHORT).show();
            return;
        }

        // 止盈规划校验
        if (etFirstTarget.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写第一止盈目标价", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etFirstRatio.getText().toString().isEmpty()) {
            Toast.makeText(this, "请填写第一止盈减仓比例", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etSecondTarget.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写第二止盈目标价", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etChasePlan.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请填写追高预案", Toast.LENGTH_SHORT).show();
            return;
        }

        // 情绪自查校验
        if (cbEmotion1.isChecked() || cbEmotion2.isChecked() || cbEmotion3.isChecked() ||
            cbEmotion4.isChecked() || cbEmotion5.isChecked() || cbEmotion6.isChecked() || cbEmotion7.isChecked()) {
            Toast.makeText(this, "情绪自查未通过，取消交易", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgEmotionConfirm.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请确认情绪自查无问题", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton confirmBtn = findViewById(rgEmotionConfirm.getCheckedRadioButtonId());
        if (confirmBtn != null && !confirmBtn.getText().toString().equals("确认无问题")) {
            Toast.makeText(this, "请确认情绪自查无问题", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建记录
        TradeRecord record = new TradeRecord();
        record.setType("买入");
        record.setStockName(etStockName.getText().toString());
        record.setFillTime(new Date());
        record.setStatus("pending");
        record.setVolatilityValue(etVolatilityValue.getText().toString());

        StringBuilder content = new StringBuilder();
        content.append("一、基础信息\n");
        content.append("1. 标的名称：").append(etStockName.getText()).append("\n");
        content.append("2. 当前总账户总资产：").append(etTotalAssets.getText()).append("\n");
        content.append("3. 本次买入金额：").append(etBuyAmount.getText()).append("\n");
        content.append("4. 本次买入后，该个股占总仓位比例：").append(etPositionRatio.getText()).append("%\n");
        content.append("5. 股票波动率：").append(etVolatility.getText()).append("%\n");
        content.append("6. 最大波动价值：").append(etVolatilityValue.getText()).append("\n");
        
        int operationId = rgOperationType.getCheckedRadioButtonId();
        String operation = "";
        if (operationId == R.id.rb_first) operation = "首次建仓";
        else if (operationId == R.id.rb_add) operation = "加仓";
        else if (operationId == R.id.rb_t) operation = "做T";
        content.append("7. 本次操作类型：").append(operation).append("\n\n");

        content.append("二、买入核心逻辑\n");
        content.append("1. 支撑上涨3条核心理由：\n");
        content.append("①").append(etReason1.getText()).append("\n");
        content.append("②").append(etReason2.getText()).append("\n");
        content.append("③").append(etReason3.getText()).append("\n");
        
        int cycleId = rgCycle.getCheckedRadioButtonId();
        String cycle = "";
        if (cycleId == R.id.rb_short) cycle = "短线（1个月内）";
        else if (cycleId == R.id.rb_mid) cycle = "中线（1-6个月）";
        else if (cycleId == R.id.rb_long) cycle = "长线（1年以上）";
        content.append("2. 本次持仓周期规划：").append(cycle).append("\n\n");

        content.append("三、风险测算\n");
        content.append("1. 单只个股最大容忍亏损比例：").append(etMaxLoss.getText()).append("%\n");
        content.append("2. 硬性止损价格：").append(etStopPrice.getText()).append("元\n");
        content.append("3. 3个潜在重大利空：\n");
        content.append("①").append(etRisk1.getText()).append("\n");
        content.append("②").append(etRisk2.getText()).append("\n");
        content.append("③").append(etRisk3.getText()).append("\n");
        content.append("4. 极端情况评估：").append(extremeYes.getText()).append("\n\n");

        content.append("四、仓位约束\n");
        content.append("1. 单只个股总仓位上限：≤15%\n");
        content.append("2. 全仓短线投机个股总仓位上限：≤20%\n");
        RadioButton positionAddBtn = findViewById(rgPositionAdd.getCheckedRadioButtonId());
        content.append("3. 账户整体浮亏超8%，是否执行加仓？").append(positionAddBtn.getText()).append("\n\n");

        content.append("五、止盈规划\n");
        content.append("1. 第一止盈目标价：").append(etFirstTarget.getText()).append("，到达减仓").append(etFirstRatio.getText()).append("%\n");
        content.append("2. 第二止盈目标价：").append(etSecondTarget.getText()).append("，到达清仓剩余筹码\n");
        content.append("3. 中途上涨后心态失控追高预案：").append(etChasePlan.getText()).append("\n\n");

        content.append("六、情绪自查（已确认无问题）\n\n");

        content.append("【提交时间】").append(record.getFillTime().toString()).append("\n");
        content.append("【状态】待生效（需1小时后进行最终审核）");

        record.setContent(content.toString());

        dbHelper.insertRecord(record);
        Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}
