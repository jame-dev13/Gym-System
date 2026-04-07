package com.jame.dev.gymApp.service.in;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This interface is a collection of all the
 * algorithms to perform some kind of R.L technics.
 */
public interface RateLimiterService {
   void fixedWindow(final HttpServletRequest request);
}
