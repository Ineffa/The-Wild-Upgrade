package com.ineffa.wondrouswilds.items;

import com.ineffa.wondrouswilds.registry.WondrousWildsItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class BycocketItem extends Item implements Wearable, IAnimatable {

    public final Flair flair;

    public BycocketItem(Flair flair) {
        super(new FabricItemSettings().maxCount(1).group(WondrousWildsItems.WONDROUS_WILDS_ITEM_GROUP));
        this.flair = flair;

        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        EquipmentSlot equipmentSlot = LivingEntity.getPreferredEquipmentSlot(itemStack);
        if (user.getEquippedStack(equipmentSlot).isEmpty()) {
            user.equipStack(equipmentSlot, itemStack.copy());
            if (!world.isClient()) user.incrementStat(Stats.USED.getOrCreateStat(this));
            itemStack.setCount(0);
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    @Nullable
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    public enum Flair {
        WOODPECKER("woodpecker");

        public final String name;

        Flair(String name) {
            this.name = name;
        }
    }
}
