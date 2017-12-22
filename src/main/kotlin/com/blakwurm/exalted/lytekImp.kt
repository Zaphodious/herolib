package com.blakwurm.exalted

import com.blakwurm.col.permutations

/**
 * @return List with physical, social, and mental totals in that order
 */
fun attributeTotals(attrmap: Map<String, Int>): List<Int> {
    fun g(keybois: List<String>) = attrmap.filter { keybois.toSet().contains(it.key) }.values.reduce(Int::plus).dec()
    return listOf(g(Attributes.physical), g(Attributes.social), g(Attributes.mental))
}

fun attributeMinErrorCalc(score: Int, target: Int, categoryname: String) =
        ((target - score).floor(0) == 0) to "Spend ${target - score} more points on your $categoryname category"

data class AttributeRankResult(val primary: String, val secondary: String, val tertiary: String, val bonus: Int)

internal fun cBO(score: Int, points: Int, bonusMult: Int) =
        ((score - points) * bonusMult).floor(0)

internal fun calcBonusBy(a: Int, b: Int, c: Int) =
        cBO(a, 8, 4) + cBO(b, 6, 4) + cBO(c, 4, 3)

internal fun sortAttrGroupsByBonus(phys: Int, soci: Int, ment: Int): AttributeRankResult
        = Triple(phys to Attributes.PHYSICAL, soci to Attributes.SOCIAL, ment to Attributes.MENTAL)
        .permutations
        .map {
            AttributeRankResult(
                    primary = it.first.second, secondary = it.second.second, tertiary = it.third.second,
                    bonus = calcBonusBy(it.first.first, it.second.first, it.third.first))
        }
        .sortedBy { it.bonus }
        .first()

fun <E, T> E.transform(fn: (E) -> T) = fn.invoke(this)

fun combineAttributesByCategory(attrmap: Map<String, Int>): Triple<Int, Int, Int> =
        listOf(attrmap.filter { Attributes.physical.toSet().contains(it.key) },
                attrmap.filter {Attributes.social.toSet().contains(it.key)},
                attrmap.filter { Attributes.mental.toSet().contains(it.key)})
                .map{it.values.reduce(Int::plus)}
                .transform { Triple(it[0], it[1], it[2])  }


fun determineAttributeRanks(attrmap: Map<String, Int>): AttributeRankResult {
    val (physical, social, mental) =
            listOf(attrmap.filter { Attributes.physical.toSet().contains(it.key) },
                    attrmap.filter {Attributes.social.toSet().contains(it.key)},
                    attrmap.filter { Attributes.mental.toSet().contains(it.key)})
                    .map{it.values.reduce(Int::plus)}
    return sortAttrGroupsByBonus(physical, social, mental)
}