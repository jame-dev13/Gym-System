package com.jame.dev.gymApp.features.audit.application.support.helper;

import com.jame.dev.gymApp.features.audit.application.dto.AuditLogMetadata;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ExtractAuditLogMetadataHelper {
   private static final String DEFAULT_IP = "UNKNOWN_IP";
   private static final String DEFAULT_UA = "UNKNOWN_AGENT";

   public static AuditLogMetadata extractAuditLogMetadata() {
      final var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes == null) {
         return new AuditLogMetadata(DEFAULT_IP, DEFAULT_UA);
      }

      final HttpServletRequest request = attributes.getRequest();
      String ip = request.getHeader("X-Forwarded-For");

      if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
         ip = request.getRemoteAddr();
      }

      if (ip != null && ip.contains(",")) {
         ip = ip.split(",")[0].trim();
      }

      String userAgent = request.getHeader("User-Agent");
      if (userAgent == null) {
         userAgent = DEFAULT_UA;
      }
      return new AuditLogMetadata(ip, userAgent);
   }
}
