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

package ccm.autoCrafter2000.guis;

import ccm.autoCrafter2000.tile.AutoCrafterTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

/**
 * The container for the autocrafter.
 *
 * @author Dries007
 */
public class AutoCrafterContainer extends Container
{
    public final AutoCrafterTile tile;
    private      ItemStack       prevOutput;

    public AutoCrafterContainer(EntityPlayer player, AutoCrafterTile te)
    {
        tile = te;
        this.addSlotToContainer(new SlotCrafting(player, tile.inventoryMatrix, tile.inventoryCraftResult, AutoCrafterTile.SLOT_OUT, 124, 35));                          // Recipe output
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryMatrix,  x + y * 3,      30 + x * 18,    17 + y * 18));  // The recipe matrix
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryIn,      x + y * 3,      26 + x * 18,    84 + y * 18));  // Input
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) this.addSlotToContainer(new Slot(tile.inventoryOut,     x + y * 3,      98 + x * 18,    84 + y * 18));  // Output
        for (int y = 0; y < 3; y++) for (int x = 0; x < 9; x++) this.addSlotToContainer(new Slot(player.inventory,      x + y * 9 + 9,  8 + x * 18,     149 + y * 18)); // Player inventory
        for (int x = 0; x < 9; x++)                             this.addSlotToContainer(new Slot(player.inventory,      x,              8 + x * 18,     207));          // Player hotbar
        this.onCraftMatrixChanged(tile);
    }

    @Override
    public ItemStack slotClick(int i, int j, int modifier, EntityPlayer entityplayer)
    {
        ItemStack stack = super.slotClick(i, j, modifier, entityplayer);
        onCraftMatrixChanged(tile);
        return stack;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        ItemStack output = tile.inventoryCraftResult.getStackInSlot(AutoCrafterTile.SLOT_OUT);
        if (output != prevOutput)
        {
            prevOutput = output;
            onCraftMatrixChanged(tile.inventoryMatrix);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);
        tile.updateRecipe();
        if (tile.recipe == null)    tile.setInventorySlotContents(AutoCrafterTile.SLOT_OUT, null);
        else                        tile.setInventorySlotContents(AutoCrafterTile.SLOT_OUT, tile.recipe.getRecipeOutput().copy());
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return tile.canInteractWith(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        if (true) return null;
        ItemStack itemstack = null;

        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < 10) return null; // Don't shift click into the crafting matrix
            else if (par2 < 28) if (!this.mergeItemStack(itemstack1, 28, this.inventorySlots.size(), true)) return null; // inventory to I & O
            else if (!this.mergeItemStack(itemstack1, 10, 28, false)) return null; // I & O to inventory

            if (itemstack1.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();
        }

        return itemstack;
    }

    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.tile && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
