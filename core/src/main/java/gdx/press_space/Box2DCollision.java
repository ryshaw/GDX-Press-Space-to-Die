package gdx.press_space;

import com.badlogic.gdx.math.Vector2;
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
		Vector2 p = checkCollisionWithSpawnpoint(a, b);
		if (artifact != null && !artifact.collected) {
			screen.collectArtifact(artifact);
			artifact.collected = true;
		} else if (p != null) player.updateSpawnpoint(p);
		else if (checkCollisionWithSpikes(a, b)) player.die();

		if (playerTouchGround(contact, a, b)) {
			player.numContacts += 1;
		}
	}

	@Override
	public void endContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

		if (playerTouchGround(contact, a, b)) {
			player.numContacts -= 1;
		}
	}

	boolean playerTouchGround(Contact c, Entity a, Entity b) {
		if (a.toString().equals("Player")) {
			return (c.getFixtureA().isSensor() && !c.getFixtureB().isSensor());
		}
		else if (b.toString().equals("Player")) {
			return (c.getFixtureB().isSensor() && !c.getFixtureA().isSensor());
		}
		else return false;
	}

	boolean checkCollisionWithSpikes(Entity a, Entity b) {
		return (a.getClass()  == Player.class || b.getClass() == Player.class)
				&& (a.getClass()  == Spikes.class || b.getClass() == Spikes.class);
	}

	Vector2 checkCollisionWithSpawnpoint(Entity a, Entity b) {
		if ((a.toString().equals("Player")) && b.toString().equals("Spawnpoint")) return b.position;
		else if ((b.toString().equals("Player")) && a.toString().equals("Spawnpoint")) return a.position;
		else return null;
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
