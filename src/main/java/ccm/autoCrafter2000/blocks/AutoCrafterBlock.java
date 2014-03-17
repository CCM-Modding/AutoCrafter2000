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

package ccm.autoCrafter2000.blocks;

import ccm.autoCrafter2000.AutoCrafter2000;
import ccm.autoCrafter2000.tile.AutoCrafterTile;
import ccm.autoCrafter2000.util.Constants;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class AutoCrafterBlock extends BlockContainer
{
    public static AutoCrafterBlock instance;
    private       Icon             icoBottom;
    private       Icon             icoSide;
    private       Icon             icoTop;

    public AutoCrafterBlock(int par1)
    {
        super(par1, Material.iron);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName("AutoCrafter");
        setCreativeTab(CreativeTabs.tabMisc);

        GameRegistry.registerBlock(this, "AutoCrafterBlock");
        GameRegistry.registerTileEntity(AutoCrafterTile.class, "AutoCrafterTile");
        LanguageRegistry.addName(this, "AutoCrafter");
        CraftingManager.getInstance().addRecipe(new ItemStack(this), " c ", "iwi", " t ", 'c', Block.chest, 'i', Item.ingotIron, 'w', Block.workbench, 't', Block.torchRedstoneActive);
        CraftingManager.getInstance().addRecipe(new ItemStack(this), " c ", "iwi", " t ", 'c', Block.chestTrapped, 'i', Item.ingotIron, 'w', Block.workbench, 't', Block.torchRedstoneActive);

        instance = this;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
    {
        super.onBlockActivated(world, x, y, z, entityplayer, side, hitX, hitY, hitZ);

        if (entityplayer.isSneaking()) return false;

        if (!world.isRemote) entityplayer.openGui(AutoCrafter2000.instance, Constants.GuiID_AutoCrafter, world, x, y, z);

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new AutoCrafterTile(world);
    }

    @Override
    public Icon getIcon(int i, int j)
    {
        switch (i)
        {
            case 0:
                return icoBottom;
            case 1:
                return icoTop;
            default:
                return icoSide;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        icoBottom = par1IconRegister.registerIcon(Constants.MODID.toLowerCase() + ":autoCrafter_bottom");
        icoSide = par1IconRegister.registerIcon(Constants.MODID.toLowerCase() + ":autoCrafter_side");
        icoTop = par1IconRegister.registerIcon(Constants.MODID.toLowerCase() + ":autoCrafter_top");
    }
}
