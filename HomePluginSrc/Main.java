package HomePluginSrc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // If there is no config yet, config gets made.
        saveDefaultConfig();
        getLogger().info("HomePlugin activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HomePlugin deactivated!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String cmd = command.getName().toLowerCase();

        if (cmd.equalsIgnoreCase("sethome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /sethome <Name>");
                return true;
            }
            String homeName = args[0];
            Location loc = player.getLocation();
            // Saves Home-Data in Config
            String path = "homes." + uuid.toString() + "." + homeName;
            getConfig().set(path + ".world", loc.getWorld().getName());
            getConfig().set(path + ".x", loc.getX());
            getConfig().set(path + ".y", loc.getY());
            getConfig().set(path + ".z", loc.getZ());
            getConfig().set(path + ".yaw", loc.getYaw());
            getConfig().set(path + ".pitch", loc.getPitch());
            saveConfig();
            player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' got set!");
            return true;
        } 
        else if (cmd.equalsIgnoreCase("home")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /home <Name>");
                return true;
            }
            String homeName = args[0];
            String path = "homes." + uuid.toString() + "." + homeName;
            if (!getConfig().contains(path)) {
                player.sendMessage(ChatColor.RED + "Home '" + homeName + "' doesn't exist!");
                return true;
            }
            String worldName = getConfig().getString(path + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                player.sendMessage(ChatColor.RED + "The World '" + worldName + "' wasn't found!");
                return true;
            }
            double x = getConfig().getDouble(path + ".x");
            double y = getConfig().getDouble(path + ".y");
            double z = getConfig().getDouble(path + ".z");
            float yaw = (float) getConfig().getDouble(path + ".yaw");
            float pitch = (float) getConfig().getDouble(path + ".pitch");
            Location loc = new Location(world, x, y, z, yaw, pitch);
            player.teleport(loc);
            player.sendMessage(ChatColor.GREEN + "Teleporting to home '" + homeName + "'.");
            return true;
        } 
        else if (cmd.equalsIgnoreCase("homes")) {
            String path = "homes." + uuid.toString();
            if (!getConfig().contains(path)) {
                player.sendMessage(ChatColor.YELLOW + "You haven't set any homes yet.");
                return true;
            }
            Set<String> homeNames = getConfig().getConfigurationSection(path).getKeys(false);
            player.sendMessage(ChatColor.AQUA + "Your homes: " + String.join(", ", homeNames));
            return true;
        }
        else if (cmd.equalsIgnoreCase("delhome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /delhome <Name>");
                return true;
            }
            String homeName = args[0];
            String path = "homes." + uuid.toString() + "." + homeName;
            if (!getConfig().contains(path)) {
                player.sendMessage(ChatColor.RED + "Home '" + homeName + "' doesn't exist!");
                return true;
            }
            // Entferne den angegebenen Home-Eintrag
            getConfig().set(path, null);
            saveConfig();
            player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' got deleted!");
            return true;
        }
        return false;
    }
}