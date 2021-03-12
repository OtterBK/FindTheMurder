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

	////////////// �����̼�
	public Location lobby;
	public String ms_alert = "��7[ ��a�˸� ��7] ��f";
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
		
		////// �̴ϰ���
		findTheMurder = new FindTheMurder(server, "FindTheMurder", "��c��l�����ڸ� ã�ƶ� ��f��l", "/ftm1");
//		findTheMurder2 = new FindTheMurder(server, "FindTheMurder2", "��c��l�����ڸ� ã�ƶ� ��f��l��ũ ä�Ρ�7", "/ftm2");
//		minigames.add(findTheMurder);
		

		
		////////////////////�̴ϰ��� ä�μ���
		
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
			getServer().getLogger().info("[����] �κ� �������� �ʾҽ��ϴ�.");
		}
	}
	
//////////////////�̺�Ʈ Ŭ����
	public class DefaultEventHandler implements Listener {

/////////////////////�̺�Ʈ

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
				p.sendMessage("��6�κ� ���� �Ϸ�");
				e.setCancelled(true);
			}

		}
	}
}


