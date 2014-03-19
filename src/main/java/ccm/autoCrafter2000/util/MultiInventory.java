/*
 * The MIT License (MIT)
 *
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

package ccm.autoCrafter2000.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Thanks for this Buildcraft.
 * Makes managing multiple inventories in block easy.
 *
 * @see ccm.autoCrafter2000.tile.AutoCrafterTile#multiInventory
 * @author Dries007
 */
public class MultiInventory implements IInventory
{
    private final ArrayList<Integer>    slotList = new ArrayList<Integer>();
    private final ArrayList<IInventory> invList  = new ArrayList<IInventory>();

    public MultiInventory(IInventory... inventories)
    {
        for (IInventory iInventory : inventories) add(iInventory);
    }

    private void add(IInventory iInventory)
    {
        for (int slot = 0; slot < iInventory.getSizeInventory(); slot++)
        {
            slotList.add(slot);
            invList.add(iInventory);
        }
    }

    @Override
    public int getSizeInventory()
    {
        return slotList.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return invList.get(slot).getStackInSlot(slotList.get(slot));
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        return invList.get(slot).decrStackSize(slotList.get(slot), amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return invList.get(slot).getStackInSlotOnClosing(slotList.get(slot));
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        invList.get(slot).setInventorySlotContents(slotList.get(slot), stack);
    }

    @Override
    public String getInvName()
    {
        return "";
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void onInventoryChanged()
    {
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
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return invList.get(slot).isItemValidForSlot(slotList.get(slot), stack);
    }
}
