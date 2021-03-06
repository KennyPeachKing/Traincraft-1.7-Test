package train.common.mtc;


import cpw.mods.fml.common.network.NetworkRegistry;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import train.common.Traincraft;
import train.common.api.Locomotive;
import train.common.entity.rollingStock.EntityLocoElectricPeachDriverlessMetro;
import train.common.mtc.packets.PacketATOSetStopPoint;

public class TileATOTransmitterStopPoint extends TileEntity implements IPeripheral {

   public Boolean isActivated = Boolean.valueOf(false);
   public double stopX = 0.0;
   public double stopY = 0.0D;
   public double stopZ = 0.0D;
   public String nextStation = "";
   public AxisAlignedBB boundingBox = null;


   public void updateEntity() {
      if(worldObj != null) {
         if(this.isActivated) {
            List<Object> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, this.getRenderBoundingBox());
            Iterator var2 = list.iterator();

            while(var2.hasNext()) {
               Object obj = var2.next();
               if(obj instanceof Locomotive) {
                  Locomotive daTrain = (Locomotive)obj;
                   if (daTrain.mtcOverridePressed) { return;}
                  if(daTrain.mtcStatus == 1 |daTrain.mtcStatus == 2  ) {
                     if(this.stopX == 0) {
                        return;
                     }
                     if (daTrain instanceof EntityLocoElectricPeachDriverlessMetro) {
                        ((EntityLocoElectricPeachDriverlessMetro)daTrain).nextStation = nextStation;
                     }
                     daTrain.xFromStopPoint = this.stopX;
                     daTrain.yFromStopPoint = this.stopY;
                     daTrain.zFromStopPoint = this.stopZ;
                     Traincraft.atoSetStopPoint.sendToAllAround(new PacketATOSetStopPoint(daTrain.getEntityId(), Double.valueOf(this.stopX), Double.valueOf(this.stopY), Double.valueOf(this.stopZ)) , new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, daTrain.posX, daTrain.posY, daTrain.posZ, 150.0D));
                  }
               }
            }
         }

      }
   }

   public String getType() {
      return "ato_stoppoint_transmitter";
   }

   public String[] getMethodNames() {
      return new String[]{"activate", "deactivate", "setX", "setY", "setZ", "nextStation"};
   }

   public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
      switch(method) {
      case 0:
         this.isActivated = Boolean.valueOf(true);
         return new Object[]{Boolean.valueOf(true)};
      case 1:
         this.isActivated = Boolean.valueOf(false);
         return new Object[]{Boolean.valueOf(true)};
      case 2:
         if (arguments[0] instanceof Double) { this.stopX = Double.parseDouble(arguments[0].toString()); } else { return new Object[] {"nil"};}
         return new Object[]{Boolean.TRUE};
      case 3:
         if (arguments[0] instanceof Double) { this.stopY = Double.parseDouble(arguments[0].toString()); } else { return new Object[] {"nil"};}
         return new Object[]{Boolean.TRUE};
      case 4:
        if (arguments[0] instanceof Double) { this.stopZ = Double.parseDouble(arguments[0].toString()); } else { return new Object[] {"nil"};}
         return new Object[]{Boolean.TRUE};
         case 5: {
            if (arguments[0] instanceof String) { this.nextStation = arguments[0].toString();}
            return new Object[]{Boolean.TRUE};
         }
      default:
         return new Object[]{"nil"};
      }
   }

   public void attach(IComputerAccess computer) {}

   public void detach(IComputerAccess computer) {}

   public boolean equals(IPeripheral other) {
      return false;
   }

   @Override
   public AxisAlignedBB getRenderBoundingBox() {
      if (boundingBox == null) {
         boundingBox = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 2, yCoord + 2, zCoord + 2);
      }
      return boundingBox;
   }

   @Override
   public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      this.isActivated = nbttagcompound.getBoolean("isActivated");
      this.stopX = nbttagcompound.getDouble("stopX");
      this.stopY = nbttagcompound.getDouble("stopY");
      this.stopZ = nbttagcompound.getDouble("stopZ");
      this.nextStation = nbttagcompound.getString("nextStation");
   }

   @Override
   public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      nbttagcompound.setBoolean("isActivated", this.isActivated);
      nbttagcompound.setDouble("stopX", this.stopX);
      nbttagcompound.setDouble("stopY", this.stopY);
      nbttagcompound.setDouble("stopZ", this.stopZ);
      nbttagcompound.setString("nextStation", this.nextStation);
   }
}
