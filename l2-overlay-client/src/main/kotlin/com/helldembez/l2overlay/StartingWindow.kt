package com.helldembez.l2overlay

import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

object BuildInfo {
    val version: String = BuildInfo::class.java.`package`?.implementationVersion ?: "dev"
}

class StartingWindow : Application(), NativeKeyListener {
    override fun start(stage: Stage) {
        val root = VBox(10.0)
        val ccBtn = Button("Cc").apply {
            style = "-fx-font-size: 18; -fx-padding: 12 20; -fx-background-radius: 8;"
            minWidth = 140.0
            minHeight = 48.0
            setOnAction {
                val newStage = Stage()
                CcWindow().start(newStage)
                stage.close()
            }
        }
        val cpBtn = Button("Cp").apply {
            style = "-fx-font-size: 18; -fx-padding: 12 20; -fx-background-radius: 8;"
            minWidth = 140.0
            minHeight = 48.0
        }
        val hbox = HBox(10.0, ccBtn, cpBtn)
        root.children.addAll(hbox)
        val scene = Scene(root)
        stage.title = "l2-overlay-client v${BuildInfo.version}"
        stage.icons.add(Image(javaClass.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
    }
}