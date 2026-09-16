# Changelog — Fate Dice: Roll of Destiny («Бросок судьбы»)

All notable changes to this project will be documented in this file.

---

## [1.0.3] — 2026-09-16

### 🇷🇺 Русский

#### 👹 Гигантские Боссы и Легендарный Лут
* **Зомби-Колосс (Тир 1 — Natural 1)**: Огромный зомби масштаба **`2.8x`** с **80 HP** в незеритовых доспехах (шлем с Шипами III) и с зачарованным топором.
  * **Награда за победу**: 3–5 Алмазов, 1–2 Золотых яблока, 8–16 Изумрудов, Незеритовый лом и зачарованный Незеритовый топор (Острота V, Прочность III, Эффективность IV).
* **Мега-Титан Крипер (Тир 1)**: Колоссальный заряженный крипер масштаба **`2.2x`** с **60 HP**.
  * Радиус взрыва удвоен до **6 блоков** (через NBT `ExplosionRadius`).
  * Полностью интегрирован с конфигом `allowDestructiveEvents`: при выключенной настройке взрыв не повреждает блоки мира.
* **Громовой Фантом-Левиафан (Тир 2)**: Летающий монстр масштаба **`2.5x`** с **60 HP**, пикирующий под раскаты призывной грозы.
  * **Награда за победу**: Элитры (`Items.ELYTRA`), Зачарованное золотое яблоко (Notch Apple), алмазы и мембраны фантома.
* **Колоссальный Скелет-Снайпер (Тир 6)**: Скелет-гигант масштаба **`2.3x`** с **60 HP** в кольчужном шлеме, стреляющий снайперскими стрелами с расстояния 12 блоков.
  * **Награда за победу**: Снайперский лук (Сила V, Откидывание II, Воспламенение, Прочность III), 32 спектральные стрелы и алмазы.
* **Инфернальный Магма-Титан (Тир 7)**: Огромный Магма-куб **`Размера 6`** с **80 HP** в сопровождении 3 горящих микро-миньонов.
  * **Награда за победу**: Незеритовый слиток, 8–16 золотых слитков, алмазы и сгустки магмы.
* **Разбойники-Громилы (Тир 6)**: 2 поборника (Vindicator) масштаба **`1.5x`** с эффектом Силы.
  * **Награда за победу**: Тотем бессмертия (`Items.TOTEM_OF_UNDYING`), алмазы и изумруды.

#### 🐭 Нашествия Микро-мобов (Micro-Swarms)
* **Армия Пигмеев-Зомби (Тир 4)**: 5 микро-зомби масштаба **`0.35x`** в золотых шлемах с золотыми мечами и Скоростью II.
* **Микро-Криперы-Камикадзе (Тир 6)**: 3 крошечных быстрых крипера масштаба **`0.4x`** с пищащим высокотональным шипением.
* **Рой микро-чешуйниц (Тир 5)**: 7 скоростных чешуйниц масштаба **`0.4x`**.
* **Микро-Жнец Бездны (Тир 9)**: Юркий Визер-скелет масштаба **`0.45x`** со Скоростью III, накладывающий Иссушение III.

#### 💎 Щедрые События с Выпадением Ресурсов
* **«Осыпь меня алмазами!» (Тир 18)**: Настоящий каскадный дождь из **16 алмазов**, падающих с неба в течение нескольких секунд, и финальный **Алмазный блок**!
* **«Всеобщий алмазный дождь» (Тир 18 — Co-op / 150 блоков)**: Раздача сокровищ всем игрокам в радиусе 150 блоков — каждый получает каскад из **8 алмазов и 16 изумрудов**!
* **«Дар Недр» (Тир 18)**: Вулканический выброс редких ресурсов Нижнего мира: 2 Древних обломка, 2 Незеритовых лома, 1 Незеритовый слиток и 8 Плачущего обсидиана.
* **«Золотая лихорадка» (Тир 14)**: Золотой гейзер из 24 золотых слитков, 3 блоков сырого золота и 3 золотых яблок.
* **«Рудный гейзер» (Тир 16)**: Извержение самоцветов — 32 лазурита, 32 редстоуна, 12 изумрудов, 16 железных слитков и 4 алмаза.
* **«Рог Изобилия Фортуны» (Тир 20 — Natural 20)**: Взрывное появление 16 Алмазов, 1 Незеритового блока, 4 Незеритовых ломов, 32 Изумрудов, 2 Зачарованных яблок и Тотема бессмертия под звук Рейд-горна.

#### ⚙️ Технические Улучшения
* Реализованы физические хелперы `dropItemShower` (каскадное падение сверху с частицами фейерверка) и `dropItemBurst` (фонтанный разброс руды с импульсом вверх).
* Добавлен класс `FateMobHelper` и зарегистрирован в NeoForge EventBus для перехвата смертей боссов (`LivingDeathEvent`) и подавления грифинга взрывов (`ExplosionEvent.Detonate`).
* Все мобы защищены шлемами от сгорания при дневном свете и помечены `setPersistenceRequired()` от деспавна.

---

### 🇬🇧 English

#### 👹 Scaled Bosses & High-Tier Loot Drops
* **Zombie Colossus (Tier 1 — Natural 1)**: Massive **`2.8x` scale** zombie with **80 HP**, full Netherite armor (Thorns III helmet), and an enchanted Diamond Axe.
  * **Rewards on defeat**: 3–5 Diamonds, 1–2 Golden Apples, 8–16 Emeralds, Netherite Scrap, and an enchanted Netherite Axe (Sharpness V, Unbreaking III, Efficiency IV).
* **Mega-Titan Creeper (Tier 1)**: Charged colossal creeper at **`2.2x` scale** with **60 HP**.
  * Explosion radius doubled to **6 blocks** (via `ExplosionRadius` NBT).
  * Fully respects `allowDestructiveEvents`: suppresses world block damage when disabled.
* **Thunder Leviathan Phantom (Tier 2)**: Giant flying beast at **`2.5x` scale** with **60 HP**, diving down during a summoned thunderstorm.
  * **Rewards on defeat**: Elytra, Enchanted Golden Apple (Notch Apple), diamonds, and phantom membranes.
* **Colossal Skeleton Sniper (Tier 6)**: Huge skeleton at **`2.3x` scale** with **60 HP** sniping from 12 blocks away.
  * **Rewards on defeat**: Sniper Bow (Power V, Punch II, Flame, Unbreaking III), 32 spectral arrows, and diamonds.
* **Infernal Magma Titan (Tier 7)**: Huge **`Size 6`** Magma Cube with **80 HP** flanked by 3 burning micro-zombie minions.
  * **Rewards on defeat**: Netherite Ingot, 8–16 Gold Ingots, diamonds, and magma cream.
* **Brute Vindicators (Tier 6)**: 2 elite Vindicators at **`1.5x` scale** with Strength I effect.
  * **Rewards on defeat**: Totem of Undying, diamonds, and emeralds.

#### 🐭 Micro-Mob Swarms
* **Pygmy Zombie Swarm (Tier 4)**: 5 micro-zombies at **`0.35x` scale** with golden helmets, swords, and Speed II.
* **Kamikaze Micro-Creepers (Tier 6)**: 3 high-speed mini creepers at **`0.4x` scale** with high-pitched hissing.
* **Micro-Silverfish Swarm (Tier 5)**: 7 speedy silverfish at **`0.4x` scale**.
* **Micro-Void Reaper (Tier 9)**: Fast Wither Skeleton at **`0.45x` scale** with Speed III and Wither effect.

#### 💎 Resource Drop Events
* **"Shower Me With Diamonds!" (Tier 18)**: A cascading shower of **16 diamonds** dropping from the sky over 2.5 seconds, followed by a **Diamond Block**!
* **Co-op Diamond Rain (Tier 18 — 150 blocks)**: Shares wealth with all nearby friends — everyone gets a shower of **8 diamonds and 16 emeralds**!
* **Netherite Cache (Tier 18)**: Volcanic burst of Nether treasures: 2 Ancient Debris, 2 Netherite Scraps, 1 Netherite Ingot, and 8 Crying Obsidian.
* **Gold Rush (Tier 14)**: Golden geyser of 24 Gold Ingots, 3 Raw Gold Blocks, and 3 Golden Apples.
* **Gemstone & Ore Geyser (Tier 16)**: Mineral eruption of 32 Lapis, 32 Redstone, 12 Emeralds, 16 Iron Ingots, and 4 Diamonds.
* **Horn of Ultimate Abundance (Tier 20 — Natural 20)**: Grand explosion of 16 Diamonds, 1 Netherite Block, 4 Netherite Scraps, 32 Emeralds, 2 Notch Apples, and a Totem of Undying.
