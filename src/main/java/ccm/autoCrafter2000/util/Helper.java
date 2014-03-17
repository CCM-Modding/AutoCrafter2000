package ccm.autoCrafter2000.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Helper
{
    public static int[] slotArray(int start, int size)
    {
        int[] out = new int[size];
        for (int i = 0; i < size; i++)
            out[i] = i + start;

        return out;
    }

    public static InventoryCrafting newCraftingMatrix(int size, final int stackLimit)
    {
        int i;
        if (size == 9) i = 3;
        else if (size == 4) i = 2;
        else throw new IllegalArgumentException("Crating matrix must be 2x2 or 3x3");
        return new InventoryCrafting(new Container()
        {
            @Override
            public boolean canInteractWith(EntityPlayer entityplayer)
            {
                return false;
            }
        }, i, i)
        {
            public int getInventoryStackLimit()
            {
                return stackLimit;
            }
        };
    }

    public static void writeInvToNBT(IInventory inv, String tag, NBTTagCompound data)
    {
        NBTTagList list = new NBTTagList();
        for (byte slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", slot);
                stack.writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        data.setTag(tag, list);
    }

    public static void readInvFromNBT(IInventory inv, String tag, NBTTagCompound data)
    {
        NBTTagList list = data.getTagList(tag);
        for (byte entry = 0; entry < list.tagCount(); entry++)
        {
            NBTTagCompound itemTag = (NBTTagCompound) list.tagAt(entry);
            int slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < inv.getSizeInventory())
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
                inv.setInventorySlotContents(slot, stack);
            }
        }
    }
}
