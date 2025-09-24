package com.helldembez.l2overlay

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.util.Duration

val SK_MANA_SUSPENSION = Skill(SkillName.MANA_SUSPENSION, 360, Images.MANA_SUSPENSION)
val SK_SUBLIME = Skill(SkillName.SUBLIME,600, Images.SUBLIME)
val SK_DOD = Skill(SkillName.DOD,1800, Images.DOD)
val SK_SOF = Skill(SkillName.SOF,600, Images.SOF)
val SK_FOI = Skill(SkillName.FOI,600, Images.FOI)

enum class SkillName {
    MANA_SUSPENSION,
    SUBLIME,
    DOD,
    SOF,
    FOI;

    fun imageForName() =
        when(this) {
            MANA_SUSPENSION -> Images.MANA_SUSPENSION
            SUBLIME -> Images.SUBLIME
            DOD -> Images.DOD
            SOF -> Images.SOF
            FOI -> Images.FOI
        }

    fun skillForName() =
        when(this) {
            MANA_SUSPENSION -> SK_MANA_SUSPENSION
            SUBLIME -> SK_SUBLIME
            DOD -> SK_DOD
            SOF -> SK_SOF
            FOI -> SK_FOI
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
                        }
                        else {
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