/*
* Class by GravelCZLP
* 
* Copyright 2016 GravelCZLP
*
* All Rights Reserved
*/

package cz.GravelCZLP.Utils;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.AxisAlignedBB;
/*
 * stolen from 
 * https://www.spigotmc.org/threads/hitboxes-and-ray-tracing.174358/
 */
public class RayTrace {

	Vector origin, direction;
	
    RayTrace(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
	
   public Vector getPostion(double away) {
	   return origin.clone().add(direction).clone().multiply(away);
   }
   
   public boolean isOnLine(Vector position) {
       double t = (position.getX() - origin.getX()) / direction.getX();
       ;
       if (position.getBlockY() == origin.getY() + (t * direction.getY()) && position.getBlockZ() == origin.getZ() + (t * direction.getZ())) {
           return true;
       }
       return false;
   }

   //get all postions on a raytrace
   public ArrayList<Vector> traverse(double blocksAway, double accuracy) {
       ArrayList<Vector> positions = new ArrayList<>();
       for (double d = 0; d <= blocksAway; d += accuracy) {
           positions.add(getPostion(d));
       }
       return positions;
   }

   //intersection detection for current raytrace with return
   public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
       ArrayList<Vector> positions = traverse(blocksAway, accuracy);
       for (Vector position : positions) {
           if (intersects(position, min, max)) {
               return position;
           }
       }
       return null;
   }

   //intersection detection for current raytrace
   public boolean intersects(Vector min, Vector max, double blocksAway, double accuracy) {
       ArrayList<Vector> positions = traverse(blocksAway, accuracy);
       for (Vector position : positions) {
           if (intersects(position, min, max)) {
               return true;
           }
       }
       return false;
   }

   //bounding box instead of vector
   public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy) {
       ArrayList<Vector> positions = traverse(blocksAway, accuracy);
       for (Vector position : positions) {
           if (intersects(position, boundingBox.min, boundingBox.max)) {
               return position;
           }
       }
       return null;
   }

   //bounding box instead of vector
   public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
       ArrayList<Vector> positions = traverse(blocksAway, accuracy);
       for (Vector position : positions) {
           if (intersects(position, boundingBox.min, boundingBox.max)) {
               return true;
           }
       }
       return false;
   }

   //general intersection detection
   public static boolean intersects(Vector position, Vector min, Vector max) {
       if (position.getX() < min.getX() || position.getX() > max.getX()) {
           return false;
       } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
           return false;
       } else if (position.getZ() < min.getZ() || position.getZ() > max.getZ()) {
           return false;
       }
       return true;
   }

   //debug / effects
   public void highlight(World world, double blocksAway, double accuracy){
       for(Vector position : traverse(blocksAway,accuracy)){
           world.playEffect(position.toLocation(world), Effect.COLOURED_DUST, 0);
       }
   }
   
   public static class BoundingBox {
	    //min and max points of hit box
	    Vector max;
	    Vector min;

	    BoundingBox(Vector min, Vector max) {
	        this.max = max;
	        this.min = min;
	    }

	    BoundingBox(Block block) {
	        net.minecraft.server.v1_10_R1.BlockPosition bp = new net.minecraft.server.v1_10_R1.BlockPosition(block.getX(), block.getY(), block.getZ());
	        net.minecraft.server.v1_10_R1.WorldServer world = ((org.bukkit.craftbukkit.v1_10_R1.CraftWorld) block.getWorld()).getHandle();
	        net.minecraft.server.v1_10_R1.IBlockData blockData = (net.minecraft.server.v1_10_R1.IBlockData) (world.getType(bp));
	        net.minecraft.server.v1_10_R1.Block blockNative = blockData.getBlock();
	        @SuppressWarnings("deprecation")
			net.minecraft.server.v1_10_R1.AxisAlignedBB aabb = blockNative.a(blockData, world, bp);
	        min = new Vector(aabb.a, aabb.b, aabb.c);
	        max = new Vector(aabb.d, aabb.e, aabb.f);
	    }

	    BoundingBox(Entity entity){
	        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
	        min = new Vector(bb.a,bb.b,bb.c);
	        max = new Vector(bb.d,bb.e,bb.f);
	    }

	    BoundingBox (AxisAlignedBB bb){
	        min = new Vector(bb.a,bb.b,bb.c);
	        max = new Vector(bb.d,bb.e,bb.f);
	    }

	    public Vector midPoint(){
	        return max.clone().add(min).multiply(0.5);
	    }
   }
}
