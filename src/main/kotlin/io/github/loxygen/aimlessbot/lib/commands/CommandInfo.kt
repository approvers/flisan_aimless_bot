package io.github.loxygen.aimlessbot.lib.commands

import java.lang.IllegalArgumentException

data class CommandInfo(
   val identify: String,
   val name: String,
   val description: String
) {
   init {
      if(identify == "") throw IllegalArgumentException("†キレた†")
   }
}