package com.massivecraft.factions.zcore.fperms.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PermissableActionFrame {

    private Gui gui;

    public PermissableActionFrame(Faction f) {
        ConfigurationSection section = P.p.getConfig().getConfigurationSection("fperm-gui.action");
        gui = new Gui(P.p,
                section.getInt("rows", 3),
                P.p.color(P.p.getConfig().getString("fperm-gui.action.name").replace("{faction}", f.getTag())));
    }

    public void buildGUI(FPlayer fplayer, Permissable perm) {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, gui.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dumby = buildDummyItem();
        // Fill background of GUI with dumbyitem & replace GUI assets after
        for (int x = 0; x <= (gui.getRows() * 9) - 1; x++) GUIItems.add(new GuiItem(dumby, e -> e.setCancelled(true)));
        for (PermissableAction action : PermissableAction.values()) {
            if (action.getSlot() == -1) continue;
            GUIItems.set(action.getSlot(), new GuiItem(action.buildAsset(fplayer, perm), e -> {
                e.setCancelled(true);
                if (PermissableAction.fromSlot(e.getSlot()) == action) {
                    Access access;
                    boolean success;
                    switch (e.getClick()) {
                        case LEFT:
                            access = Access.ALLOW;
                            success = fplayer.getFaction().setPermission(perm, action, access);
                            break;
                        case RIGHT:
                            access = Access.DENY;
                            success = fplayer.getFaction().setPermission(perm, action, access);
                            break;
                        case MIDDLE:
                            access = Access.UNDEFINED;
                            success = fplayer.getFaction().setPermission(perm, action, access);
                            break;
                        default:
                            return;
                    }
                    if (success) fplayer.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), perm.name());
                    else fplayer.msg(TL.COMMAND_PERM_LOCKED);
                    P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), perm.name()) + " for faction " + fplayer.getTag());
                    // Closing and opening resets the cursor.
                    // fplayer.getPlayer().closeInventory();
                    buildGUI(fplayer, perm);
                }
            }));
        }
        GUIItems.set(P.p.getConfig().getInt("fperm-gui.action.slots.back"), new GuiItem(buildBackItem(), event -> {
            event.setCancelled(true);
            // Closing and opening resets the cursor.
            // fplayer.getPlayer().closeInventory();
            new PermissableRelationFrame(fplayer.getFaction()).buildGUI(fplayer);
        }));
        pane.populateWithGuiItems(GUIItems);
        gui.addPane(pane);
        gui.update();
        gui.show(fplayer.getPlayer());
    }


    private ItemStack buildDummyItem() {
        ConfigurationSection config = P.p.getConfig().getConfigurationSection("fperm-gui.dummy-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(P.p.colorList(config.getStringList("Lore")));
        meta.setDisplayName(P.p.color(config.getString("Name")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackItem() {
        ConfigurationSection config = P.p.getConfig().getConfigurationSection("fperm-gui.back-item");
        ItemStack item = XMaterial.matchXMaterial(config.getString("Type")).parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(P.p.colorList(config.getStringList("Lore")));
        meta.setDisplayName(P.p.color(config.getString("Name")));
        item.setItemMeta(meta);
        return item;
    }
}