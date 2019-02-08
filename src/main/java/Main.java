import game.*;
import image.ScreenShot;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import unit.Location;
import unit.Mouse;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Scanner;
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
    private static boolean isAutoBattle;
    private static int battleCounter;
    private static boolean isWon;

    static {
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        rb.setAutoDelay(150);
        Mouse.init(rb);
        isAutoBattle = false;
        battleCounter = 0;
    }

    // 寻找游戏界面，并初始化一些值
    private static void startGame () {
        // 找到游戏界面
        screen = Screen.getInstance();

        screen.init();
        try {
            while (!screen.exist()) {
                System.out.println("找不到游戏窗口，请确保游戏在主页上");
                TimeUnit.MILLISECONDS.sleep(500);
                screen.init();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("找到窗口");

    }

    // 选择要打的章节
    private static void choose (int chapter, int section) {
        if (chapter > 4 || section > 4) {
            System.out.println("选择的章节不对");
            System.exit(1);
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // 是否是正确章节
//        System.out.println("check if right chapter");
        Mat mat = screenShot.screenShotAsMat(screen.x, screen.y + 50, 110, 110);
        Location Chapter = new Location(mat, "chapter" + chapter);

        if (!Chapter.exist()) {
            //     System.out.println("incurrect chapter");
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
                //    System.out.println("跳转第一章");
                Mouse.move(screen.x + 60, screen.y + 360);
                Mouse.click();
                mat = screenShot.screenShotAsMat(screen.x, screen.y + 50, 110, 110);
                chapter1 = new Location(mat, "chapter1");
            }

            // 选择章
            for (int i = 1; i < chapter; i++) {
                Mouse.click(1220, 360);
                //     System.out.println("go to chapter " + i);
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

        // 移动地图
        try {
            TimeUnit.MILLISECONDS.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (bf.getX() != 0 || bf.getY() != 0) {
            int x = (int) (screen.getEndPoint().x) >> 1;
            int y = (int) (screen.getEndPoint().y) >> 1;

            Mouse.move(x, y);
            rb.mousePress(InputEvent.BUTTON1_MASK);
            Mouse.move(x + bf.getOffsetX(), y + bf.getOffsetY());
            rb.mouseRelease(InputEvent.BUTTON1_MASK);
        }
    }

    private static Stack<Point> findShips () {

        Stack<Point> ships = new Stack<>();
        Mat src = screenShot.screenShotAsMat(
                screen.x,
                screen.y,
                screen.getWidth(),
                screen.getHeight());
        Point ship;
        ArrayList<Point> shipList = matchTemplates.INSTANCE.matchMultipleObjects(src, "ship3", 0.6);
        for (Point p : shipList) {
            p.y += 30;
            ships.push(p);
        }

//        ship = matchTemplates.INSTANCE.matchObject(src, "Ammunition", 0.7);
//        if (ship != null) {
//            ship.y += 50;
//            ships.push(ship);
//        }

        shipList = matchTemplates.INSTANCE.matchMultipleObjects(src, "ship2", 0.6);
        for (Point p : shipList) {
            p.y += 30;
            ships.push(p);
        }

        shipList = matchTemplates.INSTANCE.matchMultipleObjects(src, "ship1", 0.55);
        for (Point p : shipList) {
            ships.push(p);
        }
        ship = matchTemplates.INSTANCE.matchObject(src, "ship0", 0.5);
        if (ship != null) {
            ship.y += 40;
            ships.push(ship);
        }
        ship = matchTemplates.INSTANCE.matchObject(src, "QuestionMark", 0.7);
        if (ship != null) {
            ship.y += 50;
            ships.push(ship);
        }


        return ships;
    }

    private static void battle (Stack<Point> ships) {

        Point ship = ships.pop();
        Mat src;
        boolean isAmbush = false;
        int cantArriveCounter = 0;

        // 是否切换第二舰队
//        battleCounter++;
//        if (battleCounter > 6) {
//            src = screenShot.screenShotAsMat(820, 610, 250, 150);
//            Point SwitchOver = matchTemplates.INSTANCE.matchObject(src, "SwitchOver", 0.9);
//            if (SwitchOver == null) System.out.println("找不到切换按钮，切换失败");
//            else Mouse.click(screen.x + 936, screen.y + 664);
//            battleCounter = 0;
//        }

        Mouse.click(ship);
        //   System.out.println("点船");
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        while (true) {
            // 是否进入战前准备
            //     System.out.println("battle preview");
            src = screenShot.screenShotAsMat(
                    860,
                    550,
                    400,
                    200);
            Location BattlePreviewStartButton = new Location(src, "BattlePreviewStartButton");
            // 没有进入战前准备， 可能伏击或无法到达
            if (!BattlePreviewStartButton.exist()) {
                //检查伏击
                System.out.println("检查是否遭受伏击");
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
                    // 有BUG，有时候点不动，多点几次，嘻嘻
                    Mouse.click();
                    Mouse.click();
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
//                    System.out.println("Meet.exit:" + Meet.exist());
                    // 是否出现迎击， 有，则没进入战前检查
                    if (Meet.exist()) {
                        cantArriveCounter++;
                        System.out.println("未进入");
                        if (cantArriveCounter > 3) {
                            if (ships.empty()) {
                                System.out.println("匹配不到船");
                                ships = findShips();
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
            System.out.println("检查是否自动作战");
            src = screenShot.screenShotAsMat(
                    90,
                    70,
                    130,
                    90);
            Location autoFight = new Location(src, "autoFightingDetection");
            if (!autoFight.exist()) {
                System.out.println("没有自动作战");
                Mouse.click(screen.x + 180, screen.y + 90);
                // 点击 “知道了”
                Mouse.click(screen.x + 660, screen.y + 520);
            }
            isAutoBattle = true;
        } else {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 点击出击
        System.out.println("出击");
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
            //   System.out.println("点击");
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
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

          System.load(System.getProperty("user.dir") + "\\lib\\opencv_java401.dll");

        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.print("标枪是不是最萌（请输入【Y或N】）：");
        input = scanner.next().trim();
        if (!(input.equals("Y") || input.equals("y"))) {
            System.out.println("拜拜");
            System.exit(1);
        }
        System.out.println("( •̀ ω •́ )y");
        System.out.print("请输入捞船的关卡（例：3-4）：");
        input = scanner.next().trim();
        System.out.println("你输入的是：" + input);
        if (input.length() != 3) {
            System.out.println("输入无效");
            System.exit(1);
        }
        char[] charArray = input.toCharArray();
        if (charArray[0] < '1' || charArray[0] > '4' || charArray[2] < '1' || charArray[2] > '4') {
            System.out.println("输入无效");
            System.out.println("目前只支持1到4章");
            System.exit(1);
        }
        int chapter = charArray[0] - '0';
        int section = charArray[2] - '0';
        int times = 10;
        startGame();
        // 点击出击按钮
        Mouse.move(screen.x + 1091, screen.y + 398);
        Mouse.click();


        for (int J = 0; J < times; J++) {
            System.out.println("正在进行第" + (J + 1) + "轮捞船");
            choose(chapter, section);
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException ignored) {
                }
                // 检查是否出现紧急任务
                Mat src = screenShot.screenShotAsMat(screen.x, screen.y, screen.getWidth(), screen.getHeight());
                Point p = matchTemplates.INSTANCE.matchObject(src, "confirm", 0.7);
                if (p != null) {
                    System.out.println("出现紧急任务，点击确认");
                    Mouse.click(p);
                }

                Stack<Point> ships = findShips();
                if(!ships.empty()) battle(ships);
                // 是否出关卡，没有，再匹配一次
                else {
                    src = screenShot.screenShotAsMat(820, 610, 250, 150);
                    Point SwitchOver = matchTemplates.INSTANCE.matchObject(src, "SwitchOver", 0.9);
                    // 还在关卡中
                    // 重新匹配
                    if (SwitchOver != null) {
                        // TODO: 2019/2/8 可能是自己挡住了敌舰，移动后再匹配
                        System.out.println("没有发现敌舰，重新匹配");
                        ships = findShips();
                        if(!ships.empty()) battle(ships);
                        else {
                            System.out.println("无法匹配敌舰，结束程序");
                            System.exit(1);
                        }

                    } else {
                        System.out.println("捞船完毕，进行下一轮");
                        break;
                    }
                }

            }
        }
    }

    public static void main1 (String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        Screen screen = Screen.getInstance();
        screen.x = 0;
        screen.y = 30;
        Mat src;


    }
}