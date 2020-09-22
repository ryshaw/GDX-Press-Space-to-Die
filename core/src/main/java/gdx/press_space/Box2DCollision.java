package gdx.press_space;

import com.badlogic.gdx.physics.box2d.*;

public class Box2DCollision implements ContactListener {
	Player player;

	Box2DCollision(Player p) {
		player = p;
	}
	@Override
	public void beginContact(Contact contact) {
		GameActor a = (GameActor) contact.getFixtureA().getBody().getUserData();
		GameActor b = (GameActor) contact.getFixtureB().getBody().getUserData();

		boolean playerGroundCollision = (a == GameActor.PLAYER || b == GameActor.PLAYER)
										&& (a == GameActor.GROUND || b == GameActor.GROUND);

		if (playerGroundCollision) {
			player.collisionWithGround();
		}
	}

	@Override
	public void endContact(Contact contact) {}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
