package coma.game.controllers;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.event.EventHandlingManager;
import coma.game.event.KeyboardEvent;
import coma.game.event.MouseEvent;
import coma.game.models.contents.*;

public class EventController {
    public static void init() {
        // onclick
        Resources.playBtn.addEventListener("click", (MouseEvent e) -> {
            e.stopPropagation();

            UIController.getBoxModule("mode-selection-menu").setVisibility(true);
            UIController.getBoxModule("start-menu").setVisibility(false);

            Resources.mode1.setOpacity(MainGame.foe.difficulty == 1 ? 1 : 0.75f);
            Resources.mode2.setOpacity(MainGame.foe.difficulty == 2 ? 1 : 0.75f);
            Resources.mode3.setOpacity(MainGame.foe.difficulty == 3 ? 1 : 0.75f);

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.creditBtn.addEventListener("click", (MouseEvent e) -> {
            e.stopPropagation();

            UIController.getBoxModule("start-menu").setVisibility(false);
            Resources.creditBanner.isVisible = true;

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.how2playBtn.addEventListener("click", (MouseEvent e) -> {
            e.stopPropagation();

            UIController.getBoxModule("start-menu").setVisibility(false);
            Resources.how2playBanner1.isVisible = true;

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.creditBanner.addEventListener("click", (MouseEvent e) -> {
            UIController.getBoxModule("start-menu").setVisibility(true);
            Resources.creditBanner.isVisible = false;

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.how2playBanner1.addEventListener("click", (MouseEvent e) -> {
            e.stopPropagation();

            Resources.how2playBanner1.isVisible = false;
            Resources.how2playBanner2.isVisible = true;

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.how2playBanner2.addEventListener("click", (MouseEvent e) -> {
            e.stopPropagation();

            Resources.how2playBanner1.isVisible = false;
            Resources.how2playBanner2.isVisible = false;
            UIController.getBoxModule("start-menu").setVisibility(true);

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.musicBtn.addEventListener("click", (MouseEvent e) -> {
            final boolean t = Resources.themeMusic.getVolume() == 0;
            Resources.themeMusic.setVolume(t ? MainGame.THEME_VOLUME : 0);
            Resources.musicBtn.setActive(t);
        });

        Resources.mode1.addEventListener("click", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 1) return;

            Resources.mode1.setOpacity(1);
            Resources.mode2.setOpacity(0.75f);
            Resources.mode3.setOpacity(0.75f);

            MainGame.foe.difficulty = 1;
            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.mode2.addEventListener("click", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 2) return;

            Resources.mode1.setOpacity(0.75f);
            Resources.mode2.setOpacity(1);
            Resources.mode3.setOpacity(0.75f);

            MainGame.foe.difficulty = 2;
            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.mode3.addEventListener("click", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 3) return;

            Resources.mode1.setOpacity(0.75f);
            Resources.mode2.setOpacity(0.75f);
            Resources.mode3.setOpacity(1);

            MainGame.foe.difficulty = 3;
            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.startBtn.addEventListener("click", (MouseEvent e) -> {
            UIController.getBoxModule("mode-selection-menu").setVisibility(false);
            UIController.getBoxModule("in-game-menu").setVisibility(true);

            MainGame.user.setup();
            MainGame.foe.setup();
            GameStatus.isGameStarted = true;

            AudioController.playAndSetVolume(Resources.startSound, MainGame.AUDIO_VOLUME);
        });

        Resources.restartBtn.addEventListener("click", (MouseEvent e) -> {
            UIController.getBoxModule("in-game-menu").setVisibility(true);
            UIController.getBoxModule("game-over-menu").setVisibility(false);
            Resources.victoryBanner.isVisible = false;
            Resources.defeatBanner.isVisible = false;

            MainGame.user.setup();
            MainGame.foe.setup();
            GameStatus.isGameStarted = true;

            AudioController.playAndSetVolume(Resources.startSound, MainGame.AUDIO_VOLUME);
        });

        Resources.menuBtn.addEventListener("click", (MouseEvent e) -> {
            UIController.getBoxModule("start-menu").setVisibility(true);
            UIController.getBoxModule("mode-selection-menu").setVisibility(false);
            UIController.getBoxModule("game-over-menu").setVisibility(false);
            Resources.victoryBanner.isVisible = false;
            Resources.defeatBanner.isVisible = false;

            AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
        });

        Resources.unit1.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted) return;

            final Unit u = new MeleeUnit(MainGame.user.era, MeleeUnit.stats[MainGame.user.era - 1]);
            final boolean t = MainGame.user.deployUnit(u);

            if (!t) {
                Resources.unitDescText.textContent = "Requires: " + MainGame.DF.format(u.cost) + " golds!";
                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.unit2.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted) return;

            final Unit u =new RangedUnit(MainGame.user.era, RangedUnit.stats[MainGame.user.era - 1]);
            final boolean t = MainGame.user.deployUnit(u);

            if (!t) {
                Resources.unitDescText.textContent = "Requires: " + MainGame.DF.format(u.cost) + " golds!";
                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.unit3.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted) return;

            final Unit u =new CavalryUnit(MainGame.user.era, CavalryUnit.stats[MainGame.user.era - 1]);
            final boolean t = MainGame.user.deployUnit(u);

            if (!t) {
                Resources.unitDescText.textContent = "Requires: " + MainGame.DF.format(u.cost) + " golds!";
                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.unit4.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted) return;

            final Turret u = Turret.getEra(MainGame.user.era);
            final boolean t = MainGame.user.buildTurret(u);

            if (!t) {
                Resources.unitDescText.textContent = "Requires: " + MainGame.DF.format(u.cost) + " golds!";
                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.unit5.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted || MainGame.user.emergencyUltimateCaller != null) return;

            final boolean t = MainGame.user.era >= 4 ? MainGame.user.useEmergencyUltimate(MainGame.foe) : MainGame.user.upgradeStronghold();

            if (!t) {
                Resources.unitDescText.textContent = "Requires: " +
                        (MainGame.user.era >= 4
                                ? EmergencyUltimate.REQUIRED_XP
                                : MainGame.DF.format(Stronghold.getRequiredXp(MainGame.user.era))) +
                        " xp!";

                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.unitUl.addEventListener("click", (MouseEvent e) -> {
            if (!GameStatus.isGameStarted) return;

            final boolean t = MainGame.user.useUltimate(MainGame.foe);

            if (!t) {
                Resources.unitDescText.textContent = "Ultimate isn't ready!";
                Resources.unitDescText.tempTimer = 40;
            }
        });

        Resources.speedBtn.addEventListener("click", (MouseEvent e) -> {
            final boolean t = MainGame.gameSpeed == 1;
            MainGame.gameSpeed = (byte) (t ? 2 : 1);
            Resources.speedBtn.setActive(t);
        });

        // onkeydown
        EventHandlingManager.global.addEventListener("keyjustpressed", (KeyboardEvent e) -> {
            if (e.code.equals("M")) Resources.musicBtn.click();

            // menu
            if (UIController.getBoxModule("start-menu").isVisible()) {
                if (e.code.equals("Enter")) Resources.playBtn.click();
                else if (e.code.equals("Tab")) Resources.creditBtn.click();
            }
            else if (UIController.getBoxModule("mode-selection-menu").isVisible()) {
                switch (e.code) {
                    case "Left": {
                        switch (MainGame.foe.difficulty) {
                            case 2: Resources.mode1.click(); break;
                            case 3: Resources.mode2.click(); break;
                        }
                    } break;
                    case "Right": {
                        switch (MainGame.foe.difficulty) {
                            case 1: Resources.mode2.click(); break;
                            case 2: Resources.mode3.click(); break;
                        }
                    } break;
                    case "Enter": {
                        Resources.startBtn.click();
                    } break;
                    case "Delete": {
                        Resources.menuBtn.click();
                    } break;
                }
            }
            else if (UIController.getBoxModule("game-over-menu").isVisible()) {
                switch (e.code) {
                    case "Enter": {
                        Resources.restartBtn.click();
                    } break;
                    case "Delete": {
                        Resources.menuBtn.click();
                    } break;
                }
            }
            else if (Resources.creditBanner.isVisible) {
                if (e.code.equals("Delete")) Resources.creditBanner.click();
            }
            else if (Resources.how2playBanner1.isVisible) {
                if (e.code.equals("Right")) Resources.how2playBanner1.click();
                else if (e.code.equals("Delete")) Resources.how2playBanner2.click();
            }
            else if (Resources.how2playBanner2.isVisible) {
                if (e.code.equals("Left")) {
                    Resources.how2playBanner2.isVisible = false;
                    Resources.how2playBanner1.isVisible = true;

                    AudioController.playAndSetVolume(Resources.menuClickSound, MainGame.AUDIO_VOLUME);
                }
                else if (e.code.equals("Delete")) Resources.how2playBanner2.click();
            }

            // in-game
            if (GameStatus.isGameStarted) {
                switch (e.code) {
                    case "Space": {
                        MainGame.camera.position.x = MainGame.camera.viewportWidth/2;
                    } break;
                    case "1": Resources.unit1.click(); break;
                    case "2": Resources.unit2.click(); break;
                    case "3": Resources.unit3.click(); break;
                    case "4": Resources.unit4.click(); break;
                    case "5": Resources.unit5.click(); break;
                    case "6": Resources.unitUl.click(); break;
                    case "S": Resources.speedBtn.click(); break;
                }

                if (MainGame.devMode) {
                    switch (e.code) {
                        case "Q": MainGame.foe.buildTurret(Turret.getEra(MainGame.user.era)); break;
                        case "W": MainGame.user.cash += 1000; break;
                        case "E": MainGame.foe.cash += 1000; break;
                        case "T": MainGame.user.ultimateDelay = 0; break;
                        case "Y": MainGame.foe.ultimateDelay = 0; break;
                        case "A": MainGame.user.xp = Stronghold.getRequiredXp(MainGame.user.era < 4 ? MainGame.user.era : 3); break;
                        case "Z": MainGame.foe.xp = Stronghold.getRequiredXp(MainGame.foe.era < 4 ? MainGame.foe.era : 3); break;
                    }
                }
            }
        });

        EventHandlingManager.global.addEventListener("keypress", (KeyboardEvent e) -> {
            // in-game
            if (GameStatus.isGameStarted) {
                switch (e.code) {
                    case "Left": {
                        final float dm = MainGame.CAMERA_SPEED * MainGame.deltaTime / MainGame.gameSpeed;
                        if (MainGame.camera.position.x - dm > MainGame.camera.viewportWidth / 2) {

                            MainGame.camera.translate(-dm, 0);
                        }
                        else {
                            MainGame.camera.position.x = MainGame.camera.viewportWidth / 2;
                        }
                    } break;
                    case "Right": {
                        final float dm = MainGame.CAMERA_SPEED * MainGame.deltaTime / MainGame.gameSpeed;
                        if (MainGame.camera.position.x + dm < 2080 - MainGame.camera.viewportWidth / 2) {
                            MainGame.camera.translate(dm, 0);
                        }
                        else {
                            MainGame.camera.position.x = 2080 - MainGame.camera.viewportWidth / 2;
                        }
                    } break;
                }
            }
        });
    }
}
