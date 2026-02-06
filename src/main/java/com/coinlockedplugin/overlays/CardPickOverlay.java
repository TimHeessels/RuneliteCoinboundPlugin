package com.coinlockedplugin.overlays;

import com.coinlockedplugin.CoinlockedPlugin;
import com.coinlockedplugin.data.PackChoiceState;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
public class CardPickOverlay extends Overlay {
    private static final int BUTTON_SIZE = 100;
    private static final int BUTTON_SPACING = 15;
    private static final int PANEL_PADDING = 20;
    private static final int IMAGE_SIZE = 30;
    private static final int HEADER_HEIGHT = 40;
    private static final int PICKED_GAP_AFTER_HEADER = 10;
    private static final int PICKED_GAP_AFTER_CARD = 14;
    private static final int PICKED_UNLOCKED_MAX_LINES = 2;
    private static final int PICKED_GAP_AFTER_UNLOCKED = 12;

    private static final Color TYPE_COLOR = new Color(180, 160, 120);
    private static final Color PANEL_FILL = new Color(40, 32, 24, 240);
    private static final Color PANEL_BORDER = new Color(120, 100, 70, 220);
    private static final Color BUTTON_FILL = new Color(64, 53, 37, 235);
    private static final Color BUTTON_BORDER = new Color(168, 138, 92, 210);
    private static final Color BUTTON_HOVER = new Color(214, 176, 118, 245);
    private static final Color TEXT_COLOR = new Color(238, 224, 186);

    private final CoinlockedPlugin plugin;
    private final Client client;
    private final MouseManager mouseManager;

    private Rectangle buyPackButtonBounds;
    private final Rectangle[] buttonBounds = new Rectangle[4];
    private final String[] buttonLabels = new String[4];
    private final String[] buttonTypeNames = new String[4];
    private final BufferedImage[] buttonImages = new BufferedImage[4];
    private final Runnable[] buttonCallbacks = new Runnable[4];
    private final String[] buttonDescriptions = new String[4];
    private static final int BOTTOM_AREA_HEIGHT = 30;

    // Animation
    private long animationStartTime = 0;
    private static final long CARD_REVEAL_DELAY_MS = 1000;
    private static final long CARD_ANIMATION_DURATION_MS = 400;

    // Picked state (only show picked card + unlocked message + close button)
    private Integer pickedIndex = null;
    private Rectangle closeButtonBounds = null;

    @Inject
    public CardPickOverlay(Client client, CoinlockedPlugin plugin, MouseManager mouseManager) {
        this.client = client;
        this.plugin = plugin;
        this.mouseManager = mouseManager;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public void setButton(int index, String label, String typeName, String description, BufferedImage image,
            Runnable callback) {
        if (index < 0 || index >= 4) {
            return;
        }
        if (index == 0) {
            startAnimation();
            pickedIndex = null;
            closeButtonBounds = null;
        }
        buttonLabels[index] = label;
        buttonTypeNames[index] = typeName;
        buttonDescriptions[index] = description;
        buttonImages[index] = image;
        buttonCallbacks[index] = callback;
    }

    private boolean isCardVisible(int index) {
        return getCardAnimationProgress(index) > 0.0f;
    }

    public void clearButtons() {
        for (int i = 0; i < 4; i++) {
            buttonLabels[i] = null;
            buttonTypeNames[i] = null;
            buttonDescriptions[i] = null;
            buttonImages[i] = null;
            buttonCallbacks[i] = null;
            buttonBounds[i] = null;
        }
        animationStartTime = 0;

        pickedIndex = null;
        closeButtonBounds = null;
    }

    private void pickCard(int index) {
        if (pickedIndex != null) {
            return;
        }
        pickedIndex = index;

        // Run original callback once (this does the actual unlock).
        if (buttonCallbacks[index] != null) {
            buttonCallbacks[index].run();
        }

        // Disable other options immediately.
        for (int i = 0; i < 4; i++) {
            if (i != index) {
                buttonBounds[i] = null;
            }
        }
    }

    private boolean isHoveringCloseButton(MouseEvent e) {
        return pickedIndex != null
                && closeButtonBounds != null
                && closeButtonBounds.contains(e.getPoint());
    }

    private final MouseAdapter mouseListener = new MouseAdapter() {
        private int getHoveredButtonIndex(MouseEvent e) {
            if (pickedIndex != null) {
                return -1;
            }
            if (plugin.getPackChoiceState() != PackChoiceState.PACKGENERATED) {
                return -1;
            }
            if (!plugin.getConfig().showCardMenus()) {
                return -1;
            }

            for (int i = 0; i < buttonBounds.length; i++) {
                if (buttonBounds[i] != null && buttonBounds[i].contains(e.getPoint())) {
                    return i;
                }
            }
            return -1;
        }

        private boolean isHoveringBuyPackButton(MouseEvent e) {
            if (!plugin.getConfig().showCardMenus()) {
                return false;
            }
            return plugin.getPackChoiceState() == PackChoiceState.NONE
                    && plugin.getAvailablePacksToBuy() > 0
                    && buyPackButtonBounds != null
                    && buyPackButtonBounds.contains(e.getPoint());
        }

        @Override
        public MouseEvent mousePressed(MouseEvent e) {
            if (!plugin.getConfig().showCardMenus()) {
                return e;
            }
            if (pickedIndex != null) {
                if (isHoveringCloseButton(e)) {
                    e.consume();
                }
                return e;
            }

            if (getHoveredButtonIndex(e) >= 0 || isHoveringBuyPackButton(e)) {
                e.consume();
            }
            return e;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent e) {
            if (!plugin.getConfig().showCardMenus()) {
                return e;
            }

            if (pickedIndex != null) {
                if (isHoveringCloseButton(e)) {
                    clearButtons();
                    e.consume();
                }
                return e;
            }

            if (isHoveringBuyPackButton(e)) {
                log.info("Buy Pack button clicked");
                plugin.onBuyPackClicked();
                e.consume();
                return e;
            }

            int index = getHoveredButtonIndex(e);
            if (index >= 0) {
                log.info("Button {} clicked", buttonLabels[index]);
                pickCard(index);
                e.consume();
            }
            return e;
        }

        @Override
        public MouseEvent mouseClicked(MouseEvent e) {
            if (!plugin.getConfig().showCardMenus()) {
                return e;
            }
            if (pickedIndex != null) {
                if (isHoveringCloseButton(e)) {
                    e.consume();
                }
                return e;
            }
            if (getHoveredButtonIndex(e) >= 0 || isHoveringBuyPackButton(e)) {
                e.consume();
            }
            return e;
        }

        @Override
        public MouseEvent mouseDragged(MouseEvent e) {
            if (!plugin.getConfig().showCardMenus()) {
                return e;
            }
            if (pickedIndex != null) {
                return e;
            }
            if (getHoveredButtonIndex(e) >= 0 || isHoveringBuyPackButton(e)) {
                e.consume();
            }
            return e;
        }
    };

    public void start() {
        mouseManager.registerMouseListener(mouseListener);
    }

    public void stop() {
        mouseManager.unregisterMouseListener(mouseListener);
    }

    public void startAnimation() {
        animationStartTime = System.currentTimeMillis();
    }

    private float getCardAnimationProgress(int index) {
        if (animationStartTime == 0) {
            return 1.0f;
        }
        long elapsed = System.currentTimeMillis() - animationStartTime;
        long cardStartTime = index * CARD_REVEAL_DELAY_MS;

        if (elapsed < cardStartTime) {
            return 0.0f;
        }
        float progress = (float) (elapsed - cardStartTime) / CARD_ANIMATION_DURATION_MS;
        return Math.min(1.0f, progress);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (!plugin.getConfig().showCardMenus()) {
            return null;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int vpX = client.getViewportXOffset();
        int vpY = client.getViewportYOffset();
        int vpWidth = client.getViewportWidth();

        if (plugin.getPackChoiceState() == PackChoiceState.NONE && pickedIndex == null) {
            if (plugin.getAvailablePacksToBuy() > 0) {
                int buyButtonWidth = 120;
                int buyButtonHeight = 40;
                int buyButtonX = vpX + (vpWidth / 2) - (buyButtonWidth / 2);
                int buyButtonY = vpY + 50;

                buyPackButtonBounds = new Rectangle(buyButtonX, buyButtonY, buyButtonWidth, buyButtonHeight);

                Point mouse = client.getMouseCanvasPosition();
                boolean hovered = mouse != null && buyPackButtonBounds.contains(mouse.getX(), mouse.getY());

                g.setColor(BUTTON_FILL);
                g.fillRoundRect(buyButtonX, buyButtonY, buyButtonWidth, buyButtonHeight, 8, 8);

                g.setColor(hovered ? BUTTON_HOVER : BUTTON_BORDER);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(buyButtonX, buyButtonY, buyButtonWidth, buyButtonHeight, 8, 8);

                g.setColor(TEXT_COLOR);
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g.getFontMetrics();
                String text = "Buy Pack (" + plugin.getAvailablePacksToBuy() + ")";
                int textX = buyButtonX + (buyButtonWidth - fm.stringWidth(text)) / 2;
                int textY = buyButtonY + (buyButtonHeight + fm.getAscent()) / 2 - 2;
                g.drawString(text, textX, textY);
            }
            return null;
        }

        int totalButtonsWidth = (BUTTON_SIZE * 4) + (BUTTON_SPACING * 3);
        int panelWidth = totalButtonsWidth + (PANEL_PADDING * 2);

        int panelX = vpX + (vpWidth / 2) - (panelWidth / 2);
        int panelY = vpY + 50;

        int panelHeight = BUTTON_SIZE + (PANEL_PADDING * 2) + BOTTOM_AREA_HEIGHT + HEADER_HEIGHT
                + (pickedIndex != null ? 40 : 0);

        g.setColor(PANEL_FILL);
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 10, 10);

        g.setColor(PANEL_BORDER);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 10, 10);

        int buttonStartX = panelX + PANEL_PADDING;
        Point mouse = client.getMouseCanvasPosition();

        // Header text
        String headerText = pickedIndex == null ? "Choose a new unlock" : "Your new unlock!";
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics headerFm = g.getFontMetrics();
        int headerX = panelX + (panelWidth - headerFm.stringWidth(headerText)) / 2;
        int headerY = panelY + PANEL_PADDING + headerFm.getAscent();
        g.drawString(headerText, headerX, headerY);

        // Picked state: center card horizontally, then "Unlocked X", then Close button
        // (all centered).
        if (pickedIndex != null) {
            int headerTextHeight = headerFm.getHeight();

            int contentTopY = panelY + PANEL_PADDING + headerTextHeight + PICKED_GAP_AFTER_HEADER;

            int cardX = panelX + (panelWidth - BUTTON_SIZE) / 2;
            int cardY = contentTopY;

            int i = pickedIndex;

            buttonBounds[i] = new Rectangle(cardX, cardY, BUTTON_SIZE, BUTTON_SIZE);

            g.setColor(BUTTON_FILL);
            g.fillRoundRect(cardX, cardY, BUTTON_SIZE, BUTTON_SIZE, 8, 8);

            g.setColor(BUTTON_BORDER);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(cardX, cardY, BUTTON_SIZE, BUTTON_SIZE, 8, 8);

            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();

            if (buttonImages[i] != null) {
                int imgX = cardX + (BUTTON_SIZE - IMAGE_SIZE) / 2;
                int imgY = cardY + 14;
                g.drawImage(buttonImages[i], imgX, imgY, IMAGE_SIZE, IMAGE_SIZE, null);
            }

            if (buttonLabels[i] != null) {
                g.setColor(TEXT_COLOR);
                drawWrappedText(
                        g,
                        buttonLabels[i],
                        cardX + (BUTTON_SIZE / 2),
                        cardY + BUTTON_SIZE - 40,
                        BUTTON_SIZE - 4,
                        2,
                        fm);
            }

            if (buttonTypeNames[i] != null) {
                g.setColor(TYPE_COLOR);
                g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                FontMetrics typeFm = g.getFontMetrics();
                int typeX = cardX + (BUTTON_SIZE - typeFm.stringWidth(buttonTypeNames[i])) / 2;
                int typeY = cardY + BUTTON_SIZE - 6;
                g.drawString(buttonTypeNames[i], typeX, typeY);
            }

            String unlockedText = plugin.newPossibleUnlocksString;

            g.setColor(TEXT_COLOR);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics unlockedFm = g.getFontMetrics();

            int unlockedAreaTopY = cardY + BUTTON_SIZE + PICKED_GAP_AFTER_CARD;
            int unlockedMaxWidth = panelWidth - (PANEL_PADDING * 2);
            int centerX = panelX + (panelWidth / 2);

            drawWrappedText(g, unlockedText, centerX, unlockedAreaTopY, unlockedMaxWidth, PICKED_UNLOCKED_MAX_LINES,
                    unlockedFm);

            int unlockedReservedHeight = unlockedFm.getHeight() * PICKED_UNLOCKED_MAX_LINES;

            int closeW = 90;
            int closeH = 26;

            int closeX = panelX + (panelWidth - closeW) / 2;
            int closeY = unlockedAreaTopY + unlockedReservedHeight + PICKED_GAP_AFTER_UNLOCKED;

            closeButtonBounds = new Rectangle(closeX, closeY, closeW, closeH);

            boolean closeHovered = mouse != null && closeButtonBounds.contains(mouse.getX(), mouse.getY());

            g.setColor(BUTTON_FILL);
            g.fillRoundRect(closeX, closeY, closeW, closeH, 8, 8);

            g.setColor(closeHovered ? BUTTON_HOVER : BUTTON_BORDER);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(closeX, closeY, closeW, closeH, 8, 8);

            g.setColor(TEXT_COLOR);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics closeFm = g.getFontMetrics();
            String closeText = "Close";
            int closeTextX = closeX + (closeW - closeFm.stringWidth(closeText)) / 2;
            int closeTextY = closeY + (closeH + closeFm.getAscent()) / 2 - 2;
            g.drawString(closeText, closeTextX, closeTextY);

            return null;
        }

        String leftInfo = "Cards in deck: " + plugin.getPossibleUnlockablesCount();
        String rightInfo = "Restricted cards: " + plugin.getRestrictedUnlockablesCount();
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics infoFm = g.getFontMetrics();
        int leftInfoX = panelX + PANEL_PADDING;
        int rightInfoX = panelX + panelWidth - PANEL_PADDING - infoFm.stringWidth(rightInfo);
        g.drawString(leftInfo, leftInfoX, headerY);
        g.drawString(rightInfo, rightInfoX, headerY);

        int buttonY = panelY + PANEL_PADDING + HEADER_HEIGHT;

        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();

        int renderStart = 0;
        int renderEnd = 4;

        for (int i = renderStart; i < renderEnd; i++) {
            int buttonX = buttonStartX + (i * (BUTTON_SIZE + BUTTON_SPACING));

            float progress = getCardAnimationProgress(i);

            if (progress <= 0.0f) {
                buttonBounds[i] = null;
                continue;
            }

            buttonBounds[i] = new Rectangle(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);

            float flipProgress = progress < 0.5f
                    ? 1.0f - (progress * 2.0f)
                    : (progress - 0.5f) * 2.0f;

            float scaleX = (float) Math.sin(flipProgress * Math.PI / 2);
            float alpha = Math.min(1.0f, progress * 2.0f);

            java.awt.geom.AffineTransform originalTransform = g.getTransform();
            Composite originalComposite = g.getComposite();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int centerX = buttonX + BUTTON_SIZE / 2;
            g.translate(centerX, 0);
            g.scale(scaleX, 1.0);
            g.translate(-centerX, 0);

            boolean hovered = progress >= 1.0f
                    && mouse != null
                    && buttonBounds[i].contains(mouse.getX(), mouse.getY());

            g.setColor(BUTTON_FILL);
            g.fillRoundRect(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE, 8, 8);

            g.setColor(hovered ? BUTTON_HOVER : BUTTON_BORDER);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE, 8, 8);

            if (progress > 0.5f) {
                if (buttonImages[i] != null) {
                    int imgX = buttonX + (BUTTON_SIZE - IMAGE_SIZE) / 2;
                    int imgY = buttonY + 14;
                    g.drawImage(buttonImages[i], imgX, imgY, IMAGE_SIZE, IMAGE_SIZE, null);
                }

                if (buttonLabels[i] != null) {
                    g.setColor(TEXT_COLOR);
                    drawWrappedText(
                            g,
                            buttonLabels[i],
                            buttonX + (BUTTON_SIZE / 2),
                            buttonY + BUTTON_SIZE - 40,
                            BUTTON_SIZE - 4,
                            2,
                            fm);
                }

                if (buttonTypeNames[i] != null) {
                    g.setColor(TYPE_COLOR);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    FontMetrics typeFm = g.getFontMetrics();
                    int typeX = buttonX + (BUTTON_SIZE - typeFm.stringWidth(buttonTypeNames[i])) / 2;
                    int typeY = buttonY + BUTTON_SIZE - 6;
                    g.drawString(buttonTypeNames[i], typeX, typeY);
                    g.setFont(new Font("SansSerif", Font.BOLD, 10));
                }
            }

            g.setTransform(originalTransform);
            g.setComposite(originalComposite);
        }

        // Hover description
        int hoveredIndex = -1;
        if (mouse != null) {
            for (int i = 0; i < 4; i++) {
                if (buttonBounds[i] != null && buttonBounds[i].contains(mouse.getX(), mouse.getY())) {
                    float progress = getCardAnimationProgress(i);
                    if (progress >= 1.0f) {
                        hoveredIndex = i;
                        break;
                    }
                }
            }
        }

        String desc = "Hover over a card to see more information.";
        if (hoveredIndex >= 0 && buttonDescriptions[hoveredIndex] != null) {
            desc = buttonDescriptions[hoveredIndex];
        }

        g.setColor(TEXT_COLOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics descFm = g.getFontMetrics();
        int descX = panelX + (panelWidth - descFm.stringWidth(desc)) / 2;
        int descY = panelY + PANEL_PADDING + HEADER_HEIGHT + BUTTON_SIZE + BOTTOM_AREA_HEIGHT / 2
                + descFm.getAscent() / 2;
        g.drawString(desc, descX, descY);

        closeButtonBounds = null;
        return null;
    }

    private void drawWrappedText(Graphics2D g, String text, int centerX, int yTop, int maxWidth, int maxLines,
            FontMetrics fm) {
        if (text == null || text.isBlank() || maxLines <= 0) {
            return;
        }

        String[] words = text.trim().split("\\s+");
        List<String> lines = new ArrayList<>(maxLines);
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;

            if (fm.stringWidth(candidate) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
                continue;
            }

            if (current.length() > 0) {
                lines.add(current.toString());
                if (lines.size() >= maxLines) {
                    break;
                }
                current.setLength(0);
            }
            current.append(word);
        }

        if (lines.size() < maxLines && current.length() > 0) {
            lines.add(current.toString());
        }

        int lineHeight = fm.getHeight();
        int y = yTop + fm.getAscent();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int x = centerX - (fm.stringWidth(line) / 2);
            g.drawString(line, x, y);
            y += lineHeight;
        }
    }
}
