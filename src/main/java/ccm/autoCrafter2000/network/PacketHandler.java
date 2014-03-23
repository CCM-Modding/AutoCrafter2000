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

package ccm.autoCrafter2000.network;

import ccm.autoCrafter2000.guis.AutoCrafterContainer;
import ccm.autoCrafter2000.guis.AutoCrafterGui;
import ccm.autoCrafter2000.tile.AutoCrafterTile;
import ccm.nucleumOmnium.helpers.NetworkHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

import static ccm.autoCrafter2000.util.Constants.CHANNEL_NEI;
import static ccm.autoCrafter2000.util.Constants.CHANNEL_RMU;

/**
 * Packets!
 * Allows the client to update the redstone state
 * Allows the server to update the craft count
 *
 * @author Dries007
 */
public class PacketHandler implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (packet.channel.equalsIgnoreCase(CHANNEL_RMU))
        {
            String[] split = new String(packet.data).split(";");
            TileEntity tileEntity = ((EntityPlayer) player).getEntityWorld().getBlockTileEntity(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            if (tileEntity instanceof AutoCrafterTile)
            {
                ((AutoCrafterTile) tileEntity).redstoneMode = Integer.parseInt(split[3]);
                if (FMLCommonHandler.instance().getEffectiveSide().isClient() && split.length > 4) ((AutoCrafterTile) tileEntity).crafts = Integer.parseInt(split[4]);
                try
                {
                    if (FMLCommonHandler.instance().getSide().isClient())
                    {
                        if (Minecraft.getMinecraft().currentScreen instanceof AutoCrafterGui)
                        {
                            ((AutoCrafterGui) Minecraft.getMinecraft().currentScreen).setRedstonebutton();
                        }
                    }
                }
                catch (Exception e)
                {
                    // Meh... might be some client issues.
                }
            }
        }

        if (packet.channel.equalsIgnoreCase(CHANNEL_NEI) && ((EntityPlayer) player).openContainer instanceof AutoCrafterContainer)
        {
            NBTTagCompound root = NetworkHelper.byteArrayToNBT(packet.data);

            for (int i = 0; i < AutoCrafterTile.MATRIX; i++)
            {
                ItemStack itemStack = ItemStack.loadItemStackFromNBT(root.getCompoundTag(String.valueOf(i)));
                Slot slot = ((EntityPlayer) player).openContainer.getSlot(i + 1);
                slot.putStack(itemStack);
                slot.onSlotChanged();
            }
            ((EntityPlayer) player).openContainer.onCraftMatrixChanged(((AutoCrafterContainer) ((EntityPlayer) player).openContainer).tile);
        }
    }
}
