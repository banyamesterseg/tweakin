package com.github.sachin.tweakin.manager;

import com.github.sachin.tweakin.BaseTweak;
import com.github.sachin.tweakin.Message;
import com.github.sachin.tweakin.Tweakin;
import com.github.sachin.tweakin.autorecipeunlock.AutoRecipeUnlockTweak;
import com.github.sachin.tweakin.betterladder.BetterLadderTweak;
import com.github.sachin.tweakin.burnvinetip.BurnVineTipTweak;
import com.github.sachin.tweakin.controlledburn.ControlledBurnTweak;
import com.github.sachin.tweakin.coordinatehud.CoordinateHUDTweak;
import com.github.sachin.tweakin.customportals.CustomPortalTweak;
import com.github.sachin.tweakin.fastleafdecay.FastLeafDecayTweak;
import com.github.sachin.tweakin.lapisintable.LapisInTableTweak;
import com.github.sachin.tweakin.netherportalcoords.NetherPortalCoordsTweak;
import com.github.sachin.tweakin.noteblock.NoteBlockHeadsTweak;
import com.github.sachin.tweakin.poisonpotatousage.PoisonPotatoUsageTweak;
import com.github.sachin.tweakin.reacharound.ReachAroundTweak;
import com.github.sachin.tweakin.rightclickarmor.RightClickArmor;
import com.github.sachin.tweakin.rightclickshulker.RightClickShulkerBox;
import com.github.sachin.tweakin.rotationwrench.RotationWrenchItem;
import com.github.sachin.tweakin.silencemobs.SilenceMobsTweak;
import com.github.sachin.tweakin.swingthroughgrass.SwingThroughGrassTweak;
import com.github.sachin.tweakin.utils.ConfigUpdater;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweakManager {

    private final Tweakin plugin;
    private Message messageManager;
    private List<BaseTweak> tweakList = new ArrayList<>();
    private FileConfiguration recipeConfig;


    public TweakManager(Tweakin plugin){
        this.plugin = plugin;
    }

    public void load(){
        plugin.getLogger().info("Loading tweakin...");
        reload(false);
    }

    public void reload(){
        plugin.getLogger().info("Reloading tweakin...");
        reload(true);
    }

    private void reload(boolean unregister){
        plugin.saveDefaultConfig();
        int registered = 0;
        File configFile = new File(plugin.getDataFolder(),"config.yml");
        File recipeFile = new File(plugin.getDataFolder(),"recipes.yml");
        if(!recipeFile.exists()){
            plugin.saveResource("recipes.yml", false);
        }
        this.recipeConfig = YamlConfiguration.loadConfiguration(recipeFile);
        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();
        this.messageManager = new Message(plugin);
        messageManager.reload();
        for (BaseTweak t : getTweakList()) {
            t.reload();
            if(unregister){

                if(t.registered){
                    t.unregister();
                }
            }
            if(t.shouldEnable()){
                t.register();
                registered++;
            }
        }
        plugin.getLogger().info("Registered "+registered+" tweaks successfully");
        Bukkit.getOnlinePlayers().forEach(p -> p.updateCommands());
    }
    public List<BaseTweak> getTweakList() {
        if(tweakList.isEmpty()){
            tweakList.add(new RightClickArmor(plugin));
            tweakList.add(new RightClickShulkerBox(plugin));
            tweakList.add(new NoteBlockHeadsTweak(plugin));
            tweakList.add(new ReachAroundTweak(plugin));
            tweakList.add(new FastLeafDecayTweak(plugin));
            tweakList.add(new BetterLadderTweak(plugin));
            tweakList.add(new LapisInTableTweak(plugin));
            tweakList.add(new CustomPortalTweak(plugin));
            // tweakList.add(new ControlledBurnTweak(plugin)); work in progress
            tweakList.add(new AutoRecipeUnlockTweak(plugin));
            tweakList.add(new NetherPortalCoordsTweak(plugin));
            tweakList.add(new SwingThroughGrassTweak(plugin));
            tweakList.add(new CoordinateHUDTweak(plugin));
            tweakList.add(new PoisonPotatoUsageTweak(plugin));
            tweakList.add(new BurnVineTipTweak(plugin));
            tweakList.add(new SilenceMobsTweak(plugin));
            tweakList.add(new RotationWrenchItem(plugin));
        }
        return tweakList;
    }

    public Message getMessageManager() {
        return messageManager;
    }

    public FileConfiguration getRecipeFile(){
        return recipeConfig;
    }
}


