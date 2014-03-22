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

package ccm.autoCrafter2000.tile;

import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import ccm.autoCrafter2000.AutoCrafter2000;
import ccm.autoCrafter2000.buildcraft.BuildcraftHelper;
import ccm.autoCrafter2000.util.MultiInventory;
import ccm.nucleumOmnium.helpers.InventoryHelper;
import com.google.common.base.Joiner;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static ccm.autoCrafter2000.util.Constants.BC_MODID;
import static ccm.autoCrafter2000.util.Constants.CHANNEL_RMU;

/**
 * This is where the magic happens.
 * Thanks to Buildcraft for some handy code.
 *
 * @author Dries007
 */
@Optional.Interface(iface = "buildcraft.api.gates.IOverrideDefaultTriggers", modid = BC_MODID)
public class AutoCrafterTile extends TileEntity implements ISidedInventory, IOverrideDefaultTriggers
{
    // NBT data
    private static final String INV_RESULT = "result";
    private static final String INV_MATRIX = "matrix";
    private static final String INV_IN     = "in";
    private static final String INV_OUT    = "out";

    // Slots
    public static final  int   SLOT_OUT     = 0;
    private static final int   MATRIX       = 3 * 3;
    private static final int[] SLOTS_MATRIX = InventoryHelper.slotArray(SLOT_OUT, MATRIX);
    private static final int   IN           = 3 * 3;
    private static final int   OUT          = 3 * 3;
    private static final int[] SLOTS_IN     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN);
    private static final int[] SLOTS_OUT    = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1 + IN, OUT);
    private static final int[] SLOTS_IO     = InventoryHelper.slotArray(SLOTS_MATRIX.length + 1, IN + OUT);

    // Inventories this block is made out of, the multi one is used for the ISidedInventory
    public final  InventoryCraftResult inventoryCraftResult = new InventoryCraftResult();
    public final  InventoryCrafting    inventoryMatrix      = InventoryHelper.newCraftingMatrix(MATRIX, 1);
    public final  InventoryCrafting    inventoryIn          = InventoryHelper.newCraftingMatrix(MATRIX, 64);
    public final  InventoryBasic       inventoryOut         = new InventoryBasic("AutoCrafter_out", true, OUT);
    private final MultiInventory       multiInventory       = new MultiInventory(inventoryCraftResult, inventoryMatrix, inventoryIn, inventoryOut);

    // Other variables
    public IRecipe recipe;
    /**
     * 0 = default  => Stop crafting with redstone signal
     * 1            => Only craft with redstone signal
     * 2            => Ignore redstone signal
     */
    public  int redstoneMode = 0;
    private int tick         = 0;
    private InternalPlayer internalPlayer;
    private SlotCrafting   craftSlot;
    private final List<ItemStack> overflow = new LinkedList<ItemStack>();
    public int crafts = 0;
    public List<EntityPlayer> players = new LinkedList<EntityPlayer>();

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

        boolean willCraft = true;
        // Lower tick rate
        tick++;
        if (AutoCrafter2000.getConfig().craftDelay != 0 && tick % AutoCrafter2000.getConfig().craftDelay != 0) willCraft = false;
        tick = 0;

        // Redstone things
        boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        if (redstoneMode == 0 && powered) willCraft = false;
        if (redstoneMode == 1 && !powered) willCraft = false;

        // Deal with overflow. If we couldn't empty, don't make new stuff.
        emptyOverflow();
        if (!overflow.isEmpty()) willCraft = false;

        ItemStack result = null;
        if (willCraft && recipe == null) willCraft = false;
        if (willCraft && !recipe.matches(inventoryIn, worldObj)) willCraft = false;
        if (willCraft && (result = recipe.getCraftingResult(inventoryIn)) == null) willCraft = false;
        if (willCraft && !InventoryHelper.hasSpaceFor(inventoryOut, result)) willCraft = false;
        if (willCraft)
        {
            crafts ++;
            result = result.copy();
            if (AutoCrafter2000.getConfig().updateCraftCountLive) for (EntityPlayer player : players) PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(CHANNEL_RMU, Joiner.on(";").join(this.xCoord, this.yCoord, this.zCoord, this.redstoneMode, this.crafts).getBytes()), (cpw.mods.fml.common.network.Player) player);
            craftSlot.onPickupFromSlot(internalPlayer, result);

            ItemStack stack = InventoryHelper.addToInventory(inventoryOut, result);
            if (stack != null) overflow.add(stack);

            for (int i = 0; i < internalPlayer.inventory.getSizeInventory(); i++)
            {
                stack = InventoryHelper.addToInventory(inventoryOut, internalPlayer.inventory.getStackInSlotOnClosing(i));
                if (stack != null) overflow.add(stack);
            }
        }
        else reBalanceSlots();
    }

    public static boolean canStacksMergeWithOreDict(ItemStack stack1, ItemStack stack2, boolean ifNull)
    {
        if (InventoryHelper.canStacksMerge(stack1, stack2, ifNull)) return true;

        int id1 = OreDictionary.getOreID(stack1);
        int id2 = OreDictionary.getOreID(stack2);
        return id1 != -1 && id1 == id2;
    }

    /**
     * Done with 2 sets of loops to prioritize the filling of empty slots
     */
    private void reBalanceSlots()
    {
        for (int i = 0; i < MATRIX; i ++)
        {
            ItemStack craftStack = inventoryMatrix.getStackInSlot(i);
            if (craftStack == null) continue;

            ItemStack existingStack = inventoryIn.getStackInSlot(i);
            if (existingStack == null) continue; // If existing stack is null, move on. (Others can fill this stack later.)
            if (!canStacksMergeWithOreDict(craftStack, existingStack, false)) continue; // If existing stack doesn't match, move on.

            for (int j = 0; j < MATRIX; j++)
            {
                craftStack = inventoryMatrix.getStackInSlot(j);
                if (craftStack == null) continue;
                if (i == j) continue; // Don't try to merge with yourself...

                ItemStack otherStack = inventoryIn.getStackInSlot(j);
                if (otherStack == null)
                {
                    if (existingStack.stackSize == 1) continue; // Prevent derp
                    inventoryIn.setInventorySlotContents(i, existingStack);
                    inventoryIn.setInventorySlotContents(j, existingStack.splitStack(1));
                    return; // Do only 1 per tick
                }
            }
        }
        for (int i = 0; i < MATRIX; i ++)
        {
            ItemStack craftStack = inventoryMatrix.getStackInSlot(i);
            if (craftStack == null) continue;

            ItemStack existingStack = inventoryIn.getStackInSlot(i);
            if (existingStack == null) continue; // If existing stack is null, move on. (Others can fill this stack later.)
            if (!canStacksMergeWithOreDict(craftStack, existingStack, false)) continue; // If existing stack doesn't match, move on.

            for (int j = 0; j < MATRIX; j++)
            {
                craftStack = inventoryMatrix.getStackInSlot(j);
                if (craftStack == null) continue;
                if (i == j) continue; // Don't try to merge with yourself...

                ItemStack otherStack = inventoryIn.getStackInSlot(j);
                if (!canStacksMergeWithOreDict(craftStack, otherStack, false)) continue; // If the stack we pick doesn't fit into the crafting slot, pick another one.

                if (InventoryHelper.canStacksMerge(existingStack, otherStack, false) && existingStack.stackSize > otherStack.stackSize + 1)
                {
                    existingStack.stackSize --;
                    otherStack.stackSize ++;
                    return; // Do only 1 per tick
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

    @Override
    public Packet getDescriptionPacket()
    {
        Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 5, new NBTTagCompound());
        packet.data.setInteger("redstoneMode", redstoneMode);
        packet.data.setInteger("crafts", crafts);
        return packet;
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        redstoneMode = pkt.data.getInteger("redstoneMode");
        crafts = pkt.data.getInteger("crafts");
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
        for (int i : SLOTS_IN) if (i == slot) return canStacksMergeWithOreDict(stack, multiInventory.getStackInSlot(i - IN), false);
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
        for (int i : SLOTS_IN) if (i == slot) return canStacksMergeWithOreDict(stack, multiInventory.getStackInSlot(i - IN), false);
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
        PacketDispatcher.sendPacketToPlayer(PacketDispatcher.getPacket(CHANNEL_RMU, Joiner.on(";").join(this.xCoord, this.yCoord, this.zCoord, this.redstoneMode, this.crafts).getBytes()), (cpw.mods.fml.common.network.Player) player);
        return true;
    }

    @Optional.Method(modid = BC_MODID)
    @Override
    public LinkedList<ITrigger> getTriggers()
    {
        return BuildcraftHelper.getAutocrafterTriggers();
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
        redstoneMode = data.getInteger("redstoneMode");
        crafts = data.getInteger("crafts");

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
        data.setInteger("redstoneMode", redstoneMode);
        data.setInteger("crafts", crafts);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }
}