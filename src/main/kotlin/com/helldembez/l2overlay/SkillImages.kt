package com.helldembez.l2overlay

import javafx.scene.image.Image
import kotlin.jvm.javaClass

object Images {
    val MANA_SUSPENSION = Image(javaClass.getResource("/mana-sus.png")!!.toExternalForm())
    val SUBLIME = Image(javaClass.getResource("/sublime.png")!!.toExternalForm())
    val DOD = Image(javaClass.getResource("/dod.png")!!.toExternalForm())
    val SOF = Image(javaClass.getResource("/sof.png")!!.toExternalForm())
    val FOI = Image(javaClass.getResource("/foi.png")!!.toExternalForm())
}
