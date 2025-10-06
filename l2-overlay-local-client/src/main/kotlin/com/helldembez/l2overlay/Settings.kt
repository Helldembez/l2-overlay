package com.helldembez.l2overlay

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window

object SettingsWindow {
    private var stage: Stage? = null

    fun open(owner: Window?, rowManager: RowManager, parentRoot: VBox) {
        // If already open, focus it
        stage?.let {
            if (it.isShowing) {
                it.toFront()
                it.requestFocus()
                return
            }
        }

        val root = VBox(12.0).apply {
            padding = Insets(16.0)
        }
        root.children += HBox(
            10.0,
            Button("+").apply {
                setOnAction {
                    rowManager.addRow(root, parentRoot)
                    root.refresh()
                }
            },
            ColorPicker(rowManager.selectedColor).apply {
                setOnAction {
                    rowManager.selectedColor = value
                    rowManager.labels.forEach {
                        it.fill = value
                    }
                    parentRoot.refresh()
                }
            },
            ComboBox(FXCollections.observableArrayList(SkillName.entries.map(SkillName::name))).apply {
                selectionModel.selectFirst()
                setOnAction {
                    rowManager.selectedSkill = SkillName.valueOf(this.value)
                }
            },

            Button("Close").apply {
                setOnAction {
                    rowManager.refresh()
                    parentRoot.refresh()
                    stage?.close()
                }
            }
        )

        rowManager.initializeSettingsRows(root, parentRoot)

        stage = Stage().apply {
            initOwner(owner)
            initStyle(StageStyle.DECORATED)
            title = "Settings"
            scene = Scene(root)
            isResizable = true
            setOnHidden { stage = null }
        }
        stage!!.show()
        root.refresh()
    }
}
