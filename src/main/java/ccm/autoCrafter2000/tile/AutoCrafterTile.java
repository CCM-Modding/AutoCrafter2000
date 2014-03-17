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

import ccm.autoCrafter2000.util.Helper;
import ccm.autoCrafter2000.util.MultiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class AutoCrafterTile extends TileEntity implements ISidedInventory
{
    public static final String INV_RESULT     = "result";
    public static final String INV_MATRIX     = "matrix";
    public static final String INV_IN         = "in";
    public static final String INV_OUT        = "out";

    public static final int   SLOT_OUT     = 0;
    public static final int   MATRIX       = 3 * 3;
    public static final int[] SLOTS_MATRIX = Helper.slotArray(SLOT_OUT, MATRIX);
    public static final int   IN           = 3 * 3;
    public static final int   OUT          = 3 * 3;
    public static final int[] SLOTS_IN     = Helper.slotArray(SLOTS_MATRIX.length + 1, IN);
    public static final int[] SLOTS_OUT    = Helper.slotArray(SLOTS_MATRIX.length + 1 + IN, OUT);
    public static final int[] SLOTS_IO     = Helper.slotArray(SLOTS_MATRIX.length + 1, IN + OUT);

    public InventoryCraftResult inventoryCraftResult = new InventoryCraftResult();
    public InventoryCrafting    inventoryMatrix      = Helper.newCraftingMatrix(MATRIX, 1);

    public InventoryBasic inventoryIn  = new InventoryBasic("AutoCrafter_in", true, IN);
    public InventoryBasic inventoryOut = new InventoryBasic("AutoCrafter_out", true, OUT);

    public MultiInventory multiInventory = new MultiInventory(inventoryCraftResult, inventoryMatrix, inventoryIn, inventoryOut);
    public IRecipe recipe;
    private List<ItemStack> requiredItems;
    private int tick = 0;

    public AutoCrafterTile()
    {

    }

    public AutoCrafterTile(World world)
    {
        setWorldObj(world);
    }

    @Override
    public int getSizeInventory()
    {
        return multiInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return multiInventory.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        return multiInventory.decrStackSize(i, j);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return multiInventory.getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        if (i == SLOT_OUT) updateRecipe();
        multiInventory.setInventorySlotContents(i, itemstack);
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
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return true;
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
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        Helper.readInvFromNBT(inventoryCraftResult, INV_RESULT, data);
        Helper.readInvFromNBT(inventoryMatrix, INV_MATRIX, data);
        Helper.readInvFromNBT(inventoryIn, INV_IN, data);
        Helper.readInvFromNBT(inventoryOut, INV_OUT, data);

        updateRecipe();
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        Helper.writeInvToNBT(inventoryCraftResult, INV_RESULT, data);
        Helper.writeInvToNBT(inventoryMatrix, INV_MATRIX, data);
        Helper.writeInvToNBT(inventoryIn, INV_IN, data);
        Helper.writeInvToNBT(inventoryOut, INV_OUT, data);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (worldObj.isRemote) return;

        tick++;
        if (tick % 5 != 0) return;
        tick = 0;

        if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return;

        if (recipe != null && requiredItems != null)
        {
            List<ItemStack> input = Helper.getList(inventoryIn);
            if (Helper.containsEnoughAll(input, requiredItems) && Helper.hasSpaceFor(inventoryOut, getOutput()))
            {
                Helper.removeFromInventory(inventoryIn, requiredItems);
                Helper.addToInventory(inventoryOut, getOutput());
            }
        }
    }

    public void updateRecipe()
    {
        recipe = Helper.findMatchingRecipe(inventoryMatrix, worldObj);
        if (recipe != null)
        {
            requiredItems = Helper.getList(inventoryMatrix);
        }
        else
        {
            requiredItems = null;
        }
    }

    public ItemStack getOutput()
    {
        if (recipe == null) return null;
        return recipe.getRecipeOutput().copy();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        multiInventory.isItemValidForSlot(slot, stack);
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        return SLOTS_IO;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        for (int i : SLOTS_IN) if (i == slot) return isItemValidForSlot(slot, stack);
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        for (int i : SLOTS_OUT) if (i == slot) return true;
        return false;
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }
}