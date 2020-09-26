package gdx.press_space;

import com.badlogic.gdx.physics.box2d.*;


public class Box2DCollision implements ContactListener {
	Player player;

	Box2DCollision(Player p) {
		player = p;
	}

	@Override
	public void beginContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

		if (checkCollisionWithSpikes(a, b)) player.die();
		else if (playerCollision(contact, a, b)) player.collisionWithGround();
	}

	@Override
	public void endContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

		if (playerCollision(contact, a, b)) player.offTheGround();
	}

	boolean playerCollision(Contact c, Entity a, Entity b) {
		if (a.toString().equals("Player")) return c.getFixtureA().isSensor();
		else if (b.toString().equals("Player")) return c.getFixtureB().isSensor();
		else return false;
	}

	boolean checkCollisionWithSpikes(Entity a, Entity b) {
		return (a.getClass()  == Player.class || b.getClass() == Player.class)
				&& (a.getClass()  == Spikes.class || b.getClass() == Spikes.class);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
