package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.Placeholder;
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PermissableAction {
    BAN("ban"),
    BUILD("build"),
    DESTROY("destroy"),
    FROST_WALK("frostwalk"),
    PAIN_BUILD("painbuild"),
    DOOR("door"),
    BUTTON("button"),
    LEVER("lever"),
    CONTAINER("container"),
    INVITE("invite"),
    KICK("kick"),
    ITEM("items"), // generic for most items
    SETHOME("sethome"),
    TERRITORY("territory"),
    ACCESS("access"),
    HOME("home"),
    DISBAND("disband"),
    PROMOTE("promote"),
    SETWARP("setwarp"),
    WARP("warp"),
    FLY("fly"),
    VAULT("vault"),
    TNTBANK("tntbank"),
    TNTFILL("tntfill"),
    WITHDRAW("withdraw"),
    CHEST("chest"),
    CHECK("check"),
    SPAWNER("spawner");

    private String name;

    PermissableAction(String name) {
        this.name = name;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return - action
     */
    public static PermissableAction fromString(String check) {
        for (PermissableAction permissableAction : values()) {
            if (permissableAction.name().equalsIgnoreCase(check)) {
                return permissableAction;
            }
        }

        return null;
    }

    public static Map<PermissableAction, Access> fromDefaults(DefaultPermissions defaultPermissions) {
        Map<PermissableAction, Access> defaultMap = new HashMap<>();
        for (PermissableAction permissableAction : PermissableAction.values()) {
            defaultMap.put(permissableAction, defaultPermissions.getbyName(permissableAction.name) ? Access.ALLOW : Access.DENY);
        }
        return defaultMap;
    }

    public static PermissableAction fromSlot(int slot) {
        for (PermissableAction action : PermissableAction.values()) {
            if (action.getSlot() == slot) return action;
        }
        return null;
    }

    public int getSlot() {
        return P.p.getConfig().getInt("fperm-gui.action.slots." + this.name.toLowerCase());
    }

    /**
     * Get the friendly name of this action. Used for editing in commands.
     *
     * @return friendly name of the action as a String.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ItemStack buildAsset(FPlayer fme, Permissable perm) {
        ConfigurationSection section = P.p.getConfig().getConfigurationSection("fperm-gui.action");
        ItemStack item = XMaterial.matchXMaterial(section.getString("Materials." + this.name)).parseItem();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(P.p.color(section.getString("placeholder-item.name").replace("{action}", this.name)));
        List<String> lore = section.getStringList("placeholder-item.lore");

        lore = P.p.replacePlaceholders(lore,
                new Placeholder("{action-access-color}", fme.getFaction().getPermissions().get(perm).get(this).getColor()),
                new Placeholder("{action-access}", fme.getFaction().getPermissions().get(perm).get(this).getName()));

        meta.setLore(P.p.colorList(lore));
        item.setItemMeta(meta);
        return item;
    }

}