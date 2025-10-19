package com.helldembez.l2overlay

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.util.Callback

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
            skillSelector(rowManager),

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

    private fun skillSelector(rowManager: RowManager): ComboBox<Skill> {
        val cb = ComboBox<Skill>()
        cb.items.addAll(
            SK_MANA_SUSPENSION,
            SK_TREE,
            SK_SUBLIME,
            SK_DOD,
            SK_SOF,
            SK_FOI,
            SK_MASS_CLEANSE,
            SK_SOS,
            SK_FI,
            SK_SYM_ATK,
            SK_SYM_DEF,
            SK_SYM_BOW,
            SK_SYM_NOISE,
            SK_VALOR,
            SK_GRANDEUR,
            SK_DREAD,
        )

        cb.apply {
            cellFactory = Callback { createCell() }
            buttonCell = createCell()
            selectionModel.selectFirst()
            setOnAction {
                rowManager.selectedSkill = this.value.skillName
            }
        }
        rowManager.selectedSkill = SK_MANA_SUSPENSION.skillName
        return cb
    }

    private fun createCell() = object : ListCell<Skill>() {
        private val imageView = ImageView()
        override fun updateItem(skill: Skill?, empty: Boolean) {
            super.updateItem(skill, empty)
            if (empty || skill == null) {
                text = null
                graphic = null
            } else {
                text = skill.skillName.name
                imageView.image = skill.image
                imageView.fitHeight = 24.0
                imageView.fitWidth = 24.0
                graphic = imageView
            }
        }
    }
}

