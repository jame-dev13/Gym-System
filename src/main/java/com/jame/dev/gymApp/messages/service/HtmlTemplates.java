package com.jame.dev.gymApp.messages.service;

public final class HtmlTemplates {

   public static String verificationTemplate(final String subject, final String rawToken) {
      return """
           <!DOCTYPE html>
           <html lang="es">
           <head>
               <meta charset="UTF-8" />
               <meta name="viewport" content="width=device-width, initial-scale=1.0" />
               <title>Verification Code</title>
           </head>
           <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,sans-serif;">
           
               <table align="center" width="100%%" cellpadding="0" cellspacing="0"
                      style="max-width:600px;margin:auto;background:#ffffff;border-radius:8px;
                             padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
           
                   <tr>
                       <td style="text-align:center;padding:20px 0;">
                           <h2 style="color:#333;margin:0;">Verification Code</h2>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="color:#555;font-size:16px;line-height:1.6;padding:10px 20px;">
                           <p>Hello <strong>{{recipient}}</strong>,</p>
           
                           <p>To continue with your register process, enter the following code in the application dialog:</p>
           
                           <p style="font-size:32px;color:#007bff;font-weight:bold;text-align:center;margin:30px 0;">
                               {{token}}
                           </p>
           
                           <p>The code is valid for the next <strong>10 minutes</strong>.If you don't requested it, just ignore this message.</p>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="text-align:center;color:#999;font-size:12px;padding-top:20px;">
                           ©GymApp. All rights reserved.
                       </td>
                   </tr>
           
               </table>
           
           </body>
           </html>
           """
              .replace("{{recipient}}", subject)
              .replace("{{token}}", rawToken);
   }

   public static String adminTemplate(final String email, final String password) {
      return """
              <!DOCTYPE html>
              <html>
              
              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Your Credentials</title>
              </head>
              
              <body
                style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f9; color: #333;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td align="center" style="padding: 40px 0;">
                      <table border="0" cellpadding="0" cellspacing="0" width="600"
                        style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                        <tr>
                          <td align="center" style="background-color: #0891b2; padding: 30px 20px;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">Welcome!</h1>
                          </td>
                        </tr>
              
                        <tr>
                          <td style="padding: 40px 30px;">
                            <p style="font-size: 16px; line-height: 1.6; margin-bottom: 20px;">
                              Hi there! An administrator account has been created for your. Below are you credentials to access the platform.
                            </p>
              
                            <div
                              style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 20px; margin-bottom: 30px;">
                              <table width="100%">
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px; width: 80px;"><strong>Email:</strong>
                                  </td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{email}}</td>
                                </tr>
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px;"><strong>Password:</strong></td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{password}}
                                  </td>
                                </tr>
                              </table>
                            </div>
              
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                              <tr>
                                <td align="center">
                                  <a href="https://tu-app.com/login"
                                    style="background-color: #0891b2; color: #ffffff; padding: 14px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                    Login now.
                                  </a>
                                </td>
                              </tr>
                            </table>
              
                            <p
                              style="font-size: 14px; color: #ef4444; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;">
                              <strong>Important:</strong> For security reasons, we highly recommend to change your password as soon as possible or even after your first login.
                            </p>
                          </td>
                        </tr>
              
                        <tr>
                          <td align="center" style="padding: 20px; background-color: #f1f5f9; color: #94a3b8; font-size: 12px;">
                            <p style="margin: 0;">This is an automated message, please don't reply to this email.</p>
                            <p style="margin: 5px 0 0;">&copy; 2024 GymApp - All rights reserved.</p>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </body>
              
              </html>
              """
              .replace("{{email}}", email).replace("{{password}}", password);
   }

   public static String userTemplate(final String email, final String password) {
      return """
              <!DOCTYPE html>
              <html>
              
              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Your Credentials</title>
              </head>
              
              <body
                style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f9; color: #333;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td align="center" style="padding: 40px 0;">
                      <table border="0" cellpadding="0" cellspacing="0" width="600"
                        style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                        <tr>
                          <td align="center" style="background-color: #0891b2; padding: 30px 20px;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">Welcome!</h1>
                          </td>
                        </tr>
              
                        <tr>
                          <td style="padding: 40px 30px;">
                            <p style="font-size: 16px; line-height: 1.6; margin-bottom: 20px;">
                              Hi there! An user account has been created for your. Below are you credentials to access the platform.
                            </p>
              
                            <div
                              style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 20px; margin-bottom: 30px;">
                              <table width="100%">
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px; width: 80px;"><strong>Email:</strong>
                                  </td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{email}}</td>
                                </tr>
                                <tr>
                                  <td style="padding: 5px 0; color: #64748b; font-size: 14px;"><strong>Password:</strong></td>
                                  <td style="padding: 5px 0; font-family: monospace; font-size: 16px; color: #0891b2;">{{password}}
                                  </td>
                                </tr>
                              </table>
                            </div>
              
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                              <tr>
                                <td align="center">
                                  <a href="https://tu-app.com/login"
                                    style="background-color: #0891b2; color: #ffffff; padding: 14px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                    Login now.
                                  </a>
                                </td>
                              </tr>
                            </table>
              
                            <p
                              style="font-size: 14px; color: #ef4444; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;">
                              <strong>Important:</strong> For security reasons, we highly recommend to change your password as soon as possible or even after your first login.
                            </p>
                          </td>
                        </tr>
              
                        <tr>
                          <td align="center" style="padding: 20px; background-color: #f1f5f9; color: #94a3b8; font-size: 12px;">
                            <p style="margin: 0;">This is an automated message, please don't reply to this email.</p>
                            <p style="margin: 5px 0 0;">&copy; 2024 GymApp - All rights reserved.</p>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </body>
              
              </html>
              """
              .replace("{{email}}", email).replace("{{password}}", password);
   }

   public static String recoveryTemplate(final String subject, final String rawToken) {
      return """
           <!DOCTYPE html>
           <html lang="es">
           <head>
               <meta charset="UTF-8" />
               <meta name="viewport" content="width=device-width, initial-scale=1.0" />
               <title>Recovery Code</title>
           </head>
           <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,sans-serif;">
           
               <table align="center" width="100%%" cellpadding="0" cellspacing="0"
                      style="max-width:600px;margin:auto;background:#ffffff;border-radius:8px;
                             padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
           
                   <tr>
                       <td style="text-align:center;padding:20px 0;">
                           <h2 style="color:#333;margin:0;">Recovery Code</h2>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="color:#555;font-size:16px;line-height:1.6;padding:10px 20px;">
                           <p>Hello <strong>{{recipient}}</strong>,</p>
           
                           <p>To continue with your re-activation process, enter the following code in the application dialog:</p>
           
                           <p style="font-size:32px;color:#007bff;font-weight:bold;text-align:center;margin:30px 0;">
                               {{token}}
                           </p>
           
                           <p>The code is valid for the next <strong>10 minutes</strong>.If you don't requested it, just ignore this message.</p>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="text-align:center;color:#999;font-size:12px;padding-top:20px;">
                           ©GymApp. All rights reserved.
                       </td>
                   </tr>
           
               </table>
           
           </body>
           </html>
           """
              .replace("{{recipient}}", subject)
              .replace("{{token}}", rawToken);
   }

}
