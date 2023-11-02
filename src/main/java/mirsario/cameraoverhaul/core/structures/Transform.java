package mirsario.cameraoverhaul.core.structures;

import net.minecraft.util.math.*;

public class Transform
{
	public Vec3d position;
	public Vec3d eulerRot;

	public Transform()
	{
		this.position = new Vec3d(0d, 0d, 0d);
		this.eulerRot = new Vec3d(0d, 0d, 0d);
	}

	public Transform(Vec3d position, Vec3d eulerRot)
	{
		this.position = position;
		this.eulerRot = eulerRot;
	}
}