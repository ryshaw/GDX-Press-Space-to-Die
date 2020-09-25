package gdx.press_space;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class Box2DCollision implements ContactListener {
	Player player;
	static Vector2[] manifold = new Vector2[2];

	Box2DCollision(Player p) {
		player = p;
		manifold[0] = new Vector2(0, 0);
		manifold[1] = new Vector2(10, 10);
	}

	@Override
	public void beginContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();
		// lol work on this later
		/*WorldManifold w = contact.getWorldManifold();
		if (a.getClass()  == Player.class || b.getClass() == Player.class) {
			manifold = w.getPoints();
			float y = player.position.y - player.sprite.getWidth() / 2;
			float y1 = manifold[0].y;
			float y2 = manifold[1].y;
			float d1 = Math.abs(y - y1);
			float d2 = Math.abs(y - y2);
			//System.out.println(y - y1);
			//System.out.println(y - y2);
			//System.out.println(y1 < y && y2 < y);
			System.out.println(d1 < 1f && d2 < 1f); // yeah i don't know
		}*/
		if (checkCollisionWithGround(a, b)) {
			player.collisionWithGround();
		}
		if (checkCollisionWithSpikes(a, b)) {
			player.die();
		}
	}

	@Override
	public void endContact(Contact contact) {
		Entity a = (Entity) contact.getFixtureA().getBody().getUserData();
		Entity b = (Entity) contact.getFixtureB().getBody().getUserData();

		if (checkCollisionWithGround(a, b)) {
			player.offTheGround();
		}
	}

	boolean checkCollisionWithGround(Entity a, Entity b) {
		return (a.getClass()  == Player.class || b.getClass() == Player.class)
				&& (a.getClass()  == Ground.class || b.getClass() == Ground.class);
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
