package ccm.autoCrafter2000.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public static List<ItemStack> getList(IInventory inventory)
    {
        List<ItemStack> input = new LinkedList<ItemStack>();

        for (int i = 0; i < inventory.getSizeInventory(); i ++)
        {
            ItemStack stackToAdd = inventory.getStackInSlot(i);
            if (stackToAdd == null) continue;
            boolean f = false;
            for (ItemStack stackInList : input)
            {
                if (stackInList == null) continue;
                if (canStacksMerge(stackToAdd, stackInList))
                {
                    stackInList.stackSize += stackToAdd.stackSize;
                    f = true;
                    break;
                }
            }
            if (!f) input.add(stackToAdd.copy());
        }

        return input;
    }

    public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == null || stack2 == null) return false;
        if (!stack1.isItemEqual(stack2)) return false;
        if (!ItemStack.areItemStackTagsEqual(stack1, stack2)) return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    public static IRecipe findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        // Begin repair recipe handler
        int itemNum = 0;
        ItemStack item1 = null;
        ItemStack item2 = null;
        int slot;

        for (slot = 0; slot < par1InventoryCrafting.getSizeInventory(); ++slot)
        {
            ItemStack itemInSlot = par1InventoryCrafting.getStackInSlot(slot);

            if (itemInSlot != null)
            {
                if (itemNum == 0)
                {
                    item1 = itemInSlot;
                }

                if (itemNum == 1)
                {
                    item2 = itemInSlot;
                }

                ++itemNum;
            }
        }

        if (itemNum == 2 && item1.itemID == item2.itemID && item1.stackSize == 1 && item2.stackSize == 1 && Item.itemsList[item1.itemID].isRepairable())
        {
            Item itemBase = Item.itemsList[item1.itemID];
            int item1Durability = itemBase.getMaxDamage() - item1.getItemDamageForDisplay();
            int item2Durability = itemBase.getMaxDamage() - item2.getItemDamageForDisplay();
            int repairAmt = item1Durability + item2Durability + itemBase.getMaxDamage() * 5 / 100;
            int newDamage = itemBase.getMaxDamage() - repairAmt;

            if (newDamage < 0)
            {
                newDamage = 0;
            }

            ArrayList ingredients = new ArrayList<ItemStack>(2);
            ingredients.add(item1);
            ingredients.add(item2);
            return new ShapelessRecipes(new ItemStack(item1.itemID, 1, newDamage),ingredients);
        }
        // End repair recipe handler
        else
        {
            List recipes = CraftingManager.getInstance().getRecipeList();
            for (Object recipe : recipes)
            {
                IRecipe currentRecipe = (IRecipe) recipe;

                if (currentRecipe.matches(par1InventoryCrafting, par2World))
                {
                    return currentRecipe;
                }
            }

            return null;
        }
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

    public static void dump(String name, Object stuff)
    {
        System.out.println("Dump: " + name);
        dump(stuff);
    }

    public static void dump(Object stuff)
    {
        if (stuff instanceof Iterable)
            for (Object o : (Iterable) stuff)
                dump(o);
        else
            System.out.println(stuff);
    }

    public static boolean containsEnoughAll(List<ItemStack> inputList, List<ItemStack> requiredItems)
    {
        for (ItemStack requiredItem: requiredItems) if (!containsEnough(inputList, requiredItem)) return false;
        return true;
    }

    public static boolean containsEnough(List<ItemStack> inputList, ItemStack requiredItem)
    {
        for (ItemStack input : inputList)
        {
            if (canStacksMerge(input, requiredItem))
            {
                if (requiredItem.stackSize <= input.stackSize) return true;
            }
        }
        return false;
    }

    public static void removeFromInventory(InventoryBasic inventoryIn, List<ItemStack> requiredItems)
    {
        for (ItemStack requiredItem: requiredItems) removeFromInventory(inventoryIn, requiredItem);
    }

    public static void removeFromInventory(InventoryBasic inventoryIn, ItemStack requiredItems)
    {
        for (int i = 0; i < inventoryIn.getSizeInventory(); i++)
        {
            if (canStacksMerge(requiredItems, inventoryIn.getStackInSlot(i)))
            {
                requiredItems.stackSize -= inventoryIn.decrStackSize(i, requiredItems.stackSize).stackSize;
                if (requiredItems.stackSize == 0) return;
            }
        }
    }

    public static void addToInventory(InventoryBasic inventoryOut, ItemStack output)
    {
        output = output.copy();
        for (int i = 0; i < inventoryOut.getSizeInventory(); i ++)
        {
            ItemStack slotStack = inventoryOut.getStackInSlot(i);
            if (slotStack == null || slotStack.stackSize == slotStack.getMaxStackSize()) continue;

            if (canStacksMerge(slotStack, output))
            {
                if (slotStack.stackSize + output.stackSize > slotStack.getMaxStackSize())
                {
                    output.stackSize -= (slotStack.getMaxStackSize() - slotStack.stackSize);
                    slotStack.stackSize = slotStack.getMaxStackSize();
                }
                else
                {
                    slotStack.stackSize += output.stackSize;
                    output.stackSize = 0;
                }
            }

            if (output.stackSize == 0) return;
        }

        for (int i = 0; i < inventoryOut.getSizeInventory(); i ++)
        {
            ItemStack slotStack = inventoryOut.getStackInSlot(i);
            if (slotStack != null) continue;
            inventoryOut.setInventorySlotContents(i, output.copy());
            return;
        }
    }

    public static boolean hasSpaceFor(InventoryBasic inventoryOut, ItemStack output)
    {
        output = output.copy();
        for (int i = 0; i < inventoryOut.getSizeInventory(); i ++)
        {
            ItemStack slotStack = inventoryOut.getStackInSlot(i);
            if (slotStack == null || slotStack.stackSize == slotStack.getMaxStackSize()) continue;

            if (canStacksMerge(slotStack, output))
            {
                if (slotStack.stackSize + output.stackSize > slotStack.getMaxStackSize())
                {
                    output.stackSize -= (slotStack.getMaxStackSize() - slotStack.stackSize);
                }
                else
                {
                    output.stackSize = 0;
                }
            }

            if (output.stackSize == 0) return true;
        }

        for (int i = 0; i < inventoryOut.getSizeInventory(); i ++)
        {
            ItemStack slotStack = inventoryOut.getStackInSlot(i);
            if (slotStack != null) continue;
            return true;
        }
        return false;
    }

    public static void dropItems(World world, ItemStack stack, int i, int j, int k)
    {
        if (stack == null || stack.stackSize <= 0) return;

        float f1 = 0.7F;
        double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        EntityItem entityitem = new EntityItem(world, i + d, j + d1, k + d2, stack);
        entityitem.delayBeforeCanPickup = 10;

        world.spawnEntityInWorld(entityitem);
    }

    public static void dropItems(World world, IInventory inv, int i, int j, int k)
    {
        for (int slot = 0; slot < inv.getSizeInventory(); ++slot)
        {
            ItemStack items = inv.getStackInSlot(slot);

            if (items != null && items.stackSize > 0)
            {
                dropItems(world, inv.getStackInSlot(slot).copy(), i, j, k);
            }
        }
    }
}
