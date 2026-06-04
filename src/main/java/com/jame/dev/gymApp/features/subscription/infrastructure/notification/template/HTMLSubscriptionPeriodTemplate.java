package com.jame.dev.gymApp.features.subscription.infrastructure.notification.template;

import com.jame.dev.gymApp.features.subscription.infrastructure.notification.model.NotifiableSubscription;

public class HTMLSubscriptionPeriodTemplate {

   public static String buildTemplateFrom(final NotifiableSubscription ns, long daysUntilEnd) {
      return """
         <!DOCTYPE html>
                  <html lang="en">
                  <head>
                     <meta charset="UTF-8">
                     <meta name="viewport" content="width=device-width, initial-scale=1.0">
                     <style>
               body { margin: 0; padding: 0; background-color: #f4f4f4; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; -webkit-text-size-adjust: 100%; }
               .container { max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
               .header { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 40px 30px; text-align: center; }
               .header h1 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 600; letter-spacing: 0.5px; }
               .header p { color: #c8d6e5; margin: 10px 0 0; font-size: 14px; }
               .body { padding: 30px; }
               .greeting { font-size: 18px; color: #333333; margin: 0 0 6px; }
               .description { font-size: 14px; color: #666666; margin: 0 0 24px; line-height: 1.6; }
               .details { background: #f8f9fa; border-radius: 8px; padding: 20px; }
               .detail-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #e9ecef; }
               .detail-row:last-child { border-bottom: none; }
               .detail-label { font-size: 13px; color: #888888; text-transform: uppercase; letter-spacing: 0.5px; flex-shrink: 0; }
               .detail-value { font-size: 15px; color: #222222; font-weight: 600; text-align: right; word-break: break-word; }
               .badge { display: inline-block; background: #2a5298; color: #ffffff; padding: 4px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; white-space: nowrap; }
               .footer { background: #f1f3f5; padding: 20px 30px; text-align: center; }
               .footer p { margin: 0; font-size: 12px; color: #999999; }
               @media only screen and (max-width: 480px) {
                  .container { margin: 12px auto; border-radius: 8px; }
                  .header { padding: 28px 20px; }
                  .header h1 { font-size: 20px; }
                  .body { padding: 20px; }
                  .greeting { font-size: 16px; }
                  .details { padding: 14px; }
                  .detail-row { flex-direction: column; align-items: center; gap: 6px; }
                  .detail-value { text-align: left; }
                  .footer { padding: 16px 20px; }
               }
                     </style>
                  </head>
                  <body>
                     <div class="container">
                        <div class="header">
                           <h1>Subscription Details</h1>
                           <p>Your subscription period details</p>
                        </div>
                        <div class="body">
                           <p class="greeting">Hello Again, <strong>{{email}}</strong>!</p>
                           <p class="description">
                              Just a frendly remainder about your subscription plan details, we want to make sure
                              that you keep it in mind and we care about of your subscription status.
                           </p>
                           <div class="details">
                              <div class="detail-row">
                                 <span class="detail-label">Plan</span>
                                 <span class="badge">{{period}}</span>
                              </div>
                              <div class="detail-row">
                                 <span class="detail-label">Start Date</span>
                                 <span class="detail-value">{{startDate}}</span>
                              </div>
                              <div class="detail-row">
                                 <span class="detail-label">End Date</span>
                                 <span class="detail-value">{{endDate}}</span>
                              </div>
                              <div class="detail-row">
                                <span class="detail-label">Expires in</span>
                                <span class="detail-value">{{daysUntil}}</span>
                              </div>
                           </div>
                        </div>
                        <div class="footer">
                           <p>&copy; 2026 GymApp &mdash; Stay strong, stay healthy.</p>
                        </div>
                     </div>
                  </body>
                  </html>
         """
         .replace("{{email}}", ns.customerEmail())
         .replace("{{period}}", ns.period().getPeriod().name())
         .replace("{{startDate}}", ns.period().getStartPeriod().toString())
         .replace("{{endDate}}", ns.period().getEndPeriod().toString())
         .replace("{{daysUntil}}", String.valueOf(daysUntilEnd));
   }
}
