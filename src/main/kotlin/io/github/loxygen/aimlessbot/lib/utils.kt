package io.github.loxygen.aimlessbot.lib

infix fun <E> List<E>.contentEquals(other: List<E>): Boolean {

   if (this.size != other.size) return false
   return this.find { it?.equals(other) ?: false } == null

}