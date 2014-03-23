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

import ccm.autoCrafter2000.AutoCrafter2000;
import ccm.autoCrafter2000.guis.AutoCrafterGui;
import codechicken.nei.recipe.RecipeInfo;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Does all NEI comparability stuff.
 * Catches all errors, but isn't called if NEI isn't installed.
 *
 * @author Dries007
 */
public class NEIHelper
{
    public static void init()
    {
        AutoCrafter2000.getLogger().info("NEI compatibility init ...");
        try
        {
            if (FMLCommonHandler.instance().getSide().isClient()) RecipeInfo.registerOverlayHandler(AutoCrafterGui.class, new AutoCrafterOverlayHandler(), "crafting");
            AutoCrafter2000.getLogger().info("NEI compatibility done.");
        }
        catch (Exception e)
        {
            AutoCrafter2000.getLogger().warning("NEI compatibility FAILED.");
            e.fillInStackTrace();
        }
    }
}
