package com.comze_instancelabs.minigamesapi.sql;

import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.minigamesapi.sql.MySQL;
import com.comze_instancelabs.minigamesapi.sql.SQLite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainSQL {

	// used for rewards and stats

	JavaPlugin plugin = null;
	private boolean mysql = true; // false for sqlite
	MySQL MySQL;
	SQLite SQLite;

	public MainSQL(JavaPlugin plugin, boolean mysql) {
		this.plugin = plugin;
		this.mysql = mysql;

		if (mysql) {
			MySQL = new MySQL(plugin.getConfig().getString("mysql.host"), "3306", plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.user"), plugin.getConfig().getString("mysql.pw"));
		} else {
			SQLite = new SQLite(plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.user"), plugin.getConfig().getString("mysql.pw"));
		}

		if (plugin.getConfig().getBoolean("mysql.enabled") && MySQL != null) {
			try {
				this.createTables();
			} catch (Exception e) {
				System.out.println("Failed initializing MySQL. Disabling!");
				plugin.getConfig().set("mysql.enabled", false);
				plugin.saveConfig();
			}
		} else if (plugin.getConfig().getBoolean("mysql.enabled") && MySQL == null) {
			System.out.println("Failed initializing MySQL. Disabling!");
			plugin.getConfig().set("mysql.enabled", false);
			plugin.saveConfig();
		}
	}

	public void createTables() {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			c.createStatement().execute("CREATE DATABASE IF NOT EXISTS `" + plugin.getConfig().getString("mysql.database") + "`");
			c.createStatement().execute("CREATE TABLE IF NOT EXISTS " + plugin.getName() + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, player VARCHAR(100), points INT, wins INT, loses INT, kills INT)");
			ResultSet res = c.createStatement().executeQuery("SHOW COLUMNS FROM `" + plugin.getName() + "` LIKE 'kills'");
			if (!res.isBeforeFirst()) {
				// old table format without kills column -> add kills column
				c.createStatement().execute("ALTER TABLE " + plugin.getName() + " ADD kills INT");
			}
			ResultSet res2 = c.createStatement().executeQuery("SHOW COLUMNS FROM `" + plugin.getName() + "` LIKE 'deaths'");
			if (!res2.isBeforeFirst()) {
				// old table format without deaths column -> add deaths column
				c.createStatement().execute("ALTER TABLE " + plugin.getName() + " ADD deaths INT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateWinnerStats(String p_, int reward, boolean addwin) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		int wincount = addwin ? 1 : 0;

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");
			if (!res3.isBeforeFirst()) {
				// there's no such user
				c.createStatement().executeUpdate("INSERT INTO " + plugin.getName() + " VALUES('0', '" + p_ + "', '" + Integer.toString(reward) + "', '" + Integer.toString(wincount) + "', '0', '0', '0')");
				return;
			}
			res3.next();
			int points = res3.getInt("points") + reward;
			int wins = res3.getInt("wins") + wincount;

			c.createStatement().executeUpdate("UPDATE " + plugin.getName() + " SET points='" + Integer.toString(points) + "', wins='" + Integer.toString(wins) + "' WHERE player='" + p_ + "'");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateLoserStats(String p_) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");
			if (!res3.isBeforeFirst()) {
				// there's no such user
				c.createStatement().executeUpdate("INSERT INTO " + plugin.getName() + " VALUES('0', '" + p_ + "', '0', '0', '1', '0', '0')");
				return;
			}
			res3.next();
			int loses = res3.getInt("loses") + 1;

			c.createStatement().executeUpdate("UPDATE " + plugin.getName() + " SET loses='" + Integer.toString(loses) + "' WHERE player='" + p_ + "'");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateKillerStats(String p_) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");
			if (!res3.isBeforeFirst()) {
				// there's no such user
				c.createStatement().executeUpdate("INSERT INTO " + plugin.getName() + " VALUES('0', '" + p_ + "', '0', '0', '0', '1', '0')");
				return;
			}
			res3.next();
			int kills = res3.getInt("kills") + 1;

			c.createStatement().executeUpdate("UPDATE " + plugin.getName() + " SET kills='" + Integer.toString(kills) + "' WHERE player='" + p_ + "'");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateDeathStats(String p_) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");
			if (!res3.isBeforeFirst()) {
				// there's no such user
				c.createStatement().executeUpdate("INSERT INTO " + plugin.getName() + " VALUES('0', '" + p_ + "', '0', '0', '0', '0', '1')");
				return;
			}
			res3.next();
			int deaths = res3.getInt("deaths") + 1;

			c.createStatement().executeUpdate("UPDATE " + plugin.getName() + " SET deaths='" + Integer.toString(deaths) + "' WHERE player='" + p_ + "'");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getPoints(String p_) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return -1;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");

			/*
			 * if(res3.next()){ int points = res3.getInt("points"); return points; } return -1;
			 */

			if (res3.isBeforeFirst()) {
				res3.next();
				int credits = res3.getInt("points");
				return credits;
			} else {
				// System.out.println("New User detected.");
			}
		} catch (SQLException e) {
			//
		}
		return -1;
	}

	public int getWins(String p_) {
		if (!plugin.getConfig().getBoolean("mysql.enabled")) {
			return -1;
		}
		if (!mysql) {
			// TODO SQLite
		}
		Connection c = MySQL.open();

		try {
			ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + plugin.getName() + " WHERE player='" + p_ + "'");

			if (res3.isBeforeFirst()) {
				res3.next();
				int wins = res3.getInt("wins");
				return wins;
			} else {
				// System.out.println("New User detected.");
			}
		} catch (SQLException e) {
			//
		}
		return -1;
	}

}
