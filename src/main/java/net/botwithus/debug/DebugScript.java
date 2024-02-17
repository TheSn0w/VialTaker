package net.botwithus.debug;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.input.KeyboardInput;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DebugScript extends LoopingScript {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public DebugScript(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
    }

    @Override
    public void onLoop() {
        SceneObject portableWell = SceneObjectQuery.newQuery().name("Portable well").results().nearest();
        if (portableWell != null && portableWell.interact("Take Vials")) {
            boolean interfaceOpened = Execution.delayUntil((30000), () -> Interfaces.isOpen(1469));
            if (interfaceOpened) {
                KeyboardInput.enter("28");
                KeyboardInput.pressKey(KeyEvent.VK_ENTER);
                boolean itemObtained = Execution.delayUntil((30000), () -> Backpack.contains("Vial of water"));
                if (itemObtained) {
                    SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                    if (bankChest != null && bankChest.interact("Load Last Preset from")) {
                        Execution.delayUntil((30000), () -> !Backpack.contains("Vial of water"));
                    }
                }
            }
        }
    }

    private boolean delayUntil(Supplier<Boolean> condition, long timeoutMillis) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (!condition.get()) {
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                return false; // Timeout reached
            }
            TimeUnit.MILLISECONDS.sleep(50); // Check condition every 50ms
        }
        return true; // Condition met
    }
}