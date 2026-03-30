package com.jame.dev.gymApp.updaters.common;

public interface Renewable<Consumer, Resource> {
   void applyRenew(Consumer c, Resource r);
}
