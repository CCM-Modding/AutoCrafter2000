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

package ccm.autoCrafter2000.buildcraft.triggers;

import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import ccm.autoCrafter2000.tile.AutoCrafterTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

import static ccm.autoCrafter2000.util.Constants.MODID;

/**
 * Trigger for when recipe is set.
 *
 * @author Dries007
 */
public class RecipeSetTrigger implements ITrigger
{
    @SideOnly(Side.CLIENT)
    private Icon icon;

    @Override
    public int getLegacyId()
    {
        return 0;
    }

    @Override
    public String getUniqueTag()
    {
        return MODID + ":recipe_set";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(MODID.toLowerCase() + ":triggers/recipe_set");
    }

    @Override
    public boolean hasParameter()
    {
        return false;
    }

    @Override
    public boolean requiresParameter()
    {
        return false;
    }

    @Override
    public String getDescription()
    {
        return "Recipe set";
    }

    @Override
    public ITriggerParameter createParameter()
    {
        return null;
    }

    @Override
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
    {
        return tile instanceof AutoCrafterTile && ((AutoCrafterTile) tile).recipe != null;
    }
}
