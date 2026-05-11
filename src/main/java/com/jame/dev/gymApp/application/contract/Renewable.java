package com.jame.dev.gymApp.application.contract;

public interface Renewable<Consumer, Resource> {
   void applyRenew(Consumer c, Resource r);
}
