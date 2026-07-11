# محاسبه‌گر سنگ آهن صنعتی
## Iron Ore Industrial Calculator

توسعه‌یافته توسط **علی بهمنی** | ۰۹۹۱۵۴۲۰۵۵۸

---

## راهنمای Build در Android Studio

### پیش‌نیازها
- Android Studio Hedgehog (2023.1.1) یا جدیدتر
- JDK 17
- Android SDK (API 26+)
- اتصال اینترنت (فقط برای دانلود وابستگی‌ها در اولین Build)

### مراحل Build

1. **باز کردن پروژه:**
   - Android Studio را باز کنید
   - `File → Open` را انتخاب کنید
   - پوشه `IronOreCalc` را انتخاب کنید
   - منتظر بمانید تا Gradle Sync کامل شود

2. **Build APK:**
   - `Build → Build Bundle(s) / APK(s) → Build APK(s)`
   - APK در مسیر زیر ساخته می‌شود:
     `app/build/outputs/apk/debug/app-debug.apk`

3. **نصب روی گوشی:**
   - APK را به گوشی انتقال دهید
   - در تنظیمات گوشی، "نصب از منابع ناشناس" را فعال کنید
   - فایل APK را باز کنید و نصب کنید

### Build Release (امضاشده)

```bash
# ساخت Signed APK
Build → Generate Signed Bundle / APK → APK
```

---

## معماری

```
Clean Architecture + MVVM

app/src/main/java/ir/alibahmani/ironorecalc/
├── domain/
│   ├── model/          # Data classes (CalculationInputs, CalculationResult, ...)
│   └── usecase/        # Business logic (RunCalculationUseCase, SaveProjectUseCase, ...)
│       └── CalculationEngine.kt  # موتور محاسبات (port از lib/calculations.ts)
├── data/
│   ├── local/          # Room Database (Entity, DAO, Database)
│   └── repository/     # Repository implementation
├── di/                 # Hilt Dependency Injection modules
└── ui/
    ├── theme/          # Material 3 theming (Colors, Typography)
    ├── navigation/     # Compose Navigation graph
    ├── components/     # Shared UI components
    ├── screens/        # HomeScreen, CalculationScreen, HistoryScreen, ...
    └── viewmodel/      # ViewModels (StateFlow + Coroutines)
```

---

## ویژگی‌های کلیدی

- ✅ **موازنه جرم و آهن** — فرمول‌های دقیق دو مرحله‌ای
- ✅ **شاخص‌های پیشرفته** — Newton, Gaudin-Schulz, MSR, Concentration Criterion
- ✅ **تحلیل چندمعیاره** — امتیازبندی با وزن‌های کارشناسی
- ✅ **هشدار خودکار** — بر اساس آستانه‌های صنعتی
- ✅ **محاسبات تجهیزات** — سرعت درام، سرعت نوار، کفایت میدان
- ✅ **طبقه‌بندی کانی‌شناسی** — مگنتیت/هماتیت (تقریبی)
- ✅ **ذخیره‌سازی** — Room Database آفلاین
- ✅ **صدور PDF** — گزارش کامل با iText
- ✅ **صدور CSV** — برای Excel
- ✅ **دیاگرام فرآیند** — انیمیشن Compose Canvas
- ✅ **Dark/Light Theme** — Material Design 3
- ✅ **RTL کامل** — فارسی
- ✅ **آفلاین** — بدون اینترنت، بدون تبلیغات

---

## Stack فنی

| لایه | تکنولوژی |
|------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| ViewModel | Hilt + StateFlow + Coroutines |
| Database | Room |
| DI | Hilt |
| PDF | iText 5 |
| Minimum SDK | API 26 (Android 8.0) |

---

© ۱۴۰۴ علی بهمنی
