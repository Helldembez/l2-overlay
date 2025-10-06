package com.helldembez.l2overlay

import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import io.ktor.http.ContentType.Application.Json
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

@Serializable
data class PartyUpdate(val type: String, val partyId: String, val skill: String? = null, val cooldown: Int? = null)

class CcWindow : Application(), NativeKeyListener {
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var socket: WebSocket
    private val updates = ListView<String>()

    override fun start(stage: Stage) {
        val root = VBox(10.0)
        val text = TextField("CC LEADER")
        val hbox = HBox(10.0, text)
        root.children.addAll(hbox)
        val scene = Scene(root)
        stage.title = "l2-overlay-client v${BuildInfo.version}"
        stage.icons.add(Image(javaClass.getResourceAsStream("/icon.png")))
        stage.scene = scene
        stage.show()
        println("opened window")
        connect()
    }

    private fun connect() {
        val client = HttpClient.newHttpClient()
        client.newWebSocketBuilder()
            .buildAsync(URI("ws://localhost:8080/ws"), object : WebSocket.Listener {
                override fun onText(ws: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
                    println("ows open build")
                    val update = json.decodeFromString<PartyUpdate>(data.toString())
                    Platform.runLater {
                        updates.items.add("Party ${update.partyId}: ${update.skill} (${update.cooldown}s)")
                    }
                    return null
                }
            }).thenAccept { ws ->
                println("ws accept")
                socket = ws
                // Example: send one message after connect
                val msg = json.encodeToString(PartyUpdate("update_skill", "CP1", "Heal", 20))
                ws.sendText(msg, true)
            }
    }
}