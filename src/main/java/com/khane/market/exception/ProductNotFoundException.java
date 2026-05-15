package com.khane.market.exception;

public class ProductNotFoundException extends RuntimeException {

        //    Constructor to pass a message
        public ProductNotFoundException(String message) {
            super(message);
        }

////    Optional
//        public UserNotFoundException (){
//            super("User not found");
//
//    }
}
