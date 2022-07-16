package util.components;

import physics.body.Body;

public class BodyComponent extends GameComponent {
	
	Body body;
	
	public BodyComponent(Body body)
	{
		this.body = body;
	}
	public void onAdd()
	{
		body.transform = parent.transform; //binds the GameObject and body together
		parent.world.physics_world.addBody(body);
	}
	public void onRemove()
	{
		parent.world.physics_world.removeBody(body);
	}
	public void onUpdate()
	{
		body.shape.projectTo(parent.transform, body.shape_proj);
	}

}
