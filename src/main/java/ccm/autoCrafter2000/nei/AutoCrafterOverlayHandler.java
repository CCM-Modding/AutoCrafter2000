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

package ccm.autoCrafter2000.nei;

import ccm.autoCrafter2000.guis.AutoCrafterGui;
import ccm.nucleumOmnium.helpers.NetworkHelper;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import static ccm.autoCrafter2000.util.Constants.CHANNEL_NEI;

/**
 * Allows you to use NEI's '?' button to set the recipe.
 *
 * @author Dries007
 */
public class AutoCrafterOverlayHandler implements IOverlayHandler
{
    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift)
    {
        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

        NBTTagCompound root = new NBTTagCompound();
        for (PositionedStack stack : ingredients)
        {
            int x = (stack.relx - 25) / 18;
            int y = (stack.rely - 6) / 18;
            root.setCompoundTag(String.valueOf((x + y * 3)), stack.item.writeToNBT(new NBTTagCompound()));
        }
        PacketDispatcher.sendPacketToServer(NetworkHelper.makeNBTPacket(CHANNEL_NEI, root));

        ((AutoCrafterGui) firstGui).inventorySlots.getSlot(0).putStack(recipe.getResultStack(recipeIndex).item);
    }
}
