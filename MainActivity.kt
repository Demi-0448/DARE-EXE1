package com.dareexe.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Night = Color(0xFF090910)
private val Panel = Color(0xFF171725)
private val Purple = Color(0xFFB45CFF)
private val Green = Color(0xFF7CFFB2)
private val White = Color(0xFFF4F0FF)

private data class Rank(val name: String, val emoji: String, val minXp: Int, val color: Color)
private val ranks = listOf(
    Rank("NPC", "🟢", 0, Color(0xFF65E6A8)),
    Rank("BEGINNER", "🔵", 50, Color(0xFF6DB6FF)),
    Rank("INTERMEDIATE", "🟡", 150, Color(0xFFFFD45E)),
    Rank("PRO", "🟠", 350, Color(0xFFFFA35C)),
    Rank("HACKER", "🔴", 650, Color(0xFFFF637D)),
    Rank("DEVELOPER", "🟣", 1000, Purple),
    Rank("LEGEND", "⚫", 1500, Color(0xFFFFF0A6))
)
private data class Quest(val title: String, val description: String, val rank: String, val xp: Int, val category: String)
private val quests = listOf(
    Quest("MAIN CHARACTER ENERGY", "Give a dramatic 15-second introduction as if you're the hero of an action movie.", "NPC", 5, "🎭 Acting"),
    Quest("SIDE QUEST UNLOCKED", "Walk into a room and announce: ‘The side quest begins.’ Keep a straight face.", "NPC", 5, "😂 Comedy"),
    Quest("MEME DIPLOMACY", "Send a friend a harmless meme that perfectly describes your current mood.", "BEGINNER", 10, "👥 Friends"),
    Quest("NEWS AT NINE", "Deliver a dramatic news report about the next thing you eat or drink.", "INTERMEDIATE", 20, "🎬 Creator"),
    Quest("VILLAIN ORIGIN", "Record a theatrical villain monologue about a tiny inconvenience, like a missing pen.", "INTERMEDIATE", 20, "🎭 Acting"),
    Quest("EDITING WIZARD", "Create a 15-second edit with three different transitions using footage you own.", "PRO", 35, "🎨 Editing"),
    Quest("NPC PATCH NOTES", "Write five funny patch notes describing your week as if you were a video game.", "PRO", 35, "✍️ Creativity"),
    Quest("BUG HUNTER", "Find a tiny bug in a project you made, fix it, and write one sentence about the fix.", "HACKER", 50, "💻 Coding"),
    Quest("PIXEL ALCHEMIST", "Make a tiny pixel-art icon using no more than four colours.", "HACKER", 50, "🎨 Art"),
    Quest("BUILD MODE", "Create a tiny program, animation, or game prototype and show what it does.", "DEVELOPER", 75, "💻 Coding"),
    Quest("THE LEGEND CUT", "Combine acting, editing, and storytelling into a short original 30-second scene.", "LEGEND", 100, "🏆 Creator"),
    Quest("COMPLIMENT SPEEDRUN", "Give someone a sincere, specific compliment. No pranks, pressure, or recording required.", "BEGINNER", 10, "👥 Friends")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DareApp(this) }
    }
}

@Composable
private fun DareApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("dareexe", Context.MODE_PRIVATE) }
    var xp by remember { mutableIntStateOf(prefs.getInt("xp", 0)) }
    var completed by remember { mutableIntStateOf(prefs.getInt("completed", 0)) }
    var selectedRank by remember { mutableStateOf("NPC") }
    var currentQuest by remember { mutableStateOf(quests.first()) }
    var completedIds by remember { mutableStateOf(prefs.getStringSet("done", emptySet())?.toSet() ?: emptySet()) }
    var message by remember { mutableStateOf("") }
    val rank = ranks.lastOrNull { xp >= it.minXp } ?: ranks.first()
    val nextRank = ranks.firstOrNull { xp < it.minXp }
    val progress = if (nextRank == null) 1f else ((xp - rank.minXp).toFloat() / (nextRank.minXp - rank.minXp)).coerceIn(0f, 1f)

    MaterialTheme(colorScheme = darkColorScheme(primary = Purple, secondary = Green, background = Night, surface = Panel)) {
        Scaffold(containerColor = Night, bottomBar = {
            NavigationBar(containerColor = Panel, contentColor = White) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Bolt, null) }, label = { Text("QUESTS") })
                NavigationBarItem(selected = false, onClick = { message = "ACHIEVEMENTS: ${if (completed >= 1) "First Quest unlocked!" else "Complete your first quest to unlock a badge."}" }, icon = { Icon(Icons.Default.EmojiEvents, null) }, label = { Text("BADGES") })
                NavigationBarItem(selected = false, onClick = { xp = 0; completed = 0; completedIds = emptySet(); prefs.edit().clear().apply(); message = "Progress reset." }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("RESET") })
            }
        }) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17112B), Night, Night))).padding(padding).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)) {
                item {
                    Text("D A R E . E X E", color = Green, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("YOUR LIFE.\nYOUR QUESTS.\nYOUR CHAOS.", color = White, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.ExtraBold)
                    Text("v1.0 • OFFLINE QUEST ENGINE", color = Purple, fontSize = 11.sp, letterSpacing = 1.5.sp)
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column { Text("PLAYER RANK", color = Color.LightGray, fontSize = 11.sp); Text("${rank.emoji} ${rank.name}", color = rank.color, fontSize = 23.sp, fontWeight = FontWeight.Black) }
                                Column(horizontalAlignment = Alignment.End) { Text("TOTAL XP", color = Color.LightGray, fontSize = 11.sp); Text("$xp XP", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
                            }
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Purple, trackColor = Color(0xFF343044))
                            Text(if (nextRank == null) "MAX RANK ACHIEVED • LEGEND STATUS" else "${nextRank.minXp - xp} XP to ${nextRank.name}", color = Color.LightGray, fontSize = 12.sp)
                            Text("QUESTS COMPLETED: $completed", color = White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                item {
                    Text("CHOOSE YOUR CHAOS", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ranks.chunked(2).forEach { rowRanks ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                rowRanks.forEach { r ->
                                    val unlocked = xp >= r.minXp
                                    FilterChip(selected = selectedRank == r.name, onClick = { selectedRank = r.name }, label = { Text("${r.emoji} ${r.name}", fontSize = 10.sp) }, enabled = unlocked, modifier = Modifier.weight(1f))
                                }
                                if (rowRanks.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    Text("Ranks unlock as you earn XP. Choose any unlocked rank.", color = Color.Gray, fontSize = 11.sp)
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF211735)), shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) { Text("🎲  RANDOM QUEST", color = Green, fontWeight = FontWeight.Black, fontSize = 13.sp); Spacer(Modifier.weight(1f)); Text(currentQuest.category, color = Color.LightGray, fontSize = 11.sp) }
                            Text(currentQuest.title, color = White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text(currentQuest.description, color = Color(0xFFE1DDED), fontSize = 15.sp, lineHeight = 22.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) { Text("${currentQuest.rank}  •  +${currentQuest.xp} XP", color = Green, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Text("Optional proof", color = Color.LightGray, fontSize = 11.sp) }
                            Button(onClick = { val available = quests.filter { ranks.first { r -> r.name == it.rank }.minXp <= xp && it.rank == selectedRank }; val pool = if (available.isNotEmpty()) available else quests.filter { it.rank == selectedRank }; currentQuest = (pool.ifEmpty { quests.filter { it.rank == "NPC" } }).random(); message = "New quest loaded." }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(8.dp)); Text("REROLL QUEST") }
                            Button(onClick = {
                                val id = currentQuest.title
                                if (id in completedIds) { message = "Already claimed! Reroll for another quest." }
                                else {
                                    xp += currentQuest.xp; completed += 1; completedIds = completedIds + id
                                    prefs.edit().putInt("xp", xp).putInt("completed", completed).putStringSet("done", completedIds).apply()
                                    message = "+${currentQuest.xp} XP claimed! Nice work." 
                                }
                            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Night)) { Text("✓  COMPLETE QUEST  +${currentQuest.xp} XP", fontWeight = FontWeight.Black) }
                            Text("Safety rule: skip anything unsafe, humiliating, or involving unwanted contact. No dare is worth risking your safety.", color = Color(0xFFFFC7D0), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
                if (message.isNotBlank()) item { Text(message, color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                item { Text("LOCAL SAVE • NO ACCOUNT • NO SERVER REQUIRED", color = Color.Gray, fontSize = 10.sp, letterSpacing = 1.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
            }
        }
    }
}
