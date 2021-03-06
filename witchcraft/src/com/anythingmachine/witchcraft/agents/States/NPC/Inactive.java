package com.anythingmachine.witchcraft.agents.States.NPC;

import com.anythingmachine.aiengine.Action;
import com.anythingmachine.aiengine.NPCStateMachine;
import com.anythingmachine.witchcraft.WitchCraft;
import com.anythingmachine.witchcraft.GameStates.Containers.GamePlayManager;
import com.anythingmachine.witchcraft.agents.States.Transistions.ActionEnum;
import com.anythingmachine.witchcraft.agents.npcs.NonPlayer;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Inactive extends NPCState {
	public NPCState childState;
	
	public Inactive(NPCStateMachine sm, NPCStateEnum name) {
		super(sm, name);
	}
	
	@Override
	public void update(float dt) {
		aiChoiceTime += dt;

		childState.checkTarget();
		
		if ( sm.me.npctype.canAttack() )
			checkAttack();

		checkInBounds();
		
		checkGround();

		takeAction(dt);
		
		fixCBody();
		
	}

	@Override
	public void setChildState(NPCStateEnum state) {
		childState = sm.getState(state);
	}

	@Override
	public void draw(Batch batch) {

	}
	
	@Override
	public void checkAttack() {
		if (WitchCraft.cam.inBigBounds(sm.phyState.body.getPos())) 
		{
			sm.canseeplayer = sm.facingleft == GamePlayManager.player.getX() < sm.phyState.body
					.getX() && sm.currentNode.getSet() == GamePlayManager.player.getAINode().getSet();
			if (sm.canseeplayer) {
				if (GamePlayManager.player.inHighAlert()) {
					setAttack();
				} else if (GamePlayManager.player.inAlert()) {
					setAlert();
				}
			}
		}
	}

	@Override
	public void checkInLevel() {
		sm.onscreen = true;
		super.checkInLevel();
		sm.onscreen = false;
	}
	
	@Override
	public void setAttack() {
		childState = sm.getState(NPCStateEnum.ATTACKING);
		childState.transistionIn();
	}

	@Override
	public void setAlert() {
		childState = sm.getState(NPCStateEnum.ALARMED);
		childState.transistionIn();
	}

	@Override
	public void checkInBounds() {
		if( WitchCraft.cam.inscaledBounds(sm.phyState.body.getPos())) {
			sm.onscreen = true;
			sm.setState(childState.name);
		}
	}

	@Override
	public void takeAction(float dt) {
		if (aiChoiceTime > sm.behavior.getActionTime()) {
			takeAction(sm.behavior.ChooseAction(childState));
			aiChoiceTime = 0;
		}
	}
	
	@Override
	public void setTalking(NonPlayer npc) {
		sm.npc = npc;
		childState = sm.getState(NPCStateEnum.TALKING);
		childState.transistionIn();
	}
	
	@Override
	public void setIdle() {
		sm.phyState.body.stop();
		childState = sm.getState(NPCStateEnum.IDLE);
		childState.transistionIn();
	}
		
	@Override
	public void takeAction(Action action) {
		if ( action != null )  {
			sm.behavior.takeAction(action);
			childState = sm.getState(action.getAIState());
			childState.transistionIn();
		}
	}
	
	@Override
	public ActionEnum[] getPossibleActions() {
		if ( childState.name != this.name ) {
			return childState.getPossibleActions();
		}else {
			return new ActionEnum[] { };
		}
	}
	
	@Override
	public void setParent(NPCState p) {
		childState = p;
		System.out.println(sm.me.npctype+" "+p.name);
	}
	
	@Override	
	public void transistionIn() {
		sm.onscreen = false;
		if ( childState == null || childState.name == this.name) {
			childState = sm.getState(NPCStateEnum.IDLE);
		}
	}

	@Override	
	public boolean transistionOut() {
		return sm.onscreen;
	}

	@Override
	public void immediateTransOut() {
		
	}

}
