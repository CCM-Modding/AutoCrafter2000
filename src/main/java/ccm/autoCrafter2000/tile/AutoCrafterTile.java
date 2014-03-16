/*
 * Copyright (c) 2014 Dries K. Aka Dries007 and the CCM modding crew.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ccm.autoCrafter2000.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AutoCrafterTile extends TileEntity implements ISidedInventory
{
    public static final int   SIDE_OUT  = 0;
    public static final int   SLOT_OUT  = 0;
    public static final int[] SLOTS_OUT = {0};
    public static final int[] SLOTS_INV = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int   SIZE      = SLOTS_INV.length + SLOTS_OUT.length;

    ItemStack[] inv = new ItemStack[SIZE];

    public AutoCrafterTile()
    {

    }

    public AutoCrafterTile(World world)
    {
        setWorldObj(world);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        switch (var1)
        {
            case SIDE_OUT:
                return SLOTS_OUT;
            default:
                return SLOTS_INV;
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side)
    {
        if (slot == SLOT_OUT && side == SIDE_OUT) return false;
        return true;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side)
    {
        if (slot == SLOT_OUT && side == SIDE_OUT) return false;
        return true;
    }

    @Override
    public int getSizeInventory()
    {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return inv[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {

    }

    @Override
    public String getInvName()
    {
        return "AutoCrafter";
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openChest()
    {

    }

    @Override
    public void closeChest()
    {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
    }
}