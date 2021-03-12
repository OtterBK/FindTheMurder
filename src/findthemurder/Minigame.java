package findthemurder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.coloredcarrot.api.sidebar.Sidebar;
import com.coloredcarrot.api.sidebar.SidebarString;
import com.connorlinfoot.actionbarapi.ActionBarAPI;

import me.confuser.barapi.BarAPI;

public class Minigame {

	public static boolean checkSetting = false;
	public MinigameServer server;

	//���ݰ� ������
	public HashMap<String, Long> victimDelayMap = new HashMap<String, Long>();
	public HashMap<String, List<Long>> intervalMap = new HashMap<String, List<Long>>();
	
	////////////////////���� �ʼ���
	public String gameName; 
	public String disPlayGameName;
	public String inventoryGameName;
	public String cmdMain;
	public int minPlayer = 6;
	public int maxPlayer = 10;
	public int startCountTime = 60;
	public Location loc_Join;
	public boolean doneSetting;
	public boolean joinBlock = false;
	public boolean canSpectate = true;
	public boolean rankGame = false;
	///////////////////////////////////

	public Location loc_Lobby;
	public Location loc_spectate; 
	public List<String> ingamePlayer = new ArrayList<String>(10);
	public HashMap<String, String> rankMap = new HashMap<String, String>();
	public ItemStack helpItem;
	
	public boolean lobbyStart; //���� ���� ī��Ʈ��?
	public boolean ingame; //������ ����?
	public boolean ending; //������?
	
	///�� ������ ������� ���
	public List<MyScheduler> schList = new ArrayList<MyScheduler>(40);
	
	//////////�������
	public MyScheduler startSch = new MyScheduler(this);
	
	public MyScheduler spawnSch = new MyScheduler(null);
	
	//������
	public Minigame(MinigameServer server) {
		/////////////////////////�ʼ� ����
		this.server = server;
		///////////////////////�ڵ� ����(������ ���)
		loc_Lobby = server.lobby;
		helpItem = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = helpItem.getItemMeta();
		meta.setDisplayName("��f[ ��6���� ����� ��f]");
		List<String> lorelist = new ArrayList(1);
		lorelist.add("��f- ��7��Ŭ���� ���� ����̸� ���ϴ�.");
		meta.setLore(lorelist);
		helpItem.setItemMeta(meta);
	}
	
	///////////////////////////////////////// �������̵� �ʿ�
	
	//���� ������, �������̵� �ʼ�
	public void gameHelpMsg(Player p) {
		
	}
	//������ ������, �������̵� �ʼ�
	public void startGame() {

	}
	
	//���� ����, �������̵� �ʼ�
	public void gameQuitPlayer(Player p, boolean announce, boolean isDead) {

	}
	
	//���� ����
	public void endgame(boolean force) {
		
	}
	
	//������ �ݿ�
	public void saveData() {
		
	}
	
	//��ũ ����
	public void setRankMap(String pName) {
		
	}
	
	/////////////////////////////////////////////////
	
	public void dirSetting(String folderName) {
		String path = server.getDataFolder().getAbsolutePath()+"\\"+folderName;
		File file = new File(path);
		if(!file.exists()) {
			file.mkdir();
		}
	}
	
	public ItemStack makeItem(Material mt, String name, int min, int max) {
		ItemStack tmpitem = new ItemStack(mt, 1);
		ItemMeta meta = tmpitem.getItemMeta();
		meta.setDisplayName("��c" + name);
		List<String> lorelist = new ArrayList<String>();
		String damage = "��7���ݷ�: ";
		if (max == min) {
			damage += String.valueOf(min);
		} else {
			damage += min + "-";
			damage += max;
		}
		lorelist.add(damage);
		meta.setLore(lorelist);
		tmpitem.setItemMeta(meta);
		return tmpitem;
	}
	
	public ItemStack makeGun(Material mt, String name, int min, int max) {
		ItemStack tmpitem = new ItemStack(mt, 1);
		ItemMeta meta = tmpitem.getItemMeta();
		meta.setDisplayName("��c" + name);
		List<String> lorelist = new ArrayList<String>();
		String damage = "��7ȭ��: ";
		if (max == min) {
			damage += String.valueOf(min);
		} else {
			damage += min + "-";
			damage += max;
		}
		lorelist.add(damage);
		meta.setLore(lorelist);
		tmpitem.setItemMeta(meta);
		return tmpitem;
	}
	
	public Location loadLocation(String gameName, String locName) {
		File file = new File(server.getDataFolder().getPath() + "/" + gameName + "/Location", "location.yml");
		if (!file.exists() || file.isDirectory())
			return null;
		Location loc;
		String posW;
		int posX, posY, posZ;
		try {
			FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			posW = fileConfig.getString(locName + "_w");
			posX = fileConfig.getInt(locName + "_x");
			posY = fileConfig.getInt(locName + "_y");
			posZ = fileConfig.getInt(locName + "_z");
			loc = new Location(Bukkit.getWorld(posW), posX, posY, posZ);
		} catch (Exception ex) {
			return null;
		}
		return loc;
	}
	
	public void printLog(String str) {
		server.getLogger().info(str);
	}

	public boolean saveLocation(String gameName, String locName, Location loc) {
		try {
			File file = new File(server.getDataFolder().getPath() + "/" + gameName + "/Location", "location.yml");
			FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
			fileConfig.set(locName + "_w", loc.getWorld().getName());
			fileConfig.set(locName + "_x", Integer.valueOf(loc.getBlockX()));
			fileConfig.set(locName + "_y", Integer.valueOf(loc.getBlockY() + 1));
			fileConfig.set(locName + "_z", Integer.valueOf(loc.getBlockZ()));
			fileConfig.save(file);
		} catch (Exception e) {
			printLog("[" + disPlayGameName + "]" + locName + "�������� ���� �߻�");
			return false;
		}
		return true;
	}

	//���� Ż�� ó��
	public void exitGame(Player p) {

	}

	//���� Ż�� ó��
	public void exitGame(String pName) {

	}

	//�̴ϰ����� �����Ѱ�?
	public boolean isReady() {
		return this.checkSetting;
	}

	//�ش� �̴ϰ��ӿ� ������ �÷��̾�Ը� �޼��� ����
	public void sendMessage(String str) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p != null) p.sendMessage(str);
		}
	}
	
	public void sendFullTitle(Player p, int fadeIn, int showTime, int fadeOut, String mainMsg, String subMsg) {
		p.sendTitle(mainMsg, subMsg, fadeIn, showTime, fadeOut);
	}
	
	public void sendTitle(Player p, int fadeIn, int showTime, int fadeOut, String mainMsg) {
		p.sendTitle(mainMsg, "", fadeIn, showTime, fadeOut);
	}
	
	public void sendActionbar(String msg, int tick) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				ActionBarAPI.sendActionBar(p,msg, tick);
		}	
	}
	
	public void sendBossbar(String msg, int sec) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				BarAPI.setMessage(p, msg, sec);
		}	
	}
	
	public void sendTitle(String main, String sub, int tick) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				sendFullTitle(p, 10, tick, 20, main, sub);
		}	
	}
	
	public void sendSound(Sound sound) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
		}
	}
	
	public void sendToLoc(Location l) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				p.teleport(l, TeleportCause.PLUGIN);
		}	
	}
	
	public void sendPotionEffect(PotionEffect pt) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				p.addPotionEffect(pt);
		}	
	}
	
	public void sendGold(List<String> pList, int amt) {
		for (String pName : pList) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null) {
				//server.egGM.giveGold(p.getName(), 40);
			}			
		}	
	}
	
	public void sendSound(Sound sound, float volume, float spd) {
		for (String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			if(p!=null)
				p.playSound(p.getLocation(), sound, volume, spd);
		}
	}
	
//	public boolean checkVictimDelay(String pName, Player victimP) {
//		//Bukkit.broadcastMessage("ȣ���");
//		boolean res = true;
//		
//		if(victimDelayMap.containsKey(pName)) {
//			long lastAttack = victimDelayMap.get(pName);
//			long nowTime = System.currentTimeMillis();
//			long attackInterval = Math.abs(nowTime - lastAttack);		
//			
//			//Bukkit.broadcastMessage(nowTime+" - "+lastAttack+" = "+attackInterval+"���͹�");
//			
//			if(attackInterval < 510) { //�������� 510ms ������ ������ ��	
//				res = false;
//				//Bukkit.broadcastMessage("���� Ÿ�� �Ұ�");
//			}else {
//				victimDelayMap.put(pName, nowTime); //�ֱ� ����Ÿ�� ����		
//			}
//			
//			//���丶�콺 ������
//			List<Long> intervalList = intervalMap.get(pName);
//			if(intervalList == null) {
//				intervalList = new ArrayList<Long>(5);
//				intervalMap.put(pName, intervalList);
//			}
//			if(intervalList.size() >= 5) { //�ִ� 5������ ����
//				intervalList.remove(0);
//			}
//			long clickTime = System.currentTimeMillis();
//			intervalList.add(clickTime);
//			if(server.egWM.checkAutoMouse(pName, intervalList)) {
//				//Bukkit.broadcastMessage("����üũ ����");
//				res = false;
//				//if(existPlayer(victimP)) {
//				//	victimP.sendMessage("��f[ ��cEG ���� ��f] ����� ������ �÷��̾ ���丶�콺�� ��� ���� �� �����ϴ�.");
//				//}
//			}
//		}else {
//			victimDelayMap.put(pName, System.currentTimeMillis());
//			res = true;
//		}	
//		return res;
//	}
	
	public void clearClickMap() {
		victimDelayMap.clear();
		intervalMap.clear();
	}
	
	/*public void spectate(Player p) {
		if(!existPlayer(p)) return;
		p.sendMessage(server.ms_alert + "�̹� ������ ���۵Ǿ����ϴ�. ������ҷ� �̵��Ǹ� ���� ���� ä���� ����մϴ�.");
		p.teleport(loc_spectate);
		server.specList.put(p.getName(), this);
		p.setGameMode(GameMode.SPECTATOR);
		p.setFlying(true);
	}*/
	
	public boolean existPlayer(Player p) {
		if(p == null || !p.isOnline()) return false;
		return true;
	}

	public void joinGame(Player p) {
		if(!doneSetting) {
			p.sendMessage(server.ms_alert+"�ش� �̴ϰ����� ���� ������ �⺻ ������ �Ϸ���� �ʾҽ��ϴ�.");
		} else if(joinBlock) {
			p.sendMessage(server.ms_alert+"�ش� �̴ϰ����� �����ڿ� ���� ���� ���� �Ұ� �����Դϴ�.");
		} else if (ingamePlayer.contains(p.getName())) {
				p.sendMessage(server.ms_alert + "�̹� �� ���ӿ� �������̽ʴϴ�.");
		} else if (ingame) {
			p.sendMessage(server.ms_alert + "�̹� ������ ���۵Ǿ����ϴ�."); 	
		} else if (ingamePlayer.size() >= maxPlayer) {
			p.sendMessage(server.ms_alert + "�̹� �ִ��ο��� "+maxPlayer +"���� �÷��̾ �������Դϴ�.");
		} else {
			MyUtility.allClear(p);
			p.setFoodLevel(20);
			p.closeInventory();
			ingamePlayer.add(p.getName());
			p.getInventory().setItem(8, helpItem);
			p.getInventory().setHeldItemSlot(8);
			p.teleport(loc_Join, TeleportCause.PLUGIN);
			sendMessage(server.ms_alert + ChatColor.AQUA + p.getName() + ChatColor.GRAY + " ���� �����߽��ϴ�. " + ChatColor.RESET
					+ "[ " + ChatColor.GREEN + ingamePlayer.size() + " / "+ minPlayer + ChatColor.RESET + " ]");
			sendTitle(p, 10, 70, 30,ChatColor.RED+""+disPlayGameName);
			sendSound(Sound.BLOCK_NOTE_PLING, 2.0f, 1.0f);
			if(lobbyStart) {
				if(startSch.schTime > 1)
					BarAPI.setMessage(p, ChatColor.GREEN+"���� ���۱���", startSch.schTime);
			} else {
				if ((ingamePlayer.size() >= minPlayer)) {
					startCount();
				} 
			}
			setRankMap(p.getName());
		}
	}
	
	public int broadCast(String msg) {
		int sendCnt = 0;
		//String ms = "&f[ &c�˸� &f] ";
		for(Player p : Bukkit.getOnlinePlayers()) {
			//if(!noAlertList.contains(p.getName()))
			p.sendMessage(msg);
		}
		Bukkit.getLogger().info(msg);
		return sendCnt;
	}
	
	public void startCount() {
		if(ingame) return;
		lobbyStart = true;
		startSch.cancelTask(true);
		startSch.schTime = startCountTime;
		broadCast(server.ms_alert + ChatColor.RED+ChatColor.BOLD + disPlayGameName + ChatColor.GRAY + " ������ " + ChatColor.GRAY + "�� ���۵˴ϴ�.");
		sendBossbar(ChatColor.GREEN+"���� ���۱���", startSch.schTime);
		startSch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
			public void run() {
				if (startSch.schTime > 0) {
					if (startSch.schTime == 30 || startSch.schTime == 10) {
						sendMessage(server.ms_alert + ChatColor.GRAY + "������ "
								+ ChatColor.AQUA + startSch.schTime + ChatColor.GRAY + "�� �� ���۵˴ϴ�.");
						sendSound(Sound.BLOCK_STONE_BREAK, 1.0f, 2.0f);
					}
					startSch.schTime -= 1;
				} else {
					ingame = true;
					lobbyStart = false;
					startSch.cancelTask(true);
					playerSet();
					startGame();
				}
			}
		}, 0L, 20L);
	}
	
	
	public void countDown(int time, String subText) {
		if(ingamePlayer.size() <= 0) return;
		for(String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			timerLevelBar(p, time, false, true);
		}
		MyScheduler sch = new MyScheduler(this);
		sch.schTime = time;
		sch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
			public void run() {
				if(sch.schTime > 0) {
					for(String pName : ingamePlayer) {
						Player p = Bukkit.getPlayer(pName);
						sendFullTitle(p, 0, 30, 0, "��c��l"+sch.schTime, "��e��l"+subText);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
					}
					sch.schTime--;
				}
			}
		}, 0l, 20l);
	}
	
	///���̵�������
	public void removeSidebar(Player p) {
		SidebarString tmpLine = new SidebarString("");
		Sidebar sidebar = new Sidebar("�̴ϰ���", server, 600, tmpLine);
		sidebar.hideFrom(p);
	}
	
	public void playerSet() {
		for(String pName : ingamePlayer) {
			Player p = Bukkit.getPlayer(pName);
			removeSidebar(p);
			//if(existPlayer(p)) {
			//	p.teleport(loc_Join);
			//}		
		}
	}	
	
	public void divideSpawn() {
		if(ingamePlayer.size() <= 0) return;
		if(spawnSch.schId != -1) return;
		List<String> tmpList = new ArrayList<String>(ingamePlayer);
		spawnSch.schTime = tmpList.size();
		spawnSch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
			public void run() {
				if(spawnSch.schTime > 0) {
					spawnSch.schTime--;
					Player p = Bukkit.getPlayer(tmpList.get(spawnSch.schTime));
					if(existPlayer(p))
						spawn(p);
				} else {
					spawnSch.cancelTask(true);
				}
			}
		}, 0l, 2l);
	}
	
	//Ǯ�Ƿ� ������ֱ�
	public void healUp(Player p) {
		p.setHealth((p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
	}
	
	public ItemStack getHead(Player p) {
		//return new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		return SkullCreator.itemFromUuid(p.getUniqueId());
	}
	
	public int getItemDamage(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore())
			return 1;
		List<String> lorelist = item.getItemMeta().getLore();
		if (lorelist.size() <= 0)
			return 1;
		String damagestr = lorelist.get(0);
		if (!damagestr.contains("���ݷ�"))
			return 1;
		// ��7���ݷ�: 7~8
		if (!damagestr.contains("-"))
			return Integer.valueOf(damagestr.substring(7, 8));
		int min = Integer.valueOf(damagestr.substring(7, 8));
		int max = Integer.valueOf(damagestr.substring(9, 10));
		return (int) (Math.random() * (max - min + 1) + min);
	}
	
	public String getHeldMainItemName(Player p){
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return "meta����";
		return item.getItemMeta().getDisplayName();
	}
	
	public boolean takeItem(Player p, ItemStack item, int amt) {
		int tamt = amt;
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (tamt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem == item) {
					tamt -= pitem.getAmount();
					if (tamt <= 0) {
						removeItem(p, item, amt);
						return true;
					}
				}
			}
		}

		return false;
	}
	public boolean takeItem(Player p, Material itemMt, int amt) { //������ ������ �����ϸ� false ��ȯ�ϰ� ��������
		int tamt = amt;
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (tamt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem.getType() == itemMt) {
					tamt -= pitem.getAmount();
					if (tamt <= 0) {
						removeItem(p, itemMt, amt);
						return true;
					}
				}
			}
		}

		return false;
	}

	public void removeItem(Player p, ItemStack item, int amt) { //������ ������ �����ϸ� ������ ��ü  ������ ��ü
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (amt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem == item) {
					if (pitem.getAmount() >= amt) {
						int itemamt = pitem.getAmount() - amt;
						pitem.setAmount(itemamt);
						p.getInventory().setItem(i, amt > 0 ? pitem : null);
						p.updateInventory();
						return;
					} else {
						amt -= pitem.getAmount();
						p.getInventory().setItem(i, null);
						p.updateInventory();
					}
				}
			} else {
				return;
			}
		}
	}
	public void removeItem(Player p, Material itemMt, int amt) {
		for (int i = 0; i < p.getInventory().getSize(); i++) {
			if (amt > 0) {
				ItemStack pitem = p.getInventory().getItem(i);
				if (pitem != null && pitem.getType() == itemMt) {
					if (pitem.getAmount() >= amt) {
						int itemamt = pitem.getAmount() - amt;
						pitem.setAmount(itemamt);
						p.getInventory().setItem(i, amt > 0 ? pitem : null);
						p.updateInventory();
						return;
					} else {
						amt -= pitem.getAmount();
						p.getInventory().setItem(i, null);
						p.updateInventory();
					}
				}
			} else {
				return;
			}
		}
	}
	

	public int sendDistanceMsgToStringList(List<String> nameList, Player p, String str, int distance, int y,boolean useFormat) {
		int sendCnt = 0;
		String logStr = p.getName()+" : "+str+"��f[ ��a"+"�̴ϰ���"+" ��f]";
		Bukkit.getLogger().info(logStr);

			if(useFormat) str = applyFormat(p, str);
			for(String tmpN : nameList) {
				Player tmpP = Bukkit.getPlayer(tmpN);
				if(tmpP == null) continue;
				int pDistance = (int)tmpP.getLocation().distance(p.getLocation());
				if(pDistance <= distance && Math.abs(p.getLocation().getY() - tmpP.getLocation().getY()) < y) {
					tmpP.sendMessage(str);
					sendCnt += 1;
					//if(!noSoundList.contains(tmpN))
					tmpP.playSound(tmpP.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.3f);
				}
			}	
		return sendCnt;
	}
	
	public String applyFormat(Player p, String str) {
		String prefix = null;
		if(prefix != null) {
			return "��f[ ��c"+prefix+" ��f]"+" ��7"+p.getName()+" >> "+str;
		}
		else if(p.isOp()) {
			return "��f[ ��c���� ��f]"+" ��7"+p.getName()+" >> "+str;
		}
		else {
			return "��f[ ��6���� ��f]"+" ��7"+p.getName()+" >> "+str;
		}
	}
	
	public void spawn(Player p) {
		
		if (p != null) {
			p.closeInventory();
			p.setPlayerTime(3000, false);
			p.teleport(server.lobby, TeleportCause.PLUGIN);
			BarAPI.removeBar(p);
			MyUtility.allClear(p);
			if (!p.isDead())
				healUp(p);
			p.setWalkSpeed(0.2f);
			p.setFlySpeed(0.1f);
			p.setGravity(true);
			p.setLevel(0);
			p.setExp(0);
			//p.setFireTicks(20);
			p.setSneaking(false);
			p.setGameMode(GameMode.SURVIVAL);
			p.setAllowFlight(false);
			for (Player t : Bukkit.getOnlinePlayers()) {
				t.showPlayer(p);
			}
			PlayerInventory pInv = p.getInventory();
			pInv.setHeldItemSlot(8);
			pInv.setItem(80, null);
			pInv.setItem(81, null);
			pInv.setItem(82, null);
			pInv.setItem(83, null);
			p.setPlayerWeather(WeatherType.CLEAR);

			Bukkit.getScheduler().scheduleSyncDelayedTask(server, new Runnable() {
				public void run() {
					p.teleport(server.lobby, TeleportCause.PLUGIN);
				}
			}, 2l);
		}
	}
	
	public void sendTeamChat(List<String> tmpList, String str) {
		for(String pName : tmpList) {
			Player p = Bukkit.getPlayer(pName);
			if(existPlayer(p)) p.sendMessage(str);
		}
	}
	
	public void sendTeamSound(List<String> tmpList, Sound sound) {
		for(String pName : tmpList) {
			Player p = Bukkit.getPlayer(pName);
			if(existPlayer(p)) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 1.0F, 1.0F);
		}
	}
	
	public int timerLevelBar(Player p, int time, boolean up ,boolean useLevel) {
		if(!existPlayer(p)) return 0;
		MyScheduler sch = new MyScheduler(this);
		
		if(up) {
			sch.schTime = 0;
			sch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
				public void run() {
					if (sch.schTime < time) {
						if(useLevel) p.setLevel(time-sch.schTime);
						p.setExp((float)sch.schTime / time);
						sch.schTime += 1;
					} else {
						if(useLevel) p.setLevel(0);
						p.setExp(1);
						sch.cancelTask(true);
					}
				}
			}, 0L, 20L);
		} else {
			sch.schTime = time;
			sch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
				public void run() {
					if (sch.schTime > 0) {
						if(useLevel) p.setLevel(sch.schTime);
						p.setExp((float)sch.schTime / time);
						sch.schTime -= 1;
					} else {
						if(useLevel) p.setLevel(0);
						p.setExp(0);
						sch.cancelTask(true);
					}
				}
			}, 0L, 20L);
		}
		return sch.schId;
	}

}
