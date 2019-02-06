import game.Battlefront;
import game.FindShipsThread;
import game.Screen;
import image.ScreenShot;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import unit.Location;
import unit.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * @author Twikura
 * @create 2019/2/1 14:00
 */
public class Main {
    private static Screen screen = Screen.getInstance();
    private static Robot rb;
    private static ScreenShot screenShot = new ScreenShot();
    static boolean isAutoBattle;

    static {
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        rb.setAutoDelay(100);
        Mouse.init(rb);
        isAutoBattle = false;
    }

    // 寻找游戏界面，并初始化一些值
    public static void startGame () {
        // 找到游戏界面
        screen = Screen.getInstance();

        screen.init();

        while (!screen.exist()) {
            System.out.println("找不到窗口");
            screen.init();
        }
        System.out.println("找到窗口");

    }

    // 选择要打的章节
    public static void choose (int chapter, int section) {
        if (chapter > 4 || section > 4) {
            System.out.println("选择的章节不对");
            System.exit(1);
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ie) {
        }

        // 是否是正确章节
        System.out.println("check if right chapter");
        Mat mat = screenShot.screenShotAsMat(screen.x, screen.y + 50, 110, 110);
        Location Chapter = new Location(mat, "chapter" + chapter);

        if (!Chapter.exist()) {
            System.out.println("incurrect chapter");
            Location chapter1 = new Location(mat, "chapter1");
            // 选择章节
            int counter = 0;
            // 先移动到第一章
            while (!chapter1.exist()) {
                counter++;
                if (counter > 9) {
                    System.out.println("无法到达第一章");
                    System.exit(1);
                }
                System.out.println("跳转第一章");
                Mouse.move(screen.x + 60, screen.y + 360);
                Mouse.click();
                mat = screenShot.screenShotAsMat(screen.x, screen.y + 50, 110, 110);
                chapter1 = new Location(mat, "chapter1");
            }

            // 选择章
            for (int i = 1; i < chapter; i++) {
                Mouse.click(1220, 360);
                System.out.println("go to chapter " + i);
            }
        }

        // 点击节
        Battlefront bf = Battlefront.getBf();
        bf.init();
        bf.set(chapter, section);
        Mouse.move(screen.x + bf.getX(), screen.y + bf.getY());
        Mouse.click();
        Mouse.move(screen.x + 950, screen.y + 487);
        Mouse.click();
        Mouse.move(screen.x + 1053, screen.y + 626);
        Mouse.click();
    }

    public static Stack<Point> findShips () {
        Mat src;
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException ie) {
        }
        ExecutorService exec = Executors.newCachedThreadPool();
        Stack<Point> ships = new Stack<>();
        Stack<Future<ArrayList<Point>>> Fships = new Stack<>();
        src = screenShot.screenShotAsMat(
                screen.x,
                screen.y,
                screen.getWidth(),
                screen.getHeight());
        for (int i = 0; i < 4; i++) {
            Fships.add(exec.submit(new FindShipsThread(src, "ship" + i)));
        }
        ArrayList<Point> temp = new ArrayList<>(5);
        Future<ArrayList<Point>> FAP;
        while (!Fships.empty()) {
            FAP = Fships.pop();
            try {
                temp = FAP.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            for (Point p : temp) {
                ships.push(p);
            }
        }
        return ships;
    }

    public static void battle (Stack<Point> ships) {
        Point ship = ships.pop();
        Mat src;
        boolean isAirRaid = false;
        boolean isAmbush = false;
        int cantArriveCounter = 0;
        Mouse.click(ship);
        System.out.println("点船");
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException ie) {
        }

        while (true) {
            // 是否进入战前准备
            System.out.println("battle preview");
            src = screenShot.screenShotAsMat(
                    860,
                    550,
                    400,
                    200);
            Location BattlePreviewStartButton = new Location(src, "BattlePreviewStartButton");
            // 没有进入战前准备， 可能伏击或无法到达
            if (!BattlePreviewStartButton.exist()) {
                //检查伏击
                System.out.println("检查伏击");
                src = screenShot.screenShotAsMat(
                        screen.x + 430,
                        screen.y + 300,
                        350,
                        170);
                Location ambush = new Location(src, "FightAmbush");
                isAmbush = ambush.exist();
                // 如果伏击
                if (isAmbush) {
                    // 出击
                    Mouse.move(screen.x + 625, screen.y + 370);
                    Mouse.click();
                    break;
                } else {
                    // 是否无法到达
                    src = screenShot.screenShotAsMat(
                            screen.x + 1030,
                            screen.y + 610,
                            250,
                            110);
                    Location Meet = new Location(src, "Meet");
                    // 没有进入 战前检查，这换一个船
                    System.out.println("Meet.exit:" + Meet.exist());
                    // 是否出现迎击， 有，则没进入战前检查
                    if (Meet.exist()) {
                        cantArriveCounter++;
                        System.out.println("未进入");
                        if (cantArriveCounter > 4) {
                            if (ships.empty()) {
                                System.out.println("匹配不到船");
                                System.exit(1);
                            }
                            battle(ships);// 重新选一个船
                            return;
                        }
                    }
                }

            } else break;
        }

        // 检查是否自动
        if (!isAutoBattle) {
            System.out.println("检查自动");
            src = screenShot.screenShotAsMat(
                    90,
                    70,
                    130,
                    90);
            Location autoFight = new Location(src, "autoFightingDetection");
            if (!autoFight.exist()) {
                System.out.println("Not auto fight");
                Mouse.click(screen.x + 180, screen.y + 90);
                // 点击 “知道了”
                Mouse.click(screen.x + 660, screen.y + 520);
                isAutoBattle = true;
            }
        }

        // 点击出击
        System.out.println("点击出击");
        Mouse.click(screen.x + 1040, screen.y + 630);
        System.out.println("战斗中！");

        // 睡20秒
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // 是否胜利
        Location victory;
        do {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            src = screenShot.screenShotAsMat(
                    screen.x + 300,
                    screen.y + 200,
                    700,
                    250);
            victory = new Location(src, "victory");

        } while (!victory.exist());

        Mouse.move(screen.x + 655, screen.y + 560);
        System.out.println("战斗结束！");
        Location confirmAfterBattle;
        Location confirmAfterGetNewBoat;

        // 点击确认
        do {
            Mouse.click();
            System.out.println("点击");
//            src = screenShot.screenShotAsMat(
//                    screen.x + 945,
//                    screen.y + 550,
//                    280,
//                    120);
            src = screenShot.screenShotAsMat(
                    screen.x,
                    screen.y,
                    1280,
                    720);
            confirmAfterGetNewBoat = new Location(src, "confirm");
            confirmAfterBattle = new Location(src, "confirmAfterBattle");
            if (confirmAfterGetNewBoat.exist()) {
                System.out.println("获得新船");
                Mouse.click(confirmAfterGetNewBoat.x, confirmAfterGetNewBoat.y);
            }

        } while (!confirmAfterBattle.exist());
        Mouse.click(screen.x + 1100, screen.y + 600);

        // 如果空袭，继续战斗
        if (isAmbush) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            ships.push(ship);
            battle(ships);
        }
    }

    public static void main (String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.load(System.getProperty("user.dir")+"\\lib\\opencv_java401.dll");
        if(args.length!=2){
            System.out.println("输入参数不对");
        }
        int chapter = Integer.parseInt(args[0]);
        int section = Integer.parseInt(args[1]);
        startGame();
        // 点击出击按钮
        Mouse.move(screen.x + 1091, screen.y + 398);
        Mouse.click();


        for (int J = 0; J < 30; J++) {

            System.out.println(J + "轮");
            choose(chapter, section);

            for (int i = 0; i < 10; i++) {
                Stack<Point> ships = findShips();
                if (ships.empty()) {
                    System.out.println("no ship matched");
                    break;
                }
                battle(ships);
            }
        }
    }

    public static void main1 (String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//
//        Screen screen = Screen.getInstance();
//        screen.x = 0;
//        screen.y = 30;
//        findShips();
        System.out.println((System.getProperty("user.dir")+"\\lib\\opencv_java401.dll"));
    }


}
