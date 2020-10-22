package coma.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import coma.game.components.Canvas;
import coma.game.components.Image;
import coma.game.components.ImageRegion;
import coma.game.components.TextBox;
import coma.game.utils.Asset;
import coma.game.controllers.UIController;

public class Resources {
    public static Canvas devLogo;
    public static Canvas gameLogo;
    public static Canvas creditBanner;
    public static Canvas playBtn;
    public static Canvas creditBtn;
    public static Canvas musicBtn;
    public static Canvas speedBtn;
    public static Canvas mode1;
    public static Canvas mode2;
    public static Canvas mode3;
    public static Canvas modeBanner;
    public static Canvas startBtn;
    public static Canvas menuBtn;
    public static Canvas restartBtn;
    public static Canvas unit1;
    public static Canvas unit2;
    public static Canvas unit3;
    public static Canvas unit4;
    public static Canvas unit5;
    public static Canvas unitUl;
    public static Canvas cashIcon;
    public static Canvas xpIcon;
    public static final Canvas[] unitQueueIcons = new Canvas[10];
    public static Canvas healthBar;
    public static Canvas healthBarL;
    public static Canvas healthBarR;
    public static Canvas queueBar;
    public static Canvas unitQueueBarInner;
    public static Canvas ultimateBarInner;
    public static Canvas victoryBanner;
    public static Canvas defeatBanner;

    public static BitmapFont bitmapFont;
    public static BitmapFont bitmapFont2;
    public static BitmapFont bitmapFont3;
    public static TextBox cashText;
    public static TextBox xpText;
    public static TextBox unitCapText;
    public static TextBox unitDescText;
    public static final TextBox[] unitText = new TextBox[5];

    public static Image bg;
    public static final Image[] strongholdImages = new Image[4];
    public static final Image[] turretImages = new Image[4];
    public static final Image[][] meleeUnitImages = new Image[4][7];
    public static final Image[][] rangedUnitImages = new Image[4][7];
    public static final Image[][] cavalryUnitImages = new Image[4][7];
    public static final Image[] unitQueueImages = new Image[3];
    public static final Image[] ultimateBannerImages = new Image[4];
    public static final Image[] ultimateImages = new Image[4];
    public static ImageRegion explosionImageRegion;
    public static Image ulPlane;
    public static Image unitHealthBar;
    public static Image unitHealthBarInner;

    public static Music themeMusic;
    public static Sound devLogoSound;
    public static Sound startSound;
    public static Sound menuClickSound;
    public static Sound newEraSound;
    public static Sound winSound;
    public static Sound loseSound;
    public static final Sound[] meleeHitSounds = new Sound[4];
    public static final Sound[] rangedHitSounds = new Sound[4];
    public static final Sound[] cavalryHitSounds = new Sound[4];
    public static final Sound[] explosionSounds = new Sound[1];
    public static Sound meleeDie1;
    public static Sound cavalryDie1;
    public static Sound unitCallSound;
    public static Sound ulPlaneSound;

    public static void Load() {
        // set ui position and group module
        bg = new Image("game-bg.png");

        for (byte era = 0; era < 4; era++) {
            strongholdImages[era] = new Image("base-era-" + (era + 1) + ".png");
            turretImages[era] = new Image("turret-era-" + (era + 1) + ".png");
            ultimateBannerImages[era] = new Image("unit-ul-" + (era + 1) + ".png");
            ultimateImages[era] = new Image("ultimate-" + (era + 1) + ".png");
        }

        for (byte era = 0; era < 4; era++) {
            for (byte mov = 0; mov < 7; mov++) {
                meleeUnitImages[era][mov] = new Image("melee-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
                rangedUnitImages[era][mov] = new Image("ranged-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
                cavalryUnitImages[era][mov] = new Image("cavalry-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
            }
        }

        for (byte t = 0; t < unitQueueImages.length; t++) {
            unitQueueImages[t] = new Image("unit-queue-" + (t + 1) + ".png");
        }

        explosionImageRegion = new ImageRegion("explosion-region.png", 128, 128, 4, 4);
        ulPlane = new Image("ul-plane.png");
        unitHealthBar = new Image("unit-health-bar.png");
        unitHealthBarInner = new Image("unit-health-bar-inner.png");

        devLogo = new Canvas("dev-logo.png");
        gameLogo = new Canvas("game-logo.png");
        creditBanner = new Canvas("credit.png");
        playBtn = new Canvas("play-btn.png");
        creditBtn = new Canvas("credit-btn.png");
        musicBtn = new Canvas("music-btn.png");
        speedBtn = new Canvas("speed-btn.png");
        mode1 = new Canvas("mode-1.png");
        mode2 = new Canvas("mode-2.png");
        mode3 = new Canvas("mode-3.png");
        modeBanner = new Canvas("mode-selection-banner.png");
        startBtn = new Canvas("start-btn.png");
        menuBtn = new Canvas("menu-btn.png");
        restartBtn = new Canvas("restart-btn.png");
        unit1 = new Canvas("unit-1.png");
        unit2 = new Canvas("unit-2.png");
        unit3 = new Canvas("unit-3.png");
        unit4 = new Canvas("unit-4.png");
        unit5 = new Canvas("unit-5.png");
        unitUl = new Canvas(ultimateBannerImages[0]);
        cashIcon = new Canvas("cash-icon.png");
        xpIcon = new Canvas("xp-icon.png");

        for (byte i = 0; i < unitQueueIcons.length; i++) {
            unitQueueIcons[i] = new Canvas(unitQueueImages[0].Clone());
        }

        healthBar = new Canvas("health-bar.png");
        healthBarL = new Canvas("health-bar-inner.png");
        healthBarR = new Canvas(healthBarL);
        queueBar = new Canvas("queue-bar.png");
        unitQueueBarInner = new Canvas("unit-queue-bar-inner.png");
        ultimateBarInner = new Canvas("ultimate-bar-inner.png");
        victoryBanner = new Canvas("victory.png");
        defeatBanner = new Canvas("defeat.png");

        bitmapFont = Asset.LoadBitmapFont("fonts/kefa.fnt", false);
        bitmapFont2 = Asset.LoadBitmapFont("fonts/kefa.fnt", false);
        bitmapFont3 = Asset.LoadBitmapFont("fonts/kefa.fnt", false);
        cashText = new TextBox(bitmapFont);
        xpText = new TextBox(bitmapFont);
        unitCapText = new TextBox(bitmapFont);
        unitDescText = new TextBox(bitmapFont2);
        for (int i = 0; i < unitText.length; i++) {
            unitText[i] = new TextBox(bitmapFont3);
        }

        themeMusic = Asset.LoadMusic("audio/theme.mp3");

        devLogoSound = Asset.LoadSound("audio/dev-logo.mp3");
        startSound = Asset.LoadSound("audio/start-game.mp3");
        menuClickSound = Asset.LoadSound("audio/menu-click.mp3");
        newEraSound = Asset.LoadSound("audio/new-era.mp3");
        winSound = Asset.LoadSound("audio/win.mp3");
        loseSound = Asset.LoadSound("audio/lose.mp3");
        ulPlaneSound = Asset.LoadSound("audio/ul-plane.mp3");

        for (byte era = 0; era < meleeHitSounds.length; era++) {
            meleeHitSounds[era] = Asset.LoadSound("audio/melee-hit-" + (era + 1) + ".mp3");
        }

        for (byte era = 0; era < rangedHitSounds.length; era++) {
            rangedHitSounds[era] = Asset.LoadSound("audio/ranged-hit-" + (era + 1) + ".mp3");
        }

        for (byte era = 0; era < cavalryHitSounds.length; era++) {
            cavalryHitSounds[era] = Asset.LoadSound("audio/cavalry-hit-" + (era + 1) + ".mp3");
        }

        explosionSounds[0] = Asset.LoadSound("audio/explosion-1.mp3");
        meleeDie1 = Asset.LoadSound("audio/melee-die-1.mp3");
        cavalryDie1 = Asset.LoadSound("audio/cavalry-die-1.mp3");
        unitCallSound = Asset.LoadSound("audio/call-unit.mp3");
    }

    public static void Setup() {
        gameLogo.SetPosition("center", 400);
        playBtn.SetPosition("center", 300);
        creditBtn.SetPosition("center", 180);
        creditBanner.SetPosition("center", 5);
        creditBanner.isVisible = false;
        musicBtn.SetPosition(886, 14);
        musicBtn.isVisible = false;
        speedBtn.SetPosition(14, 14);
        speedBtn.SetActive(false);
        mode1.SetPosition(80, "center");
        mode2.SetPosition("center", "center");
        mode3.SetPosition(640, "center");
        modeBanner.SetPosition("center", 440);
        startBtn.SetPosition("center", 80);
        startBtn.x += -120;
        menuBtn.SetPosition("center", 80);
        menuBtn.x += 120;
        restartBtn.SetPosition("center", 80);
        restartBtn.x += -120;
        unit1.SetPosition(566, 524);
        unit2.SetPosition(646, 524);
        unit3.SetPosition(726, 524);
        unit4.SetPosition(806, 524);
        unit5.SetPosition(886, 524);
        unitUl.SetPosition(770, 420);
        cashIcon.SetPosition(-28, 504);
        cashIcon.SetScale(0.25f);
        xpIcon.SetPosition(-28, 466);
        xpIcon.SetScale(0.25f);
        for (byte i = 0; i < unitQueueIcons.length; i++) {
            unitQueueIcons[i].SetPosition(566 + 20 * i, 452);
            unitQueueIcons[i].isVisible = false;
        }
        healthBar.SetPosition("center", 14);
        healthBarL.SetPosition(318, 18);
        healthBarR.SetPosition(485, 18);
        healthBarR.SetRotation(180);
        queueBar.SetPosition(566, 482);
        unitQueueBarInner.SetPosition(567, 490);
        ultimateBarInner.SetPosition(567,483);
        victoryBanner.SetPosition("center", 180);
        defeatBanner.SetPosition("center", 180);

        cashText.SetPosition(64, 580);
        cashText.textContent = "0";
        xpText.SetPosition(64, 540);
        xpText.textContent = "0";
        unitCapText.SetPosition(22, 500);
        unitCapText.textContent = "0/NaN";
        unitDescText.SetPosition(248, 580);
        unitDescText.textContent = "";

        for (int i = 0; i < unitText.length; i++) {
            unitText[i].SetPosition(576 + (i * 80), 516);
            unitText[i].textContent = "0";
        }

        bitmapFont2.getData().setScale(0.8f);
        bitmapFont3.getData().setScale(0.4f);

        UIController.AddBoxModule("start-menu", gameLogo, playBtn, creditBtn);
        UIController.AddBoxModule("mode-selection-menu", mode1, mode2, mode3, modeBanner, startBtn, menuBtn);
        UIController.AddBoxModule("in-game-menu", speedBtn, unit1, unit2, unit3, unit4, unit5, unitUl,
                cashIcon, xpIcon, cashText, xpText, unitCapText, unitDescText, unitText[0],
                unitText[1], unitText[2], unitText[3], unitText[4], healthBar, healthBarL, healthBarR, queueBar,
                unitQueueBarInner, ultimateBarInner, unitQueueIcons[0], unitQueueIcons[1], unitQueueIcons[2],
                unitQueueIcons[3], unitQueueIcons[4], unitQueueIcons[5], unitQueueIcons[6], unitQueueIcons[7],
                unitQueueIcons[8], unitQueueIcons[9]);
        UIController.AddBoxModule("game-over-menu", restartBtn, menuBtn, victoryBanner, defeatBanner);
        UIController.GetBoxModule("start-menu").SetVisibility(false);
        UIController.GetBoxModule("game-over-menu").SetVisibility(false);
        UIController.GetBoxModule("in-game-menu").SetVisibility(false);
        UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);

        // set sounds and music
        themeMusic.setLooping(true);
        themeMusic.setVolume(MainGame.THEME_VOLUME);

        // set ui
        UIController.AddComponents(devLogo, gameLogo, playBtn, creditBtn, creditBanner, musicBtn, mode1, mode2, mode3, modeBanner, startBtn, restartBtn,
                menuBtn, speedBtn, unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon, xpIcon, healthBar, healthBarL, healthBarR, queueBar,
                unitQueueBarInner, ultimateBarInner, victoryBanner, defeatBanner, unitQueueIcons[0], unitQueueIcons[1],
                unitQueueIcons[2], unitQueueIcons[3], unitQueueIcons[4], unitQueueIcons[5], unitQueueIcons[6],
                unitQueueIcons[7], unitQueueIcons[8], unitQueueIcons[9]);
        UIController.AddComponents(cashText, xpText, unitCapText, unitDescText, unitText[0], unitText[1], unitText[2], unitText[3], unitText[4]);
    }
}
