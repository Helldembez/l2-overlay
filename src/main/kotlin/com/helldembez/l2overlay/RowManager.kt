package com.helldembez.l2overlay

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text


class RowManager() {
    val rows = mutableMapOf<PartyRow, SettingsRow>()

    fun resetAll() {
        rows.forEach { (it, _) ->it.skills.forEach { (_, skill) -> skill.skill.reset(skill.counter, skill.imageView) }}
    }

    fun initializeSettingsRows(settingsRoot: VBox, windowRoot: VBox) {
        settingsRoot.children.addAll(rows.map { (partyRow, settingsRow)  ->
            settingsRow.init(settingsRoot, windowRoot, partyRow, rows).row
        })
    }

    fun addRow(settingsRoot: VBox, windowRoot: VBox) {
        val partyRow = createPartyRow()
        val settingsRow = createSettingsRow(partyRow).init(settingsRoot, windowRoot, partyRow, rows)
        rows.put(partyRow, settingsRow)
        settingsRoot.children.add(settingsRow.row)
        windowRoot.children.add(partyRow.row)
    }

    private fun createSettingsRow(row: PartyRow): SettingsRow {
        val text = TextField().apply {
            promptText = "Cp leader"
            text = row.text.text
        }

        val skills = SkillName.entries.flatMap { skillName ->
            val cb = CheckBox()
            cb.apply {
                setOnAction {
                    row.skills[skillName]?.skillBtn?.isVisible = isSelected
                }
            }
            val imageView = ImageView(skillName.imageForName()).apply {
                isPreserveRatio = true;
                fitWidth = 24.0
            }
            listOf(cb, imageView)
        }

        val removeBtn = Button("-")
        val hbox =  HBox(10.0,text, *skills.toTypedArray(), removeBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        return SettingsRow(hbox, text, removeBtn)
    }

    private fun createPartyRow() : PartyRow {
        val text = Text().apply {
            font = Font.font(18.0)
            fill = Color.LIMEGREEN
        }

        val skills = SkillName.entries.associateWith { skillName -> createUISkill(skillName) }
        val skillButtons = skills.map { (_, uiSkill) -> uiSkill.skillBtn }

        val row = HBox(10.0, text, *skillButtons.toTypedArray()).apply {
            alignment = Pos.CENTER_LEFT
        }
        return PartyRow(text, skills, row)
    }

    private fun createUISkill(skillName: SkillName): PartyRow.UISkill {
        val imageView = ImageView(skillName.imageForName()).apply {
            isPreserveRatio = true;
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
        skillButton.isVisible = false
        return PartyRow.UISkill(skill, skillButton, counter, imageView)
    }

    fun refresh() {
        rows.forEach { (partyRow, settingsRow) ->
            partyRow.text.text = settingsRow.text.text
        }
    }

}

data class PartyRow(
    val text: Text,
    val skills: Map<SkillName, UISkill>,
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
    fun init(settingsRoot: VBox, windowRoot: VBox, partyRow: PartyRow, rows: MutableMap<PartyRow, SettingsRow>): SettingsRow {
        removeBtn.apply {
            setOnAction {
                settingsRoot.children.remove(row)
                windowRoot.children.remove(partyRow.row)
                rows.remove(partyRow)
            }
        }
        return this
    }
}