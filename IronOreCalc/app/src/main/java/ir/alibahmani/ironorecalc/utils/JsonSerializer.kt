package ir.alibahmani.ironorecalc.utils

import ir.alibahmani.ironorecalc.domain.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Minimal JSON serializer/deserializer for CalculationInputs and CalculationResult.
 * Uses only the built-in org.json library — no external dependency needed.
 */
object JsonSerializer {

    // ── Inputs ──────────────────────────────────────────────────────────────

    fun encodeInputs(i: CalculationInputs): String = JSONObject().apply {
        put("feedRate", i.feedRate); put("feedGrade", i.feedGrade)
        put("midGrade", i.midGrade); put("tailGrade1", i.tailGrade1)
        put("h1", i.h1); put("d80", i.d80); put("moisture", i.moisture)
        put("finalConcGrade", i.finalConcGrade); put("finalTailGrade", i.finalTailGrade)
        put("h2", i.h2)
        i.feO?.let { put("feO", it) }; i.fe2o3?.let { put("fe2o3", it) }
        i.liberation?.let { put("liberation", it) }
        i.rhoVal?.let { put("rhoVal", it) }; i.rhoGang?.let { put("rhoGang", it) }
        i.rhoFluid?.let { put("rhoFluid", it) }
        i.chiVal?.let { put("chiVal", it) }; i.chiGang?.let { put("chiGang", it) }
        i.drumDiameter1?.let { put("drumDiameter1", it) }
        i.beltWidth1?.let { put("beltWidth1", it) }
        i.bedThickness1?.let { put("bedThickness1", it) }
        i.drumDiameter2?.let { put("drumDiameter2", it) }
        i.beltWidth2?.let { put("beltWidth2", it) }
        i.bedThickness2?.let { put("bedThickness2", it) }
        i.bulkDensity?.let { put("bulkDensity", it) }
    }.toString()

    fun decodeInputs(json: String): CalculationInputs {
        val o = JSONObject(json)
        return CalculationInputs(
            feedRate = o.getDouble("feedRate"),
            feedGrade = o.getDouble("feedGrade"),
            midGrade = o.getDouble("midGrade"),
            tailGrade1 = o.getDouble("tailGrade1"),
            h1 = o.getDouble("h1"),
            d80 = o.getDouble("d80"),
            moisture = o.getDouble("moisture"),
            finalConcGrade = o.getDouble("finalConcGrade"),
            finalTailGrade = o.getDouble("finalTailGrade"),
            h2 = o.getDouble("h2"),
            feO = o.optDouble("feO").takeIf { !it.isNaN() },
            fe2o3 = o.optDouble("fe2o3").takeIf { !it.isNaN() },
            liberation = o.optDouble("liberation").takeIf { !it.isNaN() },
            rhoVal = o.optDouble("rhoVal").takeIf { !it.isNaN() },
            rhoGang = o.optDouble("rhoGang").takeIf { !it.isNaN() },
            rhoFluid = o.optDouble("rhoFluid").takeIf { !it.isNaN() },
            chiVal = o.optDouble("chiVal").takeIf { !it.isNaN() },
            chiGang = o.optDouble("chiGang").takeIf { !it.isNaN() },
            drumDiameter1 = o.optDouble("drumDiameter1").takeIf { !it.isNaN() },
            beltWidth1 = o.optDouble("beltWidth1").takeIf { !it.isNaN() },
            bedThickness1 = o.optDouble("bedThickness1").takeIf { !it.isNaN() },
            drumDiameter2 = o.optDouble("drumDiameter2").takeIf { !it.isNaN() },
            beltWidth2 = o.optDouble("beltWidth2").takeIf { !it.isNaN() },
            bedThickness2 = o.optDouble("bedThickness2").takeIf { !it.isNaN() },
            bulkDensity = o.optDouble("bulkDensity").takeIf { !it.isNaN() }
        )
    }

    // ── Result ───────────────────────────────────────────────────────────────

    fun encodeResult(r: CalculationResult): String = JSONObject().apply {
        put("mb", encodeMassBalance(r.massBalance))
        put("ib", encodeIronBalance(r.ironBalance))
        put("mineral", encodeMineral(r.mineral))
        put("enrichmentRatio", r.enrichmentRatio)
        put("yieldPct", r.yieldPct)
        r.concentrationCriterion?.let { put("cc", it) }
        r.msr?.let { put("msr", it) }
        r.relativeMagneticDrivingIndex?.let { put("rmdi", it) }
        put("newton", r.newton)
        put("gangueRecovery", r.gangueRecoveryToConc)
        put("selectivity", r.selectivityIndex)
        put("mc", encodeMultiCriteria(r.multiCriteria))
        put("warnings", JSONArray(r.warnings))
        put("suggestions", JSONArray(r.suggestions))
        r.drumSpeed1?.let { put("ds1", encodeDrumSpeed(it)) }
        r.drumSpeed2?.let { put("ds2", encodeDrumSpeed(it)) }
        r.beltSpeed1?.let { put("bs1", it) }
        r.beltSpeed2?.let { put("bs2", it) }
        put("fa1", encodeFieldAdequacy(r.fieldAdequacy1))
        put("fa2", encodeFieldAdequacy(r.fieldAdequacy2))
    }.toString()

    fun decodeResult(json: String): CalculationResult {
        val o = JSONObject(json)
        return CalculationResult(
            massBalance = decodeMassBalance(o.getJSONObject("mb")),
            ironBalance = decodeIronBalance(o.getJSONObject("ib")),
            mineral = decodeMineral(o.getJSONObject("mineral")),
            enrichmentRatio = o.getDouble("enrichmentRatio"),
            yieldPct = o.getDouble("yieldPct"),
            concentrationCriterion = o.optDouble("cc").takeIf { !it.isNaN() },
            msr = o.optDouble("msr").takeIf { !it.isNaN() },
            relativeMagneticDrivingIndex = o.optDouble("rmdi").takeIf { !it.isNaN() },
            newton = o.getDouble("newton"),
            gangueRecoveryToConc = o.getDouble("gangueRecovery"),
            selectivityIndex = o.getDouble("selectivity"),
            multiCriteria = decodeMultiCriteria(o.getJSONObject("mc")),
            warnings = decodeStringList(o.getJSONArray("warnings")),
            suggestions = decodeStringList(o.getJSONArray("suggestions")),
            drumSpeed1 = o.optJSONObject("ds1")?.let { decodeDrumSpeed(it) },
            drumSpeed2 = o.optJSONObject("ds2")?.let { decodeDrumSpeed(it) },
            beltSpeed1 = o.optDouble("bs1").takeIf { !it.isNaN() },
            beltSpeed2 = o.optDouble("bs2").takeIf { !it.isNaN() },
            fieldAdequacy1 = decodeFieldAdequacy(o.getJSONObject("fa1")),
            fieldAdequacy2 = decodeFieldAdequacy(o.getJSONObject("fa2"))
        )
    }

    // helpers
    private fun encodeMassBalance(m: MassBalanceResult) = JSONObject().apply {
        put("f2", m.f2); put("t1", m.t1); put("r1", m.r1)
        put("f3", m.f3); put("t2", m.t2); put("r2", m.r2); put("rTotal", m.rTotal)
    }
    private fun decodeMassBalance(o: JSONObject) = MassBalanceResult(
        o.getDouble("f2"), o.getDouble("t1"), o.getDouble("r1"),
        o.getDouble("f3"), o.getDouble("t2"), o.getDouble("r2"), o.getDouble("rTotal")
    )
    private fun encodeIronBalance(i: IronBalanceResult) = JSONObject().apply {
        put("feIn", i.feIn); put("feOut", i.feOut); put("feLoss", i.feLoss); put("errorAbs", i.errorAbs)
    }
    private fun decodeIronBalance(o: JSONObject) = IronBalanceResult(
        o.getDouble("feIn"), o.getDouble("feOut"), o.getDouble("feLoss"), o.getDouble("errorAbs")
    )
    private fun encodeMineral(m: MineralClassification) = JSONObject().apply {
        put("label", m.label); put("reliable", m.reliable)
    }
    private fun decodeMineral(o: JSONObject) = MineralClassification(o.getString("label"), o.getBoolean("reliable"))
    private fun encodeDrumSpeed(d: DrumSpeedResult) = JSONObject().apply {
        put("crit", d.criticalSpeedRpm); put("minRpm", d.recommendedMinRpm); put("maxRpm", d.recommendedMaxRpm)
    }
    private fun decodeDrumSpeed(o: JSONObject) = DrumSpeedResult(o.getDouble("crit"), o.getDouble("minRpm"), o.getDouble("maxRpm"))
    private fun encodeFieldAdequacy(f: FieldAdequacyResult) = JSONObject().apply {
        put("status", f.status); put("min", f.recommendedMinGauss); put("max", f.recommendedMaxGauss); put("basis", f.basis)
    }
    private fun decodeFieldAdequacy(o: JSONObject) = FieldAdequacyResult(
        o.getString("status"), o.getDouble("min"), o.getDouble("max"), o.getString("basis")
    )
    private fun encodeMultiCriteria(mc: MultiCriteriaResult) = JSONObject().apply {
        put("overall", mc.overallScore); put("limiting", mc.limitingFactor)
        put("breakdown", JSONArray(mc.breakdown.map { c ->
            JSONObject().apply { put("name", c.name); put("score", c.score); put("weight", c.weight) }
        }))
    }
    private fun decodeMultiCriteria(o: JSONObject): MultiCriteriaResult {
        val arr = o.getJSONArray("breakdown")
        val breakdown = (0 until arr.length()).map {
            val c = arr.getJSONObject(it)
            CriterionScore(c.getString("name"), c.getDouble("score"), c.getInt("weight"))
        }
        return MultiCriteriaResult(o.getDouble("overall"), o.getString("limiting"), breakdown)
    }
    private fun decodeStringList(arr: JSONArray): List<String> =
        (0 until arr.length()).map { arr.getString(it) }
}
