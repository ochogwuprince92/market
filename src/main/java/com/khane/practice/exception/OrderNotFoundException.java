package com.khane.practice.exception;

public class OrderNotFoundException extends RuntimeException {

        //    Constructor to pass a message
        public OrderNotFoundException(String message) {
            super(message);
        }

}
