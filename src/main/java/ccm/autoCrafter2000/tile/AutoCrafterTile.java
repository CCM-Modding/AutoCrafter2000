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

import ccm.autoCrafter2000.util.MultiInventory;
import ccm.nucleumOmnium.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is where the magic happens.
 * Thanks to Buildcraft for some handy code.
 *
 * @author Dries007
 */
public class AutoCrafterTile extends TileEntity implements ISidedInventory
{
    // NBT data
    public static final String INV_RESULT = "result";
    public static final String INV_MATRIX = "matrix";
    public static final String INV_IN     = "in";
    public static final String INV_OUT    = "out";

    // Slots
    public static final int   SLOT_OUT     = 0;
    public static final int   MATRIX       = 3 * 3;
    public static final int[] SLOTS_MATRIX = InventoryHelper.slotArray(SLOT_OUT, MATRIX);
    public static final int   IN           = 3 * 3;
    public static final int   OUT          = 3 * 3;
    public static final int[] SLOTS_IN     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN);
    public static final int[] SLOTS_OUT    = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1 + IN, OUT);
    public static final int[] SLOTS_IO     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN + OUT);

    // Inventories this block is made out of, the multi one is used for the ISidedInventory
    public InventoryCraftResult inventoryCraftResult = new InventoryCraftResult();
    public InventoryCrafting    inventoryMatrix      = InventoryHelper.newCraftingMatrix(MATRIX, 1);
    public InventoryCrafting    inventoryIn          = InventoryHelper.newCraftingMatrix(MATRIX, 64);
    public InventoryBasic       inventoryOut         = new InventoryBasic("AutoCrafter_out", true, OUT);
    public MultiInventory       multiInventory       = new MultiInventory(inventoryCraftResult, inventoryMatrix, inventoryIn, inventoryOut);

    // Other variables
    public IRecipe          recipe;
    private int             tick = 0;
    public InternalPlayer   internalPlayer;
    public SlotCrafting     craftSlot;
    public List<ItemStack>  overflow = new LinkedList<ItemStack>();

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (worldObj.isRemote) return;

        // Initialize code
        if (craftSlot == null)
        {
            internalPlayer = new InternalPlayer();
            craftSlot = new SlotCrafting(internalPlayer, inventoryIn, inventoryOut, xCoord, yCoord, zCoord);
        }

        // Lower tick rate
        tick++;
        if (tick % 5 != 0) return;
        tick = 0;

        // If powered, do nothing.
        if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return;

        // Deal with overflow. If we couldn't empty, don't make new stuff.
        emptyOverflow();
        if (!overflow.isEmpty()) return;

        // Matches recipes and handle the crafting.
        if (recipe != null && recipe.matches(inventoryIn, worldObj))
        {
            ItemStack result = recipe.getCraftingResult(inventoryIn);
            if (result == null) return;
            result = result.copy();

            if (InventoryHelper.hasSpaceFor(inventoryOut, result))
            {
                craftSlot.onPickupFromSlot(internalPlayer, result);

                ItemStack stack = InventoryHelper.addToInventory(inventoryOut, result);
                if (stack != null) overflow.add(stack);

                for (int i = 0; i < internalPlayer.inventory.getSizeInventory(); i++)
                {
                    stack = InventoryHelper.addToInventory(inventoryOut, internalPlayer.inventory.getStackInSlotOnClosing(i));
                    if (stack != null) overflow.add(stack);
                }
            }
        }
    }

    private void emptyOverflow()
    {
        Iterator<ItemStack> iterator = overflow.iterator();

        while (iterator.hasNext())
        {
            ItemStack stack = iterator.next();
            if (InventoryHelper.hasSpaceFor(inventoryOut, stack))
            {
                InventoryHelper.addToInventory(inventoryOut, stack);
                iterator.remove();
            }
        }
    }

    public void updateRecipe()
    {
        recipe = InventoryHelper.findMatchingRecipe(inventoryMatrix, worldObj);
    }

    /**
     * If slot is an input slot, check to see if it matches the recipe.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        for (int i : SLOTS_IN) if (i == slot) return InventoryHelper.canStacksMerge(stack, multiInventory.getStackInSlot(i - IN), false);
        return multiInventory.isItemValidForSlot(slot, stack);
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

    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    /**
     * I hate fake players myself but here is no better way.
     */
    private final class InternalPlayer extends EntityPlayer
    {
        public InternalPlayer()
        {
            super(AutoCrafterTile.this.worldObj, "[AutoCrafterTile]");
            posX = AutoCrafterTile.this.xCoord;
            posY = AutoCrafterTile.this.yCoord + 1;
            posZ = AutoCrafterTile.this.zCoord;
        }

        @Override
        public void sendChatToPlayer(ChatMessageComponent var1) {}

        @Override
        public boolean canCommandSenderUseCommand(int var1, String var2)
        {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates()
        {
            return null;
        }
    }


    /**
     * Start boring interface / TE code
     */
    public AutoCrafterTile() {}

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
    public void openChest() {}

    @Override
    public void closeChest() {}

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        InventoryHelper.readInvFromNBT(inventoryCraftResult, INV_RESULT, data);
        InventoryHelper.readInvFromNBT(inventoryMatrix, INV_MATRIX, data);
        InventoryHelper.readInvFromNBT(inventoryIn, INV_IN, data);
        InventoryHelper.readInvFromNBT(inventoryOut, INV_OUT, data);

        updateRecipe(); // Must update after load.
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        InventoryHelper.writeInvToNBT(inventoryCraftResult, INV_RESULT, data);
        InventoryHelper.writeInvToNBT(inventoryMatrix, INV_MATRIX, data);
        InventoryHelper.writeInvToNBT(inventoryIn, INV_IN, data);
        InventoryHelper.writeInvToNBT(inventoryOut, INV_OUT, data);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }
}