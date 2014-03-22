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
import com.google.common.base.Joiner;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import static ccm.autoCrafter2000.util.Constants.CHANNEL_RMU;

/**
 * The gui for the autocrafter.
 *
 * @see ccm.autoCrafter2000.guis.AutoCrafterContainer
 * @author Dries007
 */
public class AutoCrafterGui extends GuiContainer
{
    private static final int ID_REDSTONE = 0;
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("autocrafter2000:textures/gui/autocraftingtable.png");

    public GuiButtonItemStack redstonebutton;

    public AutoCrafterGui(EntityPlayer player, World world, int x, int y, int z)
    {
        super(new AutoCrafterContainer(player, (AutoCrafterTile) world.getBlockTileEntity(x, y, z)));
        this.ySize = 230;
    }

    public void initGui()
    {
        super.initGui();
        //noinspection unchecked
        this.buttonList.add(redstonebutton = new GuiButtonItemStack(ID_REDSTONE, width/2 + 64, height/2 - 111, new ItemStack(Item.redstone), new ItemStack(Block.torchRedstoneActive), new ItemStack(Block.torchRedstoneIdle)));
        setRedstonebutton();
    }

    public void setRedstonebutton()
    {
        if (redstonebutton == null) return;
        redstonebutton.item = ((AutoCrafterContainer) this.inventorySlots).tile.redstoneMode;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.id == ID_REDSTONE)
        {
            AutoCrafterTile tile = ((AutoCrafterContainer) this.inventorySlots).tile;
            redstonebutton.item = tile.redstoneMode = (tile.redstoneMode + 1) % 3;

            PacketDispatcher.sendPacketToServer(PacketDispatcher.getPacket(CHANNEL_RMU, Joiner.on(";").join(tile.xCoord, tile.yCoord, tile.zCoord, tile.redstoneMode).getBytes()));
        }
    }


    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString("Recipe", 28, 6, 4210752);
        this.fontRenderer.drawString("Input", 25, 73, 4210752);
        this.fontRenderer.drawString("Output", 98, 73, 4210752);
        this.fontRenderer.drawString("Inventory", 8, this.ySize - 92, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}
