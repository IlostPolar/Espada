package com.popozudos.montante.item;

import com.popozudos.montante.client.MontanteRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MontanteDoCometaItem extends SwordItem implements GeoItem {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation USE = RawAnimation.begin().then("use", Animation.LoopType.PLAY_ONCE);

    // ---- Habilidade: Queda do Cometa ----
    private static final double RANGE = 24.0;        // alcance da mira (blocos)
    private static final double RADIUS = 4.0;        // raio do impacto
    private static final float  DAMAGE = 14.0F;      // 7 coracoes
    private static final int    FIRE_SECONDS = 6;
    private static final int    COOLDOWN_TICKS = 160; // 8 segundos
    private static final int    DURABILITY_COST = 5;
    private static final double SKY_HEIGHT = 18.0;   // altura de onde o cometa cai

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public MontanteDoCometaItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (level instanceof ServerLevel serverLevel) {
            Vec3 eye = player.getEyePosition();
            Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
            BlockHitResult hit = level.clip(new ClipContext(eye, end,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();

            // dano em area
            AABB box = new AABB(target, target).inflate(RADIUS);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive() && !e.isAlliedTo(player));
            for (LivingEntity victim : victims) {
                victim.hurt(player.damageSources().playerAttack(player), DAMAGE);
                victim.setSecondsOnFire(FIRE_SECONDS);
                victim.knockback(0.9D, target.x - victim.getX(), target.z - victim.getZ());
            }

            // rastro do cometa caindo do ceu
            Vec3 sky = target.add(0, SKY_HEIGHT, 0);
            for (int i = 0; i <= 36; i++) {
                Vec3 p = sky.lerp(target, i / 36.0);
                serverLevel.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 2, 0.1, 0.1, 0.1, 0.01);
                serverLevel.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.15, 0.15, 0.15, 0.0);
            }
            // impacto
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, target.x, target.y, target.z, 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.FLAME, target.x, target.y + 0.3, target.z, 60, 1.5, 0.4, 1.5, 0.08);
            serverLevel.sendParticles(ParticleTypes.END_ROD, target.x, target.y + 0.5, target.z, 40, 1.2, 0.8, 1.2, 0.2);
            serverLevel.playSound(null, target.x, target.y, target.z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.2F, 1.3F);
            serverLevel.playSound(null, target.x, target.y, target.z,
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5F, 0.6F);

            // animacao do item (aneis giram rapido)
            triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "controller", "use");

            stack.hurtAndBreak(DURABILITY_COST, player, p -> p.broadcastBreakEvent(hand));
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.montante_do_cometa.lore").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("tooltip.montante_do_cometa.ability").withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    // ---------------- GeckoLib ----------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE))
                .triggerableAnim("use", USE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private MontanteRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new MontanteRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MontanteRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new MontanteRenderer();
                return this.renderer;
            }
        });
    }
}
