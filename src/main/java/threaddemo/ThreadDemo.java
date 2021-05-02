/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threaddemo;

/**
 *
 * @author jacob
 */
public class ThreadDemo {
    public static void main(String[] args) {
        
        Runnable r = () -> {
            System.out.println("hello from " + Thread.currentThread().getName());
        };
        
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        t1.start();
        t2.start();
    }
    
}
