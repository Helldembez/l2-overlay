package com.helldembez.l2overlay

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Duration

val SK_MANA_SUSPENSION = Skill(SkillName.MANA_SUSPENSION, 360, Images.MANA_SUSPENSION)
val SK_SUBLIME = Skill(SkillName.SUBLIME, 1200, Images.SUBLIME)
val SK_DOD = Skill(SkillName.DOD, 1800, Images.DOD)
val SK_SOF = Skill(SkillName.SOF, 600, Images.SOF)
val SK_FOI = Skill(SkillName.FOI, 1800, Images.FOI)
val SK_MASS_CLEANSE = Skill(SkillName.MASS_CLEANSE, 1200, Images.MASS_CLEANSE)
val SK_TREE = Skill(SkillName.MASS_CLEANSE, 60, Images.TREE)
val SK_SOS = Skill(SkillName.SOS, 150, Images.SOS)
val SK_FI = Skill(SkillName.FI, 150, Images.FI)
val SK_VALOR = Skill(SkillName.FI, 900, Images.VALOR)
val SK_GRANDEUR = Skill(SkillName.FI, 900, Images.GRANDEUR)
val SK_DREAD = Skill(SkillName.FI, 900, Images.DREAD)

enum class SkillName {
    MANA_SUSPENSION,
    SUBLIME,
    DOD,
    SOF,
    FOI,
    MASS_CLEANSE,
    TREE,
    SOS,
    FI,
    VALOR,
    GRANDEUR,
    DREAD;

    fun imageForName() =
        when (this) {
            MANA_SUSPENSION -> Images.MANA_SUSPENSION
            SUBLIME -> Images.SUBLIME
            DOD -> Images.DOD
            SOF -> Images.SOF
            FOI -> Images.FOI
            MASS_CLEANSE -> Images.MASS_CLEANSE
            TREE -> Images.TREE
            SOS -> Images.SOS
            FI -> Images.FI
            VALOR -> Images.VALOR
            GRANDEUR -> Images.GRANDEUR
            DREAD -> Images.DREAD
        }

    fun skillForName() =
        when (this) {
            MANA_SUSPENSION -> SK_MANA_SUSPENSION
            SUBLIME -> SK_SUBLIME
            DOD -> SK_DOD
            SOF -> SK_SOF
            FOI -> SK_FOI
            MASS_CLEANSE -> SK_MASS_CLEANSE
            TREE -> SK_TREE
            SOS -> SK_SOS
            FI -> SK_FI
            VALOR -> SK_VALOR
            GRANDEUR -> SK_GRANDEUR
            DREAD -> SK_DREAD
        }
}

data class Skill(
    val skillName: SkillName,
    val cooldown: Int,
    val image: Image,
    var timeline: Timeline? = null
) {
    fun startCooldown(counter: Label, imageView: ImageView) {
        if (timeline?.status != Animation.Status.RUNNING) {
            imageView.style = "-fx-opacity: 0.45"
            var remaining = cooldown

            val tl = Timeline(
                KeyFrame(Duration.seconds(1.0), {
                    remaining--
                    if (remaining > 0) {
                        val durationInSeconds = Duration.seconds(remaining.toDouble())
                        if (remaining > 60) {
                            counter.text = "${durationInSeconds.toMinutes().toInt()}m"
                        } else {
                            counter.text = "${remaining}s"
                        }
                    } else {
                        reset(counter, imageView)
                    }
                }
                ))
            tl.cycleCount = cooldown
            tl.play()
            timeline = tl
        }
    }

    fun reset(counter: Label, imageView: ImageView) {
        imageView.style = "-fx-opacity: 1;"
        counter.text = ""
        timeline?.stop()
    }
}