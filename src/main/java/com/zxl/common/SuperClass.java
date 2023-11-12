package com.zxl.common;


public class SuperClass{ 
        public static void staticMethod()
        {  System.out.println("SuperClass: inside staticMethod"); } 
        public static void main(String []args){
                SuperClass superClassWithSuperCons = new SuperClass();
                SuperClass childClassWithSubCons = new ChildClass();
                ChildClass childClassWithChildCons = new ChildClass();
                superClassWithSuperCons.staticMethod();
                childClassWithSubCons.staticMethod();
                childClassWithChildCons.staticMethod();  }
}
class ChildClass extends SuperClass{
        public static void staticMethod(){
                System.out.println("ChildClass: inside staticMethod");
        }}