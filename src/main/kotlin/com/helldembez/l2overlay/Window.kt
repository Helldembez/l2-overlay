package com.helldembez.l2overlay

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlin.system.exitProcess

object BuildInfo {
    val version: String = BuildInfo::class.java.`package`?.implementationVersion ?: "dev"
}

class CooldownOverlay : Application(), NativeKeyListener {
    private val rowManager = RowManager()

    override fun start(stage: Stage) {
        val root = VBox(10.0).apply {
            style = "-fx-background-color: transparent;"
        }

        val resetAllButton = Button("Reset All").apply {
            setOnAction {
                rowManager.resetAll()
            }
            style = "-fx-font-size: 12; -fx-padding: 3 10;"
        }
        val settingsBtn = Button("Settings").apply {
            setOnAction {
                SettingsWindow.open(this.scene.window, rowManager, root)
            }
        }
        val closeBtn = Button("Close").apply {
            setOnAction {
                exitProcess(0)
            }
        }
        val version = Label("v${BuildInfo.version}").apply {
            textFill = Color.BLACK
            isMouseTransparent = true
        }
        val row = HBox(10.0, resetAllButton, settingsBtn, closeBtn, version).apply {
            alignment = Pos.CENTER_LEFT
        }
        root.children.add(row)

        val scene = Scene(root).apply {
            fill = Color.TRANSPARENT
        }

        // Transparent overlay
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.title = "l2-overlay v${BuildInfo.version}"
        stage.icons.add(Image(javaClass.getResourceAsStream("/icon.png")))
        stage.isAlwaysOnTop = true
        stage.scene = scene

        // Enable dragging the overlay
        var dragOffsetX = 0.0
        var dragOffsetY = 0.0
        root.addEventFilter(MouseEvent.MOUSE_PRESSED) {
            dragOffsetX = stage.x - it.screenX
            dragOffsetY = stage.y - it.screenY
        }
        root.addEventFilter(MouseEvent.MOUSE_DRAGGED) {
            stage.x = it.screenX + dragOffsetX
            stage.y = it.screenY + dragOffsetY
        }

        stage.show()

        // Register global hotkey
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(this)
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) { }
    override fun nativeKeyReleased(e: NativeKeyEvent?) {}
    override fun nativeKeyTyped(e: NativeKeyEvent?) {}
}
