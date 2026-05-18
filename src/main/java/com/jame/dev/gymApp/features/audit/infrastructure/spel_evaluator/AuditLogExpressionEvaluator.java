package com.jame.dev.gymApp.features.audit.infrastructure.spel_evaluator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditLogExpressionEvaluator {
   private final ExpressionParser parser = new SpelExpressionParser();

   public String evaluate(String expression, String[] paramNames, Object[] args) {
      if (expression == null || expression.isBlank()) return null;

      try {
         var context = new StandardEvaluationContext();
         for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
         }

         var result = parser.parseExpression(expression).getValue(context);
         return result != null ? result.toString() : null;
      } catch (ParseException e) {
         log.warn("Failed to evaluate SPEL expression: {}", expression, e);
         return null;
      }
   }

   public String evaluate(String expression, String[] paramNames, Object[] args, Object result) {
      if (expression == null || expression.isBlank()) return null;

      try {
         var context = new StandardEvaluationContext();
         for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
         }
         context.setVariable("result", result);
         var value = parser.parseExpression(expression).getValue(context);
         return value != null ? value.toString() : null;
      } catch (Exception e) {
         log.warn("Failed to evaluate SPEL expression: {}", expression, e);
         return null;
      }
   }

   public String evaluate(String expression, Object result) {
      if (expression == null || expression.isBlank()) return null;

      try {
         var context = new StandardEvaluationContext();
         context.setVariable("result", result);
         var value = parser.parseExpression(expression).getValue(context);
         return value != null ? value.toString() : null;
      } catch (ParseException e) {
         log.warn("Failed to evaluate SPEL result expression: {}", expression, e);
         return null;
      }
   }

   public Object evaluateAsObject(String expression, String[] paramNames, Object[] args) {
      if (expression == null || expression.isBlank()) return null;

      try {
         var context = new StandardEvaluationContext();
         for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
         }
         return parser.parseExpression(expression).getValue(context);
      } catch (Exception e) {
         log.warn("Failed to evaluate SPEL expression: {}", expression, e);
         return null;
      }
   }

   public Object evaluateAsObject(String expression, Object result) {
      if (expression == null || expression.isBlank()) return null;

      try {
         var context = new StandardEvaluationContext();
         context.setVariable("result", result);
         return parser.parseExpression(expression).getValue(context);
      } catch (Exception e) {
         log.warn("Failed to evaluate SPEL result expression: {}", expression, e);
         return null;
      }
   }
}
