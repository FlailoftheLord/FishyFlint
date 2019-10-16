package me.flail.fishyflint;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FlintListener {
	private FishyFlint plugin;

	private Player player;
	private LivingEntity target;
	ItemStack item;
	ItemStack offhand;

	public FlintListener(Player player, Entity target) {
		plugin = JavaPlugin.getPlugin(FishyFlint.class);

		this.player = player;

		item = player.getInventory().getItemInMainHand();
		offhand = player.getInventory().getItemInOffHand();

		if (target.isValid() && (target instanceof LivingEntity)) {
			this.target = (LivingEntity) target;
		}

	}

	public void check() {
		if (target == null) {
			return;
		}

		if (target.isValid()) {
			if (player.hasPermission("fishyflint.use")) {
				if (hasFlintInHand()) {
					if (!canIgnite(target)) {
						return;
					}

					if (plugin.cooldowns.contains(player.getUniqueId())) {

						return;
					}

					target.setFireTicks(plugin.burnDuration * 20);

					if (!player.getGameMode().equals(GameMode.CREATIVE)) {
						subtractDurability(plugin.durabilityCost);
					}

					plugin.cooldowns.add(player.getUniqueId());

					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

						plugin.cooldowns.remove(player.getUniqueId());
					}, 20L);

				}

			}

		}

	}

	/**
	 * @param amount
	 *                   the amount to subtract from the item's current durability, won't modify if the
	 *                   item is unbreakable.
	 */
	void subtractDurability(int amount) {
		ItemStack flintItem = item;

		if (!flintItem.getType().equals(Material.FLINT_AND_STEEL)) {
			flintItem = offhand;
		}

		if (flintItem.getItemMeta() instanceof Damageable) {
			Damageable itemDamage = (Damageable) flintItem.getItemMeta();
			itemDamage.setDamage(itemDamage.getDamage() + amount);

			flintItem.setItemMeta((ItemMeta) itemDamage);
			System.out.println("YES SIR");
		}

	}

	boolean hasFlintInHand() {

		return item.getType().equals(Material.FLINT_AND_STEEL) || offhand.getType().equals(Material.FLINT_AND_STEEL) ? true : false;
	}

	boolean canIgnite(LivingEntity entity) {
		if (entity instanceof Player) {
			return plugin.useOnPlayers;
		}

		return true;
	}

}
