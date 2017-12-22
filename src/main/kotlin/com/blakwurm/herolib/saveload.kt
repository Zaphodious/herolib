package com.blakwurm.herolib


import us.bpsm.edn.parser.Parsers
import us.bpsm.edn.parser.Parsers.defaultConfiguration
import us.bpsm.edn.printer.Printer
import us.bpsm.edn.printer.Printers


object ednStation {
    var parser = Parsers.newParser(defaultConfiguration())
}



fun Any.deflate(): String? = Printers.printString(this)
fun String.inflate(): Any? = ednStation.parser.nextValue(Parsers.newParseable(this))
