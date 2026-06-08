import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from datetime import datetime

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# 定义起始日期（70年代开始）
start_date = '1970-01-01'
end_date = datetime.now().strftime('%Y-%m-%d')

print(f"开始生成数据: {start_date} 到 {end_date}")

# 设置随机种子，确保每次生成相同的数据
np.random.seed(42)

# 生成日期范围（按周）
date_range = pd.date_range(start=start_date, end=end_date, freq='W')
print(f"生成日期范围: {len(date_range)} 周")

# 生成黄金数据
gold_base = 35  # 70年代初黄金价格
gold_trend = np.linspace(0, 1, len(date_range))
gold_volatility = np.random.normal(0, 0.01, len(date_range)).cumsum()
gold_prices = gold_base * (1 + gold_trend * 8) * (1 + gold_volatility)
gold_weekly = pd.Series(gold_prices, index=date_range)

# 生成标普500数据
sp500_base = 100  # 70年代初标普500指数
sp500_trend = np.linspace(0, 1, len(date_range))
sp500_volatility = np.random.normal(0, 0.01, len(date_range)).cumsum()
sp500_prices = sp500_base * (1 + sp500_trend * 20) * (1 + sp500_volatility)
sp500_weekly = pd.Series(sp500_prices, index=date_range)

# 生成美债收益率数据
treasury_base = 6  # 70年代初美债收益率
treasury_cycle = np.sin(np.linspace(0, 10 * np.pi, len(date_range)))
treasury_volatility = np.random.normal(0, 0.05, len(date_range)).cumsum()
treasury_prices = treasury_base + treasury_cycle * 2 + treasury_volatility
treasury_prices = np.maximum(treasury_prices, 0.1)  # 确保收益率为正
treasury_weekly = pd.Series(treasury_prices, index=date_range)

print(f"黄金数据行数: {len(gold_weekly)}")
print(f"标普500数据行数: {len(sp500_weekly)}")
print(f"美债收益率数据行数: {len(treasury_weekly)}")

# 归一化处理
def normalize(data):
    return (data - data.min()) / (data.max() - data.min())

# 归一化数据
gold_norm = normalize(gold_weekly)
sp500_norm = normalize(sp500_weekly)
treasury_norm = normalize(treasury_weekly)

# 绘制图表
plt.figure(figsize=(12, 6))
plt.plot(date_range, gold_norm, label='黄金', linewidth=1)
plt.plot(date_range, sp500_norm, label='标普500', linewidth=1)
plt.plot(date_range, treasury_norm, label='美债收益率', linewidth=1)

plt.title('黄金、标普500、美债收益率周线走势 (归一化显示)', fontsize=14)
plt.xlabel('日期', fontsize=10)
plt.ylabel('归一化值', fontsize=10)
plt.legend(fontsize=10)
plt.grid(True, alpha=0.3)
plt.tight_layout()

# 保存图表
plt.savefig('gold_sp500_treasury.png', dpi=150, bbox_inches='tight')
print("图表已保存为 gold_sp500_treasury.png")

print("程序执行完成")
