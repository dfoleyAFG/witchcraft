package com.anythingmachine.witchcraft.agents.States.NPC;

import com.anythingmachine.aiengine.NPCStateMachine;
import com.anythingmachine.witchcraft.Util.Util;

public class Walking extends SharedState {

	public Walking(NPCStateMachine sm, NPCStateEnum name) {
		super(sm, name);
	}

	@Override
	public void transistionIn() {
		super.transistionIn();
		sm.animate.bindPose();
		sm.animate.setCurrent("walk", true);
		if ( !sm.phyState.body.isStable() )
			sm.phyState.body.setVel(sm.facingleft ? -Util.PLAYERWALKSPEED: Util.PLAYERWALKSPEED, 0, 0);
	}
	
	@Override
	public void setWalk() {
		
	}
	
}
