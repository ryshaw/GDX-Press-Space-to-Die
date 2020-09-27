package gdx.press_space;

import com.badlogic.gdx.physics.box2d.*;


public class Box2DCollision implements ContactListener {
	GameScreen screen;
	Player player;

	Box2DCollision(GameScreen s) {
		this.screen = s;
		player = s.player;
	}

	@Override
	public void beginContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

		Artifact artifact = checkCollisionWithArtifact(a, b);
		if (artifact != null && !artifact.collected) {
			screen.collectArtifact(artifact);
			artifact.collected = true;
		}
		else if (checkCollisionWithSpikes(a, b)) player.die();
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

	Artifact checkCollisionWithArtifact(Entity a, Entity b) {
		if ((a.toString().equals("Player")) && b.toString().equals("Artifact")) {
			return (Artifact) b;
		} else if((b.toString().equals("Player")) && a.toString().equals("Artifact")) {
			return (Artifact) a;
		} else {
			return null;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
