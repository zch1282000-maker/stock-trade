package com.example.stocktrade;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stocktrade.data.DBHelper;
import com.example.stocktrade.data.TradeRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordDetailActivity extends AppCompatActivity {
    private ListView recordListView;
    private RecordAdapter adapter;
    private DBHelper dbHelper;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        dbHelper = new DBHelper(this);
        recordListView = findViewById(R.id.record_list);
        loadRecords();

        // 每分钟刷新一次列表，更新倒计时
        handler.postDelayed(refreshRunnable, 60000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadRecords();
            handler.postDelayed(this, 60000);
        }
    };

    private void loadRecords() {
        List<TradeRecord> records = dbHelper.getAllRecords();
        adapter = new RecordAdapter(records);
        recordListView.setAdapter(adapter);
    }

    private class RecordAdapter extends BaseAdapter {
        private List<TradeRecord> records;

        public RecordAdapter(List<TradeRecord> records) {
            this.records = records;
        }

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public TradeRecord getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return records.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(RecordDetailActivity.this)
                        .inflate(R.layout.record_item, parent, false);
                holder = new ViewHolder();
                holder.tvType = convertView.findViewById(R.id.tv_type);
                holder.tvStock = convertView.findViewById(R.id.tv_stock);
                holder.tvTime = convertView.findViewById(R.id.tv_time);
                holder.tvStatus = convertView.findViewById(R.id.tv_status);
                holder.tvVolatility = convertView.findViewById(R.id.tv_volatility);
                holder.tvConclusion = convertView.findViewById(R.id.tv_conclusion);
                holder.btnView = convertView.findViewById(R.id.btn_view);
                holder.btnReview = convertView.findViewById(R.id.btn_review);
                holder.btnVoid = convertView.findViewById(R.id.btn_void);
                holder.btnDelete = convertView.findViewById(R.id.btn_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TradeRecord record = records.get(position);
            holder.tvType.setText(record.getType());
            holder.tvStock.setText(record.getStockName());
            holder.tvTime.setText(formatTime(record.getFillTime()));
            
            // 显示最大波动价值（仅买入记录）
            if ("买入".equals(record.getType()) && record.getVolatilityValue() != null) {
                holder.tvVolatility.setVisibility(View.VISIBLE);
                holder.tvVolatility.setText("最大波动价值：" + record.getVolatilityValue());
            } else {
                holder.tvVolatility.setVisibility(View.GONE);
            }

            // 显示最终结论
            if (record.getConclusion() != null && !record.getConclusion().isEmpty()) {
                holder.tvConclusion.setVisibility(View.VISIBLE);
                holder.tvConclusion.setText("最终结论：" + record.getConclusion());
            } else {
                holder.tvConclusion.setVisibility(View.GONE);
            }

            // 设置状态显示
            String statusText = "";
            int statusColor = 0;
            
            switch (record.getStatus()) {
                case "pending":
                    long remainingMinutes = record.getRemainingMinutes();
                    if (remainingMinutes > 0) {
                        statusText = "待生效（剩余" + remainingMinutes + "分钟）";
                    } else {
                        statusText = "待生效";
                    }
                    statusColor = getResources().getColor(R.color.orange_500);
                    break;
                case "effective":
                    statusText = "生效";
                    statusColor = getResources().getColor(R.color.green_500);
                    break;
                case "voided":
                    statusText = "已作废";
                    statusColor = getResources().getColor(R.color.red_500);
                    break;
                default:
                    statusText = "未知";
                    statusColor = getResources().getColor(R.color.gray_600);
            }
            
            holder.tvStatus.setText(statusText);
            holder.tvStatus.setTextColor(statusColor);

            // 设置按钮可见性
            holder.btnReview.setVisibility(View.GONE);
            holder.btnVoid.setVisibility(View.GONE);
            
            if ("pending".equals(record.getStatus())) {
                if (record.canReview()) {
                    holder.btnReview.setVisibility(View.VISIBLE);
                    holder.btnReview.setEnabled(true);
                    holder.btnReview.setText("审核生效");
                } else {
                    holder.btnReview.setVisibility(View.VISIBLE);
                    holder.btnReview.setEnabled(false);
                    holder.btnReview.setText("等待审核");
                }
                holder.btnVoid.setVisibility(View.VISIBLE);
            } else if ("effective".equals(record.getStatus())) {
                holder.btnVoid.setVisibility(View.VISIBLE);
            }

            // 设置按钮点击事件
            holder.btnView.setOnClickListener(v -> showDetail(record));
            holder.btnReview.setOnClickListener(v -> showReviewModal(record));
            holder.btnVoid.setOnClickListener(v -> voidRecord(record.getId()));
            holder.btnDelete.setOnClickListener(v -> deleteRecord(record.getId()));

            return convertView;
        }

        private class ViewHolder {
            TextView tvType, tvStock, tvTime, tvStatus, tvVolatility, tvConclusion;
            Button btnView, btnReview, btnVoid, btnDelete;
        }
    }

    private String formatTime(Date date) {
        if (date == null) return "";
        
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        
        if (days == 0) {
            return "今天 " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } else if (days == 1) {
            return "昨天 " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date);
        }
    }

    private void showDetail(TradeRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(record.getType() + "审核记录");
        builder.setMessage(record.getContent());
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

    private void showReviewModal(TradeRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最终审核");

        View view = getLayoutInflater().inflate(R.layout.dialog_review, null);
        RadioGroup rgFilled = view.findViewById(R.id.rg_filled);
        RadioGroup rgConclusion = view.findViewById(R.id.rg_conclusion);
        
        // 根据交易类型设置不同的结论选项
        if ("买入".equals(record.getType())) {
            RadioButton rbExecute = view.findViewById(R.id.rb_execute);
            RadioButton rbAbandon = view.findViewById(R.id.rb_abandon);
            rbExecute.setText("执行本次下单");
            rbAbandon.setText("放弃本次操作");
        } else {
            RadioButton rbExecute = view.findViewById(R.id.rb_execute);
            RadioButton rbAbandon = view.findViewById(R.id.rb_abandon);
            rbExecute.setText("正常执行卖出");
            rbAbandon.setText("暂缓操作");
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            int filledId = rgFilled.getCheckedRadioButtonId();
            int conclusionId = rgConclusion.getCheckedRadioButtonId();
            
            if (filledId == -1) {
                Toast.makeText(this, "请确认所有条目如实填写完成", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (conclusionId == -1) {
                Toast.makeText(this, "请选择综合判断结论", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton filledBtn = view.findViewById(filledId);
            RadioButton conclusionBtn = view.findViewById(conclusionId);
            
            if (!"是".equals(filledBtn.getText())) {
                Toast.makeText(this, "请确认所有条目如实填写完成", Toast.LENGTH_SHORT).show();
                return;
            }

            record.setStatus("effective");
            record.setConclusion(conclusionBtn.getText().toString());
            dbHelper.updateRecord(record);
            
            Toast.makeText(this, "审核完成，记录已生效", Toast.LENGTH_SHORT).show();
            loadRecords();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void voidRecord(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning);
        builder.setMessage("确定要作废这条记录吗？");
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            TradeRecord record = dbHelper.getRecordById(id);
            if (record != null) {
                record.setStatus("voided");
                dbHelper.updateRecord(record);
                Toast.makeText(this, "记录已作废", Toast.LENGTH_SHORT).show();
                loadRecords();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void deleteRecord(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning);
        builder.setMessage("确定要删除这条记录吗？");
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dbHelper.deleteRecord(id);
            Toast.makeText(this, R.string.record_deleted, Toast.LENGTH_SHORT).show();
            loadRecords();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
