package me.glaremasters.guilds.commands;

import me.glaremasters.guilds.Main;
import me.glaremasters.guilds.commands.base.CommandBase;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;


public class CommandList extends CommandBase {

    public static Inventory guildList = null;
    public static HashMap<UUID, Integer> playerPages = new HashMap<>();

    public CommandList() {
        super("list", "List all guilds on the server", "guilds.command.list", false, null, null, 0,
                0);
    }

    public static Inventory getSkullsPage(int page) {
        HashMap<UUID, ItemStack> skulls = new HashMap<>();
        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.DARK_GREEN + Main.getInstance().getConfig().getString("gui-name.list.name"));

        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < Main.getInstance().getGuildHandler().getGuilds().values().size(); i++) {
            Guild guild =
                    (Guild) Main.getInstance().getGuildHandler().getGuilds().values().toArray()[i];
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            if (Main.getInstance().getConfig().getBoolean("display.prefix")) {
                lore.add(
                        ChatColor.translateAlternateColorCodes('&',
                                Main.getInstance().getConfig().getString("list.prefix") + guild
                                        .getPrefix()));
            }
            if (Main.getInstance().getConfig().getBoolean("display.name")) {
                lore.add(
                        ChatColor.translateAlternateColorCodes('&',
                                Main.getInstance().getConfig().getString("list.name")
                                        + guild.getName()));
            }
            if (Main.getInstance().getConfig().getBoolean("display.master")) {
                lore.add(
                        ChatColor.translateAlternateColorCodes('&',
                                Main.getInstance().getConfig().getString("list.master")
                                        + Bukkit
                                        .getOfflinePlayer(guild.getGuildMaster().getUniqueId()).getName()));
            }
            if (Main.getInstance().getConfig().getBoolean("display.member-count")) {
                lore.add(
                        ChatColor.translateAlternateColorCodes('&',
                                Main.getInstance().getConfig().getString("list.member-count")
                                        + String.valueOf(guild.getMembers().size())));
            }
            if (Main.getInstance().getConfig().getBoolean("display.members")) {
                List<String> lines = Arrays.asList(guild.getMembers().stream()
                        .map(member -> Bukkit.getOfflinePlayer(member.getUniqueId()).getName())
                        .collect(Collectors.joining(", "))
                        .replaceAll("(([a-zA-Z0-9_]+, ){3})", "$0\n")
                        .split("\n"));
                lines.set(0, ChatColor.translateAlternateColorCodes('&',
                        Main.getInstance().getConfig().getString("list.members") + lines.get(0)));
                lore.addAll(lines);
            }
            if (Main.getInstance().getConfig().getBoolean("display.guildstatus")) {
                lore.add(
                        ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig()
                                .getString("list.guildstatus")
                                + guild.getStatus()));
            }
            skullMeta.setLore(lore);
            String name = Bukkit.getOfflinePlayer(guild.getGuildMaster().getUniqueId()).getName();
            skullMeta
                    .setOwner(Bukkit.getOfflinePlayer(guild.getGuildMaster().getUniqueId()).getName());
            skullMeta.setDisplayName(
                    ChatColor.AQUA +  Main.getInstance().getConfig().getString("gui-name.list.head-name").replace("{player}", name));
            skull.setItemMeta(skullMeta);
            skulls.put(guild.getGuildMaster().getUniqueId(), skull);
        }

        ItemStack previous = new ItemStack(Material.EMPTY_MAP, 1);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(ChatColor.GOLD + Main.getInstance().getConfig()
                .getString("gui-name.list.previous-page"));
        previous.setItemMeta(previousMeta);
        ItemStack next = new ItemStack(Material.EMPTY_MAP, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.GOLD + Main.getInstance().getConfig()
                .getString("gui-name.list.next-page"));
        next.setItemMeta(nextMeta);
        ItemStack barrier = new ItemStack(Material.BARRIER, 1);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName(
                ChatColor.GOLD + Main.getInstance().getConfig().getString("gui-name.list.page") + page);
        barrier.setItemMeta(barrierMeta);
        inv.setItem(53, next);
        inv.setItem(49, barrier);
        inv.setItem(45, previous);

        startIndex = (page - 1) * 45;
        endIndex = startIndex + 45;

        if (endIndex > skulls.values().size()) {
            endIndex = skulls.values().size();
        }

        int iCount = 0;
        for (int i1 = startIndex; i1 < endIndex; i1++) {
            inv.setItem(iCount, (ItemStack) skulls.values().toArray()[i1]);
            iCount++;
        }

        return inv;
    }

    @Override
    public void execute(Player player, String[] args) {

        playerPages.put(player.getUniqueId(), 1);
        guildList = getSkullsPage(1);
        player.openInventory(guildList);
    }
}


