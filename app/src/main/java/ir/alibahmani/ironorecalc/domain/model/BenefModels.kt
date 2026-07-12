package ir.alibahmani.ironorecalc.domain.model

/**
 * یک ردیف نمونه پرعیارسازی (هر سرویس درام).
 *
 * فرمول‌های محاسباتی (مطابق اکسل، بدون تغییر):
 *  - بازیابی محاسباتی    = (وزن محصول × عیار محصول) / (وزن خوراک × عیار خوراک)
 *  - بازیابی وزنی        = وزن محصول / وزن خوراک
 *  - محتوی فلز محصول     = وزن محصول × عیار محصول
 */
data class BenefSample(
    val id: Long = 0,
    val date: String,                // مثال: 1404/06/04
    val rowNum: Int,                 // ردیف
    val operatingHours: Double,      // مجموع ساعت کارکرد (D)
    val sampleCode: String,          // کد نمونه: R1 / R2 / D1 / D2
    val sampleType: String,          // نوع نمونه: "میلیمتر10-0" | "میلیمتر30-10"
    val drumNumbers: String,         // شماره درام: "1و2" | "1و2و3و4" | ...
    val drumSpeed: Int,              // سرعت درام (دور/دقیقه)
    val bladeAngle: String,          // زاویه تیغه
    val fieldStrength: String,       // شدت میدان (گوس): "1300" | "900" | "1300و900"
    val truckCount: Int,             // تعداد کامیون
    // ─── خوراک (F) ───────────────────────────────────────────────
    val feedWeight: Double,          // وزن (تن)
    val feedFe: Double,              // Fe%
    val feedFeo: Double = 0.0,       // FeO%
    // ─── محصول / کنسانتره (C) ────────────────────────────────────
    val concWeight: Double,          // وزن (تن)
    val concFe: Double,              // Fe%
    val concFeo: Double = 0.0,       // FeO%
    // ─── باطله (T) ───────────────────────────────────────────────
    val tailWeight: Double,          // وزن (تن)
    val tailFe: Double,              // Fe%
    val tailFeo: Double = 0.0,       // FeO%
    val notes: String = ""
) {
    /**
     * بازیابی محاسباتی = (وزن محصول × عیار محصول) / (وزن خوراک × عیار خوراک)
     * فرمول ستون V در اکسل
     */
    val calcRecovery: Double
        get() {
            val denom = feedWeight * feedFe
            return if (denom > 0) (concWeight * concFe) / denom else 0.0
        }

    /**
     * بازیابی وزنی = وزن محصول / وزن خوراک
     * فرمول ستون W در اکسل
     */
    val weightRecovery: Double
        get() = if (feedWeight > 0) concWeight / feedWeight else 0.0

    /**
     * محتوی فلز محصول = وزن محصول × عیار محصول
     * فرمول ستون X در اکسل
     */
    val metalContent: Double
        get() = concWeight * concFe
}

/** گزارش کلی روزانه یک روز پرعیارسازی. */
data class BenefDailyReport(
    val id: Long = 0,
    val date: String,

    // ─── وضعیت سپراتورها ─────────────────────────────────────────
    val lims1: Boolean = true,
    val lims2: Boolean = true,
    val lims3: Boolean = true,
    val lims4: Boolean = true,

    // ─── نوع بار فید شده ─────────────────────────────────────────
    val feedType: String = "",        // "R2" | "D2" | ...

    // ─── زمان شیفت ────────────────────────────────────────────────
    val shiftDowntimeHr: Double = 0.0,   // توقفات شیفت
    val shiftRuntimeHr: Double = 0.0,    // کارکرد شیفت

    // ─── تولید و توزین ────────────────────────────────────────────
    val dailyProductionTon: Double = 0.0,       // تولید روزانه
    val dailyWeighingTon: Double = 0.0,         // توزین روزانه
    val cumulativeProdTon: Double = 0.0,        // تولید از ابتدا ماه

    // ─── بار توزین شده ────────────────────────────────────────────
    val weighedCargoType: String = "O10A",      // O10A | O30A
    val weighedCargoFe: Double = 0.0,           // عیار بار توزین شده Fe%
    val weighedCargoWeightTon: Double = 0.0,    // وزن بار توزین شده (تن)
    val weighedCargoTruckCount: Int = 0,        // تعداد کامیون توزین شده
    val transferredWeightTon: Double = 0.0,     // وزن بار منتقل شده (تن)
    val transferredFeo: Double = 0.0,           // FeO% بار منتقل شده

    // ─── پرسنل ────────────────────────────────────────────────────
    val personnelOffice: Int = 0,
    val personnelSafetyExpert: Int = 0,
    val personnelExpert: Int = 0,
    val personnelTechnicalElec: Int = 0,
    val personnelTechnicalLabor: Int = 0,
    val personnelMechanics: Int = 0,
    val personnelServiceman: Int = 0,
    val personnelLoaderDriver: Int = 0,
    val personnelTruckDriver: Int = 0,
    val personnelKaraDriver: Int = 0,
    val personnelWarehouse: Int = 0,
    val personnelSupply: Int = 0,
    val personnelBenefHead: Int = 0,
    val personnelCrusherWorker: Int = 0,
    val personnelTechnicalWorker: Int = 0,

    // ─── مصرف گازوییل ─────────────────────────────────────────────
    val dieselConsumption: Double = 0.0,

    // ─── توضیحات ──────────────────────────────────────────────────
    val notes: String = ""
) {
    /** جمع پرسنل دفتر */
    val totalOfficePersonnel: Int get() = personnelOffice + personnelSafetyExpert + personnelExpert

    /** جمع خدمات فنی */
    val totalTechnical: Int get() = personnelTechnicalElec + personnelTechnicalLabor

    /** جمع ماشین‌آلات */
    val totalMachinery: Int get() = personnelMechanics + personnelServiceman + personnelLoaderDriver +
            personnelTruckDriver + personnelKaraDriver

    /** جمع پرسنل واحد پرعیارسازی */
    val totalBenefUnit: Int get() = personnelBenefHead + personnelCrusherWorker + personnelTechnicalWorker
}

/** آمار تجمعی عیار و بازیابی (ستون‌های Y و Z اکسل). */
data class BenefCumulativeStats(
    val date: String,
    val cumulativeAvgFe: Double,           // عیار میانگین تا تاریخ روز (Y)
    val cumulativeAvgWeightRecovery: Double, // بازیابی وزنی میانگین (Z)
    val monthToDateProdTon: Double         // تولید از ابتدای ماه (AA)
)
