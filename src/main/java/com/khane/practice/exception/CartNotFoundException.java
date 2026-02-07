package com.khane.practice.exception;

public class CartNotFoundException extends RuntimeException {

        //    Constructor to pass a message
        public CartNotFoundException(String message) {
            super(message);
        }

////    Optional
//        public UserNotFoundException (){
//            super("User not found");
//
//    }
}
