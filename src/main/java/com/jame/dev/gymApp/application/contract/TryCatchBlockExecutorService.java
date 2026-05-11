package com.jame.dev.gymApp.application.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TryCatchBlockExecutorService {
   /**
    * Runs code-block and delegates the exception on failure to the
    * GlobalExceptionHandler.
    * Should be used on Filters, handlers, etc.
    */
   void executeVoidBlock(
           HttpServletRequest request,
           HttpServletResponse response,
           VoidBlock block
   );
}
