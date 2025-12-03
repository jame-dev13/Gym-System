package com.jame.dev.gymApp.messages.service;

import com.jame.dev.gymApp.model.messages.EmailDetails;
import com.jame.dev.gymApp.model.messages.EmailDetailsWAttachment;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
   CompletableFuture<Boolean> sendSimpleEmail(@NonNull final EmailDetails emailDetails);

   CompletableFuture<Boolean> sendMailWithAttachment(@NonNull final EmailDetailsWAttachment emailDetails);
   String html(String to, String code);

   String HTML = """
           <!DOCTYPE html>
           <html lang="es">
           <head>
               <meta charset="UTF-8" />
               <meta name="viewport" content="width=device-width, initial-scale=1.0" />
               <title>Código de verificación</title>
           </head>
           <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,sans-serif;">
           
               <table align="center" width="100%%" cellpadding="0" cellspacing="0"
                      style="max-width:600px;margin:auto;background:#ffffff;border-radius:8px;
                             padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
           
                   <tr>
                       <td style="text-align:center;padding:20px 0;">
                           <h2 style="color:#333;margin:0;">Código de verificación</h2>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="color:#555;font-size:16px;line-height:1.6;padding:10px 20px;">
                           <p>Hola <strong>%s</strong>,</p>
           
                           <p>Para continuar con tu proceso, utiliza el siguiente código de verificación:</p>
           
                           <p style="font-size:32px;color:#007bff;font-weight:bold;text-align:center;margin:30px 0;">
                               %s
                           </p>
           
                           <p>Este código es válido por <strong>10 minutos</strong>. Si no has solicitado esta verificación, puedes ignorar este mensaje.</p>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="text-align:center;color:#999;font-size:12px;padding-top:20px;">
                           ©GymApp. Todos los derechos reservados.
                       </td>
                   </tr>
           
               </table>
           
           </body>
           </html>
           """;
   String HTML_ATTACHMENT = """
           <!DOCTYPE html>
           <html lang="es">
           <head>
               <meta charset="UTF-8" />
               <meta name="viewport" content="width=device-width, initial-scale=1.0" />
               <title>Código de verificación</title>
           </head>
           <body style="margin:0;padding:0;background-color:#f5f5f5;font-family:Arial,sans-serif;">
           
               <table align="center" width="100%" cellpadding="0" cellspacing="0"
                      style="max-width:600px;margin:auto;background:#ffffff;border-radius:8px;
                             padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
           
                   <tr>
                       <td style="text-align:center;padding:20px 0;">
                           <h2 style="color:#333;margin:0;">Código de verificación</h2>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="color:#555;font-size:16px;line-height:1.6;padding:10px 20px;">
                           <p>Hola <strong>%s</strong>,</p>
           
                           <p>Tu código de verificación es:</p>
           
                           <p style="font-size:32px;color:#007bff;font-weight:bold;text-align:center;margin:30px 0;">
                               %s
                           </p>
           
                           <p>Además, hemos adjuntado un archivo importante que forma parte de este proceso.  
                           Revísalo cuidadosamente.</p>
           
                           <p>El código es válido por <strong>10 minutos</strong>. Si no solicitaste este proceso, puedes ignorar este mensaje.</p>
                       </td>
                   </tr>
           
                   <tr>
                       <td style="text-align:center;color:#999;font-size:12px;padding-top:20px;">
                           © %d TuEmpresa. Todos los derechos reservados.
                       </td>
                   </tr>
           
               </table>
           
           </body>
           </html>
           """;

}
