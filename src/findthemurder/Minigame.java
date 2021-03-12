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

	//공격간 딜레이
	public HashMap<String, Long> victimDelayMap = new HashMap<String, Long>();
	public HashMap<String, List<Long>> intervalMap = new HashMap<String, List<Long>>();
	
	////////////////////설정 필수값
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
	
	public boolean lobbyStart; //게임 시작 카운트중?
	public boolean ingame; //게임이 시작?
	public boolean ending; //엔딩중?
	
	///각 게임의 스케쥴들 목록
	public List<MyScheduler> schList = new ArrayList<MyScheduler>(40);
	
	//////////스케쥴용
	public MyScheduler startSch = new MyScheduler(this);
	
	public MyScheduler spawnSch = new MyScheduler(null);
	
	//생성자
	public Minigame(MinigameServer server) {
		/////////////////////////필수 설정
		this.server = server;
		///////////////////////자동 설정(아이템 등등)
		loc_Lobby = server.lobby;
		helpItem = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = helpItem.getItemMeta();
		meta.setDisplayName("§f[ §6게임 도우미 §f]");
		List<String> lorelist = new ArrayList(1);
		lorelist.add("§f- §7우클릭시 게임 도우미를 엽니다.");
		meta.setLore(lorelist);
		helpItem.setItemMeta(meta);
	}
	
	///////////////////////////////////////// 오버라이드 필요
	
	//게임 도움말임, 오버라이드 필수
	public void gameHelpMsg(Player p) {
		
	}
	//게임을 시작함, 오버라이드 필수
	public void startGame() {

	}
	
	//게임 나감, 오버라이드 필수
	public void gameQuitPlayer(Player p, boolean announce, boolean isDead) {

	}
	
	//게임 종료
	public void endgame(boolean force) {
		
	}
	
	//데이터 반영
	public void saveData() {
		
	}
	
	//랭크 설정
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
		meta.setDisplayName("§c" + name);
		List<String> lorelist = new ArrayList<String>();
		String damage = "§7공격력: ";
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
		meta.setDisplayName("§c" + name);
		List<String> lorelist = new ArrayList<String>();
		String damage = "§7화력: ";
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
			printLog("[" + disPlayGameName + "]" + locName + "설정에서 오류 발생");
			return false;
		}
		return true;
	}

	//게임 탈주 처리
	public void exitGame(Player p) {

	}

	//게임 탈주 처리
	public void exitGame(String pName) {

	}

	//미니게임이 가능한가?
	public boolean isReady() {
		return this.checkSetting;
	}

	//해당 미니게임에 참여한 플레이어에게만 메세지 전달
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
//		//Bukkit.broadcastMessage("호출됨");
//		boolean res = true;
//		
//		if(victimDelayMap.containsKey(pName)) {
//			long lastAttack = victimDelayMap.get(pName);
//			long nowTime = System.currentTimeMillis();
//			long attackInterval = Math.abs(nowTime - lastAttack);		
//			
//			//Bukkit.broadcastMessage(nowTime+" - "+lastAttack+" = "+attackInterval+"인터벌");
//			
//			if(attackInterval < 510) { //공격한지 510ms 지나야 공격이 됨	
//				res = false;
//				//Bukkit.broadcastMessage("아직 타격 불가");
//			}else {
//				victimDelayMap.put(pName, nowTime); //최근 공격타임 갱신		
//			}
//			
//			//오토마우스 감별용
//			List<Long> intervalList = intervalMap.get(pName);
//			if(intervalList == null) {
//				intervalList = new ArrayList<Long>(5);
//				intervalMap.put(pName, intervalList);
//			}
//			if(intervalList.size() >= 5) { //최대 5까지만 쌓음
//				intervalList.remove(0);
//			}
//			long clickTime = System.currentTimeMillis();
//			intervalList.add(clickTime);
//			if(server.egWM.checkAutoMouse(pName, intervalList)) {
//				//Bukkit.broadcastMessage("오토체크 감지");
//				res = false;
//				//if(existPlayer(victimP)) {
//				//	victimP.sendMessage("§f[ §cEG 감시 §f] 당신을 공격한 플레이어가 오토마우스를 사용 중인 것 같습니다.");
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
		p.sendMessage(server.ms_alert + "이미 게임이 시작되었습니다. 관전장소로 이동되며 관전 전용 채팅을 사용합니다.");
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
			p.sendMessage(server.ms_alert+"해당 미니게임은 아직 관리자 기본 설정이 완료되지 않았습니다.");
		} else if(joinBlock) {
			p.sendMessage(server.ms_alert+"해당 미니게임은 관리자에 의해 현재 입장 불가 상태입니다.");
		} else if (ingamePlayer.contains(p.getName())) {
				p.sendMessage(server.ms_alert + "이미 이 게임에 참가중이십니다.");
		} else if (ingame) {
			p.sendMessage(server.ms_alert + "이미 게임이 시작되었습니다."); 	
		} else if (ingamePlayer.size() >= maxPlayer) {
			p.sendMessage(server.ms_alert + "이미 최대인원인 "+maxPlayer +"명의 플레이어가 참가중입니다.");
		} else {
			MyUtility.allClear(p);
			p.setFoodLevel(20);
			p.closeInventory();
			ingamePlayer.add(p.getName());
			p.getInventory().setItem(8, helpItem);
			p.getInventory().setHeldItemSlot(8);
			p.teleport(loc_Join, TeleportCause.PLUGIN);
			sendMessage(server.ms_alert + ChatColor.AQUA + p.getName() + ChatColor.GRAY + " 님이 참여했습니다. " + ChatColor.RESET
					+ "[ " + ChatColor.GREEN + ingamePlayer.size() + " / "+ minPlayer + ChatColor.RESET + " ]");
			sendTitle(p, 10, 70, 30,ChatColor.RED+""+disPlayGameName);
			sendSound(Sound.BLOCK_NOTE_PLING, 2.0f, 1.0f);
			if(lobbyStart) {
				if(startSch.schTime > 1)
					BarAPI.setMessage(p, ChatColor.GREEN+"게임 시작까지", startSch.schTime);
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
		//String ms = "&f[ &c알림 &f] ";
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
		broadCast(server.ms_alert + ChatColor.RED+ChatColor.BOLD + disPlayGameName + ChatColor.GRAY + " 게임이 " + ChatColor.GRAY + "곧 시작됩니다.");
		sendBossbar(ChatColor.GREEN+"게임 시작까지", startSch.schTime);
		startSch.schId = Bukkit.getScheduler().scheduleSyncRepeatingTask(server, new Runnable() {
			public void run() {
				if (startSch.schTime > 0) {
					if (startSch.schTime == 30 || startSch.schTime == 10) {
						sendMessage(server.ms_alert + ChatColor.GRAY + "게임이 "
								+ ChatColor.AQUA + startSch.schTime + ChatColor.GRAY + "초 뒤 시작됩니다.");
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
						sendFullTitle(p, 0, 30, 0, "§c§l"+sch.schTime, "§e§l"+subText);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0f, 1.0f);
					}
					sch.schTime--;
				}
			}
		}, 0l, 20l);
	}
	
	///사이드바지우기
	public void removeSidebar(Player p) {
		SidebarString tmpLine = new SidebarString("");
		Sidebar sidebar = new Sidebar("미니게임", server, 600, tmpLine);
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
	
	//풀피로 만들어주기
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
		if (!damagestr.contains("공격력"))
			return 1;
		// §7공격력: 7~8
		if (!damagestr.contains("-"))
			return Integer.valueOf(damagestr.substring(7, 8));
		int min = Integer.valueOf(damagestr.substring(7, 8));
		int max = Integer.valueOf(damagestr.substring(9, 10));
		return (int) (Math.random() * (max - min + 1) + min);
	}
	
	public String getHeldMainItemName(Player p){
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return "meta없음";
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
	public boolean takeItem(Player p, Material itemMt, int amt) { //삭제할 개수가 부족하면 false 반환하고 삭제안함
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

	public void removeItem(Player p, ItemStack item, int amt) { //삭제할 개수가 부족하면 아이템 전체  삭제로 대체
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
		String logStr = p.getName()+" : "+str+"§f[ §a"+"미니게임"+" §f]";
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
			return "§f[ §c"+prefix+" §f]"+" §7"+p.getName()+" >> "+str;
		}
		else if(p.isOp()) {
			return "§f[ §c오피 §f]"+" §7"+p.getName()+" >> "+str;
		}
		else {
			return "§f[ §6유저 §f]"+" §7"+p.getName()+" >> "+str;
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
