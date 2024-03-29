package dev.sillysaucers.somethingrising.runnables;

import dev.sillysaucers.somethingrising.GamePeriod;
import dev.sillysaucers.somethingrising.SomethingRising;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.concurrent.TimeUnit;

import static dev.sillysaucers.somethingrising.SomethingRising.config;

public class BlockRisingRunnable extends BukkitRunnable {

    public static int originalTicks;
    public static int Y_LEVEL = -65;
    public int blockHeight = 318;
    public int buildHeight = 319;
    private boolean runOnce = false;
    private final Plugin plugin;
    private final World world;
    private int ticksPerRise = 40;
    private Material block = Material.LAVA_CAULDRON;

    private int finalBorderTime = 300;

    public BlockRisingRunnable(Plugin plugin, World world, int ticks) {
        this.plugin = plugin;
        this.world = world;
        originalTicks = ticks;
    }

    public void setTicksPerRise(int ticks) {
        originalTicks = ticks;
    }

    public void setBlock(Material material) {
        block = material;

        if (SomethingRising.CURRENT_STATUS.equals(GamePeriod.ACTIVE)) {

            bs.setBlock(material.createBlockData());

        }

    }

    public void setFinalBorderTime(int seconds) {
        finalBorderTime = seconds;
    }

    public void setBlockHeightLimit(int lavaHeight) {
        this.blockHeight = lavaHeight;
    }

    public void setBuildHeight(int buildHeight) {
        this.buildHeight = buildHeight;
    }

    public void startLavaRise() {
        this.runTaskTimer(plugin, 0, 1);
    }

    public BlockDisplay bs = null;

    public float displaySize = 1;

    @Override
    public void run() {

        if (!runOnce) {

            bs = world.spawn(new Location(world, 0, 0, 0), BlockDisplay.class, (display) -> {
                display.setTransformation(new Transformation(new Vector3f(-50, -65, -50), new AxisAngle4f(), new Vector3f(100, displaySize, 100), new AxisAngle4f()));
            });
            bs.setBlock(block.createBlockData());
            bs.setBrightness(new Display.Brightness(15, 15));
            world.getWorldBorder().setSize(20, TimeUnit.SECONDS, finalBorderTime);
            for (Player pl :
                    Bukkit.getOnlinePlayers()) {
                pl.sendMessage(ChatColor.YELLOW + "Blocks will now rise. If you die beyond this point, you're eliminated. " + ChatColor.LIGHT_PURPLE + "Good luck :3");
            }
            SomethingRising.CURRENT_STATUS = GamePeriod.ACTIVE;
            runOnce = true;

        }

        if (SomethingRising.CURRENT_STATUS == GamePeriod.ENDED) {
            cancel();
        }

        ticksPerRise--;
        if (ticksPerRise <= 0) {
            ticksPerRise = originalTicks;
            if (Y_LEVEL > blockHeight) {
                cancel();
            } else {
                bs.setTransformation(new Transformation(new Vector3f(-50, -65, -50), new AxisAngle4f(), new Vector3f(100, displaySize, 100), new AxisAngle4f()));
                Y_LEVEL++;
                displaySize++;
            }
        }

    }

}
