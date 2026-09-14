package com.fatedice.fate;

import com.fatedice.FateDiceMod;
import com.fatedice.config.FateConfig;
import com.fatedice.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
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
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
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
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 3)); // Mining Fatigue IV (30s)
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0)); // Blindness (30s)

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
                        // Make charged creeper via lightning strike directly on it
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
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 9)); // Levitation X for 2s
                    level.playSound(null, pos, SoundEvents.GHAST_WARN, SoundSource.PLAYERS, 1.0F, 0.5F);
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
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0)); // Levitation I (5s)

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
                    player.setTicksFrozen(240); // Freeze effect
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1)); // Slowness II
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
                    Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
                    BlockPos groundCenter = player.blockPosition().below();

                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            BlockPos target = groundCenter.offset(dx, 0, dz);
                            BlockState state = level.getBlockState(target);
                            if (!state.isAir() && state.getBlock() != Blocks.BEDROCK && state.isSolid()) {
                                originalBlocks.put(target, state);
                                level.setBlockAndUpdate(target, Blocks.COBWEB.defaultBlockState());
                            }
                        }
                    }

                    // Revert blocks after 15 seconds (300 ticks)
                    FateScheduler.schedule(300, () -> {
                        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                            if (level.getBlockState(entry.getKey()).is(Blocks.COBWEB)) {
                                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
                            }
                        }
                    });
                })
                .add((level, player, pos) -> {
                    Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
                    BlockPos groundCenter = player.blockPosition().below();

                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            BlockPos target = groundCenter.offset(dx, 0, dz);
                            BlockState state = level.getBlockState(target);
                            if (!state.isAir() && state.getBlock() != Blocks.BEDROCK && state.isSolid()) {
                                originalBlocks.put(target, state);
                                level.setBlockAndUpdate(target, Blocks.SOUL_SAND.defaultBlockState());
                            }
                        }
                    }

                    FateScheduler.schedule(300, () -> {
                        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                            if (level.getBlockState(entry.getKey()).is(Blocks.SOUL_SAND)) {
                                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
                            }
                        }
                    });
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
                    AttributeInstance scaleAttr = player.getAttribute(Attributes.SCALE);
                    if (scaleAttr != null) {
                        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FateDiceMod.MOD_ID, "micro_scale");
                        scaleAttr.removeModifier(id); // Clean any previous
                        scaleAttr.addTransientModifier(new AttributeModifier(id, -0.70, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); // 0.3x

                        player.displayClientMessage(Component.literal("§cВы сжались до размера мыши (0.3x)!"), true);
                        level.playSound(null, pos, SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.PLAYERS, 1.0F, 1.8F);

                        // Revert scale back after 60 seconds (1200 ticks)
                        FateScheduler.schedule(1200, () -> {
                            AttributeInstance attr = player.getAttribute(Attributes.SCALE);
                            if (attr != null) {
                                attr.removeModifier(id);
                                player.displayClientMessage(Component.literal("§aВаш рост вернулся в норму."), true);
                            }
                        });
                    }
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
        );

        // ==========================================
        // 8: НЕПРИЯТНОСТЬ («Безумная гравитация»)
        // ==========================================
        register(new FateTier(8,
                Component.translatable("fate.tier.8.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    // Alternating Levitation and Slow Falling
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
        );

        // ==========================================
        // 9: НЕПРИЯТНОСТЬ («Проклятие слабости»)
        // ==========================================
        register(new FateTier(9,
                Component.translatable("fate.tier.9.title"),
                Component.translatable("fate.category.misfortune"),
                "§c")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1)); // Weakness II (60s)
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1)); // Slowness II (60s)
                    player.getFoodData().setFoodLevel(Math.min(player.getFoodData().getFoodLevel(), 2));
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
        );

        // ==========================================
        // 12: МАЛЫЙ БОНУС («Спешка разведчика»)
        // ==========================================
        register(new FateTier(12,
                Component.translatable("fate.tier.12.title"),
                Component.translatable("fate.category.minor_bonus"),
                "§a")
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1)); // Speed II (45s)
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 900, 1)); // Jump Boost II (45s)
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 2)); // Speed III (40s)
                    player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 800, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 800, 0));
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
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200, 0)); // Invisibility 60s
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
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
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1)); // Haste II (2 min)
                })
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 900, 2)); // Haste III (45s)
                    ItemStack tool = player.getMainHandItem();
                    if (!tool.isEmpty() && tool.isDamageableItem()) {
                        tool.setDamageValue(Math.max(0, tool.getDamageValue() - 150));
                        player.displayClientMessage(Component.literal("§aВаш инструмент восстановлен!"), true);
                    }
                })
        );

        // ==========================================
        // 15: БОНУС («Случайная алхимия»)
        // ==========================================
        register(new FateTier(15,
                Component.translatable("fate.tier.15.title"),
                Component.translatable("fate.category.bonus"),
                "§9")
                // Сет "Боец"
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, 1)); // Strength II
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0)); // Resistance I
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2400, 0)); // Regen I
                })
                // Сет "Пещерный житель"
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 3600, 0));
                })
                // Сет "Ветрокрылый"
                .add((level, player, pos) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2400, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2400, 1));
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
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 3)); // Absorption IV (8 hearts)
                    level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
                })
                .add((level, player, pos) -> {
                    player.clearFire();
                    player.removeAllEffects();
                    player.setHealth(player.getMaxHealth());
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2)); // Regen III (20s)
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
                    level.setDayTime(1000); // Dawn
                    player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 6000, 1));
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
                    // Алмазная жила
                    transmuteNearbyStone(level, pos, Blocks.DIAMOND_ORE.defaultBlockState(), 4);
                })
                .add((level, player, pos) -> {
                    // Золотая жила + слитки
                    transmuteNearbyStone(level, pos, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), 5);
                    dropItem(level, pos, new ItemStack(Items.GOLD_INGOT, 5));
                })
                .add((level, player, pos) -> {
                    // Древний обломок
                    transmuteNearbyStone(level, pos, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2);
                })
        );

        // ==========================================
        // 19: ОЧЕНЬ ХОРОШИЙ БОНУС («Верный спутник»)
        // ==========================================
        register(new FateTier(19,
                Component.translatable("fate.tier.19.title"),
                Component.translatable("fate.category.very_good_bonus"),
                "§6§l")
                // Боевой скакун
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
                // Железный голем
                .add((level, player, pos) -> {
                    IronGolem golem = EntityType.IRON_GOLEM.create(level);
                    if (golem != null) {
                        golem.moveTo(pos.getX() + 1, pos.getY(), pos.getZ() + 1, 0, 0);
                        golem.setPlayerCreated(true);
                        golem.setCustomName(Component.literal("§bСтраж Судьбы"));
                        level.addFreshEntity(golem);
                    }
                })
                // Стая волков
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
        );

        // ==========================================
        // 20: NATURAL 20 («Благословение Богов»)
        // ==========================================
        register(new FateTier(20,
                Component.translatable("fate.tier.20.title"),
                Component.translatable("fate.category.natural_20"),
                "§6§l★ ")
                // 20.A «Дар Небес» (Элитры + Салюты)
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    ItemStack elytra = new ItemStack(Items.ELYTRA);
                    var enchReg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                    enchReg.getHolder(Enchantments.UNBREAKING).ifPresent(h -> elytra.enchant(h, 3));
                    enchReg.getHolder(Enchantments.MENDING).ifPresent(h -> elytra.enchant(h, 1));
                    dropItem(level, pos, elytra);
                    dropItem(level, pos, new ItemStack(Items.FIREWORK_ROCKET, 64));
                })
                // 20.B «Сердце Бездны» (Звезда Незера + Яблоки Нотча)
                .add((level, player, pos) -> {
                    applyGodlyBuffs(player);
                    dropItem(level, pos, new ItemStack(Items.NETHER_STAR, 1));
                    dropItem(level, pos, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2));
                })
                // 20.C «Оружие Судьбы» (Легендарный меч)
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
        );
    }

    private static void applyGodlyBuffs(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 2)); // Strength III (5 min)
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 1)); // Resistance II (5 min)
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 6000, 0)); // Glowing (5 min)
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)); // Regen II (1 min)
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
