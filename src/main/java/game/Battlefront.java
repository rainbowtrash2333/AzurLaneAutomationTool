package game;

import java.util.ArrayList;

/**
 * @author Twikura
 * @create 2019/2/2 15:20
 */
public class Battlefront {
   private static  Battlefront bf = new Battlefront();
   private Battlefront(){}

    public static Battlefront getBf () {
        return bf;
    }
   private int chapter, section;
   public void set(int chapter,int section){
       this.chapter=chapter;
       this.section=section;
   }
    private  ArrayList<Node> nodes = new ArrayList<>(16);

    public  void init () {
        // 第一章数据
        nodes.add(new Node(153, 435));
        nodes.add(new Node(450, 265 ));
        nodes.add(new Node(650, 620 ));
        nodes.add(new Node(775, 175 ));
        // 第二章数据
        nodes.add(new Node(810, 447));
        nodes.add(new Node(745, 190 ));
        nodes.add(new Node(280, 270 ));
        nodes.add(new Node(380, 540 ));
        // 第三章数据
        nodes.add(new Node(400, 210));
        nodes.add(new Node(230, 500 ));
        nodes.add(new Node(490, 130 ));
        nodes.add(new Node(610, 360 ));
        // 第四章数据
        nodes.add(new Node(230, 300));
        nodes.add(new Node(420, 450 ));
      //  nodes.add(new Node(490, 130 ));
       // nodes.add(new Node(610, 360 ));
    }
    public  int getX(){
        if(chapter<1||section<1){
            System.out.println("未初始化章，节");
            System.exit(1);
        }
        return nodes.get((chapter*4+section-5)).x;
    }
    public  int getY(){
        if(chapter<1||section<1){
            System.out.println("未初始化章，节");
            System.exit(1);
        }
        return nodes.get((chapter*4+section-5)).y;
    }

    public static void main (String[] args) {
        Battlefront bf = Battlefront.getBf();
        bf.init();
        bf.set(1,1);
        System.out.println(bf.getX());
    }
}

class Node {
    int x;
    int y;

     Node (int x, int y) {
        this.x = x;
        this.y = y;
    }

    void set (int x, int y) {
        this.x = x;
        this.y = y;
    }
}