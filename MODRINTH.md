# 🎲 Fate Dice: Roll of Destiny («Бросок судьбы»)

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen?style=for-the-badge&logo=minecraft)
![NeoForge](https://img.shields.io/badge/Loader-NeoForge-orange?style=for-the-badge)
![Side](https://img.shields.io/badge/Side-Client%20%26%20Server-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

> *«Fortune favors the bold... or crushes them into dust.»*

**Fate Dice** adds a mystical **D20 (twenty-sided die)** directly into your Minecraft survival journey. With a simple right-click, roll the die, listen to the suspenseful clatter of bones, and let destiny decide your fate. 

Will you be blessed by the Gods with enchanted Elytra and divine strength, or will you face the wrath of fate with a direct lightning strike and a charged creeper at your back?

---

## ✨ Features

* 🎲 **One-Click In-Hand Rolling**: Right-click the D20 in your hand to start the roll. Experience a 1.25-second suspense phase with realistic bone clattering audio and animated HUD titles before the number reveals itself!
* 🌀 **20 Thematic Tiers & 100+ Outcomes**: Each face of the D20 in **Fate Dice** rolls from a vast pool of unique sub-events including control distortions, inter-player curses, and illusions. You will almost never see the exact same outcome twice!
* 👹 **Giant Bosses & Micro-Mob Swarms**:
  * **🧟 Zombie Colossus (2.8× scale, 80 HP)**: Towering undead clad in Netherite with a devastating axe. Defeating him rewards diamonds, golden apples, emeralds, and netherite gear!
  * **💥 Mega-Titan Creeper (2.2× scale, 60 HP)**: Enormous charged creeper with a doubled explosion radius of 6 blocks (fully respects `allowDestructiveEvents`).
  * **🦅 Thunder Leviathan Phantom (2.5× scale)**: Descends amidst a summoned lightning storm. Slaying it yields Elytra and an Enchanted Golden Apple!
  * **🎯 Colossal Skeleton Sniper (2.3× scale)**: Snipes from afar with high-knockback arrows, dropping legendary Power V bows.
  * **🌋 Infernal Magma Titan (Size 6)**: Spawns blazing micro-minions and drops Netherite ingots.
  * **🐭 Micro-Mob Swarms (0.35× - 0.45× scale)**: Agile pygmy zombies in golden helmets, mini kamikaze creepers, and the elusive Micro-Void Reaper!
* ⚡ **Epic Critical Rolls**:
  * **💀 Natural 1 (Critical Failure)**: Darkness falls, lightning strikes you, controls warp, or giant titans emerge to crush you!
  * **🌟 Natural 20 (Critical Success)**: A triumphant Raid Horn sounds alongside the golden Totem of Undying animation, rewarding you with god-tier buffs for 5 minutes and legendary gifts (Enchanted Elytra, Nether Star, or the God-forged Netherite Sword)!
* 🐭 **Native Player & Mob Resizing (No External Mods Required!)**: Powered natively by Minecraft 1.21's `generic.scale` attribute, roll 4 temporarily shrinks you to mouse size (`0.3x`) — run under slabs, sneak into 1-block gaps, and evade danger!
* ⏳ **Self-Restoring World Changes**: Hazardous events like turning 5x5 blocks under your feet into cobwebs or soul sand automatically revert back to their original state after 15 seconds. No ruined bases!
* 🧲 **Block Transmutation**: High rolls transmute nearby stone and deepslate into diamond veins, gold veins, or even Ancient Debris.
* 🛡️ **Server & Griefing Friendly**: Includes a built-in config to disable destructive events (fires/explosions) for multiplayer servers.

---

## 📜 The Fate Table (Outcomes Overview)

<details>
<summary><b>🔍 Click to expand the full 20 Tiers & Sub-events Table</b></summary>

| Roll | Category | Event Theme | Example Sub-Events |
| :---: | :--- | :--- | :--- |
| **1** | 💀 **Critical Fail** | **Wrath of Fate** | • Direct lightning bolt, Mining Fatigue IV + Blindness (30s), damaged mainhand tool.<br>• Spawns a ticking Charged Creeper at your back.<br>• Launches you 25 blocks skyward with zero fall protection! |
| **2** | 🌧️ Misfortune | **Local Storm** | • Instant thunderstorm + 3 lightning strikes around you + levitation.<br>• Deep freeze effect (powder snow freezing) + Slowness II. |
| **3** | 🪱 Misfortune | **Foul Ground** | • 5×5 area beneath you turns into cobwebs (auto-reverts in 15s).<br>• 5×5 soul sand + 3 silverfish swarm (auto-reverts in 15s). |
| **4** | 🐀 Misfortune | **Micro-Human** | • Shrunk to **0.3× scale** for 60 seconds! Fit under trapdoors and slabs.<br>• 0.5× scale + Weakness. |
| **5** | 👻 Misfortune | **Paranormal Fright** | • 3 Phantoms dive from the sky + Warden roar.<br>• Blinding Darkness + cave ambience screamer. |
| **6** | 🧟 Misfortune | **The Swarm** | • 4 Helmeted Zombies with iron swords.<br>• 3 venomous Cave Spiders. |
| **7** | 🔥 Misfortune | **Cursed Flame** | • Set on fire for 10 seconds with a fiery ring surrounding you. |
| **8** | 🌀 Misfortune | **Mad Gravity** | • Rollercoaster: 3-cycle alternation between Levitation and Slow Falling. |
| **9** | 💔 Misfortune | **Curse of Weakness** | • Weakness II + Slowness II for 60s + food level drops to starving. |
| **10** | 🎲 **Neutral** | **Fate is Silent** | • Mysterious chime — nothing happens.<br>• Cooldown refunded instantly (free re-roll)!<br>• Harmless "Herald of the Void" chicken spawns and vanishes in smoke. |
| **11** | 🍞 Minor Bonus | **Hearty Snack** | • Full hunger saturation + 1 Golden Apple.<br>• 5 Golden Carrots + Saturation II.<br>• Basket of 8 Cooked Steaks & 8 Baked Potatoes. |
| **12** | 🏃 Minor Bonus | **Scout's Haste** | • Speed II + Jump Boost II for 45s.<br>• Speed III + Dolphin's Grace + Water Breathing. |
| **13** | 👁️ Minor Bonus | **Chameleon** | • Invisibility + Night Vision (60s) + smoke cloud screen. |
| **14** | ⛏️ Minor Bonus | **Golden Hands** | • Haste II for 2 minutes.<br>• Haste III + repairs your held tool by 150 durability. |
| **15** | 🔮 Bonus | **Random Alchemy** | • *Fighter Set*: Strength II + Resistance I + Regeneration I (2 min).<br>• *Miner Set*: Night Vision + Fire Resistance + Haste I (3 min).<br>• *Windwalker Set*: Speed II + Slow Falling + Jump Boost II (2 min). |
| **16** | 💖 Good Bonus | **Second Wind** | • Full instant heal + Absorption IV (8 golden hearts) for 3 minutes.<br>• Full cleanse of all debuffs + Regeneration III (20s). |
| **17** | ☀️ Good Bonus | **Clear Horizon** | • Clears thunderstorms instantly, sets daytime to dawn, grants Hero of the Village II (5 min). |
| **18** | 🧲 Great Bonus | **Magnet of Wealth** | • Transmutes 4 nearby stone blocks into **Diamond Ore**.<br>• Transmutes stone into Gold Ore + drops 5 Gold Ingots.<br>• Transmutes stone into **Ancient Debris**! |
| **19** | 🐎 Great Bonus | **Faithful Companion** | • Tamed white Warhorse with Diamond Horse Armor and Saddle.<br>• Friendly Iron Golem "Guardian of Fate".<br>• Pack of 3 loyal tamed Wolves. |
| **20** | 🌟 **Natural 20** | **Blessing of the Gods** | • 5-minute Strength III, Resistance II, Glowing & Regeneration.<br>• **Gift A**: Enchanted Elytra (Unbreaking III, Mending) + 64 Fireworks.<br>• **Gift B**: Nether Star + 2 Enchanted Golden Apples.<br>• **Gift C**: Netherite Sword "Blade of Destiny" (Sharpness V, Looting III, Fire Aspect II, Unbreaking III). |

</details>

---

## 🔨 Crafting Recipe

Obtain the D20 in standard Survival mode using vanilla ingredients:

```
[ Redstone ]     [ Gold Ingot ]      [ Redstone ]
[ Gold Ingot ]   [ Amethyst Shard ]  [ Gold Ingot ]
[ Redstone ]     [ Gold Ingot ]      [ Redstone ]
```
*(Yields 1x Fate Die D20)*

---

## ⚙️ Configuration

A configuration file is generated at `.minecraft/config/fatedice-common.toml`:

```toml
[general]
    # Cooldown duration for D20 Dice in seconds (Default: 20)
    # Range: 1 ~ 3600
    cooldownSeconds = 20

    # Allow potentially destructive events like fires or lightning (Default: true)
    allowDestructiveEvents = true

    # Broadcast roll outcome to chat for all players (Default: true)
    broadcastRollToChat = true

    # Delay in ticks before the final dice roll is revealed (Default: 25 = 1.25s)
    suspenseDelayTicks = 25
```

---

## 📌 Modpacks & Info

* **📦 Modpack Policy**: **Yes!** You are completely free and welcome to include **Fate Dice** in any public or private modpack on Modrinth, CurseForge, or custom launchers.
* **🌐 Supported Languages**:
  * 🇬🇧 English (`en_us`)
  * 🇷🇺 Русский (`ru_ru`)
