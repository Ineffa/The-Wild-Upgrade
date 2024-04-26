package com.ineffa.wondrouswilds.entities.ai.navigation;

import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;

public class LeapPathNodeNavigator extends PathNodeNavigator implements ModifiesSuccessorsCapacity {

    public LeapPathNodeNavigator(LeapPathNodeMaker pathNodeMaker, int range) {
        super(pathNodeMaker, range);
    }

    private final PathNode[] leapSuccessors = new PathNode[1320];

    @Override
    public PathNode[] getSuccessorArray() {
        return this.leapSuccessors;
    }
}