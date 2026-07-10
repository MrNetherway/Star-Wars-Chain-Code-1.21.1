package net.netherway.starwarschaincode.client;

import net.neoforged.neoforge.network.PacketDistributor;
import net.netherway.starwarschaincode.network.SelectRacePayload;
import net.netherway.starwarschaincode.race.Race;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RaceSelectScreen extends Screen {

    private final List<Race> races = List.of(Race.values());
    private Race selected;

    public RaceSelectScreen() {
        super(Component.literal("Escolha sua raça"));
    }

    @Override
    protected void init() {
        selected = races.get(0);

        RaceList list = new RaceList(this.minecraft, 260, this.height - 60, 30, 20);
        addRenderableWidget(list);

        addRenderableWidget(Button.builder(Component.literal("Confirmar"), b -> {
            PacketDistributor.sendToServer(new SelectRacePayload(selected));
            onClose();
        }).bounds(this.width / 2 - 60, this.height - 25, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);

        int panelX =280, panelY = 30;
        gfx.drawString(font, selected.getName(), panelX, panelY, 0xFFFFFF);
        gfx.drawWordWrap(font, Component.literal(selected.getDescription()),
                panelX, panelY + 16, this.width - panelX - 20, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() { return true; }

    private class RaceList extends ObjectSelectionList<RaceList.Entry> {
        RaceList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
            races.forEach(r -> addEntry(new Entry(r)));
        }

        class Entry extends ObjectSelectionList.Entry<Entry> {
            final Race race;
            Entry(Race race) { this.race = race; }

            @Override
            public void render(GuiGraphics gfx, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float partialTick) {
                gfx.drawString(font, race.getName(), left + 5, top + 4, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                selected = race;
                return true;
            }

            @Override
            public Component getNarration() { return Component.literal(race.getName()); }
        }
    }
}
