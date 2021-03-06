package com.anythingmachine.physicsEngine.particleEngine.particles;

import com.anythingmachine.witchcraft.Util.Util;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

public class KinematicParticle extends Particle {
	
	public KinematicParticle (Vector3 pos, float gravityval) {
		super(pos);		
		this.externalForce.y = gravityval;
		this.stable = false;
	}
	
	@Override
	public void draw(Batch batch) {
		
	}
	
	@Override
	public Vector3 accel(Vector3 pos, Vector3 vel, float t) {
		return externalForce;
	}

	@Override
	public void integratePos(Vector3 dxdp, float dt) {
		Vector3 ds = Util.sclVec(dxdp, dt);
		this.pos.x += ds.x;
		this.pos.y += ds.y;
		this.pos.z += ds.z;
	}

	@Override
	public void integrateVel(Vector3 dvdp, float dt) {
		Vector3 ds = Util.sclVec(dvdp, dt);
		this.vel.x += ds.x;
		this.vel.y += ds.y;
		this.vel.z += ds.z;
	}
}
