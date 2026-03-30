package com.jame.dev.gymApp.updaters.common;

public interface Updatable<Consumer, Resource> {
   void apply(Consumer c, Resource r);
}
