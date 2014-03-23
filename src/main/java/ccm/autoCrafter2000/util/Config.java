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

package ccm.autoCrafter2000.util;

import net.minecraftforge.common.Configuration;

import java.io.File;

/**
 * Config file
 * Contains ModID.
 *
 * @author Dries007
 * @see ccm.autoCrafter2000.AutoCrafter2000#getConfig()
 */
public class Config
{
    public int     craftDelay           = 10;
    public int     blockAutoCrafterID   = 1655;
    public boolean updateCraftCountLive = true;

    public Config(File file)
    {
        Configuration configuration = new Configuration(file);

        blockAutoCrafterID = configuration.getBlock("blockAutoCrafterID", blockAutoCrafterID).getInt();
        updateCraftCountLive = configuration.get(Configuration.CATEGORY_GENERAL, "updateCraftCountLive", updateCraftCountLive, "Send a packet to all players in the GUI to update craft count.\nDisable if network speed is an issue.").getBoolean(updateCraftCountLive);
        craftDelay = configuration.get(Configuration.CATEGORY_GENERAL, "craftDelay", craftDelay, "Amount of ticks in between each craft operation. 20 ticks is 1 second.\nLower values (< +-5) increase item duping when shift-clicking. I can't fix that.").getInt();

        configuration.save();
    }
}
