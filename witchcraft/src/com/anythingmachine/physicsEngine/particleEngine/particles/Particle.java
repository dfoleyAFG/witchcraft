package com.anythingmachine.physicsEngine.particleEngine.particles;

import com.anythingmachine.collisionEngine.Entity;
import com.anythingmachine.physicsEngine.particleEngine.ParticleSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Particle extends Entity {
	protected Vector3 pos;
	protected Vector3 vel;
	protected boolean stable;
	protected Vector3 externalForce;
	protected ParticleSystem system;
	protected boolean destroyed = false;
	protected boolean useEuler = true;
	protected float mass = 1f;

	public Particle(Vector3 pos) {
		// this.pos = pos;
		this.pos = new Vector3(pos.x, pos.y, pos.z);
		this.vel = new Vector3(0, 0, 0);
		this.stable = true;
		this.externalForce = new Vector3(0, 0, 0);
	}

	public void destroy() {
		destroyed = true;
	}

	public boolean isEuler() {
		return useEuler;
	}
	
	public Particle setMass(float m) {
		mass = m;
		this.externalForce.y *= m;
		return this;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}

	public Particle(Vector3 pos, Vector3 vel) {
		this.pos = pos;
		this.vel = vel;
	}

	public void draw(Batch batch) {

	}

	@Override
	public void stop() {
		vel.x = 0;
		vel.y = 0;
	}

	@Override
	public void setX(float x) {
		pos.x = x;
	}
	
	@Override
	public void stopOnX() {
		vel.x = 0;
	}

	@Override
	public void stopOnY() {
		vel.y = 0;
	}

	@Override
	public void setY(float y) {
		pos.y = y;
	}
	
	@Override
	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}
	
	@Override
	public void setPos(Vector2 target) {
		pos.x = target.x;
		pos.y = target.y;
	}
	
	public void set3DPos(Vector3 target) {
		pos.x = target.x;
		pos.y = target.y;
	}

	@Override
	public void addPos(float x, float y) {
		this.pos.add(x, y, 0);
	}

	public void apply2DImpulse(float x, float y) {
		this.externalForce.set(x, y, 0);
	}

	public void applyImpulse(Vector3 force) {
		this.externalForce = force;
	}

	public Vector3 accel(Vector3 pos, Vector3 vel, float t) {
		return new Vector3(0, 0, 0);
	}

	public void integratePos(Vector3 dxdp, float dt) {

	}

	public void integrateVel(Vector3 dvdp, float dt) {

	}
	
	public void useEuler(boolean val) {
		this.useEuler = val;
	}

	@Override
	public void setGravityVal(float val) {
		externalForce.y = val;
	}

	@Override
	public void setVel(float x, float y, float z) {
		this.vel.x = x;
		this.vel.y = y;
		this.vel.z = z;
	}

	@Override
	public void setYVel(float y) {
		vel.y = y;
	}

	@Override
	public void setXVel(float x) {
		vel.x = x;
	}

	@Override
	public void addVel(float x, float y, float z) {
		this.vel.x += x;
		this.vel.y += y;
		this.vel.z += z;
	}
	
	public boolean isStable() {
		return stable;
	}

	@Override
	public void setStable(boolean val) {
		stable = val;
	}
	/**
	 * getters
	 * @return
	 */
	public Vector3 getPos() {
		return pos;
	}

	public Vector3 getVel() {
		return vel;
	}

	public void flipXVel() {
		vel.x = -vel.x;
	}
	
	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}
	
	public float getVelX() {
		return vel.x;
	}

	public float getVelY() {
		return vel.y;
	}

}
