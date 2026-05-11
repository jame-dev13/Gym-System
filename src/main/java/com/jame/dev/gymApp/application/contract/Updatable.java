package com.jame.dev.gymApp.application.contract;

public interface Updatable<Consumer, Resource> {
   void apply(Consumer c, Resource r);
}
