package com.example.stocktrade;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stocktrade.data.TemplateManager;

public class TemplateConfigActivity extends AppCompatActivity {
    private RadioGroup rgTemplateType;
    private EditText etTemplateContent;
    private TemplateManager templateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_config);

        templateManager = new TemplateManager(this);
        rgTemplateType = findViewById(R.id.rg_template_type);
        etTemplateContent = findViewById(R.id.et_template_content);

        rgTemplateType.setOnCheckedChangeListener((group, checkedId) -> {
            loadTemplate();
        });

        Button saveBtn = findViewById(R.id.save_btn);
        Button resetBtn = findViewById(R.id.reset_btn);
        Button backBtn = findViewById(R.id.back_btn);

        saveBtn.setOnClickListener(v -> saveTemplate());
        resetBtn.setOnClickListener(v -> resetTemplate());
        backBtn.setOnClickListener(v -> finish());

        loadTemplate();
    }

    private void loadTemplate() {
        String template;
        if (rgTemplateType.getCheckedRadioButtonId() == R.id.rb_buy) {
            template = templateManager.getBuyTemplate();
        } else {
            template = templateManager.getSellTemplate();
        }
        etTemplateContent.setText(template);
    }

    private void saveTemplate() {
        String content = etTemplateContent.getText().toString();
        if (rgTemplateType.getCheckedRadioButtonId() == R.id.rb_buy) {
            templateManager.setBuyTemplate(content);
        } else {
            templateManager.setSellTemplate(content);
        }
        Toast.makeText(this, "模板已保存", Toast.LENGTH_SHORT).show();
    }

    private void resetTemplate() {
        if (rgTemplateType.getCheckedRadioButtonId() == R.id.rb_buy) {
            templateManager.resetBuyTemplate();
        } else {
            templateManager.resetSellTemplate();
        }
        loadTemplate();
        Toast.makeText(this, "已恢复默认模板", Toast.LENGTH_SHORT).show();
    }
}
