package com.helldembez.l2overlay.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class PartyUpdate(val type: String, val partyId: String, val skill: String? = null, val cooldown: Int? = null)

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(30)
        }
        routing {
            val sessions = ConcurrentHashMap.newKeySet<DefaultWebSocketServerSession>()

            webSocket("/ws") {
                sessions.add(this)
                try {
                    incoming.consumeEach { frame ->
                        println("frame event")
                        if (frame is Frame.Text) {
                            val msg = frame.readText()
                            // parse incoming
                            val event = Json.decodeFromString<PartyUpdate>( msg)

                            println("Received: $event")

                            // broadcast to all clients
                            sessions.forEach { session ->
                                session.send(Frame.Text(Json.encodeToString<PartyUpdate>( event)))
                            }
                        }
                    }
                } finally {
                    sessions.remove(this)
                }
            }
        }
    }.start(wait = true)
}
