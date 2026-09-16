package com.fatedice.fate;

import com.fatedice.FateDiceMod;
import com.fatedice.config.FateConfig;
import com.fatedice.init.ModEffects;
import com.fatedice.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FateEventsPool {
    private static final Map<Integer, FateTier> TIERS = new HashMap<>();

    public static void register(FateTier tier) {
        TIERS.put(tier.getRollValue(), tier);
    }

    public static FateTier getTier(int roll) {
        return TIERS.get(roll);
    }

    public static void init() {
        // ==========================================
        // 1: CRITICAL FAIL («Гнев судьбы»)
        // ==========================================
        register(new FateTier(1,
                Component.translatable("fate.tier.1.title"),
                Component.translatable("fate.category.critical_fail"),
                "§4§l")
                // 1.A: Молния в игрока, слепота, утомление, повреждение инструмента
                .add((level, player, pos) -> {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.moveTo(player.position());
                        level.addFreshEntity(bolt);
                    }
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));

                    ItemStack mainHand = player.getMainHandItem();
                    if (!mainHand.isEmpty() && mainHand.isDamageableItem()) {
                        mainHand.hurtAndBreak(mainHand.getMaxDamage() / 2, player, player.getEquipmentSlotForItem(mainHand));
                    }
                })
                // 1.B: Заряженный крипер за спиной с шипением
                .add((level, player, pos) -> {
                    Vec3 look = player.getLookAngle();
                    BlockPos spawnPos = pos.offset((int) (-look.x * 3), 0, (int) (-look.z * 3));
                    Creeper creeper = EntityType.CREEPER.create(level);
                    if (creeper != null) {
                        creeper.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                        if (bolt != null) {
                            bolt.moveTo(creeper.position());
                            bolt.setVisualOnly(true);
                            level.addFreshEntity(bolt);
                        }
                        creeper.thunderHit(level, bolt);
                        creeper.setCustomName(Component.literal("§4Заряженное Возмездие"));
                        level.addFreshEntity(creeper);
                    }
                })
                // 1.C: Взлет на 25 блоков и свободное падение
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 9));
                    level.playSound(null, pos, SoundEvents.GHAST_WARN, SoundSource.PLAYERS, 1.0F, 0.5F);
                })
                // 1.D (PvP / Co-op): «Козел отпущения» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    ServerPlayer victim = FatePlayerUtils.findNearestOtherPlayer(level, player, 150.0);
                    if (victim != null) {
                        LightningBolt b1 = EntityType.LIGHTNING_BOLT.create(level);
                        if (b1 != null) { b1.moveTo(player.position()); level.addFreshEntity(b1); }
                        LightningBolt b2 = EntityType.LIGHTNING_BOLT.create(level);
                        if (b2 != null) { b2.moveTo(victim.position()); level.addFreshEntity(b2); }

                        dropItem(level, victim.blockPosition(), new ItemStack(Items.GOLDEN_APPLE));
                        victim.displayClientMessage(Component.literal("§eВ вас ударила рикошетная молния от " + player.getName().getString() + "! Но вам досталось золотое яблоко!"), false);
                        player.displayClientMessage(Component.literal("§cВаша кара задела " + victim.getName().getString() + "!"), false);
                    } else {
                        // Solo fallback: 3 lightnings nearby
                        for (int i = 0; i < 3; i++) {
                            LightningBolt b = EntityType.LIGHTNING_BOLT.create(level);
                            if (b != null) {
                                b.moveTo(pos.getX() + level.random.nextInt(7) - 3, pos.getY(), pos.getZ() + level.random.nextInt(7) - 3, 0, 0);
                                level.addFreshEntity(b);
                            }
                        }
                    }
                })
                // 1.E (Галлюцинация): Аудио-скример шипения и взрыва
                .add((level, player, pos) -> {
                    level.playSound(null, pos, SoundEvents.CREEPER_PRIMED, SoundSource.PLAYERS, 2.0F, 1.0F);
                    FateScheduler.schedule(25, () -> {
                        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.0F, 0.8F);
                        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0);
                        player.displayClientMessage(Component.literal("§4Это была всего лишь жуткая галлюцинация... Сердце ушло в пятки!"), true);
                    });
                })
        );

        // ==========================================
        // 2: НЕПРИЯТНОСТЬ («Локальный шторм»)
        // ==========================================
        register(new FateTier(2,
                Component.translatable("fate.tier.2.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    level.setWeatherParameters(0, 6000, true, true);
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
                    for (int i = 0; i < 3; i++) {
                        double angle = (i * 2 * Math.PI) / 3;
                        BlockPos strikePos = pos.offset((int) (Math.cos(angle) * 7), 0, (int) (Math.sin(angle) * 7));
                        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                        if (bolt != null) {
                            bolt.moveTo(strikePos.getX() + 0.5, strikePos.getY(), strikePos.getZ() + 0.5, 0, 0);
                            level.addFreshEntity(bolt);
                        }
                    }
                })
                .add((level, player, pos) -> {
                    player.setTicksFrozen(240);
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1));
                })
                // 2.C (Управление): «Морская болезнь» — инверсия WASD на 15 секунд
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(ModEffects.INVERTED_CONTROLS, 300, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
                    player.displayClientMessage(Component.literal("§cГолова кружится... Ваши ноги идут в обратную сторону! (WASD инвертирован)"), true);
                    level.playSound(null, pos, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.8F, 1.2F);
                })
                // 2.D (Галлюцинация): Вой бури и летучие мыши
                .add((level, player, pos) -> {
                    level.playSound(null, pos, SoundEvents.PHANTOM_BITE, SoundSource.PLAYERS, 1.5F, 0.5F);
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
                    for (int i = 0; i < 5; i++) {
                        Chicken bat = EntityType.CHICKEN.create(level);
                        if (bat != null) {
                            bat.moveTo(pos.getX() + level.random.nextInt(5) - 2, pos.getY() + 2, pos.getZ() + level.random.nextInt(5) - 2, 0, 0);
                            level.addFreshEntity(bat);
                            FateScheduler.schedule(80, bat::discard);
                        }
                    }
                })
        );

        // ==========================================
        // 3: НЕПРИЯТНОСТЬ («Дурная почва»)
        // ==========================================
        register(new FateTier(3,
                Component.translatable("fate.tier.3.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    replaceGroundTemporarily(level, player.blockPosition().below(), Blocks.COBWEB.defaultBlockState(), 300);
                })
                .add((level, player, pos) -> {
                    replaceGroundTemporarily(level, player.blockPosition().below(), Blocks.SOUL_SAND.defaultBlockState(), 300);
                })
                // 3.C (PvP / Co-op): «Обмен телами» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    ServerPlayer target = FatePlayerUtils.findNearestOtherPlayer(level, player, 150.0);
                    if (target != null) {
                        Vec3 pPos = player.position();
                        Vec3 tPos = target.position();
                        float pYRot = player.getYRot();
                        float tYRot = target.getYRot();

                        player.teleportTo(tPos.x, tPos.y, tPos.z);
                        player.setYRot(tYRot);
                        target.teleportTo(pPos.x, pPos.y, pPos.z);
                        target.setYRot(pYRot);

                        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        level.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                        player.displayClientMessage(Component.literal("§dВы поменялись местами с " + target.getName().getString() + "!"), false);
                        target.displayClientMessage(Component.literal("§d" + player.getName().getString() + " поменялся с вами местами силой судьбы!"), false);
                    } else {
                        // Solo fallback: обмен местами с ближайшим мобом
                        LivingEntity nearestMob = FatePlayerUtils.findNearestOtherEntity(level, player, 30.0);
                        if (nearestMob != null) {
                            Vec3 pPos = player.position();
                            Vec3 mPos = nearestMob.position();
                            player.teleportTo(mPos.x, mPos.y, mPos.z);
                            nearestMob.teleportTo(pPos.x, pPos.y, pPos.z);
                            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                            player.displayClientMessage(Component.literal("§dВы поменялись местами с ближайшим существом!"), true);
                        } else {
                            player.teleportTo(player.getX() + 10, player.getY(), player.getZ() + 10);
                            player.displayClientMessage(Component.literal("§dВас внезапно сместило в пространстве!"), true);
                        }
                    }
                })
                // 3.D: «Вязкое болото» — тяжелые ноги
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(ModEffects.HEAVY_LEGS, 300, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1));
                    player.displayClientMessage(Component.literal("§cВаши ноги словно налились свинцом! Прыжки заблокированы."), true);
                    level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 0.6F);
                })
        );

        // ==========================================
        // 4: НЕПРИЯТНОСТЬ («Микро-человек»)
        // ==========================================
        register(new FateTier(4,
                Component.translatable("fate.tier.4.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    applyScale(player, "micro_scale", -0.70, 1200, "§cВы сжались до размера мыши (0.3x)!");
                })
                .add((level, player, pos) -> {
                    applyScale(player, "micro_scale", -0.50, 900, "§cВы уменьшились до 0.5x!");
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 900, 0));
                })
                // 4.C (Управление): «Гномик-спринтер» — уменьшение + бесконтрольный бег
                .add((level, player, pos) -> {
                    applyScale(player, "micro_scale", -0.60, 600, "§cВы крошечный и не можете перестать бежать!");
                    player.addEffect(new MobEffectInstance(ModEffects.FORCED_SPRINT, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
                })
        );

        // ==========================================
        // 5: НЕПРИЯТНОСТЬ («Паранормальный испуг»)
        // ==========================================
        register(new FateTier(5,
                Component.translatable("fate.tier.5.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    for (int i = 0; i < 3; i++) {
                        Phantom phantom = EntityType.PHANTOM.create(level);
                        if (phantom != null) {
                            phantom.moveTo(pos.getX() + level.random.nextInt(7) - 3, pos.getY() + 15, pos.getZ() + level.random.nextInt(7) - 3, 0, 0);
                            level.addFreshEntity(phantom);
                        }
                    }
                    level.playSound(null, pos, SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.0F, 0.8F);
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    level.playSound(null, pos, SoundEvents.AMBIENT_CAVE.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
                })
                // 5.C (Галлюцинация): Скример зажженного динамита прямо в ухо
                .add((level, player, pos) -> {
                    level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 2.0F, 1.0F);
                    FateScheduler.schedule(40, () -> {
                        level.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1.0F, 0.5F);
                        player.displayClientMessage(Component.literal("§7Фух... Никакого динамита не было."), true);
                    });
                })
                // 5.D: Выпадение предмета из оффхенда
                .add((level, player, pos) -> {
                    ItemStack off = player.getOffhandItem();
                    if (!off.isEmpty()) {
                        player.drop(off.copy(), true);
                        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        player.displayClientMessage(Component.literal("§cОт неожиданного испуга вы выронили предмет из левой руки!"), true);
                        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);
                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
                    }
                })
        );

        // ==========================================
        // 6: НЕПРИЯТНОСТЬ («Нашествие»)
        // ==========================================
        register(new FateTier(6,
                Component.translatable("fate.tier.6.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    for (int i = 0; i < 4; i++) {
                        Zombie zombie = EntityType.ZOMBIE.create(level);
                        if (zombie != null) {
                            zombie.moveTo(pos.getX() + level.random.nextInt(9) - 4, pos.getY(), pos.getZ() + level.random.nextInt(9) - 4, 0, 0);
                            zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                            zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                            level.addFreshEntity(zombie);
                        }
                    }
                })
                .add((level, player, pos) -> {
                    for (int i = 0; i < 3; i++) {
                        CaveSpider spider = EntityType.CAVE_SPIDER.create(level);
                        if (spider != null) {
                            spider.moveTo(pos.getX() + level.random.nextInt(7) - 3, pos.getY(), pos.getZ() + level.random.nextInt(7) - 3, 0, 0);
                            level.addFreshEntity(spider);
                        }
                    }
                })
                // 6.C (PvP / Co-op): «Засада для соседа» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    ServerPlayer target = FatePlayerUtils.findNearestOtherPlayer(level, player, 150.0);
                    ServerPlayer victim = target != null ? target : player;
                    for (int i = 0; i < 3; i++) {
                        Skeleton skel = EntityType.SKELETON.create(level);
                        if (skel != null) {
                            skel.moveTo(victim.getX() + level.random.nextInt(5) - 2, victim.getY(), victim.getZ() + level.random.nextInt(5) - 2, 0, 0);
                            skel.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                            level.addFreshEntity(skel);
                        }
                    }
                    if (target != null) {
                        target.displayClientMessage(Component.literal("§cНа вас внезапно напали скелеты из-за неудачного броска " + player.getName().getString() + "!"), false);
                        player.displayClientMessage(Component.literal("§6Скелеты заспавнились возле " + target.getName().getString() + "!"), false);
                    }
                })
        );

        // ==========================================
        // 7: НЕПРИЯТНОСТЬ («Проклятое пламя»)
        // ==========================================
        register(new FateTier(7,
                Component.translatable("fate.tier.7.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    player.igniteForSeconds(10);
                    if (FateConfig.ALLOW_DESTRUCTIVE_EVENTS.get()) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                BlockPos firePos = pos.offset(dx, 0, dz);
                                if (level.isEmptyBlock(firePos) && level.getBlockState(firePos.below()).isSolid()) {
                                    level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
                                }
                            }
                        }
                    }
                })
                // 7.B (Управление): «Горящие пятки» — принудительный бег вперед
                .add((level, player, pos) -> {
                    player.igniteForSeconds(5);
                    player.addEffect(new MobEffectInstance(ModEffects.FORCED_SPRINT, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
                    player.displayClientMessage(Component.literal("§cПод вами горит земля! Вы бежите вперед сломя голову!"), true);
                })
        );

        // ==========================================
        // 8: НЕПРИЯТНОСТЬ («Безумная гравитация»)
        // ==========================================
        register(new FateTier(8,
                Component.translatable("fate.tier.8.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 1));
                    FateScheduler.schedule(60, () -> {
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0));
                        FateScheduler.schedule(60, () -> {
                            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 1));
                            FateScheduler.schedule(60, () -> {
                                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0));
                            });
                        });
                    });
                })
                // 8.B (Управление): «Свинцовые ноги» — запрет прыжков
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(ModEffects.HEAVY_LEGS, 400, 0));
                    player.displayClientMessage(Component.literal("§cГравитация вдавила вас в землю! Прыжки заблокированы на 20 сек."), true);
                    level.playSound(null, pos, SoundEvents.ANVIL_FALL, SoundSource.PLAYERS, 0.7F, 0.5F);
                })
                // 8.C (PvP / Co-op): «Гравитационный отскок»
                .add((level, player, pos) -> {
                    AABB radiusBox = player.getBoundingBox().inflate(15.0);
                    List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, radiusBox, LivingEntity::isAlive);
                    for (LivingEntity e : nearby) {
                        e.setDeltaMovement(e.getDeltaMovement().x, 1.2, e.getDeltaMovement().z);
                        e.hurtMarked = true;
                    }
                    level.playSound(null, pos, SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 0.8F);
                    player.displayClientMessage(Component.literal("§bГравитационная волна подбросила всех вокруг!"), true);
                })
        );

        // ==========================================
        // 9: НЕПРИЯТНОСТЬ («Проклятие слабости»)
        // ==========================================
        register(new FateTier(9,
                Component.translatable("fate.tier.9.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1));
                    player.getFoodData().setFoodLevel(Math.min(player.getFoodData().getFoodLevel(), 2));
                })
                // 9.B (Управление): «Дрожащие руки» — перемешивание слотов хотбара
                .add((level, player, pos) -> {
                    var inv = player.getInventory();
                    for (int i = 0; i < 9; i++) {
                        int swapWith = level.random.nextInt(9);
                        ItemStack temp = inv.getItem(i);
                        inv.setItem(i, inv.getItem(swapWith));
                        inv.setItem(swapWith, temp);
                    }
                    player.displayClientMessage(Component.literal("§6Ваши руки задрожали, и предметы в хотбаре перемешались!"), true);
                    level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                })
                // 9.C (PvP / Co-op): «Сброс кармы» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    ServerPlayer other = FatePlayerUtils.findNearestOtherPlayer(level, player, 150.0);
                    if (other != null) {
                        other.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
                        other.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1));
                        other.displayClientMessage(Component.literal("§c" + player.getName().getString() + " сбросил на вас свое проклятие слабости!"), false);
                        player.displayClientMessage(Component.literal("§aВы перенаправили слабость на " + other.getName().getString() + "!"), false);
                    } else {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
                    }
                })
        );

        // ==========================================
        // 10: НЕЙТРАЛЬНЫЙ ИСХОД («Судьба молчит»)
        // ==========================================
        register(new FateTier(10,
                Component.translatable("fate.tier.10.title"),
                Component.translatable("fate.category.neutral"),
                "§e")
                .add((level, player, pos) -> {
                    player.displayClientMessage(Component.literal("§7Кубик замер на ребре... Судьба молчит."), false);
                    level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                })
                .add((level, player, pos) -> {
                    player.getCooldowns().removeCooldown(ModItems.D20_DICE.get());
                    player.displayClientMessage(Component.literal("§eВозврат ставки: кубик перезаряжен мгновенно!"), true);
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.5F);
                })
                .add((level, player, pos) -> {
                    Chicken chicken = EntityType.CHICKEN.create(level);
                    if (chicken != null) {
                        chicken.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                        chicken.setCustomName(Component.literal("§dВестник Пустоты"));
                        level.addFreshEntity(chicken);
                        FateScheduler.schedule(140, () -> {
                            level.sendParticles(ParticleTypes.POOF, chicken.getX(), chicken.getY(), chicken.getZ(), 20, 0.2, 0.2, 0.2, 0.05);
                            chicken.discard();
                        });
                    }
                })
                // 10.D (Галлюцинация): Шепот ветра
                .add((level, player, pos) -> {
                    level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.0F, 0.8F);
                    player.displayClientMessage(Component.literal("§8«Судьба наблюдает за каждым твоим шагом...»"), true);
                })
        );

        // ==========================================
        // 11: МАЛЫЙ БОНУС («Сытный перекус»)
        // ==========================================
        register(new FateTier(11,
                Component.translatable("fate.tier.11.title"),
                Component.translatable("fate.category.minor_bonus"),
                "§a")
                .add((level, player, pos) -> {
                    player.getFoodData().setFoodLevel(20);
                    player.getFoodData().setSaturation(20.0F);
                    dropItem(level, pos, new ItemStack(Items.GOLDEN_APPLE, 1));
                })
                .add((level, player, pos) -> {
                    player.getFoodData().setFoodLevel(20);
                    dropItem(level, pos, new ItemStack(Items.GOLDEN_CARROT, 5));
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 600, 1));
                })
                .add((level, player, pos) -> {
                    dropItem(level, pos, new ItemStack(Items.COOKED_BEEF, 8));
                    dropItem(level, pos, new ItemStack(Items.BAKED_POTATO, 8));
                })
                // 11.D (PvP / Co-op): «Пир на весь мир» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    List<ServerPlayer> friends = FatePlayerUtils.findOtherPlayersInRadius(level, player, 150.0);
                    player.getFoodData().setFoodLevel(20);
                    player.getFoodData().setSaturation(20.0F);
                    dropItem(level, pos, new ItemStack(Items.GOLDEN_APPLE));

                    for (ServerPlayer friend : friends) {
                        friend.getFoodData().setFoodLevel(20);
                        dropItem(level, friend.blockPosition(), new ItemStack(Items.GOLDEN_APPLE));
                        friend.displayClientMessage(Component.literal("§a" + player.getName().getString() + " устроил всеобщий пир! Вам выдано Золотое яблоко!"), false);
                    }
                    if (!friends.isEmpty()) {
                        player.displayClientMessage(Component.literal("§aВы накормили всех вокруг (" + friends.size() + " игроков)!"), false);
                    }
                })
        );

        // ==========================================
        // 12: МАЛЫЙ БОНУС («Спешка разведчика»)
        // ==========================================
        register(new FateTier(12,
                Component.translatable("fate.tier.12.title"),
                Component.translatable("fate.category.minor_bonus"),
                "§a")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 900, 1));
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 800, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 800, 0));
                })
                // 12.C: «Паркур-мастер»
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
                    player.displayClientMessage(Component.literal("§aРежим ниндзя: Скорость + Высокие прыжки + Безопасное падение!"), true);
                })
        );

        // ==========================================
        // 13: МАЛЫЙ БОНУС («Хамелеон»)
        // ==========================================
        register(new FateTier(13,
                Component.translatable("fate.tier.13.title"),
                Component.translatable("fate.category.minor_bonus"),
                "§a")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
                })
                // 13.B (PvP / Co-op): «Маскарад» — обмен предметами в главной руке (радиус 150 блоков)
                .add((level, player, pos) -> {
                    ServerPlayer target = FatePlayerUtils.findNearestOtherPlayer(level, player, 150.0);
                    if (target != null) {
                        ItemStack pItem = player.getMainHandItem().copy();
                        ItemStack tItem = target.getMainHandItem().copy();
                        player.setItemInHand(InteractionHand.MAIN_HAND, tItem);
                        target.setItemInHand(InteractionHand.MAIN_HAND, pItem);

                        level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 1.0F, 1.2F);
                        player.displayClientMessage(Component.literal("§dМаскарад! Вы обменялись удерживаемыми предметами с " + target.getName().getString() + "!"), false);
                        target.displayClientMessage(Component.literal("§dМаскарад! " + player.getName().getString() + " обменялся с вами удерживаемыми предметами!"), false);
                    } else {
                        // Solo fallback: 3 ender pearls
                        dropItem(level, pos, new ItemStack(Items.ENDER_PEARL, 3));
                        player.displayClientMessage(Component.literal("§dВам дарованы 3 жемчужины Края!"), true);
                    }
                })
        );

        // ==========================================
        // 14: МАЛЫЙ БОНУС («Золотые руки»)
        // ==========================================
        register(new FateTier(14,
                Component.translatable("fate.tier.14.title"),
                Component.translatable("fate.category.minor_bonus"),
                "§a")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1));
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 900, 2));
                    ItemStack tool = player.getMainHandItem();
                    if (!tool.isEmpty() && tool.isDamageableItem()) {
                        tool.setDamageValue(Math.max(0, tool.getDamageValue() - 150));
                        player.displayClientMessage(Component.literal("§aВаш инструмент восстановлен!"), true);
                    }
                })
                // 14.C: «Бур»
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 3)); // Haste IV
                    player.displayClientMessage(Component.literal("§6Сверхскоростная копка активирована! (Haste IV)"), true);
                })
        );

        // ==========================================
        // 15: БОНУС («Случайная алхимия»)
        // ==========================================
        register(new FateTier(15,
                Component.translatable("fate.tier.15.title"),
                Component.translatable("fate.category.bonus"),
                "§9")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2400, 0));
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 3600, 0));
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2400, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2400, 1));
                })
                // 15.E (PvP / Co-op): «Аура алхимика» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    List<ServerPlayer> nearby = FatePlayerUtils.findOtherPlayersInRadius(level, player, 150.0);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));

                    for (ServerPlayer p : nearby) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
                        p.displayClientMessage(Component.literal("§bБлагословение алхимии от " + player.getName().getString() + " озарило вас!"), false);
                    }
                    if (!nearby.isEmpty()) {
                        player.displayClientMessage(Component.literal("§bВы поделились мощной защитой с соратниками!"), false);
                    }
                })
        );

        // ==========================================
        // 16: ХОРОШИЙ БОНУС («Второе дыхание»)
        // ==========================================
        register(new FateTier(16,
                Component.translatable("fate.tier.16.title"),
                Component.translatable("fate.category.good_bonus"),
                "§b§l")
                .add((level, player, pos) -> {
                    player.setHealth(player.getMaxHealth());
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 3));
                    level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
                })
                .add((level, player, pos) -> {
                    player.clearFire();
                    player.removeAllEffects();
                    player.setHealth(player.getMaxHealth());
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
                })
                // 16.C (PvP / Co-op): «Клятва исцеления» (радиус 150 блоков)
                .add((level, player, pos) -> {
                    List<ServerPlayer> targets = FatePlayerUtils.findOtherPlayersInRadius(level, player, 150.0);
                    player.setHealth(player.getMaxHealth());
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));

                    for (ServerPlayer ally : targets) {
                        ally.setHealth(ally.getMaxHealth());
                        ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
                        ally.displayClientMessage(Component.literal("§d" + player.getName().getString() + " применил Клятву исцеления! Вы полностью вылечены!"), false);
                    }
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5F, 1.2F);
                })
        );

        // ==========================================
        // 17: ХОРОШИЙ БОНУС («Ясный горизонт»)
        // ==========================================
        register(new FateTier(17,
                Component.translatable("fate.tier.17.title"),
                Component.translatable("fate.category.good_bonus"),
                "§b§l")
                .add((level, player, pos) -> {
                    level.setWeatherParameters(24000, 0, false, false);
                    level.setDayTime(1000);
                    player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 6000, 1));
                })
                // 17.C: «Гравитационный вихрь»
                .add((level, player, pos) -> {
                    AABB vortexBox = player.getBoundingBox().inflate(25.0);
                    List<Monster> monsters = level.getEntitiesOfClass(Monster.class, vortexBox);
                    Vec3 targetCenter = player.position().add(player.getLookAngle().scale(3.0));

                    for (Monster m : monsters) {
                        Vec3 dir = targetCenter.subtract(m.position()).normalize().scale(1.2);
                        m.setDeltaMovement(dir.x, 0.4, dir.z);
                        m.hurtMarked = true;
                    }
                    level.playSound(null, pos, SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.5F, 1.0F);
                    player.displayClientMessage(Component.literal("§bВраги притянуты вихрем прямо перед вами!"), true);
                })
        );

        // ==========================================
        // 18: ОЧЕНЬ ХОРОШИЙ БОНУС («Магнит богатства»)
        // ==========================================
        register(new FateTier(18,
                Component.translatable("fate.tier.18.title"),
                Component.translatable("fate.category.very_good_bonus"),
                "§6§l")
                .add((level, player, pos) -> {
                    transmuteNearbyStone(level, pos, Blocks.DIAMOND_ORE.defaultBlockState(), 4);
                })
                .add((level, player, pos) -> {
                    transmuteNearbyStone(level, pos, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), 5);
                    dropItem(level, pos, new ItemStack(Items.GOLD_INGOT, 5));
                })
                .add((level, player, pos) -> {
                    transmuteNearbyStone(level, pos, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2);
                })
                // 18.D: Изумрудный россыпь
                .add((level, player, pos) -> {
                    transmuteNearbyStone(level, pos, Blocks.EMERALD_ORE.defaultBlockState(), 5);
                    dropItem(level, pos, new ItemStack(Items.EMERALD, 10));
                })
        );

        // ==========================================
        // 19: ОЧЕНЬ ХОРОШИЙ БОНУС («Верный спутник»)
        // ==========================================
        register(new FateTier(19,
                Component.translatable("fate.tier.19.title"),
                Component.translatable("fate.category.very_good_bonus"),
                "§6§l")
                .add((level, player, pos) -> {
                    Horse horse = EntityType.HORSE.create(level);
                    if (horse != null) {
                        horse.moveTo(pos.getX() + 1, pos.getY(), pos.getZ() + 1, 0, 0);
                        horse.tameWithName(player);
                        horse.equipSaddle(new ItemStack(Items.SADDLE), SoundSource.NEUTRAL);
                        horse.setBodyArmorItem(new ItemStack(Items.DIAMOND_HORSE_ARMOR));
                        horse.setCustomName(Component.literal("§6Пегас Судьбы"));
                        level.addFreshEntity(horse);
                    }
                })
                .add((level, player, pos) -> {
                    IronGolem golem = EntityType.IRON_GOLEM.create(level);
                    if (golem != null) {
                        golem.moveTo(pos.getX() + 1, pos.getY(), pos.getZ() + 1, 0, 0);
                        golem.setPlayerCreated(true);
                        golem.setCustomName(Component.literal("§bСтраж Судьбы"));
                        level.addFreshEntity(golem);
                    }
                })
                .add((level, player, pos) -> {
                    for (int i = 0; i < 3; i++) {
                        Wolf wolf = EntityType.WOLF.create(level);
                        if (wolf != null) {
                            wolf.moveTo(pos.getX() + i, pos.getY(), pos.getZ() + i, 0, 0);
                            wolf.tame(player);
                            level.addFreshEntity(wolf);
                        }
                    }
                })
                // 19.D (Веселье): «Бешеное родео»
                .add((level, player, pos) -> {
                    Pig pig = EntityType.PIG.create(level);
                    if (pig != null) {
                        pig.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
                        pig.setCustomName(Component.literal("§dСвинья Формулы-1"));
                        pig.equipSaddle(new ItemStack(Items.SADDLE), SoundSource.NEUTRAL);
                        pig.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 4)); // Speed V
                        level.addFreshEntity(pig);
                        player.startRiding(pig);
                        dropItem(level, pos, new ItemStack(Items.CARROT_ON_A_STICK));
                        player.displayClientMessage(Component.literal("§dБешеное родео! Держитесь крепче!"), true);
                    }
                })
        );

        // ==========================================
        // 20: NATURAL 20 («Благословение Богов»)
        // ==========================================
        register(new FateTier(20,
                Component.translatable("fate.tier.20.title"),
                Component.translatable("fate.category.natural_20"),
                "§6§l★ ")
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    ItemStack elytra = new ItemStack(Items.ELYTRA);
                    var enchReg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                    enchReg.getHolder(Enchantments.UNBREAKING).ifPresent(h -> elytra.enchant(h, 3));
                    enchReg.getHolder(Enchantments.MENDING).ifPresent(h -> elytra.enchant(h, 1));
                    dropItem(level, pos, elytra);
                    dropItem(level, pos, new ItemStack(Items.FIREWORK_ROCKET, 64));
                })
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    dropItem(level, pos, new ItemStack(Items.NETHER_STAR, 1));
                    dropItem(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2));
                })
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
                    sword.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§6§lКлинок Судьбы"));
                    var enchReg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                    enchReg.getHolder(Enchantments.SHARPNESS).ifPresent(h -> sword.enchant(h, 5));
                    enchReg.getHolder(Enchantments.LOOTING).ifPresent(h -> sword.enchant(h, 3));
                    enchReg.getHolder(Enchantments.UNBREAKING).ifPresent(h -> sword.enchant(h, 3));
                    enchReg.getHolder(Enchantments.FIRE_ASPECT).ifPresent(h -> sword.enchant(h, 2));
                    dropItem(level, pos, sword);
                })
                // 20.D (PvP / Co-op): «Королевский банкет» — салюты и баффы ВСЕМ игрокам на сервере!
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    dropItem(level, pos, new ItemStack(Items.TOTEM_OF_UNDYING));
                    dropItem(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

                    for (ServerPlayer other : level.getServer().getPlayerList().getPlayers()) {
                        if (other != player) {
                            other.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                            other.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1));
                            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, other.getX(), other.getY() + 1.0, other.getZ(), 40, 0.5, 0.5, 0.5, 0.2);
                            level.sendParticles(ParticleTypes.FIREWORK, other.getX(), other.getY() + 1.5, other.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
                            other.displayClientMessage(Component.literal("§6★ Игрок " + player.getName().getString() + " выбросил NATURAL 20! Весь мир празднует!"), false);
                        }
                    }
                })
        );
    }

    private static void applyGodlyBuffs(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 1));
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 6000, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
    }

    private static void applyScale(ServerPlayer player, String modifierName, double amount, int durationTicks, String message) {
        AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FateDiceMod.MOD_ID, modifierName);
            scaleAttr.removeModifier(id);
            scaleAttr.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

            player.displayClientMessage(Component.literal(message), true);
            player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.PLAYERS, 1.0F, 1.8F);

            FateScheduler.schedule(durationTicks, () -> {
                AttributeInstance attr = player.getAttribute(Attributes.SCALE);
                if (attr != null) {
                    attr.removeModifier(id);
                    player.displayClientMessage(Component.literal("§aВаш рост вернулся в норму."), true);
                }
            });
        }
    }

    private static void replaceGroundTemporarily(ServerLevel level, BlockPos groundCenter, BlockState newState, int durationTicks) {
        Map<BlockPos, BlockState> originalBlocks = new HashMap<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos target = groundCenter.offset(dx, 0, dz);
                BlockState state = level.getBlockState(target);
                if (!state.isAir() && state.getBlock() != Blocks.BEDROCK && state.isSolid()) {
                    originalBlocks.put(target, state);
                    level.setBlockAndUpdate(target, newState);
                }
            }
        }

        FateScheduler.schedule(durationTicks, () -> {
            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                if (level.getBlockState(entry.getKey()).is(newState.getBlock())) {
                    level.setBlockAndUpdate(entry.getKey(), entry.getValue());
                }
            }
        });
    }

    private static void transmuteNearbyStone(ServerLevel level, BlockPos center, BlockState newState, int maxBlocks) {
        int replaced = 0;
        for (int dx = -3; dx <= 3 && replaced < maxBlocks; dx++) {
            for (int dy = -2; dy <= 2 && replaced < maxBlocks; dy++) {
                for (int dz = -3; dz <= 3 && replaced < maxBlocks; dz++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState current = level.getBlockState(target);
                    if (current.is(Blocks.STONE) || current.is(Blocks.DEEPSLATE) || current.is(Blocks.ANDESITE) || current.is(Blocks.DIORITE) || current.is(Blocks.GRANITE)) {
                        level.setBlockAndUpdate(target, newState);
                        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.0);
                        replaced++;
                    }
                }
            }
        }
    }

    private static void dropItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }
}
