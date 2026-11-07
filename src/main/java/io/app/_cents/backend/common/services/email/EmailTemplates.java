package io.app._cents.backend.common.services.email;

import org.springframework.web.util.HtmlUtils;

public class EmailTemplates {

  private static final String APP_NAME = "50cents";

  private static final String LAYOUT =
      """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="margin:0;padding:0;background-color:#f3f4f6;-webkit-font-smoothing:antialiased;">
          <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#f3f4f6;padding:40px 16px;">
            <tr>
              <td align="center">
                <table role="presentation" width="520" cellpadding="0" cellspacing="0" style="max-width:520px;width:100%;">
                  <tr>
                    <td style="padding:0 4px 20px 4px;font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:22px;font-weight:700;color:#111827;letter-spacing:-0.3px;">
                      {{appName}}
                    </td>
                  </tr>
                  <tr>
                    <td style="background-color:#ffffff;border:1px solid #e5e7eb;border-top:4px solid #4f46e5;border-radius:12px;padding:36px 32px;font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
                      <h1 style="margin:0 0 16px 0;font-size:22px;line-height:1.3;font-weight:700;color:#111827;">{{title}}</h1>
                      <p style="margin:0;font-size:15px;line-height:1.7;color:#4b5563;">{{message}}</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:24px 8px 0 8px;font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;font-size:12px;line-height:1.6;color:#9ca3af;text-align:center;">
                      This is an automated message from {{appName}}. Please do not reply to this email.
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """;

  public static String getEmailHtml(String title, String message) {
    return LAYOUT
        .replace("{{appName}}", HtmlUtils.htmlEscape(APP_NAME))
        .replace("{{title}}", HtmlUtils.htmlEscape(title))
        .replace("{{message}}", HtmlUtils.htmlEscape(message).replace("\n", "<br>"));
  }
}
