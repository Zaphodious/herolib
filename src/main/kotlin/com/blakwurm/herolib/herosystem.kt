package com.blakwurm.herolib

import com.sun.org.apache.xpath.internal.operations.Bool
import sun.net.www.content.text.Generic

typealias PropName = String
typealias HeroType = String
typealias HeroMap = Map<PropName, Any>
typealias HeroValidator = (HeroMap) -> ValidationResult
typealias ValidationMap = Map<PropName, HeroValidator>
typealias HeroValidationResults = Map<PropName, ValidationResult>

data class ValidationResult(val isValid: Boolean,
                            val message: String,
                            val offender: Any? = null) {
}



infix fun Boolean.because(message: String) = ValidationResult(this, message)
infix fun ValidationResult.lookingAt(thing: Any?): ValidationResult =
        this.copy(offender = thing)

fun vfn(fn: HeroValidator) = fn

data class NewHeroType(val newHero: (HeroMap) -> HeroMap,
                       val validateHero: (HeroMap) -> HeroValidationResults)

infix fun HeroMap.validateBy(t: ValidationMap): HeroValidationResults =
        t.map { it.key to it.value(this) }.toMap()

val HeroValidationResults.isFullyValid: Boolean
    get() = this.map { it.value.isValid }.contains(false).not()

fun Any?.ifReal(thing: () -> Unit): Unit =
    thing()
infix fun Map<*,*>.ifNotEmpty(thing: () -> Unit) =
        if (this.isNotEmpty()) thing()
        else Unit

object HeroValidationRegistry {
    private var _validationRegistry: Map<PropName, ValidationMap> = mapOf()
    val validations
        get() = _validationRegistry

    private var _defaultsRegistry: Map<PropName, HeroMap> = mapOf()
    val defaults
        get() = _defaultsRegistry

    fun defHeroType(herotype: String,
                    validationMap: ValidationMap = mapOf(),
                    defaultMap: HeroMap = mapOf()) {
        defaultMap ifNotEmpty {
            _defaultsRegistry += herotype to defaultMap
        }
        validationMap ifNotEmpty {
            _validationRegistry += herotype to validationMap
        }
    }

    fun validate(hero: HeroMap): HeroValidationResults {
        val type: String by hero
        val validatormap: ValidationMap = validations[type] ?: mapOf()
        return hero.validateBy(validatormap)
    }
}