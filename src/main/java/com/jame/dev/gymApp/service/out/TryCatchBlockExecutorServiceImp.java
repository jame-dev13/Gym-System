package com.jame.dev.gymApp.service.out;

import com.jame.dev.gymApp.service.in.TryCatchBlockExecutorService;
import com.jame.dev.gymApp.shared.interfaces.VoidBlock;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
@RequiredArgsConstructor
public class TryCatchBlockExecutorServiceImp implements TryCatchBlockExecutorService {
   private final HandlerExceptionResolver handlerExceptionResolver;

   @Override
   public void executeVoidBlock(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                VoidBlock block) {
      try {
         block.execute();
      } catch (Exception e) {
         handlerExceptionResolver.resolveException(request, response, null, e);
      }
   }
}
