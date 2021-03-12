package findthemurder;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MinigameServer extends JavaPlugin{

	////////////// 로케이션
	public Location lobby;
	public String ms_alert = "§7[ §a알림 §7] §f";
	public MinigameServer server;
	public FindTheMurder findTheMurder;
	public FindTheMurder findTheMurder2;
	
	public void onEnable() {
		//////////////////////////////////
		this.getLogger().info("plugin start");
		server = this;
		loadConfig();
		
		//////////////////////////////////
		getServer().getPluginManager().registerEvents(new DefaultEventHandler(), this);
		
		////// 미니게임
		findTheMurder = new FindTheMurder(server, "FindTheMurder", "§c§l살인자를 찾아라 §f§l", "/ftm1");
//		findTheMurder2 = new FindTheMurder(server, "FindTheMurder2", "§c§l살인자를 찾아라 §f§l랭크 채널§7", "/ftm2");
//		minigames.add(findTheMurder);
		

		
		////////////////////미니게임 채널설정
		
		///////////////////////
		String path = this.getDataFolder().getAbsolutePath();
		File file = new File(path);
		if (!file.exists()) file.mkdir();
		
		path = this.getDataFolder().getAbsolutePath()+"\\Minigame";
		file = new File(path);
		if (!file.exists()) file.mkdir();
		
		
	}
	
	public void setLobby(Location l) {
	    getConfig().set("Lobby_World", l.getWorld().getName());
	    getConfig().set("Lobby_X", l.getBlockX());
	    getConfig().set("Lobby_Y", l.getBlockY() + 1);
	    getConfig().set("Lobby_Z", l.getBlockZ());
	    saveConfig();
	    loadConfig();
	}
	
	public void loadConfig() {
		try {
			lobby = new Location(Bukkit.getWorld(getConfig().getString("Lobby_World")), getConfig().getInt("Lobby_X"),
					getConfig().getInt("Lobby_Y"), getConfig().getInt("Lobby_Z"));
		} catch (IllegalArgumentException e) {
			getServer().getLogger().info("[메인] 로비가 지정되지 않았습니다.");
		}
	}
	
//////////////////이벤트 클래스
	public class DefaultEventHandler implements Listener {

/////////////////////이벤트

		@EventHandler(priority = EventPriority.HIGH)
		public void onPlayercommand(PlayerCommandPreprocessEvent e) {
			Player p = e.getPlayer();
			if (e.getMessage() == null)
				return;
			String cmdMain = e.getMessage().split(" ")[0];
			if (cmdMain == null)
				return;
			if (cmdMain.equalsIgnoreCase("/setLobby") && p.isOp()) {
				setLobby(MyUtility.getIntLocation(p));
				p.sendMessage("§6로비 설정 완료");
				e.setCancelled(true);
			}

		}
	}
}


