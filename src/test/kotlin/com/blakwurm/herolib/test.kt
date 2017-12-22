package com.blakwurm.herolib

import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

val testHero: HeroMap =
        mapOf(
                "name" to "Agamemnon",
                "system" to "DND5",
                "type" to "Adventurer",
                "charClass" to "Cleric",
                "race" to "Elf",
                "attributes" to mapOf(
                        "str" to 13,
                        "dex" to 8,
                        "con" to 9,
                        "wis" to 18,
                        "int" to 16,
                        "car" to 14
                ),
                "selectedAbilities" to setOf("Animal Handling", "Medicine", "History")
        )

val testEDNHero: HeroMap =
        """
            {"name" "Agamemnon",
            "system" "DND5",
            "type" "Adventurer",
            "charClass" "Cleric",
            "race" "Elf",
            "attributes" {"str" 13, "dex" 08, "con" 9, "wis" 18, "int" 16, "car" 14},
            "selectedAbilities" #{"Animal Handling" "Medicine" "History"}}
        """.trimIndent().inflate() as HeroMap

fun mvfn(fn: (TestClass) -> ValidationResult) = vfn { fn(TestClass(it)) }

val charclasses = setOf("Bard", "Cleric", "Wizard", "Monk")
val charraces = setOf("Elf", "Human", "Halfling")



val testValids: ValidationMap =
        mapOf(
                "name" to mvfn {(it.name.isNotEmpty()) because "Name shouldn't be empty." lookingAt it.name },
                "system" to mvfn {(it.system == "DND5") because "System should be DND5" lookingAt it.system },
                "class" to mvfn {(charclasses.contains(it.charClass)) because "type must be one of $charclasses" lookingAt it.charClass},
                "race" to mvfn {(charraces.contains(it.race)) because "race must be one of $charraces" lookingAt it.race},
                "attributes" to mvfn {
                    it.attributes.map { it.value in 1..18 }.contains(false).not() because "Attributes should be between 1 and 18" lookingAt it.attributes }

        )

data class TestClass(val heroData: HeroMap) {
    val name: String by heroData
    val system: String by heroData
    val charClass: String by heroData
    val race: String by heroData
    val attributes: Map<String, Int> by heroData
}

data class TestValidators(val heroValidators: ValidationMap) {
    val name by heroValidators
    val system by heroValidators
    val charClass by heroValidators
    val race by heroValidators
    val attributes by heroValidators
}

class HeroValidation : StringSpec() {
    init {
        val v = TestValidators(testValids)
        val h = TestClass(testHero)

        HeroValidationRegistry.defHeroType(
                herotype = "Adventurer",
                validationMap = testValids,
                defaultMap = testHero
        )



        "(internal) Data wrappers should access map data correctly" {
           h.system shouldBe "DND5"
            v.name(testHero).isValid shouldBe true
        }

        "HeroData should validate using the .validateBy() function provided a validation map" {
            testHero.validateBy(testValids).isFullyValid shouldBe true
        }

        "HeroData should validate using the HeroValidationRegistry" {
            HeroValidationRegistry.validate(testHero).isFullyValid shouldBe true
        }

        "HeroData should inflate and deflate with no practical difference" {
            val inflatedHero = testHero.deflate()?.inflate() as HeroMap? ?: mapOf()
            val inflatedHC = TestClass(inflatedHero)
            inflatedHC.name shouldBe h.name
            inflatedHC.system shouldBe h.system
            inflatedHC.attributes["strength"] shouldBe h.attributes["strength"]
            HeroValidationRegistry.validate(inflatedHero).isFullyValid shouldBe true
            testEDNHero shouldBe inflatedHero


        }
    }
}
