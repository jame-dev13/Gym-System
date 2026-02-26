package com.jame.dev.gymApp.service.out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.List;

@Service
@RequiredArgsConstructor
@Deprecated
public class RedisCacheInvalidationService {
   private final JedisPooled cacheAppPool;

   public void deleteByPrefix(final String prefix) {
      String cursor = ScanParams.SCAN_POINTER_START;
      final ScanParams scanParams = new ScanParams()
              .match(prefix + "*")
              .count(500);
      do {
         final ScanResult<String> scanResult = cacheAppPool.scan(cursor, scanParams);
         cursor = scanResult.getCursor();
         final List<String> keys = scanResult.getResult();
         if (!keys.isEmpty()) {
            cacheAppPool.del(keys.toArray(new String[0]));
         }
      } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

   }
}
