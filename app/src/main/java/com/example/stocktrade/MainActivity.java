package com.example.stocktrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buyBtn = findViewById(R.id.buy_btn);
        Button sellBtn = findViewById(R.id.sell_btn);
        Button recordsBtn = findViewById(R.id.records_btn);
        Button templateBtn = findViewById(R.id.template_btn);

        buyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BuyReviewActivity.class);
            startActivity(intent);
        });

        sellBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SellReviewActivity.class);
            startActivity(intent);
        });

        recordsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordDetailActivity.class);
            startActivity(intent);
        });

        templateBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TemplateConfigActivity.class);
            startActivity(intent);
        });
    }
}
