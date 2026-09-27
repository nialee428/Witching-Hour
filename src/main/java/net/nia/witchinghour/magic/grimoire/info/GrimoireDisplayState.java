package net.nia.witchinghour.magic.grimoire.info;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GrimoireDisplayState {

    private Text title = Text.literal("");
    private final List<Text> pages = new ArrayList<>();
    private int currentPage = 0;

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getTitle() {
        return title;
    }

    public void clearPages() {
        pages.clear();
        currentPage = 0;
    }

    public void addPage(Text page) {
        pages.add(page);
    }

    public Text getCurrentPage() {
        if (pages.isEmpty()) return Text.literal("");
        return pages.get(currentPage);
    }

    public void nextPage() {
        if (!pages.isEmpty()) {
            currentPage = (currentPage + 1) % pages.size();
        }
    }

    public void previousPage() {
        if (!pages.isEmpty()) {
            currentPage = (currentPage - 1 + pages.size()) % pages.size();
        }
    }
}
