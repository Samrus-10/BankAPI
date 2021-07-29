package sam.rus.bankapi;

import sam.rus.bankapi.util.Base64Coder;

public class Test {
    public static void main(String[] args) {
        System.out.println(Base64Coder.toEncoder("admin:1111"));
        System.out.println(Base64Coder.toEncoder("user1:2222"));
        System.out.println(Base64Coder.toEncoder("user2:3333"));
        System.out.println(Base64Coder.toEncoder("user3:4444"));
    }
}
