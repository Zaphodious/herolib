package com.blakwurm.col

import io.kotlintest.properties.*
import io.kotlintest.specs.StringSpec
import kotlin.math.absoluteValue
import kotlin.math.min

class BoundedListGen(val maxsize: Int = 6, val minsize: Int = 2) : Gen<List<Int>> {
    override fun generate(): List<Int> {
        val vally = (0).rangeTo(Gen.int().generate().absoluteValue.rem(maxsize - minsize).plus(minsize)).map { Gen.int().generate() }
        return vally
    }



}

class PermutationsTest : StringSpec() {
    init {
        "List.permutations returns a full list of all unique permutations" {
            forAll (BoundedListGen()) { listo: List<Int> ->
                val permutes = listo.permutations
                //println(permutes)
                permutes.size == permutes.toSet().size &&
                        permutes.map { it.reduce(Int::plus) }.toSet().size == 1
            }
        }

        "Triple.permutations returns a list of all triple permutations" {
            forAll (object : Gen<Triple<Int, Int, Int>> {
                override fun generate(): Triple<Int, Int, Int> =
                        Triple(Gen.int().generate(),
                                Gen.int().generate(),
                                Gen.int().generate())
            }) { trip: Triple<Int, Int, Int> ->
                trip.permutations.size == 6
            }
        }
    }
}