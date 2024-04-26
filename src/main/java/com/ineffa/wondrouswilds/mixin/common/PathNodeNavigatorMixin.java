package com.ineffa.wondrouswilds.mixin.common;

import com.ineffa.wondrouswilds.entities.ai.navigation.ModifiesSuccessorsCapacity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PathNodeNavigator.class)
public class PathNodeNavigatorMixin {

    @Shadow @Final private PathNode[] successors;

    @Redirect(method = "findPathToAny(Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/entity/ai/pathing/PathNode;Ljava/util/Map;FIF)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/pathing/PathNodeNavigator;successors:[Lnet/minecraft/entity/ai/pathing/PathNode;"))
    private PathNode[] swapSuccessorArray(PathNodeNavigator navigator) {
        if (this instanceof ModifiesSuccessorsCapacity modifiesSuccessorsCapacity) return modifiesSuccessorsCapacity.getSuccessorArray();
        return this.successors;
    }
}
