package com.blakwurm.exalted

import com.blakwurm.herolib.HeroMap
import com.blakwurm.herolib.HeroValidationRegistry
import com.blakwurm.herolib.isFullyValid
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec
import kotlin.math.absoluteValue


val defaultSolar: HeroMap = HeroValidationRegistry.defaults[SOLAR_NASCENT_TYPE] ?: mapOf()

class LytekTest : StringSpec() {
    init {

        setupExaltedRegistry()

        "The default Nascent Solar should validate" {
            determineAttributeRanks(SolarWrapper(defaultSolar).attributes)
            val validation = HeroValidationRegistry.validate(defaultSolar)
//            println(validation)
            validation.isFullyValid shouldBe true
        }


    }
}

class BoundedIntGen(val floor:Int, val max:Int): Gen<Int> {
    override fun generate(): Int =
        Gen.int().generate().absoluteValue.rem(max+1).floor(floor)


}

class NascentSolarValidationTest : StringSpec() {
    init{
        "cBO() correctly calculates the bonus amount given bounded inputs" {
            forAll (BoundedIntGen(1, 42),
                    BoundedIntGen(1, 5),
                    BoundedIntGen(4, 9))
            { _bonus: Int,
                mult: Int,
                points: Int->
                val bonus = _bonus - (_bonus % mult)
                val score = (bonus / mult) + points
                val calcBonus = cBO(score, points, mult)


                calcBonus == bonus
            }
        }

        "calcBonusBy() determines correct bonus according to Solar creation rules" {
            val t = table(Headers4("Primary", "Secondary", "Tertiary", "Bonus"),
                    row(8, 6, 4, 0),
                    row(10, 6, 4, 8),
                    row(6, 6, 6, 6),
                    row(9, 7, 4, 8))

            forAll(t) { primary, secondary, tertiary, targetBonus ->
                calcBonusBy(primary, secondary, tertiary) shouldBe targetBonus
            }
        }

        "sortAttrGroupsByBonus() correctly determines the ranking of attribute categories" {
            val t = table(Headers4(Attributes.PHYSICAL, Attributes.SOCIAL, Attributes.MENTAL, "Expected"),
                    row(8, 6, 4, AttributeRankResult(Attributes.PHYSICAL, Attributes.SOCIAL, Attributes.MENTAL,0)),
                    row(6, 10, 4, AttributeRankResult( Attributes.SOCIAL, Attributes.PHYSICAL, Attributes.MENTAL,2*4)),
                    row(10, 10, 4, AttributeRankResult(Attributes.PHYSICAL, Attributes.SOCIAL, Attributes.MENTAL,2*4+4*4)),
                    row(10, 10, 10, AttributeRankResult(Attributes.PHYSICAL, Attributes.SOCIAL, Attributes.MENTAL,2*4+4*4+6*3)),
                    row(7, 8, 7, AttributeRankResult(Attributes.SOCIAL, Attributes.PHYSICAL, Attributes.MENTAL,4+3*3)),
                    row(7, 8, 7, AttributeRankResult(Attributes.SOCIAL, Attributes.PHYSICAL, Attributes.MENTAL,4+3*3))

            )

            forAll(t) {physical, social, mental, expected ->
                val result = sortAttrGroupsByBonus(physical, social, mental)
                println(result)
                result shouldBe expected
            }
        }

        "getGroupScoreByCategory() gets the correct attribute "
    }
}


