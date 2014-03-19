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
