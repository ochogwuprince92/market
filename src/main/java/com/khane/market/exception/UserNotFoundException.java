package com.khane.market.exception;

public class UserNotFoundException extends RuntimeException {

        //    Constructor to pass a message
        public UserNotFoundException( String message) {
            super(message);
        }

////    Optional
//        public UserNotFoundException (){
//            super("User not found");
//
//    }
}
