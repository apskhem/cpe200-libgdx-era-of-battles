package coma.game.controllers;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.event.EventHandlingManager;
import coma.game.event.KeyboardEvent;
import coma.game.event.MouseEvent;
import coma.game.models.contents.*;

public class EventController {
    public static void Init() {
        // onclick
        Resources.playBtn.AddEventListener("onclick", (MouseEvent e) -> {
            e.StopPropagation();

            UIController.GetBoxModule("mode-selection-menu").SetVisibility(true);
            UIController.GetBoxModule("start-menu").SetVisibility(false);

            Resources.mode1.SetOpacity(MainGame.foe.difficulty == 1 ? 1 : 0.75f);
            Resources.mode2.SetOpacity(MainGame.foe.difficulty == 2 ? 1 : 0.75f);
            Resources.mode3.SetOpacity(MainGame.foe.difficulty == 3 ? 1 : 0.75f);

            Resources.menuClickSound.play();
        });

        Resources.creditBtn.AddEventListener("onclick", (MouseEvent e) -> {
            e.StopPropagation();

            UIController.GetBoxModule("start-menu").SetVisibility(false);
            Resources.creditBanner.isVisible = true;

            Resources.menuClickSound.play();
        });

        Resources.creditBanner.AddEventListener("onclick", (MouseEvent e) -> {
            UIController.GetBoxModule("start-menu").SetVisibility(true);
            Resources.creditBanner.isVisible = false;

            Resources.menuClickSound.play();
        });

        Resources.musicBtn.AddEventListener("onclick", (MouseEvent e) -> {
            final boolean t = Resources.themeMusic.getVolume() == 0;
            Resources.themeMusic.setVolume(t ? 0.7f : 0);
            Resources.musicBtn.SetActive(t);
        });

        Resources.mode1.AddEventListener("onclick", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 1) return;

            Resources.mode1.SetOpacity(1);
            Resources.mode2.SetOpacity(0.75f);
            Resources.mode3.SetOpacity(0.75f);

            MainGame.foe.difficulty = 1;
            Resources.menuClickSound.play();
        });

        Resources.mode2.AddEventListener("onclick", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 2) return;

            Resources.mode1.SetOpacity(0.75f);
            Resources.mode2.SetOpacity(1);
            Resources.mode3.SetOpacity(0.75f);

            MainGame.foe.difficulty = 2;
            Resources.menuClickSound.play();
        });

        Resources.mode3.AddEventListener("onclick", (MouseEvent e) -> {
            if (MainGame.foe.difficulty == 3) return;

            Resources.mode1.SetOpacity(0.75f);
            Resources.mode2.SetOpacity(0.75f);
            Resources.mode3.SetOpacity(1);

            MainGame.foe.difficulty = 3;
            Resources.menuClickSound.play();
        });

        Resources.startBtn.AddEventListener("onclick", (MouseEvent e) -> {
            UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
            UIController.GetBoxModule("in-game-menu").SetVisibility(true);

            MainGame.user.Setup();
            MainGame.foe.Setup();
            GameStatus.isGameStarted = true;

            Resources.startSound.play();
        });

        Resources.restartBtn.AddEventListener("onclick", (MouseEvent e) -> {
            UIController.GetBoxModule("in-game-menu").SetVisibility(true);
            UIController.GetBoxModule("game-over-menu").SetVisibility(false);
            Resources.victoryBanner.isVisible = false;
            Resources.defeatBanner.isVisible = false;

            MainGame.user.Setup();
            MainGame.foe.Setup();
            GameStatus.isGameStarted = true;

            Resources.startSound.play();
        });

        Resources.menuBtn.AddEventListener("onclick", (MouseEvent e) -> {
            UIController.GetBoxModule("start-menu").SetVisibility(true);
            UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
            UIController.GetBoxModule("game-over-menu").SetVisibility(false);
            Resources.victoryBanner.isVisible = false;
            Resources.defeatBanner.isVisible = false;

            Resources.menuClickSound.play();
        });

        Resources.unit1.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.DeployUnit(new MeleeUnit(MainGame.user.era, MeleeUnit.stats[MainGame.user.era - 1]));
        });
        Resources.unit2.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.DeployUnit(new RangedUnit(MainGame.user.era, RangedUnit.stats[MainGame.user.era - 1]));
        });
        Resources.unit3.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.DeployUnit(new CavalryUnit(MainGame.user.era, CavalryUnit.stats[MainGame.user.era - 1]));
        });
        Resources.unit4.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.BuildTurret(Turret.GetEra(MainGame.user.era));
        });
        Resources.unit5.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.UpgradeStronghold();
        });
        Resources.unitUl.AddEventListener("onclick", (MouseEvent e) -> {
            MainGame.user.UseUltimate();
        });

        Resources.speedBtn.AddEventListener("onclick", (MouseEvent e) -> {
            final boolean t = MainGame.gameSpeed == 1;
            MainGame.gameSpeed = (byte) (t ? 2 : 1);
            Resources.speedBtn.SetActive(t);
        });

        // onkeydown
        EventHandlingManager.global.AddEventListener("onkeyjustpressed", (KeyboardEvent e) -> {
            if (e.code.equals("M")) Resources.musicBtn.Click();

            // menu
            if (UIController.GetBoxModule("start-menu").IsVisible()) {
                if (e.code.equals("Enter")) Resources.playBtn.Click();
                else if (e.code.equals("Tab")) Resources.creditBtn.Click();
            }
            else if (UIController.GetBoxModule("mode-selection-menu").IsVisible()) {
                switch (e.code) {
                    case "Left": {
                        switch (MainGame.foe.difficulty) {
                            case 2: Resources.mode1.Click(); break;
                            case 3: Resources.mode2.Click(); break;
                        }
                    } break;
                    case "Right": {
                        switch (MainGame.foe.difficulty) {
                            case 1: Resources.mode2.Click(); break;
                            case 2: Resources.mode3.Click(); break;
                        }
                    } break;
                    case "Enter": {
                        Resources.startBtn.Click();
                    } break;
                    case "Delete": {
                        Resources.menuBtn.Click();
                    } break;
                }
            }
            else if (UIController.GetBoxModule("game-over-menu").IsVisible()) {
                switch (e.code) {
                    case "Enter": {
                        Resources.restartBtn.Click();
                    } break;
                    case "Delete": {
                        Resources.menuBtn.Click();
                    } break;
                }
            }
            else if (Resources.creditBanner.isVisible) {
                if (e.code.equals("Delete")) Resources.creditBanner.Click();
            }

            // in-game
            if (GameStatus.isGameStarted) {
                switch (e.code) {
                    case "Space": {
                        MainGame.camera.position.x = MainGame.camera.viewportWidth/2;
                    } break;
                    case "1": Resources.unit1.Click(); break;
                    case "2": Resources.unit2.Click(); break;
                    case "3": Resources.unit3.Click(); break;
                    case "4": Resources.unit4.Click(); break;
                    case "5": Resources.unit5.Click(); break;
                    case "6": Resources.unitUl.Click(); break;
                    case "S": Resources.speedBtn.Click(); break;
                }

                if (MainGame.devMode) {
                    switch (e.code) {
                        case "Q": MainGame.foe.BuildTurret(Turret.GetEra(MainGame.user.era)); break;
                        case "W": MainGame.user.cash += 1000; break;
                        case "E": MainGame.foe.cash += 1000; break;
                        case "T": MainGame.user.ultimateDelay = 0; break;
                        case "Y": MainGame.foe.ultimateDelay = 0; break;
                        case "A": MainGame.user.xp = Stronghold.GetRequiredXp(MainGame.user.era < 4 ? MainGame.user.era : 3); break;
                        case "Z": MainGame.foe.xp = Stronghold.GetRequiredXp(MainGame.foe.era < 4 ? MainGame.foe.era : 3); break;
                    }
                }
            }
        });

        EventHandlingManager.global.AddEventListener("onkeypress", (KeyboardEvent e) -> {
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
