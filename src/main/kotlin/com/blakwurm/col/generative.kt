package com.blakwurm.col


fun <E> colPermutations(col: List<E>) : List<List<E>> {
    val perms = Permutations(col.size)
    val toReturn = perms.toList().map { it.map { col[it] } }
    //println(toReturn)
    return toReturn
}
val <E> List<E>.permutations
        get() = colPermutations(this)

val <E> Triple<E, E, E>.permutations : List<Triple<E, E, E>>
    get() =
        this.toList().permutations.map { Triple(it.first(), it.component2(), it.component3()) }
