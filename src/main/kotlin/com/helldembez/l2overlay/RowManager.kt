package com.helldembez.l2overlay

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage


class RowManager() {
    val rows = mutableMapOf<PartyRow, SettingsRow>()
    var selectedSkill = SkillName.MANA_SUSPENSION
    var selectedColor = Color.LIMEGREEN
    private val maxLabelWidth = SimpleDoubleProperty(0.0)
    val labels = mutableListOf<Text>()

    fun resetAll() {
        rows.forEach { (it, _) -> it.skills.forEach { skill -> skill.skill.reset(skill.counter, skill.imageView) } }
    }

    fun initializeSettingsRows(settingsRoot: VBox, windowRoot: VBox) {
        settingsRoot.children.addAll(rows.map { (partyRow, settingsRow) ->
            settingsRow.init(settingsRoot, windowRoot, partyRow, this).row
        })
    }

    fun addRow(settingsRoot: VBox, windowRoot: VBox) {
        val partyRow = createPartyRow()
        val settingsRow = createSettingsRow(partyRow, settingsRoot).init(settingsRoot, windowRoot, partyRow, this)
        rows[partyRow] = settingsRow
        settingsRoot.children.add(settingsRow.row)
        windowRoot.children.add(partyRow.row)
    }

    private fun createSettingsRow(row: PartyRow, settingsRoot: VBox): SettingsRow {
        val text = TextField().apply {
            promptText = "Cp leader"
            text = row.text.text
        }
        val skills = HBox(5.0).apply {}
        val addSkill = Button("+").apply {
            setOnAction {
                val skillName = selectedSkill
                val uiSkill = createUISkill(skillName)
                row.skills.add(uiSkill)
                row.skillsBox.children.add(uiSkill.skillBtn)
                val imageView = ImageView(selectedSkill.imageForName()).apply {
                    isPreserveRatio = true
                    fitWidth = 24.0
                    setOnMouseEntered {
                        image = Images.CROSS
                    }
                    setOnMouseExited {
                        image = skillName.imageForName()
                    }
                    setOnMouseClicked {
                        skills.children.remove(this)
                        row.skills.remove(uiSkill)
                        row.skillsBox.children.remove(uiSkill.skillBtn)
                        settingsRoot.refresh()
                    }
                }
                skills.children.add(imageView)
                settingsRoot.refresh()
            }
        }

        val removeBtn = Button("-")
        val hbox = HBox(10.0, text, addSkill, skills, removeBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        return SettingsRow(hbox, text, removeBtn)
    }

    private fun createPartyRow(): PartyRow {
        val text = Text().apply {
            font = Font.font(18.0)
            fill = selectedColor
        }
        labels += text
        text.textProperty().addListener { _, _, _ -> recompute() }

        val wrapper = HBox(text).apply {
            minWidthProperty().bind(maxLabelWidth)
            prefWidthProperty().bind(maxLabelWidth)
        }

        val skills = HBox(5.0).apply {}

        val row = HBox(10.0, wrapper, skills).apply {
            alignment = Pos.CENTER_LEFT
            HBox.setHgrow(skills, Priority.ALWAYS) // skills can take remaining space
        }

        return PartyRow(text, mutableListOf(), skills, row)
    }

    private fun createUISkill(skillName: SkillName): PartyRow.UISkill {
        val imageView = ImageView(skillName.imageForName()).apply {
            isPreserveRatio = true
            fitWidth = 24.0
        }
        val counter = Label().apply {
            textFill = Color.ORANGE
            isMouseTransparent = true
        }

        val skill = skillName.skillForName().copy()
        val graphic = StackPane(imageView, counter)
        val skillButton = Button().apply {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphicProperty().set(graphic)
            setOnMouseClicked { e: MouseEvent ->
                if (e.button == MouseButton.PRIMARY) {
                    if (e.isShiftDown) {
                        skill.reset(counter, imageView)
                    } else {
                        skill.startCooldown(counter, imageView)
                    }
                    e.consume()
                }

            }
            style = "-fx-background-color: transparent; -fx-padding: 0;"
        }
        return PartyRow.UISkill(skill, skillButton, counter, imageView)
    }

    private fun recompute() {
        var max = 0.0
        labels.forEach { t ->
            t.applyCss()
            val w = t.layoutBounds.width
            if (w > max) max = w
        }
        maxLabelWidth.set(max)
    }

    fun remove(text: Text) {
        labels.remove(text)
        recompute()
    }

    fun refresh() {
        rows.forEach { (partyRow, settingsRow) ->
            partyRow.text.text = settingsRow.text.text
        }
    }

}

data class PartyRow(
    val text: Text,
    val skills: MutableList<UISkill>,
    val skillsBox: HBox,
    val row: HBox,
) {
    data class UISkill(
        val skill: Skill,
        val skillBtn: Button,
        val counter: Label,
        val imageView: ImageView,
    )
}

data class SettingsRow(
    val row: HBox,
    val text: TextField,
    val removeBtn: Button,
) {
    fun init(settingsRoot: VBox, windowRoot: VBox, partyRow: PartyRow, rowManager: RowManager): SettingsRow {
        removeBtn.apply {
            setOnAction {
                settingsRoot.children.remove(row)
                windowRoot.children.remove(partyRow.row)
                rowManager.rows.remove(partyRow)
                rowManager.remove(partyRow.text)
            }
        }
        return this
    }
}

fun VBox.refresh() {
    (scene.window as? Stage)?.sizeToScene()
}