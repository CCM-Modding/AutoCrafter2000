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

package ccm.autoCrafter2000.buildcraft;

import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import ccm.autoCrafter2000.AutoCrafter2000;
import ccm.autoCrafter2000.buildcraft.triggers.InventoryTrigger;
import ccm.autoCrafter2000.buildcraft.triggers.RecipeSetTrigger;

import java.util.LinkedList;

/**
 * Does all BC comparability stuff.
 * Catches all errors, but isn't called if BS isn't installed.
 *
 * @author Dries007
 */
public class BuildcraftHelper
{
    private static final LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

    public static void init()
    {
        AutoCrafter2000.getLogger().info("BuildCraft compatibility init ...");
        try
        {
            triggers.add(new RecipeSetTrigger());
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Empty, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Empty, InventoryTrigger.InventoryType.Out));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Full, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Full, InventoryTrigger.InventoryType.Out));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Items, InventoryTrigger.InventoryType.In));
            triggers.add(new InventoryTrigger(InventoryTrigger.State.Has_Items, InventoryTrigger.InventoryType.Out));

            for (ITrigger trigger : triggers) ActionManager.registerTrigger(trigger);
            AutoCrafter2000.getLogger().info("BuildCraft compatibility done.");
        }
        catch (Exception e)
        {
            AutoCrafter2000.getLogger().warning("BuildCraft compatibility FAILED.");
            e.fillInStackTrace();
        }
    }

    /**
     * Used for overriding the default BC triggers.
     */
    public static LinkedList<ITrigger> getAutocrafterTriggers()
    {
        return triggers;
    }
}
