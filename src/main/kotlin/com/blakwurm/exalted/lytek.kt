package com.blakwurm.exalted

import com.blakwurm.col.permutations
import com.blakwurm.herolib.*

val SOLAR_NASCENT_TYPE = "nascent_solar"


data class ExaltedWrapper(val heroMap: HeroMap) {
    val name: String by heroMap
    val attributes: Map<String, Int> by heroMap
}

data class SolarWrapper(val heroMap: HeroMap) {
    val name: String by heroMap
    val caste: String by heroMap
    val attributes: Map<String, Int> by heroMap
}

fun evfn(validator: (ExaltedWrapper) -> ValidationResult): HeroValidator =
        vfn { validator(ExaltedWrapper(it)) }

fun svfn(validator: (SolarWrapper) -> ValidationResult): HeroValidator =
        vfn { validator(SolarWrapper(it)) }

fun sbonus(calculator: (SolarWrapper) -> Int): (HeroMap) -> Int =
        { it: HeroMap -> calculator(SolarWrapper(it)) }

object FieldNames {
    val NAME = "name"
    val TYPE = "type"
    val CASTE = "caste"
    val ATTRIBUTES = "attributes"
}

object SolarCastes {
    val DAWN = "dawn"
    val TWILIGHT = "twilight"
    val NIGHT = "night"
    val ECLIPSE = "eclipse"
    val ZENITH = "zenith"
    val all = setOf(DAWN, TWILIGHT, NIGHT, ECLIPSE, ZENITH)
}

object Attributes {
    val STRENGTH = "strength"
    val DEXTERITY = "dexterity"
    val STAMINA = "stamina"
    val CHARISMA = "charisma"
    val MANIPULATION = "manipulation"
    val APPEARANCE = "appearance"
    val PERCEPTION = "perception"
    val INTELLIGENCE = "intelligence"
    val WITS = "wits"
    val all = listOf(
            STRENGTH, DEXTERITY, STAMINA,
            CHARISMA, MANIPULATION, APPEARANCE,
            PERCEPTION, INTELLIGENCE, WITS
    )
    val physical = listOf(STRENGTH, DEXTERITY, STAMINA)
    val social = listOf(CHARISMA, MANIPULATION, APPEARANCE)
    val mental = listOf(PERCEPTION, INTELLIGENCE, WITS)
    val PHYSICAL = "physical"
    val SOCIAL = "social"
    val MENTAL = "mental"
    val categories = listOf(PHYSICAL, SOCIAL, MENTAL)
}

val validateName = evfn { it.name.isEmpty().not() because "Name cannot be empty" lookingAt it.name }

val validateSolarCaste = svfn { SolarCastes.all.contains(it.caste) because "Solar castes must be one of " + SolarCastes.all lookingAt it.caste }

val validateNascientSolarAttributes = svfn {

    /*val attrs = it.attributes

    val totalScore = attrs.values.reduce(Int::plus)
    val totals = attributeTotals(attrs)
    val (physicalScore, socialScore, mentalScore) = totals
    val (primaryScore, secondaryScore, tertiaryScore) = totals.sorted().reversed()
    //println("physical is $physicalScore social is $socialScore mental is $mentalScore. In order, $primaryScore, $secondaryScore, $tertiaryScore")

    val errors = listOf(
            Triple(primaryScore, 8, "primary"),
            Triple(secondaryScore, 6, "secondary"),
            Triple(tertiaryScore, 4, "tertiary")
    ).map { attributeMinErrorCalc(it.first, it.second, it.third) }
            .filter { it.first.not() }

    if (errors.isEmpty()) true because "Attribute points are fully spent" lookingAt it.attributes
    else false because errors.reduce { thing, it -> thing.copy(second = thing.second + " " + it.second) }.second lookingAt it.attributes*/

    val (primary, secondary, tertiary, bonusamt) = determineAttributeRanks(it.attributes)




    true because "" lookingAt 1
}

fun <T : Number> T.floor(other: T) = if (this.toDouble() > other.toDouble()) this else other
val calcBonusAttributes = sbonus {
    val A = Attributes
    fun filRed(listyboi: List<String>) =
            it.attributes.filter { listyboi.toSet().contains(it.key) }.values.reduce(Int::plus)
    val (physical, social, mental) = listOf(A.physical, A.social, A.mental).map(::filRed)

    fun cBO(score: Int, points: Int, bonusMult: Int): Int {
        val modifiedScore = score - points - 3
        val bonuscalced = modifiedScore * bonusMult
        val toReturn = ((score - points - 3).floor(0) * bonusMult).floor(0)

        println("bonus for score $score is $bonuscalced, from modified $modifiedScore")
        return bonusMult.floor(0)
    }

    fun calcBonusBy(a: Triple<Int, Int, Int>) =
            cBO(a.first, 8, 4) + cBO(a.second, 6, 4) + cBO(a.third, 4, 3)

    val possibleBonusAmounts =
            listOf(Triple(social, mental, physical), Triple(mental, physical, social), Triple(physical, social, mental))
                    .map(::calcBonusBy)

    println("potential bonus amounts " + possibleBonusAmounts)
    println("attribute totals are " + listOf(physical, social, mental))
    println("attributes are " + it.attributes)

    it.attributes.values
            .reduce(Int::plus)
            .minus(3 /*Three free dots*/)
            .minus(18 /*total attribute points available*/)
            .floor(0)

    possibleBonusAmounts.sorted().first()
}


fun setupExaltedRegistry() {
    HeroValidationRegistry.defHeroType(
            herotype = SOLAR_NASCENT_TYPE,
            validationMap = mapOf(
                    FieldNames.NAME to validateName,
                    FieldNames.CASTE to validateSolarCaste,
                    FieldNames.ATTRIBUTES to validateNascientSolarAttributes
            ),
            defaultMap = mapOf(
                    FieldNames.NAME to "Daesh",
                    FieldNames.TYPE to SOLAR_NASCENT_TYPE,
                    FieldNames.CASTE to SolarCastes.DAWN,
                    FieldNames.ATTRIBUTES to mapOf(
                            Attributes.STRENGTH to 3,
                            Attributes.DEXTERITY to 4,
                            Attributes.STAMINA to 2,
                            Attributes.APPEARANCE to 1,
                            Attributes.MANIPULATION to 2,
                            Attributes.CHARISMA to 2,
                            Attributes.INTELLIGENCE to 5,
                            Attributes.PERCEPTION to 4,
                            Attributes.WITS to 2
                    )

            )
    )
}